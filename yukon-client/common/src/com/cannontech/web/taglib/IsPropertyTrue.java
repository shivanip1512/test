package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Attempts to match a propertyid with the LiteYukonUser
 * in the current session. If the property is set to true,
 * then the body of the tag is evaluated, otherwise it
 * is skipped.
 * 
 * This method uses the default value of the property if the
 * user does not have the role this property belongs to.
 * 
 */
public class IsPropertyTrue extends BodyTagSupport
{
	private int propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException
	{
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
	
		LiteYukonRoleProperty liteProp =
			AuthFuncs.getRoleProperty(propertyid);

		if( user != null && liteProp != null )
		{
			String val = AuthFuncs.getRolePropertyValue(
				user,
				liteProp.getRolePropertyID(),
				liteProp.getDefaultValue() );
			
			if( Boolean.TRUE.toString().equalsIgnoreCase(val) )
				return EVAL_BODY_INCLUDE;
			else
				return SKIP_BODY;
		}
		else
			return SKIP_BODY;
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

}
