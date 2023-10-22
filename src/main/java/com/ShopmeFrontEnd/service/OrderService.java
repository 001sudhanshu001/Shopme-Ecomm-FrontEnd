package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.dao.OrderRepo;
import com.ShopmeFrontEnd.entity.CheckoutInfo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.order.Order;
import com.ShopmeFrontEnd.entity.readonly.order.OrderDetail;
import com.ShopmeFrontEnd.entity.readonly.order.OrderStatus;
import com.ShopmeFrontEnd.entity.readonly.order.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
                             PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliveryDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliveryDate(checkoutInfo.getDeliverDate());

        if(address == null) {
            newOrder.copyAddressFromCustomer();
        }else {
            newOrder.copyShippingAddressFromShipping(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        for(CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost()* cartItem.getQuantity());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        }


        return orderRepo.save(newOrder);


    }
}
