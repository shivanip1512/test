package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DeviceUnsupported {

    private List<Integer> deviceIds = new ArrayList<Integer>();
    private List<BuiltInAttribute> attributes = new ArrayList<BuiltInAttribute>();
    private String detail;
    private DeviceCollection deviceCollection;
    
    
    public List<Integer> getDeviceIds() {
        return deviceIds;
    }
    public void setDeviceIds(List<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }
    public List<BuiltInAttribute> getAttributes() {
        return attributes;
    }
    public void setAttributes(List<BuiltInAttribute> attributes) {
        this.attributes = attributes;
    }
    public String getDetail() {
        detail = deviceIds.size() + " devices do not support " + getAttributes().get(0).getDescription();
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }
}
