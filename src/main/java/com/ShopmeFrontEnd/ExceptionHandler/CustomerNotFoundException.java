package com.ShopmeFrontEnd.ExceptionHandler;

public class CustomerNotFoundException extends Exception{
    public CustomerNotFoundException() {
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
