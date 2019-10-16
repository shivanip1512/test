package com.cannontech.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTestConnectionController {

    @GetMapping("/testyukon")
    public ResponseEntity<Object> testConnection() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
