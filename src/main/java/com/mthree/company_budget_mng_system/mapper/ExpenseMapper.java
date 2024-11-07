package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "categoryType", source = "categoryType")
    ExpenseDTO map(Expense expense);

    @Mapping(target = "categoryType", source = "categoryType")
    Expense map(ExpenseDTO expenseDTO);
    List<ExpenseDTO> mapToDtoList(List<Expense> expenses);
    List<Expense> mapToEntityList(List<ExpenseDTO> expenseDTOS);
}
