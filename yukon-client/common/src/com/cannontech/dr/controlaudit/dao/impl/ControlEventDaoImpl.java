package com.cannontech.dr.controlaudit.dao.impl;

import static com.cannontech.dr.controlaudit.ControlEventDeviceStatus.UNKNOWN;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.dao.ControlEventDao;
import com.cannontech.dr.controlaudit.model.ControlAuditStats;
import com.cannontech.dr.controlaudit.model.ControlAuditSummary;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventState;

public class ControlEventDaoImpl implements ControlEventDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;

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
    public void createNewEventMapping(int eventId, int groupId, Instant startTime, Instant stopTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink p = sql.insertInto("ControlEvent");
        p.addValue("ControlEventId", eventId);
        p.addValue("GroupId", groupId);
        p.addValue("StartTime", startTime);
        p.addValue("ScheduledStopTime", stopTime);
        jdbcTemplate.update(sql);

    }

    @Override
    public void insertDeviceControlEvent(int eventId, int loadGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ControlEventDevice (DeviceId, ControlEventId, OptOutEventId, Result)");
        sql.append("   (SELECT ypo.PaobjectId, ").appendArgument(eventId).append(",").append("ooe.OptOutEventId").append(",").appendArgument_k(UNKNOWN);
        sql.append("    FROM LMHardwareControlGroup lmhcg");
        sql.append("      JOIN InventoryBase inv ON inv.InventoryId = lmhcg.InventoryId");
        sql.append("      LEFT JOIN OptOutEvent ooe ON ooe.InventoryId = lmhcg.InventoryId");
        sql.append("      AND (ooe.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT).append(")");
        sql.append("      JOIN YukonPAObject ypo on ypo.PaobjectId = inv.DeviceId");
        sql.append("    WHERE lmhcg.LMGroupId").eq(loadGroupId);
        sql.append("      AND NOT lmhcg.GroupEnrollStart IS NULL");
        sql.append("      AND lmhcg.GroupEnrollStop IS NULL");
        sql.append("   )");
        jdbcTemplate.update(sql);
    }

    @Override
    public void updateDeviceControlEvent(int eventId, int deviceId, List<ControlEventDeviceStatus> skipUpdateForStatus,
            ControlEventDeviceStatus recievedDeviceStatus, Instant deviceRecievedTime) {
        SqlStatementBuilder where = new SqlStatementBuilder();
        if (recievedDeviceStatus != ControlEventDeviceStatus.SUCCESS_COMPLETED) {
            where.append("EXISTS");
            where.append("(SELECT 1 FROM ControlEventDevice");
            where.append("WHERE");
            where.append(getWhereClauseFragment(eventId, deviceId));
            where.append("AND RESULT").in_k(skipUpdateForStatus).append(")");
            where.append("AND");
        }
        where.append(getWhereClauseFragment(eventId, deviceId));
        updateDeviceControlEvent(eventId, deviceId, recievedDeviceStatus.name(), deviceRecievedTime, where);
    }

    private SqlFragmentSource getWhereClauseFragment(int eventId, int deviceId) {
        SqlStatementBuilder whereClause = new SqlStatementBuilder();
        whereClause.append("ControlEventId").eq(eventId);
        whereClause.append("  AND DeviceId").eq(deviceId);
        return whereClause;
    }

    private void updateDeviceControlEvent(int eventId, int deviceId, String recievedDeviceStatus,
            Instant deviceRecievedTime, SqlFragmentSource where) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ControlEventDevice");
        sql.set("Result", recievedDeviceStatus, "DeviceReceivedTime", deviceRecievedTime);
        sql.append("WHERE");
        sql.appendFragment(where);
        jdbcTemplate.update(sql);

    }

    private final static YukonRowMapper<ControlAuditSummary> auditSummaryRowMapper =
        new YukonRowMapper<ControlAuditSummary>() {
            @Override
            public ControlAuditSummary mapRow(YukonResultSet rs) throws SQLException {
                String programName = rs.getString("ProgramName");
                Instant startTime = rs.getInstant("StartTime");
                int numConfirmed = rs.getInt("Confirmed");
                int numUnknowns = rs.getInt("Unknown");
                ControlAuditSummary auditSummary =
                    new ControlAuditSummary(programName, startTime, numConfirmed, numUnknowns);
                return auditSummary;
            }
        };

    @Override
    public List<ControlAuditSummary> getControlAuditSummary(int numberOfEvents) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM (");
        sql.append("    SELECT ROW_NUMBER() OVER (ORDER BY ce.StartTime DESC) AS RowNumber, pgypo.PAOName AS ProgramName, ce.StartTime AS StartTime,");
        sql.append("      SUM(CASE WHEN ced.Result").eq_k(UNKNOWN).append("THEN 1 ELSE 0 END) Unknown,");
        sql.append("      SUM(CASE WHEN ced.Result").in_k(ControlEventDeviceStatus.getAllDeviceStatus()).append("THEN 1 ELSE 0 END) Confirmed");
        sql.append("    FROM ControlEvent ce");
        sql.append("      JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("      JOIN InventoryBase inv ON inv.DeviceId = ced.DeviceId");
        sql.append("      JOIN LMHardwareControlGroup lmhcg ON lmhcg.InventoryId = inv.InventoryId");
        sql.append("      JOIN LMProgramWebPublishing lmpwp ON lmpwp.ProgramId = lmhcg.ProgramId");
        sql.append("      JOIN yukonPAObject pgypo on pgypo.PAObjectId = lmpwp.DeviceId");
        sql.append("    WHERE  NOT lmhcg.GroupEnrollStart IS NULL");
        sql.append("      AND lmhcg.GroupEnrollStop IS NULL");
        sql.append("      GROUP BY pgypo.PAOName, ce.StartTime) AS tbl");
        sql.append("WHERE tbl.RowNumber").lte(numberOfEvents);
        List<ControlAuditSummary> auditSummaries = jdbcTemplate.query(sql, auditSummaryRowMapper);
        return auditSummaries;
    }

    private final static YukonRowMapper<ControlAuditStats> controlAuditStatsRowMapper =
        new YukonRowMapper<ControlAuditStats>() {
            @Override
            public ControlAuditStats mapRow(YukonResultSet rs) throws SQLException {

                int controlEventId = rs.getInt("EventId");
                String programName = rs.getString("Program");
                String loadGroupName = rs.getString("LoadGroup");
                Instant startTime = rs.getInstant("StartTime");
                int numConfirmed = rs.getInt("Confirmed");
                int numUnknowns = rs.getInt("Unknown");
                ControlAuditStats auditStats =
                    new ControlAuditStats(controlEventId, programName, loadGroupName, startTime, numConfirmed,
                        numUnknowns);
                return auditStats;
            }
        };

    @Override
    public List<ControlAuditStats> getControlAuditStats(Range<Instant> range, PagingParameters pagingParameters,
            SortingParameters sortingParameters) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT * FROM (");
        sql.append("    SELECT ROW_NUMBER() OVER (ORDER BY ");
        sql.append(sortingParameters.getSort());
        sql.append(sortingParameters.getDirection());
        sql.append("    ) AS RowNumber, EventId, Program, LoadGroup, StartTime, Unknown, Confirmed ");
        sql.append("    FROM (");
        sql.append("        SELECT ce.ControlEventId AS EventId, pgypo.PAOName AS Program, lgypo.PAOName AS LoadGroup, ce.StartTime AS StartTime,");
        sql.append("          SUM(CASE WHEN ced.Result").eq_k(UNKNOWN).append("THEN 1 ELSE 0 END) Unknown,");
        sql.append("          SUM(CASE WHEN ced.Result").in_k(ControlEventDeviceStatus.getAllDeviceStatus()).append("THEN 1 ELSE 0 END) Confirmed");
        sql.append("        FROM ControlEvent ce");
        sql.append("          JOIN ControlEventDevice ced ON ced.ControlEventId = ce.ControlEventId");
        sql.append("          JOIN YukonPAObject dypo ON dypo.PAObjectId = ced.DeviceId");
        sql.append("          JOIN InventoryBase inv ON inv.DeviceId = ced.DeviceId");
        sql.append("          JOIN LMHardwareControlGroup lmhcg ON lmhcg.InventoryId = inv.InventoryId");
        sql.append("          JOIN yukonPAObject lgypo on lgypo.PAObjectId = lmhcg.LMGroupId");
        sql.append("          JOIN LMProgramWebPublishing lmpwp ON lmpwp.ProgramId = lmhcg.ProgramId");
        sql.append("          JOIN yukonPAObject pgypo on pgypo.PAObjectId = lmpwp.DeviceId");
        sql.append("        WHERE  NOT lmhcg.GroupEnrollStart IS NULL");
        sql.append("          AND lmhcg.GroupEnrollStop IS NULL");
        if (range != null) {
            sql.append("      AND StartTime").gt(range.getMin());
            sql.append("      AND StartTime").lt(range.getMax());
        }
        sql.append("        GROUP BY ce.ControlEventId, ce.StartTime, lgypo.PAOName, pgypo.PAOName) AS innertable");
        sql.append("    ) outertable ");
        sql.append("WHERE RowNumber BETWEEN").append(pagingParameters.getOneBasedStartIndex());
        sql.append("  AND").append(pagingParameters.getOneBasedEndIndex());
        List<ControlAuditStats> auditStats = jdbcTemplate.query(sql, controlAuditStatsRowMapper);
        return auditStats;
    }

}
