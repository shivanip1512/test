package com.cannontech.dr.rfn.dao.impl;

import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.FAILURE;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.SUCCESS;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.SUCCESS_UNENROLLED;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.UNKNOWN;
import static com.cannontech.dr.rfn.model.DeviceStatus.COMMUNICATING;
import static com.cannontech.dr.rfn.model.DeviceStatus.NEW_INSTALL_NOT_COMMUNICATING;
import static com.cannontech.dr.rfn.model.DeviceStatus.NOT_COMMUNICATING;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.PagingParameters;
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
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.DeviceStatus;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Maps;


public class PerformanceVerificationDaoImpl implements PerformanceVerificationDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private InventoryDao inventoryDao;

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
    public PerformanceVerificationEventMessageStats getReportForEvent(long eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Result, Count(*) as Count");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN RfnBroadcastEvent rbe ON rbed.RfnBroadcastEventId = rbe.RfnBroadcastEventId");
        sql.append("WHERE rbed.RfnBroadcastEventId").eq(eventId);
        sql.append("GROUP BY Result");

        final Map<PerformanceVerificationMessageStatus, Integer> stats = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                stats.put(rs.getEnum("Result", PerformanceVerificationMessageStatus.class), rs.getInt("Count"));
            }
        });
        PerformanceVerificationEventMessage messageSent = getEventMessagesByEvent(eventId);
        return new PerformanceVerificationEventMessageStats(messageSent.getMessageId(),
                                                         messageSent.getTimeMessageSent(),
                                                         Boolean.FALSE,
                                                         stats.containsKey(SUCCESS) ? stats.get(SUCCESS): 0,
                                                         stats.containsKey(FAILURE) ? stats.get(FAILURE): 0,
                                                         stats.containsKey(UNKNOWN) ? stats.get(UNKNOWN): 0
                                                         );
        
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
    public PerformanceVerificationEventMessage getEventMessagesByEvent(long eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId, EventSentTime");
        sql.append("FROM RfnBroadcastEvent");
        sql.append("WHERE RfnBroadcastEventId").eq(eventId);

        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<PerformanceVerificationEventMessage>() {
            @Override
            public PerformanceVerificationEventMessage mapRow(YukonResultSet rs) throws SQLException {
                return new PerformanceVerificationEventMessage(
                    rs.getLong("RfnBroadcastEventId"),
                    rs.getInstant("EventSentTime"), 
                    Boolean.FALSE);
            }
        });
    }
    
    @Override
    public List<BroadcastEventDeviceDetails> getAllDevicesWithStatus(long eventId, List<PerformanceVerificationMessageStatus> statuses) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Result, DeviceId, InventoryId, LMHardwareTypeId, AccountId, AccountNumber, LastCommunication,");
        sql.append("'COMMUNICATING' as DeviceStatus");
        sql.append("FROM (");
        addSelectDevices(sql, statuses, eventId, null);
        sql.append(") tbl");
        return jdbcTemplate.query(sql, new DeviceDetailsRowMapper());
    }


    @Override
    public List<BroadcastEventDeviceDetails> getFilteredDevicesWithStatus(long eventId,
                                                    List<PerformanceVerificationMessageStatus> status,
                                                    PagingParameters pagingParameters, List<DeviceGroup> subGroups) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Result, DeviceId, InventoryId, LMHardwareTypeId, AccountId, AccountNumber, LastCommunication,");
        sql.append("'COMMUNICATING' AS DeviceStatus");
        sql.append("FROM (");
        addSelectDevices(sql, status, eventId, subGroups);
        sql.append(") tbl");
        sql.append("WHERE tbl.RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());
        return jdbcTemplate.query(sql, new DeviceDetailsRowMapper());
    }
    
    // Row mapper for broadcast event device details object
    private class DeviceDetailsRowMapper implements YukonRowMapper<BroadcastEventDeviceDetails> {
        @Override
        public BroadcastEventDeviceDetails mapRow(YukonResultSet rs) throws SQLException {
                LiteLmHardware lmh = new LiteLmHardware();
                lmh.setIdentifier(new InventoryIdentifier(rs.getInt("InventoryId"),
                    inventoryDao.getHardwareTypeById(rs.getInt("LMHardwareTypeId"))));
                lmh.setDeviceId(rs.getInt("DeviceId"));
                lmh.setAccountId(rs.getInt("AccountId"));
                lmh.setAccountNo(rs.getString("AccountNumber"));
                
            return new BroadcastEventDeviceDetails(rs.getEnum("Result", PerformanceVerificationMessageStatus.class),
                lmh, rs.getInstant("LastCommunication"),
                rs.getEnum("DeviceStatus", DeviceStatus.class)
                );
        }
    }
    
    // Query to select device details 
    private void addSelectDevices(SqlStatementBuilder sql, List<PerformanceVerificationMessageStatus> status,
            long eventId, List<DeviceGroup> subGroups) {
        sql.append("SELECT Result, ib.DeviceId, ib.InventoryId, LMHardwareTypeId, ib.AccountId, AccountNumber, LastCommunication");
        sql.append(",ROW_NUMBER() OVER (ORDER BY ib.DeviceId) AS RowNumber");
        sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
        sql.append("JOIN InventoryBase ib ON ib.DeviceID = rbed.DeviceId");
        sql.append("LEFT JOIN DynamicLcrCommunications lcr ON ib.DeviceID = lcr.DeviceId");
        sql.append("JOIN LmHardwareBase lmhb ON lmhb.InventoryId = ib.InventoryId");
        sql.append("JOIN EcToInventoryMapping ecim ON ecim.InventoryId = ib.InventoryId");
        sql.append("JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
        sql.append("WHERE rbed.RfnBroadcastEventId").eq(eventId);
        if (!CollectionUtils.isEmpty(subGroups)) {
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(subGroups, "ib.DeviceId"));
        }
        sql.append("AND Result").in_k(status);
    }
    
    
    @Override
    public List<BroadcastEventDeviceDetails> getAllDevicesWithUnknownStatus(long eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Result, DeviceId, InventoryId, LMHardwareTypeId, AccountId, AccountNumber, LastCommunication, DeviceStatus FROM (");
        addSelectForUnknownStatus(sql, eventId, true, null);
        sql.append(") tbl");
        return jdbcTemplate.query(sql, new DeviceDetailsRowMapper());
    }
    
    @Override
    public List<BroadcastEventDeviceDetails> getFilteredDevicesWithUnknownStatus(long eventId, PagingParameters pagingParameters, List<DeviceGroup> subGroups) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Result, DeviceId, InventoryId, LMHardwareTypeId, AccountId, AccountNumber, LastCommunication, DeviceStatus FROM (");
        addSelectForUnknownStatus(sql, eventId, true, subGroups);
        sql.append(") tbl");
        sql.append("WHERE tbl.RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());

        return jdbcTemplate.query(sql, new DeviceDetailsRowMapper());
    }

    @Override
    public int getFilteredCountForStatus(long eventId, List<PerformanceVerificationMessageStatus> status, List<DeviceGroup> subGroups) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM (");
        addSelectDevices(sql, status, eventId, subGroups);
        sql.append(") tbl");
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getFilteredCountForUnknownStatus(long eventId, List<DeviceGroup> subGroups) {
        SqlStatementBuilder sqlUnknown = new SqlStatementBuilder();
        sqlUnknown.append("SELECT COUNT(*) FROM (");
        addSelectForUnknownStatus(sqlUnknown, eventId, false, subGroups);
        sqlUnknown.append(") tbl");
        return jdbcTemplate.queryForInt(sqlUnknown);
    }
   

    @Override
    public Map<DeviceStatus, Integer> getUnknownCounts(long eventId) {
        Map<DeviceStatus, Integer> unknownStatusCount = new HashMap<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) AS Count, DeviceStatus FROM (");
        addSelectForUnknownStatus(sql, eventId, false, null);
        sql.append(") tbl");
        sql.append("GROUP BY DeviceStatus");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                unknownStatusCount.put(rs.getEnum("DeviceStatus", DeviceStatus.class), rs.getInt("Count"));
            }
        });
        return unknownStatusCount;
    }

    // Generates query to select devices(Hardware) for unknown status 
    private void addSelectForUnknownStatus(SqlStatementBuilder sql,
                                                   long messageId, boolean isPaged, List<DeviceGroup> subGroups) {
          Instant now = new Instant();
          Instant communicatingWindowEnd = getCommunicationWindowEnd(now);
          Instant newDeviceWindowEnd = now.minus(Duration.standardDays(4));
          
          sql.append("SELECT Result, ib.DeviceId, ib.InventoryId, LMHardwareTypeId, ib.AccountId, AccountNumber, LastCommunication");
          if (isPaged) {
              sql.append(",ROW_NUMBER() OVER (ORDER BY IB.DeviceId) AS RowNumber");
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
          sql.append("END AS DeviceStatus");
          sql.append("FROM RfnBroadcastEventDeviceStatus rbed");
          sql.append("JOIN InventoryBase ib ON ib.DeviceID = rbed.DeviceId");
          sql.append("JOIN LmHardwareControlGroup lhcg on lhcg.InventoryID = ib.InventoryID");
          sql.append("JOIN LmHardwareBase lmhb ON lmhb.InventoryId = ib.InventoryId");
          sql.append("JOIN EcToInventoryMapping ecim ON ecim.InventoryId = ib.InventoryId");
          sql.append("JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
          sql.append("LEFT JOIN DynamicLcrCommunications dlc ON dlc.deviceId = rbed.DeviceId");
          sql.append("WHERE rbed.RfnBroadcastEventId").eq(messageId);
          sql.append("AND GroupEnrollStart = ");
          sql.append("    (SELECT MIN(GroupEnrollStart)");
          sql.append("        FROM LMHardwareControlGroup lmhcg");
          sql.append("        WHERE ib.InventoryID = lmhcg.InventoryID)");
          sql.append("AND Result").eq_k(UNKNOWN);
          if (!CollectionUtils.isEmpty(subGroups)) {
              sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(subGroups, "ib.DeviceId"));
          }
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
    public void archiveRfnBroadcastEventStatus(DateTime removeBeforeDate) {
        List<Long> rfnBroadcastEventId = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RfnBroadcastEventId FROM RfnBroadcastEvent");
        sql.append("WHERE EventSentTime").lt(removeBeforeDate);
        sql.append("AND RfnBroadcastEventId NOT IN (");
        sql.append("SELECT RfnBroadcastEventId FROM RfnBroadcastEventSummary)");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                rfnBroadcastEventId.add(rs.getLong("RfnBroadcastEventId"));
            }
        });

        for (Long eventId : rfnBroadcastEventId) {
            archiveRfnBroadcastEvents(eventId, removeBeforeDate);
        }
    }

    @Override
    @Transactional
    public void archiveRfnBroadcastEvents(Long eventId, DateTime removeBeforeDate) {
        insertIntoRfnBroadcastEventSummary(eventId);
        removeOlderRfnBroadcastEventStatus(removeBeforeDate);
    }

    private void insertIntoRfnBroadcastEventSummary(Long eventId) {
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO RfnBroadcastEventSummary");
        insertSql.append("SELECT RfnBroadcastEventId,");
        insertSql.append("COUNT(CASE WHEN Result").eq_k(SUCCESS).append("THEN 1 END), ");
        insertSql.append("COUNT(CASE WHEN Result").eq_k(SUCCESS_UNENROLLED).append(" THEN 1 END), ");
        insertSql.append("COUNT(CASE WHEN Result").eq_k(FAILURE).append("THEN 1 END), ");
        insertSql.append("COUNT(CASE WHEN Result").eq_k(UNKNOWN).append("THEN 1 END) ");
        insertSql.append("FROM RfnBroadcastEventDeviceStatus");
        insertSql.append("WHERE RfnBroadcastEventId").eq(eventId);
        insertSql.append("GROUP BY RfnBroadcastEventId");

        jdbcTemplate.update(insertSql);
    }

    private void removeOlderRfnBroadcastEventStatus(DateTime removeBeforeDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM RfnBroadcastEventDeviceStatus ");
        sql.append("WHERE RfnBroadcastEventId IN");
        sql.append("(SELECT rbae.RfnbroadcastEventid");
        sql.append(" FROM RfnBroadcastEventSummary rbae");
        sql.append(" JOIN RfnBroadcastEvent rbe ON");
        sql.append(" rbae.RfnBroadcastEventId=rbe.RfnBroadcastEventId");
        sql.append(" WHERE EventSentTime").lt(removeBeforeDate);
        sql.append(")");

        jdbcTemplate.update(sql);
    }

    @Override
    public List<PerformanceVerificationEventMessageStats> getArchiveReports(Range<Instant> range) {
        List<PerformanceVerificationEventMessageStats> reports = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rbaes.RfnBroadcastEventId AS EventId, rbe.EventSentTime AS EventSentTime, Success, Failure, Unknown");
        sql.append("FROM RfnBroadcastEventSummary rbaes");
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

