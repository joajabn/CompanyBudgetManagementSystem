package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.dto.CategoryDTO;
import com.mthree.company_budget_mng_system.exception.BudgetAlreadyExistsException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.mapper.BudgetMapper;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.repository.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Slf4j
@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
    }

    @Transactional
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        // Check if a budget for the specified year already exists
        if (budgetRepository.existsByYear(budgetDTO.getYear())) {
            throw new BudgetAlreadyExistsException("A budget for the year " + budgetDTO.getYear() + " already exists.");
        }

        Budget budget = budgetMapper.map(budgetDTO);
        return budgetMapper.map(budgetRepository.save(budget));
    }

    public List<BudgetDTO> getAllBudgets() {
        return budgetMapper.mapToDtoList(budgetRepository.findAll());
    }

    public BudgetDTO getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id " + id));
        return budgetMapper.map(budget);
    }

    @Transactional
    public BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id " + id));

        // Update fields as needed
        budget.setTotalAmount(budgetDTO.getTotalAmount());
        budget.setYear(budgetDTO.getYear());
        // Update categories if needed

        return budgetMapper.map(budgetRepository.save(budget));
    }

    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id " + id));
        budgetRepository.delete(budget);
    }

    public BigDecimal calculatePercentageOfBudgetUsed(Long budgetId) {
        BudgetDTO budgetDTO = budgetRepository.findById(budgetId)
                .map(budget -> budgetMapper.map(budget))
                .orElseThrow(() -> {
                    String message = "Budget with given id doesn't exist";
                    log.error(message);
                    throw new ResourceNotFoundException(message);
                });
        BigDecimal totalAmount = budgetDTO.getTotalAmount();
        if(totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0){
            throw new IllegalStateException("Total budget amount must be greater than zero.");
        }
        BigDecimal totalExpenses = budgetDTO.getCategories().stream()
                .map(CategoryDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalExpenses
                .multiply(BigDecimal.valueOf(100))
                .divide(totalAmount, 2, RoundingMode.HALF_UP);
    }


}
