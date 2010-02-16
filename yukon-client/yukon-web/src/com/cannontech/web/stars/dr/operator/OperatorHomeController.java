package com.cannontech.web.stars.dr.operator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;

@Controller
public class OperatorHomeController {

	// HOME
	@RequestMapping(value = "/operator/home")
    public String home(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		return "operator/home.jsp";
	}
}
