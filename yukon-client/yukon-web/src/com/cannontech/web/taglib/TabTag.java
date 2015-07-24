package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class TabTag extends YukonTagSupport {
    
    private String title = "";
    private String classes = "";
    private String headerClasses = "";
    private String tabId;
    private boolean selected = false;
    
    public static class TabHeader {

        private final String title;
        private final String classes;
        private final String id;
        private final boolean selected;
        
        private TabHeader(String id, String title, String classes, boolean selected) {
            this.id = id;
            this.title = title;
            this.classes = classes;
            this.selected = selected;
        }
        
        public final String getId() {
            return id;
        }

        public final String getTitle() {
            return title;
        }
        
        public final String getClasses() {
            return classes;
        }
        public final boolean isSelected() {
            return selected;
        }
        
        
        public static TabHeader of(String id, String title, String classes, boolean selected) {
            return new TabHeader(id, title, classes, selected);
        }
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        String id = tabId != null ? tabId : UniqueIdentifierTag.generateIdentifier(getJspContext(), "tab-");
        
        // tell container tag about ourself
        TabsTag parent = getParent(TabsTag.class);
        
        parent.addTab(TabHeader.of(id, title, headerClasses, selected));
        
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
    
    public void setHeaderClasses(String headerClasses) {
        this.headerClasses = headerClasses;
    }
 
    public void setTabId(String tabId) {
        this.tabId = tabId;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
}