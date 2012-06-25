package com.cannontech.amr.demandreset.service;

import java.util.Map;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.google.common.collect.Maps;


public interface DemandResetCallback {
    class Results {
        private Map<SimpleDevice, SpecificDeviceErrorDescription> errors;

        public Results() {
            errors = Maps.newHashMap();
        }

        public Results(Map<SimpleDevice, SpecificDeviceErrorDescription> errors) {
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
    }

    /**
     * Called when the request is made.  This callback method should be called relatively quickly
     * but will only include basic errors.  This method will be called once for all devices.
     */
    void initiated(Results results);

    /**
     * This will be called on a per device basis for devices for which the demand reset has been
     * verified to have occurred.  This will be called when it is known for sure that the reset
     * happened.
     */
    void verified(SimpleDevice device);

    /**
     * This method will be called on a per device basis when it is known that the demand reset
     * failed.
     */
    void failed(SimpleDevice device);

    /**
     * This method will be called on a per device basis for devices for which it cannot be
     * determined if the reset succeeded or failed.  Often this is the result of a timeout but
     * it can also be caused by missing points.
     */
    void cannotVerify(SimpleDevice device, String reason);
}
