package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Checks two or more roleids against the LiteYukonUser in the session
 * If any of them are true then the body of the tag is evaluated.
 * 
 * The roleids must be comma separated.
 * @author alauinger
 * @see CheckRole
 * @see CheckNoRole
 */
public class CheckMultiRole extends BodyTagSupport {

	private String roleid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if (user == null) return SKIP_BODY;
		
		java.util.StringTokenizer st = new java.util.StringTokenizer(roleid, ",");
		while (st.hasMoreTokens()) {
			try {
				int rid = Integer.parseInt( st.nextToken() );
				if (AuthFuncs.checkRole(user, rid) != null) return EVAL_BODY_INCLUDE;
			}
			catch (NumberFormatException e) {
				throw new JspException( e.getMessage() );
			}
		}
		
		return SKIP_BODY;
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
	 * Returns the roleid.
	 * @return int
	 */
	public String getRoleid() {
		return roleid;
	}

	/**
	 * Sets the roleid.
	 * @param roleid The roleid to set
	 */
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

}
