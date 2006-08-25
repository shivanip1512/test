/*
 * Created on Aug 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.Nameplate;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakVendor
{
    public static final String CANNON_MSP_COMPANYNAME = "Cannon ";
    
	private Integer vendorID = null;
	private String companyName = CtiUtilities.STRING_NONE;
	private String userName = CtiUtilities.STRING_NONE;
	private String password = CtiUtilities.STRING_NONE;
	//Valid values are meternumber | devicename
	private String uniqueKey = "meternumber";
    private int timeout = 0;
	private String url = "http://127.0.0.1:8080/soap/";    //some default url string for formatting example
	
	private List<MultispeakInterface> mspInterfaces = null;
	
	public MultispeakVendor()
    {
        super();
    }

    public MultispeakVendor(Integer vendorID, String companyName, String userName, String password, String uniqueKey, int timeout, String url)
    {
        super();
        this.vendorID = vendorID;
        this.companyName = companyName;
        this.userName = userName;
        this.password = password;
        this.uniqueKey = uniqueKey;
        this.timeout = timeout;
        this.url = url;
    }

	/**
	 * @return
	 */
	public String getCompanyName()
	{
		return companyName;
	}


    /**
     * @return Returns the mspInterfaces.
     */
    public List<MultispeakInterface> getMspInterfaces()
    {
        if( mspInterfaces == null)
            
            mspInterfaces = MultispeakFuncs.getMultispeakDao().getMultispeakInterfaces(getVendorID().intValue());
        return mspInterfaces;
    }

    /**
     * @param mspInterfaces The mspInterfaces to set.
     */
    public void setMspInterfaces(List<MultispeakInterface> mspInterfaces)
    {
        this.mspInterfaces = mspInterfaces;
    }

    
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public Map<String, MultispeakInterface> getMspInterfaceMap()
	{
        Map<String, MultispeakInterface> mspInterfaceMap = new HashMap<String, MultispeakInterface>();
        for (MultispeakInterface mspInterface : getMspInterfaces())
            mspInterfaceMap.put(mspInterface.getMspInterface(), mspInterface);

        return mspInterfaceMap;
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
	/**
	 * @return
	 */
	public Integer getVendorID()
	{
		return vendorID;
	}

	/**
	 * @param i
	 */
	public void setVendorID(Integer i)
	{
		vendorID = i;
	}
    
    /*public String getMeterObjectID(int deviceID)
    {
        if( getUniqueKey().toLowerCase().startsWith("device") || getUniqueKey().toLowerCase().startsWith("pao"))
            return DaoFactory.getPaoDao().getLiteYukonPAO(deviceID).getPaoName();
        else //if(key.toLowerCase().startsWith("meternum"))
            return DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceID).getMeterNumber();
    }*/
    /*public LiteBase getMeterObjectID(int deviceID)
    {
        if( getUniqueKey().toLowerCase().startsWith("device") || getUniqueKey().toLowerCase().startsWith("pao"))
            return DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
        else //if(key.toLowerCase().startsWith("meternum"))
            return DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceID);
    }
    
    public LiteBase getMeterObject(String meterNumber)
    {
        if( getUniqueKey().toLowerCase().startsWith("device") || getUniqueKey().toLowerCase().startsWith("pao"))
            return DaoFactory.getPaoDao().getLiteYukonPaoByName(meterNumber, false).get(0);
        else //if(key.toLowerCase().startsWith("meternum"))
            return DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(0);
    }
    
    public Meter getMeter(int deviceID)
    {
        String objectID = getMeterObjectID(deviceID);
        return MultispeakFuncs.createMeter(objectID);
    }

    public Nameplate getNameplate(int deviceID)
    {
        String objectID = getMeterObjectID(deviceID);
        return MultispeakFuncs.getNameplate(objectID);
    }*/

    /**
     * @return Returns the timeout.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
}
