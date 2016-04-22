package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

public class CCFeederSubAssignment extends DBPersistent {

    private Integer substationBusID = null;
    private Integer feederID = null;
    private Integer displayOrder = null;
    private static JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

    public static final String SETTER_COLUMNS[] = { "DisplayOrder" };

    public static final String CONSTRAINT_COLUMNS[] = { "SubstationBusID", "FeederID" };

    public static final String TABLE_NAME = "CCFeederSubAssignment";

    public CCFeederSubAssignment(Integer feedID, Integer subID, Integer dispOrder) {
        super();

        setFeederID(feedID);
        setSubstationBusID(subID);
        setDisplayOrder(dispOrder);
    }

    @Override
    public void add() throws SQLException {
        Object[] addValues = { getSubstationBusID(), getFeederID(), getDisplayOrder() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        Object[] values = { getFeederID(), getSubstationBusID() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, values);

    }

    public static boolean deleteCCFeedersFromSubList(Integer subId, Integer feederID, Connection conn) {
        PreparedStatement pstmt = null;

        String sql = null;

        if (subId != null) {
            // must be deleting a SubBus
            sql = "DELETE FROM " + TABLE_NAME + " WHERE SubstationBusID = " + subId;
        } else if (feederID != null) {
            // must be deleting a feeder
            sql = "DELETE FROM " + TABLE_NAME + " WHERE FeederID = " + feederID;
        }

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection can not be null.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);// something is up
            }
        }

        return true;
    }

    public static Integer getSubBusIdForFeeder(int paoId) {
        String sql = "SELECT SubstationBusId FROM " + TABLE_NAME + " WHERE Feederid = ? ";

        Integer substationBusId = -1;
        try {
            substationBusId = jdbcOps.queryForObject(sql, Integer.class, paoId);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }

        return substationBusId;
    }

    public static List<CCFeederSubAssignment> getCCFeedersOnSub(Integer subId, Connection conn) {
        List<CCFeederSubAssignment> returnList = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT FeederID, DisplayOrder FROM " + TABLE_NAME + " " + "WHERE SubstationBusID = ? "
                + "ORDER BY DisplayOrder";

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection can not be null.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.setInt(1, subId.intValue());

                rset = pstmt.executeQuery();
                returnList = new ArrayList<CCFeederSubAssignment>();

                while (rset.next()) {
                    returnList.add(new CCFeederSubAssignment(rset.getInt("FeederID"), 
                                                             subId, 
                                                             rset.getInt("DisplayOrder")));
                }

            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt);
        }

        return returnList;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Integer getFeederID() {
        return feederID;
    }

    public Integer getSubstationBusID() {
        return substationBusID;
    }

    @Override
    public void retrieve() throws SQLException {
        /* WE DO NOT RETRIEVE ANY VALUES WITH THIS OBJECT */
    }

    public void setDisplayOrder(Integer newDisplayOrder) {
        displayOrder = newDisplayOrder;
    }

    public void setFeederID(Integer newFeederID) {
        feederID = newFeederID;
    }

    public void setSubstationBusID(Integer newSubstationBusID) {
        substationBusID = newSubstationBusID;
    }

    @Override
    public void update() throws SQLException {
        /* WE DO NOT UPDATE ANY VALUES WITH THIS OBJECT */
    }
}
