package com.cannontech.database.data.point;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class PointUtil {

    private final static Logger log = YukonLogManager.getLogger(PointUtil.class);
    
    private static DBPersistentDao dbPersistentDao = YukonSpringHook.getBean(DBPersistentDao.class);
    private static PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
    private static PointDao pointDao = YukonSpringHook.getBean(PointDao.class);

    public static PointBase createPoint(int type, String name, Integer paoId, boolean disabled) throws PersistenceException {
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        PointBase point = null;

        // Service flag of 'Y' means disabled, 'N' means enabled.
        char disabledChar = disabled ? CtiUtilities.trueChar : CtiUtilities.falseChar;
        int validPointOffset = PointOffsetUtils.getValidPointOffset(paoId, PointType.getForId(type));
        switch (type) {
        case PointTypes.ANALOG_POINT:
            point = PointFactory.createAnalogPoint(name,
                                                   paoId,
                                                   null,
                                                   validPointOffset,
                                                   UnitOfMeasure.VOLTS.getId(),
                                                   1.0,
                                                   PointTemplate.DEFAULT_DATA_OFFSET,
                                                   StateGroupUtils.STATEGROUP_ANALOG,
                                                   PointUnit.DEFAULT_DECIMAL_PLACES,
                                                   PointArchiveType.NONE,
                                                   PointArchiveInterval.ZERO);
            break;
        case PointTypes.STATUS_POINT:
            point = PointFactory.createBankStatusPt(paoId);
            point.getPoint().setPointName(name);
            point.getPoint().setPointOffset(new Integer(validPointOffset));
            break;

        case PointTypes.PULSE_ACCUMULATOR_POINT:
            point = PointFactory.createPulseAccumPoint(name,
                                                       paoId,
                                                       null,
                                                       validPointOffset,
                                                       UnitOfMeasure.UNDEF.getId(),
                                                       1.0,
                                                       StateGroupUtils.STATEGROUP_ANALOG,
                                                       PointUnit.DEFAULT_DECIMAL_PLACES,
                                                       PointArchiveType.NONE,
                                                       PointArchiveInterval.ZERO);

            break;

        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            point = PointFactory.createDmdAccumPoint(name,
                                                     paoId,
                                                     null,
                                                     0,
                                                     UnitOfMeasure.UNDEF.getId(),
                                                     0.1,
                                                     StateGroupUtils.STATEGROUP_ANALOG,
                                                     PointUnit.DEFAULT_DECIMAL_PLACES,
                                                     PointArchiveType.NONE,
                                                     PointArchiveInterval.ZERO);

            break;

        case PointTypes.CALCULATED_POINT:
            YukonPao yukonPao = paoDao.getYukonPao(paoId);
            point = PointFactory.createCalculatedPoint(yukonPao.getPaoIdentifier(), name, StateGroupUtils.STATEGROUP_ANALOG, validPointOffset);
            break;

        case PointTypes.CALCULATED_STATUS_POINT:
            point = PointFactory.createCalcStatusPoint(paoId, name, StateGroupUtils.STATEGROUP_ANALOG, validPointOffset);
            break;

        default:
            throw new Error("PointUtil::createPoint - Unrecognized point type");
        }

        point.getPoint().setServiceFlag(disabledChar);

        dbPersistentVector.getDBPersistentVector().add(point);
        PointUtil.insertIntoDB(dbPersistentVector);

        return point;
    }

    public static void insertIntoDB(DBPersistent pointVector) throws PersistenceException {
        if (pointVector != null) {
            try {
                dbPersistentDao.performDBChange(pointVector, TransactionType.INSERT);
            } catch (PersistenceException e) {
                throw e;
            }
        } else {
            throw new PersistenceException("Trying to INSERT empty - insertIntoDB - PointUtil");
        }
    }

    /**
     * Helper method to change pointBase to the newPointTemplate type.
     * If the pointBase type and the newPointTemplate type are the same, pointBase is returned unchanged.
     * Else, the pointBase object is returned with the newPointTemplate data set.
     * Sends DBChangeMsg for each point that actually has 'changes'. 
     * @param pointBase
     * @param newPointTemplate
     * @return
     * @throws PersistenceException
     */
    public static PointBase changePointType(PointBase pointBase, PointTemplate newPointTemplate) throws PersistenceException {

        PointTemplate existingPointTemplate = null;
        try {
            existingPointTemplate = createPointTemplate(pointBase);
        } catch (NotFoundException e) {
            // ignore
            // example: original device has a point that is already deleted, this point is part of the calculation for
            // second point, it will not be possible to create a template for the second point without the first point.
        }
        int oldType = PointTypes.getType(pointBase.getPoint().getPointType());

        if (oldType != newPointTemplate.getPointType().getPointTypeId()) {
            
            PointAlarming savePointAlarming = pointBase.getPointAlarming();
            Point savePoint = pointBase.getPoint();

            // Delete partial point data so type can be changed.
            deletePartialPointData(pointBase, newPointTemplate);

            // Create a new point for new point type
            pointBase = PointFactory.createPoint(newPointTemplate.getPointType().getPointTypeId());
            String newPointType = pointBase.getPoint().getPointType();
     
            // Set old point data that can transfer over to new point
            pointBase.setPoint(savePoint);
            pointBase.setPointAlarming(savePointAlarming);
            pointBase.setPointID(savePoint.getPointID());
            pointBase.getPoint().setPointType(newPointType);

            // Add the updated (partial) point information.
            dbPersistentDao.performDBChangeWithNoMsg(pointBase, TransactionType.ADD_PARTIAL);
        }

        if (existingPointTemplate == null || !existingPointTemplate.equals(newPointTemplate)) {
            applyPointTemplate(pointBase, newPointTemplate);
            dbPersistentDao.performDBChange(pointBase, TransactionType.UPDATE);
        }

        return pointBase;
    }
    
    /**
     * Deletes partial point data
     * example: MCT410IL->Delivered kWh is PulseAccumulator, RFN420CD ->Delivered kWh is Analog
     */
    private static void deletePartialPointData(PointBase pointBase, PointTemplate newPointTemplate) {
        PointType newPointType = newPointTemplate.getPointType();
        boolean partialDelete = false;
        if (pointBase instanceof AnalogPoint && newPointType != PointType.Analog) {
            partialDelete = true;
            AnalogPoint point = (AnalogPoint) pointBase;
            dbPersistentDao.performDBChangeWithNoMsg(point, TransactionType.DELETE_PARTIAL);
        } else if (pointBase instanceof StatusPoint && newPointType != PointType.Status) {
            partialDelete = true;
            StatusPoint point = (StatusPoint) pointBase;
            dbPersistentDao.performDBChangeWithNoMsg(point, TransactionType.DELETE_PARTIAL);
        } else if (pointBase instanceof AccumulatorPoint && newPointType != PointType.DemandAccumulator
            && newPointType != PointType.PulseAccumulator) {
            partialDelete = true;
            AccumulatorPoint point = (AccumulatorPoint) pointBase;
            dbPersistentDao.performDBChangeWithNoMsg(point, TransactionType.DELETE_PARTIAL);
        } else if (pointBase instanceof CalculatedPoint && newPointType != PointType.CalcAnalog
            && newPointType != PointType.CalcStatus) {
            partialDelete = true;
            CalculatedPoint point = (CalculatedPoint) pointBase;
            dbPersistentDao.performDBChangeWithNoMsg(point, TransactionType.DELETE_PARTIAL);
        } else {
            dbPersistentDao.performDBChangeWithNoMsg(pointBase, TransactionType.DELETE_PARTIAL);
        }

        if (partialDelete && log.isDebugEnabled()) {
            Point point = pointBase.getPoint();
            log.debug("Partialy deleting point information due to a point type change:  point: id="
                + point.getPointID() + " name=" + point.getPointName() + " type=" + point.getPointType() + " offset="
                + point.getPointOffset() + "--Change to=" + newPointTemplate);
        }
    }

    private static PointTemplate createPointTemplate(PointBase pointBase) throws IllegalArgumentException {

        int pointType = PointTypes.getType(pointBase.getPoint().getPointType());
        int pointOffset = pointBase.getPoint().getPointOffset();
        PointTemplate pointTemplate = new PointTemplate(new PointIdentifier(PointType.getForId(pointType), pointOffset));
        pointTemplate.setName(pointBase.getPoint().getPointName());

        if (pointBase instanceof AnalogPoint) {

            AnalogPoint analogPoint = (AnalogPoint) pointBase;

            pointTemplate.setMultiplier(analogPoint.getPointAnalog().getMultiplier());
            pointTemplate.setStateGroupId(analogPoint.getPoint().getStateGroupID());
            pointTemplate.setUnitOfMeasure(analogPoint.getPointUnit().getUomID());

        } else if (pointBase instanceof StatusPoint) {

            StatusPoint statusPoint = (StatusPoint) pointBase;

            pointTemplate.setStateGroupId(statusPoint.getPoint().getStateGroupID());

        } else if (pointBase instanceof AccumulatorPoint) {

            AccumulatorPoint accumulatorPoint = (AccumulatorPoint) pointBase;

            pointTemplate.setMultiplier(accumulatorPoint.getPointAccumulator().getMultiplier());
            pointTemplate.setStateGroupId(accumulatorPoint.getPoint().getStateGroupID());
            pointTemplate.setUnitOfMeasure(accumulatorPoint.getPointUnit().getUomID());

        } else if (pointBase instanceof CalculatedPoint) {
            CalculatedPoint calcPoint = (CalculatedPoint) pointBase;

            pointTemplate.setStateGroupId(calcPoint.getPoint().getStateGroupID());
            pointTemplate.setUnitOfMeasure(calcPoint.getPointUnit().getUomID());
            pointTemplate.setDecimalPlaces(calcPoint.getPointUnit().getDecimalPlaces());
            pointTemplate.setPointArchiveType(calcPoint.getPoint().getArchiveType());
            pointTemplate.setPointArchiveInterval(PointArchiveInterval.getIntervalBySeconds(calcPoint.getPoint().getArchiveInterval()));

            CalcPointInfo calcPointInfo = new CalcPointInfo(calcPoint.getCalcBase().getUpdateType(), calcPoint.getCalcBase().getPeriodicRate(), false);
            List<CalcPointComponent> components = Lists.newArrayListWithExpectedSize(calcPoint.getCalcComponents().size());

            for (CalcComponent calcComponent : calcPoint.getCalcComponents()) {
                int pointId = calcComponent.getComponentPointID();
                String componentType = calcComponent.getComponentType();
                String operation = calcComponent.getOperation();

                LitePoint litePoint = pointDao.getLitePoint(pointId);
                PointIdentifier pointIdentifier = new PointIdentifier(PointType.getForId(litePoint.getPointType()), litePoint.getPointOffset());

                CalcPointComponent newComponent = new CalcPointComponent(pointIdentifier, componentType, operation);
                components.add(newComponent);
            }
            calcPointInfo.setComponents(components);
            pointTemplate.setCalcPointInfo(calcPointInfo);
        } else {
            throw new IllegalArgumentException("Unsupported PointBase type: " + pointBase);
        }

        return pointTemplate;
    }

    public static void applyPointTemplate(PointBase pointBase, PointTemplate pointTemplate) {

        if (PointTypes.getType(pointBase.getPoint().getPointType()) != pointTemplate.getPointType().getPointTypeId()) {
            throw new IllegalArgumentException("Method not intended to be used to change point type.");
        }

        pointBase.getPoint().setPointOffset(pointTemplate.getOffset());
        pointBase.getPoint().setPointName(pointTemplate.getName());

        if (pointBase instanceof AnalogPoint) {

            AnalogPoint analogPoint = (AnalogPoint) pointBase;

            analogPoint.getPointAnalog().setMultiplier(pointTemplate.getMultiplier());
            analogPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
            analogPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
            analogPoint.getPointUnit().setDecimalPlaces(pointTemplate.getDecimalPlaces());
            analogPoint.getPointAnalog().setDataOffset(pointTemplate.getDataOffset());

        } else if (pointBase instanceof StatusPoint) {

            StatusPoint statusPoint = (StatusPoint) pointBase;

            statusPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
            statusPoint.getPointStatusControl().setControlOffset(pointTemplate.getControlOffset());
            statusPoint.getPointStatusControl().setControlType(pointTemplate.getControlType().getControlName());
            statusPoint.getPointStatusControl().setStateZeroControl(pointTemplate.getStateZeroControl().getControlCommand());
            statusPoint.getPointStatusControl().setStateOneControl(pointTemplate.getStateOneControl().getControlCommand());

        } else if (pointBase instanceof AccumulatorPoint) {

            AccumulatorPoint accumulatorPoint = (AccumulatorPoint) pointBase;

            accumulatorPoint.getPointAccumulator().setMultiplier(pointTemplate.getMultiplier());
            accumulatorPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
            accumulatorPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
            accumulatorPoint.getPointUnit().setDecimalPlaces(pointTemplate.getDecimalPlaces());
            accumulatorPoint.getPointAccumulator().setDataOffset(pointTemplate.getDataOffset());

        } else if (pointBase instanceof CalculatedPoint) {
            addCalcPointTemplateAttributesToPointBase(pointTemplate, (CalculatedPoint) pointBase);
        } else {
            throw new IllegalArgumentException("Unsupported PointBase type: " + pointBase);
        }
    }

    // TODO: Change the very similar PointFactory.createCalculatedPoint code to use this method
    private static void addCalcPointTemplateAttributesToPointBase(PointTemplate pointTemplate, CalculatedPoint calcPoint) {
        calcPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
        calcPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
        calcPoint.getPointUnit().setDecimalPlaces(pointTemplate.getDecimalPlaces());
        calcPoint.getPoint().setArchiveType(pointTemplate.getPointArchiveType());
        calcPoint.getPoint().setArchiveInterval(pointTemplate.getPointArchiveInterval().getSeconds());

        CalcBase calcBase = new CalcBase();
        calcBase.setUpdateType(pointTemplate.getCalcPointInfo().getUpdateType());
        calcBase.setPeriodicRate(pointTemplate.getCalcPointInfo().getPeriodicRate());
        calcPoint.setCalcBase(calcBase);

        int order = 1;
        List<CalcComponent> calcComponents = Lists.newArrayListWithExpectedSize(pointTemplate.getCalcPointInfo().getComponents().size());
        for (CalcPointComponent calcPointComponent : pointTemplate.getCalcPointInfo().getComponents()) {
            Integer componentPointId = calcPointComponent.getPointId();
            if (componentPointId == null) {
                // If this CalcPointComponent's pointId isn't set by now... we assume it's pointIdentifier refers to
                // this same paoIdentifier
                YukonPao yukonPao = paoDao.getYukonPao(calcPoint.getPoint().getPaoID());
                LitePoint litePoint = pointDao.getLitePoint(new PaoPointIdentifier(yukonPao.getPaoIdentifier(), calcPointComponent.getPointIdentifier()));
                componentPointId = litePoint.getPointID();
            } else {
                // We are done with this value. Clear it out.
                // TODO: make this thing not be stateful like this
                calcPointComponent.setPointId(null);
            }
            String componentType = calcPointComponent.getCalcComponentType();
            String operation = calcPointComponent.getOperation();

            calcComponents.add(new CalcComponent(calcPoint.getPointUnit().getPointID(),
                                                 order++,
                                                 componentType,
                                                 componentPointId,
                                                 operation,
                                                 0.0,
                                                 CtiUtilities.STRING_NONE));
        }
        calcPoint.setCalcComponents(calcComponents);
    }
}