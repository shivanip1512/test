package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAccumulator;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.spring.YukonSpringHook;

public final class PointFactory {

    public final static PointBase createPoint(int type) {

        PointBase retPoint = null;
        switch (type) {
        case PointTypes.ANALOG_POINT:
            retPoint = new AnalogPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.ANALOG_POINT));
            break;
        case PointTypes.PULSE_ACCUMULATOR_POINT:
            retPoint = new AccumulatorPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.PULSE_ACCUMULATOR_POINT));
            break;
        case PointTypes.DEMAND_ACCUMULATOR_POINT:
            retPoint = new AccumulatorPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT));
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
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.CALCULATED_STATUS_POINT));
            break;
        case PointTypes.SYSTEM_POINT:
            retPoint = new SystemPoint();
            retPoint.getPoint().setPointType(PointTypes.getType(PointTypes.SYSTEM_POINT));
            break;
        default: // this is bad
            throw new Error("PointFactory::createPoint - Unrecognized point type"); // this is also bad
        }

        return retPoint;
    }

    public static final PointBase retrievePoint(Integer id) {

        return retrievePoint(id, CtiUtilities.getDatabaseAlias());
    }

    public static final PointBase retrievePoint(Integer id, String databaseAlias) {

        Connection conn = null;
        PointBase returnVal = null;

        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

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
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {}
        }

        return returnVal;
    }

    public static PointBase createAnalogPoint(String pointName, Integer paoID, Integer pointID, int pointOffset,
            int pointUnit, int stateGroupId) {

        return createAnalogPoint(pointName, paoID, pointID, pointOffset, pointUnit, 1.0,
            PointTemplate.DEFAULT_DATA_OFFSET, stateGroupId, PointUnit.DEFAULT_DECIMAL_PLACES, PointArchiveType.NONE,
            PointArchiveInterval.ZERO);
    }

    public static PointBase createAnalogPoint(String pointName, Integer paoID, Integer pointID, int pointOffset,
            int pointUnit, double multiplier, double dataOffset, int stateGroupId, int decimalPlaces,
            PointArchiveType pointArchiveType, PointArchiveInterval pointArchiveInterval) {
        
        PointBase point = createNewPoint(pointID, PointTypes.ANALOG_POINT, pointName, paoID, pointOffset);

        point.getPoint().setArchiveType(pointArchiveType);
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId);

        // defaults - pointUnit
        ((AnalogPoint) point).setPointUnit(new PointUnit(pointID, pointUnit, decimalPlaces,
            CtiUtilities.INVALID_MAX_DOUBLE, CtiUtilities.INVALID_MIN_DOUBLE, 0));

        ((AnalogPoint) point).getPointAnalog().setMultiplier(multiplier);
        ((AnalogPoint) point).getPointAnalog().setDataOffset(dataOffset);

        return point;
    }

    public static PointBase createDmdAccumPoint(String pointName, Integer paoID, Integer pointID, int pointOffset,
            int pointUnit, double multiplier, int stateGroupId, int decimalPlaces, PointArchiveType pointArchiveType,
            PointArchiveInterval pointArchiveInterval) {
        
        PointBase point = createNewPoint(pointID, PointTypes.DEMAND_ACCUMULATOR_POINT, pointName, paoID, pointOffset);

        point.getPoint().setArchiveType(pointArchiveType);
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId);

        // defaults - pointAccumulator
        PointAccumulator accumPt = new PointAccumulator(pointID, multiplier, 0.0);

        ((AccumulatorPoint) point).setPointAccumulator(accumPt);

        // defaults - pointUnit
        ((AccumulatorPoint) point).setPointUnit(new PointUnit(pointID, pointUnit, decimalPlaces, 0.0, 0.0, 0));

        return point;
    }

    public final static PointBase createNewPoint(Integer pointID, int pointType, String pointName, Integer paoID,
            Integer offset) {

        // A point is automatically created here
        PointBase newPoint = createPoint(pointType);

        // set default point values for point tables
        newPoint.setPoint(new Point(pointID,
                                    PointTypes.getType(pointType),
                                    pointName,
                                    paoID,
                                    PointLogicalGroups.DEFAULT.getDbValue(),
                                    0,
                                    CtiUtilities.getFalseCharacter(),
                                    CtiUtilities.getFalseCharacter(),
                                    offset,
                                    PointArchiveType.NONE,
                                    PointArchiveInterval.ZERO.getSeconds()));

        newPoint.setPointAlarming(new PointAlarming(pointID, 
                                                    PointAlarming.DEFAULT_ALARM_STATES, 
                                                    PointAlarming.DEFAULT_EXCLUDE_NOTIFY, 
                                                    "N",
                                                    PointAlarming.NONE_NOTIFICATIONID));

        newPoint.setPointID(pointID);

        return newPoint;
    }

    public static PointBase createPulseAccumPoint(String pointName, Integer paoID, Integer pointID, int pointOffset,
            int pointUnit, double multiplier, int stateGroupId, int decimalPlaces, PointArchiveType pointArchiveType,
            PointArchiveInterval pointArchiveInterval) {

        PointBase point =
            createNewPoint(pointID, PointTypes.PULSE_ACCUMULATOR_POINT, pointName, paoID, pointOffset);

        point.getPoint().setArchiveType(pointArchiveType);
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        point.getPoint().setStateGroupID(stateGroupId);
        
        // defaults - pointAccumulator
        PointAccumulator accumPt = new PointAccumulator(pointID, multiplier, 0.0);

        ((AccumulatorPoint) point).setPointAccumulator(accumPt);

        // defaults - pointUnit
        ((AccumulatorPoint) point).setPointUnit(new PointUnit(pointID, pointUnit, decimalPlaces,
            CtiUtilities.INVALID_MAX_DOUBLE, CtiUtilities.INVALID_MIN_DOUBLE, 0));

        return point;
    }

    public static synchronized PointBase createStatusPoint(String pointName, Integer paoID, Integer pointID,
            int pointOffset, int stateGroupId, int initialState, Integer controlOffset, StatusControlType controlType,
            String stateZeroControl, String stateOneControl, PointArchiveType pointArchiveType,
            PointArchiveInterval pointArchiveInterval) {
        
        // Create new point
        PointBase newPoint = createNewPoint(pointID, PointTypes.STATUS_POINT, pointName, paoID, pointOffset);

        newPoint.getPoint().setArchiveType(pointArchiveType);
        newPoint.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());

        newPoint.getPoint().setStateGroupID(stateGroupId);

        ((StatusPoint) newPoint).getPointStatus().setInitialState(initialState);

        if (controlOffset != null) {
            ((StatusPoint) newPoint).getPointStatusControl().setControlOffset(controlOffset);
        }

        ((StatusPoint) newPoint).getPointStatusControl().setControlType(controlType.getControlName());

        if (stateZeroControl != null) {
            ((StatusPoint) newPoint).getPointStatusControl().setStateZeroControl(stateZeroControl);
        }
        if (stateOneControl != null) {
            ((StatusPoint) newPoint).getPointStatusControl().setStateOneControl(stateOneControl);
        }

        return newPoint;
    }

    /**
     * Creates a CapBanks status point automatically
     * 
     */
    public static synchronized PointBase createBankStatusPt(Integer capBankID) {

        // a status point is created
        PointBase newPoint = createNewPoint(null, PointTypes.STATUS_POINT, "BANK STATUS", capBankID, 1);

        newPoint.getPoint().setStateGroupID(3);

        return newPoint;
    }

    public static PointBase createCalcStatusPoint(Integer paoId, String name, int stateGroupId, int pointOffset) {

        PointBase newPoint = createNewPoint(null, PointTypes.CALCULATED_STATUS_POINT, name, paoId, pointOffset);

        newPoint.getPoint().setStateGroupID(stateGroupId);

        ((CalcStatusPoint) newPoint).getCalcBase().setPeriodicRate(1);
        ((CalcStatusPoint) newPoint).getCalcBase().setUpdateType("On All Change");

        return newPoint;

    }

    public static PointBase createCalculatedPoint(PaoIdentifier paoIdentifier, String name, int stateGroupId, int pointOffset) {

        return createCalculatedPoint(paoIdentifier, name, stateGroupId, UnitOfMeasure.UNDEF.getId(),
            PointUnit.DEFAULT_DECIMAL_PLACES, PointArchiveType.NONE, PointArchiveInterval.ZERO, null, pointOffset);
    }

    /**
     * This method only supports creating calculated points that contain inner CalcPointComponent's
     * that reference their own pao
     */
    public static PointBase createCalculatedPoint(PaoIdentifier paoIdentifier, String name, int stateGroupId,
            int unitOfMeasure, int decimalPlaces, PointArchiveType pointArchiveType,
            PointArchiveInterval pointArchiveInterval, CalcPointInfo calcPoint, int pointOffset) {
        
        PointBase point = createNewPoint(null, PointTypes.CALCULATED_POINT, name, paoIdentifier.getPaoId(), pointOffset);

        point.getPoint().setArchiveType(pointArchiveType);
        point.getPoint().setArchiveInterval(pointArchiveInterval.getSeconds());
        point.getPoint().setStateGroupID(stateGroupId);
        
        PointUnit punit = new PointUnit(null, unitOfMeasure, decimalPlaces, 
            CtiUtilities.INVALID_MAX_DOUBLE, CtiUtilities.INVALID_MIN_DOUBLE, 0);

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
                calcBase.setPeriodicRate(1);
            }
        } else {
            calcBase.setUpdateType(PointTypes.UPDATE_FIRST_CHANGE);
            calcBase.setPeriodicRate(1);
        }

        ((CalculatedPoint) point).setCalcBase(calcBase);
        ((CalculatedPoint) point).setBaselineAssigned(false);

        if (calcPoint != null) {
            PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);

            int order = 1;
            List<CalcComponent> calcComponents = new ArrayList<>();
            Integer pointId = point.getPoint().getPointID();
            for (CalcPointComponent calcPointComponent : calcPoint.getComponents()) {
                Integer componentPointId = calcPointComponent.getPointId();
                if (componentPointId == null) {
                    
                    // If this CalcPointComponent's pointId isn't set by now... we assume it's
                    // pointIdentifier refers to this same paoIdentifier
                    LitePoint litePoint = pointDao.getLitePoint(new PaoPointIdentifier(paoIdentifier,
                            calcPointComponent.getPointIdentifier()));
                    
                    componentPointId = litePoint.getPointID();
                } else {
                    // We are done with this value. Clear it out.
                    // TODO: make this thing not be stateful like this
                    calcPointComponent.setPointId(null);
                }
                
                String componentType = calcPointComponent.getCalcComponentType();
                String operation = calcPointComponent.getOperation();

                calcComponents.add(new CalcComponent(pointId, order++, componentType, componentPointId, operation, 0.0,
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
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {}
        }

        return returnVal;
    }

    public static synchronized void addPoint(PointBase point) {
        Connection connection = null;
        try {
            connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            point.setDbConnection(connection);
            YukonSpringHook.getBean(DBPersistentDao.class).performDBChange(point, TransactionType.INSERT);
        } catch (PersistenceException te) {
            CTILogger.error(te);
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }
        }
    }

}