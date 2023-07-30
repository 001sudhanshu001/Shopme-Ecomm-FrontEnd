package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.Util.MailSenderUtil;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.EmailSettingBag;
import com.ShopmeFrontEnd.security.CustomerUserDetail;
import com.ShopmeFrontEnd.security.oauth2.CustomerOauth2User;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerControllerFrontEnd {

    @Autowired
    private CustomerServiceFrontEnd customerService;
    private final SettingService settingService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = customerService.listAllCountries();

        model.addAttribute("listCountries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model,
                                 HttpServletRequest request) {

        customerService.registerCustomer(customer);

        try {
            sendVerificationEmail(request, customer);
        } catch (MessagingException | UnsupportedEncodingException e) {
            return "error/500";
        }

        model.addAttribute("pageTitle", "Registration Succeeded");

        return "register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSetting = settingService.getEmailSetting();

        JavaMailSenderImpl mailSender = MailSenderUtil.prepareMailSender(emailSetting);


        String toAddress = customer.getEmail();
        String subject = emailSetting.getCustomerVerifySubject();
        String content = emailSetting.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]",customer.getFullName());

        String verifyURL = MailSenderUtil.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
//        Verify URL : http://localhost:8085/verify?code=KAXnPGXaXXuYq8glVOdp1nRJHZJ2aRNkZUHj0UFmY5Zb8vE6v8nJYSo4hxQdm6qP

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true); // Content will be enabled with html

        mailSender.send(message);

    }

    // This url will handle VerifyURL with the code and check this code, we saved the Customer in DB with
    // the verification code and then for verification we matched the Unique code in the url with the code in DB
    // After verification we enabled the customer
    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        System.out.println("Verify Called");
        boolean verified = customerService.verify(code);

        // return view based on the verification status
        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request){
        // In case of Database login the principalType is UsernamepasswordAuthenticationToken
        // and if we use remember me option then token will be RememberMeAuthenticationToken
        // In case of oauth2 login principalType is OAuth2AuthenticationToken

        // based on all these info we will show account page to customer
//        Object principal = request.getUserPrincipal();
//        String principalType = principal.getClass().getName();
//        System.out.println("Principal Name" + request.getUserPrincipal().getName());
//        System.out.println(principalType);

        String email = getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.getCustomerByEmail(email);
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);
        
        return "customer/account_form";
    }

    private String getEmailOfAuthenticatedCustomer(HttpServletRequest request){
        Object principal = request.getUserPrincipal();
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

    @PostMapping("/update_account_details")
    public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
                                       HttpServletRequest request){
        customerService.update(customer);

        ra.addFlashAttribute("message", "Your Account Details have been updated");

        updateNameForAuthenticatedCustomer(customer, request);
        // To show updated principal.fullName immediately, Because name is Loaded by Spring Security at then time of
        // login only, so the updated name will be displayed only when user do again log-in
        // So to avoid this we use this method
        // when customer Updates his name
        // We have to update CustomerOauth2User class and add a setFullName() method, because we were using customer name
        // from his Google account(if he uses Google OAuth)
        
        return "redirect:/account_details";
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();

        // if customer logged-in using DATABASE
        if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            CustomerUserDetail userDetail = (CustomerUserDetail) principal;
            Customer authenticatedCustomer = userDetail.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());

        }else if(principal instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;

            String fullName = customer.getFirstName() + " " + customer.getLastName();
            CustomerOauth2User oauth2User = (CustomerOauth2User) oauthToken.getPrincipal();
            oauth2User.setFullName(fullName);
        }

    }

}
