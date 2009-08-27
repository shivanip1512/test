package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TabbedContentSelectorTag extends SimpleTagSupport {

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
        
        // javascript, ext
		getJspBody().invoke(null);
		getJspContext().getOut().println("<script type=\"text/javascript\">");
		getJspContext().getOut().println("	function doSetup_" + id + "() {");
		getJspContext().getOut().println("		setupTabbedControl('tabbedControl_" + id + "', " + makeContentItemsArray() + ", " + getSelectedTabIndex() + ");");
		getJspContext().getOut().println("	}");
		getJspContext().getOut().println("	Event.observe (window, 'load', doSetup_" + id + ");");
		getJspContext().getOut().println("</script>");
		
        getJspContext().getOut().println("<span id=\"tabbedControl_" + id + "\"></span>");
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
	
	
	// make inline array of hashes representing tab data
	private String makeContentItemsArray() {
		
	    JSONArray jsonArray = new JSONArray();
	    for (int idx = 0; idx < tabIds.size(); idx++) {
	        
	        JSONObject tabObj = new JSONObject();
	        tabObj.put("title", tabNames.get(idx));
	        tabObj.put("contentEl",  tabIds.get(idx));
	        jsonArray.put(tabObj);
	    }

	    return jsonArray.toString();
	}
	
}
