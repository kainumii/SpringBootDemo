package com.myswcompany.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class ContentNotAllowedException extends Exception {

    private static final long serialVersionUID = 1L;
    public ContentNotAllowedException(String msg) {
        super(msg);
    }
}
