package com.thatbackendguy.quizapp.config;

import com.thatbackendguy.quizapp.security.JwtAuthenticationEntryPoint;
import com.thatbackendguy.quizapp.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig
{

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {

        http.csrf(csrf -> csrf.disable())
                .authorizeRequests()
                .requestMatchers("/api/departments/**")
                .authenticated()
                .requestMatchers("/api/quizzes/**")
                .authenticated()
                .requestMatchers("/api/users/**")
                .authenticated()
                .requestMatchers("/api/auth/login")
                .permitAll()
                .requestMatchers("/api/auth/create-user")
                .permitAll()
                .and()
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider()
    {

        var daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return daoAuthenticationProvider;
    }

}
