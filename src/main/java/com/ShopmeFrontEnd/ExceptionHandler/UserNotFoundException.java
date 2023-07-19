package com.ShopmeFrontEnd.ExceptionHandler;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message) {
        super(message);
    }
}
