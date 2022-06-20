package com.cannontech.web.multispeak;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.METERING)
public class CISInformationController {
    @Autowired private MspHandler mspHandler;
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;

    @RequestMapping("viewCISDetails/{deviceId}")
    public ModelAndView viewCISDetails(HttpServletRequest request, ModelMap map, YukonUserContext context,
            @PathVariable int deviceId) {
        ModelAndView mav = new ModelAndView("setup/cisDetails.jsp");
        YukonMeter meter = meterDao.getForId(deviceId);
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        return mspHandler.getMspInformation(meter, mav, context);
    }

    @RequestMapping("viewCISDetailsV4/{deviceId}")
    public ModelAndView viewCISDetailsV4(HttpServletRequest request, ModelMap map, YukonUserContext context,
            @PathVariable int deviceId) {
        ModelAndView mav = new ModelAndView("setup/cisDetailsV4.jsp");
        YukonMeter meter = meterDao.getForId(deviceId);
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        return mspHandler.getMspInformation(meter, mav, context);
    }
}