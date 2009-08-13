package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.model.Address;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.CayentaApiService;
import com.cannontech.web.cayenta.util.CayentaMeterNotFoundException;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED)
public class CayentaAccountInformationWidget extends WidgetControllerBase {
    
    private MeterDao meterDao;
    private CayentaApiService cayentaApiService;
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
    	ModelAndView mav = new ModelAndView("cayentaAccountInformationWidget/accountInfo.jsp");
        int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        
        Meter meter = meterDao.getForId(deviceId);
        String meterName = meter.getName();
        
        try {
	        CayentaLocationInfo locationInfo = cayentaApiService.getLocationInfoForMeterName(meterName);
	        CayentaMeterInfo meterInfo = cayentaApiService.getMeterInfoForMeterName(meterName);
	        CayentaPhoneInfo phoneInfo = cayentaApiService.getPhoneInfoForAccountNumber(meterInfo.getAccountNumber());
	        
	        Address address = new Address();
	        address.setLocationAddress1(meterInfo.getAddress());
	        address.setCityName(locationInfo.getLocationCity());
	        address.setStateCode(locationInfo.getLocationState());
	        address.setZipCode(locationInfo.getLocationZipCode());
	        
	        mav.addObject("locationInfo", locationInfo);
	        mav.addObject("address", address);
	        mav.addObject("meterInfo", meterInfo);
	        mav.addObject("phoneInfo", phoneInfo);
	        
        } catch (CayentaMeterNotFoundException e) {
        	mav.addObject("msg", "Meter not found.");
        }
        
        return mav;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
    
    @Autowired
    public void setCayentaApiService(CayentaApiService cayentaApiService) {
		this.cayentaApiService = cayentaApiService;
	}

}
