/**
 * 
 */
package com.cannontech.common.device.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;

public class DisplayableDevice extends DisplayablePaoBase {

    public DisplayableDevice(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }
    
    public int getId() {
        return this.getPaoIdentifier().getPaoId();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}