package com.cannontech.common.bulk;

import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.user.YukonUserContext;

/**
 * Interface which represents a currently running bulk process
 */
public interface BulkProcess {

    /**
     * Method to get the id of the bulk process
     * @return Bulk process id
     */
    public int getId();
    
    /**
     * Method to get the date the process was started
     * @return The start date
     */
    public Date getStartDate();

    /**
     * Method to get the UserContext of the user who started the process
     * @return The user context
     */
    public YukonUserContext getUserContext();

    /**
     * Method to get the result holder for the process
     * @return The result holder
     */
    public BulkProcessingResultHolder getResultHolder();

    /**
     * Method to get the description of the  action this bulk process is performing
     * @return Action description
     */
    public MessageSourceResolvable getActionMessage();

    /**
     * Method to get the description of the device collection this process is processing
     * @return Device Collection description
     */
    public MessageSourceResolvable getDeviceCollectionMessage();
    
    /**
     * Method to get the device collection this process is processing
     * @return Device Collection
     */
    public DeviceCollection getDeviceCollection();

}
