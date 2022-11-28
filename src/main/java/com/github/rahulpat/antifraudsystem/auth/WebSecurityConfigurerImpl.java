package com.github.rahulpat.antifraudsystem.auth;

import com.github.rahulpat.antifraudsystem.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.sql.DataSource;

// This class is used to configure our Spring Boot application authentication and authorization using Spring Security

@EnableWebSecurity
@Configuration
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    // RestAuthenticationEntryPoint is an instance of the class that implements the AuthenticationEntryPoint interface.
    // This endpoint handles authentication errors.
    @Autowired
    AuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    DataSource dataSource;
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // The code block below can be uncommented to hardcode a User into memory for testing purposes only
/*
        auth.inMemoryAuthentication()
                .withUser("user2")
                .password(getEncoder().encode("pass2"))
                .roles("MERCHANT")
                .and()
                .withUser("user3")
                .password(getEncoder().encode("pass3"))
                .roles("SUPPORT")
                .and()
                .passwordEncoder(getEncoder());
*/


        // We are using a custom implementation of authentication using UserDetailsService and an H2 database
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());

    }

    // Authorization configuration
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .mvcMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.POST,"/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .mvcMatchers(HttpMethod.GET,"/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .mvcMatchers(HttpMethod.DELETE,"/api/antifraud/suspicious-ip/*").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/stolencard").hasRole("SUPPORT")
                .mvcMatchers("/actuator/shutdown").permitAll() // needs to run test
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}

