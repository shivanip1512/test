package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * 
 * @author alauinger
 */
public class CheckNoRole extends BodyTagSupport {

	private String name;

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if(	user != null && 
				name != null &&
				AuthFuncs.checkRole(user,name) == null) {
			BodyContent bc = getBodyContent();					
			try {
				getPreviousOut().print(bc.getString());
			} catch (IOException e) {
			}			
		}
		
		return SKIP_BODY;
	}

}
