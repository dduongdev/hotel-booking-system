package com.dduongdev.hotel.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dduongdev.hotel.security.config.SecurityProperties;
import com.dduongdev.hotel.security.service.HotelUserDetailsService;
import com.dduongdev.hotel.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HotelUserDetailsService hotelUserDetailsService;
    private final SecurityProperties securityProperties;
    
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        
        if (jwt != null && jwtService.validate(jwt)) {

            String requestURI = request.getRequestURI();
            boolean phoneVerified = jwtService.extractPhoneVerified(jwt);

            boolean isSkipActivatedUrl = securityProperties.getSkipActivatedUrls().stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI)); 

            if (!phoneVerified && !isSkipActivatedUrl) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\": \"Account is not activated phone number\"}");
                return;
            }

            String username = jwtService.extractUsername(jwt);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = hotelUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
