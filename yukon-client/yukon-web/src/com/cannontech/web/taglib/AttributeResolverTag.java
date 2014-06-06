package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LitePoint;

@Configurable(value="attributeResolverTagPrototype", autowire=Autowire.BY_NAME)
public class AttributeResolverTag extends YukonTagSupport {
    
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDao paoDao;
    
    private String var;
    private YukonPao pao = null;
    private Attribute attribute = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        Validate.notNull(attribute, "attribute or attributeName must be set");
        Validate.notNull(pao, "pao or paoId must be set");
        
        int pointId = 0;
        boolean pointExists = attributeService.pointExistsForAttribute(pao, attribute);
        if (pointExists) {
            LitePoint pointForAttribute = attributeService.getPointForAttribute(pao, attribute);
            pointId = pointForAttribute.getPointID();
        }
        
        getJspContext().setAttribute(var, pointId);
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setPao(YukonPao pao) {
        this.pao = pao;
    }
    
    public void setAttributeName(String attributeName) {
        this.attribute = attributeService.resolveAttributeName(attributeName);
    }
    
    public void setPaoId(int paoId) {
        this.pao = paoDao.getYukonPao(paoId);
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
}