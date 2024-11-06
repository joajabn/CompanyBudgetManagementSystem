package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.model.Budget;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetDTO map(Budget budget);
    Budget map(BudgetDTO budgetDTO);
    List<BudgetDTO> mapToDtoList(List<Budget> budgets);
    List<Budget> mapToEntityList(List<BudgetDTO> budgetDTOS);
}
