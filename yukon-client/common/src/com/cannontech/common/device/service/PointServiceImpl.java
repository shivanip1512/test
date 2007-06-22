package com.cannontech.common.device.service;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
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

    private PointDao pointDao = null;
    private NextValueHelper nextValueHelper = null;

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

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

    public LitePoint getPointForDevice(YukonDevice device, DevicePointIdentifier template) {

        LitePoint point = pointDao.getLitePointIdByDeviceId_Offset_PointType(device.getDeviceId(),
                                                                             template.getOffset(),
                                                                             template.getType());

        return point;
    }

    public boolean pointExistsForDevice(YukonDevice device, DevicePointIdentifier template) {

        try {
            LitePoint point = this.getPointForDevice(device, template);
            if (point.getPointType() == PointTypes.SYSTEM_POINT) {
                return false;
            }
        } catch (NotFoundException e) {
            return false;
        }

        return true;
    }
}
