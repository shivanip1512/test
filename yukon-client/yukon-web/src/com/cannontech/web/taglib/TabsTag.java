package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;


public class TabsTag extends BodyTagSupport {
    
    private String classes = "";
    private String id="";
    private int selectedTabIndex = 0;
    private List<String> tabNames = new ArrayList<String>();
    private List<String> tabIds = new ArrayList<String>();
    
    public void addTab(String name, String id, boolean initiallySelected) {
        tabNames.add(name);
        tabIds.add(id);
        if (initiallySelected) {
            selectedTabIndex = tabNames.size() - 1;
        }
    }
    
    public void setClasses(String classes) {
        this.classes = classes;
    }
    
    @Override
    public int doStartTag() throws JspException {
        tabIds.clear();
        tabNames.clear();
        return EVAL_BODY_BUFFERED;
    }
    
    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    @Override
    public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
    }
    
    @Override
    public int doEndTag() throws JspException {
        
        JspWriter out = pageContext.getOut();
        try {
            
            out.print("<div class=\"dn tabbed-container js-init-tabs " + classes + "\"");
            if (!StringUtils.isEmpty(id)) {
                out.print(" id=\"" + id + "\"");
            }
            out.print(" data-selected-tab=\"" + selectedTabIndex + "\">");
            
            out.println("<ul>");
            for (int i = 0; i < tabIds.size(); i++) {
                out.println("<li><a href=\"#" + tabIds.get(i) + "\">" + tabNames.get(i) + "</a></li>");
            }
            out.println("</ul>");
            
            //output the actual tabbed content
            out.write(bodyContent.getString());
            out.println("</div>");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
}