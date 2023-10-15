package com.ShopmeFrontEnd.controller.restController;

import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestControllerFrontEnd {

    private final CustomerServiceFrontEnd service;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(@Param("email") String email) {
        return service.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
