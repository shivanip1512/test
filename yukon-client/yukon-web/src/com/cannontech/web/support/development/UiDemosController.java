package com.cannontech.web.support.development;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.security.annotation.CheckDevelopmentMode;

@Controller
@RequestMapping("/development/uiDemos/*")
@CheckDevelopmentMode
public class UiDemosController {
    @RequestMapping("main")
    public void main(ModelMap model) {
        model.addAttribute("numberNegativeFive", -5);
        model.addAttribute("numberZero", 0);
        model.addAttribute("numberOne", 1);
        model.addAttribute("numberTwo", 2);
        model.addAttribute("numberFive", 5);
        model.addAttribute("numberOneThird", 1.0 / 3.0);
        model.addAttribute("numberOneHalf", 1.0 / 2.0);
        model.addAttribute("numberTwoThirds", 2.0 / 3.0);
        model.addAttribute("numberFiveNinths", 5.0 / 9.0);
        model.addAttribute("largeDecimal", 9523155.235);
        model.addAttribute("funArguments", new Object[] {7, "Cool Beans", 13, 54.231555});
    }
}
