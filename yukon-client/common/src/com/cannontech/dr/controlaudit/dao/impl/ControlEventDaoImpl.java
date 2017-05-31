package com.cannontech.dr.controlaudit.dao.impl;

import static com.cannontech.dr.controlaudit.ControlEventDeviceStatus.UNKNOWN;

import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.dao.ControlEventDao;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
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
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if ((dbChange.getDbChangeType() == DbChangeType.ADD || dbChange.getDbChangeType() == DbChangeType.UPDATE)
                    && dbChange.getDatabase() == DBChangeMsg.CHANGE_OPTOUT_DB) {
                    OptOutEvent optOutEvent = optOutEventDao.getOptOutEventById(dbChange.getId());
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

}
