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
    
    private enum Mode{ box, section };

    private Mode mode = Mode.section;
    private String classes = "tabbed-container js-init-tabs";
    private String id="";
    private String selectedTabName = "";
    private List<String> tabNames = new ArrayList<String>();
    private List<String> tabIds = new ArrayList<String>();
    
    public void addTab(String name, String id, boolean initiallySelected) {
        this.tabNames.add(name);
        this.tabIds.add(id);
        if (initiallySelected) {
            this.selectedTabName = name;
        }
    }
    
    public void setMode(String newMode) {
        this.mode = Mode.valueOf(newMode);
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
            
            if (mode == Mode.section) {
                classes += " section";
            }
            
            out.print("<div class=\"dn "+ classes +"\"");
            if (!StringUtils.isEmpty(id)) {
                out.print(" id=\"" + id + "\"");
            }
            out.println(">");
            
            out.println("<ul>");
            int selectedTabIndex = getSelectedTabIndex();
            for (int i = 0; i < tabIds.size(); i++) {
                out.println("<li");
                if (i == selectedTabIndex) {
                    out.println(" data-selected=\"" + i + "\"");
                }
                out.println(">");
                out.println("<a href=\"#" + tabIds.get(i) + "\">" + tabNames.get(i) + "</a></li>");
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
    
    private int getSelectedTabIndex() {
        int selectedTabIndex = 0;
        for (int idx = 0; idx < tabIds.size(); idx++) {
            if (tabNames.get(idx).equals(this.selectedTabName)) {
                selectedTabIndex = idx;
                break;
            }
        }
        return selectedTabIndex;
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