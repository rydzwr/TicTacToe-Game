package com.rydzwr.tictactoe.security.filter.jwt;

import com.rydzwr.tictactoe.security.constants.SecurityConstants;
import com.rydzwr.tictactoe.security.filter.FilterErrorHandler;
import com.rydzwr.tictactoe.security.service.JWTService;
import com.rydzwr.tictactoe.security.service.TokenBlackList;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    private final TokenBlackList tokenBlackList;

    private final FilterErrorHandler errorHandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean validAuth = jwtService.validateAuthHeader(request);

        String token = "";
        if (validAuth) {
            token = jwtService.getTokenFromAuthHeader(request);
        }

        if (!validAuth || tokenBlackList.contains(token)) {
            errorHandler.sendError(response, HttpStatus.FORBIDDEN, "Invalid Auth Token!");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(jwtService.getAuthFromToken(request));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> shouldNotFilter = List.of(SecurityConstants.LOGIN_ENDPOINT,
              /*  SecurityConstants.WEB_SOCKET_HANDSHAKE_ENDPOINT,*/
                SecurityConstants.REGISTER_ENDPOINT);
        return shouldNotFilter.contains(request.getServletPath());
    }
}
