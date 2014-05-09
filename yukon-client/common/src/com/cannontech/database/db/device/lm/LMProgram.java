package com.cannontech.database.db.device.lm;

import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;

public class LMProgram extends com.cannontech.database.db.DBPersistent 
{
    private Integer deviceID = null;
    private String controlType = null;
    private Integer constraintID = new Integer(CtiUtilities.NONE_ZERO_ID);

    public static final String SETTER_COLUMNS[] = 
    { 
        "ControlType", "ConstraintID"
    };

    public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
    public static final String TABLE_NAME = "LMProgram";

    @Override
    public void add() throws java.sql.SQLException {
        Object addValues[] = { getDeviceID(), getControlType(), getConstraintID() };
        add( TABLE_NAME, addValues );
    }

    @Override
    public void delete() throws java.sql.SQLException {
        delete( TABLE_NAME, "DeviceID", getDeviceID() );
    }

    public java.lang.String getControlType() {
        return controlType;
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public java.lang.Integer getConstraintID() {
        return constraintID;
    }

    /**
     * This method returns all the LMProgram ID's that are not assgined
     *  to a Control Area.
     */
    public static java.util.Vector<Integer> getUnassignedPrograms() {
        java.util.Vector<Integer> returnVector = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        
        
        String sql = "SELECT DeviceID FROM " + TABLE_NAME + " where " +
                         " deviceid not in (select lmprogramdeviceid from " + LMControlAreaProgram.TABLE_NAME +
                         ") ORDER BY deviceid";
        
        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
            
            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            }
            pstmt = conn.prepareStatement(sql.toString());
            rset = pstmt.executeQuery();
            returnVector = new Vector<Integer>(5); //rset.getFetchSize()
            
            while (rset.next()) {
                returnVector.addElement(new Integer(rset.getInt("DeviceID")));
            }        
        } catch( java.sql.SQLException e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        } finally {
            SqlUtils.close(rset, pstmt, conn );
        }
        return returnVector;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { getDeviceID() };    
        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
        
        if( results.length == SETTER_COLUMNS.length ) {
            setControlType( (String) results[0] );
            setConstraintID( (Integer) results[1] );
        }
        else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void setControlType(java.lang.String newControlType) {
        controlType = newControlType;
    }

    public void setDeviceID(Integer newValue) {
        this.deviceID = newValue;
    }

    public void setConstraintID(java.lang.Integer newID) {
        constraintID = newID;
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object setValues[] = { getControlType(), getConstraintID() };
        
        Object constraintValues[] = { getDeviceID() };
        
        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

}
