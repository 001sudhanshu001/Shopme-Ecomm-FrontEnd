package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.dao.CountryRepoFrontEnd;
import com.ShopmeFrontEnd.dao.CustomerRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    // This is used to fetch all the countries from DB to show into Customer Registration Page
    private final CountryRepoFrontEnd countryRepo;
    private final CustomerRepoFrontEnd customerRepo;

    public List<Country> listAllCountries(){
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);

        return customer == null;
    }
}
