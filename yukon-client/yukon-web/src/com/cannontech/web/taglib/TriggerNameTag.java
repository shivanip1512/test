package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.dr.setup.ControlAreaTriggerType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

@Configurable(value="triggerNameTagPrototype", autowire=Autowire.BY_NAME)
public class TriggerNameTag extends YukonTagSupport {
    
    private Integer pointId;
    private ControlAreaTriggerType type;
    private String var = null;
    
    @Autowired private PointDao pointDao;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        if (pointId == null) throw new JspException("pointId must be set");
        
        LitePoint p = pointDao.getLitePoint(pointId);
        LiteYukonPAObject yukonPao = cache.getAllPaosMap().get(p.getPaobjectID());
        
        MessageSourceAccessor messageAccessor = this.getMessageSource();
        String formattedType = messageAccessor.getMessage(type.getFormatKey());
        String formattedName = formattedType + " (" + yukonPao.getPaoName() + " / " + p.getPointName() + ")";

        if (var == null) {
            final JspWriter writer = getJspContext().getOut();
            writer.print(StringEscapeUtils.escapeHtml4(formattedName));
        } else {
            getJspContext().setAttribute(var, formattedName);
        }

    }
    
    public void setPointId(final int pointId) {
        this.pointId = pointId;
    }

    public void setType(ControlAreaTriggerType type) {
        this.type = type;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }

}
