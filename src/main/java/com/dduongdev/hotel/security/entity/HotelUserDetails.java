package com.dduongdev.hotel.security.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dduongdev.hotel.entity.User;

@Getter
@Setter
public class HotelUserDetails extends org.springframework.security.core.userdetails.User {
    
    private int id;
    private User.Role role;

    public HotelUserDetails(int id, String username, String password, User.Role role) {
        super(username, password, List.of());
        this.id = id;
        this.role = role;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
