package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * Checks two or more propertyids against the LiteYukonUser in the session
 * If any of them are true then the body of the tag is evaluated.
 * 
 * The propertyids must be comma separated.
 * @author alauinger
 * @see CheckProperty
 * @see CheckNoProperty
 */
public class CheckMultiProperty extends BodyTagSupport {

	private String propertyid;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		LiteYukonUser user = 
			(LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if (user == null) return SKIP_BODY;
		
		java.util.StringTokenizer st = new java.util.StringTokenizer(propertyid, ",");
		while (st.hasMoreTokens()) {
			try {
				int pid = Integer.parseInt( st.nextToken() );
				if (DaoFactory.getAuthDao().checkRoleProperty(user, pid)) return EVAL_BODY_INCLUDE;
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
	 * Returns the propertyid.
	 * @return int
	 */
	public String getPropertyid() {
		return propertyid;
	}

	/**
	 * Sets the propertyid.
	 * @param propertyid The propertyid to set
	 */
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}
	
	/**
     * Sets the propertyid by searching for the property
     * @param property The property to set
     */
    public void setProperty(String property){
        List<Integer> propertyIds = new ArrayList<Integer>();
        for (String propertyName : StringUtils.split(property, ",")) {
            propertyIds.add(ReflectivePropertySearcher.getRoleProperty().getIntForName(propertyName));
        }
        this.propertyid = StringUtils.join(propertyIds.toArray(), ",");
    }

}
