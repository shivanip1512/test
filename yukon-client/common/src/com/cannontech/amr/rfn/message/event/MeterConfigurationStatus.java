package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;

public class MeterConfigurationStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private MeterStatusCode meterStatusCode;
    private DetailedConfigurationStatusCode detailedConfigurationStatusCode;

    public MeterStatusCode getMeterStatusCode() {
        return meterStatusCode;
    }

    public void setMeterStatusCode(MeterStatusCode meterStatusCode) {
        this.meterStatusCode = meterStatusCode;
    }

    public DetailedConfigurationStatusCode getDetailedConfigurationStatusCode() {
        return detailedConfigurationStatusCode;
    }

    public void setDetailedConfigurationStatusCode(DetailedConfigurationStatusCode detailedConfigurationStatusCode) {
        this.detailedConfigurationStatusCode = detailedConfigurationStatusCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detailedConfigurationStatusCode == null) ? 0
                : detailedConfigurationStatusCode.hashCode());
        result = prime * result + ((meterStatusCode == null) ? 0 : meterStatusCode.hashCode());
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
        MeterConfigurationStatus other = (MeterConfigurationStatus) obj;
        if (detailedConfigurationStatusCode == null) {
            if (other.detailedConfigurationStatusCode != null)
                return false;
        } else if (!detailedConfigurationStatusCode.equals(other.detailedConfigurationStatusCode))
            return false;
        if (meterStatusCode == null) {
            if (other.meterStatusCode != null)
                return false;
        } else if (!meterStatusCode.equals(other.meterStatusCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("MeterConfigurationStatus [meterStatusCode=%s, detailedConfigurationStatusCode=%s]",
                    meterStatusCode,
                    detailedConfigurationStatusCode);
    }
}
