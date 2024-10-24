package com.example.apigatewayservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class gateController {

    @Value("${domain}")
    private String domain;

    @GetMapping("/gateway/test")
    public ResponseEntity<String> ok(){
        return ResponseEntity.ok(domain);
    }
}
