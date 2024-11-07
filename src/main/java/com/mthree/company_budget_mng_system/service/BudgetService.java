package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.BudgetAlreadyExistsException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.mapper.BudgetMapper;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, BudgetMapper budgetMapper, ExpenseMapper expenseMapper) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        // 1.Check if a budget for the specified year already exists
        if (budgetRepository.existsByYear(budgetDTO.getYear())) {
            throw new BudgetAlreadyExistsException("A budget for the year " + budgetDTO.getYear() + " already exists.");
        }
        // 2. Check if amounts sum up to total amount -> IllegalArgumentException
        budgetDTO.validateBudget(budgetDTO);
        // 3. Map BudgetDTO to Budget
        Budget budget = budgetMapper.toEntity(budgetDTO);
        return budgetMapper.toDto(budgetRepository.save(budget));
    }

    public List<BudgetDTO> getAllBudgets() {
        return budgetMapper.mapToDtoList(budgetRepository.findAll());
    }

    public BudgetDTO getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(handleBudgetNotFound());
        return budgetMapper.toDto(budget);
    }

    public List<ExpenseDTO> getActualExpenses(Long budgetId) {
        // Retrieve the budget by ID
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget with ID " + budgetId + " not found"));

        // Map the actual expenses from the Budget entity to ExpenseDTOs
        List<ExpenseDTO> expenseDTOs = budget.getActualExpenses().stream()
                .map(expense -> expenseMapper.map(expense))
                .collect(Collectors.toList());

        return expenseDTOs;
    }

    @Transactional
    public BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(handleBudgetNotFound());

        budget.setTotalAmount(budgetDTO.getTotalAmount());
        budget.setYear(budgetDTO.getYear());

        if (budgetDTO.getCategoryTypeAmountDTOS() != null) {
            for (var categoryDTO : budgetDTO.getCategoryTypeAmountDTOS()) {
                var categoryType = categoryDTO.getCategoryType();
                var newCategoryAmount = categoryDTO.getAmount();

                if (budget.getBudgetPlanned().containsKey(categoryType)) {
                    var oldCategoryAmount = budget.getBudgetPlanned().get(categoryType);
                    var totalActualExpensesForCategory = getTotalActualExpensesForCategory(budget, categoryType);
                    if (isNewAmountOutOfLimits(newCategoryAmount, totalActualExpensesForCategory, oldCategoryAmount)) {
                        sendWarning();
                    }
                    budget.getBudgetPlanned().put(categoryType, newCategoryAmount);
                }
            }
        }
        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(savedBudget);
    }

    private static Supplier<RuntimeException> handleBudgetNotFound() {
        return () -> {
            String message = "Budget with given id doesn't exist";
            log.error(message);
            throw new ResourceNotFoundException(message);
        };
    }

    private static BigDecimal getTotalActualExpensesForCategory(Budget budget, CategoryType categoryType) {
        return budget.getActualExpenses().stream()
                .filter(exp -> exp.getCategoryType().equals(categoryType))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void sendWarning() {
        String warningMessage = "You can't reduce the amount for this category because it will be less than the current total of expenses.";
        log.warn(warningMessage);
        throw new IllegalArgumentException(warningMessage);
    }

    private static boolean isNewAmountOutOfLimits(BigDecimal newCategoryAmount, BigDecimal totalActualExpensesForCategory, BigDecimal oldCategoryAmount) {
        return newCategoryAmount.compareTo(totalActualExpensesForCategory) > 0 && newCategoryAmount.compareTo(oldCategoryAmount) < 0;
    }

    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(handleBudgetNotFound());
        budgetRepository.delete(budget);
    }

    public BigDecimal calculatePercentageOfBudgetUsed(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(handleBudgetNotFound());
        BigDecimal totalAmount = budget.getTotalAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Total budget amount must be greater than zero.");
        }
        BigDecimal totalExpenses = budget.getActualExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalExpenses
                .multiply(BigDecimal.valueOf(100))
                .divide(totalAmount, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateRestOfBudget(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(handleBudgetNotFound());
        BigDecimal totalAmount = budget.getTotalAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Total budget amount must be greater than zero.");
        }
        BigDecimal totalExpenses = budget.getActualExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.subtract(totalExpenses);
    }


}
