package com.cannontech.web.stars.wifi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WifiConnectionController {
        
    @PostMapping("/wifiConnection/refresh/{deviceId}")
    public void refreshConnection(@PathVariable int deviceId) {
        //TODO: Call code to refresh connection
    }
    
}