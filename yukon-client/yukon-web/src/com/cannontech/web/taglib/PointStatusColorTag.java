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
    
    private PointDataRegistrationService registrationService;
    
    @Override
    public void doTag() throws JspException ,IOException {
        
        if (!isPointIdSet) throw new JspException("pointId must be set");
        
        final UpdateValue value = registrationService.getLatestValue(pointId, format, getUserContext());
        
        final String color;
        if (value.isUnavailable()) {
            color = background ? "rgb(255,255,255)" : "rgb(0,0,0)";
        } else {
            color = value.getValue();
        }
        
        if (!StringUtils.isBlank(var)) {
            
            getJspContext().setAttribute(var, color);
            
        } else {
            
            final StringBuilder beforeBodyBuilder = new StringBuilder();
            
            String format = background ? "background" : "format";
            String style = background ? " style=\"background-color: " + color + " \"" : " style=\"color: " + color + " !important;\"";
            
            beforeBodyBuilder.append("<span" + style);
            beforeBodyBuilder.append(" data-format=\"" + format + "\"");
            
            if (!StringUtils.isBlank(styleClass)) {
                beforeBodyBuilder.append(" class=\"" + styleClass + "\"");
            }
            
            beforeBodyBuilder.append(" cannonColorUpdater=\"" + value.getFullIdentifier() + "\">");
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
        this.registrationService = pointDataRegistrationService;
    }
    
}