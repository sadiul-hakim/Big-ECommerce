package org.shopme.admin.config;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final String REMEMBER_ME_KEY = "ZmlsbG1hcGNlcnRhaW5seWJveHN1Y2huZWFybHluZXdzc3RyaWtldGlyZWRuZXN0Y28=";
    private final CustomUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain config(HttpSecurity http) throws Exception {

        String[] publicUrl = {
                "/css/**",
                "/js/**",
                "/image/**",
                "/font/**",
                "/images/**"
        };

        String[] adminAccess = {
                "/users/**",
                "/settings/**",
        };

        String[] editorAccess = {
                "/categories/**",
                "/brands/**",
                "/articles/**",
                "/menus/**"
        };

        String[] salesPersonAccess = {
                "/customers/**",
                "/shipping/**",
                "/reports/**"
        };

        String[] assistantAccess = {
                "/questions/**",
                "/reviews/**"
        };

        String[] adminAndEditorAndSalesperson = {
                "/products/product-details-page/**",
                "/products/add-details",
                "/products/remove-details"
        };

        String[] adminAndEditorAndShipperAndSalesperson = {
                "/products",
                "/products/search",
                "/products/export-csv",
                "/products/view_page/**"
        };

        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicUrl).permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(editorAccess).hasAnyRole("EDITOR", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(salesPersonAccess).hasAnyRole("SALESPERSON", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(assistantAccess).hasAnyRole("ASSISTANT", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAndEditorAndShipperAndSalesperson).hasAnyRole("ADMIN", "EDITOR", "SALESPERSON", "SHIPPER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAndEditorAndSalesperson).hasAnyRole("ADMIN", "EDITOR", "SALESPERSON"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/**").hasAnyRole("ADMIN", "EDITOR"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/orders/**").hasAnyRole("ADMIN", "SALESPERSON", "SHIPPER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(adminAccess).hasRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .formLogin(form -> form
                        .loginPage("/loginPage")
                        .permitAll()
                        .loginProcessingUrl("/login")
                        .permitAll()
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/loginPage?error=true")
                )
                .rememberMe(me -> me.key(REMEMBER_ME_KEY)
                        .tokenValiditySeconds(86400)
                        .rememberMeParameter("remember-me"))
                .logout(logout -> logout.logoutSuccessUrl("/loginPage?logout=true")
                        .logoutUrl("/logout")
                        .permitAll())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
