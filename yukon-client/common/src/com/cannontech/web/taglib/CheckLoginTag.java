package com.cannontech.web.taglib;

import javax.servlet.http.Cookie;
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
    
    
    /**
     * Checks the login. Can be called from other tags.
     * @param pageContext
     * @return
     * @throws JspException
     */
    public static boolean checkLogin(PageContext pageContext) throws JspException {
        try {
            javax.servlet.http.HttpSession session;
            
            if( (session = pageContext.getSession()) != null ) {             
                if(session.getAttribute("YUKON_USER") != null ) {
                    return true;
                }
            }

            
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

        return false;
    }

}
