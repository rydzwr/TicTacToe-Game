package com.rydzwr.tictactoe.security.config;

import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.database.service.UserService;
import com.rydzwr.tictactoe.security.filter.FilterErrorHandler;
import com.rydzwr.tictactoe.security.filter.JWTQueryParamAdapterFilter;
import com.rydzwr.tictactoe.security.filter.RequestValidationBeforeFilter;
import com.rydzwr.tictactoe.security.filter.jwt.AuthenticationFilter;
import com.rydzwr.tictactoe.security.filter.jwt.AuthorizationFilter;
import com.rydzwr.tictactoe.security.filter.jwt.JWTTokenRefreshFilter;
import com.rydzwr.tictactoe.security.filter.jwt.LogoutFilter;
import com.rydzwr.tictactoe.security.service.AuthHeaderDataExtractor;
import com.rydzwr.tictactoe.security.service.CookieManager;
import com.rydzwr.tictactoe.security.service.JWTService;
import com.rydzwr.tictactoe.security.service.TokenBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.rydzwr.tictactoe.security.constants.SecurityConstants.*;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenBlackList tokenBlackList;

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private AuthHeaderDataExtractor extractor;

    @Autowired
    private FilterErrorHandler errorHandler;

    @Autowired
    private CorsConfig corsConfig;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .configurationSource(corsConfig.corsConfigurationSource())
                .and()
                .csrf()
                .disable().headers().frameOptions().sameOrigin();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests().requestMatchers(
                LOGIN_ENDPOINT,
                TOKEN_REFRESH_ENDPOINT,
                REGISTER_ENDPOINT,
                WEB_SOCKET_HANDSHAKE_ENDPOINT
        ).permitAll();

        http.authorizeHttpRequests().anyRequest().authenticated();

        http.addFilterBefore(
                new JWTQueryParamAdapterFilter(jwtService),
                BasicAuthenticationFilter.class
        );

        http.addFilterBefore(
                new RequestValidationBeforeFilter(extractor),
                BasicAuthenticationFilter.class
        );

        http.addFilterBefore(
                new AuthorizationFilter(jwtService, tokenBlackList, errorHandler),
                BasicAuthenticationFilter.class
        );

        http.addFilterAfter(
                new AuthenticationFilter(jwtService, service, repository, cookieManager),
                BasicAuthenticationFilter.class
        );

        http.addFilterBefore(
                new LogoutFilter(jwtService, tokenBlackList, repository, cookieManager),
                BasicAuthenticationFilter.class
        );
        http.addFilterBefore(
                new JWTTokenRefreshFilter(repository, jwtService, cookieManager, tokenBlackList, errorHandler),
                BasicAuthenticationFilter.class
        );

        http.httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
