package com.cannontech.web.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author ryan
 *
 */
public class JSFParamUtil
{

	/**
	 * 
	 */
	public JSFParamUtil()
	{
		super();
	}
	
	/**
	 * Allows a JSF variable to be retrieved in a JSP scriptlet
	 */
	public static Object getJSFVar( String varName )
	{
		if( varName == null )		
			return null;
		else
			return
				FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(
					FacesContext.getCurrentInstance(), varName );
	}

	/**
	 * Allows a JSF variable to be removed from the session scope
	 */
	public static void removeJSFVar( String varName )
	{
		if( varName != null )
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove( varName );
	}

	/**
	 * Allows URL parameters to be retrived.
	 */
	public static String[] getReqParamsVar( String varName )
	{
		ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
		
		if( varName != null && ex != null ) {
			Object retVal = ex.getRequestParameterValuesMap().get( varName );
			if( retVal instanceof String[] )
				return (String[])retVal;
			else if( retVal != null )
				return new String[]{ retVal.toString() };
		}

		return null;
	}

	/**
	 * Returns the current YukonUser in the session scope
	 */
	public static LiteYukonUser getYukonUser()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);

		if( session != null ) {
			CTILogger.warn("The current YukonUser inside the HttpSession is NULL");
			return null;
		}
		else 
			return (LiteYukonUser)session.getAttribute(LoginController.YUKON_USER);
	} 
}