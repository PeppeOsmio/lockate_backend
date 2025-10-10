package com.peppeosmio.lockate.anonymous_group.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AGAdminAuthentication implements Authentication {

    private final String agAdminToken;
    private boolean authenticated = true;

    public AGAdminAuthentication(String agAdminToken) {
        this.agAdminToken = agAdminToken;
    }

    public String getAGAdminToken() {
        return agAdminToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("AG_ADMIN"));
    }

    @Override
    @Nullable
    public Object getCredentials() {
        return null;
    }

    @Override
    @Nullable
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return agAdminToken;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated)
            throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return agAdminToken;
    }

    @Override
    public String toString() {
        return "AGMemberAuthentication(\"" + agAdminToken + "\")";
    }
}
