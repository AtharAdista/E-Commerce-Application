package com.app.exceptions;

public class InvalidBankException extends RuntimeException{
    public InvalidBankException(String message) {
        super(message);
    }
}
