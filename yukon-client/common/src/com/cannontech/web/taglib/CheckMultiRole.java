package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * Checks two or more roleids against the LiteYukonUser in the session
 * If any of them are true then the body of the tag is evaluated.
 * 
 * The roleids must be comma separated.
 * @author alauinger
 * @see CheckRole
 * @see CheckNoRole
 */
public class CheckMultiRole extends BodyTagSupport {

	private String roleid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if (user == null) return SKIP_BODY;

		RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
		
		java.util.StringTokenizer st = new java.util.StringTokenizer(roleid, ",");
		while (st.hasMoreTokens()) {
			try {
				int rid = Integer.parseInt( st.nextToken() );
				YukonRole yukonRole = YukonRole.getForId(rid);
				if (rolePropertyDao.checkRole(yukonRole, user)) return EVAL_BODY_INCLUDE;
			}
			catch (NumberFormatException e) {
				throw new JspException( e.getMessage() );
			}
		}
		
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
     * Splits the string and sets the roleid
     * @param roles - Comma separated list of role names
     */
    public void setRoles(String roles) {

        String[] roleArray = roles.split("\\s*,\\s*");

        Integer[] roleIntArray = new Integer[roleArray.length];
        int count = 0;
        for (String roleName : roleArray) {
            roleIntArray[count++] = ReflectivePropertySearcher.getRoleProperty()
                                                              .getIntForName(roleName);
        }

        this.setRoleid(StringUtils.join(roleIntArray, ","));
    }
	
	/**
     * Returns the roleid.
     * @return int
     */
	public String getRoleid() {
		return roleid;
	}

	/**
	 * Sets the roleid.
	 * @param roleid The roleid to set
	 */
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

}
