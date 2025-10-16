package com.spartans.config;

import com.spartans.filter.JWTFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public FilterRegistrationBean getFilter() {
//        FilterRegistrationBean filterbean = new FilterRegistrationBean();
//        filterbean.setFilter(new JWTFilter(jwtSecret));
//        filterbean.addUrlPatterns("/api/students/*",
//                "/api/admin/*");
//        return filterbean;
//
//    }
}
