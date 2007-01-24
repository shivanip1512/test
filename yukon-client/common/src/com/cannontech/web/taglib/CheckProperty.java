package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * Attempts to matche a roleid with the LiteYukonUser in the current session.
 * If the property is true then the body of the tag is evaluated, otherwise it is skipped.
 * @author alauinger
 * @see CheckNoProperty
 */
public class CheckProperty extends BodyTagSupport {

	private int propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
			
		return (user == null || !DaoFactory.getAuthDao().checkRoleProperty(user,propertyid)) ?
					SKIP_BODY :
					EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Fix for JRun3.1 tags
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {		
		try {
			if(bodyContent != null) {
				pageContext.getOut().print(bodyContent.getString());
			}
			return EVAL_PAGE;
		} catch (IOException e) {
			throw new JspException(e.getMessage());
		}
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
	 * Sets the propertyid by searching for the property
	 * @param property The property to set
	 */
    public void setProperty(String property){
        this.setPropertyid(ReflectivePropertySearcher.getRoleProperty().getIntForName(property));
    }

}
