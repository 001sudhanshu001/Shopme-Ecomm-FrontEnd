package com.ShopmeFrontEnd.security;

import com.ShopmeFrontEnd.dao.CustomerRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    private CustomerRepoFrontEnd customerRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByEmail(username);

        if(customer == null){
            throw new UsernameNotFoundException("No Customer with the email " + username);
        }
        return new CustomerUserDetail(customer);
    }
}
