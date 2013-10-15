package com.cannontech.dr.assetavailability;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum AssetAvailabilityCombinedStatus implements DisplayableEnum {
    ACTIVE(AssetAvailabilityStatus.ACTIVE),
    INACTIVE(AssetAvailabilityStatus.INACTIVE),
    UNAVAILABLE(AssetAvailabilityStatus.UNAVAILABLE),
    OPTED_OUT(null);

    private final AssetAvailabilityStatus backingStatus;
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

    private AssetAvailabilityCombinedStatus(AssetAvailabilityStatus backingStatus) {
        this.backingStatus = backingStatus;
    }

    public String getValue() {
        return name();
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
}


