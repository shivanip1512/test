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

        sql.append("SELECT Result, rbed.RfBroadcastEventId, Count(*) as count");
        sql.append("FROM RfBroadcastEventDevice rbed");
        sql.append("JOIN RfBroadcastEvent rbe ON rbed.RfBroadcastEventId = rbe.RfBroadcastEventId");
        sql.append("WHERE SendTime").gt(range.getMin());
        sql.append("AND SendTime").lt(range.getMax());
        sql.append("GROUP BY Result, rbed.RfBroadcastEventId");

        final Map<Long, Map<PerformanceVerificationMessageStatus, Integer>> stats = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Long messageId = rs.getLong("RfBroadcastEventId");

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
        sql.append("FROM RfBroadcastEventDevice rbed");
        sql.append("JOIN RfBroadcastEvent rbe ON rbed.RfBroadcastEventId = rbe.RfBroadcastEventId");
        sql.append("WHERE SendTime").gt(range.getMin());
        sql.append("AND SendTime").lt(range.getMax());
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
        sql.append("SELECT RfBroadcastEventId, SendTime");
        sql.append("FROM RfBroadcastEvent");
        sql.append("WHERE SendTime").gt(range.getMin());
        sql.append("AND SendTime").lt(range.getMax());

        return jdbcTemplate.query(sql, new YukonRowMapper<PerformanceVerificationEventMessage>() {
            @Override
            public PerformanceVerificationEventMessage mapRow(YukonResultSet rs) throws SQLException {
                return new PerformanceVerificationEventMessage(rs.getLong("RfBroadcastEventId"),
                                                                          rs.getInstant("SendTime"));
            }
        });
    }
    
    @Override
    public Set<Integer> getDevicesWithUnknownStatus(long messageId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM RfBroadcastEventDevice rbed");
        sql.append("JOIN RfBroadcastEvent rbe ON rbed.RfBroadcastEventId = rbe.RfBroadcastEventId");
        sql.append("WHERE rbed.RfBroadcastEventId").eq(messageId);
        sql.append("AND Result").eq_k(UNKNOWN);

        return new HashSet<>(jdbcTemplate.query(sql, RowMapper.INTEGER));
    }

    @Override
    public PerformanceVerificationEventMessage createVerificationEvent() {
        int nextId = nextValueHelper.getNextValue("RfBroadcastEvent");
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("RfBroadcastEvent");
        
        params.addValue("RfBroadcastEventId", nextId);
        params.addValue("SendTime", now);
        
        jdbcTemplate.update(sql);
        
        return new PerformanceVerificationEventMessage(nextId, now);
    }
    
    @Override
    public void writeNewVerificationEventForDevices(long messageId, Set<Integer> deviceIds) {
        writeVerificationEventForDevices(messageId, deviceIds, UNKNOWN);
    }
    
    @Override
    public void writeUnenrolledEventResultForDevices(long messageId, Set<Integer> deviceIds) {
        writeVerificationEventForDevices(messageId, deviceIds, SUCCESS_UNENROLLED);
    }
    
    /**
     * Writes entries to the RfBroadcastEventDevice table for all of the specified devices. 
     * @param messageId the unique message id of the broadcast event
     * @param deviceIds the device ids the event is being logged for.
     * @param status - the status of the broadcast event.
     */
    private void writeVerificationEventForDevices(long messageId, 
                                                  Set<Integer> deviceIds, 
                                                  PerformanceVerificationMessageStatus status) {
        for (Integer deviceId : deviceIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("RfBroadcastEventDevice");
            
            int rowId = nextValueHelper.getNextValue("RfBroadcastEventDevice");
            
            params.addValue("RfBroadcastEventDeviceId", rowId);
            params.addValue("DeviceId", deviceId);
            params.addValue("RfBroadcastEventId", messageId);
            params.addValue("Result", status);
            
            jdbcTemplate.update(sql);
        }
    }
}
