package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Checks a yukon system settng
 * If the setting is true then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckGlobalSetting extends BodyTagSupport {

	private GlobalSettingType globalSetting;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	@Override
    public int doStartTag() throws JspException {
	    boolean isSettingSet = YukonSpringHook.getBean(GlobalSettingDao.class).getBoolean(globalSetting);
		return (isSettingSet) ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}
    
	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	@Override
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
        this.globalSetting = GlobalSettingType.valueOf(setting);
    }

}
