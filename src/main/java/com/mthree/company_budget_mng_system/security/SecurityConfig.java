package com.mthree.company_budget_mng_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Optionally disable CSRF protection if it's not needed
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Permits all requests without authentication
                );
        return http.build();
    }
}
