package com.cannontech.message.porter.message;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MeterProgramValidationRequest {
    
    private UUID meterProgramGuid;

    public MeterProgramValidationRequest(UUID meterProgramGuid) {
        this.meterProgramGuid = meterProgramGuid;
    }
    
    public UUID getMeterProgramGuid() {
        return meterProgramGuid;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}