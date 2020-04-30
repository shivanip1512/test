package com.cannontech.web.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PingUrlController {
    @GetMapping("/ping")
    public @ResponseBody String ping() {
        return "Success";
    }
}
