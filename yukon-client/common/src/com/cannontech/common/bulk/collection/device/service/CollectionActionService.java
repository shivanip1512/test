package com.cannontech.common.bulk.collection.device.service;

import java.util.LinkedHashMap;

import org.joda.time.Instant;

import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface CollectionActionService {

    CollectionActionResult getResult(int key);


    CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, CommandRequestType commandRequestType, DeviceRequestType deviceRequestType,
            YukonUserContext context);
    
    CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, YukonUserContext context);
    /**
     * Attempts to cancel execution.
     */
    void cancel(int key, LiteYukonUser user);
    
    void updateResult(CollectionActionResult result, CommandRequestExecutionStatus status);

    
    
    
    
    
    
    
    
    
    
    
    /**
     * Takes X number of random meters and builds collection result.
     * 
     * @param numberOfDevices - number of devices user entered 
     * @param action - Read Attribute etc
     * @return pre-populated result
     */

    CollectionActionResult getRandomResult(int numberOfDevices, LinkedHashMap<String, String> userInputs,
            Instant stopTime, CommandRequestExecutionStatus status, CollectionAction action);

    void loadLotsOfDataForNonCreCollectionActions(LinkedHashMap<String, String> userInputs);

    void printResult(CollectionActionResult result);

    void compareCacheAndGB(int key);

    







}
