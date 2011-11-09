package com.cannontech.web.support.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.security.annotation.CheckDevelopmentMode;

@Controller
@RequestMapping("/development/uiDemos/*")
@CheckDevelopmentMode
public class UiDemosController {
    @RequestMapping("main")
    public void main() {
    }
}
