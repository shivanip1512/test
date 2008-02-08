package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;

@Configurable("pointStatusColorTagPrototype")
public class PointStatusColorTag extends YukonTagSupport {
    private int pointId;
    private boolean isPointIdSet;
    private String format = "{stateColor|#%02X%02X%02X}";
    private PointDataRegistrationService pointDataRegistrationService;
    
    @Override
    public void doTag() throws JspException ,IOException {
        if (!isPointIdSet) throw new JspException("pointId must be set");
        
        final UpdateValue latestValue = pointDataRegistrationService.getLatestValue(pointId, format, getUserContext());
        final String color = latestValue.getValue();
        
        final StringBuilder beforeBodyBuilder = new StringBuilder();
        beforeBodyBuilder.append("<span id=\"cannonColorUpdater_" + pointId + "\" style=\"color: ");
        beforeBodyBuilder.append(color + " !important");
        beforeBodyBuilder.append(";\" cannonColorUpdater=\"" + latestValue.getFullIdentifier() + "\">");
        String before = beforeBodyBuilder.toString(); 
        
        final StringBuilder afterBodyBuilder = new StringBuilder();
        afterBodyBuilder.append("</span>\n");
        afterBodyBuilder.append("<script type=\"text/javascript\" language=\"JavaScript\">\n");
        afterBodyBuilder.append("   $('cannonColorUpdater_" + pointId + "').childElements().each(function(child) {\n");
        afterBodyBuilder.append("       child.style.color = '" + color + "';\n");
        afterBodyBuilder.append("   });\n");
        afterBodyBuilder.append("</script>\n");
        String after = afterBodyBuilder.toString();
        
        final JspWriter writer = getJspContext().getOut();
        writer.print(before);
        getJspBody().invoke(writer);
        writer.print(after);
    }

    public void setPointId(final int pointId) {
        this.isPointIdSet = true;
        this.pointId = pointId;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }

    @Required
    public void setPointDataRegistrationService(PointDataRegistrationService pointDataRegistrationService) {
        this.pointDataRegistrationService = pointDataRegistrationService;
    }
    
}
