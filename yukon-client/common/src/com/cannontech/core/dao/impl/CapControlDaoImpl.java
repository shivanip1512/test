package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CBCPointGroup;
import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class CapControlDaoImpl implements CapControlDao {

    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoAuthorizationService paoAuthorizationService;

    @Override
    public Map<CBCPointGroup, List<LitePoint>> getSortedCBCPointTimeStamps(Integer cbcId) {

        List<LitePoint> allPoints = new ArrayList<>(pointDao.getLitePointsByPaObjectId(cbcId));
        
        Collections.sort(allPoints, new Comparator<LitePoint>() {

            @Override
            public int compare(LitePoint o1, LitePoint o2) {
                return o1.getPointName().compareTo(o2.getPointName());
            }
        });
        
        
        List<LitePoint> analogList = new ArrayList<>();
        List<LitePoint> configList = new ArrayList<>();
        List<LitePoint> accumList = new ArrayList<>();
        List<LitePoint> statusList = new ArrayList<>();
        List<LitePoint> miscList = new ArrayList<>();

        for (LitePoint point : allPoints) {
            
            switch (point.getPointTypeEnum()) {
            case Analog:
            case AnalogOutput:
            case CalcAnalog:
                
                if (point.getPointOffset() > 10000) {
                    configList.add(point);
                } else {
                    analogList.add(point);
                }
                break;
                
            case DemandAccumulator:
            case PulseAccumulator:
                
                accumList.add(point);
                break;
                
            case Status:
            case CalcStatus:
            case StatusOutput:
                
                statusList.add(point);
                break;
                
            default:
                
                miscList.add(point);
                break;
            }
        }
        
        Map<CBCPointGroup, List<LitePoint>> result = ImmutableMap.of(
            CBCPointGroup.ANALOG, analogList,
            CBCPointGroup.ACCUMULATOR, accumList,
            CBCPointGroup.STATUS, statusList,
            CBCPointGroup.CONFIGURABLE_PARAMETERS, configList,
            CBCPointGroup.MISC, miscList
        );
        
        return result;
    }

    @Override
    public List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user) {
        List<LiteYukonPAObject> subList = new ArrayList<>(10);

        List<LiteYukonPAObject> temp = paoDao.getAllCapControlSubBuses();
        for (Iterator<LiteYukonPAObject> iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = iter.next();

            if (paoAuthorizationService.isAuthorized(user, Permission.PAO_VISIBLE, element)) {
                subList.add(element);
            }
        }
        return subList;
    }

    @Override
    public List<LitePoint> getPaoPoints(YukonPAObject pao) {
        return pointDao.getLitePointsByPaObjectId(pao.getPAObjectID());
    }

    @Override
    public CapControlType getCapControlType(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT type FROM YukonPAObject WHERE PAObjectID").eq(id);

        String typeStr = yukonJdbcTemplate.queryForString(sql);

        return CapControlType.getCapControlType(typeStr);
    }

    @Override
    public List<OrphanCBC> getOrphanedCBCs() {

        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("select y.paoname as devicename ");
        query.append(", p.pointid as pointid ");
        query.append(", p.pointname as pointname ");
        query.append("from point p ");
        query.append(", yukonpaobject y ");
        query.append("where p.paobjectid = y.paobjectid ");
        query.append("and y.type like 'CBC%'  ");
        query.append("and y.paobjectid not in (select controldeviceid from capbank) ");
        query.append("and p.pointoffset = 1  ");
        query.append("and p.pointtype = 'Status' ");
        query.append("order by y.paoname ");

        List<OrphanCBC> cbcList = yukonJdbcTemplate.query(query, new YukonRowMapper<OrphanCBC>() {
            @Override
            public OrphanCBC mapRow(YukonResultSet rs) throws SQLException {
                String deviceName = rs.getString("devicename");
                Integer pointId = rs.getInt("pointid");
                String pointName = rs.getString("pointname");
                OrphanCBC cbc = new OrphanCBC(deviceName, pointId, pointName);
                return cbc;
            }
        });
        return cbcList;
    }

    @Override
    public List<LiteCapBankAdditional> getCapBankAdditional(List<Integer> deviceIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.DeviceID, ca.DriveDirections, cbc.SERIALNUMBER");
        sql.append("FROM CAPBANKADDITIONAL ca");
        sql.append("JOIN CAPBANK bank on ca.DeviceID = bank.DEVICEID");
        sql.append("left outer join DeviceCBC cbc on bank.CONTROLDEVICEID = cbc.deviceid");

        List<LiteCapBankAdditional> capbanks = Lists.newArrayList();

        if (deviceIds.size() > 0) {
            sql.append("WHERE bank.DeviceID").in(deviceIds);

            capbanks = yukonJdbcTemplate.query(sql, new YukonRowMapper<LiteCapBankAdditional>() {
                @Override
                public LiteCapBankAdditional mapRow(YukonResultSet rs) throws SQLException {
                    int deviceId = rs.getInt("DeviceID");
                    String drivingDirections = rs.getString("DriveDirections");
                    Integer serialNumber = rs.getInt("SERIALNUMBER");

                    LiteCapBankAdditional capBank = new LiteCapBankAdditional();

                    capBank.setDeviceId(deviceId);
                    capBank.setDrivingDirections(drivingDirections);
                    capBank.setSerialNumber(serialNumber);

                    return capBank;
                }
            });

        }
        return capbanks;
    }

    @Override
    public List<CCEventLog> getEventsForPao(StreamableCapObject streamable, int prevDaysCount) {
        PaoType type = PaoType.getForDbString(streamable.getCcType());
        int id = streamable.getCcId();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LogId, PointId, DateTime, SubId, FeederId, EventType, SeqId, Value, Text, Username");
        sql.append("FROM").append(CCEventLog.TABLE_NAME).append("WHERE");

        LocalDateTime date = LocalDateTime.now().minusDays(prevDaysCount).withTime(0, 0, 0, 0);

        if (type == PaoType.CAP_CONTROL_FEEDER) {
            sql.append("FeederId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SUBBUS) {
            sql.append("SubId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SUBSTATION) {
            sql.append("StationId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_AREA) {
            sql.append("AreaId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            sql.append("SPAreaId").eq(id);
        } else if (type == PaoType.CAPBANK) {
            int pointId = ((CapBankDevice) streamable).getStatusPointID();
            sql.append("PointId").eq(pointId);
        }
        sql.append("AND DateTime").gte(date.toDate());

        sql.append("ORDER BY " + CCEventLog.COLUMNS[CCEventLog.COL_DATETIME] + " DESC");

        return yukonJdbcTemplate.query(sql, new YukonRowMapper<CCEventLog>() {
            @Override
            public CCEventLog mapRow(YukonResultSet rs) throws SQLException {
                CCEventLog row = new CCEventLog();
                row.setLogId(rs.getLong("LogId"));
                row.setPointId(rs.getLong("PointId"));
                row.setDateTime(rs.getDate("DateTime"));
                row.setSubId(rs.getLong("SubId"));
                row.setFeederId(rs.getLong("FeederId"));
                row.setEventType(rs.getInt("EventType"));
                row.setSeqId(rs.getLong("SeqId"));
                row.setValue(rs.getLong("Value"));
                row.setText(rs.getString("Text"));
                row.setUserName(rs.getString("Username"));
                return row;
            }
        });
    }

}