package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.AppllicationConstants.AppConstants;
import com.ShopmeFrontEnd.ExceptionHandler.ShoppingCartException;
import com.ShopmeFrontEnd.dao.CartItemRepo;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    private final CartItemRepo cartRepo;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);

        if(cartItem != null){ // means item is already in the shopping cart
            updatedQuantity = cartItem.getQuantity() + quantity;

            if(updatedQuantity > AppConstants.maxLimitForCart){
                throw new ShoppingCartException("Could not add more " + quantity + " item(s) "
                                            + "because there's already " + cartItem.getQuantity() + " item(s) in your "
                                            + "shopping cart. Maximum allowed is " + AppConstants.maxLimitForCart);
            }

        } else { // Means this time product id added first time so updatedQuantity will be same ad quantity
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);

        cartRepo.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer){

        return cartRepo.findByCustomer(customer);
    }
}
