package com.cannontech.amr.rfn.model;

import com.cannontech.common.pao.definition.model.PaoPointValue;

public final class CalculationData {

    private final int interval; //interval length in seconds
    private final PaoPointValue paoPointValue;
    
    private CalculationData(PaoPointValue paoPointValue, int interval) {
        this.paoPointValue = paoPointValue;
        this.interval = interval;
    }
    
    public PaoPointValue getPaoPointValue() {
        return paoPointValue;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public static CalculationData of(PaoPointValue ppv, int interval) {
        return new CalculationData(ppv, interval);
    }

    @Override
    public String toString() {
        return String.format("CalculationData [interval=%s, paoPointValue=%s]", interval, paoPointValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + interval;
        result = prime * result + ((paoPointValue == null) ? 0 : paoPointValue.hashCode());
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
        CalculationData other = (CalculationData) obj;
        if (interval != other.interval)
            return false;
        if (paoPointValue == null) {
            if (other.paoPointValue != null)
                return false;
        } else if (!paoPointValue.equals(other.paoPointValue))
            return false;
        return true;
    }
    
}