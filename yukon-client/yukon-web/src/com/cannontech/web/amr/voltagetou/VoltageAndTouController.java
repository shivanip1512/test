package com.cannontech.web.amr.voltagetou;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTOUSchedule;

public class VoltageAndTouController extends MultiActionController {

    
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("voltageAndTou.jsp");
		
		int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
		mav.addObject("deviceId", deviceId);

		// Schedules
		List<LiteTOUSchedule> schedules = DefaultDatabaseCache.getInstance().getAllTOUSchedules();
		mav.addObject("schedules", schedules);

		return mav;
	}
}
