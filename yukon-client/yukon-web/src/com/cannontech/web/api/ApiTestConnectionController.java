package com.cannontech.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTestConnectionController {

    @GetMapping("/yUk0n1ranD6_")
    public ResponseEntity<Object> testConnection() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
