package com.cannontech.database.db.starscustomer;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerAction extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer actionID = null;
    private String action = null;
    private String validFor = null;

    public static final String[] SETTER_COLUMNS = {
        "Action", "ValidFor"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ActionID" };

    public static final String TABLE_NAME = "CustomerAction";

    public static final String GET_NEXT_ACTION_ID_SQL =
        "SELECT MAX(ActionID) FROM " + TABLE_NAME;

    public CustomerAction() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getActionID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getActionID(), getAction(), getValidFor()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAction(), getValidFor()
        };

        Object[] constraintValues = { getActionID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getActionID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAction( (String) results[0] );
            setValidFor( (String) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextActionID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextActionID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_ACTION_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextActionID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextActionID );
    }

    public Integer getActionID() {
        return actionID;
    }

    public void setActionID(Integer newActionID) {
        actionID = newActionID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String newAction) {
        action = newAction;
    }

    public String getValidFor() {
        return validFor;
    }

    public void setValidFor(String newValidFor) {
        validFor = newValidFor;
    }
}