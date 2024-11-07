package com.mthree.company_budget_mng_system.dto;

import com.mthree.company_budget_mng_system.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotNull(message = "You need to provide username!")
    private String username;

    @NotNull(message = "Provide your role.")
    private Role role;

    @Size(min = 4, message = "Password too short (min = 4).")
    @NotBlank(message = "Password cannot be blank!")
    private String password;
}
