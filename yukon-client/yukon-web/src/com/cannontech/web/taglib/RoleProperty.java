package com.cannontech.web.taglib;


import javax.servlet.jsp.JspException;

import org.springframework.web.util.ExpressionEvaluationUtils;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;
import com.cannontech.util.ServletUtil;
/**
 * Writes out the value of a role property for the current user.
 * Creation date: (11/13/2001 4:30:35 PM)
 * @author: alauinger
 */
public class RoleProperty extends javax.servlet.jsp.tagext.BodyTagSupport {

	private int propertyid;
    private boolean useId = true;
    private String property;
    private String defaultvalue = "";
	public String format = null;
	public String var = null;
	
/**
 * TextTag constructor comment.
 */
public RoleProperty() {
	super();
}
/**
 * Creation date: (11/13/2001 4:36:50 PM)
 * @return int
 * @exception javax.servlet.jsp.JspException The exception description.
 */
public int doEndTag() throws javax.servlet.jsp.JspException {
    property = "";
    propertyid = 0;
    defaultvalue = "";
    format = "";
    return EVAL_PAGE;
}
/**
 * Creation date: (11/13/2001 4:35:26 PM)
 * @return int
 * @exception javax.servlet.jsp.JspException The exception description.
 */
public int doStartTag() throws JspException {
	try {
		LiteYukonUser user = (LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
		if(user != null) {
            int propId;
            if (useId) {
                propId = propertyid;
            } else {
                propId = ReflectivePropertySearcher.getRoleProperty().getIntForName(property);
            }
			String text = DaoFactory.getAuthDao().getRolePropertyValueEx(user, propId);
			String fmat = getFormat();
			if (fmat != null) {
				if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_UPPER ))
					text = text.toUpperCase();
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_LOWER ))
					text = text.toLowerCase();
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_CAPITAL ))
					text = ServletUtil.capitalize( text );
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_ALL_CAPITAL ))
					text = ServletUtil.capitalizeAll( text );
				else if (fmat.equalsIgnoreCase( ServletUtil.FORMAT_ADD_ARTICLE ))
					text = ServletUtil.addArticle( text );
			}
			
			// Expose as variable, if demanded, else write to the page.
			String resolvedVar = ExpressionEvaluationUtils.evaluateString("var", this.var, pageContext);
			if (resolvedVar != null) {
				pageContext.setAttribute(resolvedVar, text);
			} else {
				pageContext.getOut().write(text);
			}
		}
	}
	catch(java.io.IOException e )
	{
		throw new JspException(e.getMessage());
	}

	return SKIP_BODY;
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
        useId = true;
	}

	/**
	 * Returns the format.
	 * @return String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return
	 */
	public String getDefaultvalue() {
		return defaultvalue;
	}

	/**
	 * @param string
	 */
	public void setDefaultvalue(String string) {
		defaultvalue = string;
	}
    
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
        useId = false;
    }
    
    public void setVar(String var) {
		this.var = var;
	}
    
    public String getVar() {
		return var;
	}

}
