package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = service.listAllCountries();

        model.addAttribute("listCountries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model){
        service.registerCustomer(customer);

        model.addAttribute("pageTitle", "Registration Succeeded");

        return "/register/register_success";
    }
}
