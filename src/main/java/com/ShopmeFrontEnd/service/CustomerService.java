package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.dao.CountryRepoFrontEnd;
import com.ShopmeFrontEnd.dao.CustomerRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    // This is used to fetch all the countries from DB to show into Customer Registration Page
    private final CountryRepoFrontEnd countryRepo;
    private final CustomerRepoFrontEnd customerRepo;
    private final PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries(){
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);

        return customer == null;
    }

    public void registerCustomer(Customer customer){
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        customer.setEnabled(false); // customer will be enabled after verification
        customer.setCreatedTime(new Date());

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        customerRepo.save(customer);
    }

    public boolean verify(String verificationCode){
        Customer customer = customerRepo.findByVerificationCode(verificationCode);
        // Customer == null means wrong verification code and isEnabled means customer already exist
        if(customer == null || customer.isEnabled()){
            return false;
        }else {
            // we will set customer verification code to null
            customerRepo.enable(customer.getId());
            return true;
        }
    }
}
