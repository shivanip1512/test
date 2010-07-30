package com.cannontech.amr.crf.message;

import java.io.Serializable;

import com.cannontech.amr.crf.model.CrfMeterIdentifier;

public class CrfMeterReadRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private CrfMeterIdentifier crfMeterIdentifier;
    
    public CrfMeterReadRequest(CrfMeterIdentifier meter) {
        setCrfMeterIdentifier(meter);
    }
  
    public CrfMeterIdentifier getCrfMeterIdentifier() {
        return crfMeterIdentifier;
    }

    public void setCrfMeterIdentifier(CrfMeterIdentifier crfMeterIdentifier) {
        this.crfMeterIdentifier = crfMeterIdentifier;
    }

    @Override
    public String toString() {
        return String.format("CrfMeterReadRequestMessage [crfMeterIdentifier=%s]", crfMeterIdentifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((crfMeterIdentifier == null) ? 0 : crfMeterIdentifier.hashCode());
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
        CrfMeterReadRequest other = (CrfMeterReadRequest) obj;
        if (crfMeterIdentifier == null) {
            if (other.crfMeterIdentifier != null)
                return false;
        } else if (!crfMeterIdentifier.equals(other.crfMeterIdentifier))
            return false;
        return true;
    }
}