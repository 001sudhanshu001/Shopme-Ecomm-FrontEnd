package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.Util.MailSenderUtil;
import com.ShopmeFrontEnd.entity.CheckoutInfo;
import com.ShopmeFrontEnd.entity.readonly.*;
import com.ShopmeFrontEnd.entity.readonly.order.Order;
import com.ShopmeFrontEnd.entity.readonly.order.PaymentMethod;
import com.ShopmeFrontEnd.service.*;
import lombok.RequiredArgsConstructor;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CustomerServiceFrontEnd customerService;
    private final AddressService addressService;
    private final ShippingRateService rateService;
    private final ShoppingCartService cartService;
    private final OrderService orderService;
    private final SettingService settingService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate;
        boolean usePrimaryAddressAsDefault = false;
        if (defaultAddress != null){
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = rateService.getShippingRateForAddress(defaultAddress);
        }else {
            model.addAttribute("shippingAddress", customer.toString());
            usePrimaryAddressAsDefault = true;
            shippingRate = rateService.getShippingRateForCustomer(customer);
        }

        if(shippingRate == null) {
            return "redirect:/cart";
        }
        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);
        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod"); // Fetched from checkout.html name = "paymentMethod" and its value is COD
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate;
        boolean usePrimaryAddressAsDefault = false;
        if (defaultAddress != null){
            shippingRate = rateService.getShippingRateForAddress(defaultAddress);
        }else {
            usePrimaryAddressAsDefault = true;
            shippingRate = rateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        // Placing Order and removing the items from the Cart
        Order placedOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        cartService.deleteByCustomer(customer);

        sendOrderConfirmationEmail(request, placedOrder);
        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSetting();
        JavaMailSenderImpl mailSender = MailSenderUtil.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8"); // For currency Symbol
        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");

        String orderTime = dateFormat.format(order.getOrderTime());
        String totalAmount = String.valueOf(order.getTotal());

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getShippingAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);

        mailSender.send(message);

    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);
        return customerService.getCustomerByEmail(email);
    }
}
