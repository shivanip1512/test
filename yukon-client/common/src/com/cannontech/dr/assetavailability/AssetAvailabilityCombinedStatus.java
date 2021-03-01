package com.cannontech.dr.assetavailability;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.cannontech.common.YukonColorPalette;

public enum AssetAvailabilityCombinedStatus implements DisplayableEnum {
    ACTIVE(AssetAvailabilityStatus.ACTIVE, YukonColorPalette.GREEN),
    OPTED_OUT(null, YukonColorPalette.BLUE),
    INACTIVE(AssetAvailabilityStatus.INACTIVE, YukonColorPalette.ORANGE),
    UNAVAILABLE(AssetAvailabilityStatus.UNAVAILABLE, YukonColorPalette.GRAY);

    private final AssetAvailabilityStatus backingStatus;
    private YukonColorPalette color;

    private final static ImmutableMap<AssetAvailabilityStatus, AssetAvailabilityCombinedStatus> byBackingStatus;
    static {
        Builder<AssetAvailabilityStatus, AssetAvailabilityCombinedStatus> builder = ImmutableMap.builder();
        for (AssetAvailabilityCombinedStatus status : values()) {
            if (status != OPTED_OUT) {
                builder.put(status.backingStatus, status);
            }
        }
        byBackingStatus = builder.build();
    }

    private AssetAvailabilityCombinedStatus(AssetAvailabilityStatus backingStatus, YukonColorPalette color) {
        this.backingStatus = backingStatus;
        this.color = color;
    }

    public String getValue() {
        return name();
    }
    
    public YukonColorPalette getColor() {
        return color;
    }
    
    public String getHexColor() {
        return color.getHexValue();
    } 
    
    public AssetAvailabilityCombinedStatus getCombinedStatus() {
        return this;
    }

    public static AssetAvailabilityCombinedStatus valueOf(AssetAvailabilityStatus status, boolean optedOut) {
        if (optedOut) {
            return OPTED_OUT;
        }
        return byBackingStatus.get(status);
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.assetDetails.status." + name();
    }
    
    public static String getStringValues(AssetAvailabilityCombinedStatus[] array) {
        String assetAvailabilityCombinedStatus = null;
        if (null != array) {
            assetAvailabilityCombinedStatus = Joiner.on(',').join(array);
        }
        return assetAvailabilityCombinedStatus;
    }
    
}


