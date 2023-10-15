package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.dao.CountryRepoFrontEnd;
import com.ShopmeFrontEnd.dao.CustomerRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.AuthenticationType;
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
        customer.setAuthenticationType(AuthenticationType.DATABASE);

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

    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        // this null condition was the for already entered customer who don't have authType, now this is not required
        if(customer.getAuthenticationType() == null || !customer.getAuthenticationType().equals(type)){
            customerRepo.updateAuthenticationType(customer.getId(), type);
        }
    }

    public Customer getCustomerByEmail(String email){
        return customerRepo.findByEmail(email);
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
                                                AuthenticationType authenticationType) {
        // Here we have only customer name and email and country from request
        Customer customer = new Customer();
        customer.setEmail(email);

        setName(name, customer);

        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(authenticationType);
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

    public void update(Customer customerInForm) {
        Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

        // Password can be updated only if Authentication type is Database
        if(customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)){
            if (!customerInForm.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
                customerInForm.setPassword(encodedPassword);
            } else {
                customerInForm.setPassword(customerInDB.getPassword());
            }
        }else { // Because Password can't be null
            customerInForm.setPassword(customerInDB.getPassword());
        }

        // Since in the Editing form are not sending created time, enabled status, and verification code, resetPasswordToken
        // so if we update it then all these three values will be null.
        // so we have to add these also explicitly
        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());


        // This is to avoid glitch, when Updating Customer Profile if users does not select any State then
        // old state is saved otherwise null will be saved
        if(customerInForm.getState() == null){
            customerInForm.setState(customerInDB.getState());
        }
        if(customerInForm.getCountry() == null){
            customerInForm.setCountry(customerInDB.getCountry());
        }

        customerRepo.save(customerInForm);
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepo.findByEmail(email);
        if(customer != null){
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            //customerRepo.save(customer);
            // Instead of saving whole object again to DB we will use update Query for setting the resetPasswordToken
            customerRepo.updateResetPasswordTokenToGivenToken(customer.getId(), token);

            return token;
        }else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public Customer getByResetPasswordToken(String token){
        return customerRepo.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = customerRepo.findByResetPasswordToken(token);
        if(customer == null){
            throw new CustomerNotFoundException("No Customer found: invalid Token");
        }

        String updatedpassword = passwordEncoder.encode(newPassword);

        customerRepo.updatePassword(customer.getId(), updatedpassword);
        customerRepo.setResetPasswordTokenToNull(customer.getId());

    // Instead of saving whole object again to DB we will use update Query for setting the resetPasswordToken to nulll
    // customer.setResetPasswordToken(null);
    //   customerRepo.save(customer);
    }
}
