package com.cannontech.database.db.capcontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
/**
 * Strategy of control for a SubBus or Feeder.
 *  No .data. object for this DBPersistent at this time.
 * 
 */
public class CapControlStrategy extends com.cannontech.database.db.DBPersistent  implements com.cannontech.database.db.CTIDbChange
{
	private Integer strategyID = null;
	private String strategyName = null;
	private String controlMethod = CNTRL_INDIVIDUAL_FEEDER;
	private Integer maxDailyOperation = new Integer(0);
	private Character maxOperationDisableFlag = new Character('N');
	private Integer peakStartTime = null;
	private Integer peakStopTime = null;
	private Integer controlInterval = new Integer(0);
	private Integer minResponseTime = new Integer(30);
	private Integer minConfirmPercent = new Integer(15);
	private Integer failurePercent = new Integer(15);
	private String daysOfWeek = new String("YYYYYNNN");
	private String controlUnits = CalcComponentTypes.LABEL_KVAR;
	private Integer controlDelayTime = new Integer(0);
	private Integer controlSendRetries = new Integer(0);
	
	private Double peakLag = new Double(0.0);
	private Double peakLead = new Double(0.0);
	private Double offPkLag = new Double(0.0);
	private Double offPkLead = new Double(0.0);



	public static final String CNTRL_INDIVIDUAL_FEEDER = "IndividualFeeder";
	public static final String CNTRL_SUBSTATION_BUS = "SubstationBus";
	public static final String CNTRL_BUSOPTIMIZED_FEEDER= "BusOptimizedFeeder";
	public static final String CNTRL_MANUAL_ONLY= "ManualOnly";

	public static final String SETTER_COLUMNS[] = 
	{ 
		"StrategyName", "ControlMethod", "MaxDailyOperation",
		"MaxOperationDisableFlag",
		"PeakStartTime", "PeakStopTime",
		"ControlInterval", "MinResponseTime", "MinConfirmPercent",
		"FailurePercent", "DaysOfWeek",
		"ControlUnits", "ControlDelayTime", "ControlSendRetries",
		"PeakLag", "PeakLead", "OffPkLag", "OffPkLead"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "StrategyID" };
	public static final String TABLE_NAME = "CapControlStrategy";

	/**
	 * Default constructor.
	 */
	public CapControlStrategy()
	{
		super();
	}
	
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object[] addValues = 
		{
			getStrategyID(), getStrategyName(), getControlMethod(), getMaxDailyOperation(), 
			getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
			getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
			getFailurePercent(), getDaysOfWeek(), getControlUnits(),
			getControlDelayTime(), getControlSendRetries(),
			getPeakLag(), getPeakLead(), getOffPkLag(), getOffPkLead()
		};
	
		add( TABLE_NAME, addValues );
	}
	
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getStrategyID() );	
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getControlInterval() {
		return controlInterval;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getControlMethod() {
		return controlMethod;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getControlUnits() {
		return controlUnits;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/26/00 10:27:13 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getDaysOfWeek() {
		return daysOfWeek;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getFailurePercent() {
		return failurePercent;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMaxDailyOperation() {
		return maxDailyOperation;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @return java.lang.Character
	 */
	public java.lang.Character getMaxOperationDisableFlag() {
		return maxOperationDisableFlag;
	}

	
	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public boolean isMaxOperationDisabled() {
		return CtiUtilities.isTrue(getMaxOperationDisableFlag());
	}
	
	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public void setMaxOperationDisabled( boolean val ) {
		setMaxOperationDisableFlag(
			(val ? CtiUtilities.trueChar : CtiUtilities.falseChar) );
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMinConfirmPercent() {
		return minConfirmPercent;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMinResponseTime() {
		return minResponseTime;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPeakStartTime() {
		return peakStartTime;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPeakStopTime() {
		return peakStopTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @return java.lang.Double
	 */
	public Integer getControlDelayTime() {
		return controlDelayTime;
	}
	
	public Integer getControlSendRetries() {
		return controlSendRetries;
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getStrategyID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setStrategyName( (String) results[0] );
			setControlMethod( (String) results[1] );
			setMaxDailyOperation( (Integer) results[2] );
			setMaxOperationDisableFlag( new Character(results[3].toString().charAt(0)) );		
			setPeakStartTime( (Integer)results[4] );
			setPeakStopTime( (Integer)results[5] );			
			setControlInterval( (Integer) results[6] );
			setMinResponseTime( (Integer) results[7] );
			setMinConfirmPercent( (Integer) results[8] );
			setFailurePercent( (Integer) results[9] );
			setDaysOfWeek( (String) results[10] );
			setControlUnits( (String) results[11] );
			setControlDelayTime( (Integer) results[12] );
			setControlSendRetries( (Integer) results[13] );

			setPeakLag( (Double) results[14] );
			setPeakLead( (Double) results[15] );
			setOffPkLag( (Double) results[16] );
			setOffPkLead( (Double) results[17] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setControlInterval(Integer newValue) {
		this.controlInterval = newValue;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @param newControlMethod java.lang.String
	 */
	public void setControlMethod(java.lang.String newControlMethod) {
		controlMethod = newControlMethod;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @param newControlUnits java.lang.String
	 */
	public void setControlUnits(java.lang.String newControlUnits) {
		controlUnits = newControlUnits;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/26/00 10:27:13 AM)
	 * @param newDaysOfWeek java.lang.String
	 */
	public void setDaysOfWeek(java.lang.String newDaysOfWeek) {
		daysOfWeek = newDaysOfWeek;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setFailurePercent(Integer newValue) {
		this.failurePercent = newValue;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMaxDailyOperation(Integer newValue) {
		this.maxDailyOperation = newValue;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @param newMaxOperationDisableFlag java.lang.Character
	 */
	public void setMaxOperationDisableFlag(java.lang.Character newMaxOperationDisableFlag) {
		maxOperationDisableFlag = newMaxOperationDisableFlag;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMinConfirmPercent(Integer newValue) {
		this.minConfirmPercent = newValue;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMinResponseTime(Integer newValue) {
		this.minResponseTime = newValue;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @param newPeakStartTime java.lang.Integer
	 */
	public void setPeakStartTime(java.lang.Integer newPeakStartTime) {
		peakStartTime = newPeakStartTime;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @param newPeakStopTime java.lang.Integer
	 */
	public void setPeakStopTime(java.lang.Integer newPeakStopTime) {
		peakStopTime = newPeakStopTime;
	}

	public void setControlDelayTime(Integer newValue) {
		controlDelayTime = newValue;
	}
	
	public void setControlSendRetries(Integer newTime) {
		controlSendRetries = newTime;
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[]= 
		{ 
			getStrategyName(), getControlMethod(), getMaxDailyOperation(), 
			getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
			getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
			getFailurePercent(), getDaysOfWeek(), getControlUnits(),
			getControlDelayTime(), getControlSendRetries(),
			getPeakLag(), getPeakLead(), getOffPkLag(), getOffPkLead()
		};
	
	
		Object constraintValues[] = { getStrategyID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * This method returns all CapControlStrategy currently
	 * in the database with all their attributes populated
	 *
	 */
	public static CapControlStrategy[] getAllCBCStrategies()
	{
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		Vector vect = new Vector(32);

	   //Get all the data from the database                
	   String sql = "select " + 
		   "StrategyID, StrategyName, ControlMethod, MaxDailyOperation,"+
		   "MaxOperationDisableFlag," +
		   "PeakStartTime, PeakStopTime," +
		   "ControlInterval, MinResponseTime, MinConfirmPercent," +
		   "FailurePercent, DaysOfWeek," +
		   "ControlUnits, ControlDelayTime, ControlSendRetries," +
		   "PeakLag, PeakLead, OffPkLag, OffPkLead" +
		   " from " + TABLE_NAME + " order by StrategyName";

		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			}
			else {
				pstmt = conn.prepareStatement(sql.toString());			
				rset = pstmt.executeQuery();
	
				while( rset.next() ) {
					CapControlStrategy cbcStrat = new CapControlStrategy();

					cbcStrat.setStrategyID( new Integer(rset.getInt(1)) );
					cbcStrat.setStrategyName( rset.getString(2) );
					cbcStrat.setControlMethod( rset.getString(3) );
					cbcStrat.setMaxDailyOperation( new Integer(rset.getInt(4)) );
					cbcStrat.setMaxOperationDisableFlag( new Character(rset.getString(5).charAt(0)) );		
					cbcStrat.setPeakStartTime( new Integer(rset.getInt(6)) );
					cbcStrat.setPeakStopTime( new Integer(rset.getInt(7)) );		
					cbcStrat.setControlInterval( new Integer(rset.getInt(8)) );
					cbcStrat.setMinResponseTime( new Integer(rset.getInt(9)) );
					cbcStrat.setMinConfirmPercent( new Integer(rset.getInt(10)) );
					cbcStrat.setFailurePercent( new Integer(rset.getInt(11)) );
					cbcStrat.setDaysOfWeek( rset.getString(12) );
					cbcStrat.setControlUnits( rset.getString(13) );
					cbcStrat.setControlDelayTime( new Integer(rset.getInt(14)) );
					cbcStrat.setControlSendRetries( new Integer(rset.getInt(15)) );

					cbcStrat.setPeakLag( new Double(rset.getDouble(16)) );
					cbcStrat.setPeakLead( new Double(rset.getDouble(17)) );
					cbcStrat.setOffPkLag( new Double(rset.getDouble(18)) );
					cbcStrat.setOffPkLead( new Double(rset.getDouble(19)) );

					vect.add( cbcStrat );				
				}

			}		
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 ) {
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}


		CapControlStrategy[] strats = new CapControlStrategy[vect.size()];
		return (CapControlStrategy[])vect.toArray( strats );
	}

	/**
	 * Generates a DBChange msg.
	 * 
	 */
	public DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		DBChangeMsg[] dbChange = new DBChangeMsg[1];

		//add the basic change method
		dbChange[0] = new DBChangeMsg(
						getStrategyID().intValue(),
						DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
						DBChangeMsg.CAT_CBC_STRATEGY,
						typeOfChange );

		return dbChange;
	}

	/**
	 * @param string
	 */
	public String toString() {
		return getStrategyName();
	}


	/**
	 * @return
	 */
	public Integer getStrategyID() {
		return strategyID;
	}

	/**
	 * @return
	 */
	public String getStrategyName() {
		return strategyName;
	}

	/**
	 * @param integer
	 */
	public void setStrategyID(Integer integer) {
		strategyID = integer;
	}

	/**
	 * @param string
	 */
	public void setStrategyName(String string) {
		strategyName = string;
	}

	/**
	 * @return
	 */
	public Double getOffPkLag() {
		return offPkLag;
	}

	/**
	 * @return
	 */
	public Double getOffPkLead() {
		return offPkLead;
	}

	/**
	 * @return
	 */
	public Double getPeakLag() {
		return peakLag;
	}

	/**
	 * @return
	 */
	public Double getPeakLead() {
		return peakLead;
	}

	/**
	 * @param double1
	 */
	public void setOffPkLag(Double double1) {
		offPkLag = double1;
	}

	/**
	 * @param double1
	 */
	public void setOffPkLead(Double double1) {
		offPkLead = double1;
	}

	/**
	 * @param double1
	 */
	public void setPeakLag(Double double1) {
		peakLag = double1;
	}

	/**
	 * @param double1
	 */
	public void setPeakLead(Double double1) {
		peakLead = double1;
	}

}