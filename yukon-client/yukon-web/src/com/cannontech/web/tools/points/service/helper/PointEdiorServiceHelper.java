package com.cannontech.web.tools.points.service.helper;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAccumulator;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointStatusControl;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcPointBaseline;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.PointModel;
import com.google.common.collect.Lists;

public class PointEdiorServiceHelper {

    public static PointBase populateBankStatusPtToCopy(PointModel<? extends PointBase> pointModel,
            PointBase pointBaseToCopy) {
        PointBase newPoint = populatePointBaseToCopy(pointModel, pointBaseToCopy);
        newPoint.getPoint().setStateGroupID(pointBaseToCopy.getPoint().getStateGroupID());
        
        StatusPoint statusPointToCopy = (StatusPoint) pointBaseToCopy;
        StatusPoint newStatusPoint = (StatusPoint) newPoint;
        newStatusPoint.getPointStatus().setInitialState(statusPointToCopy.getPointStatus().getInitialState());
        
        PointStatusControl newPointStatusControl = newStatusPoint.getPointStatusControl();
        newPointStatusControl.setCloseTime1(statusPointToCopy.getPointStatusControl().getCloseTime1());
        newPointStatusControl.setCloseTime2(statusPointToCopy.getPointStatusControl().getCloseTime2());
        newPointStatusControl.setStateZeroControl(statusPointToCopy.getPointStatusControl().getStateZeroControl());
        newPointStatusControl.setStateOneControl(statusPointToCopy.getPointStatusControl().getStateOneControl());
        newPointStatusControl.setCommandTimeOut(statusPointToCopy.getPointStatusControl().getCommandTimeOut());
        newPointStatusControl.setControlType(statusPointToCopy.getPointStatusControl().getControlType());
        newPointStatusControl.setControlInhibited(statusPointToCopy.getPointStatusControl().isControlInhibited());
        newPointStatusControl.setControlOffset(statusPointToCopy.getPointStatusControl().getControlOffset());
        
        return newPoint;
    }
    
    public static PointBase populateCalculatedStatusPoint(PointModel<? extends PointBase> pointModel,
            PointBase pointToCopy) {
        PointBase newPoint = populatePointBaseToCopy(pointModel, pointToCopy);
        Integer newPointId = null;

        CalcStatusPoint calcStatusPointToCopy = (CalcStatusPoint) pointToCopy;
        CalcStatusPoint newCalcStatusPoint = (CalcStatusPoint) newPoint;

        newCalcStatusPoint.setCalcBase(new CalcBase(null, calcStatusPointToCopy.getCalcBase().getUpdateType(),
            calcStatusPointToCopy.getCalcBase().getPeriodicRate(),
            calcStatusPointToCopy.getCalcBase().getCalculateQuality()));

        newCalcStatusPoint.setCalcComponents(populateCalcComponents(calcStatusPointToCopy.getCalcComponents()));

        newCalcStatusPoint.setCalcBaselinePoint(
            new CalcPointBaseline(newPointId, calcStatusPointToCopy.getCalcBaselinePoint().getBaselineID()));

        newCalcStatusPoint.setBaselineAssigned(calcStatusPointToCopy.getBaselineAssigned());

        return newPoint;
    }
    
    public static PointBase populateCalculatedPoint(PointModel<? extends PointBase> pointModel, PointBase pointToCopy) {
        PointBase newPoint = populatePointBaseToCopy(pointModel, pointToCopy);
        Integer newPointId = null;

        CalculatedPoint calculatedPointToCopy = (CalculatedPoint) pointToCopy;
        CalculatedPoint newCalcStatusPoint = (CalculatedPoint) newPoint;

        newCalcStatusPoint.setCalcBase(new CalcBase(null, calculatedPointToCopy.getCalcBase().getUpdateType(),
            calculatedPointToCopy.getCalcBase().getPeriodicRate(),
            calculatedPointToCopy.getCalcBase().getCalculateQuality()));

        newCalcStatusPoint.setCalcComponents(populateCalcComponents(calculatedPointToCopy.getCalcComponents()));

        newCalcStatusPoint.setCalcBaselinePoint(
            new CalcPointBaseline(newPointId, calculatedPointToCopy.getCalcBaselinePoint().getBaselineID()));

        newCalcStatusPoint.setBaselineAssigned(calculatedPointToCopy.getBaselineAssigned());

        return newPoint;
    }

    private static List<CalcComponent> populateCalcComponents(List<CalcComponent> calcComponents) {
        List<CalcComponent> newCalcComponents = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(calcComponents)) {
            for(CalcComponent calcComponent : calcComponents) {
                CalcComponent newCalcComponent = new CalcComponent(null, 
                                                                   calcComponent.getComponentOrder(), 
                                                                   calcComponent.getComponentType(),
                                                                   calcComponent.getComponentPointID(), 
                                                                   calcComponent.getOperation(),
                                                                   calcComponent.getConstant(),
                                                                   calcComponent.getFunctionName());
                newCalcComponents.add(newCalcComponent);
            }
            
        }
        return newCalcComponents;
    }

    public static PointBase populateAnalogPointToCopy(PointModel<? extends PointBase> pointModel, PointBase pointToCopy) {
        PointBase newPoint = populatePointBaseToCopy(pointModel, pointToCopy);
        
        AnalogPoint analogPointToCopy = (AnalogPoint)pointToCopy;
        AnalogPoint newAnalogPoint = (AnalogPoint)newPoint;
        
        newAnalogPoint.setPointUnit(new PointUnit(null, 
                                                  analogPointToCopy.getPointUnit().getUomID(), 
                                                  analogPointToCopy.getPointUnit().getDecimalPlaces(), 
                                                  analogPointToCopy.getPointUnit().getHighReasonabilityLimit(),
                                                  analogPointToCopy.getPointUnit().getLowReasonabilityLimit(),
                                                  analogPointToCopy.getPointUnit().getMeterDials()));

        ((AnalogPoint) newPoint).getPointAnalog().setMultiplier(analogPointToCopy.getPointAnalog().getMultiplier());
        ((AnalogPoint) newPoint).getPointAnalog().setDataOffset(analogPointToCopy.getPointAnalog().getDataOffset());
        ((AnalogPoint) newPoint).getPointAnalog().setDeadband(analogPointToCopy.getPointAnalog().getDeadband());
        
        return newPoint;
    }
    
    private static PointBase populatePointBaseToCopy(PointModel<? extends PointBase> pointModel, PointBase pointBaseToCopy) {
        Point pointToCopy = pointBaseToCopy.getPoint();
        PointType pointType = pointToCopy.getPointTypeEnum();
        String pointName = pointModel.getPointBase().getPoint().getPointName();
        Integer paoID = pointModel.getPointBase().getPoint().getPaoID();
        Integer newPointId = null;
        Integer pointOffset = 0;
        
        if (pointModel.getPointBase().getPoint().isPhysicalOffset()) {
            pointOffset = pointModel.getPointBase().getPoint().getPointOffset();
        }
        
        // Create a new point
        PointBase newPoint = PointFactory.createPoint(pointType.getPointTypeId());
        
        // set point fields
        newPoint.setPoint(new Point(newPointId,
                                    PointTypes.getType(pointType.getPointTypeId()),
                                    pointName,
                                    paoID,
                                    pointToCopy.getLogicalGroup(),
                                    pointToCopy.getStateGroupID(),
                                    pointToCopy.getServiceFlag(),
                                    pointToCopy.getAlarmInhibit(),
                                    pointOffset,
                                    pointToCopy.getArchiveType(),
                                    pointToCopy.getArchiveInterval()));
        
        // set point alarming fields
        PointAlarming pointAlarmingToCopy = pointBaseToCopy.getPointAlarming();
        newPoint.setPointAlarming(new PointAlarming(newPointId, pointAlarmingToCopy.getAlarmStates(), 
                                                    pointAlarmingToCopy.getExcludeNotifyStates(), 
                                                    pointAlarmingToCopy.getNotifyOnAcknowledge(), 
                                                    pointAlarmingToCopy.getNotificationGroupID()));
        
        // set the FDR fields
        if (CollectionUtils.isNotEmpty(pointBaseToCopy.getPointFDRList())) {
            List<FDRTranslation> fdrTranslationsToCopy = pointBaseToCopy.getPointFDRList();
            List<FDRTranslation> newFdrTranslations = Lists.newArrayList();
            for (FDRTranslation fdrTranslation : fdrTranslationsToCopy) {
                FDRTranslation newFdrTranslation =
                    new FDRTranslation(null, fdrTranslation.getDirectionType(), fdrTranslation.getInterfaceType(),
                        fdrTranslation.getDestination(), fdrTranslation.getTranslation());
                newFdrTranslations.add(newFdrTranslation);
            }
            newPoint.setPointFDRTranslations(newFdrTranslations);
        }
        
        if (pointBaseToCopy instanceof ScalarPoint) {
            ScalarPoint scalarPointToCopy = (ScalarPoint) pointBaseToCopy;
            ScalarPoint newScalarPoint = (ScalarPoint) newPoint;

            for (Entry<Integer, PointLimit> entry : scalarPointToCopy.getPointLimitsMap().entrySet()) {
                PointLimit limitToCopy = entry.getValue();
                PointLimit newPointLimit = new PointLimit(null, limitToCopy.getLimitNumber(),
                    limitToCopy.getHighLimit(), limitToCopy.getLowLimit(), limitToCopy.getLimitDuration());
                newScalarPoint.getPointLimitsMap().put(entry.getKey(), newPointLimit);
            }

            newScalarPoint.setPointUnit(new PointUnit(null, scalarPointToCopy.getPointUnit().getUomID(),
                scalarPointToCopy.getPointUnit().getDecimalPlaces(),
                scalarPointToCopy.getPointUnit().getHighReasonabilityLimit(),
                scalarPointToCopy.getPointUnit().getLowReasonabilityLimit(),
                scalarPointToCopy.getPointUnit().getMeterDials()));
        }
        
        return newPoint;
    }

    public static StaleData populateStaleDataObjectToCopy(StaleData stateDataToCopy) {
        StaleData staleData = new StaleData();
        staleData.setEnabled(stateDataToCopy.isEnabled());
        staleData.setTime(stateDataToCopy.getTime());
        staleData.setUpdateStyle(stateDataToCopy.getUpdateStyle());
        return staleData;
    }

    public static PointBase populatePulseAccumulatorPtToCopy(PointModel<? extends PointBase> pointModel,
            PointBase pointToCopy) {
        PointBase newPoint = populatePointBaseToCopy(pointModel, pointToCopy);

        AccumulatorPoint accumulatorPointToCopy = (AccumulatorPoint) pointToCopy;

        ((AccumulatorPoint) newPoint).setPointAccumulator(
            new PointAccumulator(null, accumulatorPointToCopy.getPointAccumulator().getMultiplier(),
                accumulatorPointToCopy.getPointAccumulator().getDataOffset()));

        return newPoint;
    }

}
