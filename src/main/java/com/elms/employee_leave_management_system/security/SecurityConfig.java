package com.elms.employee_leave_management_system.security;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler successHandler;
        public SecurityConfig(
        CustomUserDetailsService userDetailsService,
        CustomLoginSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }
    //@Bean
    //public PasswordEncoder passwordEncoder() {
      //  return new BCryptPasswordEncoder();
    //}
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                // Public pages and resources
                .requestMatchers(
                    "/login",
                    "/signup",
                    "/register",
                    "/403",
                    "/404",
                    "/api/departments",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                // Employee API security
                .requestMatchers("/api/employees/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/employees").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/*")
                .hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/employees/*")
                .hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/*")
                .hasRole("ADMIN")
                // Employee and manager pages
                .requestMatchers(
                    "/dashboard",
                    "/profile",
                    "/apply-leave",
                    "/leave-history",
                    "/medical-certificate"
                ).hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                // Manager pages
                .requestMatchers(
                    "/manager-dashboard",
                    "/manage-leaves",
                    "/employees"
                ).hasAnyRole("MANAGER", "ADMIN")
                // Employee leave application
                .requestMatchers(HttpMethod.POST, "/api/leaves")
                .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                // Employee's own leave history
                .requestMatchers(HttpMethod.GET, "/api/leaves/my")
                .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                // Manager leave management
                .requestMatchers(HttpMethod.GET, "/api/leaves")
                .hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/leaves/*/approve",
                    "/api/leaves/*/reject"
                ).hasAnyRole("MANAGER", "ADMIN")
                // Only managers and admins can view certificates
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/leaves/*/certificate"
                ).hasAnyRole("MANAGER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler(successHandler)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
            );
        return http.build();
    }
}