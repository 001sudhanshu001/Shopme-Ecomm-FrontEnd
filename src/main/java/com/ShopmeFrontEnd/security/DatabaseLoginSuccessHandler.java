package com.ShopmeFrontEnd.security;

import com.ShopmeFrontEnd.entity.readonly.AuthenticationType;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    // In this we will just update authentication Type
    @Lazy
    @Autowired
    private CustomerServiceFrontEnd customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        CustomerUserDetail userDetail = (CustomerUserDetail) authentication.getPrincipal();
        Customer customer = userDetail.getCustomer();

        System.out.println("After Database login success");

        // MAKE ONLY NECESSARY CALL TO DATABASE
        if(customer.getAuthenticationType() == null || !customer.getAuthenticationType().equals(AuthenticationType.DATABASE)){
            customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
