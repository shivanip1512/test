package com.cannontech.web.support.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.security.annotation.CheckDevelopmentMode;

@Controller
@RequestMapping("/development/pickerTest/*")
@CheckDevelopmentMode
public class PickerTestController {
    @RequestMapping("main")
    public void main() {
    }
}
