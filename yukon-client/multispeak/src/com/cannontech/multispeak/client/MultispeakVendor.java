/*
 * Created on Aug 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.util.HashMap;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakVendor
{
	public static final int COMPANY_NAME_INDEX = 0;
	public static final int USERNAME_INDEX = 1;
	public static final int PASSWORD_INDEX = 2;
	public static final int UNIQUE_KEY_INDEX = 3;
	public static final int URL_INDEX = 4;
	public static final int FIRST_ENDPOINT_INDEX = 5;
	//The serviceEndpoints are the indexes greater than URL_INDEX values
	
	
	private String companyName = CtiUtilities.STRING_NONE;
	private String userName = CtiUtilities.STRING_NONE;
	private String password = CtiUtilities.STRING_NONE;
	//Valid values are meternumber | devicename
	private String uniqueKey = "meternumber";
	private String url = "";
	
	private HashMap serviceToEndpointMap = null;
	
	/**
	 * 
	 */
	public MultispeakVendor(String companyName_, String userName_, String password_, String uniqueKey_, String url_)
	{
		super();
		companyName = companyName_;
		userName = userName_;
		password = password_;
		uniqueKey = uniqueKey_;
		url = url_;
	}

	/**
	 * @return
	 */
	public String getCompanyName()
	{
		return companyName;
	}

	/**
	 * @return
	 */
	public HashMap getServiceToEndpointMap()
	{
		if( serviceToEndpointMap == null)
			serviceToEndpointMap = new HashMap(4);
		
		return serviceToEndpointMap;
	}

	/**
	 * @return
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @return
	 */
	public String getUniqueKey()
	{
		return uniqueKey;
	}

	/**
	 * @return
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @return
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * @param string
	 */
	public void setCompanyName(String string)
	{
		companyName = string;
	}

	/**
	 * @param map
	 */
	public void setServiceToEndpointMap(HashMap map)
	{
		serviceToEndpointMap = map;
	}

	/**
	 * @param string
	 */
	public void setPassword(String string)
	{
		password = string;
	}

	/**
	 * @param string
	 */
	public void setUniqueKey(String string)
	{
		uniqueKey = string;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string)
	{
		url = string;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string)
	{
		userName = string;
	}
}
