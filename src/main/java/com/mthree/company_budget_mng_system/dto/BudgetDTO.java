package com.mthree.company_budget_mng_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDTO {

    private Long id;

    @NotNull(message = "You need to provide value!")
    @Positive(message = "The total amount must be positive!")
    private BigDecimal totalAmount;

    private Integer year;

    private List<CategoryDTO> categories;

    public void validateBudget(BudgetDTO budgetDTO) {
        BigDecimal totalCategoryAmount = budgetDTO.getCategories().stream()
                .map(CategoryDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCategoryAmount.compareTo(budgetDTO.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("The sum of category amounts must equal the total budget amount.");
        }
    }

}
