package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.CategoryDTO;
import com.mthree.company_budget_mng_system.exception.CategoryInvalidBudgetException;
import com.mthree.company_budget_mng_system.mapper.CategoryMapper;
import com.mthree.company_budget_mng_system.model.Category;
import com.mthree.company_budget_mng_system.repository.CategoryRepository;
import com.mthree.company_budget_mng_system.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getBudget() == null) {
            throw new CategoryInvalidBudgetException("Budget cannot be null for category.");
        }
        return categoryMapper.map(categoryRepository.save(categoryMapper.map(categoryDTO)));
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.mapToDtoList(categoryRepository.findAll());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id " + id));
        return categoryMapper.map(category);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id " + id));

        // Update fields as needed
        category.setName(categoryDTO.getName());
        category.setAmount(categoryDTO.getAmount());
        // Update other fields if necessary

        return categoryMapper.map(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id " + id));
        categoryRepository.delete(category);
    }
}
