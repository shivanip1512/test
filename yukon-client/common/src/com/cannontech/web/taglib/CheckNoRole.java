package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If a match is found then the body of the tag is skipped, otherwise it is evaluated.
 * @author alauinger
 * @see CheckRole
 */
public class CheckNoRole extends BodyTagSupport {

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
				if (AuthFuncs.checkRole(user, rid) != null) return SKIP_BODY;
			}
			catch (NumberFormatException e) {
				throw new JspException( e.getMessage() );
			}
		}
		
		return EVAL_BODY_INCLUDE;			
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
