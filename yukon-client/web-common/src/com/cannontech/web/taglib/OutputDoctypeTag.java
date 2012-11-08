package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

/**
 * This tag will output the strictest doctype possible taking into account the
 * all of the levels passed in. 
 */
public class OutputDoctypeTag extends SimpleTagSupport {
    private static final Map<HtmlLevel, String> doctypes = new HashMap<HtmlLevel, String>();
    
    static {
        doctypes.put(HtmlLevel.transitional, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
        doctypes.put(HtmlLevel.strict, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n");
    }
    
    private List<HtmlLevel> levels = new ArrayList<HtmlLevel>(0);
    
    @Override
    public void doTag() throws JspException, IOException {
        // find lowest level (some day this could ensure "levels" are compatible)
        int min = Integer.MAX_VALUE;
        for (HtmlLevel level : levels) {
            int value = level.ordinal();
            if (value < min) {
                min = value;
            }
        }
        
        // default to lowest level if non specified
        if (min == Integer.MAX_VALUE) {
            min = 0;
        }
        
        HtmlLevel level = HtmlLevel.values()[min];
        String doctypeStr = doctypes.get(level);
        if (doctypeStr != null) {
            getJspContext().getOut().write(doctypeStr);
        }
    }
    
    public String getLevels() {
        return StringUtils.join(this.levels, ",");
    }

    public void setLevels(String htmlLevels) {
        String[] levelStrs = StringUtils.split(htmlLevels, ",");
        List<HtmlLevel> levels = new ArrayList<HtmlLevel>(levelStrs.length);
        for (String level : levelStrs) {
            level = level.trim();
            HtmlLevel valueOf = HtmlLevel.valueOf(level);
            levels.add(valueOf);
        }
        this.levels = levels;
    }
}
