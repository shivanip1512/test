package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
/**
 * Attempts to match the LiteYukonUser in the session with a given role.
 * If a match is found then it writes out the value of the role for this user.
 * Creation date: (11/13/2001 4:30:35 PM)
 * @author: alauinger
 */
public class TextTag extends javax.servlet.jsp.tagext.BodyTagSupport {
	
	public int roleid;
	
/**
 * TextTag constructor comment.
 */
public TextTag() {
	super();
}
/**
 * Creation date: (11/13/2001 4:36:50 PM)
 * @return int
 * @exception javax.servlet.jsp.JspException The exception description.
 */
public int doEndTag() throws javax.servlet.jsp.JspException {
	return EVAL_PAGE;
}
/**
 * Creation date: (11/13/2001 4:35:26 PM)
 * @return int
 * @exception javax.servlet.jsp.JspException The exception description.
 */
public int doStartTag() throws JspException {
	try {
		String uri = null;			
		LiteYukonUser user = (LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if(user != null) {			
			String text = AuthFuncs.getRoleValue(user,roleid,"");
			pageContext.getOut().write(text);
		}				 	
	}
	catch(java.io.IOException e )
	{
		throw new JspException(e.getMessage());
	}

	return SKIP_BODY;
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

}
