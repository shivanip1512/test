package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class TabTag extends YukonTagSupport {
    
    private String title = "";
    private String classes = "";
    private String tabId;
    private boolean selected = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        String id = tabId != null ? tabId : UniqueIdentifierTag.generateIdentifier(getJspContext(), "tab-");
        
        // tell container tag about ourself
        TabsTag parent = getParent(TabsTag.class);
        parent.addTab(title, id, selected);
        
        // tab content
        getJspContext().getOut().println("<div id=\"" + id + "\" class=\"" + classes + "\">");
        getJspBody().invoke(getJspContext().getOut());
        getJspContext().getOut().println("</div>");
        
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
 
    public void setTabId(String tabId) {
        this.tabId = tabId;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
}