package com.cannontech.cbc.point;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.AccumPointParams;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.point.StatusPointParams;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;

public class CBCPointFactory {
    public static final String GRP_TRUE_FALSE = "TrueFalse";
    public static final String GRP_REMOTE_LOCAL = "RemoteLocal";
    private static Integer DEFAULT_STATUS_GRPID = new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS);

    private static final PointParams[] PAO_POINT_PARAMS = {
        new AnalogPointParams(1.0, 1, "Estimated Var Load", PointUnits.UOMID_VARS),
        new AnalogPointParams(1.0, 2, "Daily Operations", PointUnits.UOMID_OPS),
        new AnalogPointParams(1.0, 3, "Power Factor", PointUnits.UOMID_PF),
        new AnalogPointParams(1.0, 4, "Estimated Power Factor", PointUnits.UOMID_PF), 
        
};

    private static final PointParams[] CBC_POINT_PROTOTYPES = {
            new StatusPointParams(1,
                                  "Capacitor bank state",
                                  PointTypes.CONTROLTYPE_NORMAL),
            new StatusPointParams(2, "Re-close Blocked"),
            new StatusPointParams(3, "Control Mode"),
            new StatusPointParams(4, "Auto Volt Control"),
            new StatusPointParams(5, "Last Control - Local"),
            new StatusPointParams(6, "Last Control -Remote"),
            new StatusPointParams(7, "Last Control - OVUV"),
            new StatusPointParams(8, "Last Control - Neutral Fault"),
            new StatusPointParams(9, "Last Control - Scheduled"),
            new StatusPointParams(10, "Last Control - Digital"),
            new StatusPointParams(11, "Last Control - Analog"),
            new StatusPointParams(12, "Last Control - Temperature"),
            new StatusPointParams(13, "OV Condition"),
            new StatusPointParams(14, "UV Condition"),
            new StatusPointParams(15, "Op Failed - Neutral Current"),
            new StatusPointParams(16, "Neutral Current Fault"),
            new StatusPointParams(24, "Bad Relay"),
            new StatusPointParams(25, "Daily Max Ops"),
            new StatusPointParams(26, "Voltage Delta Abnormal"),
            new StatusPointParams(27, "Temp Alarm"),
            new StatusPointParams(28, "DST Active"),
            new StatusPointParams(29, "Neutral Lockout"),
            new StatusPointParams(34, "Control Ignored Indicator"),

            // analog
            new AnalogPointParams(0.1, 5, "Voltage", PointUnits.UOMID_VOLTS),
            new AnalogPointParams(0.1,
                                  6,
                                  "High Voltage",
                                  PointUnits.UOMID_VOLTS),
            new AnalogPointParams(0.1, 7, "Low Voltage", PointUnits.UOMID_VOLTS),
            new AnalogPointParams(0.1,
                                  8,
                                  "Delta Voltage",
                                  PointUnits.UOMID_VOLTS),
            new AnalogPointParams(1.0,
                                  9,
                                  "Analog Input 1",
                                  PointUnits.UOMID_UNDEF),
            new AnalogPointParams(0.1,
                                  10,
                                  "Temperature",
                                  PointUnits.UOMID_TEMP_F),
            new AnalogPointParams(1.0,
                                13,
                                "RSSI",
                                PointUnits.UOMID_UNDEF),
            new AnalogPointParams(1.0,
                              14,
                              "Control Ignored Reason",
                              PointUnits.UOMID_UNDEF),
            new AnalogPointParams(1.0,
                                       10002,
                                        "Control UV Set Point",
                                        PointUnits.UOMID_VOLTS),
            new AnalogPointParams(1.0,
                                  10003,
                                   "Control OV Set Point",
                                   PointUnits.UOMID_VOLTS),
           new AnalogPointParams(1.0,
                                 10004,
                                  "Control OVUV Track Time",
                                  PointUnits.UOMID_SECONDS),
          new AnalogPointParams(1.0,
                                10010,
                                 "Neutral Current Sensor",
                                 PointUnits.UOMID_AMPS),
          new AnalogPointParams(1.0,
                               10011,
                                "Neutral Current Alarm Set Point",
                                PointUnits.UOMID_AMPS),
          new AnalogPointParams(1.0,
                               20001,
                               "IP Address",
                               PointUnits.UOMID_UNDEF),
           new AnalogPointParams(1.0,
                                 20002,
                                 "UDP Port",
                                 PointUnits.UOMID_UNDEF),

            // accumulator
            new AccumPointParams(1.0,
                                 1,
                                 "Total op count",
                                 PointUnits.UOMID_COUNTS),
            new AccumPointParams(1.0, 2, "UV op count", PointUnits.UOMID_COUNTS),
            new AccumPointParams(1.0, 3, "OV op count", PointUnits.UOMID_COUNTS)

    };

    public CBCPointFactory() {
        super();

    }

    public static MultiDBPersistent createPointsForCBCDevice(
            YukonPAObject deviceCBC) {

        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();

        Integer paoId = deviceCBC.getPAObjectID();
        switch (PAOGroups.getDeviceType(deviceCBC.getPAOType())) {

        case PAOGroups.CBC_7020:
        case PAOGroups.CBC_7022:
        case PAOGroups.CBC_7023:
        case PAOGroups.CBC_7024:

            dbPersistentVector = (MultiDBPersistent) createPointsForCBCDevice(paoId);
        }

        return dbPersistentVector;
    }

    public static DBPersistent createPointsForCBCDevice(Integer paoId) {
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        dbPersistentVector.getDBPersistentVector().addAll(createPoints(paoId));
        return dbPersistentVector;
    }

    private static List createPoints(Integer paoId) {

        List pointList = new ArrayList();
        for (int i = 0; i < CBC_POINT_PROTOTYPES.length; i++) {
            PointParams param = CBC_POINT_PROTOTYPES[i];
            PointBase point = PointFactory.createPoint(param.getType());
            switch (param.getType()) {
            case PointTypes.STATUS_POINT:
                point = PointFactory.createNewPoint(point.getPoint()
                                                         .getPointID(),
                                                    PointTypes.STATUS_POINT,
                                                    param.getName(),
                                                    paoId,
                                                    new Integer(param.getOffset()));
                setGrpForStatusPoint(param, point);
                PointStatus pointStatus = (((StatusPoint) point).getPointStatus());
                int controlType = ((StatusPointParams) param).getControlType();
                pointStatus.setControlType(PointTypes.getType(controlType));
                break;
            case PointTypes.ANALOG_POINT:
                point = PointFactory.createPoint(PointTypes.ANALOG_POINT);
                point = PointFactory.createAnalogPoint(param.getName(),
                                                       paoId,
                                                       point.getPoint()
                                                            .getPointID(),
                                                       param.getOffset(),
                                                       ((AnalogPointParams) param).getUofm(),
                                                       ((AnalogPointParams) param).getMult());

                break;

            case PointTypes.PULSE_ACCUMULATOR_POINT:

                point = PointFactory.createPoint(PointTypes.PULSE_ACCUMULATOR_POINT);
                point = PointFactory.createPulseAccumPoint(param.getName(),
                                                           paoId,
                                                           point.getPoint()
                                                                .getPointID(),
                                                           param.getOffset(),
                                                           ((AccumPointParams) param).getUofm(),
                                                           ((AccumPointParams) param).getMult());

                PointUnit punit = new PointUnit(point.getPoint().getPointID(),
                                                new Integer(PointUnits.UOMID_COUNTS),
                                                new Integer(0),
                                                new Double(0.0),
                                                new Double(0.0),
                                                new Integer(0));

                ((AccumulatorPoint) point).setPointUnit(punit);
                break;
            }
            pointList.add(point);
        }

        return pointList;

    }

    /**
     * @param param
     * @param p
     */
    private static void setGrpForStatusPoint(PointParams param, PointBase p) {
        Integer statusGrpID = null;
        int offset = param.getOffset();
        Point point = p.getPoint();
        if (offset == 1) {
            statusGrpID = DEFAULT_STATUS_GRPID;
        } else if (offset == 3) {
            statusGrpID = CBCUtils.getStateGroupIDByGroupName(GRP_REMOTE_LOCAL);
        } else {
            statusGrpID = CBCUtils.getStateGroupIDByGroupName(GRP_TRUE_FALSE);
        }
        statusGrpID = (statusGrpID == null) ? DEFAULT_STATUS_GRPID
                : statusGrpID;
        point.setStateGroupID(statusGrpID);
    }

    public static SmartMultiDBPersistent createPointsForPAO(DBPersistent dbObj) {
        SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();

        if ((dbObj instanceof CapControlSubBus) || (dbObj instanceof CapControlFeeder)) {
            for (int i = 0; i < PAO_POINT_PARAMS.length; i++) {
                AnalogPointParams params = (AnalogPointParams) PAO_POINT_PARAMS[i];
                Integer paoID = new Integer(0);

                if (dbObj instanceof CapControlSubBus) {
                    CapControlSubBus bus = (CapControlSubBus) dbObj;
                    paoID = bus.getPAObjectID();
                }
                if (dbObj instanceof CapControlFeeder) {
                    CapControlFeeder feeder = (CapControlFeeder) dbObj;
                    paoID = feeder.getPAObjectID();
                }

                PointDao pointDAO = DaoFactory.getPointDao();
                PointBase analogPoint = PointFactory.createAnalogPoint(params.getName(),
                                                                       paoID,
                                                                       pointDAO.getNextPointId(),
                                                                       params.getOffset(),
                                                                       params.getUofm());
                retSmart.addDBPersistent(analogPoint);
            }
        }
        return retSmart;
    }

    public static LitePoint getTagPoint(Integer objectID) {
        List<LitePoint> points = DaoFactory.getPointDao()
                                           .getLitePointsByPaObjectId(objectID);
        LitePoint tagPoint = null;
        for (LitePoint point : points) {
            if ((point.getPointType() == PointTypes.STATUS_POINT) && point.getPointName().equalsIgnoreCase(PointFactory.PTNAME_TAG)){
                tagPoint = point;
                break;
            }
        }
        if (tagPoint == null) {
            tagPoint = createTagPoint(objectID);
        }
        return tagPoint;
    }

    public static LitePoint createTagPoint(Integer objectID) {
        LitePoint tagPoint;
        Integer offset = new Integer(-1);
        PointBase point = PointFactory.createTagPoint(objectID, offset);
        PointFactory.addPoint(point);
        tagPoint = (LitePoint) LiteFactory.createLite(point);
        return tagPoint;
    }
}
