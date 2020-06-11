package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

@Configurable(value="deviceNameFromPointIdTagPrototype", autowire=Autowire.BY_NAME)
public class DeviceNameFromPointIdTag extends YukonTagSupport {
    
    private Integer pointId;
    private String var = null;
    
    @Autowired private PointDao pointDao;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        if (pointId == null) throw new JspException("pointId must be set");
        
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject yukonPao = cache.getAllPaosMap().get(point.getPaobjectID());
        if (var == null) {
            final JspWriter writer = getJspContext().getOut();
            writer.print(StringEscapeUtils.escapeHtml4(yukonPao.getPaoName()));
        } else {
            getJspContext().setAttribute(var, StringEscapeUtils.escapeHtml4(yukonPao.getPaoName()));
        }
    }
    
    public void setPointId(final int pointId) {
        this.pointId = pointId;
    }

    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
}