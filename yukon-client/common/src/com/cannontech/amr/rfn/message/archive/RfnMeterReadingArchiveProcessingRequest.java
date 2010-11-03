package com.cannontech.amr.rfn.message.archive;

import java.io.Serializable;

import com.cannontech.amr.rfn.model.RfnMeter;

/**
 * JMS Queue name: yukon.rr.obj.amr.rfn.MeterReadingArchiveProcessingRequest
 */
public class RfnMeterReadingArchiveProcessingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnMeterReadingArchiveRequest originalRequest;
    private RfnMeter meter;
    
    public RfnMeterReadingArchiveRequest getOriginalRequest() {
        return originalRequest;
    }
    
    public void setOriginalRequest(RfnMeterReadingArchiveRequest originalRequest) {
        this.originalRequest = originalRequest;
    }
    
    public RfnMeter getMeter() {
        return meter;
    }
    
    public void setMeter(RfnMeter meter) {
        this.meter = meter;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meter == null) ? 0 : meter.hashCode());
        result = prime * result + ((originalRequest == null) ? 0 : originalRequest.hashCode());
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
        RfnMeterReadingArchiveProcessingRequest other = (RfnMeterReadingArchiveProcessingRequest) obj;
        if (meter == null) {
            if (other.meter != null)
                return false;
        } else if (!meter.equals(other.meter))
            return false;
        if (originalRequest == null) {
            if (other.originalRequest != null)
                return false;
        } else if (!originalRequest.equals(other.originalRequest))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("RfnMeterReadingArchiveProcessingRequest [meter=%s, originalRequest=%s]",
                             meter,
                             originalRequest);
    }
    

}