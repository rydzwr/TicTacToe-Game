package com.rydzwr.tictactoe.security.filter;

import com.rydzwr.tictactoe.security.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@RequiredArgsConstructor
public class JWTQueryParamAdapterFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getParameter("token");

        if (token != null && !jwtService.validateAuthHeader(request)) {
            final HttpServletRequest httpRequest = request;
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getHeader(String name) {
                    if (name.equals("Authorization"))
                        return "Bearer " + token;
                    return super.getHeader(name);
                }

                @Override
                public Enumeration getHeaderNames() {
                    List<String> names = Collections.list(super.getHeaderNames());
                    names.add("Authorization");
                    return Collections.enumeration(names);
                }

                @Override
                public Enumeration getHeaders(String name) {
                    List<String> values = Collections.list(super.getHeaders(name));
                    if (name.equals("Authorization"))
                        values.add("Bearer " + token);
                    return Collections.enumeration(values);
                }
            };
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }
}
