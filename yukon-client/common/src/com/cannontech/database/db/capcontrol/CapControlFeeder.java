package com.cannontech.database.db.capcontrol;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

/**
 * Feeder object
 */
public class CapControlFeeder extends com.cannontech.database.db.DBPersistent {
	private Integer feederID = null;
	private Integer currentVarLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer currentWattLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String mapLocationID = "0";  //old integer default
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
    private String multiMonitorControl = "N";
    private String usePhaseData = "N";
    private Double phaseB = new Double(CtiUtilities.NONE_ZERO_ID);
    private Double phaseC = new Double(CtiUtilities.NONE_ZERO_ID);

	public static final String SETTER_COLUMNS[] = { 
		"CurrentVarLoadPointID", "CurrentWattLoadPointID",
		"MapLocationID", "CurrentVoltLoadPointID", "MultiMonitorControl",
        "UsePhaseData", "PhaseB", "PhaseC"
	};
	public static final String CONSTRAINT_COLUMNS[] = { "FeederID" };
	public static final String TABLE_NAME = "CapControlFeeder";

	/**
	 * Default constructor.
	 */
	public CapControlFeeder() {
		super();
	}
	
	/**
	 * DeviceTwoWayFlags constructor comment.
	 */
	public CapControlFeeder(Integer feedID) {
		super();
		setFeederID( feedID );
	}
	
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException {
		Object[] addValues = {
			getFeederID(), getCurrentVarLoadPointID(),
			getCurrentWattLoadPointID(), getMapLocationID(),
			getCurrentVoltLoadPointID(),
            getMultiMonitorControl(), 
            getUsePhaseData(),
            getPhaseB(),
            getPhaseC()
		};
		add( TABLE_NAME, addValues );
	}
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getFeederID() );	
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
	 * Creation date: (11/9/2001 6:46:13 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getFeederID() {
		return feederID;
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
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 *
	 * This method returns all the Feeders that are not assgined
	 *  to a SubBus.
	 */
	@SuppressWarnings({ "unchecked" })
    public static CapControlFeeder[] getUnassignedFeeders() {
		java.util.Vector returnVector = null;
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT FeederID FROM " + TABLE_NAME + " where " +
						 " FeederID not in (select FeederID from " + CCFeederSubAssignment.TABLE_NAME +
						 ") ORDER BY FeederID";
		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			} else {
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				returnVector = new java.util.Vector(5); //rset.getFetchSize()
				while( rset.next() ) {				
					returnVector.addElement( new CapControlFeeder(  new Integer(rset.getInt("FeederID")) ) );
				}
			}		
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally{
			SqlUtils.close(rset, pstmt, conn );
		}
	
		CapControlFeeder[] feeders = new CapControlFeeder[returnVector.size()];
		return (CapControlFeeder[])returnVector.toArray( feeders );
	}

	/**
	 * This method returns all the Feeder IDs that are not assgined
	 *  to a SubBus.
	 */
	public static int[] getUnassignedFeederIDs() {
		NativeIntVector intVect = new NativeIntVector(16);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		String sql = "SELECT FeederID FROM " + TABLE_NAME + " where " +
						 " FeederID not in (select FeederID from " + CCFeederSubAssignment.TABLE_NAME +
						 ") ORDER BY FeederID";

		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			} else {
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				while( rset.next() ) {				
					intVect.add( rset.getInt(1) );
				}
			}		
		} catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		} finally {
			SqlUtils.close(rset, pstmt, conn );
		}
		return intVect.toArray();
	}

	/**
	 * This method returns the SubBus ID that owns the given feeder ID.
	 * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
	 * 
	 */
	public static int getParentSubBusID( int feederID ) {
		int subBusID = CtiUtilities.NONE_ZERO_ID;
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT SubStationBusID FROM " +
					 	CCFeederSubAssignment.TABLE_NAME +
					 	" where FeederID = ?";	
		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			} else {
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt( 1, feederID );				
				rset = pstmt.executeQuery();
				if( rset.next() ) {				
					subBusID = rset.getInt(1);
				}						
			}		
		}catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}finally {
			try {
				if( pstmt != null ) {
                    pstmt.close();
                }
				if( conn != null ) {
                    conn.close();
                }
			} catch( java.sql.SQLException e2 ) {
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
		return subBusID;
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException {
		Object constraintValues[] = { getFeederID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length ) {
			setCurrentVarLoadPointID( (Integer) results[0] );
			setCurrentWattLoadPointID( (Integer) results[1] );
			setMapLocationID( (String) results[2] );
			setCurrentVoltLoadPointID( (Integer) results[3] );
            setMultiMonitorControl((String) results[4]);
            setUsePhaseData((String) results[5]);
            setPhaseB((Double) results[6]);
            setPhaseC((Double) results[7]);
		} else {
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
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
	 * Creation date: (11/9/2001 6:46:13 PM)
	 * @param newFeederID java.lang.Integer
	 */
	public void setFeederID(java.lang.Integer newFeederID) {
		feederID = newFeederID;
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
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		Object setValues[]= { 
			getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), 
			getMapLocationID(), getCurrentVoltLoadPointID(),
            getMultiMonitorControl(), getUsePhaseData(),
            getPhaseB(), getPhaseC()
		};
		Object constraintValues[] = { getFeederID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getCurrentVoltLoadPointID() {
		return currentVoltLoadPointID;
	}

	/**
	 * @param integer
	 */
	public void setCurrentVoltLoadPointID(Integer integer) {
		currentVoltLoadPointID = integer;
	}

    public String getMultiMonitorControl() {
        return multiMonitorControl;
    }

    public void setMultiMonitorControl(String multiMonitorControl) {
        this.multiMonitorControl = multiMonitorControl;
    }

    public Double getPhaseB() {
        return phaseB;
    }
    
    public void setPhaseB(Double phaseB) {
        this.phaseB = phaseB;
    }

    public Double getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(Double phaseC) {
        this.phaseC = phaseC;
    }

    public String getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(String usePhaseData) {
        this.usePhaseData = usePhaseData;
    }
    
}