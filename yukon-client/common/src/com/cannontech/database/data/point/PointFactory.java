package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.spring.YukonSpringHook;

public final class PointFactory {

    public final static PointBase createPoint(int type) {

        PointBase retPoint = null;
        switch (type)
        {
        case PointTypes.ANALOG_POINT:
            retPoint = new AnalogPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.ANALOG_POINT));
            break;
        case PointTypes.PULSE_ACCUMULATOR_POINT:
            retPoint = new AccumulatorPoint();
            retPoint.getPoint()
                .setPointType(PointTypes.getType(PointTypes.PULSE_ACCUMULATOR_POINT));
            break;
        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            retPoint = new AccumulatorPoint();
            retPoint.getPoint()
                .setPointType(PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT));
            break;
        case PointTypes.STATUS_POINT:
            retPoint = new StatusPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.STATUS_POINT));
            break;
        case PointTypes.CALCULATED_POINT:
            retPoint = new CalculatedPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.CALCULATED_POINT));
            break;
        case PointTypes.CALCULATED_STATUS_POINT:
            retPoint = new CalcStatusPoint();
            retPoint.getPoint()
                .setPointType(PointTypes.getType(PointTypes.CALCULATED_STATUS_POINT));
            break;
        default: // this is bad
            throw new Error("PointFactory::createPoint - Unrecognized point type");
        }

        return retPoint;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/15/99 2:34:02 PM)
     * @return com.cannontech.database.data.point.PointBase
     * @param id java.lang.Integer
     */
    public static final PointBase retrievePoint(Integer id) throws java.sql.SQLException {

        return retrievePoint(id, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/15/99 2:34:02 PM)
     * @return com.cannontech.database.data.point.PointBase
     * @param id java.lang.Integer
     */
    public static final PointBase retrievePoint(Integer id, String databaseAlias)
            throws java.sql.SQLException {

        java.sql.Connection conn = null;
        PointBase returnVal = null;

        try
        {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

            com.cannontech.database.db.point.Point p = new com.cannontech.database.db.point.Point();
            p.setPointID(id);

            p.setDbConnection(conn);
            p.retrieve();
            p.setDbConnection(null);

            returnVal = createPoint(PointTypes.getType(p.getPointType()));
            returnVal.setPointID(id);

            returnVal.setDbConnection(conn);
            returnVal.retrieve();
            returnVal.setDbConnection(null);
        } catch (java.sql.SQLException e)
        {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        } finally
        {
            try {
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e2) {}
        }

        return returnVal;
    }

    public static PointBase createAnalogPoint(String pointName, Integer paoID,
                                              Integer pointID, int pointOffset, int pointUnit,
                                              int stateGroupId)
    {

        return createAnalogPoint(pointName,
                                 paoID,
                                 pointID,
                                 pointOffset,
                                 pointUnit,
                                 1.0,
                                 stateGroupId,
                                 com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES,
                                 PointArchiveType.NONE,
                                 PointArchiveInterval.ZERO);
    }

    public static PointBase createAnalogPoint(String pointName, Integer paoID,
                                              Integer pointID, int pointOffset, int pointUnit,
                                              double multiplier, int stateGroupId,
                                              int decimalPlaces, PointArchiveType pointArchiveType,
                                              PointArchiveInterval pointArchiveInterval)
    {
        com.cannontech.database.data.point.PointBase point =
            PointFactory.createNewPoint(
                                        pointID,
                                        com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
                                        pointName,
                                        paoID,
                                        new Integer(pointOffset));

        point.getPoint().setArchiveType(pointArchiveType.getPointArchiveTypeName());
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId); // new
                                                        // Integer(StateGroupUtils.STATEGROUP_ANALOG)
                                                        // );

        // defaults - pointUnit
        ((com.cannontech.database.data.point.ScalarPoint) point).setPointUnit(
            new com.cannontech.database.db.point.PointUnit(
                                                           pointID,
                                                           new Integer(pointUnit),
                                                           new Integer(decimalPlaces),
                                                           new Double(0.0),
                                                           new Double(0.0),
                                                           new Integer(0)));

        ((AnalogPoint) point).getPointAnalog().setMultiplier(multiplier);

        return point;
    }

    public static PointBase createDmdAccumPoint(String pointName, Integer paoID,
                                                Integer pointID, int pointOffset, int pointUnit,
                                                double multiplier, int stateGroupId,
                                                int decimalPlaces,
                                                PointArchiveType pointArchiveType,
                                                PointArchiveInterval pointArchiveInterval)
    {
        com.cannontech.database.data.point.PointBase point =
            PointFactory
                .createNewPoint(
                                pointID,
                                com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT,
                                pointName,
                                paoID,
                                new Integer(pointOffset));

        point.getPoint().setArchiveType(pointArchiveType.getPointArchiveTypeName());
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId); // new
                                                        // Integer(StateGroupUtils.STATEGROUP_ANALOG)
                                                        // );

        // defaults - pointAccumulator
        com.cannontech.database.db.point.PointAccumulator accumPt =
            new com.cannontech.database.db.point.PointAccumulator(
                                                                  pointID,
                                                                  new Double(multiplier),
                                                                  new Double(0.0));

        ((AccumulatorPoint) point).setPointAccumulator(accumPt);

        // defaults - pointUnit
        ((com.cannontech.database.data.point.ScalarPoint) point).setPointUnit(
            new com.cannontech.database.db.point.PointUnit(
                                                           pointID,
                                                           new Integer(pointUnit),
                                                           new Integer(decimalPlaces),
                                                           new Double(0.0),
                                                           new Double(0.0),
                                                           new Integer(0)));

        return point;
    }

    /**
     * This method was created in VisualAge.
     * @param pointID java.lang.Integer
     */
    public final static PointBase createNewPoint(Integer pointID, int pointType, String pointName,
                                                 Integer paoID, Integer offset) {

        // A point is automatically created here
        PointBase newPoint =
            com.cannontech.database.data.point.PointFactory.createPoint(pointType);

        // set default point values for point tables
        newPoint
            .setPoint(
            new com.cannontech.database.db.point.Point(
                                                       pointID,
                                                       PointTypes.getType(pointType),
                                                       pointName,
                                                       paoID,
                                                       PointLogicalGroups
                                                           .getLogicalGrp(PointLogicalGroups.LGRP_DEFAULT),
                                                       new Integer(0),
                                                       com.cannontech.common.util.CtiUtilities
                                                           .getFalseCharacter(),
                                                       com.cannontech.common.util.CtiUtilities
                                                           .getFalseCharacter(),
                                                       offset,
                                                       PointArchiveType.NONE
                                                           .getPointArchiveTypeName(),
                                                       PointArchiveInterval.ZERO.getSeconds()));

        newPoint
            .setPointAlarming(
            new com.cannontech.database.db.point.PointAlarming(
                                                               pointID,
                                                               com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES,
                                                               com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY,
                                                               "N",
                                                               new Integer(com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID)));

        newPoint.setPointID(pointID);
        
        return newPoint;
    }

    public static PointBase createPulseAccumPoint(String pointName, Integer paoID,
                                                  Integer pointID, int pointOffset, int pointUnit,
                                                  double multiplier, int stateGroupId,
                                                  int decimalPlaces,
                                                  PointArchiveType pointArchiveType,
                                                  PointArchiveInterval pointArchiveInterval)
    {

        PointBase point = PointFactory.createNewPoint(
                                                      pointID,
                                                      PointTypes.PULSE_ACCUMULATOR_POINT,
                                                      pointName,
                                                      paoID,
                                                      new Integer(pointOffset));

        point.getPoint().setArchiveType(pointArchiveType.getPointArchiveTypeName());
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId); // new
                                                        // Integer(StateGroupUtils.STATEGROUP_ANALOG)
                                                        // );

        // defaults - pointAccumulator
        com.cannontech.database.db.point.PointAccumulator accumPt =
            new com.cannontech.database.db.point.PointAccumulator(
                                                                  pointID,
                                                                  new Double(multiplier),
                                                                  new Double(0.0));

        ((AccumulatorPoint) point).setPointAccumulator(accumPt);

        // defaults - pointUnit
        ((com.cannontech.database.data.point.ScalarPoint) point).setPointUnit(
            new com.cannontech.database.db.point.PointUnit(
                                                           pointID,
                                                           new Integer(pointUnit),
                                                           new Integer(decimalPlaces),
                                                           new Double(0.0),
                                                           new Double(0.0),
                                                           new Integer(0)));

        return point;
    }

    public static synchronized PointBase createStatusPoint(String pointName, Integer paoID,
                                                           Integer pointID, int pointOffset,
                                                           int stateGroupId, int initialState,
                                                           Integer controlOffset,
                                                           StatusControlType controlType,
                                                           StateControlType stateZeroControl,
                                                           StateControlType stateOneControl,
                                                           PointArchiveType pointArchiveType,
                                                           PointArchiveInterval pointArchiveInterval)
    {
        // Create new point
        PointBase newPoint = PointFactory.createNewPoint(
                                               pointID,
                                               PointTypes.STATUS_POINT,
                                               pointName,
                                               paoID,
                                               pointOffset);

        newPoint.getPoint().setArchiveType(pointArchiveType.getPointArchiveTypeName());
        newPoint.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        newPoint.getPoint().setStateGroupID(stateGroupId);

        ((StatusPoint)newPoint).getPointStatus().setInitialState(initialState);

        if (controlOffset != null) {
            ((StatusPoint)newPoint).getPointStatusControl().setControlOffset(controlOffset);
        }

        ((StatusPoint)newPoint).getPointStatusControl().setControlType(controlType.getControlName());

        if (stateZeroControl != null) {
            ((StatusPoint)newPoint).getPointStatusControl().setStateZeroControl(stateZeroControl.getControlCommand());
        }
        if (stateOneControl != null) {
            ((StatusPoint)newPoint).getPointStatusControl().setStateOneControl(stateOneControl.getControlCommand());
        }

        return newPoint;
    }

    /**
     * Creates a CapBanks stutus point automatically
     * 
     */
    public static synchronized PointBase createBankStatusPt(Integer capBankID)
    {

        // a status point is created
        PointBase newPoint = 
            PointFactory.createNewPoint(
                null,
                PointTypes.STATUS_POINT,
                "BANK STATUS",
                capBankID,
                new Integer(1));

        newPoint.getPoint().setStateGroupID(new Integer(3));

        return newPoint;
    }

    public static PointBase createCalcStatusPoint(Integer paoId, String name, int stateGroupId) {

        PointBase newPoint =
            PointFactory.createNewPoint(
                null,
                PointTypes.CALCULATED_STATUS_POINT,
                name,
                paoId,
                0);

        newPoint.getPoint().setStateGroupID(stateGroupId);

        ((CalcStatusPoint) newPoint).getCalcBase().setPeriodicRate(new Integer(1));
        ((CalcStatusPoint) newPoint).getCalcBase().setUpdateType("On All Change");

        return newPoint;

    }

    public static PointBase createCalculatedPoint(PaoIdentifier paoIdentifier, String name,
                                                  int stateGroupId) {
        return createCalculatedPoint(paoIdentifier,
                                     name,
                                     stateGroupId,
                                     UnitOfMeasure.UNDEF.getId(),
                                     PointUnit.DEFAULT_DECIMAL_PLACES,
                                     PointArchiveType.NONE,
                                     PointArchiveInterval.ZERO,
                                     null);
    }

    /**
     * This method only supports creating calculated points that contain inner CalcPointComponent's
     * that reference their own pao
     */
    public static PointBase createCalculatedPoint(PaoIdentifier paoIdentifier, String name,
                                                  int stateGroupId, int unitOfMeasure,
                                                  int decimalPlaces,
                                                  PointArchiveType pointArchiveType,
                                                  PointArchiveInterval pointArchiveInterval,
                                                  CalcPointInfo calcPoint) {
        PointBase point = 
            PointFactory.createNewPoint(
                null,
                PointTypes.CALCULATED_POINT,
                name,
                paoIdentifier.getPaoId(),
                new Integer(0));

        point.getPoint().setArchiveType(pointArchiveType.getPointArchiveTypeName());
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());
        point.getPoint().setStateGroupID(stateGroupId);
        PointUnit punit = new PointUnit(null,
                                        new Integer(unitOfMeasure),
                                        new Integer(decimalPlaces),
                                        new Double(CtiUtilities.INVALID_MAX_DOUBLE),
                                        new Double(CtiUtilities.INVALID_MIN_DOUBLE),
                                        new Integer(0));

        ((ScalarPoint) point).setPointUnit(punit);

        // Calculated Point consists of CalcBase and CalcPointBaseline
        CalcBase calcBase = new CalcBase();
        calcBase.setPointID(point.getPoint().getPointID());

        if (calcPoint != null) {
            if (calcPoint.getUpdateType() != null) {
                calcBase.setUpdateType(calcPoint.getUpdateType());
            }
            if (calcPoint.getPeriodicRate() >= 1) {
                calcBase.setPeriodicRate(calcPoint.getPeriodicRate());
            } else {
                calcBase.setPeriodicRate(new Integer(1));
            }
        } else {
            calcBase.setUpdateType(PointTypes.UPDATE_FIRST_CHANGE);
            calcBase.setPeriodicRate(new Integer(1));
        }

        ((CalculatedPoint) point).setCalcBase(calcBase);
        ((CalculatedPoint) point).setBaselineAssigned(false);

        if (calcPoint != null) {
            PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);

            int order = 1;
            Vector<CalcComponent> calcComponents = new Vector<CalcComponent>();
            Integer pointId = point.getPoint().getPointID();
            for (CalcPointComponent calcPointComponent : calcPoint.getComponents()) {
                Integer componentPointId = calcPointComponent.getPointId();
                if (componentPointId == null) {
                    // If this CalcPointComponent's pointId isn't set by now... we assume it's
                    // pointIdentifier refers to this same paoIdentifier
                    LitePoint litePoint =
                        pointDao.getLitePoint(new PaoPointIdentifier(paoIdentifier,
                                                                     calcPointComponent
                                                                         .getPointIdentifier()));
                    componentPointId = litePoint.getPointID();
                } else {
                    // We are done with this value. Clear it out.
                    // TODO: make this thing not be stateful like this
                    calcPointComponent.setPointId(null);
                }
                String componentType = calcPointComponent.getCalcComponentType();
                String operation = calcPointComponent.getOperation();

                calcComponents.add(new CalcComponent(pointId,
                                                     order++,
                                                     componentType,
                                                     componentPointId,
                                                     operation,
                                                     0.0,
                                                     "(none)"));
            }
            ((CalculatedPoint) point).setCalcComponents(calcComponents);
        }

        return point;
    }
    
    /**
     * @return The point with the specified point id, or null if it doesn't exist.
     */
    public static final PointBase findPoint(Integer id) {
        Connection conn = null;
        PointBase returnVal = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            Point p = new Point();
            p.setPointID(id);

            p.setDbConnection(conn);
            p.retrieve();
            p.setDbConnection(null);

            returnVal = createPoint(PointTypes.getType(p.getPointType()));
            returnVal.setPointID(id);

            returnVal.setDbConnection(conn);
            returnVal.retrieve();
            returnVal.setDbConnection(null);
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e2) {}
        }

        return returnVal;
    }
    
    public static synchronized void addPoint(PointBase point) {
        Connection connection = null;
        try {
            connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            point.setDbConnection(connection);
            DaoFactory.getDbPersistentDao().performDBChange(point, TransactionType.INSERT);
        } catch (PersistenceException te) {
            CTILogger.error(te);
        }
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }
    }

}