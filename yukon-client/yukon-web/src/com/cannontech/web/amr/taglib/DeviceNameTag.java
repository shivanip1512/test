package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable(value="deviceNameTagPrototype", autowire = Autowire.BY_NAME)
public class DeviceNameTag extends YukonTagSupport {
    
	private String var = null;
    private DeviceDao deviceDao;
    private PaoLoadingService paoLoadingService = null;
    private int deviceId = 0;
    private boolean deviceIdSet = false;
    private SimpleDevice device = null;
    
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
        	out.print("<span class=\"deviceNameTagSpan\" title=\"deviceId: " + deviceId + "\">");
            out.print(formattedName);
            out.print("</span>");
        } else {
        	jspContext.setAttribute(var, formattedName);
        }
    }
    
    
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
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
}
