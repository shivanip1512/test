package com.cannontech.common.pao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.definition.model.CalcPointBase;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.incrementer.NextValueHelper;

public class PointCreationServiceImpl implements PointCreationService {

	private NextValueHelper nextValueHelper = null;
	
	// TODO This service is intended to be the future home of Pointfactory code.
	public PointBase createPoint(int type, String name, int paoId, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, int initialState, int decimalPlaces, ControlType controlType, PointArchiveType pointArchiveType, PointArchiveInterval pointArchiveInterval, CalcPointBase calcPoint) {

        PointBase point = null;
        int pointId = nextValueHelper.getNextValue("point");
        
        switch (type) {
        case PointTypes.ANALOG_POINT:
            point = (AnalogPoint) PointFactory.createAnalogPoint(name,
                                                                 paoId,
                                                                 pointId,
                                                                 offset,
                                                                 unitOfMeasure,
                                                                 multiplier,
                                                                 stateGroupId,
                                                                 decimalPlaces,
                                                                 pointArchiveType,
                                                                 pointArchiveInterval);
            break;

        case PointTypes.STATUS_POINT:
            point = (StatusPoint) PointFactory.createStatusPoint(name, 
            													paoId, 
            													pointId, 
            													offset, 
            													stateGroupId,
            													initialState,
            													controlType,
            													pointArchiveType,
            													pointArchiveInterval);
            break;

        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            point = (AccumulatorPoint) PointFactory.createDmdAccumPoint(name,
                                                                        paoId,
                                                                        pointId,
                                                                        offset,
                                                                        unitOfMeasure,
                                                                        multiplier, 
                                                                        stateGroupId,
                                                                        decimalPlaces,
                                                                        pointArchiveType,
                                                                        pointArchiveInterval);

            break;

        case PointTypes.PULSE_ACCUMULATOR_POINT:
            point = (AccumulatorPoint) PointFactory.createPulseAccumPoint(name,
                                                                          paoId,
                                                                          pointId,
                                                                          offset,
                                                                          unitOfMeasure,
                                                                          multiplier, 
                                                                          stateGroupId,
                                                                          decimalPlaces,
                                                                          pointArchiveType,
                                                                          pointArchiveInterval);

            break;
            
        case PointTypes.CALCULATED_POINT:
            point = (CalculatedPoint) PointFactory.createCalculatedPoint(paoId, name, stateGroupId, unitOfMeasure, decimalPlaces, calcPoint);
            
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
                                template.getStateGroupId(),
                                template.getInitialState(),
                                template.getDecimalPlaces(),
                                template.getControlType(),
                                template.getPointArchiveType(),
                                template.getPointArchiveInterval(),
                                template.getCalcPoint());
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
