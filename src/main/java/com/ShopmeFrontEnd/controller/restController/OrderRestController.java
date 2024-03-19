package com.ShopmeFrontEnd.controller.restController;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.ExceptionHandler.OrderNotFoundException;
import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.dto.OrderReturnResponse;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.dto.OrderReturnRequest;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class OrderRestController {

    private final CustomerServiceFrontEnd customerService;
    private final OrderService orderService;

    @PostMapping("/order/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest servletRequest) {

        System.out.println("Order Id :: " + returnRequest.getOrderId());
        System.out.println("Reason :: " + returnRequest.getOrderId());
        System.out.println("Note :: " + returnRequest.getNote());
        Customer customer;
        try {
            customer = getAuthenticatedCustomer(servletRequest);
        }catch (CustomerNotFoundException e){
            return new ResponseEntity<>("Authentication Required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);

        if (email == null) { // If user tries to add to cart without log-in
            throw new CustomerNotFoundException("No Authenticated Customer");
        }

        return customerService.getCustomerByEmail(email);
    }
}
