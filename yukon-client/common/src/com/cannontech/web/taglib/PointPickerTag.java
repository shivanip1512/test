package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class PointPickerTag extends TagSupport {
    private String pointIdField;
    private String constraint;
    private String pointNameElement;
    private String paoNameElement;

    public PointPickerTag() {
        super();
    }
    
    @Override
    public int doStartTag() throws JspException {
        try {
            // make sure our script file is included
            StandardPageTag spTag = (StandardPageTag) TagSupport.findAncestorWithClass(this, StandardPageTag.class);
            if (spTag != null) {
                spTag.addScriptFile("/JavaScript/pointPicker.js");
                spTag.addScriptFile("/JavaScript/tableCreation.js");
                spTag.addCSSFile("/WebConfig/yukon/styles/pointPicker.css");
            }
            
            // build up extraMapping string
            List<String> extraMappings = new ArrayList<String>();
            if (StringUtils.isNotBlank(pointNameElement)) {
                extraMappings.add("pointName:" + pointNameElement);
            }
            if (StringUtils.isNotBlank(paoNameElement)) {
                extraMappings.add("deviceName:" + paoNameElement);
            }
            String extraMappingString = StringUtils.join(extraMappings.iterator(), ";");
            pageContext.getOut().println(
                " <a href=\"javascript:pointPicker_showPicker(\'"
                    + pointIdField + "\','" + constraint + "', '" 
                    + extraMappingString + "')\">");
            return super.doEndTag();
        } catch (IOException e) {
            throw new JspException("Can't output PointPickerTag", e);
        } finally {
            cleanup();
        }
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            String sufix = RandomStringUtils.randomNumeric(6);
            pageContext.getOut().println("</a>");
            return super.doEndTag();
        } catch (IOException e) {
            throw new JspException("Can't output PointPickerTag", e);
        } finally {
            cleanup();
        }
    }
    
    public void cleanup() {
        pointIdField = "";
        constraint = "";
        pointNameElement = "";
        paoNameElement = "";
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getPointIdField() {
        return pointIdField;
    }

    public void setPointIdField(String pointIdField) {
        this.pointIdField = pointIdField;
    }

    public String getPointNameElement() {
        return pointNameElement;
    }

    public void setPointNameElement(String pointNameElement) {
        this.pointNameElement = pointNameElement;
    }

    public String getPaoNameElement() {
        return paoNameElement;
    }

    public void setPaoNameElement(String paoNameElement) {
        this.paoNameElement = paoNameElement;
    }

}
