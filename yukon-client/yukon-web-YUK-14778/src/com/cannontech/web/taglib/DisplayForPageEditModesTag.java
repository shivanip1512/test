package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.web.PageEditMode;
import com.google.common.collect.Sets;

public class DisplayForPageEditModesTag extends YukonTagSupport {

	private String modes = null;
	
	@Override
    public void doTag() throws JspException, IOException {
    	
		String attributeName = StandardPageTag.class.getName() + ".pageEditMode";
        PageEditMode currentPageEditMode = (PageEditMode) getJspContext().getAttribute(attributeName, PageContext.REQUEST_SCOPE);
		if (currentPageEditMode == null) {
			currentPageEditMode = PageEditMode.EDIT;
		}
		
		if (modes != null) {
			
			Set<PageEditMode> selectedModes = Sets.newHashSet();
			String[] modesStrs = StringUtils.split(modes, ",");
			for (String modeStr : modesStrs) {
				selectedModes.add(PageEditMode.valueOf(modeStr));
			}
			
			if (selectedModes.contains(currentPageEditMode)) {
				getJspBody().invoke(getJspContext().getOut());
			}
		}
    }
	
	public void setModes(String modes) {
		this.modes = modes;
	}
}
