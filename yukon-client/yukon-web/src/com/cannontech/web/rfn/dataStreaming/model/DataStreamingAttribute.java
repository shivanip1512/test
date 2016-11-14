package com.cannontech.web.rfn.dataStreaming.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingMetricStatus;

public class DataStreamingAttribute {
    private BuiltInAttribute attribute;
    private Boolean attributeOn = Boolean.FALSE;
    private int interval;
    private DataStreamingMetricStatus status;
    
    public Boolean getAttributeOn() {
        return attributeOn;
    }
    
    public void setAttributeOn(Boolean attributeOn) {
        this.attributeOn = attributeOn;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((attributeOn == null) ? 0 : attributeOn.hashCode());
        result = prime * result + interval;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataStreamingAttribute other = (DataStreamingAttribute) obj;
        if (attribute != other.attribute) {
            return false;
        }
        if (attributeOn == null) {
            if (other.attributeOn != null) {
                return false;
            }
        } else if (!attributeOn.equals(other.attributeOn)) {
            return false;
        }
        if (interval != other.interval) {
            return false;
        }
        return true;
    }
    
    public DataStreamingMetricStatus getStatus() {
        return status;
    }

    public void setStatus(DataStreamingMetricStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("attribute", attribute);
        builder.append("status", status);
        builder.append("attributeOn", attributeOn);
        return builder.toString();
    }    
}
