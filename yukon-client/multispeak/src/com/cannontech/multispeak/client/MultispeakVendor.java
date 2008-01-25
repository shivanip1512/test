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

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.multispeak.dao.impl.MultispeakDaoImpl;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakVendor
{
    public static final String CANNON_MSP_COMPANYNAME = "Cannon";
    
    private Integer vendorID = null;
    private String companyName = CtiUtilities.STRING_NONE;
    private String appName = CtiUtilities.STRING_NONE;
    private String userName = CtiUtilities.STRING_NONE;
    private String password = CtiUtilities.STRING_NONE;
    private String outUserName = CtiUtilities.STRING_NONE;
    private String outPassword = CtiUtilities.STRING_NONE;
    //Valid values are meternumber | devicename
    private String uniqueKey = "meternumber";

    private int maxReturnRecords = 10000;
    private long requestMessageTimeout = 120000;
    private long maxInitiateRequestObjects = 15;
    private String templateNameDefault = "*Default Template";
    
	public static final int DEFAULT_PAONAME = 0;
    public static final int ACCOUNT_NUMBER_PAONAME = 1;
    public static final int SERVICE_LOCATION_PAONAME = 2;
    public static final int CUSTOMER_PAONAME = 3;
    public static final int EA_LOCATION_PAONAME = 4;

    public static String DEFAULT_PAONAME_STRING = "Device Name";
    public static String ACCOUNT_NUMBER_PAONAME_STRING = "Account Number";
    public static String SERVICE_LOCATION_PAONAME_STRING = "Service Location";
    public static String CUSTOMER_PAONAME_STRING = "Customer";
    public static String EA_LOCATION_PAONAME_STRING = "EA Location";
    
    public static String[] paoNameAliasStrings = new String[]{
           DEFAULT_PAONAME_STRING,
           ACCOUNT_NUMBER_PAONAME_STRING,
           SERVICE_LOCATION_PAONAME_STRING,
           CUSTOMER_PAONAME_STRING,
           EA_LOCATION_PAONAME_STRING
       };
    private String url = "http://127.0.0.1:8080/soap/";    //some default url string for formatting example
    
	private List<MultispeakInterface> mspInterfaces = null;
    
    public MultispeakVendor()
    {
        super();
    }

    
    public MultispeakVendor(Integer vendorID, String companyName, String appName, String userName, 
            String password, String outUserName, String outPassword, String uniqueKey,  
            int maxReturnRecords, long requestMessageTimeout, long maxInitiateRequestObjects, 
            String templateNameDefault, String url) {
        super();
        // TODO Auto-generated constructor stub
        this.vendorID = vendorID;
        this.companyName = companyName;
        this.appName = appName;
        this.userName = userName;
        this.password = password;
        this.outUserName = outUserName;
        this.outPassword = outPassword;
        this.uniqueKey = uniqueKey;
        this.maxReturnRecords = maxReturnRecords;
        this.requestMessageTimeout = requestMessageTimeout;
        this.maxInitiateRequestObjects = maxInitiateRequestObjects;
        this.templateNameDefault = templateNameDefault;
        this.url = url;
    }


    public MultispeakVendor(Integer vendorID, String companyName, String userName, String password, 
            String uniqueKey, String url)
    {
        super();
        this.vendorID = vendorID;
        this.companyName = companyName;
        this.userName = userName;
        this.password = password;
        this.uniqueKey = uniqueKey;
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
            
            mspInterfaces = ((MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao")).getMultispeakInterfaces(getVendorID().intValue());
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getOutPassword() {
        return outPassword;
    }

    public void setOutPassword(String outPassword) {
        this.outPassword = outPassword;
    }

    public String getOutUserName() {
        return outUserName;
    }

    public void setOutUserName(String outUserName) {
        this.outUserName = outUserName;
    }
    
    public String[] getPaoNameAliasStrings() {
        return paoNameAliasStrings;
    }
    
    public String getEndpointURL(String service) {
		MultispeakInterface mspInterface = getMspInterfaceMap().get(service);
		String endpointURL = "";
		if( mspInterface != null)
			endpointURL = getUrl() + mspInterface.getMspEndpoint();
		return endpointURL;
    }
    
    public SOAPHeaderElement getHeader() {
    	YukonMultispeakMsgHeader yukonMspMsgHeader = new YukonMultispeakMsgHeader(getOutUserName(), getOutPassword());
        SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", yukonMspMsgHeader);
        header.setPrefix("");	//Trying to eliminate "ns1" prefix for the namespace showing up.  Exceleron had problems with this.
        return header;
    }


    public long getMaxInitiateRequestObjects() {
        return maxInitiateRequestObjects;
    }


    public void setMaxInitiateRequestObjects(long maxInitiateRequestObjects) {
        this.maxInitiateRequestObjects = maxInitiateRequestObjects;
    }


    public int getMaxReturnRecords() {
        return maxReturnRecords;
    }


    public void setMaxReturnRecords(int maxReturnRecords) {
        this.maxReturnRecords = maxReturnRecords;
    }


    public long getRequestMessageTimeout() {
        return requestMessageTimeout;
    }


    public void setRequestMessageTimeout(long requestMessageTimeout) {
        this.requestMessageTimeout = requestMessageTimeout;
    }


    public String getTemplateNameDefault() {
        return templateNameDefault;
    }


    public void setTemplateNameDefault(String templateNameDefault) {
        this.templateNameDefault = templateNameDefault;
    }


}