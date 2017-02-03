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
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.v3.MultispeakCustomerInfoService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.accountInformation.MspAccountInformationHandler;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/accountInformationWidget/*")
@CheckRole(YukonRole.METERING)
public class AccountInformationWidget extends WidgetControllerBase{
    
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MeterDao meterDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MspAccountInformationHandler mspAccountInfo;
    
    
   @Autowired
    public AccountInformationWidget(@Qualifier("widgetInput.deviceId") 
            SimpleWidgetInput simpleWidgetInput) {
  
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
    }

    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("accountInformationWidget/accountInfo.jsp");
        int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        if (!isConfigured()) {
            mav.addObject("hasVendorId", false);
            return mav;
        }
        mav.addObject("hasVendorId", true);

        YukonMeter meter = meterDao.getForId(deviceId);
        MultispeakVendor mspPrimaryCISVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        
        if (mspPrimaryCISVendor.getVendorID() > 0) {
            return mspAccountInfo.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
        }
        return mav;
    }
    

    /**
     * Returns true if the system is configured for MultiSpeak Account Information AND has a primary CIS defined.
     */
    private boolean isConfigured() {
        CisDetailRolePropertyEnum cisDetail = globalSettingDao.getEnum(GlobalSettingType.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class);
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        
        return (cisDetail == CisDetailRolePropertyEnum.MULTISPEAK && vendorId > MultispeakVendor.CANNON_MSP_VENDORID);
    }
}
