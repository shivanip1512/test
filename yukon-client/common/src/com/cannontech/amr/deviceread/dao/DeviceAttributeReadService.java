package com.cannontech.amr.deviceread.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This is the preferred Service for "reading" PAOs. This service, unlike the ones
 * that proceeded it, correctly handles RFN devices. Additionally,
 * it maintains all of the information about the attribute so that the callback can 
 * easily determine what the PointValueHolders that are being returned are for.
 */
public interface DeviceAttributeReadService {
    
    /**
     * This method will return true if at least one attribute of at least one
     * of the meters could be read in the absence of communication problems.
     * 
     * This will return false if either the devices or attributes collection is empty.
     */
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes, LiteYukonUser user);
       
    public void initiateRead(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
                             DeviceAttributeReadCallback callback,
                             DeviceRequestType type,
                             LiteYukonUser user);

    /**
     * This method will attempt to read device collection
     */

    public String initiateRead(DeviceCollection deviceCollection,
                               Set<? extends Attribute> attributes,
                               DeviceRequestType type,
                               final SimpleCallback<GroupMeterReadResult> callback,
                               LiteYukonUser user);
    
    /**
     * This method will attempt to read device and wait for the result
     */
    DeviceReadResult initiateReadAndWait(YukonDevice device, Set<? extends Attribute> toRead,
                                                              DeviceRequestType requestType, LiteYukonUser user);
    
    
    // The methods below this line represent a distinct part of this service.
    // The String returned by the first method can be used to look up the 
    // result object that is stored in memory.
    
   
    public List<GroupMeterReadResult> getCompleted();
    public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type);

    public List<GroupMeterReadResult> getPending();
    public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type);

    public GroupMeterReadResult getResult(String id);
}