package com.cannontech.database.db.state;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

public class StateGroup extends DBPersistent {
    private Integer stateGroupID = null;
    private String name = null;
    private String groupType = StateGroupUtils.GROUP_TYPE_STATUS;

    public static final String SETTER_COLUMNS[] = {
        "Name", "GroupType"
    };

    public static final String CONSTRAINT_COLUMNS[] = { "StateGroupID" };

    private static final String TABLE_NAME = "StateGroup";

    public StateGroup() {
    }

    public StateGroup(Integer stateGroupID) {
        initialize(stateGroupID, null, null);
    }

    public StateGroup(Integer stateGroupID, String name, String groupType_) {
        initialize(stateGroupID, name, groupType_);
    }

    public void add() throws SQLException {
        Object setValues[] = { getStateGroupID(), getName(), getGroupType() };

        add(TABLE_NAME, setValues);
    }

    public void delete() throws SQLException {
        Object constraintValues[] = { getStateGroupID() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    public String getName() {
        return name;
    }

    public final static Integer getNextStateGroupID() throws SQLException {
        return getNextStateGroupID(CtiUtilities.getDatabaseAlias());
    }

    public final static Integer getNextStateGroupID(String databaseAlias) throws SQLException {
        SqlStatement stmt = new SqlStatement("SELECT Max(StateGroupID)+1 FROM " + TABLE_NAME, databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString());
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
            return -5;
        }
    }

    public Integer getStateGroupID() {
        return stateGroupID;
    }

    public static final StateGroup[] getStateGroups() throws SQLException {
        return getStateGroups(CtiUtilities.getDatabaseAlias());
    }

    public static final StateGroup[] getStateGroups(String databaseAlias) throws SQLException {
        List<StateGroup> tmpList = new ArrayList<StateGroup>(30);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql = "SELECT STATEGROUPID,NAME,GroupType FROM " + TABLE_NAME;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());

                rset = pstmt.executeQuery();

                while (rset.next()) {
                    tmpList.add(new StateGroup(rset.getInt("StateGroupID"),
                                               rset.getString("Name"),
                                               rset.getString("GroupType")));
                }

            }
        } catch (SQLException e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        StateGroup retVal[] = new StateGroup[tmpList.size()];
        tmpList.toArray(retVal);

        return retVal;
    }

    private void initialize(Integer stateGroupID, String name, String groupType_) {
        setStateGroupID(stateGroupID);
        setName(name);
        setGroupType(groupType_);
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getStateGroupID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setName((String) results[0]);
            setGroupType((String) results[1]);
        }
        else {
            throw new Error(getClass() + "::retrieve - Incorrect number of results returned");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStateGroupID(Integer stateGroupID) {
        this.stateGroupID = stateGroupID;
    }

    public String toString() {
        return getName();
    }

    public void update() throws SQLException {
        Object constraintValues[] = { getStateGroupID() };
        Object setValues[] = { getName(), getGroupType() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}
