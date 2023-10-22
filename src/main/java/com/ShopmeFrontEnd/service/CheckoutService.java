package com.ShopmeFrontEnd.service;


import java.util.List;

import javax.transaction.Transactional;

import com.ShopmeFrontEnd.entity.CheckoutInfo;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.ShippingRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
@Transactional
public class CheckoutService{



    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCost = (int) calculateShippingCost(cartItems, shippingRate, 138);
        int paymentTotal = (int)(productTotal + shippingCost);

        System.out.println("Payment : " + paymentTotal);
        System.out.println("Shipping : " + shippingCost);

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCost);
        checkoutInfo.setPaymentTotal(paymentTotal);

//        float productCost = CheckoutUtil.calculateProductCost(cartItems);
//        float productTotal = CheckoutUtil.calculateProductTotal(cartItems);
//        float shippingCostTotal = CheckoutUtil.calculateShippingCost(cartItems, shippingRate, DIM_DIVISOR);
//        float paymentTotal = productTotal + shippingCostTotal;
//
//        checkoutInfo.setProductCost(productCost);
//        checkoutInfo.setProductTotal(productTotal);
//        checkoutInfo.setShippingCostTotal(shippingCostTotal);
//        checkoutInfo.setPaymentTotal(paymentTotal);
//
//        checkoutInfo.setDeliverDays(shippingRate.getDays());
//        checkoutInfo.setCodSupported(shippingRate.isCodSupported());

        return checkoutInfo;
    }

    public static float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate, int division) {
        float shippingCostTotal = 0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / division;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();

            item.setShippingCost(shippingCost);

            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0;
        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }
        return cost;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubTotal();
        }
        System.out.println("::::::::::::::::::::::::::::::::: TOTAL ::::::::::::::::::::::::::::::::::::::");
        System.out.println(total);
        return total;
    }
}
