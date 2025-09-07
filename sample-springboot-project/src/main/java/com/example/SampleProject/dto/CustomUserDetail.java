package com.example.SampleProject.dto;

import com.example.SampleProject.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {

    private final User user;

    public CustomUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Add roles with the "ROLE_" prefix
        this.user.getRoles().forEach(role -> {
            authorityList.add(new SimpleGrantedAuthority(role.getName()));

            // Add permissions without the prefix
            role.getPermissions().forEach(permission -> {
                authorityList.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        return authorityList;
    }


    public Long getUserId(){
        return this.user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {return user.getName();}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
