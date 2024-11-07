package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.UserDTO;
import com.mthree.company_budget_mng_system.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
}
