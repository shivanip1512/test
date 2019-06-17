package com.cannontech.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dr/setup/loadGroup")
public class ApiTestConnectionController {

    @GetMapping("/test")
    public ResponseEntity<Object> testConnection() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
