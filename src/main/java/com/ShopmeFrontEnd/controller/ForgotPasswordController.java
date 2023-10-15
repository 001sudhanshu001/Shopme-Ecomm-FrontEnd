package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.Util.MailSenderUtil;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.EmailSettingBag;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final CustomerServiceFrontEnd customerService;
    private final SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password") // Instead of using HttpServletRequest we can @ParamRequest, but here we need site url also
    public String processRequestForm(HttpServletRequest request, /*@RequestParam("email") String email,*/ Model model){
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = MailSenderUtil.getSiteURL(request) + "/reset_password?token=" + token;
            //url from getSiteURL(request) -> http://localhost:8085

            sendEmail(link, email);

            model.addAttribute("message", "We have sent a reset password link to your email. " +
                    "Please check your Registered Email to reset password");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Something went Wrong !!! Please try again....");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link,String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSetting = settingService.getEmailSetting();
        JavaMailSenderImpl mailSender = MailSenderUtil.prepareMailSender(emailSetting);

        String toAddress = email;
        String subject = "Here is the link to reset your password";

        String content = "<p>Hello,<p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";


        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("/reset_password") // This URL is provided in the email to the Customer to reset his password
    public String showResetForm(@Param("token") String token, Model model){
        Customer customer = customerService.getByResetPasswordToken(token);
        if(customer != null){
            model.addAttribute("token", token);
        }else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "Invalid Token");
            return "message";
        }

        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model){
        String token = request.getParameter("token"); // From hidden field in reset_password_form.html
        String password = request.getParameter("password"); // from then reset_password_form.html, name must be same in html form

        try {
            customerService.updatePassword(token, password);

            model.addAttribute("pageTitle", "Reset Your Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "Password has been changed successfully.");

            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }


}
