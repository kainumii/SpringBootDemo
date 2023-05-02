package com.myswcompany.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HomeController {
    @GetMapping("/greetings")
    public ResponseEntity<String> getGreetings()
    {
        String moi = "Moi";
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(moi);
    }
}
