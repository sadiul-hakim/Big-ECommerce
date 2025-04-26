package org.shopme.site.config;

import lombok.RequiredArgsConstructor;
import org.shopme.site.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final String REMEMBER_ME_KEY = "c2V0dGluZ21hZ25ldGxvb3NlcGxhc3RpY2NhZ2VtYXN0ZXJibGFja3BhcmFsbGVsbGU=";
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

        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicUrl).permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
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
