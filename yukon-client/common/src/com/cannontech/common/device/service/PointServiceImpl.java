package com.cannontech.common.device.service;

import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Implementation class for PointService
 */
public class PointServiceImpl implements PointService {

    private NextValueHelper nextValueHelper = null;

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public PointBase createPoint(int type, String name, int paoId, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId) {

        PointBase point = null;
        int pointId = nextValueHelper.getNextValue("point");

        switch (type) {
        case PointTypes.ANALOG_POINT:
            point = (AnalogPoint) PointFactory.createAnalogPoint(name,
                                                                 paoId,
                                                                 pointId,
                                                                 offset,
                                                                 unitOfMeasure,
                                                                 multiplier);
            break;

        case PointTypes.STATUS_POINT:
            point = (StatusPoint) PointFactory.createBankStatusPt(paoId);
            point.setPointID(pointId);
            point.getPoint().setPointName(name);
            point.getPoint().setPointOffset(offset);
            point.getPoint().setStateGroupID(stateGroupId);
            break;

        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            point = (AccumulatorPoint) PointFactory.createDmdAccumPoint(name,
                                                                        paoId,
                                                                        pointId,
                                                                        offset,
                                                                        unitOfMeasure,
                                                                        multiplier);

            break;

        case PointTypes.PULSE_ACCUMULATOR_POINT:
            point = (AccumulatorPoint) PointFactory.createPulseAccumPoint(name,
                                                                          paoId,
                                                                          pointId,
                                                                          offset,
                                                                          unitOfMeasure,
                                                                          multiplier);

            break;

        default:
            throw new IllegalArgumentException("Invalid point type: " + type);
        }

        return point;
    }

    public PointBase createPoint(int paoId, PointTemplate template) {
        return this.createPoint(template.getType(),
                                template.getName(),
                                paoId,
                                template.getOffset(),
                                template.getMultiplier(),
                                template.getUnitOfMeasure(),
                                template.getStateGroupId());
    }
}
