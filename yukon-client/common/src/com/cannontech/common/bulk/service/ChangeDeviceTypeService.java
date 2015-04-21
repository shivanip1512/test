package com.cannontech.common.bulk.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;

public interface ChangeDeviceTypeService {

    class ChangeDeviceTypeInfo{
        private int address;
        private int routeId;
        private RfnIdentifier rfnIdentifier;
        
        public int getAddress() {
            return address;
        }
        public void setAddress(int address) {
            this.address = address;
        }
        
        public RfnIdentifier getRfnIdentifier() {
            return rfnIdentifier;
        }
        
        public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
            this.rfnIdentifier = rfnIdentifier;
        }
        
        public int getRouteId() {
            return routeId;
        }
        public void setRouteId(int routeId) {
            this.routeId = routeId;
        }
    }
    
    SimpleDevice changeDeviceType(SimpleDevice device, PaoType newDeviceType, ChangeDeviceTypeInfo info);
}
