package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
/**
 * Creation date: (11/14/2001 1:04:09 PM)
 * @author: 
 */
public class CheckLoginTag extends BodyTagSupport {
/**
 * CheckLoginTag constructor comment.
 */
public CheckLoginTag() {
	super();
}
/**
 * Creation date: (11/14/2001 1:05:56 PM)
 * @return int
 * @exception javax.servlet.jsp.JspException The exception description.
 */
public int doStartTag() throws javax.servlet.jsp.JspException {
	
	try {
		javax.servlet.http.HttpSession session;
		
		if( (session = pageContext.getSession()) != null ) {			 
			if(session.getAttribute("YUKON_USER") != null ) {
				return EVAL_PAGE;
			}
		}

		// Not logged in
		 javax.servlet.http.HttpServletResponse response = 
		 	(javax.servlet.http.HttpServletResponse) pageContext.getResponse();

		 response.sendRedirect("/login.jsp");
	}
	catch(Exception e ) {
		throw new JspException(e.getMessage());
	}

	return SKIP_PAGE;
	
}
}
