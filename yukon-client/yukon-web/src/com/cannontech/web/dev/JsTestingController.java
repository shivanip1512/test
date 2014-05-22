package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JsTestingController {

    @RequestMapping("js-testing")
    public String jsTesting() {
        return "jsTesting.jsp";
    }
}
