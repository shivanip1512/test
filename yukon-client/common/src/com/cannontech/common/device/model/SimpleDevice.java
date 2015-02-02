package com.cannontech.common.device.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.google.common.base.Function;

public final class SimpleDevice implements YukonDevice {
    
    private final PaoIdentifier paoIdentifier;
    
    public SimpleDevice(int deviceId, int type) {
        this(deviceId, PaoType.getForId(type));
    }
    
    public SimpleDevice(int deviceId, PaoType type) {
        paoIdentifier = new PaoIdentifier(deviceId, type);
    }
    
    public SimpleDevice(YukonPao pao) {
        PaoUtils.validateDeviceType(pao);
        paoIdentifier = pao.getPaoIdentifier();
    }
    
    public int getDeviceId() {
        return paoIdentifier.getPaoId();
    }
    
    public PaoType getDeviceType() {
        return paoIdentifier.getPaoType();
    }
    
    /**
     * @deprecated use {@link #getDeviceType()}
     */
    @Deprecated
    public int getType() {
        return paoIdentifier.getPaoType().getDeviceTypeId();
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    @Override
    public String toString() {
        return paoIdentifier.toString();
    }
    
    public static final Function<SimpleDevice, PaoType> TO_PAO_TYPE =
        new Function<SimpleDevice, PaoType>() {
            @Override
            public PaoType apply(SimpleDevice simpleDevice) {
                return simpleDevice.paoIdentifier.getPaoType();
            }
        };
        
    public static final Function<SimpleDevice, PaoIdentifier> TO_PAO_IDENTIFIER =
        new Function<SimpleDevice, PaoIdentifier>() {
            @Override
            public PaoIdentifier apply(SimpleDevice simpleDevice) {
                return simpleDevice.paoIdentifier;
            }
        };
        
    public static final Function<SimpleDevice, Integer> TO_PAO_ID = new Function<SimpleDevice, Integer>() {
        @Override
        public Integer apply(SimpleDevice simpleDevice) {
            return simpleDevice.paoIdentifier.getPaoId();
        }
    };
    
    @Override
    public int hashCode() {
        return paoIdentifier.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && paoIdentifier.equals(((SimpleDevice)obj).getPaoIdentifier());
    }
    
    public static SimpleDevice of(PaoIdentifier pao) {
        return new SimpleDevice(pao.getPaoId(), pao.getPaoType());
    }
}