package com.cannontech.web.taglib;

import javax.servlet.http.Cookie;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.common.constants.LoginController;

/**
 * Creation date: (11/14/2001 1:04:09 PM)
 * @author: 
 */
public class CheckLoginTag extends BodyTagSupport {
	private boolean skip;
	
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
	
	//Force this boolean to false.  Found out the hard way that the skip value is held onto 
	//for each individual jsp page.  This 'seems' to fix a bug where changing logins would 
	//somehow get the user to recieve a blank screen and you could never view the page again.
	skip = false;
	
	try {
		javax.servlet.http.HttpSession session;
		
		if( (session = pageContext.getSession()) != null ) {			 
			if(session.getAttribute("YUKON_USER") != null ) {
				return EVAL_BODY_INCLUDE;
			}
		}

		// Not logged in
		skip = true;
		
		javax.servlet.http.HttpServletRequest request = 
			(javax.servlet.http.HttpServletRequest) pageContext.getRequest();
		javax.servlet.http.HttpServletResponse response = 
			(javax.servlet.http.HttpServletResponse) pageContext.getResponse();
		
		String redirectURL = "/login.jsp";
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {		
			for(int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				System.out.println(c.getName());
				if(c.getName().equalsIgnoreCase(LoginController.LOGIN_URL_COOKIE)) {
					redirectURL = c.getValue();
					break;
				}
			}
		}
		
		if (redirectURL.startsWith("/"))
			redirectURL = request.getContextPath() + redirectURL;
		
		response.sendRedirect(redirectURL);
	}
	catch(Exception e ) {
		throw new JspException(e.getMessage());
	}

	return SKIP_BODY;
	
}
	/* 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		return (skip ? SKIP_PAGE : EVAL_PAGE);
	}

}
