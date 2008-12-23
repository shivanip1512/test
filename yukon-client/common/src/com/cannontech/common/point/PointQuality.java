package com.cannontech.common.point;

public enum PointQuality {
    Uninitialized(0, "Uninitialized", "Uninit"),
    InitDefault(1, "Init Default", "Init-Def"),
    InitLastKnown(2, "Init Last Known", "Init-Last"),
    NonUpdated(3, "Non Updated", "Non"),
    Manual(4, "Manual", "Man"),
    Normal(5, "Normal", "Norm"),
    ExceedsLow(6, "Exceeds Low", "Exc-Low"),
    ExceedsHigh(7, "Exceeds High", "Exc-High"),
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