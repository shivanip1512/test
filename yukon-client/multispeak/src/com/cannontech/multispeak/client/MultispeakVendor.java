package com.cannontech.multispeak.client;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.google.common.collect.Lists;

public class MultispeakVendor
{
    public static final String CANNON_MSP_COMPANYNAME = "Cannon";
    public static final int CANNON_MSP_VENDORID = 1;
    
    private Integer vendorID = null;
    private String companyName;
    private String appName;
    private String userName;
    private String password;
    private String outUserName;
    private String outPassword;

    private int maxReturnRecords = MultispeakDefines.MSP_MAX_RETURN_RECORDS;
    private long requestMessageTimeout = MultispeakDefines.MSP_REQUEST_MESSAGE_TIMEOUT;
    private long maxInitiateRequestObjects = MultispeakDefines.MSP_MAX_INITIATE_REQUEST_OBJECTS;
    private String templateNameDefault = MultispeakDefines.MSP_TEMPLATE_NAME_DEFAULT;

    private static MultispeakMeterLookupFieldEnum[] meterLookupFields = MultispeakMeterLookupFieldEnum.values();
    private static MspPaoNameAliasEnum [] paoNameAliases = MspPaoNameAliasEnum.values();
    
    private String url = "http://127.0.0.1:8080/soap/";    //some default url string for formatting example
    
	private List<MultispeakInterface> mspInterfaces = Lists.newArrayList();
    
    public MultispeakVendor()
    {
        super();
    }
    
    public MultispeakVendor(Integer vendorID, String companyName, String appName, String userName, 
            String password, String outUserName, String outPassword,   
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
        this.maxReturnRecords = maxReturnRecords;
        this.requestMessageTimeout = requestMessageTimeout;
        this.maxInitiateRequestObjects = maxInitiateRequestObjects;
        this.templateNameDefault = templateNameDefault;
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
    
    public MspPaoNameAliasEnum[] getPaoNameAliases() {
        return paoNameAliases;
    }
    
    public MultispeakMeterLookupFieldEnum[] getMeterLookupFields() {
        return meterLookupFields;
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