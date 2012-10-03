package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

/**
 * Checks a yukon system settng
 * If the setting is true then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckYukonSetting extends BodyTagSupport {

	private YukonSetting yukonSetting;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
	    boolean isSettingSet = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDao.class).checkSetting(yukonSetting);
		return (isSettingSet) ? SKIP_BODY : EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Fix for JRun3.1 tags
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {		
		try {
			if(bodyContent != null) {
				pageContext.getOut().print(bodyContent.getString());
			}
			return EVAL_PAGE;
		} catch (IOException e) {
			throw new JspException(e.getMessage());
		}
	}
    
    public void setSetting(String setting){
        this.yukonSetting = YukonSetting.valueOf(setting);
    }

}
