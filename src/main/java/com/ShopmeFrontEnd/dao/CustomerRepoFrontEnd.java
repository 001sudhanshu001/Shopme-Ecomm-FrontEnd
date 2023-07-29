package com.ShopmeFrontEnd.dao;


import com.ShopmeFrontEnd.entity.readonly.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface CustomerRepoFrontEnd extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);

//    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
//    @Modifying
//    void updateAuthenticationType(Integer customerId, AuthenticationType type);
//

}
