package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.CategoryThresholdExceededException;
import com.mthree.company_budget_mng_system.exception.ExpenseNotFoundException;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import com.mthree.company_budget_mng_system.repository.ExpenseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExpenseServiceTest {

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private ExpenseMapper expenseMapper;

    @MockBean
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseService expenseService;

    private ExpenseDTO expenseDTO;
    private Budget budget;
    private Expense expense;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        expenseDTO = ExpenseDTO.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryType(CategoryType.HR)
                .description("Travel expense")
                .date(LocalDate.now())
                .build();

        budget = new Budget();
        budget.setId(1L);
        budget.setTotalAmount(BigDecimal.valueOf(10000));
        budget.setYear(LocalDate.now().getYear());
        budget.setBudgetPlanned(Map.of(CategoryType.HR, BigDecimal.valueOf(500)));

        expense = Expense.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .description("Travel expense")
                .date(LocalDate.of(2024, 10, 10))
                .budget(budget)
                .categoryType(CategoryType.HR)
                .build();


        when(budgetRepository.findByYear(anyInt())).thenReturn(Optional.of(budget));
        when(expenseMapper.map(any(ExpenseDTO.class))).thenReturn(expense);
        when(expenseMapper.map(any(Expense.class))).thenReturn(expenseDTO);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(expense));
    }

    @Test
    void createExpense_ShouldCreateExpense_WhenValid() {
        // Given a valid expense DTO
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(BigDecimal.valueOf(100));
        expenseDTO.setCategoryType(CategoryType.HR);
        expenseDTO.setDescription("Travel expense");
        expenseDTO.setDate(LocalDate.of(2024, 10, 10));

        // When
        ExpenseDTO result = expenseService.createExpense(expenseDTO);

        // Then
        verify(expenseRepository, times(1)).save(any(Expense.class));
        assertEquals(expenseDTO.getAmount(), result.getAmount());
    }

    @Test
    void createExpense_ShouldThrowException_WhenCategoryBudgetExceeded() {
        // Given
        expenseDTO.setAmount(BigDecimal.valueOf(600));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> expenseService.createExpense(expenseDTO));
    }

    @Test
    void updateExpense_ShouldUpdateExpense_WhenValid() {
        // Given
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setCategoryType(CategoryType.HR);
        expense.setDescription("Old description");
        expense.setDate(LocalDate.of(2024, 10, 10));

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // When
        ExpenseDTO updatedExpenseDTO = expenseService.updateExpense(1L, expenseDTO);

        // Then
        verify(expenseRepository, times(1)).save(expense);
        assertEquals(expenseDTO.getDescription(), updatedExpenseDTO.getDescription());
        assertEquals(expenseDTO.getAmount(), updatedExpenseDTO.getAmount());
    }

    @Test
    void updateExpense_ShouldThrowException_WhenCategoryBudgetExceeded() {
        // Given
        expenseDTO.setAmount(BigDecimal.valueOf(600));
        budget.setActualExpenses(List.of(expense));// Exceeds the budget for the category

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> expenseService.updateExpense(1L, expenseDTO));
    }

    @Test
    void getExpenseById_ShouldReturnExpense_WhenFound() {
        // Given
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setAmount(BigDecimal.valueOf(100));

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // When
        ExpenseDTO result = expenseService.getExpenseById(1L);

        // Then
        assertNotNull(result);
        assertEquals(expense.getId(), result.getId());
        assertEquals(expense.getAmount(), result.getAmount());
    }

    @Test
    void getExpenseById_ShouldThrowException_WhenNotFound() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpenseById(1L));
    }

    @Test
    void deleteExpense_ShouldDeleteExpense_WhenFound() {
        // Given
        Expense expense = new Expense();
        expense.setId(1L);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // When
        expenseService.deleteExpense(1L);

        // Then
        verify(expenseRepository, times(1)).delete(expense);
    }

    @Test
    void deleteExpense_ShouldThrowException_WhenNotFound() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense(1L));
    }
}