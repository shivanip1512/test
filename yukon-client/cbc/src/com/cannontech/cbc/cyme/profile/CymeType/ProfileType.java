package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

public enum ProfileType implements CymeRepresentation{
    NETWORK,
    METER,
    GENERATOR,
    CUSTOMERTYPE,
    METEREDLOAD;
    

    static public ProfileType getFromCymeValue(String cymeValue) {
        
        for (ProfileType i : values()) {
            if (i.name().equals(cymeValue)) {
                return i;
            }
        }
        throw new NotFoundException("ProfileType was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return this.name();
    } 
}
