package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.Util.MailSenderUtil;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.EmailSettingBag;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
}
