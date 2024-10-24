package com.memberservice.comm.jwt;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

// 토큰 어떻게 디코딩되는지 모르겠어서 일단 이 객체로 디코딩함
@Getter
@ToString
public class UserData implements Authentication {

    private final String memberId;

    public UserData(String memberId) {
        this.memberId = memberId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "";
    }
}
