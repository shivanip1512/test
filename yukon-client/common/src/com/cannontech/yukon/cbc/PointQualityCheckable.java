package com.cannontech.yukon.cbc;

public interface PointQualityCheckable {
    public enum PointQuality {
        UnintializedQuality(0),

        InitDefaultQuality(1),

        InitLastKnownQuality(2),

        NonUpdatedQuality(3),

        ManualQuality(4),

        NormalQuality(5),

        ExceedsLowQuality(6),

        ExceedsHighQuality(7),

        AbnormalQuality(8),

        UnknownQuality(9),

        InvalidQuality(10),

        PartialIntervalQuality(11),

        DeviceFillerQuality(12),

        QuestionableQuality(13),

        OverflowQuality(14),

        PowerfailQuality(15),

        UnreasonableQuality(16),

        ConstantQuality(17);
        private final int value;

        PointQuality(int num) {
            value = num;
        }

        public int value() {
            return value;
        }

    }

    Integer getCurrentPtQuality(int pointType);

}
