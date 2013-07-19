package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;

/**
 * Attempts to match a propertyid with the LiteYukonUser
 * in the current session. If the property is set to false,
 * then the body of the tag is evaluated, otherwise it
 * is skipped.
 * 
 * This method uses the default value of the property if the
 * user does not have the role this property belongs to.
 * 
 */
public class IsPropertyFalse extends BodyTagSupport
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
			YukonSpringHook.getBean(RoleDao.class).getRoleProperty(propertyid);

		if( user != null && liteProp != null ) {
			
			boolean val = YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(
			        YukonRoleProperty.getForId(liteProp.getRolePropertyID()),
					user );
				
				if(!val) {
					return EVAL_BODY_INCLUDE;
				} else {
					return SKIP_BODY;
				}
			
		} else {
			return SKIP_BODY;
		}
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
        this.setPropertyid(YukonRoleProperty.valueOf(property).getPropertyId());
    }

}
