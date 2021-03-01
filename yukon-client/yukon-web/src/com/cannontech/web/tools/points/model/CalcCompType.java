package com.cannontech.web.tools.points.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum CalcCompType implements DatabaseRepresentationSource {
    
    OPERATION("Operation"),
    CONSTANT("Constant"),
    FUNCTION("Function");

    private final String calcCompType;
    private static final Logger log = YukonLogManager.getLogger(CalcCompType.class);
    private final static ImmutableMap<String, CalcCompType> lookupByCalcCompType;
    static {
        try {
            ImmutableMap.Builder<String, CalcCompType> commTypeBuilder = ImmutableMap.builder();
            for (CalcCompType calcCompType : values()) {
                commTypeBuilder.put(calcCompType.getCalcCompType(), calcCompType);
            }
            lookupByCalcCompType = commTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name.", e);
            throw e;
        }
    }
    public static CalcCompType getCalcCompType(String calcCompType) {
        CalcCompType compType = lookupByCalcCompType.get(calcCompType);
        checkArgument(compType != null, compType);
        return compType;
    }

    private CalcCompType(String calcCompType) {
        this.calcCompType = calcCompType;
    }

    public String getCalcCompType() {
        return calcCompType;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getCalcCompType();
    }
}
