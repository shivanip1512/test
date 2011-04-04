package com.cannontech.web.amr.voltagetou;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.yukon.IDatabaseCache;

public class VoltageAndTouController extends MultiActionController {

    private IDatabaseCache databaseCache;
    private MeterDao meterDao;
    
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("voltageAndTou.jsp");
		int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
		Meter meter = meterDao.getForId(deviceId);
		mav.addObject("deviceId", deviceId);
		mav.addObject("deviceName", meter.getName());
		
		// Schedules
		List<LiteTOUSchedule> schedules = databaseCache.getAllTOUSchedules();
		mav.addObject("schedules", schedules);

		return mav;
	}
	
	@Required
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
	
	@Autowired
	public void setMeterDao(MeterDao meterDao) {
	    this.meterDao = meterDao;
	}
	
}
