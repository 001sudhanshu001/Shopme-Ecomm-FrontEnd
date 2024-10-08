package com.ShopmeFrontEnd.security;

import com.ShopmeFrontEnd.entity.readonly.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerUserDetail implements UserDetails {
    private Customer customer;

    public CustomerUserDetail(Customer customer) {
        this.customer = customer;
    }

    @Override // Customer does not have any role
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

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
        return customer.isEnabled();
    }

    public String getFullName() {
        return customer.getFirstName() + " "  + customer.getLastName();
    }
    public Customer getCustomer() {
        return this.customer;
    }
}
