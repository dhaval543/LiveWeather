package com.anytime.weather.controller.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class CoreControllerAdvice {


    @ExceptionHandler(value = { Exception.class })
    @ResponseBody
    public ResponseEntity handleException(Exception ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body("handle execpetion, display generic error page");
    }

    @ExceptionHandler(value = { TimeoutException.class })
    @ResponseBody
    public ResponseEntity handleTimeoutException(Exception ex) {

       return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("handle TimeoutException, display timeout generic error page");
    }
}
