package com.cannontech.amr.paoPointValue.model;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;

public class PaoPointValue implements YukonPao {

    private Meter meter;
    private PaoPointIdentifier paoPointIdentifier;
    private BuiltInAttribute attribute;
    private PointValueHolder pointValueHolder;
    private String pointName;
    private String formattedValue;

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public void setPaoPointIdentifier(PaoPointIdentifier paoPointIdentifier) {
        this.paoPointIdentifier = paoPointIdentifier;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public PointValueHolder getPointValueHolder() {
        return pointValueHolder;
    }

    public void setPointValueHolder(PointValueHolder pointValueHolder) {
        this.pointValueHolder = pointValueHolder;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoPointIdentifier.getPaoIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((formattedValue == null) ? 0 : formattedValue.hashCode());
        result = prime * result + ((meter == null) ? 0 : meter.hashCode());
        result =
            prime * result + ((paoPointIdentifier == null) ? 0 : paoPointIdentifier.hashCode());
        result = prime * result + ((pointName == null) ? 0 : pointName.hashCode());
        result = prime * result + ((pointValueHolder == null) ? 0 : pointValueHolder.hashCode());
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
        PaoPointValue other = (PaoPointValue) obj;
        if (attribute != other.attribute)
            return false;
        if (formattedValue == null) {
            if (other.formattedValue != null)
                return false;
        } else if (!formattedValue.equals(other.formattedValue))
            return false;
        if (meter == null) {
            if (other.meter != null)
                return false;
        } else if (!meter.equals(other.meter))
            return false;
        if (paoPointIdentifier == null) {
            if (other.paoPointIdentifier != null)
                return false;
        } else if (!paoPointIdentifier.equals(other.paoPointIdentifier))
            return false;
        if (pointName == null) {
            if (other.pointName != null)
                return false;
        } else if (!pointName.equals(other.pointName))
            return false;
        if (pointValueHolder == null) {
            if (other.pointValueHolder != null)
                return false;
        } else if (!pointValueHolder.equals(other.pointValueHolder))
            return false;
        return true;
    }
}
