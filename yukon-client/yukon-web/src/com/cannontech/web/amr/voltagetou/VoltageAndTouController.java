package com.cannontech.web.amr.voltagetou;

import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.bean.YCBean;

/**
 * Spring controller class for voltage and tou
 */
public class VoltageAndTouController extends MultiActionController {

    private AttributeService attributeService = null;
    private MeterDao meterDao = null;
    private MeterReadService meterReadService = null;
    private DeviceDao deviceDao = null;
    
	public VoltageAndTouController() {
		super();
	}

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("voltageAndTou.jsp");

		int deviceId = ServletRequestUtils.getRequiredIntParameter(request,
				"deviceId");
		mav.addObject("deviceId", deviceId);

		// Get or create the YCBean and put it into the session
		YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
		if (ycBean == null) {
			ycBean = new YCBean();
		}
		request.getSession().setAttribute("YC_BEAN", ycBean);

		LiteYukonUser user = ServletUtil.getYukonUser(request);

		ycBean.setUserID(user.getUserID());
		ycBean.setDeviceID(deviceId);

		YukonDevice device = deviceDao.getYukonDevice(deviceId);
		mav.addObject("device", device);
		mav.addObject("voltageAttribute", BuiltInAttribute.VOLTAGE);
		mav.addObject("minimumVoltageAttribute", BuiltInAttribute.MINIMUM_VOLTAGE);
		mav.addObject("maximumVoltageAttribute", BuiltInAttribute.MAXIMUM_VOLTAGE);

		// Schedules
		List<LiteTOUSchedule> schedules = DefaultDatabaseCache.getInstance().getAllTOUSchedules();
		mav.addObject("schedules", schedules);

        // Checks to see if the meter is readable
	Meter meter = meterDao.getForId(deviceId);  
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(meter);
        boolean isReadable = meterReadService.isReadable(meter, existingAttributes, user);
        mav.addObject("isReadable", isReadable);

        String errorMsg = ServletRequestUtils.getStringParameter(request,
				"errorMsg");
		if (errorMsg == null && ycBean.getErrorMsg() != null) {
			errorMsg = ycBean.getErrorMsg();
			ycBean.setErrorMsg("");
		}

		mav.addObject("errorMsg", errorMsg);

		return mav;
	}

}
