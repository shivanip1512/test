package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If the property is true then the body of the tag is skipped, otherwise it is evaluated
 * @author alauinger
 */
public class CheckNoProperty extends BodyTagSupport {

	private int propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
			
		return (user == null || !YukonSpringHook.getBean(AuthDao.class).checkRoleProperty(user,propertyid)) ?
					EVAL_BODY_INCLUDE :
					SKIP_BODY;
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
		
	/**
	 * Returns the propertyid.
	 * @return int
	 */
	public int getPropertyid() {
		return propertyid;
	}

	/**
	 * Sets the propertyid.
	 * @param propertyid The propertyid to set
	 */
	public void setPropertyid(int propertyid) {
		this.propertyid = propertyid;
	}

}
