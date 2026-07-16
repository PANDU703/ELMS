package com.elms.employee_leave_management_system.security;

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

                    .requestMatchers(
                            "/login",
                            "/signup",
                            "/register",
                            "/403",
                            "/404",
                            "/api/departments",
                            "/api/employees/**",
                            "/css/**",
                            "/js/**",
                            "/images/**"
                    ).permitAll()

                    .requestMatchers(
                        "/manager-dashboard",
                        "/manage-leaves",
                        "/employees"
                    ).hasAnyRole("MANAGER", "ADMIN")

                    .requestMatchers(
                        "/dashboard",
                        "/profile",
                        "/apply-leave",
                        "/leave-history"
                    ).hasAnyRole("EMPLOYEE", "MANAGER","ADMIN")

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