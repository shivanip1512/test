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
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class AttributeReadingWidgetHelper {
    
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;

    public ModelAndView initiateRead(HttpServletRequest request,
                             Meter meter,
                             Set<Attribute> toRead, 
                             String view, 
                             DeviceRequestType requestType) 
    throws ServletRequestBindingException {
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        ModelAndView mav = new ModelAndView(view);
        
        CollectingDeviceAttributeReadCallback callback = new CollectingDeviceAttributeReadCallback();
        Set<Meter> meterSingleton = Collections.singleton(meter);
        deviceAttributeReadService.initiateRead(meterSingleton, toRead, callback, requestType, user);
        callback.waitForCompletion();
        
        mav.addObject("result", callback);
        
        boolean readable = deviceAttributeReadService.isReadable(meterSingleton, toRead, user);
        mav.addObject("readable", readable);
        mav.addObject("meter", meter);
        
        return mav;
    }
    
    public void initiateRead(HttpServletRequest request,
                                     Meter meter,
                                     Set<Attribute> toRead, 
                                     ModelMap model, 
                                     DeviceRequestType requestType) 
    throws ServletRequestBindingException {
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        CollectingDeviceAttributeReadCallback callback = new CollectingDeviceAttributeReadCallback();
        Set<Meter> meterSingleton = Collections.singleton(meter);
        deviceAttributeReadService.initiateRead(meterSingleton, toRead, callback, requestType, user);
        callback.waitForCompletion();
        
        model.addAttribute("result", callback);
        
        boolean readable = deviceAttributeReadService.isReadable(meterSingleton, toRead, user);
        model.addAttribute("readable", readable);
        model.addAttribute("meter", meter);
    }
    
    public Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
}