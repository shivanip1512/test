package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If the property is true then the body of the tag is evaluated, otherwise it is skipped.
 * @author alauinger
 * @see CheckNoProperty
 */
public class CheckProperty extends BodyTagSupport {

	private int propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
			
		return (user == null || !AuthFuncs.checkRoleProperty(user,propertyid)) ?
					SKIP_BODY :
					EVAL_BODY_INCLUDE;
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
