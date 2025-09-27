package com.ly.saas.server.config;

import com.ly.saas.server.filter.TenantRoutingFilter;
import com.ly.saas.server.security.JwtAuthenticationEntryPoint;
import com.ly.saas.server.security.JwtAuthenticationFilter;
import com.ly.saas.server.security.SaaSUserDetailsService;
import com.ly.saas.wei.core.constant.Constants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
//@EnableWebSecurity(debug = true)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Resource(name = "saasUserDetailsService")
    private SaaSUserDetailsService userDetailsService;

    @Resource
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Resource
    private TenantRoutingFilter tenantRoutingFilter;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                com.ly.saas.wei.core.constant.Constants.API_PREFIX + "/auth/**",
                                com.ly.saas.shu.core.constant.Constants.API_PREFIX + "/auth/**",
                                com.ly.saas.wu.core.constant.Constants.API_PREFIX + "/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        // 添加租户感知过滤器 - 应该在JWT过滤器之前，但不影响路径匹配
        http.addFilterBefore(tenantRoutingFilter, UsernamePasswordAuthenticationFilter.class);

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}