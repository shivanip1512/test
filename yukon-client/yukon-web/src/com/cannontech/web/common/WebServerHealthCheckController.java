package com.cannontech.web.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebServerHealthCheckController {
    @GetMapping("/runningStatus")
    public @ResponseBody String runningStatus() {
        return "Success";
    }
}
