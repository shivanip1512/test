package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("deviceNameTagPrototype")
public class DeviceNameTag extends YukonTagSupport {
    
    private DeviceDao deviceDao; 
    private int deviceId = 0;
    private boolean deviceIdSet = false;
    private YukonDevice device = null;
    
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
                formattedName = deviceDao.getFormattedName(getDevice());
                deviceId = device.getDeviceId();
            }
            
        } catch (NotFoundException e) {
            throw new JspException("deviceId: " + deviceId + " is not a valid deviceId", e);
        }

        JspWriter out = getJspContext().getOut();
        out.print("<span class=\"deviceNameTagSpan\" title=\"deviceId: " + deviceId + "\">");
        out.print(formattedName);
        out.print("</span>");
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        deviceIdSet = true;
        this.deviceId = deviceId;
    }
    public YukonDevice getDevice() {
        return device;
    }
    public void setDevice(YukonDevice device) {
        this.device = device;
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
