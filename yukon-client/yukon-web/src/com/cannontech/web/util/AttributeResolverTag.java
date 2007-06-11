package com.cannontech.web.util;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("attributeResolverTagPrototype")
public class AttributeResolverTag extends YukonTagSupport {
    private AttributeService attributeService;
    private PaoDao paoDao;
    
    // inputs
    private String attribute;
    private int deviceId;
    private String var;
    
    @Override
    public void doTag() throws JspException, IOException {
        Attribute attr = attributeService.resolveAttributeName(attribute);

        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(deviceId);
        
        LitePoint pointForAttribute = attributeService.getPointForAttribute(liteYukonPAO, attr);
        
        int pointId = pointForAttribute.getPointID();
        
        getJspContext().setAttribute(var, pointId);
    }
    
    
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
