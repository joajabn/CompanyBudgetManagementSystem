package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.*;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import com.mthree.company_budget_mng_system.repository.ExpenseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private BudgetRepository budgetRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        //1. Find the matching budget by Year
        int year = expenseDTO.getDate().getYear();
        Budget budget = budgetRepository.findByYear(year)
                .orElseThrow(() -> new ResourceNotFoundException("No budget found for the year: " + year));
        Expense expense = expenseMapper.map(expenseDTO);
        expense.setBudget(budget);

        if (expense.getId() != null) {
            expense = entityManager.merge(expense);
        }

        //2. Validate the expense against the budgetPlanned
        BigDecimal plannedAmountPerCategory = budget.getBudgetPlanned().get(expenseDTO.getCategoryType());

        BigDecimal totalExpensesForCategory = budget.getActualExpenses().stream()
                .filter(exp -> exp.getCategoryType().equals(expenseDTO.getCategoryType())) // Filter by category type
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalForCategory = totalExpensesForCategory.add(expenseDTO.getAmount());

        //check whether the category exists in the planned budget -> IllegalArgumentException
        if (plannedAmountPerCategory == null) {
            throw new IllegalArgumentException("No planned budget found for the category: " + expenseDTO.getCategoryType());
        } else if (newTotalForCategory.compareTo(plannedAmountPerCategory) > 0) {
            throw new IllegalArgumentException("Adding this expense will exceed the budget for the category " + expenseDTO.getCategoryType());
        }

        budget.getActualExpenses().add(expense);
        Expense savedExpense = expenseRepository.save(expense);

        // Check if the total expenses for the category exceeded 90% of the planned budget
        BigDecimal thresholdForCategory = plannedAmountPerCategory.multiply(BigDecimal.valueOf(0.9));
        if (newTotalForCategory.compareTo(thresholdForCategory) > 0) {
            // Throw exception with warning message
            throw new CategoryThresholdExceededException("You exceeded 90% of the budget for category " + expenseDTO.getCategoryType() + ".");
        }

        //Check if the total expenses for the entire budget exceeded 90% of the total planned budget
        BigDecimal totalActualExpenses = budget.getActualExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal thresholdForBudget = budget.getTotalAmount().multiply(BigDecimal.valueOf(0.9));
        if (totalActualExpenses.compareTo(thresholdForBudget) > 0) {
            throw new BudgetThresholdExceededException("You exceeded 90% of the total budget for the year " + year);
        }
        // Return response with both the expense and warning message (if any)
        return expenseMapper.map(savedExpense);
    }

    public List<ExpenseDTO> getAllExpenses() {
        return expenseMapper.mapToDtoList(expenseRepository.findAll());
    }

    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id));
        return expenseMapper.map(expense);
    }

    @Transactional
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id));

        // Store the old categoryType and amount to validate reductions
        BigDecimal oldAmount = expense.getAmount();

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setCategoryType(expenseDTO.getCategoryType());

        Budget budget = budgetRepository.findByYear(expense.getDate().getYear())
                .orElseThrow(() -> new ResourceNotFoundException("No budget found for the year: " + expense.getDate().getYear()));

        BigDecimal totalExpensesForCategory = budget.getActualExpenses().stream()
                .filter(exp -> exp.getCategoryType().equals(expenseDTO.getCategoryType()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal plannedAmountPerCategory = budget.getBudgetPlanned().get(expenseDTO.getCategoryType());

        BigDecimal newTotalForCategory = totalExpensesForCategory.add(expenseDTO.getAmount());

        // Validate that reducing the amount does not breach the category budget
        if (expenseDTO.getAmount().compareTo(oldAmount) > 0) {
            // Check if the new amount violates the category's budget
            // If the new amount will make total expenses greater than actual expenses, warn
            if (newTotalForCategory.compareTo(plannedAmountPerCategory) > 0) {
                throw new IllegalArgumentException("Updating this expense will exceed the budget for the category " + expenseDTO.getCategoryType());
            }
        }

        Expense updatedExpense = expenseRepository.save(expense);

        // Check if the total expenses exceed 90% of the total budget after updating the expense
        BigDecimal totalExpenses = budget.getActualExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpenses.compareTo(budget.getTotalAmount().multiply(BigDecimal.valueOf(0.9))) > 0) {
            String warningMessage = "Warning: Expenses exceed 90% of the total budget.";
            log.warn(warningMessage);
        }

        return expenseMapper.map(updatedExpense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id));
        expenseRepository.delete(expense);
    }
}
