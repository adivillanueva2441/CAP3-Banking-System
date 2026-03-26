package com.example.banking.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://localhost:4200");  // Your Angular app
        config.addAllowedMethod("*");                      // GET, POST, PUT, PATCH, DELETE
        config.addAllowedHeader("*");                      // Authorization, Content-Type, etc.
        config.setAllowCredentials(true);                  // Allow cookies/auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply to all endpoints

        return new CorsFilter(source);
    }
}