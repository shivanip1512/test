package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If a match is found then the body of the tag is evaluated, otherwise it is skipped.
 * @author alauinger
 * @see CheckNoRole
 */
public class CheckRole extends BodyTagSupport {

	private int roleid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
			
		return (user == null || AuthFuncs.checkRole(user,roleid) == null) ?
					SKIP_BODY :
					EVAL_BODY_INCLUDE;
	}

	/**
	 * Returns the roleid.
	 * @return int
	 */
	public int getRoleid() {
		return roleid;
	}

	/**
	 * Sets the roleid.
	 * @param roleid The roleid to set
	 */
	public void setRoleid(int roleid) {
		this.roleid = roleid;
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

}
