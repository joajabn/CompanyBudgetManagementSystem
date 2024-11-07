package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.dto.CategoryTypeAmountDTO;
import com.mthree.company_budget_mng_system.exception.BudgetAlreadyExistsException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.mapper.BudgetMapper;
import com.mthree.company_budget_mng_system.mapper.ExpenseMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BudgetServiceTest {

    @MockBean
    private BudgetRepository budgetRepository;

    @MockBean
    private BudgetMapper budgetMapper;

    @MockBean
    private ExpenseMapper expenseMapper;

    @Autowired
    private BudgetService budgetService;

    private BudgetDTO budgetDTO;
    private Budget budget;
    private Expense expense;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        budgetDTO = BudgetDTO.builder()
                .id(1L)
                .totalAmount(BigDecimal.valueOf(10000))
                .year(2024)
                .build();
        budgetDTO.setCategoryTypeAmountDTOS(Collections.singletonList(
                new CategoryTypeAmountDTO(CategoryType.HR, BigDecimal.valueOf(5000))
        ));

        expense = Expense.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(200))
                .categoryType(CategoryType.HR)
                .date(LocalDate.of(2024, 10, 01))
                .build();

        budget = Budget.builder()
                .id(1L)
                .totalAmount(BigDecimal.valueOf(10000))
                .year(2024)
                .budgetPlanned(Collections.singletonMap(CategoryType.HR, BigDecimal.valueOf(5000)))
                .actualExpenses(Arrays.asList(expense))
                .build();

        budgetDTO.setCategoryTypeAmountDTOS(Collections.singletonList(
                new CategoryTypeAmountDTO(CategoryType.HR, BigDecimal.valueOf(10000))
        ));
    }

    @Test
    void createBudget_ShouldCreateBudget_WhenValid() {
        // Given
        when(budgetRepository.existsByYear(anyInt())).thenReturn(false);
        when(budgetMapper.toEntity(any(BudgetDTO.class))).thenReturn(budget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(budgetDTO);

        // When
        BudgetDTO createdBudget = budgetService.createBudget(budgetDTO);

        // Then
        assertNotNull(createdBudget);
        assertEquals(budgetDTO.getId(), createdBudget.getId());
        verify(budgetRepository).save(any(Budget.class));  // Ensure save method was called once
    }

    @Test
    void createBudget_ShouldThrowException_WhenBudgetAlreadyExists() {
        // Given
        when(budgetRepository.existsByYear(anyInt())).thenReturn(true);  // Budget for this year already exists

        // When & Then
        BudgetAlreadyExistsException exception = assertThrows(BudgetAlreadyExistsException.class, () -> {
            budgetService.createBudget(budgetDTO);
        });
        assertEquals("A budget for the year 2024 already exists.", exception.getMessage());
    }

    @Test
    void getAllBudgets_ShouldReturnBudgets_WhenExists() {
        // Given
        List<Budget> budgets = List.of(budget);
        when(budgetRepository.findAll()).thenReturn(budgets);
        when(budgetMapper.mapToDtoList(anyList())).thenReturn(List.of(budgetDTO));

        // When
        List<BudgetDTO> allBudgets = budgetService.getAllBudgets();

        // Then
        assertNotNull(allBudgets);
        assertEquals(1, allBudgets.size());
        assertEquals(budgetDTO.getId(), allBudgets.get(0).getId());
    }

    @Test
    void getBudgetById_ShouldReturnBudget_WhenExists() {
        // Given
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(budget));
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(budgetDTO);

        // When
        BudgetDTO retrievedBudget = budgetService.getBudgetById(1L);

        // Then
        assertNotNull(retrievedBudget);
        assertEquals(budgetDTO.getId(), retrievedBudget.getId());
    }

    @Test
    void getBudgetById_ShouldThrowException_WhenNotFound() {
        // Given
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.getBudgetById(1L);
        });
        assertEquals("Budget with given id doesn't exist", exception.getMessage());
    }

    @Test
    void updateBudget_ShouldUpdateBudget_WhenValid() {
        // Given
        CategoryTypeAmountDTO categoryDTO = new CategoryTypeAmountDTO(CategoryType.TRAVEL, BigDecimal.valueOf(500));
        budgetDTO.setCategoryTypeAmountDTOS(List.of(categoryDTO));

        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(budgetDTO);

        // When
        BudgetDTO updatedBudget = budgetService.updateBudget(1L, budgetDTO);

        // Then
        assertNotNull(updatedBudget);
        assertEquals(budgetDTO.getId(), updatedBudget.getId());
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void deleteBudget_ShouldDeleteBudget_WhenExists() {
        // Given
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(budget));

        // When
        budgetService.deleteBudget(1L);

        // Then
        verify(budgetRepository).delete(any(Budget.class));
    }

    @Test
    void deleteBudget_ShouldThrowException_WhenNotFound() {
        // Given
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.deleteBudget(1L);
        });
        assertEquals("Budget with given id doesn't exist", exception.getMessage());
    }

    @Test
    void calculateRestOfBudget_ShouldReturnCorrectRemainingAmount_WhenBudgetExists() {
        // Given
        Long budgetId = 1L;
        Expense expense1 = Expense.builder()
                .id(1L)
                .categoryType(CategoryType.HR)
                .date(LocalDate.of(2024, 9, 30))
                .amount(new BigDecimal("200"))
                .build();
        Expense expense2 = Expense.builder()
                .id(2L)
                .categoryType(CategoryType.HR)
                .date(LocalDate.of(2024, 10, 1))
                .amount(new BigDecimal("150"))
                .build();
        budget.setActualExpenses(List.of(expense1, expense2));

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // When
        BigDecimal remainingBudget = budgetService.calculateRestOfBudget(budgetId);

        // Then
        assertEquals(new BigDecimal("9650"), remainingBudget);
    }

    @Test
    void calculateRestOfBudget_ShouldThrowResourceNotFoundException_WhenBudgetDoesNotExist() {
        // Given
        Long budgetId = 1L;
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> budgetService.calculateRestOfBudget(budgetId));
    }

    @Test
    void calculateRestOfBudget_ShouldThrowIllegalStateException_WhenTotalAmountIsZero() {
        // Given
        Long budgetId = 1L;
        Budget budget = new Budget();
        budget.setId(budgetId);
        budget.setTotalAmount(BigDecimal.ZERO);
        budget.setActualExpenses(Arrays.asList());

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // When / Then
        assertThrows(IllegalStateException.class, () -> budgetService.calculateRestOfBudget(budgetId));
    }

    @Test
    void calculateRestOfBudget_ShouldThrowIllegalStateException_WhenTotalAmountIsNull() {
        // Given
        Long budgetId = 1L;
        Budget budget = new Budget();
        budget.setId(budgetId);
        budget.setTotalAmount(null);
        budget.setActualExpenses(Arrays.asList());

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // When / Then
        assertThrows(IllegalStateException.class, () -> budgetService.calculateRestOfBudget(budgetId));
    }

}