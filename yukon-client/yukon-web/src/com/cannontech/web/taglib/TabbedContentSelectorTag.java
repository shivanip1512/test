package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.web.JsLibrary;


public class TabbedContentSelectorTag extends BodyTagSupport {
    private enum Mode{ box, section };

    private Mode mode = Mode.box;
    private String classes = "";
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
	    try {
	        String outputId = ! StringUtils.isEmpty(id) ? id : "tabbedControl_"+ UniqueIdentifierTag.generateIdentifier(pageContext, "tabbedContentSelectorContainer_");
	        boolean doHeaderOutline = this.mode.equals(Mode.section);

	        pageContext.getOut().println("<script type=\"text/javascript\">");
            pageContext.getOut().println("jQuery(function(){jQuery(\"#" + outputId + "\").tabs({selected: "+ getSelectedTabIndex() +"}).show();});");
            pageContext.getOut().println("</script>");
            //The tabs are initially hidden on page load to avoid jarring page shrinkage after the js initialization of the tabs.
            String css = this.classes +" tabbedContainer ui-tabs-box" + (doHeaderOutline ? " ui-tabs-header" : "");
            pageContext.getOut().println("<div id=\"" + outputId + "\" class=\"dn "+ css +"\">");
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
