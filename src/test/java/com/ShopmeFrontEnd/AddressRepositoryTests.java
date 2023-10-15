package com.ShopmeFrontEnd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.ShopmeFrontEnd.dao.AddressRepo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepo repo;

    @Test
    public void testAddNew() {
        Integer customerId = 6;
        Integer countryId = 1; // India

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("Sudhanshu");
        newAddress.setLastName("Arya");
        newAddress.setPhoneNumber("7468461553");
        newAddress.setAddressLine1("Panipat");
        newAddress.setCity("Panipat");
        newAddress.setState("Haryana");
        newAddress.setPostalCode("132106");

        Address savedAddress = repo.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 6;
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 6;

        Address address = repo.findByIdAndCustomer(addressId, customerId);

        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Integer addressId = 1;
        String phoneNumber = "646-232-3932";

        Address address = repo.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = repo.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        repo.deleteByIdAndCustomer(addressId, customerId);

        Address address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 1;
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses() {
        Integer addressId = 1;
        Integer customerId = 6;
        repo.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    public void testGetDefault() {
        Integer customerId = 5;
        Address address = repo.findDefaultByCustomer(customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }
}