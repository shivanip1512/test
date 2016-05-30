package com.cannontech.dr.rfn.dao.impl;

import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.*;
import static com.cannontech.dr.rfn.model.UnknownStatus.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.UnknownDevice;
import com.cannontech.dr.rfn.model.UnknownDevices;
import com.cannontech.dr.rfn.model.UnknownStatus;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Maps;

public class PerformanceVerificationDaoImpl implements PerformanceVerificationDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private GlobalSettingDao globalSettingDao;

    @Override
    public List<PerformanceVerificationEventMessageStats> getReports(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Result, rbed.RfnBroadcastEventId, Count(*) as count");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("WHERE EventSentTime").gt(range.getMin());
        sql.append("AND EventSentTime").lt(range.getMax());
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
            if (counts != null) {
                reports.add(new PerformanceVerificationEventMessageStats(messageSent.getMessageId(),
                                                                 messageSent.getTimeMessageSent(),
                                                                 Boolean.FALSE,
                                                                 counts.get(SUCCESS),
                                                                 counts.get(FAILURE),
                                                                 counts.get(UNKNOWN)));
            }
        }

        return reports;
    }
    
    @Override
    public Instant getEventTime(long messageId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventSentTime");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE RfnBroadcastEventId").eq(messageId);
        
        return jdbcTemplate.queryForObject(sql, TypeRowMapper.INSTANT);
    }

    @Override
    public List<PerformanceVerificationEventMessage> getEventMessages(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId, EventSentTime");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE EventSentTime").gt(range.getMin());
        sql.append("AND EventSentTime").lt(range.getMax());

        return jdbcTemplate.query(sql, new YukonRowMapper<PerformanceVerificationEventMessage>() {
            @Override
            public PerformanceVerificationEventMessage mapRow(YukonResultSet rs) throws SQLException {
                return new PerformanceVerificationEventMessage(rs.getLong("RfnBroadcastEventId"),
                                                                          rs.getInstant("EventSentTime"),
                                                                          Boolean.FALSE);
            }
        });
    }

    @Override
    public UnknownDevices getDevicesWithUnknownStatus(long messageId, PagingParameters pagingParameters) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId, Type, PaoName, UnknownStatus FROM (");
        addSelectDevicesWithUnknownStatus(sql, messageId, true);
        sql.append(") tbl");
        sql.append("WHERE tbl.RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());

        final UnknownDevices.Builder unknownDeviceBuilder = new UnknownDevices.Builder();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier pao = new PaoIdentifier(rs.getInt("DeviceId"), rs.getEnum("Type", PaoType.class));
                UnknownStatus status = rs.getEnum("UnknownStatus", UnknownStatus.class);
                unknownDeviceBuilder.addUnknownDevice(new UnknownDevice(pao, status));
            }
        });

        addUnknownCounts(messageId, unknownDeviceBuilder);

        return unknownDeviceBuilder.build();
    }

    @Override
    public UnknownDevices getAllDevicesWithUnknownStatus(long messageId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        addSelectDevicesWithUnknownStatus(sql, messageId, false);

        final UnknownDevices.Builder unknownDeviceBuilder = new UnknownDevices.Builder();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier pao = rs.getPaoIdentifier("deviceId", "type");
                UnknownStatus status = rs.getEnum("UnknownStatus", UnknownStatus.class);
                unknownDeviceBuilder.addUnknownDevice(new UnknownDevice(pao, status));
            }
        });
        addUnknownCounts(messageId, unknownDeviceBuilder);

        return unknownDeviceBuilder.build();
    }

    @Override
    public int getNumberOfDevices(long messageId, PerformanceVerificationMessageStatus status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*) from RfnBroadcastEventDeviceStatus");
        sql.append("WHERE RfnBroadcastEventId").eq(messageId);
        sql.append("AND RESULT").eq_k(status);
        return jdbcTemplate.queryForInt(sql);
    }

    private void addUnknownCounts(long messageId, final UnknownDevices.Builder builder) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Count(*) as count, UnknownStatus FROM (");
        addSelectDevicesWithUnknownStatus(sql, messageId, false);
        sql.append(") tbl");
        sql.append("Group By UnknownStatus");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                builder.setCountForStatus(rs.getEnum("UnknownStatus", UnknownStatus.class), rs.getInt("count"));
            }
        });
    }

    @Override
    public List<PaoIdentifier> getAllDevicesWithStatus(long messageId,
            PerformanceVerificationMessageStatus status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        addSelectDevices(sql, messageId, status, false);
        return jdbcTemplate.query(sql, new YukonRowMapper<PaoIdentifier>() {
            @Override
            public PaoIdentifier mapRow(YukonResultSet rs) throws SQLException {
                return rs.getPaoIdentifier("DeviceId", "Type");
            }
        });
    }

    @Override
    public List<PaoIdentifier> getDevicesWithStatus(long messageId,
                                                    PerformanceVerificationMessageStatus status,
                                                    PagingParameters pagingParameters) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId, Type FROM (");
        addSelectDevices(sql, messageId, status, true);
        sql.append(") tbl");
        sql.append("WHERE tbl.RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());
        return jdbcTemplate.query(sql, new YukonRowMapper<PaoIdentifier>() {
            @Override
            public PaoIdentifier mapRow(YukonResultSet rs) throws SQLException {
                return rs.getPaoIdentifier("DeviceId", "Type");
            }
        });

    }

    private void addSelectDevicesWithUnknownStatus(SqlStatementBuilder sql,
                                                   long messageId, boolean isPaged) {
          Instant now = new Instant();
          Instant communicatingWindowEnd = getCommunicationWindowEnd(now);
          Instant newDeviceWindowEnd = now.minus(Duration.standardDays(4));
    
          sql.append("SELECT RBED.DeviceId, YPO.Type, YPO.PaoName");
          if (isPaged) {
              sql.append(",ROW_NUMBER() OVER (ORDER BY PaoName) AS RowNumber");
          }
          sql.append(",CASE");
          sql.append("    WHEN LastCommunication IS NOT NULL THEN");
          sql.append("    CASE");
          sql.append("      WHEN lastcommunication").gt(communicatingWindowEnd);
          sql.append("            THEN").appendArgument_k(COMMUNICATING);
          sql.append("      WHEN LastCommunication").lte(communicatingWindowEnd);
          sql.append("         THEN").appendArgument_k(NOT_COMMUNICATING);
          sql.append("    END");
          sql.append("    WHEN LastCommunication IS NULL THEN");
          sql.append("    CASE");
          sql.append("      WHEN GroupEnrollStart IS NULL OR GroupEnrollStart").gte(newDeviceWindowEnd);
          sql.append("         THEN").appendArgument_k(NEW_INSTALL_NOT_COMMUNICATING);
          sql.append("      WHEN GroupEnrollStart").lt(newDeviceWindowEnd);
          sql.append("         THEN").appendArgument_k(NOT_COMMUNICATING);
          sql.append("    END");
          sql.append("END AS UnknownStatus");
          sql.append("FROM RfnBroadcastEventDeviceStatus RBED");
          sql.append("JOIN YukonPAObject YPO on YPO.PAObjectId = RBED.DeviceId");
          sql.append("JOIN InventoryBase IB ON IB.DeviceID = RBED.DeviceId");
          sql.append("JOIN LmHardwareControlGroup LHCG on LHCG.InventoryID = IB.InventoryID");
          sql.append("LEFT JOIN DynamicLcrCommunications DLC ON DLC.deviceId = RBED.DeviceId");
          sql.append("WHERE RBED.RfnBroadcastEventId").eq(messageId);
          sql.append("AND GroupEnrollStart = ");
          sql.append("    (SELECT MIN(GroupEnrollStart)");
          sql.append("        FROM LMHardwareControlGroup LMHCG");
          sql.append("        WHERE IB.InventoryID = LMHCG.InventoryID)");
          sql.append("AND Result").eq_k(UNKNOWN);
      }

    private void addSelectDevices(SqlStatementBuilder sql, long messageId,
            PerformanceVerificationMessageStatus messageStatus, boolean isPaged) {
        sql.append("SELECT RBED.DeviceId, YPO.Type, YPO.PaoName");
        if (isPaged) {
            sql.append(",ROW_NUMBER() OVER (ORDER BY PaoName) AS RowNumber");
        }
        sql.append("FROM RfnBroadcastEventDeviceStatus RBED");
        sql.append("JOIN YukonPAObject YPO on YPO.PAObjectId = RBED.DeviceId");
        sql.append("JOIN InventoryBase IB ON IB.DeviceID = RBED.DeviceId");
        sql.append("WHERE RBED.RfnBroadcastEventId").eq(messageId);
        sql.append("AND Result").eq_k(messageStatus);
    }

    @Override
    public PerformanceVerificationEventMessage createVerificationEvent() {
        int nextId = nextValueHelper.getNextValue("RfnBroadcastEvent");
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("RfnBroadcastEvent");
        
        params.addValue("RfnBroadcastEventId", nextId);
        params.addValue("EventSentTime", now);
        
        jdbcTemplate.update(sql);
        
        return new PerformanceVerificationEventMessage(nextId, now, Boolean.FALSE);
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
                
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Long> getValidEventIdsForDevice(int deviceId, Iterable<Long> eventIds) {
        //12 message ids maximum we can get from device
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId");
        sql.append("FROM RfnBroadcastEventDeviceStatus");
        sql.append("WHERE  DeviceId").eq(deviceId);
        sql.append("AND RfnBroadcastEventId").in(eventIds);
        return jdbcTemplate.query(sql, TypeRowMapper.LONG);
    }
    
    @Override
    public List<Long> getValidEventIds(Iterable<Long> eventIds) {
        //12 message ids maximum we can get from device
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE RfnBroadcastEventId").in(eventIds);
        return jdbcTemplate.query(sql, TypeRowMapper.LONG);
    }
    
    @Override
    public void createUnenrolledEventResultStatus(int deviceId, long messageId, Instant deviceReceivedTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("RfnBroadcastEventDeviceStatus");
        params.addValue("DeviceId", deviceId);
        params.addValue("RfnBroadcastEventId", messageId);
        params.addValue("Result", SUCCESS_UNENROLLED);
        params.addValue("DeviceReceivedTime", deviceReceivedTime);    
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void setEventResultStatusToSuccessful(int deviceId, Map<Long, Instant> verificationMsgs) {
        for (long eventId : verificationMsgs.keySet()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.update("RfnBroadcastEventDeviceStatus");
            params.addValue("Result", SUCCESS);
            params.addValue("DeviceReceivedTime", verificationMsgs.get(eventId));
            sql.append("WHERE  DeviceId").eq(deviceId);
            sql.append("AND RfnBroadcastEventId").eq(eventId);
            sql.append("AND Result").neq_k(SUCCESS);
            sql.append("AND Result").neq_k(SUCCESS_UNENROLLED);

            jdbcTemplate.update(sql);
        }
    }
    
    @Override
    public void setEventResultStatusToFailure(int deviceId, Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("RfnBroadcastEventDeviceStatus");
        params.addValue("Result", FAILURE);
        sql.append("WHERE DeviceId").eq(deviceId);
        sql.append("   AND Result").eq_k(UNKNOWN);
        sql.append("   AND RfnBroadcastEventId IN (SELECT RfnBroadcastEventId");
        sql.append("                               FROM RfnBroadcastEvent");
        sql.append("                               WHERE EventSentTime").gte(range.getMin());
        sql.append("                               AND EventSentTime").lt(range.getMax()).append(")");

        jdbcTemplate.update(sql);
    }
    
    private Instant getCommunicationWindowEnd(Instant now) {
        return now.minus(Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.LAST_COMMUNICATION_HOURS)));
    }
    
    @Override
    public void archiveRfnBroadcastEventStatus(DateTime removeAfterDate) {
        List<Long> rfnBroadcastEventId = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId FROM RfnBroadcastEvent");
        sql.append("WHERE EventSentTime").lt(removeAfterDate);
        sql.append("AND RfnBroadcastEventId NOT IN (");
        sql.append("SELECT RfnBroadcastEventId FROM RfnBroadcastArchivedEventStatus)");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                rfnBroadcastEventId.add(rs.getLong("RfnBroadcastEventId"));
            }
        });

        for (Long eventId : rfnBroadcastEventId) {
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO RfnBroadcastArchivedEventStatus");
            insertSql.append("(RfnBroadcastEventId, Success, SuccessUnenrolled, Failure, Unknown) VALUES (");
            insertSql.append(eventId);
            insertSql.append(",");
            insertSql.append(" (SELECT COUNT(*) FROM RfnBroadcastEventDeviceStatus where RfnBroadcastEventId")
                     .eq(eventId);
            insertSql.append(" AND RfnBroadcastEventDeviceStatus.Result").eq_k(SUCCESS);
            insertSql.append(" )");
            insertSql.append(",");
            insertSql.append(" (SELECT COUNT(*) FROM RfnBroadcastEventDeviceStatus where RfnBroadcastEventId")
                     .eq(eventId);
            insertSql.append(" AND RfnBroadcastEventDeviceStatus.Result").eq_k(SUCCESS_UNENROLLED);
            insertSql.append(" )");
            insertSql.append(",");
            insertSql.append(" (SELECT COUNT(*) FROM RfnBroadcastEventDeviceStatus where RfnBroadcastEventId")
                     .eq(eventId);
            insertSql.append(" AND RfnBroadcastEventDeviceStatus.Result").eq_k(FAILURE);
            insertSql.append(" )");
            insertSql.append(",");
            insertSql.append(" (SELECT COUNT(*) FROM RfnBroadcastEventDeviceStatus where RfnBroadcastEventId")
                     .eq(eventId);
            insertSql.append(" AND RfnBroadcastEventDeviceStatus.Result").eq_k(UNKNOWN);
            insertSql.append(" ))");

            jdbcTemplate.update(insertSql);

        }
    }
    
    @Override
    public void removeOlderRfnBroadcastEventStatus(DateTime removeAfterDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM RfnBroadcastEventDeviceStatus ");
        sql.append("WHERE RfnBroadcastEventId IN");
        sql.append("(SELECT RfnBroadcastEventId");
        sql.append(" FROM RfnBroadcastEvent");
        sql.append(" WHERE EventSentTime").lt(removeAfterDate);
        sql.append(")");

        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<PerformanceVerificationEventMessageStats> getArchiveReports(Range<Instant> range) {
        List<PerformanceVerificationEventMessageStats> reports = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rbaes.RfnBroadcastEventId AS EventId, rbe.EventSentTime AS EventSentTime, Success, Failure, Unknown");
        sql.append("FROM RfnBroadcastArchivedEventStatus rbaes");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbaes.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("AND rbe.EventSentTime").gt(range.getMin());
        sql.append("AND rbe.EventSentTime").lt(range.getMax());

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                reports.add(new PerformanceVerificationEventMessageStats(rs.getLong("EventId"),
                                                                         rs.getInstant("EventSentTime"),
                                                                         Boolean.TRUE,
                                                                         rs.getInt("Success"),
                                                                         rs.getInt("Failure"),
                                                                         rs.getInt("Unknown")));
            }
        });
        return reports;
    }
}

