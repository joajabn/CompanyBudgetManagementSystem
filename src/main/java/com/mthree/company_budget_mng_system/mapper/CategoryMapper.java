package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.CategoryDTO;
import com.mthree.company_budget_mng_system.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO map(Category category);
    Category map(CategoryDTO categoryDTO);
    List<CategoryDTO> mapToDtoList(List<Category> categories);
    List<Category> mapToEntityList(List<CategoryDTO> categoryDTOS);
}
