package com.ShopmeFrontEnd.controller.restController;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.ExceptionHandler.ShoppingCartException;
import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ShoppingCartRestController {
    private final ShoppingCartService cartService;
    private final CustomerServiceFrontEnd customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {

        // This String message will be response which is shown by javascript code
        try {
            Customer customer = getAuthenticatedCustomer(request);

            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product are added to your shopping cart";
        } catch (CustomerNotFoundException e) {
            return "You must login to add this product to cart";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);

        if (email == null) { // If user tries to add to cart without log-in
            throw new CustomerNotFoundException("No Authenticated Customer");
        }

        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable(name = "productId") Integer productId,
                                 @PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {

        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subTotal = cartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subTotal);

        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of product.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);

            return "The Product has been removed from your Shopping Cart";

        } catch (CustomerNotFoundException e) {
            return "You must login to remove product.";
        }
    }
}
