package ru.otus.hw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.RestApiErrorDto;
import ru.otus.hw.dto.SuccessfulLoginResponseDto;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        
    private static final String[] PUBLIC_PATHS = {
        "/login", "/index.html", "/static/**", "/assets/**",
        "/*.js", "/*.css", "/*.ico", "/*.png"
    };
    
    private static final String[] AUTH_PATHS = {
        "/api/v1/auth/login", "/api/v1/auth/logout", "/api/v1/auth/me"
    };
    
    private final ObjectMapper objectMapper = new ObjectMapper();
  
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .requestMatchers(AUTH_PATHS).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/v1/auth/login")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
            )
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessHandler(logoutHandler())
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(entryPoint())
            )
            .build();
    }
    
    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            response.setStatus(200);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), new SuccessfulLoginResponseDto(true, "Login successful"));
        };
    }
    
    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), new RestApiErrorDto("Unauthorized", "Invalid credentials"));
        };
    }
    
    private LogoutSuccessHandler logoutHandler() {
        return (request, response, authentication) -> {
            response.setStatus(200);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), new SuccessfulLoginResponseDto(true, "Logout successful"));
        };
    }
    
    private AuthenticationEntryPoint entryPoint() {
        return (request, response, authException) -> {
            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(401);
                response.setContentType("application/json");
                objectMapper.writeValue(
                    response.getWriter(), 
                    new RestApiErrorDto("Unauthorized", "Authentication required")
                );
            } else {
                response.sendRedirect("/login");
            }
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}