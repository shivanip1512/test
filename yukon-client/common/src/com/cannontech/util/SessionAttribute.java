/*
 * Created on Apr 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.util;

/**
 * @author snebben
 *
 * A class to contain a name for the attribute and the Object value of the attribute.
 * Used to pass errors, for example, back to web pages.
 */
public class SessionAttribute
{
	private String attName = null;
	private Object attValue = null;
	/**
	 * 
	 */
	public SessionAttribute(String name, Object value)
	{
		super();
		setAttName( name );
		setAttValue( value );
	}

	/**
	 * @return
	 */
	public String getAttName()
	{
		return attName;
	}

	/**
	 * @return
	 */
	public Object getAttValue()
	{
		return attValue;
	}

	/**
	 * @param string
	 */
	public void setAttName(String string)
	{
		attName = string;
	}

	/**
	 * @param object
	 */
	public void setAttValue(Object object)
	{
		attValue = object;
	}

}
