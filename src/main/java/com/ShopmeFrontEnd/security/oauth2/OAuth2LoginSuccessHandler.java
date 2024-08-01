package com.ShopmeFrontEnd.security.oauth2;

import com.ShopmeFrontEnd.entity.readonly.AuthenticationType;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Lazy
    @Autowired
    private CustomerServiceFrontEnd customerService;
    // This method will be invoked after the oauth login, So we can get user's detail if user is not in the database
    // and store then save it into database
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomerOauth2User oauth2User = (CustomerOauth2User) authentication.getPrincipal();

        String name = oauth2User.getName();
        String email = oauth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oauth2User.getClientName();

        log.info("onAuthenticationSuccess : " + name + " | " + email);
        log.info("Client Name " + clientName);

        AuthenticationType authenticationType = getAuthenticationType(clientName);
        Customer customer = customerService.getCustomerByEmail(email);

        if(customer == null){ // means this user does not exist in our database, so we will enter it into DB
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        }else { // Just changing Authentication Type
             customerService.updateAuthenticationType(customer, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if(clientName.equals("Google")){
            return AuthenticationType.GOOGLE;
        }else if(clientName.equals("Facebook")){
            return AuthenticationType.FACEBOOK;
        } else if (clientName.equals("Github")) {
            return AuthenticationType.GITHUB;
        }else {
            return AuthenticationType.DATABASE;
        }
    }
}
