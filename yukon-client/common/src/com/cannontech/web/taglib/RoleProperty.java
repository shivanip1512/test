package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
/**
 * Writes out the value of a role property for the current user.
 * Creation date: (11/13/2001 4:30:35 PM)
 * @author: alauinger
 */
public class RoleProperty extends javax.servlet.jsp.tagext.BodyTagSupport {

	public int propertyid;	
	
/**
 * TextTag constructor comment.
 */
public RoleProperty() {
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
			String text = AuthFuncs.getRolePropertyValue(user, propertyid, "Missing rolePropertyID:  " + Integer.toString(propertyid));
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
