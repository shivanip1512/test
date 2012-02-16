package com.cannontech.common.pao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
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
import com.cannontech.database.data.point.StateControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.incrementer.NextValueHelper;

public class PointCreationServiceImpl implements PointCreationService {

	private NextValueHelper nextValueHelper = null;
	
	@Override
	public PointBase createPoint(int type, String name, PaoIdentifier paoIdentifier, int offset, double multiplier,
	                             int unitOfMeasure, int stateGroupId, int initialState, int decimalPlaces, ControlType controlType, PointArchiveType pointArchiveType, PointArchiveInterval pointArchiveInterval) {
	    return createPoint(type,
	                       name,
	                       paoIdentifier,
	                       offset,
	                       multiplier,
	                       unitOfMeasure,
	                       stateGroupId,
	                       initialState,
	                       decimalPlaces,
	                       null,
	                       controlType,
	                       null,
	                       null,
	                       pointArchiveType,
	                       pointArchiveInterval,
	                       null);
	}

    @Override
    public PointBase createPoint(int type, String name, PaoIdentifier paoIdentifier, int offset, double multiplier,
                                 int unitOfMeasure, int stateGroupId, int initialState, int decimalPlaces, Integer controlOffset, ControlType controlType, StateControlType stateZeroControl, StateControlType stateOneControl, PointArchiveType pointArchiveType, PointArchiveInterval pointArchiveInterval, CalcPointInfo calcPoint) {
        PointBase point = null;
        int pointId = nextValueHelper.getNextValue("point");
        
        switch (type) {
        case PointTypes.ANALOG_POINT:
            point = (AnalogPoint) PointFactory.createAnalogPoint(name,
                                                                 paoIdentifier.getPaoId(),
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
                                                                 paoIdentifier.getPaoId(), 
            													pointId, 
            													offset, 
            													stateGroupId,
            													initialState,
            													controlOffset,
            													controlType,
            													stateZeroControl,
            													stateOneControl,
            													pointArchiveType,
            													pointArchiveInterval);
            break;

        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            point = (AccumulatorPoint) PointFactory.createDmdAccumPoint(name,
                                                                        paoIdentifier.getPaoId(),
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
                                                                          paoIdentifier.getPaoId(),
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
            point = (CalculatedPoint) PointFactory.createCalculatedPoint(paoIdentifier,
                                                                         name,
                                                                         stateGroupId,
                                                                         unitOfMeasure,
                                                                         decimalPlaces,
                                                                         pointArchiveType,
                                                                         pointArchiveInterval,
                                                                         calcPoint);
            
            break;

        default:
            throw new IllegalArgumentException("Invalid point type: " + type);
        }

        return point;
    }

    @Override
    public PointBase createPoint(PaoIdentifier paoIdentifier, PointTemplate template) {
        return this.createPoint(template.getType(),
                                template.getName(),
                                paoIdentifier,
                                template.getOffset(),
                                template.getMultiplier(),
                                template.getUnitOfMeasure(),
                                template.getStateGroupId(),
                                template.getInitialState(),
                                template.getDecimalPlaces(),
                                template.getControlOffset(),
                                template.getControlType(),
                                template.getStateZeroControl(),
                                template.getStateOneControl(),
                                template.getPointArchiveType(),
                                template.getPointArchiveInterval(),
                                template.getCalcPointInfo());
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
