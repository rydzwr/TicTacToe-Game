package com.rydzwr.tictactoe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.database.service.UserService;
import com.rydzwr.tictactoe.security.constants.SecurityConstants;
import com.rydzwr.tictactoe.security.service.CookieManager;
import com.rydzwr.tictactoe.security.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserService service;
    private final UserRepository repository;
    private final CookieManager cookieManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // CREATING TOKENS
        String accessToken = jwtService.generateAccessToken(request, authentication);
        String refreshToken = jwtService.generateRefreshToken(request, authentication);

        // SAVING REFRESH TOKEN INTO DATABASE
        User user = repository.findByName(authentication.getName());
        user.setRefreshToken(refreshToken);
        service.saveUser(user);

        // CREATING JSON MAP
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("role", jwtService.getUserRole(authentication));

        // CREATING HTTP COOKIE WITH REFRESH TOKEN
        cookieManager.addRefreshToken(response, refreshToken);

        // SENDING RESPONSE
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        response.setStatus(200);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> shouldNotFilter = List.of(SecurityConstants.LOGIN_ENDPOINT/*, SecurityConstants.WEB_SOCKET_HANDSHAKE_ENDPOINT*/);
        return !shouldNotFilter.contains(request.getServletPath());
    }
}
