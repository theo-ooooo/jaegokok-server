package com.jaegokok.api.security;

import com.jaegokok.domain.auth.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = jwtProvider.parseToken(token);

                // refresh token은 인증에 사용 불가
                if (!"access".equals(claims.get("type", String.class))) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Long memberId = Long.parseLong(claims.getSubject());
                String role = claims.get("role", String.class);

                UserPrincipal principal = new UserPrincipal(
                        memberId,
                        claims.getSubject(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // 토큰이 존재하지만 유효하지 않음 (만료 포함) → 401 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"토큰이 만료되었거나 유효하지 않습니다.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
