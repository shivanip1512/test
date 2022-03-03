package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.AccumulatorPoint;

public class AccumulatorPointModel extends ScalarPointModel<AccumulatorPoint> {

    private PointAccumulatorModel accumulatorPoint;

    public PointAccumulatorModel getAccumulatorPoint() {
        if (accumulatorPoint == null) {
            accumulatorPoint = new PointAccumulatorModel();
        }
        return accumulatorPoint;
    }

    public void setAccumulatorPoint(PointAccumulatorModel accumulatorPoint) {
        this.accumulatorPoint = accumulatorPoint;
    }

    @Override
    public void buildDBPersistent(AccumulatorPoint accumulatorPoint) {
        super.buildDBPersistent(accumulatorPoint);
        getAccumulatorPoint().buildDBPersistent(accumulatorPoint.getPointAccumulator());
    }

    @Override
    public void buildModel(AccumulatorPoint accumulatorPoint) {
        super.buildModel(accumulatorPoint);
        getAccumulatorPoint().buildModel(accumulatorPoint.getPointAccumulator());
    }

}
