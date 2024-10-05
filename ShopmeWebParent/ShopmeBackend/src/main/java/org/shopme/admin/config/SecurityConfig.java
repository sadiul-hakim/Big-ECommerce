package org.shopme.admin.config;

import org.shopme.admin.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain config(HttpSecurity http) throws Exception {
    	
    	String[] publicUrl = {
    			"/css/**",
    			"/js/**",
    			"/image/**",
    			"/font/**"
    	};
    	
		return http
				.authorizeHttpRequests(auth -> auth.requestMatchers(publicUrl).permitAll())
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.userDetailsService(userDetailsService)
				.formLogin(form->form
						.loginPage("/loginPage")
						.permitAll()
						.loginProcessingUrl("/login")
						.permitAll()
						.defaultSuccessUrl("/", false)
						.failureUrl("/loginPage?error=true")
						)
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
