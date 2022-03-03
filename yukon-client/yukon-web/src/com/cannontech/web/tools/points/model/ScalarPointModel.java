package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.web.tools.points.model.PointUnit;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ScalarPointModel<T extends ScalarPoint> extends PointBaseModel<T> {

    private List<PointLimitModel> limits;
    private PointUnit pointUnit;

    public List<PointLimitModel> getLimits() {
        if (limits == null) {
            limits = new ArrayList<PointLimitModel>();
        }
        return limits;
    }

    public void setLimits(List<PointLimitModel> limits) {
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

        getPointUnit().buildDBPersistent(point.getPointUnit());

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
        getLimits().clear();
        point.getPointLimitsMap().values().forEach(pointLimit -> {
            PointLimitModel pointLimitModel = new PointLimitModel();
            pointLimitModel.buildModel(pointLimit);
            getLimits().add(pointLimitModel);
        });

        super.buildModel(point);
    }

}
