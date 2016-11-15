package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

public class LMGroupHoneywell extends com.cannontech.database.db.DBPersistent {

    private static final long serialVersionUID = 1L;
    private static final Integer MIN_GROUPID = 1;
    private static final Integer MAX_GROUPID = 10000;
    private Integer deviceID;
    public static final String TABLE_NAME = "LMGroupHoneywellWiFi";
    public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

    private static YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
    private static final Logger log = YukonLogManager.getLogger(LMGroupHoneywell.class);

    public LMGroupHoneywell() {
        super();
    }

    public void add() throws java.sql.SQLException {
        int honeywellGroupId = getNextHoneywellGroupId();
        Object addValues[] = { getDeviceID(), honeywellGroupId };
        add(TABLE_NAME, addValues);

    }

    public void delete() throws java.sql.SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID());
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Integer newValue) {
        this.deviceID = newValue;
    }

    @Override
    public void retrieve() throws SQLException {
        // nothing to retrieve
    }

    @Override
    public void update() throws SQLException {
        // nothing to update
    }

    /**
     * Check maximum group limit for honeywell group
     */

    public static boolean isMaximumGroupLimitExceeded() {
        Integer groupIdCount = getHoneywellGroupIdCount();

        if (groupIdCount != null && groupIdCount> MAX_GROUPID) {
            return true;
        }
        return false;
    }

    /**
     * Get next honeywellGroupId from LMGroupHoneywellWifi table
     */

    private final static Integer getNextHoneywellGroupId() {
        int groupID = 0;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT MIN(lmGroupHoneywellWifi.HoneywellGroupId + 1)");
            sql.append("FROM LMGroupHoneywellWifi lmGroupHoneywellWifi");
            sql.append("WHERE NOT EXISTS");
            sql.append("    (SELECT HoneywellGroupId FROM LMGroupHoneywellWifi lmGroupHoneywellWifi2");
            sql.append("        WHERE lmGroupHoneywellWifi2.HoneywellGroupId = lmGroupHoneywellWifi.HoneywellGroupId + 1)");

            groupID = jdbcTemplate.queryForInt(sql);

            if (groupID == 0) {
                return MIN_GROUPID;
            }

        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
        }
        return groupID;

    }

    /**
     * Get count of HoneywellGroupId from LMGroupHoneywellWiFi table
     */
    private final static Integer getHoneywellGroupIdCount() {
        Integer groupIdCount = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT COUNT(HoneywellGroupId)");
            sql.append("FROM LMGroupHoneywellWiFi");

            groupIdCount = jdbcTemplate.queryForInt(sql);

        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
        }
        return groupIdCount;

    }

}
