package com.cannontech.web.support.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.security.annotation.CheckDevelopmentMode;

@Controller
@CheckDevelopmentMode
public class MiscellaneousMethodController {
    
    @RequestMapping("/development/miscellaneousMethod/main")
    public void main() {
    }
    
}
