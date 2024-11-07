package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.*;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import com.mthree.company_budget_mng_system.repository.ExpenseRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private BudgetRepository budgetRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.budgetRepository = budgetRepository;
    }

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        log.info("Creating an expense.");
        //1. Find the matching budget by Year
        int year = expenseDTO.getDate().getYear();
        Budget budget = budgetRepository.findByYear(year)
                .orElseThrow(handleResourceNotFound(year));
        Expense expense = expenseMapper.map(expenseDTO);
        expense.setBudget(budget);
        Expense savedExpense = validateExpenseAgainstBudgetPlanned(expenseDTO, budget, expense, year);
        // Return response with both the expense and warning message (if any)
        ExpenseDTO dto = expenseMapper.map(savedExpense);
        log.info("Expense created.");
        return dto;
    }

    private Expense validateExpenseAgainstBudgetPlanned(ExpenseDTO expenseDTO, Budget budget, Expense expense, int year) {
        var plannedAmountPerCategory = budget.getBudgetPlanned().get(expenseDTO.getCategoryType());

        var totalExpensesForCategory = getTotalExpensesForCategory(expenseDTO, budget);

        var newTotalForCategory = getNewTotalForCategoryOrThrowException(expenseDTO, totalExpensesForCategory, plannedAmountPerCategory);

        Expense savedExpense = saveExpenseAndUpdateBudget(budget, expense);

        // Check if the total expenses for the category exceeded 90% of the planned budget
        var thresholdForCategory = calculateThreshold(plannedAmountPerCategory);
        isThresholdForCategoryExceeded(expenseDTO, newTotalForCategory, thresholdForCategory);

        //Check if the total expenses for the entire budget exceeded 90% of the total planned budget
        var totalActualExpenses = getTotalActualExpenses(budget);
        var thresholdForBudget = calculateThreshold(budget.getTotalAmount());
        isThresholdForBudgetExceeded(totalActualExpenses, thresholdForBudget, year);
        return savedExpense;
    }

    private static void isThresholdForBudgetExceeded(BigDecimal totalActualExpenses, BigDecimal thresholdForBudget, int year) {
        if (totalActualExpenses.compareTo(thresholdForBudget) > 0) {
            String message = "You exceeded 90% of the total budget for the year " + year;
            log.error(message);
            throw new BudgetThresholdExceededException(message);
        }
    }

    private static Supplier<ResourceNotFoundException> handleResourceNotFound(int year) {
        return () -> {
            String message = "No budget found for the year: " + year;
            log.error(message);
            throw new ResourceNotFoundException(message);
        };
    }

    @Transactional
    private Expense saveExpenseAndUpdateBudget(Budget budget, Expense expense) {
        budget.getActualExpenses().add(expense);
        Expense savedExpense = expenseRepository.save(expense);
        return savedExpense;
    }

    private static BigDecimal getNewTotalForCategoryOrThrowException(ExpenseDTO expenseDTO, BigDecimal totalExpensesForCategory, BigDecimal plannedAmountPerCategory) {
        var newTotalForCategory = totalExpensesForCategory.add(expenseDTO.getAmount());
        //check whether the category exists in the planned budget -> IllegalArgumentException
        if (plannedAmountPerCategory == null) {
            String message = "No planned budget found for the category: " + expenseDTO.getCategoryType();
            handleIllegalArgument(message);
        } else if (newTotalForCategory.compareTo(plannedAmountPerCategory) > 0) {
            String message = "Adding this expense will exceed the budget for the category " + expenseDTO.getCategoryType();
            handleIllegalArgument(message);
        }
        return newTotalForCategory;
    }

    private static void handleIllegalArgument(String message) {
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    private static BigDecimal calculateThreshold(BigDecimal toCalculateThreshold) {
        return toCalculateThreshold.multiply(BigDecimal.valueOf(0.9));
    }


    private static BigDecimal getTotalActualExpenses(Budget budget) {
        return budget.getActualExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void isThresholdForCategoryExceeded(ExpenseDTO expenseDTO, BigDecimal newTotalForCategory, BigDecimal thresholdForCategory) {
        if (newTotalForCategory.compareTo(thresholdForCategory) > 0) {
            // Throw exception with warning message
            String message = "You exceeded 90% of the budget for category " + expenseDTO.getCategoryType() + ".";
            log.warn(message);
            throw new CategoryThresholdExceededException(message);
        }
    }

    private static BigDecimal getTotalExpensesForCategory(ExpenseDTO expenseDTO, Budget budget) {
        return budget.getActualExpenses().stream()
                .filter(exp -> exp.getCategoryType().equals(expenseDTO.getCategoryType())) // Filter by category type
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ExpenseDTO> getAllExpenses() {
        log.info("Fetching all expenses...");
        List<Expense> allExpenses = expenseRepository.findAll();
        List<ExpenseDTO> expenseDTOS = expenseMapper.mapToDtoList(allExpenses);
        log.info("Fetch completed.");
        return expenseDTOS;
    }

    public ExpenseDTO getExpenseById(Long id) {
        log.info("Getting expenses for id '{}'.", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(handleExpenseNotFound(id));
        ExpenseDTO expenseDTO = expenseMapper.map(expense);
        log.info("Fetch completed");
        return expenseDTO;
    }

    public List<ExpenseDTO> getExpenseByCategoryType(CategoryType categoryType){
        log.info("Getting expenses for category '{}'.", categoryType);
        List<Expense> byCategoryType = expenseRepository.findByCategoryType(categoryType);
        List<ExpenseDTO> expenseDTOS = expenseMapper.mapToDtoList(byCategoryType);
        log.info("Fetch completed");
        return expenseDTOS;
    }


    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        log.info("Updating expense with id '{}'.", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(handleExpenseNotFound(id));

        // Store the old categoryType and amount to validate reductions
        var oldAmount = expense.getAmount();
        var newAmount = expenseDTO.getAmount();

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(newAmount);
        expense.setDate(expenseDTO.getDate());
        expense.setCategoryType(expenseDTO.getCategoryType());

        Budget budget = budgetRepository.findByYear(expense.getDate().getYear())
                .orElseThrow(handleResourceNotFound(expense.getDate().getYear()));

        Expense updatedExpense = validateUpdatedExpenseAgainstBudgetPlanned(expenseDTO, budget, newAmount, oldAmount, expense);
        ExpenseDTO updatedExpenseDTO = expenseMapper.map(updatedExpense);
        log.info("Update completed.");
        return updatedExpenseDTO;
    }

    @Transactional
    private Expense validateUpdatedExpenseAgainstBudgetPlanned(ExpenseDTO expenseDTO, Budget budget, BigDecimal newAmount, BigDecimal oldAmount, Expense expense) {
        var totalExpensesForCategory = getTotalExpensesForCategory(expenseDTO, budget);

        var plannedAmountPerCategory = budget.getBudgetPlanned().get(expenseDTO.getCategoryType());
        var newTotalForCategory = totalExpensesForCategory.add(newAmount.subtract(oldAmount));
        // Validate that reducing the amount does not breach the category budget
        if (newAmount.compareTo(oldAmount) > 0) {
            // Check if the new amount violates the category's budget
            if (newTotalForCategory.compareTo(plannedAmountPerCategory) > 0) {
                String message = "Updating this expense will exceed the budget for the category " + expenseDTO.getCategoryType();
                handleIllegalArgument(message);
            }
        }
        Expense updatedExpense = expenseRepository.save(expense);
        // Check if the total expenses for the category exceeded 90% of the planned budget
        var thresholdForCategory = calculateThreshold(plannedAmountPerCategory);
        isThresholdForCategoryExceeded(expenseDTO, newTotalForCategory, thresholdForCategory);
        // Check if the total expenses exceed 90% of the total budget after updating the expense
        var totalActualExpenses = getTotalActualExpenses(budget);
        var thresholdForBudget = calculateThreshold(budget.getTotalAmount());
        isThresholdForBudgetExceeded(totalActualExpenses, thresholdForBudget, budget.getYear());

        return updatedExpense;
    }

    private static Supplier<ExpenseNotFoundException> handleExpenseNotFound(Long id) {
        return () -> {
            String message = "Expense not found with id " + id;
            log.error(message);
            throw new ExpenseNotFoundException(message);
        };
    }

    @Transactional
    public void deleteExpense(Long id) {
        log.info("Removing expense with id '{}'.", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(handleExpenseNotFound(id));
        expenseRepository.delete(expense);
        log.info("Removing expense completed.");
    }
}
