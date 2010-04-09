package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.core.dao.YukonListDao;

@Configurable("yukonListEntryValueTagPrototype")
public class YukonListEntryValueTag extends YukonTagSupport {

	private String var;
	private int entryId;
	
	private YukonListDao yukonListDao;
    
    @Override
    public void doTag() throws JspException, IOException {
    	
    	YukonListEntry yukonListEntry = yukonListDao.getYukonListEntry(entryId);
    	String entryText = yukonListEntry.getEntryText();
    	
    	if (var == null) {
            getJspContext().getOut().print(entryText);
        } else {
            this.getJspContext().setAttribute(var, entryText);
        }
    }

    public void setVar(String var) {
		this.var = var;
	}
    public void setEntryId(int entryId) {
		this.entryId = entryId;
	}
    
    @Required
    public void setYukonListDao(YukonListDao yukonListDao) {
		this.yukonListDao = yukonListDao;
	}
}
