package com.cannontech.message.porter.message;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.amr.errors.dao.DeviceError;

public class MeterProgramValidationResponse implements Serializable {
    
    private UUID meterProgramGuid;
    private DeviceError status;

    public MeterProgramValidationResponse() { }
    
    public MeterProgramValidationResponse(UUID meterProgramGuid, DeviceError status) {
        this.meterProgramGuid = meterProgramGuid;
        this.status = status;
    }
    
    public UUID getMeterProgramGuid() {
        return meterProgramGuid;
    }
    
    public boolean isValid() {
        return status == DeviceError.SUCCESS;
    }
    
    public DeviceError getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}