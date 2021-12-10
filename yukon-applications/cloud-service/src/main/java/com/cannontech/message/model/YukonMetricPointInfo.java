package com.cannontech.message.model;

public enum YukonMetricPointInfo {
    RFN_METER_READING_ARCHIVERE_REQUEST_RECEIVED(1032, PointType.Analog),
    RFN_METER_READING_ARCHIVERE_REQUEST_PUSHED(1033, PointType.Analog);

    private Integer offset;
    private PointType type;

    YukonMetricPointInfo(Integer offset, PointType type) {
        this.setOffset(offset);
        this.setType(type);
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

}
