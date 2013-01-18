package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;

@Configurable("pointStatusColorTagPrototype")
public class PointStatusColorTag extends YukonTagSupport {
    
    private int pointId;
    private boolean isPointIdSet;
    private String var;
    private String format = "{stateColor|#%02X%02X%02X}";
    private String styleClass;
    private boolean background;
    
    private PointDataRegistrationService pointDataRegistrationService;
    
    @Override
    public void doTag() throws JspException ,IOException {
        
        if (!isPointIdSet) throw new JspException("pointId must be set");
        
        final UpdateValue latestValue = pointDataRegistrationService.getLatestValue(pointId, format, getUserContext());
        final String color = latestValue.isUnavailable() ? "black" :  latestValue.getValue();
        
        if (!StringUtils.isBlank(var)) {
            
            getJspContext().setAttribute(var, color);
            
        } else {
            
            final StringBuilder beforeBodyBuilder = new StringBuilder();
            String style = background ? " style=\"background-color: " + color + " !important;\"" : " style=\"color: " + color + " !important;\"";
            beforeBodyBuilder.append("<span" + style);
            String format = background ? "background" : "format";
            beforeBodyBuilder.append(" data-format=\"" + format + "\"");
            if (!StringUtils.isBlank(styleClass)) {
                beforeBodyBuilder.append(" class=\"" + styleClass + "\"");
            }
            beforeBodyBuilder.append(" cannonColorUpdater=\"" + latestValue.getFullIdentifier() + "\">");
            String before = beforeBodyBuilder.toString();
            
            final StringBuilder afterBodyBuilder = new StringBuilder();
            afterBodyBuilder.append("</span>");
            String after = afterBodyBuilder.toString();
            
            final JspWriter writer = getJspContext().getOut();
            writer.print(before);
            getJspBody().invoke(writer);
            writer.print(after);
            
        }
    }

    public void setPointId(final int pointId) {
        this.isPointIdSet = true;
        this.pointId = pointId;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public void setBackground(boolean background) {
        this.background = background;
    }

    @Required
    public void setPointDataRegistrationService(PointDataRegistrationService pointDataRegistrationService) {
        this.pointDataRegistrationService = pointDataRegistrationService;
    }
    
}