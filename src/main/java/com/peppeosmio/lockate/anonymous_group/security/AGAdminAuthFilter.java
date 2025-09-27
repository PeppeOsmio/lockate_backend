package com.peppeosmio.lockate.anonymous_group.security;

import com.peppeosmio.lockate.anonymous_group.service.AnonymousGroupService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AGAdminAuthFilter extends OncePerRequestFilter {
    private final AnonymousGroupService anonymousGroupService;

    public AGAdminAuthFilter(AnonymousGroupService anonymousGroupService) {
        this.anonymousGroupService = anonymousGroupService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        // "AGAdmin <token>"
        var start = "AGAdmin ";
        if (authHeader != null && authHeader.startsWith(start)) {
            try {
                String[] parts =
                        authHeader.substring(start.length()).trim().split("\\s+");
                if (parts.length == 1) {
                    String adminToken = parts[0];
                    var authentication =
                            anonymousGroupService.authAdmin(adminToken);
                    if (authentication != null) {
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
