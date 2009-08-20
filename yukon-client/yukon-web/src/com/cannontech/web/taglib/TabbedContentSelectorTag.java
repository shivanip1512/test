package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class TabbedContentSelectorTag extends SimpleTagSupport {

	private String selectedTabName = "";
	private List<String> tabNames = new ArrayList<String>();
	private List<String> tabIds = new ArrayList<String>();
	
	public void setSelectedTabName(String selectedTabName) {
		this.selectedTabName = selectedTabName;
	}
	
	public void addTab(String name, String id) {
		this.tabNames.add(name);
		this.tabIds.add(id);
	}
	
	@Override
    public void doTag() throws JspException, IOException {
		
		String id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "tabbedContentSelectorContainer_");
		
		// add scripts, css
		StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (spTag != null) {
            spTag.addScriptFile("/JavaScript/extjs/ext-base.js");
            spTag.addScriptFile("/JavaScript/extjs/ext-all.js");
            spTag.addScriptFile("/JavaScript/tabbedContentSelectorContainer.js");
            spTag.addCSSFile("/JavaScript/extjs_cannon/resources/css/tabs.css");
        } else {
        	throw new UnsupportedOperationException("TabbedContentSelectorTag should be used within a StandardPageTag");
        }
        
		// selected tab
        int selectedTabIndex = 0;
        for (int idx = 0; idx < tabNames.size(); idx++) {
        	if (tabNames.get(idx).equals(this.selectedTabName)) {
        		selectedTabIndex = idx;
        		break;
        	}
        }
        
        // javascript, ext
		getJspBody().invoke(null);
		getJspContext().getOut().println("<script type=\"text/javascript\">");
		getJspContext().getOut().println("	function doSetup_" + id + "() {");
		getJspContext().getOut().println("		setupTabbedControl('tabbedControl_" + id + "', " + makeContentItemsArray() + ", " + selectedTabIndex + ");");
		getJspContext().getOut().println("	}");
		getJspContext().getOut().println("	Event.observe (window, 'load', doSetup_" + id + ");");
		getJspContext().getOut().println("</script>");
		
        getJspContext().getOut().println("<span id=\"tabbedControl_" + id + "\"></span>");
	}
	
	
	// make inline array of hashes representing tab data
	private String makeContentItemsArray() {
		
		StringBuffer s = new StringBuffer();
		s.append("$A([");
		
		for (int idx = 0; idx < tabIds.size(); idx++) {
			
			s.append("$H({'title':'" + tabNames.get(idx) + "', 'contentEl':'" + tabIds.get(idx) + "'})");
			if (idx < tabIds.size() - 1) {
				s.append(", ");
			}
		}
		
		s.append("])");
		return s.toString();
	}
	
}
