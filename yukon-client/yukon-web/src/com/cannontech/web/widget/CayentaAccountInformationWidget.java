package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.model.Address;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.CayentaApiService;
import com.cannontech.web.cayenta.util.CayentaMeterNotFoundException;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/cayentaAccountInformationWidget/*")
public class CayentaAccountInformationWidget extends WidgetControllerBase {
    
    @Autowired private CayentaApiService cayentaApiService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterDao meterDao;
    
    @Autowired
    public CayentaAccountInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput) {
        
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
    	ModelAndView mav = new ModelAndView("cayentaAccountInformationWidget/accountInfo.jsp");
        if (!isConfigured()) {
            mav.addObject("msg", "Cayenta not configured.");
            return mav;
        }

        int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        
        YukonMeter meter = meterDao.getForId(deviceId);
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
    
    /**
     * Returns true if the system is configured for Cayenta Account Information.
     */
    private boolean isConfigured() {
        CisDetailRolePropertyEnum cisDetail = globalSettingDao.getEnum(GlobalSettingType.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class);
        return (cisDetail == CisDetailRolePropertyEnum.CAYENTA);
    }
}
