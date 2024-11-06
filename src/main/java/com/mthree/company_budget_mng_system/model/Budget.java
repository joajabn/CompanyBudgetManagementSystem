package com.mthree.company_budget_mng_system.model;

import com.mthree.company_budget_mng_system.dto.CategoryTypeAmountDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Integer year;

    @ElementCollection
    @CollectionTable(name = "budget_category_amounts",
            joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyColumn(name = "category_type")
    @Column(name = "amount")
    private Map<CategoryType, BigDecimal> budgetPlanned = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    private List<Expense> actualExpenses = new ArrayList<>();

    public void allocateBudgetToCategories(List<CategoryTypeAmountDTO> categoryAmountDTOs) {
//        categoryAmounts.clear(); // Clear any existing amounts before reallocating?
        for (CategoryTypeAmountDTO dto : categoryAmountDTOs) {
            budgetPlanned.put(dto.getCategoryType(), dto.getAmount());
        }
    }

//    public void addExpense(CategoryType categoryType, Expense expense) {
//        actualExpenses.computeIfAbsent(categoryType, k -> new ArrayList<>()).add(expense);
//    }

}
