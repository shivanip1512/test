package com.cannontech.web.amr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable(value="deviceGroupNameTagPrototype", autowire=Autowire.BY_NAME)
public class DeviceGroupNameTag extends YukonTagSupport {
    
    @Autowired private DeviceGroupService deviceGroupService;
    
    private String var = null;
    private String deviceGroupFullName;
    
    public String getDeviceGroupFullName() {
        return deviceGroupFullName;
    }

    public void setDeviceGroupFullName(String deviceGroupFullName) {
        this.deviceGroupFullName = deviceGroupFullName;
    }

    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        
        
        if (deviceGroupFullName == null) {
            throw new JspException("deviceGroupFullName must be set");
        }
        
        String formattedName = null;
        try{
            
            DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroupFullName);
            formattedName = group.getName();
            
        } catch (NotFoundException e) {
            throw new JspException("group: " + deviceGroupFullName + " is not a valid group", e);
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