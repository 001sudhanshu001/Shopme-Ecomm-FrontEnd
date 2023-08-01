package com.ShopmeFrontEnd.Util;

import com.ShopmeFrontEnd.security.oauth2.CustomerOauth2User;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class GetEmailOfAuthenticatedCustomer {
    public static String getEmail(HttpServletRequest request){
        Object principal = request.getUserPrincipal();

        if(principal == null){ // means Customer has not logged-in
            return null; // This is for Shopping cart if user tries to add to cart without log-in
        }

        String customerEmail = null;

        // if customer logged-in using DATABASE
        if(principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken){
            customerEmail = request.getUserPrincipal().getName();
        }else if(principal instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;

            CustomerOauth2User oauth2User = (CustomerOauth2User) oauthToken.getPrincipal();
            customerEmail = oauth2User.getEmail();
        }

        return customerEmail;
    }
}
