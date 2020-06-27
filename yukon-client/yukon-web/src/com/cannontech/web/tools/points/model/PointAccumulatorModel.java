package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.point.PointAccumulator;

public class PointAccumulatorModel implements DBPersistentConverter<PointAccumulator> {

    private Double multiplier;
    private Double dataOffset;

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Double getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(Double dataOffset) {
        this.dataOffset = dataOffset;
    }

    @Override
    public void buildModel(PointAccumulator pointAccumulator) {
        setDataOffset(pointAccumulator.getDataOffset());
        setMultiplier(pointAccumulator.getMultiplier());
    }

    @Override
    public void buildDBPersistent(PointAccumulator pointAccumulator) {

        if (getDataOffset() != null) {
            pointAccumulator.setDataOffset(getDataOffset());
        }

        if (getMultiplier() != null) {
            pointAccumulator.setMultiplier(getMultiplier());
        }

    }

}
