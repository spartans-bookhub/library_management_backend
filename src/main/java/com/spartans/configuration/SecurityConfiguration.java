package com.spartans.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public FilterRegistrationBean getFilter() {
//        FilterRegistrationBean filterbean = new FilterRegistrationBean();
//        filterbean.setFilter(new JWTFilter());
////        filterbean.addUrlPatterns("/api/user/*",
////                "/api/books/*");
//        return filterbean;
//
//    }
}
