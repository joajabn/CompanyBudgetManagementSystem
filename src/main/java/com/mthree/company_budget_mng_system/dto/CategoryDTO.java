package com.mthree.company_budget_mng_system.dto;

import com.mthree.company_budget_mng_system.model.Budget;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Data
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    private String name;

    @NotNull(message = "You need to provide value for this category!")
    @Positive(message = "The amount must be positive!")
    private BigDecimal amount;

    @NotNull(message = "You need to provide main budget.")
    private Budget budget;

    private List<ExpenseDTO> expenses;
}
