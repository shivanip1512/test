package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointUnit;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ScalarPointModel<T extends ScalarPoint> extends PointBaseModel<T> {

    private List<PointLimit> limits;
    private PointUnit pointUnit;

    public List<PointLimit> getLimits() {
        if (limits == null) {
            limits = new ArrayList<PointLimit>();
        }
        return limits;
    }

    public void setLimits(List<PointLimit> limits) {
        this.limits = limits;
    }

    public PointUnit getPointUnit() {
        if (pointUnit == null) {
            pointUnit = new PointUnit();
        }
        return pointUnit;
    }

    public void setPointUnit(PointUnit pointUnit) {
        this.pointUnit = pointUnit;
    }

    @Override
    public void buildDBPersistent(T point) {
        if (getPointUnit() != null) {
            getPointUnit().buildDBPersistent(point.getPointUnit());
        }

        if (CollectionUtils.isNotEmpty(getLimits())) {
            getLimits().forEach(pointLimitModel -> {
                Map<Integer, PointLimit> pointLimitsMap = point.getPointLimitsMap();
                PointLimit pointLimit = new PointLimit();
                pointLimitModel.buildDBPersistent(pointLimit);
                pointLimitsMap.put(pointLimitModel.getLimitNumber(), pointLimit);
            });
        }

        super.buildDBPersistent(point);
    }

    @Override
    public void buildModel(T point) {
        getPointUnit().buildModel(point.getPointUnit());

        point.getPointLimitsMap().values().forEach(pointLimit -> {
            PointLimit pointLimitModel = new PointLimit();
            pointLimitModel.buildModel(pointLimit);
            getLimits().add(pointLimitModel);
        });

        super.buildModel(point);
    }

}
