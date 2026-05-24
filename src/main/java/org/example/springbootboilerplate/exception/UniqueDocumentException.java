package org.example.springbootboilerplate.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueDocumentException extends RuntimeException{
    public UniqueDocumentException(String message) {
        super(message);
    }
}
