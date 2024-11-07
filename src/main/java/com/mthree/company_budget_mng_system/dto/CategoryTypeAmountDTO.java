package com.mthree.company_budget_mng_system.dto;

import com.mthree.company_budget_mng_system.model.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTypeAmountDTO {

    @NotNull
    private CategoryType categoryType;

    @NotNull
    @Positive
    private BigDecimal amount; // Amount to allocate to this category
}
