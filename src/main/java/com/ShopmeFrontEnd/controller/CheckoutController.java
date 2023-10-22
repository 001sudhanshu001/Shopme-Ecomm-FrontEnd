package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.entity.CheckoutInfo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.ShippingRate;
import com.ShopmeFrontEnd.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CustomerServiceFrontEnd customerService;
    private final AddressService addressService;
    private final ShippingRateService rateService;
    private final ShoppingCartService cartService;

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

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);
        // Since only Logged-in customer can call "/cart" url(we configure in SecurityConfig ) So the email is always present

        return customerService.getCustomerByEmail(email);
    }
}
