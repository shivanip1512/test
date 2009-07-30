package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.model.SimpleDevice;

public class PaoPointTemplate {
    public PaoPointTemplate(SimpleDevice yukonDevice,
            PointTemplate pointTemplate) {
        super();
        this.yukonDevice = yukonDevice;
        this.pointTemplate = pointTemplate;
    }
    private SimpleDevice yukonDevice;
    private PointTemplate pointTemplate;
    
    public SimpleDevice getYukonDevice() {
        return yukonDevice;
    }
    public void setYukonDevice(SimpleDevice yukonDevice) {
        this.yukonDevice = yukonDevice;
    }
    public PointTemplate getPointTemplate() {
        return pointTemplate;
    }
    public void setPointTemplate(PointTemplate pointTemplate) {
        this.pointTemplate = pointTemplate;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pointTemplate == null) ? 0
                : pointTemplate.hashCode());
        result = prime * result + ((yukonDevice == null) ? 0
                : yukonDevice.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaoPointTemplate other = (PaoPointTemplate) obj;
        if (pointTemplate == null) {
            if (other.pointTemplate != null)
                return false;
        } else if (!pointTemplate.equals(other.pointTemplate))
            return false;
        if (yukonDevice == null) {
            if (other.yukonDevice != null)
                return false;
        } else if (!yukonDevice.equals(other.yukonDevice))
            return false;
        return true;
    }
    
    
    
}
