package com.cannontech.cbc.point;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.AccumPointParams;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPointParams;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
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
                                  "Capacitor Bank State",
                                  PointTypes.CONTROLTYPE_NORMAL),
            new StatusPointParams(2, "Re-Close Blocked"),
            new StatusPointParams(3, "Control Mode"),
            new StatusPointParams(4, "Auto Volt Control"),
            new StatusPointParams(5, "Last Control - Local"),
            new StatusPointParams(6, "Last Control - Remote"),
            new StatusPointParams(7, "Last Control - OVUV"),
            new StatusPointParams(8, "Last Control - Neutral Fault"),
            new StatusPointParams(9, "Last Control - Scheduled"),
            new StatusPointParams(10, "Last Control - VAR"),
            new StatusPointParams(11, "Last Control - Analog"),
            new StatusPointParams(12, "Last Control - Temperature"),
            new StatusPointParams(13, "OV Condition"),
            new StatusPointParams(14, "UV Condition"),
            new StatusPointParams(15, "Op Failed - Neutral Current"),
            new StatusPointParams(16, "Neutral Current Fault"),
            new StatusPointParams(17, "VAR Alarm"),
            new StatusPointParams(24, "Bad Relay"),
            new StatusPointParams(25, "Daily Max Ops"),
            new StatusPointParams(26, "Voltage Delta Abnormal"),
            new StatusPointParams(27, "Temp Alarm"),
            new StatusPointParams(28, "DST Active"),
            new StatusPointParams(29, "Neutral Lockout"),
            new StatusPointParams(30, "Power Flow"),
            new StatusPointParams(34, "Control Ignored Indicator"),

            // analog
            new AnalogPointParams(1.0, 3, "Firmware Version",PointUnits.UOMID_UNDEF),
			new AnalogPointParams(0.1, 5, "Voltage", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(0.1, 6, "High Voltage", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(0.1, 7, "Low Voltage", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(0.1, 8, "Delta Voltage", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 9, "Analog Input 1", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(0.1, 10, "Temperature", PointUnits.UOMID_TEMP_F),
			new AnalogPointParams(1.0, 13, "RSSI", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 14, "Control Ignored Reason", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 15, "CDMA Rx Power", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 16, "CDMA Ec/Io", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 17, "True VAR RMS Current", PointUnits.UOMID_AMPS),
			new AnalogPointParams(1.0, 18, "3 Phase kVARs", PointUnits.UOMID_KVAR),
			new AnalogPointParams(1.0, 19, "VAR RMS Voltage", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 20, "VAR 1 Phase kVA", PointUnits.UOMID_KVA),
			new AnalogPointParams(1.0, 21, "VAR 1 Phase kWatts", PointUnits.UOMID_KW),
			new AnalogPointParams(1.0, 22, "VAR Power Factor", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 10002, "UV Threshold", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 10003, "OV Threshold", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 10004, "Control OVUV Track Time", PointUnits.UOMID_SECONDS),
			new AnalogPointParams(1.0, 10006, "Daily Control Limit", PointUnits.UOMID_COUNTS),
			new AnalogPointParams(1.0, 10007, "Emergency UV Threshold", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 10008, "Emergency OV Threshold", PointUnits.UOMID_VOLTS),
			new AnalogPointParams(1.0, 10009, "Emergency OVUV Track Time", PointUnits.UOMID_SECONDS),
			new AnalogPointParams(1.0, 10010, "Neutral Current Sensor", PointUnits.UOMID_AMPS),
			new AnalogPointParams(1.0, 10011, "Neutral Current Alarm Threshold", PointUnits.UOMID_AMPS),
			new AnalogPointParams(1.0, 10015, "Trip Delay Time", PointUnits.UOMID_SECONDS),
			new AnalogPointParams(1.0, 10016, "Close Delay Time", PointUnits.UOMID_SECONDS),
			new AnalogPointParams(1.0, 10017, "Bank Control Time", PointUnits.UOMID_MINUTES),
            new AnalogPointParams(1.0,10018,"Re-Close Delay Time", PointUnits.UOMID_SECONDS),
            new AnalogPointParams(1.0,10057,"Comms Loss Time", PointUnits.UOMID_SECONDS),
            new AnalogPointParams(1.0,10110,"Comms Retry Delay Time", PointUnits.UOMID_SECONDS),
            new AnalogPointParams(1.0,10111,"Yukon Poll Time", PointUnits.UOMID_SECONDS),
            new AnalogPointParams(1.0,10113,"VAR Close Point", PointUnits.UOMID_KVAR),
            new AnalogPointParams(1.0,10114,"VAR Trip Point", PointUnits.UOMID_KVAR),
    		new AnalogPointParams(1.0, 20001, "IP Address", PointUnits.UOMID_UNDEF),
			new AnalogPointParams(1.0, 20002, "UDP Port", PointUnits.UOMID_UNDEF),

            // accumulator
			new AccumPointParams(1.0, 1, "Total Op Count", PointUnits.UOMID_COUNTS),
			new AccumPointParams(1.0, 2, "UV Op Count", PointUnits.UOMID_COUNTS),
			new AccumPointParams(1.0, 3, "OV Op Count", PointUnits.UOMID_COUNTS)

    };

    public CBCPointFactory() {
        super();

    }

    public static MultiDBPersistent createPointsForCBCDevice(YukonPAObject deviceCBC) {

        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();

        Integer paoId = deviceCBC.getPAObjectID();
        switch (PAOGroups.getDeviceType(deviceCBC.getPAOType())) {
	        case PAOGroups.CBC_7020:
	        case PAOGroups.CBC_7022:
	        case PAOGroups.CBC_7023:
	        case PAOGroups.CBC_7024: 
	        case PAOGroups.CBC_8020:
	        case PAOGroups.CBC_8024: {
	        
	            dbPersistentVector = (MultiDBPersistent) createPointsForCBCDevice(paoId);
	            break;
	        }
	        case PAOGroups.CBC_DNP: {
	            dbPersistentVector = (MultiDBPersistent) createPointsForCBCDNPDevice(paoId);
	            break;
	        }
	        default: {
	        	//One way
	        	PointBase statusPt = CapBankController.createStatusControlPoint(paoId);
	        	dbPersistentVector.getDBPersistentVector().add(statusPt);
	        }
        }

        return dbPersistentVector;
    }

    public static DBPersistent createPointsForCBCDevice(Integer paoId) {
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        dbPersistentVector.getDBPersistentVector().addAll(createPoints(paoId));
        return dbPersistentVector;
    }
    
    public static DBPersistent createPointsForCBCDNPDevice(Integer paoId) {
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        PointBase point = PointFactory.createPoint(PointTypes.STATUS_POINT);
        
        point = PointFactory.createNewPoint(point.getPoint()
                                            .getPointID(),
                                       PointTypes.STATUS_POINT,
                                       CBC_POINT_PROTOTYPES[0].getName(),
                                       paoId,
                                       new Integer(CBC_POINT_PROTOTYPES[0].getOffset()));
        
        setGrpForStatusPoint(CBC_POINT_PROTOTYPES[0], point);
        PointStatus pointStatus = (((StatusPoint) point).getPointStatus());
        int controlType = ((StatusPointParams) CBC_POINT_PROTOTYPES[0]).getControlType();
        pointStatus.setControlType(PointTypes.getType(controlType));
        
        dbPersistentVector.getDBPersistentVector().add(point);
        return dbPersistentVector;
    }
    
    private static List<PointBase> createPoints(Integer paoId) {

        List<PointBase> pointList = new ArrayList<PointBase>();
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
                                                       ((AnalogPointParams) param).getMult(), 
                                                       StateGroupUtils.STATEGROUP_ANALOG,
                                                       PointUnit.DEFAULT_DECIMAL_PLACES,
                                                       PointArchiveType.NONE,
                                                       PointArchiveInterval.ZERO);

                break;

            case PointTypes.PULSE_ACCUMULATOR_POINT:

                point = PointFactory.createPoint(PointTypes.PULSE_ACCUMULATOR_POINT);
                point = PointFactory.createPulseAccumPoint(param.getName(),
                                                           paoId,
                                                           point.getPoint()
                                                                .getPointID(),
                                                           param.getOffset(),
                                                           ((AccumPointParams) param).getUofm(),
                                                           ((AccumPointParams) param).getMult(), 
                                                           StateGroupUtils.STATEGROUP_ANALOG,
                                                           PointUnit.DEFAULT_DECIMAL_PLACES,
                                                           PointArchiveType.NONE,
                                                           PointArchiveInterval.ZERO);

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

    public static SmartMultiDBPersistent createPointsForPAO(int paoID, Connection connection) {
        SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();

        for (int i = 0; i < PAO_POINT_PARAMS.length; i++) {
            AnalogPointParams params = (AnalogPointParams) PAO_POINT_PARAMS[i];
            
            PointDao pointDAO = DaoFactory.getPointDao();
            PointBase analogPoint = PointFactory.createAnalogPoint(params.getName(),
                                                                   paoID,
                                                                   pointDAO.getNextPointId(),
                                                                   params.getOffset(),
                                                                   params.getUofm(), 
                                                                   StateGroupUtils.STATEGROUP_ANALOG);

            analogPoint.setDbConnection(connection);
            retSmart.addDBPersistent(analogPoint);
        }

        return retSmart;
    }

    public static LitePoint getTagPointFromCache(final Integer objectId, final Map<Integer,List<LitePoint>> pointCache) {
        List<LitePoint> pointList = pointCache.get(objectId);
        return getTagPoint(objectId, pointList);
    }
    
    public static LitePoint getTagPoint(final Integer objectId) {
        List<LitePoint> pointList = DaoFactory.getPointDao().getLitePointsByPaObjectId(objectId);
        return getTagPoint(objectId, pointList);
    }
    
    private static LitePoint getTagPoint(final Integer objectId, final List<LitePoint> pointList) {
        LitePoint tagPoint = null;
        for (final LitePoint point : pointList) {
            if ((point.getPointType() == PointTypes.STATUS_POINT) && point.getPointName().equalsIgnoreCase(PointFactory.PTNAME_TAG)){
                tagPoint = point;
                break;
            }
        }
        if (tagPoint == null) {
            tagPoint = createTagPoint(objectId);
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
