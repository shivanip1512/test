package com.cannontech.web.bulk;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/progressReport/*")
public class ProgressReportController {


    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public void detail(ModelMap model) {

    }
}