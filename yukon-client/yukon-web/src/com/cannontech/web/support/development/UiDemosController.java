package com.cannontech.web.support.development;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.taglib.MessageScopeHelper;

@Controller
@RequestMapping("/development/uiDemos/*")
@CheckDevelopmentMode
public class UiDemosController {
    public static class ScopePeeker {
        private HttpServletRequest request;

        private ScopePeeker(HttpServletRequest request) {
            this.request = request;
        }

        public String getScope() {
            return MessageScopeHelper.forRequest(request).toString();
        }
    }

    @RequestMapping("main")
    public void main(ModelMap model, HttpServletRequest request) {
        model.addAttribute("scopePeeker", new ScopePeeker(request));

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
