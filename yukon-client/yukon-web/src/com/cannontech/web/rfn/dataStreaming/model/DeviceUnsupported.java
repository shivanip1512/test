package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DeviceUnsupported {
    
    private static final String nameKey= "yukon.web.modules.tools.bulk.dataStreaming.verification.devicesUnsupported";

    private List<Integer> deviceIds = new ArrayList<Integer>();
    private List<BuiltInAttribute> attributes = new ArrayList<BuiltInAttribute>();
    private String detail;
    private DeviceCollection deviceCollection;
    private MessageSourceAccessor accessor;
    private boolean allAttributes;
    
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
    public void setAccessor(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }
    public String getDetail() {
        String allAttributesText = accessor.getMessage(nameKey + "AnyAttributes");
        List<String> attList = new ArrayList<String>();
        getAttributes().forEach(attribute -> attList.add(attribute.getDescription()));
        detail = accessor.getMessage(nameKey, deviceIds.size(), allAttributes ? allAttributesText : String.join(", ", attList));
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
    public boolean getAllAttributes() {
        return allAttributes;
    }
    public void setAllAttributes(boolean allAttributes) {
        this.allAttributes = allAttributes;
    }
}
