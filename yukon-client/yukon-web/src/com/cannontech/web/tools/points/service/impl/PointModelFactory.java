package com.cannontech.web.tools.points.service.impl;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;

/**
 * The purpose of this class is to create PointBaseModel and Populating default PointBase object based on mandatory
 * fields for different point type.
 */
public class PointModelFactory {

    /**
     * Create PointBaseModel based on point Type.
     */
    public final static PointBaseModel<? extends PointBase> getModel(PointType pointType) {

        PointBaseModel<? extends PointBase> pointModel = null;
        switch (pointType) {
            case Analog:
                pointModel = new AnalogPointModel();
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

        return pointModel;
    }

    /**
     * Populating default PointBase object based on mandatory fields present in PointBaseModel for different point
     * types.
     */
    public final static PointBase createPoint(PointBaseModel baseModel) {

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
