package com.cannontech.common.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PointQuality implements DatabaseRepresentationSource {
    
    Uninitialized(0, "Uninitialized", "Uninit"),
    @Deprecated InitDefault(1, "Init Default", "Init-Def"),	//not used JOtteson 6/30/2009
    @Deprecated InitLastKnown(2, "Init Last Known", "Init-Last"),	//not used JOtteson 6/30/2009
    NonUpdated(3, "Non Updated", "Non"),
    Manual(4, "Manual", "Man"),
    Normal(5, "Normal", "Norm"),
    @Deprecated ExceedsLow(6, "Exceeds Low", "Exc-Low"),	//not used JOtteson 6/30/2009
    @Deprecated ExceedsHigh(7, "Exceeds High", "Exc-High"),	//not used JOtteson 6/30/2009
    Abnormal(8, "Abnormal"),
    Unknown(9, "Unknown"),
    Invalid(10, "Invalid", "Inv"),
    PartialInterval(11, "Partial Interval", "Part-Int"),
    DeviceFiller(12, "Device Filler", "Dev-Fill"),
    Questionable(13, "Questionable", "Quest"),
    Overflow(14, "Overflow"),
    Powerfail(15, "Power Fail", "Pow-Fail"),
    Unreasonable(16, "Unreasonable", "Unr"),
    Constant(17, "Constant", "Const"),
    Estimated(18, "Estimated", "Est");

    private final int quality;
    private final String description;
    private final String abbreviation;

    PointQuality(int quality, String desc) {
        this.quality = quality;
        this.description = desc;
        this.abbreviation = desc; 
    }

    PointQuality(int quality, String desc, String abbr) {
        this.quality = quality;
        this.description = desc;
        this.abbreviation = abbr;
    }
    public final int getQuality() {
		return quality;
	}
    
    @Override
    public Object getDatabaseRepresentation() {
        return quality;
    }
    
    public final String getDescription() {
		return description;
	}
    
    public final String getAbbreviation() {
		return abbreviation;
	}
    
    public static PointQuality getPointQuality(int value) {
    	for (PointQuality pointQuality : values()) {
			if (pointQuality.quality == value) {
				return pointQuality;
			}
		}
    	throw new IllegalArgumentException();
    }
}