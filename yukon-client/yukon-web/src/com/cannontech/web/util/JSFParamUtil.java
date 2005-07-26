package com.cannontech.web.util;

import javax.faces.context.FacesContext;

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
		if( varName == null ) return null;
		
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
}
