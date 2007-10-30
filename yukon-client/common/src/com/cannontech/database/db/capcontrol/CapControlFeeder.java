package com.cannontech.database.db.capcontrol;

import com.cannontech.common.util.CtiUtilities;

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
    private Integer phaseB = new Integer(CtiUtilities.NONE_ZERO_ID);
    private Integer phaseC = new Integer(CtiUtilities.NONE_ZERO_ID);

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
            setPhaseB((Integer) results[6]);
            setPhaseC((Integer) results[7]);
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

    public Integer getPhaseB() {
        return phaseB;
    }
    
    public void setPhaseB(Integer phaseB) {
        this.phaseB = phaseB;
    }

    public Integer getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(Integer phaseC) {
        this.phaseC = phaseC;
    }

    public String getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(String usePhaseData) {
        this.usePhaseData = usePhaseData;
    }
    
    public boolean getUsePhaseDataBoolean() {
        if(usePhaseData.equalsIgnoreCase("Y")) {
            return true;
        }
        return false;
    }

    public void setUsePhaseDataBoolean(boolean bool) {
        if(bool) {
            this.usePhaseData = "Y";
        }else {
            this.usePhaseData = "N";
        }
    }
    
}