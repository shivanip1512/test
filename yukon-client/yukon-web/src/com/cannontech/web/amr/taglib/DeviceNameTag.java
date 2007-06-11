package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("deviceNameTagPrototype")
public class DeviceNameTag extends YukonTagSupport {
    private MeterDao meterDao;
    private int deviceId = 0;
    private boolean deviceIdSet = false;
    private Meter device = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (deviceIdSet && device != null) {
            throw new JspException("pointId and pointValue should not both be set");
        }
        
        if (!deviceIdSet && device == null) {
            throw new JspException("either pointId or pointValue must be set");
        }
        
        if (deviceIdSet) {
            device = meterDao.getForId(deviceId);
        } else {
            deviceId = device.getDeviceId();
        }
        
        String formattedName = meterDao.getFormattedDeviceName(device);
        
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
    public Meter getDevice() {
        return device;
    }
    public void setDevice(Meter device) {
        this.device = device;
    }
    
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

}
