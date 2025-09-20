package com.peppeosmio.lockate.anonymous_group.security;

import com.peppeosmio.lockate.anonymous_group.service.AnonymousGroupService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AGMemberAuthFilter extends OncePerRequestFilter {
    private final AnonymousGroupService anonymousGroupService;

    public AGMemberAuthFilter(AnonymousGroupService anonymousGroupService) {
        this.anonymousGroupService = anonymousGroupService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        // "AGMember <id> <token>"
        var start = "AGMember ";
        if (authHeader != null && authHeader.startsWith(start)) {
            try {
                String[] parts =
                        authHeader.substring(start.length()).trim().split("\\s+");

                if (parts.length == 2) {
                    String memberIdStr = parts[0];
                    String memberToken = parts[1];
                    UUID memberId = UUID.fromString(memberIdStr);
                    var authentication =
                            anonymousGroupService.authMember(memberId, memberToken);
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
