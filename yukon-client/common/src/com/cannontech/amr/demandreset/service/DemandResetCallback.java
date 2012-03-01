package com.cannontech.amr.demandreset.service;

import java.util.Map;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.google.common.collect.Maps;


public interface DemandResetCallback {
    public static class Results {
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

    public void completed(Results results);
}
