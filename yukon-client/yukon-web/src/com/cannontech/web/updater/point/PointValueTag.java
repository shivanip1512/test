package com.cannontech.web.updater.point;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.updater.UpdateValue;

@Configurable("pointValueTagPrototype")
public class PointValueTag extends YukonTagSupport {
    private PointDataRegistrationService registrationService;
    private String format = Format.FULL.toString();
    private int pointId = 0;
    private boolean pointIdSet;
    private String unavailableValue = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!pointIdSet) {
            throw new JspException("pointId must be set");
        }
        
        UpdateValue value = registrationService.getLatestValue(pointId, format, getUserContext());
        
        String outputText;
        if (value.isUnavailable()) {
        	if (getUnavailableValue() != null) {
        		outputText = getUnavailableValue();
        	} else {
        		outputText = getMessageSource().getMessage("yukon.common.pointFormatting.unavailablePlaceholder");
        	}
        } else {
        	outputText = value.getValue();
        }
        
        JspWriter out = getJspContext().getOut();
        out.print("<span title=\"pointId:" + pointId + "\" cannonUpdater=\"" + value.getFullIdentifier() + "\" class=\"pointValueTagSpan\" >");
        out.print(outputText);
        out.print("</span>");
    }

    public int getPointId() {
        return pointId;
    }
    public void setPointId(int pointId) {
        pointIdSet = true;
        this.pointId = pointId;
    }

    public void setRegistrationService(
            PointDataRegistrationService registrationService) {
        this.registrationService = registrationService;
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

	public String getUnavailableValue() {
		return unavailableValue;
	}

}
