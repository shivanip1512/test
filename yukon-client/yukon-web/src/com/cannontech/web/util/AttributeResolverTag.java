package com.cannontech.web.util;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("attributeResolverTagPrototype")
public class AttributeResolverTag extends YukonTagSupport {
    private AttributeService attributeService;
    private DeviceDao deviceDao;
    
    // inputs
    private String var;
    private YukonDevice device = null;
    private Attribute attribute = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        Validate.notNull(attribute, "attribute or attributeName must be set");
        Validate.notNull(device, "device or deviceId must be set");
        LitePoint pointForAttribute = attributeService.getPointForAttribute(device, attribute);
        
        int pointId = pointForAttribute.getPointID();
        
        getJspContext().setAttribute(var, pointId);
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setDeviceDao(DeviceDao paoDao) {
        this.deviceDao = paoDao;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setDevice(YukonDevice device) {
        this.device = device;
    }
    
    public void setAttributeName(String attributeName) {
        this.attribute = attributeService.resolveAttributeName(attributeName);
    }
    
    public void setDeviceId(int deviceId) {
        this.device = deviceDao.getYukonDevice(deviceId);
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
