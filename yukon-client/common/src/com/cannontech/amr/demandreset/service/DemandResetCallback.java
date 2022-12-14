package com.cannontech.amr.demandreset.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionStrategyCompletionCallback;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Maps;


public interface DemandResetCallback extends CollectionActionStrategyCompletionCallback{
    class Results {
        
        //all devices the request was sent to
        private final Set<? extends YukonPao> allDevices;
        private final Map<SimpleDevice, SpecificDeviceErrorDescription> errors;

        public Results(Set<? extends YukonPao> devices) {
            this.allDevices = devices;
            errors = Maps.newHashMap();
        }

        public Results(Set<? extends YukonPao> paos, Map<SimpleDevice, SpecificDeviceErrorDescription> errors) {
            this.allDevices = paos;
            this.errors = errors;
        }

        public void append(Results other) {
            errors.putAll(other.errors);
        }

        public int getNumErrors() {
            return errors.size();
        }

        public Map<SimpleDevice, SpecificDeviceErrorDescription> getErrors() {
            return errors;
        }

        public Set<? extends YukonPao> getAllDevices() {
            return allDevices;
        }
    }

    /**
     * Called when the request is made. This callback method should be called relatively quickly
     * but will only include basic errors. This method will be called once per strategy.
     */
    default void initiated(Results results, StrategyType type) {

    }

    /**
     * Combined results for all strategies
     */
    default void initiated(Results results) {

    }

    /**
     * This will be called on a per device basis for devices for which the demand reset has been
     * verified to have occurred.  This will be called when it is known for sure that the reset
     * happened. 
     */
    default void verified(SimpleDevice device, PointValueHolder value) {
        
    }

    /**
     * This method will be called on a per device basis when it is known that the demand reset
     * failed.
     */
    default void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
        
    }

    /**
     * This method will be called on a per device basis for devices for which it cannot be
     * determined if the reset succeeded or failed.  Often this is the result of a timeout but
     * it can also be caused by missing points.
     */
    default void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
        
    }
    
    default CollectionActionResult getResult() {
        return null;
    }
    
    /**
     * This method should be called if an error occurred that will
     * prevent the command from being sent to any devices (example: no porter connection)
     */
    default void processingExceptionOccurred(String reason) {
        
    }
}
