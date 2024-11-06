package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.exception.BudgetAlreadyExistsException;
import com.mthree.company_budget_mng_system.mapper.BudgetMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    private BudgetDTO budgetDTO;
    private Budget existingBudget;

    @BeforeEach
    void setUp() {
        budgetDTO = BudgetDTO.builder()
                .id(1L)
                .totalAmount(new BigDecimal("50000"))
                .year(2024)
                .build();

        existingBudget = Budget.builder()
                .id(1L)
                .totalAmount(new BigDecimal("50000"))
                .year(2024)
                .build();
    }

    @Test
    void createBudget_whenBudgetAlreadyExists_shouldThrowException() {
        when(budgetRepository.existsByYear(any(Integer.class))).thenReturn(true);

        BudgetAlreadyExistsException exception = assertThrows(
                BudgetAlreadyExistsException.class,
                () -> budgetService.createBudget(budgetDTO)
        );

        String expectedMessage = "A budget for the year " + budgetDTO.getYear() + " already exists.";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void createBudget_whenBudgetDoesNotExist_shouldReturnCreatedBudget() {
        when(budgetRepository.existsByYear(any(Integer.class))).thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(existingBudget);
        when(budgetMapper.map(any(BudgetDTO.class))).thenReturn(existingBudget);
        when(budgetMapper.map(any(Budget.class))).thenReturn(budgetDTO); // Mock saving the budget to DTO conversion

        BudgetDTO createdBudget = budgetService.createBudget(budgetDTO);

        assertNotNull(createdBudget, "Expected budgetDTO to be non-null");
        assertEquals(budgetDTO.getTotalAmount(), createdBudget.getTotalAmount());
        assertEquals(budgetDTO.getYear(), createdBudget.getYear());
    }

}