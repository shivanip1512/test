package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;/**
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
			
		com.cannontech.database.data.web.User user = (com.cannontech.database.data.web.User) pageContext.getSession().getValue("USER");
		if( user != null )
			uri = user.getCustomerWebSettings().getHomeURL();		

		if( uri == null )	
			uri = "/"; 

		String res = pageContext.getServletContext().getRealPath(uri) + "/text.properties";

		java.io.FileInputStream is = null;
		java.util.Properties props = new java.util.Properties();
		
		try
		{
			is = new java.io.FileInputStream(res);
	  		props.load(is);
	  	}
	  	catch (Exception e)
	  	{
		 	throw new JspException(e.getMessage());		 
	  	}
	  	finally
	  	{
		  	if( is != null ) is.close();
	  	}

		pageContext.getOut().write(props.getProperty( getKey() ));
			 	
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
