package com.userauthenticationmicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    
    //Create the bean for password encoder, current default encoder is BCryptPasswordEncoder.
    //Keeping it abstracted and not using BCrypt directly makes it easier to switch to a different encoding algorithm(like Argon2) in the future without needing to change the code that uses it.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/v1/signup", "/auth/v1/login").permitAll()  // Public endpoints
                .anyRequest().authenticated()  // All other endpoints require auth
            );
        
        return httpSecurity.build();

    }
}
