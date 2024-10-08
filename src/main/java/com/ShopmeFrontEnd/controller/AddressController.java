package com.ShopmeFrontEnd.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.service.AddressService;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
public class AddressController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    private final CustomerServiceFrontEnd customerService;

//    private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
//
//    @Autowired
//    public AddressController(AddressService addressService,
//                             CustomerServiceFrontEnd customerService,
//                             AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
//        super();
//        this.addressService = addressService;
//        this.customerService = customerService;
//        this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
//    }

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request)
            throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);
        boolean usePrimaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

        return "address_book/addresses";
    }


    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra)
            throws CustomerNotFoundException {

        Customer customer = getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.save(address);
        ra.addFlashAttribute("message", "The address has been saved successfully.");

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("checkout".equals(redirectOption)) {
            redirectURL += "?redirect=checkout";
        }

        return redirectURL;
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId, Model model,
                              HttpServletRequest request) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);

        List<Country> listCountries = customerService.listAllCountries();

        Address address = addressService.get(addressId, customer.getId());
        if(address == null) {
            return "error/404";
        }

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");

        return "address_book/address_form";
    }


    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
                                HttpServletRequest request) throws CustomerNotFoundException {

        Customer customer = getAuthenticatedCustomer(request);

        // Checking if this Address belongs to this user
        Address address = addressService.get(addressId, customer.getId());
        if(address == null) {
            return "error/404";
        }

        addressService.delete(addressId, customer.getId());
        ra.addFlashAttribute("message",
                "The address ID " + addressId + " has been deleted.");

        return "redirect:/address_book";
    }


    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId,
                                    HttpServletRequest request) throws CustomerNotFoundException {

        Customer customer  = getAuthenticatedCustomer(request);

        addressService.setDefaultAddress(addressId, customer.getId());

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

//        if (redirectOption.equals("cart")) {
//            redirectURL = "redirect:/cart";
//        }else if ("checkout".equals(redirectOption)) {
//            redirectURL = "redirect:/checkout";
//        }

        if ("address_book".equals(redirectOption)) {
            redirectURL = "redirect:/address_book";
        }
//        else if(redirectOption.equals("cart")){
//            redirectURL = "redirect:/cart";
//        }

        return redirectURL;
    }


    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);
        return customerService.getCustomerByEmail(email);
    }
}
