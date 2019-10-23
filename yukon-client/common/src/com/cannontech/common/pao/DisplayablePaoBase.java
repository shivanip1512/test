package com.cannontech.common.pao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DisplayablePaoBase implements DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private String name;

    public DisplayablePaoBase(PaoIdentifier paoIdentifier, String name) {
        this.paoIdentifier = paoIdentifier;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
