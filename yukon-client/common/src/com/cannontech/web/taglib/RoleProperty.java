package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
/**
 * Writes out the value of a role property for the current user.
 * Creation date: (11/13/2001 4:30:35 PM)
 * @author: alauinger
 */
public class RoleProperty extends javax.servlet.jsp.tagext.BodyTagSupport {

	public int propertyid;
	public String defaultvalue = null;
	public String format = null;
	
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
			if (defaultvalue == null) defaultvalue=" ";
			//defaultvalue = "Missing rolePropertyID:  " + Integer.toString(propertyid);
			String text = AuthFuncs.getRolePropertyValue(user, propertyid, defaultvalue);
			String fmat = getFormat();
			if (fmat != null) {
				if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_UPPER ))
					text = text.toUpperCase();
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_LOWER ))
					text = text.toLowerCase();
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_CAPITAL ))
					text = ServletUtil.capitalize( text );
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_ALL_CAPITAL ))
					text = ServletUtil.capitalizeAll( text );
			}
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

	/**
	 * Returns the format.
	 * @return String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return
	 */
	public String getDefaultvalue() {
		return defaultvalue;
	}

	/**
	 * @param string
	 */
	public void setDefaultvalue(String string) {
		defaultvalue = string;
	}

}
