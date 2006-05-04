package com.cannontech.database.db.capcontrol;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.db.point.Point;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubstationBus extends com.cannontech.database.db.DBPersistent 
{
	private Integer substationBusID = null;
	private Integer currentVarLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer currentWattLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String mapLocationID = "0";  //old integer default
	private Integer strategyID = new Integer(CtiUtilities.NONE_ZERO_ID);	
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	//some variables for the dual bus support
	private Integer altSubPAOId = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer switchPointID =   new Integer(CtiUtilities.NONE_ZERO_ID);
	private String dualBusEnabled = "N";
    private String multiMonitorControl = "N";
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"CurrentVarLoadPointID",
			"CurrentWattLoadPointID", "MapLocationID", "StrategyID",
			"CurrentVoltLoadPointID", "AltSubId", "SwitchPointId",
			"DualBusEnabled", "MultiMonitorControl"};

	public static final String CONSTRAINT_COLUMNS[] = { "SubstationBusID"};


	public static final String TABLE_NAME = "CapControlSubstationBus";

/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlSubstationBus()
{
	super();
}


/**
 * DeviceTwoWayFlags constructor comment.
 */
public CapControlSubstationBus(Integer subID) 
{
	super();
	setSubstationBusID( subID );
}


/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
    setAltSubPAOId(getSubstationBusID());
	Object[] addValues = 
	{
		getSubstationBusID(),
				getCurrentVarLoadPointID(), getCurrentWattLoadPointID(),
				getMapLocationID(), getStrategyID(),
				getCurrentVoltLoadPointID(), getAltSubPAOId(),
				getSwitchPointID(), getDualBusEnabled(), getMultiMonitorControl()};

	add( TABLE_NAME, addValues );
}


/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	handleAltSubIdOnDelete (getSubstationBusID());
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getSubstationBusID() );	
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentVarLoadPointID() {
	return currentVarLoadPointID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentWattLoadPointID() {
	return currentWattLoadPointID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public String getMapLocationID() {
	return mapLocationID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationBusID() {
	return substationBusID;
}


/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getSubstationBusID()};
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCurrentVarLoadPointID( (Integer) results[0] );
		setCurrentWattLoadPointID( (Integer) results[1] );
		setMapLocationID( (String) results[2] );
		setStrategyID( (Integer) results[3] );
		setCurrentVoltLoadPointID( (Integer) results[4] );
		setAltSubPAOId((Integer)results[5]);
		setSwitchPointID((Integer)results[6]);
		setDualBusEnabled((String)results[7]);
        setMultiMonitorControl((String)results[8]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newCurrentVarLoadPointID java.lang.Integer
 */
public void setCurrentVarLoadPointID(java.lang.Integer newCurrentVarLoadPointID) {
	currentVarLoadPointID = newCurrentVarLoadPointID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newCurrentWattLoadPointID java.lang.Integer
 */
public void setCurrentWattLoadPointID(java.lang.Integer newCurrentWattLoadPointID) {
	currentWattLoadPointID = newCurrentWattLoadPointID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newMapLocationID java.lang.Integer
 */
public void setMapLocationID(String newMapLocationID) {
	mapLocationID = newMapLocationID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:42:02 PM)
 * @param newSubstationBusID java.lang.Integer
 */
public void setSubstationBusID(java.lang.Integer newSubstationBusID) {
	substationBusID = newSubstationBusID;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= 
	{
		getCurrentVarLoadPointID(),
				getCurrentWattLoadPointID(), getMapLocationID(),
				getStrategyID(), getCurrentVoltLoadPointID(),
				getAltSubPAOId(), getSwitchPointID(),getDualBusEnabled(), getMultiMonitorControl() };


	Object constraintValues[] = { getSubstationBusID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * @return
	 */
	public Integer getCurrentVoltLoadPointID() {
		return currentVoltLoadPointID;
	}

	/**
	 * @return
	 */
	public Integer getStrategyID() {
		return strategyID;
	}

	/**
	 * @param integer
	 */
	public void setCurrentVoltLoadPointID(Integer integer) {
		currentVoltLoadPointID = integer;
	}

	/**
	 * @param integer
	 */
	public void setStrategyID(Integer integer) {
		strategyID = integer;
	}

	/**
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 *
	 * This method returns a SubBus's name that already uses
	 *  the pointID for its VAR point, if returns null if the
	 *  pointID is not yet used.
	 */
	public static com.cannontech.common.util.NativeIntVector getUsedVARPointIDs( Integer excludedSubBusId, Integer excludedFeederID )
	{
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		NativeIntVector vect = new NativeIntVector(10);
	
	   if( excludedFeederID == null )
	      excludedFeederID = new Integer(0);
	      
	   if( excludedSubBusId == null )
	      excludedSubBusId = new Integer(0);
	      
	//	String sql = "SELECT CurrentVarLoadPointID FROM " + TABLE_NAME +
	//					 " where SubstationBusID <> " + excludedPAOId;
	
	   //Get all the used Var PointIDs in the CapControlSubBus table and the Feeder table                
	   String sql = "select p.pointid from " + Point.TABLE_NAME + " p where p.pointid in " +
	      "(select currentvarloadpointid from " + CapControlSubstationBus.TABLE_NAME + 
	         " where substationbusid <> " + excludedSubBusId + ")" +
	      "or p.pointid in " +
	      "(select currentvarloadpointid from " + CapControlFeeder.TABLE_NAME + 
	         " where feederid <> " + excludedFeederID + ")";
	                
	
		try
		{		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());			
				rset = pstmt.executeQuery();
		
				while( rset.next() )
					vect.add( rset.getInt(1) );					
			}		
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) 
					pstmt.close();
				if( conn != null ) 
					conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		return vect;
	}



//	setters and getters for the SwitchPointId field 
	public Integer getSwitchPointID() {
		return switchPointID;
	}

	public void setSwitchPointID(Integer switchPointID) {
		this.switchPointID = switchPointID;
	}


	public String getDualBusEnabled() {
		return dualBusEnabled;
	}


	public void setDualBusEnabled(String dualBusEnabled) {
		this.dualBusEnabled = dualBusEnabled;
	}


	public Integer getAltSubPAOId() {
		return altSubPAOId;
	}


	public void setAltSubPAOId(Integer altSubPAOId) {
		this.altSubPAOId = altSubPAOId;
	}


    public String getMultiMonitorControl() {
        return multiMonitorControl;
    }


    public void setMultiMonitorControl(String multiMonitorControl) {
        this.multiMonitorControl = multiMonitorControl;
    }
    /**
     * takes care of setting the altsubid column to substationbusid if the dualBus is enabled
     * if a substation is deleted.
     */
    private void handleAltSubIdOnDelete (Integer subBusDeletedId) {
    	String query = 	"update capcontrolsubstationbus  " +
    					"set  altsubid = substationbusid," +
	    				"     dualbusenabled = 'N',		 " +
				    	"     switchpointid = 0			 " +
				    	"where altsubid = ?				 " +
				    	"and							 " +
				    	"dualbusenabled = 'Y'			 ";	   

    	
    	JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
    	yukonTemplate.update(query, new Integer[]{subBusDeletedId});
    }
}