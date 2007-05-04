package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.common.constants.LoginController;

/**
 * Creation date: (11/14/2001 1:04:09 PM)
 * @author: 
 */
public class CheckLoginTag extends TagSupport {
	
	/**
	 * CheckLoginTag constructor comment.
	 */
	public CheckLoginTag() {
	    super();
	}
	/* 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
        return checkLogin(pageContext) ? EVAL_PAGE : SKIP_PAGE;
	}
    
    
    public static boolean checkLogin(PageContext pageContext) throws JspException {
        HttpServletRequest request = 
            (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = 
            (HttpServletResponse) pageContext.getResponse();

        try {
            return checkLogin(request, response);
        } catch(Exception e ) {
                throw new JspException("Caught error while checking login", e);
            }
        }
    
    /**
     * Checks the login. Can be called from other tags.
     * @param pageContext
     * @return
     * @throws JspException
     */
    public static boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        javax.servlet.http.HttpSession session;
        if( (session = request.getSession()) != null ) {             
            if(session.getAttribute("YUKON_USER") != null ) {
                return true;
            }
        }

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

        return false;
    }

}
