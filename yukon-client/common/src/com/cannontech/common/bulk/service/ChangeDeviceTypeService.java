package com.cannontech.common.bulk.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;

public interface ChangeDeviceTypeService {

    class ChangeDeviceTypeInfo{
        private int address;
        private int routeId;
        private RfnIdentifier rfnIdentifier;
        
        /** Use for CarrierBase types */
        public ChangeDeviceTypeInfo(int address, int routeId) {
            super();
            this.address = address;
            this.routeId = routeId;
        }

        /** Use for RfnBase types */
        public ChangeDeviceTypeInfo(RfnIdentifier rfnIdentifier) {
            super();
            this.rfnIdentifier = rfnIdentifier;
        }
        
        public int getAddress() {
            return address;
        }
        
        public RfnIdentifier getRfnIdentifier() {
            return rfnIdentifier;
        }
        
        public int getRouteId() {
            return routeId;
        }
    }
    
    SimpleDevice changeDeviceType(SimpleDevice device, PaoType newDeviceType, ChangeDeviceTypeInfo info);
}
