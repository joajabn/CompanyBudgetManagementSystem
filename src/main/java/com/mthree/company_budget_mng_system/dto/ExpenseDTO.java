package com.mthree.company_budget_mng_system.dto;

import com.mthree.company_budget_mng_system.model.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;

    private String description;

    @NotNull(message = "You need to provide value!")
    @Positive(message = "The total amount must be positive!")
    private BigDecimal amount;

    private LocalDate date;

    @NotNull(message = "Provide category of your expense.")
    private CategoryType categoryType;
}
