package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.order.Order;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final CustomerServiceFrontEnd customerService;
    private final OrderService orderService;
    @GetMapping("/orders")
    public String listFirstPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listOrdersByPage(Model model, HttpServletRequest request,
                                   @PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortField")String sortField, @Param("sortDir")String sortDir, @Param("orderKeyword")String orderKeyword
    ) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, orderKeyword);

        System.out.println("PRINTING");
        List<Order> listOrders = page.getContent();
        for(Order o : listOrders) {
            System.out.println(o);
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("orderKeyword", orderKeyword);
        model.addAttribute("moduleURL", "/orders");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");


        long startCount = (long) (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;

        model.addAttribute("startCount", startCount);

        long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);

        return "orders/orders_customer";
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(Model model, @PathVariable("id") Integer id, HttpServletRequest request) throws CustomerNotFoundException {

        Customer customer = getAuthenticatedCustomer(request);
        Order order = orderService.getOrder(id, customer);
        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }


    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);
        return customerService.getCustomerByEmail(email);
    }

}
