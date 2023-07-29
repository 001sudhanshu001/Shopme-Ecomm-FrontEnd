package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.customerAuthentication.AuthenticationType;
import com.ShopmeFrontEnd.dao.CountryRepoFrontEnd;
import com.ShopmeFrontEnd.dao.CustomerRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceFrontEnd {
    // This is used to fetch all the countries from DB to show into Customer Registration Page
    private final CountryRepoFrontEnd countryRepo;
    @Autowired
    private CustomerRepoFrontEnd customerRepo;
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
//        customer.setAuthenticationType(AuthenticationType.DATABASE);

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
            // we will set customer enable = true and verification code to null, so that this code don't create conflict
            customerRepo.enable(customer.getId());
            return true;
        }
    }

//    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
//        if(!customer.getAuthenticationType().equals(type)){
//            customerRepo.updateAuthenticationType(customer.getId(), type);
//        }
//    }

    public Customer getCustomerByEmail(String email){
        return customerRepo.findByEmail(email);
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode) {
        // Here we have only customer name and email and country from request
        Customer customer = new Customer();
        customer.setEmail(email);

        setName(name, customer);

        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        // customer.setAuthenticationType(AuthenticationType.GOOGLE);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepo.findByCode(countryCode));

        customerRepo.save(customer);
    }

    private void setName(String name, Customer customer){
        String[] nameArray = name.split(" ");
        if(nameArray.length < 2){
            customer.setFirstName(name);
            customer.setLastName(""); // we set it empty because it can't be null
        }else{
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName, "");
            customer.setLastName(lastName);
        }
    }

}
