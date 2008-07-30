package com.cannontech.common.point;
import static com.cannontech.database.data.point.PointQualities.*;


public enum PointQuality {
    Unintialized(UNINTIALIZED_QUALITY),
    InitDefault(INIT_DEFAULT_QUALITY),
    InitLastKnown(INIT_LAST_KNOWN_QUALITY),
    NonUpdated(NON_UPDATED_QUALITY),
    Manual(MANUAL_QUALITY),
    Normal(NORMAL_QUALITY),
    ExceedsLow(EXCEEDS_LOW_QUALITY),
    ExceedsHigh(EXCEEDS_HIGH_QUALITY),
    Abnormal(ABNORMAL_QUALITY),
    Unknown(UNKNOWN_QUALITY),
    Invalid(INVALID_QUALITY),
    PartialInterval(PARTIAL_INTERVAL_QUALITY),
    DeviceFiller(DEVICE_FILLER_QUALITY),
    Questionable(QUESTIONABLE_QUALITY),
    Overflow(OVERFLOW_QUALITY),
    Powerfail(POWERFAIL_QUALITY),
    Unreasonable(UNREASONABLE_QUALITY),
    Constant(CONSTANT_QUALITY),
    Estimated(ESTIMATED_QUALITY);
    
    private final int value;

    PointQuality(int num) {
        value = num;
    }

    public int getPointQualityValue() {
        return value;
    }
    
    public static PointQuality getPointQuality(int value) {
    	for (PointQuality quality : values()) {
			if (quality.value == value) {
				return quality;
			}
		}
    	throw new IllegalArgumentException();
    }
}

