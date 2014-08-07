package com.cannontech.amr.deviceread.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PlcDeviceAttributeReadService {
    
    
    /**
     * Initiates a simple read for the attributes and returns the CommandResultHolder that
     * corresponds to the Command Requests that were submitted.
     */
    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes, DeviceRequestType type, LiteYukonUser user);
    
    /**
     * This method only exists to support old code that hasn't been converted
     * to the DeviceAttributeReadService.
     *
     * Note: this method works only on PLC devices. This method supports cancellations and retries while
     * {@link DeviceAttributeReadService#readDeviceCollection()} does not.
     * 
     * @deprecated Please use {@link DeviceAttributeReadService#readDeviceCollection()} instead.
     */
    @Deprecated
    public CommandRequestExecutionObjects<CommandRequestDevice> backgroundReadDeviceCollection(DeviceCollection deviceCollection, 
                                                                           Set<? extends Attribute> attributes, 
                                                                           DeviceRequestType type, 
                                                                           CommandCompletionCallback<CommandRequestDevice> callback, 
                                                                           LiteYukonUser user,
                                                                           RetryParameters retryParameters);

    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user);
    
    /**
     * This method serves two purposes: it supports the above method and it is used internally
     * by the DeviceAttributeReadService's PLC strategy. When the above method is no longer 
     * needed, this implementation could probably be moved to DeviceAttributeReadPlcStrategy.
     * @param points
     * @param type
     * @param callback
     * @param user
     * @param retryParameters
     * @return
     */
    public CommandRequestExecutionObjects<CommandRequestDevice> backgroundReadDeviceCollection(final Iterable<PaoMultiPointIdentifier> points, 
                                                                           DeviceRequestType type, 
                                                                           CommandCompletionCallback<CommandRequestDevice> callback, 
                                                                           LiteYukonUser user,
                                                                           RetryParameters retryParameters);
    
    public boolean isReadable(YukonPao device, Set<? extends Attribute> attribute, LiteYukonUser user);
    
    
    // The methods below this line represent a distinct part of this service.
    // The String returned by the first method can be used to look up the 
    // result object that is stored in memory.
    
    public String addResult(GroupMeterReadResult groupMeterReadResult);
        
    public List<GroupMeterReadResult> getCompleted();
    public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type);

    public List<GroupMeterReadResult> getPending();
    public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type);

    public GroupMeterReadResult getResult(String id);

}
