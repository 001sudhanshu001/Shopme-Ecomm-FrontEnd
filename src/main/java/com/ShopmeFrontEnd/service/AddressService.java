package com.ShopmeFrontEnd.service;


import java.util.List;

import javax.transaction.Transactional;

import com.ShopmeFrontEnd.dao.AddressRepo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepo repo;

    public List<Address> listAddressBook(Customer customer) {
        return repo.findByCustomer(customer);
    }

    public void save(Address address) {
        repo.save(address);
    }

    public Address get(Integer addressId, Integer customerId) {
        return repo.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Integer addressId, Integer customerId) {
        repo.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        // 0 is for the main address of the customer
        if (defaultAddressId > 0) {
            repo.setDefaultAddress(defaultAddressId);
        }
        // removing the previously set default
        repo.setNonDefaultForOthers(defaultAddressId, customerId);
    }

    public Address getDefaultAddress(Customer customer) {
        return repo.findDefaultByCustomer(customer.getId());
    }

}
