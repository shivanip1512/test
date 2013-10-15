package com.cannontech.web.updater.point;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.updater.UpdateValue;

@Configurable(value="pointValueTagPrototype", autowire=Autowire.BY_NAME)
public class PointValueTag extends YukonTagSupport {
    
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private PointDao pointDao;

    private String format = Format.FULL.toString();
    private int pointId = 0;
    private boolean pointIdSet;
    private String unavailableValue = null;
    private boolean colorForStatus;
    private String cssClass = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!pointIdSet) {
            throw new JspException("pointId must be set");
        }
        
        UpdateValue value = registrationService.getLatestValue(pointId, format, getUserContext());
        
        String outputText;
        if (value.isUnavailable()) {
        	if (unavailableValue != null) {
        		outputText = unavailableValue;
        	} else {
        		outputText = getMessageSource().getMessage("yukon.common.point.pointFormatting.unavailablePlaceholder");
        	}
        } else {
        	outputText = value.getValue();
        }
        
        JspWriter out = getJspContext().getOut();
        
        PointType type = pointDao.getPaoPointIdentifier(pointId).getPointIdentifier().getPointType();
        boolean useColor = colorForStatus && type.isStatus();
        if (useColor) {
            final UpdateValue latestValue = registrationService.getLatestValue(pointId, "{stateColor|#%02X%02X%02X}", getUserContext());
            final String color = latestValue.isUnavailable() ? "black" :  latestValue.getValue();
            out.print("<span style=\"color: " + color + " !important;\" data-color-updater=\"" + latestValue.getFullIdentifier() + "\">");
        }
        
        out.print("<span data-updater=\"" + value.getFullIdentifier() + "\" class=\"pointValueTagSpan " + cssClass +"\" >");
        out.print(outputText);
        out.print("</span>");
        if (useColor) {
            out.print("</span>");
        }
    }

    public void setPointId(int pointId) {
        pointIdSet = true;
        this.pointId = pointId;
    }

    /**
     * @param format either a Format enum value or a string compatible with the PointFormattingService
     */
    public void setFormat(String format) {
        this.format = format;
    }

	public void setUnavailableValue(String unavailableValue) {
		this.unavailableValue = unavailableValue;
	}

	public void setColorForStatus(boolean colorForStatus) {
        this.colorForStatus = colorForStatus;
    }
	
	public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
	
}