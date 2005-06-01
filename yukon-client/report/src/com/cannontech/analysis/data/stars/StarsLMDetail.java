/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.stars;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StarsLMDetail
{
    private Integer groupID = null;
    private Integer groupCapacity = null;
    private String accountNumber = null;
    private String contLastName = null;
    private String contFirstName = null;
    private String mapNumber = null;
    private String serialNumber = null;
    private Integer deviceType = null;
    private Integer applianceType = null;
    private Integer applianceCapacity = null;
    private Integer enrollStatus = null;
    
    /**
     * @param groupID
     * @param groupCapacity
     * @param accountNumber
     * @param contLastName
     * @param contFirstName
     * @param mapNumber
     * @param serialNumber
     * @param deviceType
     * @param applianceType
     * @param applianceCapacity
     * @param enrollStatus
     */
    public StarsLMDetail(Integer groupID, Integer groupCapacity, String accountNumber, String contLastName, String contFirstName, String mapNumber, String serialNumber,
            Integer deviceType, Integer applianceType, Integer applianceCapacity, Integer enrollStatus) {
        super();
        this.groupID = groupID;
        this.groupCapacity = groupCapacity;
        this.accountNumber = accountNumber;
        this.contLastName = contLastName;
        this.contFirstName = contFirstName;
        this.mapNumber = mapNumber;
        this.serialNumber = serialNumber;
        this.deviceType = deviceType;
        this.applianceType = applianceType;
        this.applianceCapacity = applianceCapacity;
        this.enrollStatus = enrollStatus;
    }
    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }
    /**
     * @return Returns the applianceCapacity.
     */
    public Integer getApplianceCapacity()
    {
        return applianceCapacity;
    }
    /**
     * @return Returns the applianceType.
     */
    public Integer getApplianceType()
    {
        return applianceType;
    }
    /**
     * @return Returns the contFirstName.
     */
    public String getContFirstName()
    {
        return contFirstName;
    }
    /**
     * @return Returns the contLastName.
     */
    public String getContLastName()
    {
        return contLastName;
    }
    /**
     * @return Returns the deviceType.
     */
    public Integer getDeviceType()
    {
        return deviceType;
    }
    /**
     * @return Returns the enrollStatus.
     */
    public Integer getEnrollStatus()
    {
        return enrollStatus;
    }
    /**
     * @return Returns the groupCapacity.
     */
    public Integer getGroupCapacity()
    {
        return groupCapacity;
    }
    /**
     * @return Returns the groupID.
     */
    public Integer getGroupID()
    {
        return groupID;
    }
    /**
     * @return Returns the mapNumber.
     */
    public String getMapNumber()
    {
        return mapNumber;
    }
    /**
     * @return Returns the serialNumber.
     */
    public String getSerialNumber()
    {
        return serialNumber;
    }
}
