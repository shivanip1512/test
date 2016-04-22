package com.cannontech.database.db.capcontrol;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Data class to retrieve and persist subBus to substation assignments from the database.
 */
public class CCSubstationSubBusList extends com.cannontech.database.db.DBPersistent {
	private Integer substationID = null;
	private Integer substationBusID = null;
	private Integer displayOrder = null;
    private static List<CCSubstationSubBusList> subBusesForSubstationList = new ArrayList<CCSubstationSubBusList>();
    private static JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
	public static final String SETTER_COLUMNS[] = { "DisplayOrder" };
	public static final String CONSTRAINT_COLUMNS[] = { "SubstationID", "SubstationBusID" };
	public static final String TABLE_NAME= "CCSubstationSubBusList";

    /**
     * Constructor
     */
    public CCSubstationSubBusList(Integer substationID, Integer subBusID, Integer dispOrder) {
    	super();
    	setSubstationID( substationID );
    	setSubstationBusID( subBusID );
    	setDisplayOrder( dispOrder );
    }
    
    static public class ModelRow {
        public Integer subStationId;
        public Integer diplayOrder;
    }
    
    /**
     * add method comment.
     */
    @Override
    public void add() throws java.sql.SQLException {
    	Object[] addValues = { getSubstationID(), getSubstationBusID(), getDisplayOrder() };
    	add( TABLE_NAME, addValues );
    }
    
    /**
     * delete method comment.
     */
    @Override
    public void delete() throws java.sql.SQLException {
    	Object[] values = { getSubstationID(), getSubstationBusID() };
    	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
    }
    
    /**
     * This method was created by Cannon Technologies Inc.
     */
    public static boolean deleteCCSubBusFromSubstationList(Integer substationID, Integer subBusID, java.sql.Connection conn) {
    	java.sql.PreparedStatement pstmt = null;
    	String sql = null;
    	if( substationID != null ) {
    		//must be deleting a substation
    		sql = "DELETE FROM " + TABLE_NAME + " WHERE SubstationID = " + substationID;
    	} else if( subBusID != null ) {
    		//must be deleting a subBus
    		sql = "DELETE FROM " + TABLE_NAME + " WHERE substationBusID = " + subBusID;
    	}
    	try {		
    		if( conn == null ) {
    			throw new IllegalStateException("Database connection can not be null.");
    		} else {
    			pstmt = conn.prepareStatement(sql.toString());			
    			pstmt.executeUpdate();					
    		}		
    	} catch( java.sql.SQLException e ) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    		return false;
    	} finally {
    		try {
    			if( pstmt != null ) { 
    				pstmt.close();
                }
    		} catch( java.sql.SQLException e2 ) {
    			CTILogger.error( e2.getMessage(), e2 );
    		}	
    	}
    
    	return true;
    }
    
    /**
     * @param substationId java.lang.Integer
     */
    public static List<CCSubstationSubBusList> getCCSubBusesOnSubstation(Integer substationId) {
    	String sql = "SELECT substationId, substationBusId, displayOrder FROM " + TABLE_NAME + " WHERE substationId = ? ORDER BY displayOrder";
        subBusesForSubstationList = new ArrayList<CCSubstationSubBusList>();
        jdbcOps.query(sql, new Integer[] {substationId}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                subBusesForSubstationList.add( new CCSubstationSubBusList( rs.getInt("substationId"), rs.getInt("substationBusId"), rs.getInt("displayOrder")));
            }
        });
    
    	return subBusesForSubstationList;
    }
    
    /**
     * @param substationId java.lang.Integer
     */
    public static Integer getSubStationForSubBus(Integer subBusId) {
        Integer substationId = -1;
        String sql = "SELECT DISTINCT substationId FROM " + TABLE_NAME + " WHERE substationBusId = ? ";
        try {
            substationId = jdbcOps.queryForObject(sql, Integer.class, subBusId);
        }catch (EmptyResultDataAccessException e) {
            return -1;
        }
        return substationId;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 2:02:03 PM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getDisplayOrder() {
    	return displayOrder;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 11:47:10 AM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getSubstationBusID() {
    	return substationBusID;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 2:02:03 PM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getSubstationID() {
    	return substationID;
    }
    
    /**
     * retrieve method comment.
     */
    @Override
    public void retrieve() throws java.sql.SQLException {
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 2:02:03 PM)
     * @param newDisplayOrder java.lang.Integer
     */
    public void setDisplayOrder(java.lang.Integer newDisplayOrder) {
    	displayOrder = newDisplayOrder;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 11:47:10 AM)
     * @param newSubBusID java.lang.Integer
     */
    public void setSubstationBusID(java.lang.Integer newSubBusID) {
    	substationBusID = newSubBusID;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (11/9/2001 2:02:03 PM)
     * @param newSubstationBusID java.lang.Integer
     */
    public void setSubstationID(java.lang.Integer newSubstationID) {
    	substationID = newSubstationID;
    }
    
    /**
     * update method comment.
     */
    @Override
    public void update() throws java.sql.SQLException {
    }
}
