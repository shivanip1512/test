package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If the property is true then the body of the tag is skipped, otherwise it is evaluated
 * @author alauinger
 * @see CheckNoProperty
 */
public class CheckNoProperty extends BodyTagSupport {

	private String propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if (user == null) return SKIP_BODY;
			
		java.util.StringTokenizer st = new java.util.StringTokenizer(propertyid, ",");
		while (st.hasMoreTokens()) {
			try {
				int pid = Integer.parseInt( st.nextToken() );
				if (AuthFuncs.checkRoleProperty(user, pid)) return SKIP_BODY;
			}
			catch (NumberFormatException e) {
				throw new JspException( e.getMessage() );
			}
		}
		
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Returns the propertyid.
	 * @return int
	 */
	public String getPropertyid() {
		return propertyid;
	}

	/**
	 * Sets the propertyid.
	 * @param propertyid The propertyid to set
	 */
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}

}
