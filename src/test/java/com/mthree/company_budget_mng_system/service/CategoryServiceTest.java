package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.CategoryDTO;
import com.mthree.company_budget_mng_system.exception.CategoryInvalidBudgetException;
import com.mthree.company_budget_mng_system.exception.CategoryNotFoundException;
import com.mthree.company_budget_mng_system.mapper.CategoryMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.Category;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import com.mthree.company_budget_mng_system.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_whenCategoryValid_shouldReturnCreatedCategory() {
        // Given
        Budget budget = new Budget(1L, BigDecimal.valueOf(5000), 2024, null); // Ensure budget is not null
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Travel", BigDecimal.valueOf(1000), budget, null);
        Category category = new Category(1L, "Travel", BigDecimal.valueOf(1000), budget, null);

        // Mocking repository and mapper behavior
        lenient().when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(budget));
        // Mocking categoryMapper.map with valid CategoryDTO input
        lenient().when(categoryMapper.map(any(CategoryDTO.class))).thenReturn(category);
        lenient().when(categoryRepository.save(any(Category.class))).thenReturn(category);
        lenient().when(categoryMapper.map(any(Category.class))).thenReturn(categoryDTO);

        // When
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);

        // Then
        assertNotNull(createdCategory);
        assertEquals("Travel", createdCategory.getName());
        assertEquals(BigDecimal.valueOf(1000), createdCategory.getAmount());
        assertNotNull(createdCategory.getBudget()); // Ensure budget is not null
    }

    @Test
    void createCategory_whenCategoryInvalid_shouldThrowException() {
        // Given
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Travel", BigDecimal.valueOf(1000), null, null);
        lenient().when(budgetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryInvalidBudgetException.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void getCategoryById_whenCategoryExists_shouldReturnCategory() {
        // Given
        Category category = new Category(1L, "Travel", BigDecimal.valueOf(1000), null, null);
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Travel", BigDecimal.valueOf(1000), null, null);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.map(any(Category.class))).thenReturn(categoryDTO);

        // When
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Travel", result.getName());
    }

    @Test
    void getCategoryById_whenCategoryNotFound_shouldThrowException() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void updateCategory_whenCategoryExists_shouldReturnUpdatedCategory() {
        // Given
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Travel", BigDecimal.valueOf(1200), null, null);
        Category category = new Category(1L, "Travel", BigDecimal.valueOf(1000), null, null);
        Category updatedCategory = new Category(1L, "Travel", BigDecimal.valueOf(1200), null, null);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        lenient().when(categoryMapper.map(any(CategoryDTO.class))).thenReturn(updatedCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.map(any(Category.class))).thenReturn(categoryDTO);

        // When
        CategoryDTO result = categoryService.updateCategory(1L, categoryDTO);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1200), result.getAmount());
    }

    @Test
    void updateCategory_whenCategoryNotFound_shouldThrowException() {
        // Given
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Travel", BigDecimal.valueOf(1200), null, null);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, categoryDTO));
    }


}