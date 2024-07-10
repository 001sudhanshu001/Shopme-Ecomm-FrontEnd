package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.ExceptionHandler.OrderNotFoundException;
import com.ShopmeFrontEnd.dao.OrderRepo;
import com.ShopmeFrontEnd.dto.OrderReturnRequest;
import com.ShopmeFrontEnd.entity.CheckoutInfo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final int ORDERS_PER_PAGE = 5;

    private final OrderRepo orderRepo;
    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
                             PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = getOrder(customer, paymentMethod, checkoutInfo);

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

    private static Order getOrder(Customer customer, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
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
        return newOrder;
    }

    public Order getOrder(Integer id, Customer customer) {
        return orderRepo.findByIdAndCustomer(id, customer);
    }


    public Page<Order> listForCustomerByPage(Customer customer, int pageNum,
                                             String sortField, String sortDir, String orderKeyword) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (orderKeyword != null) {
            return orderRepo.findAll(orderKeyword, customer.getId(), pageable);
        }

        return orderRepo.findAll(customer.getId(), pageable);

    }

    public void setOrderReturnRequested(OrderReturnRequest returnRequested, Customer customer) throws OrderNotFoundException {
        Order order = orderRepo.findByIdAndCustomer(returnRequested.getOrderId(), customer);
        if(order == null) {
            throw  new OrderNotFoundException("Order Id " + returnRequested.getOrderId() + " not found");
        }

        if(order.isReturnRequested()) {
            return;
        }

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus((OrderStatus.RETURN_REQUESTED));

        String notes =  "Reason : " + returnRequested.getReason();
        if(! "".equals(returnRequested.getReason())) {
            notes += ". " + returnRequested.getNote();
        }

        track.setNotes(notes);

        order.getOrderTracks().add(track);

        order.setStatus(OrderStatus.RETURN_REQUESTED);

        orderRepo.save(order);
    }
}
