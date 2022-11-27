package com.github.rahulpat.antifraudsystem.auth;

import com.github.rahulpat.antifraudsystem.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// This class is the middle-man between our User Store, UserDetailsServiceImpl and Spring Security.
// This class implements UserDetails because Spring Security can only recognize users of this type
// In the constructor of this class, we add the User from our user store, and it becomes of type
// UserDetails which can be recognized by Spring Security.
// There are 7 methods to implement, 3 of them are auto-marked as true.
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> rolesAndAuthorities;
    private boolean isAccountNonLocked;

    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.rolesAndAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
        this.isAccountNonLocked = user.isUserNotLocked();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    // 3 remaining methods that just return true
    @Override
    public boolean isAccountNonExpired() {
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
