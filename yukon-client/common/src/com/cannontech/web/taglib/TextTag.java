package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
/**
 * Creation date: (11/13/2001 4:30:35 PM)
 * @author: Aaron Lauinger
 */
public class TextTag extends javax.servlet.jsp.tagext.BodyTagSupport {
	public String key;
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
	try
	{
		String uri = null;
			
		LiteYukonUser user = (LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if(user != null) {
			String text = AuthFuncs.getRoleValue(user,getKey(),"");
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
 * Creation date: (11/13/2001 4:35:05 PM)
 * @return java.lang.String
 */
public java.lang.String getKey() {
	return key;
}
/**
 * Creation date: (11/13/2001 4:35:05 PM)
 * @param newKey java.lang.String
 */
public void setKey(java.lang.String newKey) {
	key = newKey;
}
}
