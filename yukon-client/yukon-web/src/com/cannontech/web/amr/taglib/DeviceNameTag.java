package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable(value="deviceNameTagPrototype", autowire=Autowire.BY_NAME)
public class DeviceNameTag extends YukonTagSupport {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    
    private String var = null;
    private int deviceId = 0;
    private boolean deviceIdSet = false;
    private SimpleDevice device = null;
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        deviceIdSet = true;
        this.deviceId = deviceId;
    }
    
    public SimpleDevice getDevice() {
        return device;
    }
    
    public void setDevice(SimpleDevice device) {
        this.device = device;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        
        if (deviceIdSet && device != null) {
            throw new JspException("deviceId and device should not both be set");
        }
        
        if (!deviceIdSet && device == null) {
            throw new JspException("either deviceId or device must be set");
        }
        
        String formattedName = null;
        try{
        
            if (deviceIdSet) {
                formattedName = deviceDao.getFormattedName(getDeviceId());
            } else {
                formattedName = paoLoadingService.getDisplayablePao(device).getName();
                deviceId = device.getDeviceId();
            }
            
        } catch (NotFoundException e) {
            throw new JspException("deviceId: " + deviceId + " is not a valid deviceId", e);
        }
        
        JspContext jspContext = getJspContext();
        if (var == null) {
            JspWriter out = jspContext.getOut();
            out.print(StringEscapeUtils.escapeHtml4(formattedName));
        } else {
            jspContext.setAttribute(var, formattedName);
        }
    }
    
}