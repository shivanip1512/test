package com.cannontech.common.bulk.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;

public interface ChangeDeviceTypeService {

    class ChangeDeviceTypeInfo{
        private int address;
        private int routeId;
        private String model;
        private String manufacturer;
        private String serialNumber;
        
        public int getAddress() {
            return address;
        }
        public void setAddress(int address) {
            this.address = address;
        }
        public String getModel() {
            return model;
        }
        public void setModel(String model) {
            this.model = model;
        }
        public String getManufacturer() {
            return manufacturer;
        }
        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }
        public String getSerialNumber() {
            return serialNumber;
        }
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
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
