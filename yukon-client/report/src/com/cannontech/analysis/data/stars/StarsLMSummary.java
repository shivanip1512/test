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
public class StarsLMSummary
{
    private String groupName = null;
    private Integer groupCapacity = null;
    private Integer numberOfReceivers = null;
    private Integer totalReceiversCapactiy = null;
    private Integer numberOfReceiversOutOfService = null;
    private Integer totalReceiversOutOfServiceCapacity = null;
    // adjustedGroupCapacity = groupCapacity - totalReceiversCapacityOutOfService 
    /**
     * @param groupName
     * @param groupCapacity
     * @param numberOfReceivers
     * @param totalReceiversCapactiy
     * @param numberOfReceiversOutOfService
     * @param totalReceiversCapacityOutOfService
     */
    public StarsLMSummary(String groupName, Integer groupCapacity, Integer numberOfReceivers, Integer totalReceiversCapactiy, Integer numberOfReceiversOutOfService,
            Integer totalReceiversOutOfServiceCapacity) {
        super();
        this.groupName = groupName;
        this.groupCapacity = groupCapacity;
        this.numberOfReceivers = numberOfReceivers;
        this.totalReceiversCapactiy = totalReceiversCapactiy;
        this.numberOfReceiversOutOfService = numberOfReceiversOutOfService;
        this.totalReceiversOutOfServiceCapacity = totalReceiversOutOfServiceCapacity;
    }
    /**
     * groupCapacity - totalReceiversOutOfServiceCapactiy
     * @return Returns the adjustedGroupCapacity.
     */
    public Integer getAdjustedGroupCapacity()
    {
        return new Integer(getGroupCapacity().intValue() - getTotalReceiversOutOfServiceCapacity().intValue());
    }
    /**
     * @return Returns the groupCapacity.
     */
    public Integer getGroupCapacity()
    {
        return groupCapacity;
    }
    /**
     * @return Returns the groupName.
     */
    public String getGroupName()
    {
        return groupName;
    }
    /**
     * @return Returns the numberOfReceivers.
     */
    public Integer getNumberOfReceivers()
    {
        return numberOfReceivers;
    }
    /**
     * @return Returns the numberOfReceiversOutOfService.
     */
    public Integer getNumberOfReceiversOutOfService()
    {
        return numberOfReceiversOutOfService;
    }
    /**
     * @return Returns the totalReceiversOutOfServiceCapacity.
     */
    public Integer getTotalReceiversOutOfServiceCapacity()
    {
        return totalReceiversOutOfServiceCapacity;
    }
    /**
     * @return Returns the totalReceiversCapactiy.
     */
    public Integer getTotalReceiversCapactiy()
    {
        return totalReceiversCapactiy;
    }
    /**
     * @param numberOfReceiversOutOfService The numberOfReceiversOutOfService to set.
     */
    public void setNumberOfReceiversOutOfService(Integer numberOfReceiversOutOfService)
    {
        this.numberOfReceiversOutOfService = numberOfReceiversOutOfService;
    }
    /**
     * @param totalReceiversOutOfServiceCapacity The totalReceiversOutOfServiceCapacity to set.
     */
    public void setTotalReceiversOutOfServiceCapacity(Integer totalReceiversOutOfServiceCapacity)
    {
        this.totalReceiversOutOfServiceCapacity = totalReceiversOutOfServiceCapacity;
    }
}
