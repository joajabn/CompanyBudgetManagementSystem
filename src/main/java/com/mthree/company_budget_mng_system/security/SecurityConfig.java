package com.mthree.company_budget_mng_system.security;

import com.mthree.company_budget_mng_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Disable CSRF protection if needed
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/register/**").permitAll()
//                        .requestMatchers("/api/users/**").hasAuthority("ADMIN")
//                        .requestMatchers("/api/budgets/**").hasAnyRole("MANAGER", "ADMIN")
//                        .requestMatchers("/api/expenses/**").hasAnyRole("USER", "MANAGER", "ADMIN")
//                        .anyRequest().authenticated()  // Require authentication for all other requests
//                )
//                .httpBasic(Customizer.withDefaults());
        http
                .csrf(csrf -> csrf.disable())  // Optionally disable CSRF protection if it's not needed
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Permits all requests without authentication
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(username ->
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"))
        ).passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }
}
