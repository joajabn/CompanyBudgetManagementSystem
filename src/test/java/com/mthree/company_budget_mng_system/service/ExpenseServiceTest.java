package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.CategoryNotFoundException;
import com.mthree.company_budget_mng_system.exception.ExpenseNotFoundException;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Category;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.CategoryRepository;
import com.mthree.company_budget_mng_system.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createExpense_whenExpenseValid_shouldReturnCreatedExpense() {
        // Given
        Category category = new Category(1L, "Travel", BigDecimal.valueOf(1000), null, null);
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, "Flight", BigDecimal.valueOf(500), LocalDate.now(), category);
        Expense expense = new Expense(1L, BigDecimal.valueOf(500), "Flight", LocalDate.now(), category);

        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        lenient().when(expenseMapper.map(any(ExpenseDTO.class))).thenReturn(expense);
        lenient().when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        lenient().when(expenseMapper.map(any(Expense.class))).thenReturn(expenseDTO);

        // When
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);

        // Then
        assertNotNull(createdExpense);
        assertEquals("Flight", createdExpense.getDescription());
        assertEquals(BigDecimal.valueOf(500), createdExpense.getAmount());
    }

    @Test
    void createExpense_whenCategoryNotFound_shouldThrowException() {
        // Given
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, "Flight", BigDecimal.valueOf(500), LocalDate.now(), null);
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> expenseService.createExpense(expenseDTO));
    }

    @Test
    void getExpenseById_whenExpenseExists_shouldReturnExpense() {
        // Given
        Expense expense = new Expense(1L, BigDecimal.valueOf(500), "Flight", LocalDate.now(), null);
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, "Flight", BigDecimal.valueOf(500), LocalDate.now(), null);

        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(expense));
        when(expenseMapper.map(any(Expense.class))).thenReturn(expenseDTO);

        // When
        ExpenseDTO result = expenseService.getExpenseById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Flight", result.getDescription());
    }

    @Test
    void getExpenseById_whenExpenseNotFound_shouldThrowException() {
        // Given
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpenseById(1L));
    }

    @Test
    void updateExpense_whenExpenseExists_shouldReturnUpdatedExpense() {
        // Given
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, "Flight", BigDecimal.valueOf(550), LocalDate.now(), null);
        Expense expense = new Expense(1L, BigDecimal.valueOf(500), "Flight", LocalDate.now(), null);
        Expense updatedExpense = new Expense(1L, BigDecimal.valueOf(550), "Flight", LocalDate.now(), null);

        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(expense));
        lenient().when(expenseMapper.map(any(ExpenseDTO.class))).thenReturn(updatedExpense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);
        when(expenseMapper.map(any(Expense.class))).thenReturn(expenseDTO);

        // When
        ExpenseDTO result = expenseService.updateExpense(1L, expenseDTO);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(550), result.getAmount());
    }

    @Test
    void updateExpense_whenExpenseNotFound_shouldThrowException() {
        // Given
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, "Flight", BigDecimal.valueOf(550), LocalDate.now(), null);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense(1L, expenseDTO));
    }

}