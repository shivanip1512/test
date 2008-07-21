package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("deviceNameTagPrototype")
public class DeviceNameTag extends YukonTagSupport {
    private MeterDao meterDao;
    private PaoDao paoDao = null;
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
        if (deviceIdSet) {
            formattedName = getDeviceName(deviceId);
            
        } else {
            formattedName = getDeviceName(device.getDeviceId());
        }

        JspWriter out = getJspContext().getOut();
        out.print("<span class=\"deviceNameTagSpan\" title=\"deviceId: " + deviceId + "\">");
        out.print(formattedName);
        out.print("</span>");
    }
    
    private String getDeviceName(int deviceId) throws JspException{
        
        String formattedName = null;
        
        try {
            Meter meter = meterDao.getForId(deviceId);
            device = meter;
            formattedName = meterDao.getFormattedDeviceName(meter);
        } catch (NotFoundException e) {
            // device is not a meter
            try {
                LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
                formattedName = pao.getPaoName();
            } catch (NotFoundException e1) {
                throw new JspException("deviceId: " + deviceId + " is not a valid deviceId", e1);
            }
        }
        
        return formattedName;
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
    
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
