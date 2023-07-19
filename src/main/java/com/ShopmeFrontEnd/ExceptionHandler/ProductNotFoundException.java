package com.ShopmeFrontEnd.ExceptionHandler;

public class ProductNotFoundException extends Exception{

    public ProductNotFoundException(String message) {
        super(message);
    }
}
