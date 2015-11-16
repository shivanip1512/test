package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.updater.DataUpdaterService;
import com.cannontech.web.updater.UpdateValue;

@Configurable("classUpdaterTagPrototype")
public class ClassUpdaterTag extends YukonTagSupport {
    private DataUpdaterService dataUpdaterService;
    
    private String type;
    private String identifier;
    private String key;
    private String initialClassName = "";
    
    @Override
    public void doTag() throws JspException, IOException {
    	if (getKey() == null && (getType() == null || getIdentifier() == null)) {
    		throw new IllegalArgumentException("Must use type/identifier parameters, or the key parameter.");
    	}
        
    	UpdateValue value;
    	
    	if (getType() != null && getIdentifier() != null) {
    		value = dataUpdaterService.getFirstValue(getType() + "/" + getIdentifier(), getUserContext());
    	} else {
    		value = dataUpdaterService.getFirstValue(getKey(), getUserContext());
    	}
    	
        JspWriter out = getJspContext().getOut();
        
        String className = value.isUnavailable() ? initialClassName :  value.getValue();
        out.print("<span data-class-updater=\""
                + StringEscapeUtils.escapeHtml4(value.getIdentifier().getFullIdentifier()) + "\" class=\"" + className + "\" >");
        getJspBody().invoke(null);
        out.print("</span>");
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getInitialClassName() {
		return initialClassName;
	}

	public void setInitialClassName(String initialClassName) {
		this.initialClassName = initialClassName;
	}

	public void setDataUpdaterService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }
}
