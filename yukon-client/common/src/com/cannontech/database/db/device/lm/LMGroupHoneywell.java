package com.cannontech.database.db.device.lm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class LMGroupHoneywell extends com.cannontech.database.db.DBPersistent {

    private static final long serialVersionUID = 1L;
    private static final Integer MAX_GROUPID = 10000;
    private Integer deviceID;
    public static final String TABLE_NAME = "LMGroupHoneywellWiFi";
    public static final String SETTER_COLUMNS[] = { "DeviceID", "GroupID" };
    public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

    private static YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
    private static final Logger log = YukonLogManager.getLogger(LMGroupHoneywell.class);

    /**
     * LMGroupHoneywell constructor comment.
     */
    public LMGroupHoneywell() {
        super();
    }

    /**
     * add method comment.
     */
    public void add() throws java.sql.SQLException {
        int groupID = getNextGroupID();
        Object addValues[] = { getDeviceID(), groupID };
        add(TABLE_NAME, addValues);

    }

    /**
     * delete method comment.
     */
    public void delete() throws java.sql.SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID());
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return java.lang.Integer
     */
    public Integer getDeviceID() {
        return deviceID;
    }

    /**
     * This method was created in VisualAge.
     * 
     * @param newValue java.lang.Integer
     */
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

    public static boolean isMaximumGroupLimitExceeded(PaoType paoType) {
        List<Integer> groupIDs = getGroupIDs();

        if (groupIDs != null && groupIDs.size() > MAX_GROUPID) {
            return true;
        }
        return false;
    }

    /**
     * Get GroupID in sequence until it reaches to max GroupID but if GroupID is more than max GroupID, then
     * it will look for first missing GroupID (or deleted GroupID)
     */

    private final static Integer getNextGroupID() {
        int groupID = 0;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT MAX(GroupID)");
            sql.append("FROM LMGroupHoneywellWiFi");

            groupID = jdbcTemplate.queryForInt(sql) + 1;

            if (groupID > MAX_GROUPID) {
                List<Integer> groupIDs = getGroupIDs();
                if (groupIDs != null && groupIDs.size() <= MAX_GROUPID) {
                    groupID = findFirstMissingSeqNumber(groupIDs);
                }
            }
        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
        }
        return groupID;

    }

    /**
     * Get list of sorted GroupID from LMGroupHoneywellWiFi table
     */
    private final static List<Integer> getGroupIDs() {
        List<Integer> groupIDs = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT GroupID");
            sql.append("FROM LMGroupHoneywellWiFi");
            sql.append("ORDER BY GroupID");

            RowMapper<Integer> intMapper = new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("GroupID");
                }
            };

            groupIDs = jdbcTemplate.query(sql.toString(), intMapper);

        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
        }
        return groupIDs;

    }
    
    /**
     * Find the first missing element in sorted list (database sequence for GroupID)
     */
    private static int findFirstMissingSeqNumber(List<Integer> groupIDs) {
        int groupID = 0;
        for (int groupIdCount = 0; groupIdCount <= MAX_GROUPID; ++groupIdCount) {
            if (groupIdCount + 1 != groupIDs.get(groupIdCount)) {
                groupID = groupIdCount + 1;
                break;
            }
        }
        return groupID;
    }

}
