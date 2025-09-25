package com.ly.saas.shu.config;

import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.security.JwtAuthenticationEntryPoint;
import com.ly.saas.shu.security.JwtAuthenticationFilter;
import com.ly.saas.shu.security.SaaSUserDetailsService;
import com.ly.saas.shu.security.TenantAwareAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Resource(name = Constants.PREFIX + "SaasUserDetailsService")
    private SaaSUserDetailsService userDetailsService;

    @Resource(name = Constants.PREFIX + "JwtAuthenticationEntryPoint")
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Resource(name = Constants.PREFIX + "TenantAwareAuthenticationFilter")
    private TenantAwareAuthenticationFilter tenantAwareAuthenticationFilter;

    @Bean(Constants.PREFIX + "JwtAuthenticationFilter")
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(Constants.PREFIX + "DaoAuthenticationProvider")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean(Constants.PREFIX + "AuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean(Constants.PREFIX + "PasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(Constants.API_PREFIX + "/auth/**").permitAll()
                        .requestMatchers(Constants.API_PREFIX + "/test/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 添加租户感知过滤器
        http.addFilterBefore(tenantAwareAuthenticationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}