package com.cannontech.database.db.capcontrol;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.point.Point;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubstationBus extends com.cannontech.database.db.DBPersistent {
	private Integer substationBusID = null;
	private Integer currentVarLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer currentWattLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String mapLocationID = "0";  //old integer default
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer altSubPAOId = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer switchPointID =   new Integer(CtiUtilities.NONE_ZERO_ID);
	private String dualBusEnabled = "N";
    private String multiMonitorControl = "N";
    private String usePhaseData = "N";
    private Integer phaseB = new Integer(CtiUtilities.NONE_ZERO_ID);
    private Integer phaseC = new Integer(CtiUtilities.NONE_ZERO_ID);
    private String controlFlag = "N";
    private Integer voltReductionPointId = 0;
    private Integer disableBusPointId = 0;
	
	public static final String SETTER_COLUMNS[] = { 
		"CurrentVarLoadPointID", "CurrentWattLoadPointID", "MapLocationID", 
		"CurrentVoltLoadPointID", "AltSubId", "SwitchPointId",
		"DualBusEnabled", "MultiMonitorControl", "UsePhaseData",
		"PhaseB", "PhaseC", "ControlFlag", "VoltReductionPointId", "DisableBusPointId"};
	public static final String CONSTRAINT_COLUMNS[] = { "SubstationBusID"};
	public static final String TABLE_NAME = "CapControlSubstationBus";

    /**
     * DeviceTwoWayFlags constructor comment.
     */
    public CapControlSubstationBus() {
    	super();
    }
    
    /**
     * DeviceTwoWayFlags constructor comment.
     */
    public CapControlSubstationBus(Integer subID) {
    	super();
    	setSubstationBusID( subID );
    }
    
    /**
     * add method comment.
     */
    @Override
    public void add() throws java.sql.SQLException {
        setAltSubPAOId(getSubstationBusID());
    	Object[] addValues = {
    		getSubstationBusID(), getCurrentVarLoadPointID(), getCurrentWattLoadPointID(),
    		getMapLocationID(), getCurrentVoltLoadPointID(), getAltSubPAOId(),
    		getSwitchPointID(), getDualBusEnabled(), getMultiMonitorControl(),
            getUsePhaseData(), getPhaseB(), getPhaseC(), getControlFlag(), getVoltReductionPointId(),
            getDisableBusPointId()};
    
    	add( TABLE_NAME, addValues );
    }
    
    /**
     * delete method comment.
     */
    @Override
    public void delete() throws java.sql.SQLException {
    	handleAltSubIdOnDelete (getSubstationBusID());
    	handleSubStationAssignmentDelete(getSubstationBusID());
    	handleZoneDelete(getSubstationBusID());
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
    @Override
    public void retrieve() throws java.sql.SQLException {
    	Object constraintValues[] = { getSubstationBusID()};
    	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
    	if( results.length == SETTER_COLUMNS.length ) {
    		setCurrentVarLoadPointID( (Integer) results[0] );
    		setCurrentWattLoadPointID( (Integer) results[1] );
    		setMapLocationID( (String) results[2] );
    		setCurrentVoltLoadPointID( (Integer) results[3] );
    		setAltSubPAOId((Integer)results[4]);
    		setSwitchPointID((Integer)results[5]);
    		setDualBusEnabled((String)results[6]);
            setMultiMonitorControl((String)results[7]);
            setUsePhaseData((String) results[8]);
            setPhaseB((Integer) results[9]);
            setPhaseC((Integer) results[10]);
            setControlFlag((String) results[11]);
            setVoltReductionPointId((Integer) results[12]);
            setDisableBusPointId((Integer) results[13]);
    	}
    	else {
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
    @Override
    public void update() throws java.sql.SQLException {
    	Object setValues[]= {
    		getCurrentVarLoadPointID(), getCurrentWattLoadPointID(), getMapLocationID(),
    		getCurrentVoltLoadPointID(), getAltSubPAOId(), getSwitchPointID(),
            getDualBusEnabled(), getMultiMonitorControl(), getUsePhaseData(),
            getPhaseB(), getPhaseC(), getControlFlag(), getVoltReductionPointId(), 
            getDisableBusPointId()};
    
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
     * @param integer
     */
    public void setCurrentVoltLoadPointID(Integer integer) {
    	currentVoltLoadPointID = integer;
    }
    
    /**
     * This method was created in VisualAge.
     * @param pointID java.lang.Integer
     * This method returns a SubBus's name that already uses
     *  the pointID for its VAR point, if returns null if the
     *  pointID is not yet used.
     */
    public static NativeIntVector getUsedVARPointIDs( Integer excludedSubBusId, Integer excludedFeederID ){
    	java.sql.Connection conn = null;
    	java.sql.PreparedStatement pstmt = null;
    	java.sql.ResultSet rset = null;
    	NativeIntVector vect = new NativeIntVector(10);
    
       if( excludedFeederID == null ) {
          excludedFeederID = new Integer(0);
       }
       if( excludedSubBusId == null ) {
          excludedSubBusId = new Integer(0);
       }
       //Get all the used Var PointIDs in the CapControlSubBus table and the Feeder table                
       String sql = "select p.pointid from " + Point.TABLE_NAME + " p where p.pointid in " +
          "(select currentvarloadpointid from " + CapControlSubstationBus.TABLE_NAME + 
             " where substationbusid <> " + excludedSubBusId + ")" +
          "or p.pointid in " +
          "(select currentvarloadpointid from " + CapControlFeeder.TABLE_NAME + 
             " where feederid <> " + excludedFeederID + ")";
    
    	try {		
    		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
    
    		if( conn == null ) {
    			throw new IllegalStateException("Error getting database connection.");
    		} else {
    			pstmt = conn.prepareStatement(sql.toString());			
    			rset = pstmt.executeQuery();
    	
    			while( rset.next() ) {
    				vect.add( rset.getInt(1) );
                }
    		}		
    	}
    	catch( java.sql.SQLException e ) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	finally {
    		SqlUtils.close(rset, pstmt, conn );
    	}
    	return vect;
    }
    
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
    	yukonTemplate.update(query, subBusDeletedId);
    }
    
    private void handleSubStationAssignmentDelete (Integer subBusDeletedId) {
        String query =  "delete from ccsubstationsubbuslist " +
                        "where substationbusid = ? ";    
    
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(query, subBusDeletedId);
    }

    private void handleZoneDelete(Integer subBusId) {
        ZoneDao zoneDao = YukonSpringHook.getBean(ZoneDao.class);
        CcMonitorBankListDao ccMonitorBankListDao = YukonSpringHook.getBean(CcMonitorBankListDao.class);
        List<Zone> zonesBySubBusId = zoneDao.getZonesBySubBusId(subBusId);
        for (Zone zone : zonesBySubBusId) {
            ccMonitorBankListDao.removePointsByZone(zone.getId());
        }
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
    
    public String getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(String controlFlag) {
        this.controlFlag = controlFlag;
    }
    
    public boolean getControlFlagBoolean() {
        if(controlFlag.equalsIgnoreCase("Y")) {
            return true;
        }
        return false;
    }

    public void setControlFlagBoolean(boolean bool) {
        if(bool) {
            this.controlFlag = "Y";
        }else {
            this.controlFlag = "N";
        }
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
    
    public Integer getVoltReductionPointId() {
        return voltReductionPointId;
    }
    
    public void setVoltReductionPointId(Integer voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }
 
    public Integer getDisableBusPointId() {
		return disableBusPointId;
	}

	public void setDisableBusPointId(Integer disableBusPointId) {
		this.disableBusPointId = disableBusPointId;
	}
	
    public static int[] getUnassignedSubBusIDs() {
		NativeIntVector intVect = new NativeIntVector(16);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		String sql = "SELECT substationBusID FROM " + TABLE_NAME + " where " +
						 " substationBusID not in (select substationBusID from " + CCSubstationSubBusList.TABLE_NAME +
						 ") ORDER BY substationBusID";

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
}