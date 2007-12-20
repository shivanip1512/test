package com.cannontech.web.amr.profile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Spring controller class for profile
 */
public class ProfileController extends MultiActionController {

	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("profile.jsp");

		int deviceId = ServletRequestUtils.getRequiredIntParameter(request,
				"deviceId");
		mav.addObject("deviceId", deviceId);

		return mav;
	}

}
