package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;

public class DeviceNameTag extends SimpleTagSupport {
    private DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao", DeviceDao.class);
    private PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    private int deviceId = 0;
    private boolean deviceIdSet = false;
    private LiteYukonPAObject device = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (deviceIdSet && device != null) {
            throw new JspException("pointId and pointValue should not both be set");
        }
        
        if (!deviceIdSet && device == null) {
            throw new JspException("either pointId or pointValue must be set");
        }
        
        if (deviceIdSet) {
            device = paoDao.getLiteYukonPAO(deviceId);
        } else {
            deviceId = device.getLiteID();
        }
        
        String formattedName = deviceDao.getFormattedDeviceName(device);
        
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
    public LiteYukonPAObject getPointValue() {
        return device;
    }
    public void setPointValue(LiteYukonPAObject device) {
        this.device = device;
    }

}
