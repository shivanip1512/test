package com.cannontech.web.tools.points.service.impl;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;

public class PointBaseModelFactory {

    public final static PointBaseModel<? extends PointBase> createPointBaseModel(PointType pointType) {

        PointBaseModel<? extends PointBase> baseModel = null;
        switch (pointType) {
            case Analog:
                baseModel = new AnalogPointModel();
                break;
            case CalcAnalog:
                break;
            case CalcStatus:
                break;
            case DemandAccumulator:
                break;
            case PulseAccumulator:
                break;
            case Status:
                break;
            case System:
                break;
            default:
                break;

        }

        return baseModel;
    }

    public final static PointBase createPointBase(PointBaseModel baseModel) {

        PointBase pointBase = null;
        switch (baseModel.getPointType()) {
            case Analog:
                ScalarPointModel<?> analogPointModel = (ScalarPointModel<?>) baseModel;
                pointBase = PointFactory.createAnalogPoint(baseModel.getPointName(),
                                                           baseModel.getPaoId(),
                                                           baseModel.getPointId(),
                                                           baseModel.getPointOffset(),
                                                           analogPointModel.getPointUnit().getUomID(),
                                                           -1);
                break;
            case CalcAnalog:
                break;
            case CalcStatus:
                break;
            case DemandAccumulator:
                break;
            case PulseAccumulator:
                break;
            case Status:
                break;
            case System:
                break;
            default:
                break;

        }

        return pointBase;
    }

}
