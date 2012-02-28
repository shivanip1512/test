package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TabbedContentSelectorTag extends BodyTagSupport {

    private String cssClass = "";
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
	
	@Override
	public int doStartTag() throws JspException {
	    tabIds.clear();
	    tabNames.clear();
	    return EVAL_BODY_BUFFERED;
	}
	
	public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
	
	@Override
	public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
    }
	
	public int doEndTag() throws JspException {
	    try {
	        // add scripts, css
	        StandardPageTag spTag = StandardPageTag.find(pageContext);
	        if (spTag != null) {
	            spTag.addScriptFile(JsLibrary.JQUERY.getPath()); //add the jQuery Library for posterity 
	            spTag.addScriptFile(JsLibrary.JQUERY_UI.getPath()); //add the UI Library for posterity 
	        } else {
	            throw new UnsupportedOperationException("TabbedContentSelectorTag should be used within a StandardPageTag");
	        }
	        
	        String id = UniqueIdentifierTag.generateIdentifier(pageContext, "tabbedContentSelectorContainer_");
	        
	        pageContext.getOut().println("<script type=\"text/javascript\">");
            pageContext.getOut().println("jQuery(function(){jQuery(\"#tabbedControl_" + id + "\").tabs({selected: "+ getSelectedTabIndex() +"}).show();});");
            pageContext.getOut().println("</script>");
            //The tabs are initially hidden on page load to avoid jarring page shrinkage after the js initialization of the tabs.
            pageContext.getOut().println("<div id=\"tabbedControl_" + id + "\" class=\"dn "+ this.cssClass +"\">");
	        pageContext.getOut().println("<ul>");
            for(int i=0; i<tabIds.size(); i++){
                pageContext.getOut().println("<li><a href=\"#" + tabIds.get(i) + "\">" + tabNames.get(i) + "</a></li>");
            }
            pageContext.getOut().println("</ul>");
            
            //output the actual tabbed content
            pageContext.getOut().write(bodyContent.getString());
	        pageContext.getOut().println("</div>");
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
}
