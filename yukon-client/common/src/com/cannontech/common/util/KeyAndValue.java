/*
 * Created on Sep 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.util;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KeyAndValue 
{
	private String key = null;
	private String value = null;

	public KeyAndValue()
	{
		super();
	}
	public KeyAndValue(String key_, String value_)
	{
		super();
		key = key_;
		value = value_;
	}
	public void setKey(String key_)
	{
		key = key_;
	}
	public String getKey()
	{
		return key;
	}
	public void setValue(String value_)
	{
		value = value_;
	}
	public String getValue()
	{
		return value;
	}
}
