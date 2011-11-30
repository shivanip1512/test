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

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.yukon.IDatabaseCache;

public class VoltageAndTouController extends MultiActionController {

    private IDatabaseCache databaseCache;
    private DeviceDao deviceDao;
    private PaoLoadingService paoLoadingService;
    private PaoDefinitionDao paoDefinitionDao; 
    
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("voltageAndTou.jsp");
		int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
		SimpleDevice device = deviceDao.getYukonDevice(deviceId);
		boolean threePhaseVoltage = paoDefinitionDao.isTagSupported(device.getDeviceType(), 
                                                                    PaoTag.SUPPORTS_THREE_PHASE_VOLTAGE);
		boolean threePhaseCurrent = paoDefinitionDao.isTagSupported(device.getDeviceType(),
		                                                            PaoTag.SUPPORTS_THREE_PHASE_CURRENT);
		mav.addObject("deviceId", deviceId);
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());
		mav.addObject("threePhaseVoltage", threePhaseVoltage);
		mav.addObject("threePhaseCurrent", threePhaseCurrent);
		
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
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }

	@Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

	@Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}
