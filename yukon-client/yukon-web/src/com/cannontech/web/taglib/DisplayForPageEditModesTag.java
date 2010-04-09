package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import com.cannontech.web.PageEditMode;
import com.google.common.collect.Sets;

public class DisplayForPageEditModesTag extends YukonTagSupport {

	private String modes = null;
	
	@Override
    public void doTag() throws JspException, IOException {
    	
		PageEditMode currentPageEditMode = null;
		try {
			currentPageEditMode = (PageEditMode)this.getJspContext().getAttribute(StandardPageTag.PAGE_EDIT_MODE_ATTR, PageContext.REQUEST_SCOPE);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid PageEditMode has been set.", e);
		}
		if (currentPageEditMode == null) {
			throw new IllegalArgumentException("PageEditMode has not been set.");
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
