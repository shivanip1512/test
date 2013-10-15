package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.CollectingDeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class AttributeReadingWidgetHelper {

    @Autowired private DeviceDao deviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;

    public ModelAndView initiateRead(HttpServletRequest request,
                             YukonDevice device,
                             Set<Attribute> toRead, 
                             String view, 
                             DeviceRequestType requestType) 
    throws ServletRequestBindingException {
        ModelAndView mav = new ModelAndView(view);
        initiateRead(request, device, toRead, mav.getModelMap(), requestType);
        return mav;
    }
    
    public void initiateRead(HttpServletRequest request,
                                     YukonDevice device,
                                     Set<Attribute> toRead, 
                                     ModelMap model, 
                                     DeviceRequestType requestType) 
    throws ServletRequestBindingException {
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        CollectingDeviceAttributeReadCallback callback = new CollectingDeviceAttributeReadCallback();
        Set<YukonDevice> meterSingleton = Collections.singleton(device);
        deviceAttributeReadService.initiateRead(meterSingleton, toRead, callback, requestType, user);
        callback.waitForCompletion();
        
        model.addAttribute("result", callback);
        
        boolean readable = deviceAttributeReadService.isReadable(meterSingleton, toRead, user);
        model.addAttribute("readable", readable);
    }
    
    /**
     * Use this method only when you MUST have a YukonMeter object, else use getDevice(request).
     */
    public YukonMeter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonMeter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    public YukonDevice getDevice(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        return device;
    }
}