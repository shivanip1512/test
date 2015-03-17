package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class TabTag extends YukonTagSupport {
    
    private String selectorName = "";
    private String cssClass = "";
    private String tabId;
    private boolean initiallySelected = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        String id = tabId != null ? tabId : UniqueIdentifierTag.generateIdentifier(getJspContext(), "tabbedContentSelectorContent_");
        
        // tell container tag about ourself
        TabbedContentSelectorTag parent = getParent(TabbedContentSelectorTag.class);
        parent.addTab(selectorName, id, initiallySelected);
        
        // tab content
        getJspContext().getOut().println("<div id=\"" + id + "\" class=\"" + cssClass + "\">");
        getJspBody().invoke(getJspContext().getOut());
        getJspContext().getOut().println("</div>");
        
    }
    
    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
 
    public void setTabId(String tabId) {
        this.tabId = tabId;
    }
    
    public void setInitiallySelected(boolean initiallySelected) {
        this.initiallySelected = initiallySelected;
    }
    
}