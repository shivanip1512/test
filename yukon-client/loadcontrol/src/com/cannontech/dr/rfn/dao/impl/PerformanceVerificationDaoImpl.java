package com.cannontech.dr.rfn.dao.impl;

import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.SUCCESS;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.SUCCESS_UNENROLLED;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.UNKNOWN;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.UNSUCCESS;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.google.common.collect.Maps;

public class PerformanceVerificationDaoImpl implements PerformanceVerificationDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<PerformanceVerificationEventMessageStats> getReports(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Result, rbed.RfnBroadcastEventId, Count(*) as count");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("WHERE EventSendTime").gt(range.getMin());
        sql.append("AND EventSendTime").lt(range.getMax());
        sql.append("GROUP BY Result, rbed.RfnBroadcastEventId");

        final Map<Long, Map<PerformanceVerificationMessageStatus, Integer>> stats = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Long messageId = rs.getLong("RfnBroadcastEventId");

                if(!stats.containsKey(messageId)) {
                    Map<PerformanceVerificationMessageStatus, Integer> counts = 
                            Maps.newHashMapWithExpectedSize(PerformanceVerificationMessageStatus.values().length);
                    for (PerformanceVerificationMessageStatus status : PerformanceVerificationMessageStatus.values()) {
                        counts.put(status, 0);
                    }
                    stats.put(messageId, counts);
                }
                stats.get(messageId).put(rs.getEnum("Result",
                                         PerformanceVerificationMessageStatus.class), rs.getInt("count"));
            }
        });

        List<PerformanceVerificationEventMessage> messagesSent = getEventMessages(range);
        List<PerformanceVerificationEventMessageStats> reports = new ArrayList<>();
        for (PerformanceVerificationEventMessage messageSent : messagesSent) {
            Map<PerformanceVerificationMessageStatus, Integer> counts = stats.get(messageSent.getMessageId());
            reports.add(new PerformanceVerificationEventMessageStats(messageSent.getMessageId(),
                                                                 messageSent.getTimeMessageSent(),
                                                                 counts.get(SUCCESS),
                                                                 counts.get(UNSUCCESS),
                                                                 counts.get(UNKNOWN)));
        }

        return reports;
    }

    @Override
    public PerformanceVerificationEventStats getAverageReport(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Result, Count(*) as count");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("WHERE EventSendTime").gt(range.getMin());
        sql.append("AND EventSendTime").lt(range.getMax());
        sql.append("GROUP BY Result");

        final Map<PerformanceVerificationMessageStatus, Integer> counts =
                Maps.newHashMapWithExpectedSize(PerformanceVerificationMessageStatus.values().length);
        for (PerformanceVerificationMessageStatus status : PerformanceVerificationMessageStatus.values()) {
            counts.put(status, 0);
        }
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                counts.put(rs.getEnum("Result", PerformanceVerificationMessageStatus.class), rs.getInt("count"));
            }
        });

        return new PerformanceVerificationEventStats(counts.get(SUCCESS), counts.get(UNSUCCESS), counts.get(UNKNOWN));
    }

    @Override
    public List<PerformanceVerificationEventMessage> getEventMessages(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId, EventSendTime");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE EventSendTime").gt(range.getMin());
        sql.append("AND EventSendTime").lt(range.getMax());

        return jdbcTemplate.query(sql, new YukonRowMapper<PerformanceVerificationEventMessage>() {
            @Override
            public PerformanceVerificationEventMessage mapRow(YukonResultSet rs) throws SQLException {
                return new PerformanceVerificationEventMessage(rs.getLong("RfnBroadcastEventId"),
                                                                          rs.getInstant("EventSendTime"));
            }
        });
    }
    
    @Override
    public Set<Integer> getDevicesWithUnknownStatus(long messageId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("WHERE rbed.RfnBroadcastEventId").eq(messageId);
        sql.append("AND Result").eq_k(UNKNOWN);

        return new HashSet<>(jdbcTemplate.query(sql, RowMapper.INTEGER));
    }

    @Override
    public PerformanceVerificationEventMessage createVerificationEvent() {
        int nextId = nextValueHelper.getNextValue("RfnBroadcastEvent");
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("RfnBroadcastEvent");
        
        params.addValue("RfnBroadcastEventId", nextId);
        params.addValue("EventSendTime", now);
        
        jdbcTemplate.update(sql);
        
        return new PerformanceVerificationEventMessage(nextId, now);
    }
    
    @Override
    public void writeNewVerificationEventForEnrolledDevices(long messageId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO RfnBroadcastEventDeviceStatus (DeviceId, RfnBroadcastEventId, Result)");
        sql.append("(SELECT DISTINCT PAO.PaobjectId, ").appendArgument(messageId).append(",").appendArgument_k(UNKNOWN);
        sql.append(" FROM LMHardwareControlGroup LM");
        sql.append("    JOIN InventoryBase IB ON IB.InventoryID = LM.InventoryID");
        sql.append("    JOIN YukonPAObject PAO ON PAO.PAObjectID = IB.DeviceID");
        sql.append(" WHERE PAO.PAObjectID != 0");
        sql.append("    AND LM.GroupEnrollStart IS NOT NULL");
        sql.append("    AND LM.GroupEnrollStop IS NULL");
        sql.append("    AND PAO.Type").in(PaoType.getRfLcrTypes()).append(")");
        
        System.out.println(sql.getDebugSql());
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Long> getEventIdsForDevice(int deviceId, List<Long> eventIds) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId");
        sql.append("FROM RfnBroadcastEventDeviceStatus");
        sql.append("WHERE  DeviceId").eq(deviceId);
        sql.append("AND RfnBroadcastEventId").in(eventIds);
		return jdbcTemplate.query(sql, RowMapper.LONG);
    }
    
    @Override
    public List<Long> getEventIds(List<Long> eventIds) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE RfnBroadcastEventId").in(eventIds);
		return jdbcTemplate.query(sql, RowMapper.LONG);
    }
    
    @Override
    public void createUnenrolledEventResult(int deviceId, long messageId, Instant deviceReceivedTime) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("RfnBroadcastEventDeviceStatus");
                
        params.addValue("DeviceId", deviceId);
        params.addValue("RfnBroadcastEventId", messageId);
        params.addValue("Result", SUCCESS_UNENROLLED);
        params.addValue("DeviceReceivedTime", deviceReceivedTime);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateSuccessEventResult(int deviceId, Map<Long, Instant> verificationMsgs) {
		for (long eventId : verificationMsgs.keySet()) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			SqlParameterSink params = sql.update("RfnBroadcastEventDeviceStatus");
			params.addValue("Result", SUCCESS);
			params.addValue("DeviceReceivedTime", verificationMsgs.get(eventId));
			sql.append("WHERE  DeviceId").eq(deviceId);
			sql.append("AND RfnBroadcastEventId").eq(eventId);
			sql.append("AND Result").neq(SUCCESS);

			jdbcTemplate.update(sql);
		}
    }
    
    @Override
	public void updateUnsuccessEventResult(int deviceId, Range<Instant> range) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink params = sql.update("RfnBroadcastEventDeviceStatus");
		params.addValue("Result", UNSUCCESS);
		sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
		sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
		sql.append("WHERE DeviceId").eq(deviceId);
		sql.append("AND Result").eq(UNKNOWN);
		sql.append("AND EventSendTime").gte(range.getMin());
		sql.append("AND EventSendTime").lt(range.getMax());
		
		jdbcTemplate.update(sql);
	}
}
