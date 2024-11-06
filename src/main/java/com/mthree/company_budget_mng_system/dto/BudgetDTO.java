package com.mthree.company_budget_mng_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private List<CategoryTypeAmountDTO> categoryTypeAmountDTOS = new ArrayList<>();

    public boolean validateBudget(BudgetDTO budgetDTO) {
        BigDecimal totalCalculatedAmount = categoryTypeAmountDTOS.stream()
                .map(CategoryTypeAmountDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Check if the total calculated amount is less than or equal to the total budget amount
        if (totalCalculatedAmount.compareTo(budgetDTO.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("The sum of category amounts exceeds the total budget amount.");
        }

        // Check if the total calculated amount is equal to the total budget amount (not exceeding)
        if (totalCalculatedAmount.compareTo(budgetDTO.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("The sum of category amounts must equal the total budget amount.");
        }

        return true;
    }

}
