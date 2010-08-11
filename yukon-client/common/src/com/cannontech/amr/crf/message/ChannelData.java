package com.cannontech.amr.crf.message;

import java.io.Serializable;
import java.util.Set;

public class ChannelData implements Serializable {
    private static final long serialVersionUID = 2L;

    private int channelNumber;
    private ChannelDataStatus status;
    private String unitOfMeasure;
    private Set<String> unitOfMeasureModifiers;
    private double value;

    public int getChannelNumber() {
        return channelNumber;
    }

    public ChannelDataStatus getStatus() {
        return status;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Set<String> getUnitOfMeasureModifiers() {
        return unitOfMeasureModifiers;
    }

    public double getValue() {
        return value;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public void setStatus(ChannelDataStatus status) {
        this.status = status;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setUnitOfMeasureModifiers(Set<String> unitOfMeasureModifiers) {
        this.unitOfMeasureModifiers = unitOfMeasureModifiers;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + channelNumber;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
        result = prime * result + ((unitOfMeasureModifiers == null) ? 0
                : unitOfMeasureModifiers.hashCode());
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        ChannelData other = (ChannelData) obj;
        if (channelNumber != other.channelNumber)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (unitOfMeasure == null) {
            if (other.unitOfMeasure != null)
                return false;
        } else if (!unitOfMeasure.equals(other.unitOfMeasure))
            return false;
        if (unitOfMeasureModifiers == null) {
            if (other.unitOfMeasureModifiers != null)
                return false;
        } else if (!unitOfMeasureModifiers.equals(other.unitOfMeasureModifiers))
            return false;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("ChannelData [channelNumber=%s, unitOfMeasure=%s, unitOfMeasureModifiers=%s, value=%s, status=%s]",
                             channelNumber,
                             unitOfMeasure,
                             unitOfMeasureModifiers,
                             value,
                             status);
    }

}
