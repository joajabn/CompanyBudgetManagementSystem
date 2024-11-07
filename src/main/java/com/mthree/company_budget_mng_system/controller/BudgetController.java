package com.mthree.company_budget_mng_system.controller;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.exception.BudgetAlreadyExistsException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.service.BudgetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody BudgetDTO budgetDTO) {
        BudgetDTO createdBudget = budgetService.createBudget(budgetDTO);
        return ResponseEntity.ok(createdBudget);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public ResponseEntity<List<BudgetDTO>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @GetMapping("/{budgetId}/expenses")
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public ResponseEntity<List<ExpenseDTO>> getActualExpenses(@PathVariable Long budgetId) {
        List<ExpenseDTO> expenses = budgetService.getActualExpenses(budgetId);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetDTO budgetDTO) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDTO));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{budgetId}/percentage-used")
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public ResponseEntity<BigDecimal> getPercentageOfBudgetUsed(@PathVariable Long budgetId) {
        BigDecimal percentageUsed = budgetService.calculatePercentageOfBudgetUsed(budgetId);
        return ResponseEntity.ok(percentageUsed);
    }

    @GetMapping("/{budgetId}/rest")
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public ResponseEntity<BigDecimal> getRestOfBudgetAvailable(@PathVariable Long budgetId) {
        BigDecimal calculateRestFoBudget = budgetService.calculateRestOfBudget(budgetId);
        return ResponseEntity.ok(calculateRestFoBudget);
    }

    @ExceptionHandler(BudgetAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleBudgetAlreadyExists(BudgetAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
    }


}
