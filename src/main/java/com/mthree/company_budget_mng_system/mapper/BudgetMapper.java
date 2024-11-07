package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.BudgetDTO;
import com.mthree.company_budget_mng_system.dto.CategoryTypeAmountDTO;
import com.mthree.company_budget_mng_system.model.Budget;
import com.mthree.company_budget_mng_system.model.CategoryType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
//    BudgetDTO map(Budget budget);
//    Budget map(BudgetDTO budgetDTO);
    List<BudgetDTO> mapToDtoList(List<Budget> budgets);
    List<Budget> mapToEntityList(List<BudgetDTO> budgetDTOS);

    // Mapping between BudgetDTO and Budget
    @Mapping(target = "budgetPlanned", source = "categoryTypeAmountDTOS")
    Budget toEntity(BudgetDTO budgetDTO);
    @Mapping(target = "categoryTypeAmountDTOS", source = "budgetPlanned")
    BudgetDTO toDto(Budget budget);

    // Mapping between CategoryTypeAmountDTO and Map<CategoryType, BigDecimal>
    @IterableMapping(elementTargetType = Map.Entry.class)
    default Map<CategoryType, BigDecimal> mapCategoryTypeAmountDTOsToBudgetPlanned(List<CategoryTypeAmountDTO> categoryTypeAmountDTOS) {
        Map<CategoryType, BigDecimal> plannedBudget = new HashMap<>();
        for (CategoryTypeAmountDTO dto : categoryTypeAmountDTOS) {
            plannedBudget.put(dto.getCategoryType(), dto.getAmount());
        }
        return plannedBudget;
    }

    @IterableMapping(elementTargetType = CategoryTypeAmountDTO.class)
    default List<CategoryTypeAmountDTO> mapBudgetPlannedToCategoryTypeAmountDTOs(Map<CategoryType, BigDecimal> plannedBudget) {
        List<CategoryTypeAmountDTO> categoryTypeAmountDTOs = new ArrayList<>();
        for (Map.Entry<CategoryType, BigDecimal> entry : plannedBudget.entrySet()) {
            CategoryTypeAmountDTO dto = CategoryTypeAmountDTO.builder()
                    .categoryType(entry.getKey())
                    .amount(entry.getValue())
                    .build();
            categoryTypeAmountDTOs.add(dto);
        }
        return categoryTypeAmountDTOs;
    }
}
