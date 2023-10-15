package com.ShopmeFrontEnd.dao;


import com.ShopmeFrontEnd.entity.readonly.AuthenticationType;
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

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Integer customerId, AuthenticationType type);

    @Query("UPDATE Customer c SET c.resetPasswordToken = ?2 WHERE c.id = ?1")
    @Modifying
    void updateResetPasswordTokenToGivenToken(Integer customerId, String token);

    Customer findByResetPasswordToken(String resetPasswordToken);

    @Query("UPDATE Customer c SET c.resetPasswordToken = null WHERE c.id = ?1")
    @Modifying
    void setResetPasswordTokenToNull(Integer customerId);

    @Query("UPDATE Customer c SET c.password = ?2 WHERE c.id = ?1")
    @Modifying
    void updatePassword(Integer customerId, String password);


}
