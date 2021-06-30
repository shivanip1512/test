package com.cannontech.dr.recenteventparticipation.dao.impl;

import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.UNKNOWN;
import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.FAILED;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.ControlOptOutStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.dr.recenteventparticipation.model.ControlDeviceDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationStats;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationSummary;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class RecentEventParticipationDaoImpl implements RecentEventParticipationDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    private static final Logger log = YukonLogManager.getLogger(RecentEventParticipationDaoImpl.class);
    /**
     * The DB change Listener updates the OptOutEventId in ControlEventDevice
     */

    @PostConstruct
    public void init() {
        createDatabaseChangeListener();
    }

    private void createDatabaseChangeListener() {

        asyncDynamicDataSource.addDatabaseChangeEventListener(new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if ((event.getChangeCategory() == DbChangeCategory.CHANGE_OPTOUT)) {
                    OptOutEvent optOutEvent = optOutEventDao.getOptOutEventById(event.getPrimaryKey());
                    if (optOutEvent.getState() == OptOutEventState.START_OPT_OUT_SENT) {
                        LiteInventoryBase liteInvBase = inventoryBaseDao.getByInventoryId(optOutEvent.getInventoryId());
                        updateDeviceOptOutEventId(liteInvBase.getDeviceID(), optOutEvent.getEventId());
                    }
                }
            }
        });
    }

    private void updateDeviceOptOutEventId(int deviceId, int optOutEventId) {
        Instant now = new Instant();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ControlEventDevice").set("OptOutEventId", optOutEventId);
        sql.append("WHERE ControlEventId = ");
        sql.append("   (SELECT ce.ControlEventId FROM ControlEvent ce");
        sql.append("      JOIN (SELECT MAX(ControlEventId) AS controlEventId");
        sql.append("            FROM ControlEventDevice");
        sql.append("            WHERE DeviceId").eq(deviceId).append(")t");
        sql.append("      ON t.controlEventId = ce.ControlEventId");
        sql.append("      AND ce.ScheduledStopTime").gt(now).append(")");
        sql.append("  AND DeviceId").eq(deviceId);
        jdbcTemplate.update(sql);
                            
    }

    @Override
    public void createNewEventMapping(int programId, long eventId, int groupId, Instant startTime, Instant stopTime, String externalEventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink p = sql.insertInto("ControlEvent");
        p.addValue("ControlEventId", eventId);
        p.addValue("GroupId", groupId);
        p.addValue("StartTime", startTime);
        p.addValue("ScheduledStopTime", stopTime);
        p.addValue("ProgramId", programId);
        p.addValue("ExternalEventId", externalEventId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void insertDeviceControlEvent(long eventId, int loadGroupId, Instant eventTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ControlEventDevice (DeviceId, ControlEventId, OptOutEventId, Result)");
        sql.append("   (SELECT ypo.PaobjectId, ").appendArgument(eventId).append(",").append("ooe.OptOutEventId").append(",").appendArgument_k(UNKNOWN);
        sql.append("    FROM LMHardwareControlGroup lmhcg");
        sql.append("      JOIN InventoryBase inv ON inv.InventoryId = lmhcg.InventoryId");
        sql.append("      LEFT JOIN OptOutEvent ooe ON ooe.InventoryId = lmhcg.InventoryId");
        sql.append("      AND ooe.StartDate").lt(eventTime);
        sql.append("      AND ooe.StopDate").gt(eventTime);
        sql.append("      AND ooe.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT);
        sql.append("      JOIN YukonPAObject ypo on ypo.PaobjectId = inv.DeviceId");
        sql.append("    WHERE lmhcg.LMGroupId").eq(loadGroupId);
        sql.append("      AND lmhcg.GroupEnrollStart IS NOT NULL");
        sql.append("      AND lmhcg.GroupEnrollStop IS NULL");
        sql.append("   )");
        jdbcTemplate.update(sql);
    }

    private final static YukonRowMapper<RecentEventParticipationSummary> recentEventParticipationSummaryRowMapper =
        new YukonRowMapper<RecentEventParticipationSummary>() {
            @Override
            public RecentEventParticipationSummary mapRow(YukonResultSet rs) throws SQLException {
                String programName = rs.getString("ProgramName");
                Instant startTime = rs.getInstant("StartTime");
                int numConfirmed = rs.getInt("Confirmed");
                int numUnknowns = rs.getInt("Unknown");
                int numFailed = rs.getInt("Failed");
                int numConfirmedRetry = rs.getInt("ConfirmedRetry");
                RecentEventParticipationSummary recentEventParticipationSummary =
                    new RecentEventParticipationSummary(programName, startTime, numConfirmed, numUnknowns, numFailed, numConfirmedRetry);
                return recentEventParticipationSummary;
            }
        };

    @Override
    public List<RecentEventParticipationSummary> getRecentEventParticipationSummary(int numberOfEvents) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM (");
        sql.append("    SELECT ROW_NUMBER() OVER (ORDER BY ce.StartTime DESC) AS RowNumber, pgypo.PAOName AS ProgramName, ce.StartTime AS StartTime,");
        sql.append(getSumSql());
        sql.append("    FROM ControlEvent ce");
        sql.append("      JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("      JOIN YukonPAObject pgypo on pgypo.PAObjectId = ce.ProgramId");
        sql.append("      GROUP BY pgypo.PAOName, ce.StartTime) AS tbl");
        sql.append("WHERE tbl.RowNumber").lte(numberOfEvents);
        List<RecentEventParticipationSummary> recentEventParticipationSummaries = jdbcTemplate.query(sql, recentEventParticipationSummaryRowMapper);
        return recentEventParticipationSummaries;
    }
    
    public SqlStatementBuilder getSumSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SUM(CASE WHEN ced.Result").eq_k(UNKNOWN).append("THEN 1 ELSE 0 END) Unknown,");
        sql.append("SUM(CASE WHEN ced.Result").eq_k(FAILED).append("THEN 1 ELSE 0 END) Failed,");
        sql.append("SUM(CASE WHEN ced.Result").in_k(List.of(ControlEventDeviceStatus.SUCCESS_COMPLETED,
                ControlEventDeviceStatus.SUCCESS_RECEIVED, ControlEventDeviceStatus.SUCCESS_STARTED))
                .append("AND ced.RetryTime is NULL THEN 1 ELSE 0 END) Confirmed,");
        sql.append("SUM(CASE WHEN ced.Result").in_k(List.of(ControlEventDeviceStatus.SUCCESS_COMPLETED,
                ControlEventDeviceStatus.SUCCESS_RECEIVED, ControlEventDeviceStatus.SUCCESS_STARTED))
                .append("AND ced.RetryTime is NOT NULL THEN 1 ELSE 0 END) ConfirmedRetry");
        return sql;
    }

    private final static YukonRowMapper<RecentEventParticipationStats> recentEventParticipationStatsRowMapper =
        new YukonRowMapper<RecentEventParticipationStats>() {
            @Override
            public RecentEventParticipationStats mapRow(YukonResultSet rs) throws SQLException {

                int controlEventId = rs.getInt("EventId");
                String programName = rs.getString("Program");
                String loadGroupName = rs.getString("LoadGroup");
                Instant startTime = rs.getInstant("StartTime");
                int numConfirmed = rs.getInt("Confirmed");
                int numUnknowns = rs.getInt("Unknown");
                int numFailed = rs.getInt("Failed");
                int numConfirmedRetry = rs.getInt("ConfirmedRetry");
                String externalEventId = rs.getString("ExternalEventId");
                RecentEventParticipationStats recentEventParticipationStats =
                    new RecentEventParticipationStats(controlEventId, programName, loadGroupName, startTime, numConfirmed,
                        numUnknowns, externalEventId, numFailed, numConfirmedRetry);
                return recentEventParticipationStats;
            }
        };

    @Override
    public List<RecentEventParticipationStats> getRecentEventParticipationStats(Range<Instant> range, PagingParameters pagingParameters) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT * FROM (");
        sql.append("    SELECT ROW_NUMBER() OVER (ORDER BY EventId DESC");
        sql.append("    ) AS RowNumber, EventId, Program, LoadGroup, StartTime, Unknown, Confirmed, Failed, ConfirmedRetry, ExternalEventId");
        sql.append("    FROM (");
        sql.append("        SELECT ce.ControlEventId AS EventId, pgypo.PAOName AS Program, lgypo.PAOName AS LoadGroup, ce.StartTime AS StartTime, ExternalEventId, ");
        sql.append(getSumSql());
        sql.append("        FROM ControlEvent ce");
        sql.append("          JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("          JOIN YukonPAObject dypo ON dypo.PAObjectId = ced.DeviceId");
        sql.append("          JOIN YukonPAObject lgypo on lgypo.PAObjectId = ce.GroupId");
        sql.append("          JOIN YukonPAObject pgypo on pgypo.PAObjectId = ce.ProgramId");
        if (range != null) {
            sql.append("      WHERE StartTime").gt(range.getMin());
            sql.append("      AND StartTime").lt(range.getMax());
        }
        sql.append("        GROUP BY ce.ControlEventId, ce.StartTime, lgypo.PAOName, pgypo.PAOName, ExternalEventId) AS innertable");
        sql.append("    ) outertable ");
        sql.append("WHERE RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());
        List<RecentEventParticipationStats> recentEventParticipationStats = jdbcTemplate.query(sql, recentEventParticipationStatsRowMapper);
        return recentEventParticipationStats;
    }

    private final static YukonRowMapper<RecentEventParticipationDetail> recentEventParticipationDetailRowMapper =
        new YukonRowMapper<RecentEventParticipationDetail>() {
            @Override
            public RecentEventParticipationDetail mapRow(YukonResultSet rs) throws SQLException {

                int eventId = rs.getInt("EventId");
                String programName = rs.getString("ProgramName");
                String groupName = rs.getString("LoadGroup");
                Instant startTime = rs.getInstant("StartTime");
                Instant stopTime = rs.getInstant("StopTime");
                String externalEventId = rs.getString("ExternalEventId");
                RecentEventParticipationDetail recentEventParticipationDetail =
                    new RecentEventParticipationDetail(eventId, programName, groupName, startTime, stopTime, null, externalEventId);
                return recentEventParticipationDetail;
            }
        };

    @Override
    public List<RecentEventParticipationDetail> getRecentEventParticipationDetail(long eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(getControlAuditBaseQuery());
        sql.append("ce.ControlEventId").eq(eventId);
        List<RecentEventParticipationDetail> recentEventParticipationDetails = jdbcTemplate.query(sql, recentEventParticipationDetailRowMapper);
        return recentEventParticipationDetails;
    }

    @Override
    public List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(getControlAuditBaseQuery());
        if (range.isIncludesMinValue()) {
            sql.append("ce.StartTime").gte(range.getMin());
        } else {
            sql.append("ce.StartTime").gt(range.getMin());
        }
        if (range.isIncludesMaxValue()) {
            sql.append("AND ce.StartTime").lte(range.getMax());
        } else {
            sql.append("AND ce.StartTime").lt(range.getMax());
        }
        sql.append("ORDER BY ce.ControlEventId DESC");
        List<RecentEventParticipationDetail> recentEventParticipationDetails = jdbcTemplate.query(sql, recentEventParticipationDetailRowMapper);

        return recentEventParticipationDetails;
    }

    @Override
    public Map<Integer, Integer> getControlEventDeviceStatus(List<Integer> deviceId, Date startDate, Date endDate) {
        SqlStatementBuilder selectSql = new SqlStatementBuilder();

        selectSql.append("SELECT DeviceId,");
        selectSql.append("  SUM(CASE WHEN Result ").in_k(ControlEventDeviceStatus.getAllDeviceStatus());
        selectSql.append("  THEN 1 ELSE 0 END) AS StatusCount");
        selectSql.append("FROM ControlEvent ce, ControlEventDevice ced");
        selectSql.append("WHERE ce.ControlEventId = ced.ControlEventId");
        selectSql.append("  AND StartTime").gte(startDate);
        selectSql.append("  AND StartTime").lte(endDate);
        selectSql.append("GROUP BY DeviceId");

        final Map<Integer, Integer> deviceStatus = Maps.newHashMap();
        jdbcTemplate.query(selectSql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int deviceId = rs.getInt("DeviceId");
                int statusCount = rs.getInt("StatusCount");
                deviceStatus.put(deviceId, statusCount);
            }
        });
        return deviceStatus;
    }
    
    private SqlFragmentSource getControlAuditBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ce.ControlEventId AS EventId, ce.StartTime, ce.ScheduledStopTime AS StopTime, ");
        sql.append("ce.ExternalEventId AS ExternalEventId, ");
        sql.append("lgypo.PAOName AS LoadGroup, pgypo.PAOName AS ProgramName");
        sql.append("FROM ControlEvent ce");
        sql.append("  JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("  JOIN YukonPAObject dypo ON dypo.PAObjectId = ced.DeviceId");
        sql.append("  JOIN YukonPAObject lgypo on lgypo.PAObjectId = ce.GroupId");
        sql.append("  JOIN YukonPAObject pgypo on pgypo.PAObjectId = ce.ProgramId");
        sql.append("WHERE");
        return sql;
    }

    private final static YukonRowMapper<ControlDeviceDetail> controlDeviceDetailMapper = new YukonRowMapper<ControlDeviceDetail>() {

        @Override
        public ControlDeviceDetail mapRow(YukonResultSet rs) throws SQLException {
            String deviceName = rs.getString("DeviceName");
            String accountNumber = rs.getString("AccountNumber");
            String serialNumber = rs.getString("SerialNumber");
            String result = rs.getString("Result");
            String eventPhase = ControlEventDeviceStatus.valueOf(result)
                    .getEventPhase() == null ? "Unknown" : ControlEventDeviceStatus.valueOf(result).getEventPhase()
                            .getJsonString();
            String participationState = ControlEventDeviceStatus.valueOf(result)
                    .getEventPhase() == null ? "Unreported" : "Confirmed";
            ControlOptOutStatus optOutStatus = ControlOptOutStatus.valueOf(rs.getString("OptoutStatus"));
            String failReason = rs.getString("FailReason");
            Instant retryTime = rs.getInstant("RetryTime");
            ControlDeviceDetail controlDeviceDetail = new ControlDeviceDetail(deviceName, accountNumber, serialNumber,
                    participationState, eventPhase, optOutStatus, failReason, retryTime);
            return controlDeviceDetail;
        }
    };

    @Override
    public List<ControlDeviceDetail> getControlEventDeviceData(int eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dypo.PAOName AS DeviceName, ca.AccountNumber, ced.Result, ced.FailReason, ced.RetryTime, lmbh.ManufacturerSerialNumber AS SerialNumber,");
        sql.append("  CASE WHEN ced.OptOutEventId IS NULL THEN").appendArgument_k(ControlOptOutStatus.NONE);
        sql.append("       WHEN ced.OptOutEventId IS NOT NULL THEN");
        sql.append("         CASE WHEN (SELECT StartDate");
        sql.append("                    FROM OptOutEvent");
        sql.append("                    WHERE OptOutEventId = ced.OptOutEventId) >= ce.StartTime");
        sql.append("                      THEN").appendArgument_k(ControlOptOutStatus.POST_OPTOUT);
        sql.append("                    ELSE ").appendArgument_k(ControlOptOutStatus.PRE_OPTOUT);
        sql.append("         END");
        sql.append("  END AS OptoutStatus");
        sql.append("FROM ControlEvent ce");
        sql.append("  JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("  JOIN YukonPAObject dypo ON dypo.PAObjectID = ced.DeviceId");
        sql.append("  JOIN InventoryBase inv ON inv.DeviceID = ced.DeviceId");
        sql.append("  JOIN CustomerAccount ca ON ca.AccountId = inv.AccountId");
        sql.append("  JOIN LMHardwareBase lmbh ON lmbh.InventoryID= inv.InventoryID");
        sql.append("WHERE ce.ControlEventId").eq_k(eventId);
        List<ControlDeviceDetail> controlDeviceDetails = jdbcTemplate.query(sql, controlDeviceDetailMapper);
        return controlDeviceDetails;
    }

    @Override
    public int getNumberOfEvents(Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(DISTINCT(ce.ControlEventId))");
        sql.append("FROM ControlEvent ce");
        sql.append("  JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        if (range != null) {
            sql.append("      WHERE StartTime").gt(range.getMin());
            sql.append("      AND StartTime").lt(range.getMax());
        }
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public Integer getExternalEventId(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ce.ExternalEventID");
        sql.append("FROM ControlEvent ce");
        sql.append("WHERE ce.GroupId").eq(groupId);
        sql.append("AND StartTime = (SELECT max(StartTime) from ControlEvent where GroupId").eq(groupId).append(")");
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (Exception e) {
            log.error("ExternalEventID doesn't exist for group id:{}", groupId, e);
            return null;
        }
    }

    @Override
    public void updateDeviceControlEvent(String externalEventId, int deviceId, ControlEventDeviceStatus status,
            Instant deviceReceivedTime, String failReason, Instant retryTime) {
       
        /*
         * if the current status is SUCCESS_RECEIVED, and we receive SUCCESS_STARTED, it will update. If we receive them out of
         * order, it will prevent us from updating the SUCCESS_STARTED status to SUCCESS_RECEIVED
         */
        //list of statuses that are "earlier" than the received status
        List<ControlEventDeviceStatus> statusesToUpdate =
                ImmutableList.copyOf(ControlEventDeviceStatus.values())
                             .stream()
                             .filter(messageStatus -> messageStatus.getMessageOrder() <= status.getMessageOrder())
                             .collect(Collectors.toList());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ControlEventDevice");
        sql.append("SET Result").eq(status);
        sql.append(",");
        sql.append("DeviceReceivedTime").eq(deviceReceivedTime);
        if (failReason != null) {
            sql.append(",");
            sql.append("FailReason").eq(failReason);
        }
        if (retryTime != null) {
            sql.append(",");
            sql.append("RetryTime").eq(retryTime);
        }
        sql.append("WHERE ControlEventId in (SELECT ControlEventId from ControlEvent WHERE ExternalEventId").eq(externalEventId).append(")");
        sql.append("AND Result").in(statusesToUpdate);
        sql.append("AND DeviceId").eq(deviceId);
        jdbcTemplate.update(sql);
    }
}