package com.cannontech.database.db.device.lm;

import java.sql.Statement;

import com.cannontech.database.data.device.lm.ThermostatPreOperateGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.db.NestedDBPersistent;

/**
 * This type was created in VisualAge.
 *  new 1.8
 */

public abstract class LMProgramDirectGear
	extends NestedDBPersistent
	implements DeviceListItem, IlmDefines
{
	private Integer deviceID = null;
	private String gearName = null;
	private Integer gearNumber = new Integer(1);
	private String controlMethod = null;
	private Integer methodRate = new Integer(0);
	private Integer methodPeriod = new Integer(0);
	private Integer methodRateCount = new Integer(0);
	private Integer cycleRefreshRate = new Integer(0);
	private String methodStopType = STOP_RESTORE;
	private String changeCondition = CHANGE_NONE;
	private Integer changeDuration = new Integer(0);
	private Integer changePriority = new Integer(0);
	private Integer changeTriggerNumber = new Integer(0);
	private Double changeTriggerOffset = new Double(0.0);
	private Integer percentReduction = new Integer(100);
	private String groupSelectionMethod = SELECTION_LAST_CONTROLLED;
	private String methodOptionType = OPTION_FIXED_COUNT;
	private Integer methodOptionMax = new Integer(0);
	private Integer gearID = null;
	private Integer rampInInterval = new Integer(0);
	private Integer rampInPercent = new Integer(0);
	private Integer rampOutInterval = new Integer(0);
	private Integer rampOutPercent = new Integer(0);

	public static final String SETTER_COLUMNS[] =
   {
   		"DeviceID",
		"GearName",
		"gearNumber",
		"ControlMethod",
		"MethodRate",
		"MethodPeriod",
		"MethodRateCount",
		"CycleRefreshRate",
		"MethodStopType",
		"ChangeCondition",
		"ChangeDuration",
		"ChangePriority",
		"ChangeTriggerNumber",
		"ChangeTriggerOffset",
		"PercentReduction",
		"GroupSelectionMethod",
		"MethodOptionType",
		"MethodOptionMax",
		"RampInInterval",
		"RampInPercent",
		"RampOutInterval",
		"RampOutPercent"
   };

	public static final String CONSTRAINT_COLUMNS[] = { "GearID" };

	public static final String TABLE_NAME = "LMProgramDirectGear";

	/**
	 * LMGroupVersacomSerial constructor comment.
	 */
	public LMProgramDirectGear()
	{
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException
	{
		if (getGearID() == null)
			setGearID( new Integer(getNextGearID(getDbConnection())) );

		Object addValues[] =
		{ 
			getDeviceID(), getGearName(), getGearNumber(), getControlMethod(),
			getMethodRate(), getMethodPeriod(), getMethodRateCount(),
			getCycleRefreshRate(), getMethodStopType(), getChangeCondition(),
			getChangeDuration(), getChangePriority(), getChangeTriggerNumber(),
			getChangeTriggerOffset(), getPercentReduction(), getGroupSelectionMethod(),
			getMethodOptionType(), getMethodOptionMax(), getGearID(), getRampInInterval(),
			getRampInPercent(), getRampOutInterval(), getRampOutPercent()
		};

		add(TABLE_NAME, addValues);
	}
	/**
	 * This method was created in VisualAge.
	 */
	public static final LMProgramDirectGear createGearFactory(String gearType)
	{
		if (gearType == null)
			return null;

		if (gearType.equalsIgnoreCase(CONTROL_LATCHING))
		{
			return new com.cannontech.database.data.device.lm.LatchingGear();
		}
		else if (gearType.equalsIgnoreCase(CONTROL_MASTER_CYCLE))
		{
			return new com.cannontech.database.data.device.lm.MasterCycleGear();
		}
		else if (gearType.equalsIgnoreCase(CONTROL_ROTATION))
		{
			return new com.cannontech.database.data.device.lm.RotationGear();
		}
		else if (gearType.equalsIgnoreCase(CONTROL_SMART_CYCLE))
		{
			return new com.cannontech.database.data.device.lm.SmartCycleGear();
		}
		else if (gearType.equalsIgnoreCase(CONTROL_TRUE_CYCLE))
		{
			return new com.cannontech.database.data.device.lm.TrueCycleGear();
		}
		else if (gearType.equalsIgnoreCase(CONTROL_TIME_REFRESH))
		{
			return new com.cannontech.database.data.device.lm.TimeRefreshGear();
		}
		else if (gearType.equalsIgnoreCase(THERMOSTAT_PRE_OPERATE))
		{
			return new ThermostatPreOperateGear();
		}
		else if (gearType.equalsIgnoreCase(THERMOSTAT_SETBACK))
		{
			return new ThermostatSetbackGear();
		}
		else if (gearType.equalsIgnoreCase(NO_CONTROL))
		{
			return new NoControlGear();
		}
		else
			throw new IllegalArgumentException(
				"Unable to create DirectGear for type : " + gearType);

	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException
	{
		delete(TABLE_NAME, "GearID", getGearID());
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final void deleteAllDirectGears(
		Integer deviceID,
		java.sql.Connection conn)
		throws java.sql.SQLException
	{
		String tGear = "DELETE FROM " + LMThermostatGear.TABLE_NAME +
							" where gearid in (select gearid from " + TABLE_NAME + 
							" where deviceID=" + deviceID + ")";

		String dGear = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

		if (conn == null)
			throw new IllegalArgumentException("Received a (null) database connection");

		Statement stmt = null;

		try
		{
			stmt = conn.createStatement();
			stmt.execute( tGear );
			stmt.execute( dGear );
		}
		catch (java.sql.SQLException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (java.sql.SQLException e2)
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
			}
		}

	}
   
	/**
	 * This method was created in VisualAge.
	 * @return LMProgramDirectGear[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final java.util.Vector getAllDirectGears( Integer deviceID, java.sql.Connection conn)
		throws java.sql.SQLException
	{
		java.util.Vector gearList = new java.util.Vector();
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		//get all the gears that have the passed DeviceID
		String sql = "select " + CONSTRAINT_COLUMNS[0]
					+ ", " + SETTER_COLUMNS[1] + ", " + SETTER_COLUMNS[2] + ", " + SETTER_COLUMNS[3] 
					+ ", " + SETTER_COLUMNS[4] + ", " + SETTER_COLUMNS[5] + ", " + SETTER_COLUMNS[6]
					+ ", " + SETTER_COLUMNS[7] + ", " + SETTER_COLUMNS[8] + ", " + SETTER_COLUMNS[9]
					+ ", " + SETTER_COLUMNS[10] + ", " + SETTER_COLUMNS[11] + ", " + SETTER_COLUMNS[12]
					+ ", " + SETTER_COLUMNS[13] + ", " + SETTER_COLUMNS[14] + ", " + SETTER_COLUMNS[15]
					+ ", " + SETTER_COLUMNS[16] + ", " + SETTER_COLUMNS[17] + ", " + SETTER_COLUMNS[18]
					+ ", " + SETTER_COLUMNS[19] + ", " + SETTER_COLUMNS[20] + ", " + SETTER_COLUMNS[21]
					+ " from " + TABLE_NAME +
				" where deviceid=? order by GearNumber";
		try
		{
			if (conn == null)
				throw new IllegalArgumentException("Received a (null) database connection");

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, deviceID.intValue());

			rset = pstmt.executeQuery();

			while (rset.next())
			{

				Integer gID = new Integer(rset.getInt(1)); //"GearID"));
				String name = new String(rset.getString(2));
				Integer gearNum = new Integer(rset.getInt(3));
				LMProgramDirectGear gear =
					LMProgramDirectGear.createGearFactory(rset.getString(4));
				gear.setDeviceID(deviceID);
				gear.setGearID(gID);
				gear.setDbConnection(conn);
				gear.setGearName(name);
				gear.setGearNumber(gearNum);
				
				gear.setMethodRate(new Integer(rset.getInt(5)));
				gear.setMethodPeriod(new Integer(rset.getInt(6)));
				gear.setMethodRateCount(new Integer(rset.getInt(7)));
				gear.setCycleRefreshRate(new Integer(rset.getInt(8)));
				gear.setMethodStopType(rset.getString(9));
				gear.setChangeCondition(rset.getString(10));
				gear.setChangeDuration(new Integer(rset.getInt(11)));
				gear.setChangePriority(new Integer(rset.getInt(12)));
				gear.setChangeTriggerNumber(new Integer(rset.getInt(13)));
				gear.setChangeTriggerOffset(new Double(rset.getFloat(14)));
				gear.setPercentReduction(new Integer(rset.getInt(15)));
				gear.setGroupSelectionMethod(rset.getString(16));
				gear.setMethodOptionType(rset.getString(17));
				gear.setMethodOptionMax(new Integer(rset.getInt(18)));
				gear.setRampInInterval(new Integer(rset.getInt(19)));
				gear.setRampInPercent(new Integer(rset.getInt(20)));
				gear.setRampOutInterval(new Integer(rset.getInt(21)));
				gear.setRampOutPercent(new Integer(rset.getInt(22)));
				
				gearList.add(gear);
			}

		}
		catch (java.sql.SQLException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (java.sql.SQLException e2)
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
			}
		}

		return gearList;
	}
	
public static final java.util.Vector getTheGearIDs(
		Integer deviceID,
		java.sql.Connection conn)
		throws java.sql.SQLException
{
		String sql = "SELECT GearID FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

	java.util.Vector someIDs = new java.util.Vector();
		
	if (conn == null)
		throw new IllegalArgumentException("Received a (null) database connection");

	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
   	{
       	if (conn == null)
           	throw new IllegalArgumentException("Received a (null) database connection");

       	pstmt = conn.prepareStatement(sql.toString());

       	rset = pstmt.executeQuery();

       	while( rset.next() )
        {
           	someIDs.addElement(new Integer(rset.getInt(1)));
       	}
		return someIDs;
        
   	}
   	catch (java.sql.SQLException e)
   	{
       	e.printStackTrace();
   	}
   	finally
   	{
       	try
       	{
          	if (pstmt != null)
           	pstmt.close();
       	}
      	catch (java.sql.SQLException e2)
       	{
       		e2.printStackTrace(); //something is up
       	}
   	}
 
    throw new java.sql.SQLException("Unable to retrieve the gearIDs where deviceID = " + deviceID);
}

public static final Integer getDefaultGearID(Integer programID, java.sql.Connection conn)
		throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "select MIN(GearID) from " + TABLE_NAME + " where deviceID = " + programID;
      
	try
	  {
		 if (conn == null)
			throw new IllegalArgumentException("Received a (null) database connection");

		 pstmt = conn.prepareStatement(sql.toString());

		 rset = pstmt.executeQuery();

		 while( rset.next() )
		 {
			return new Integer(rset.getInt(1));
		 }
		
        
	}
	catch (java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if (pstmt != null)
			pstmt.close();
		}
		catch (java.sql.SQLException e2)
		{
			e2.printStackTrace(); //something is up
		}
	}
 
	throw new java.sql.SQLException("Unable to retrieve a gearid for the program with id " + programID);
}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getChangeCondition()
	{
		return changeCondition;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getChangeDuration()
	{
		return changeDuration;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getChangePriority()
	{
		return changePriority;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getChangeTriggerNumber()
	{
		return changeTriggerNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Float
	 */
	public java.lang.Double getChangeTriggerOffset()
	{
		return changeTriggerOffset;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getControlMethod()
	{
		return controlMethod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	protected java.lang.Integer getCycleRefreshRate()
	{
		return cycleRefreshRate;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getDeviceID()
	{
		return deviceID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getGearName()
	{
		return gearName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getGearNumber()
	{
		return gearNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/11/2002 2:39:03 PM)
	 * @return java.lang.String
	 */
	protected java.lang.String getGroupSelectionMethod()
	{
		return groupSelectionMethod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2002 10:49:05 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getMethodOptionMax()
	{
		return methodOptionMax;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2002 10:49:05 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getMethodOptionType()
	{
		return methodOptionType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	protected java.lang.Integer getMethodPeriod()
	{
		return methodPeriod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	protected java.lang.Integer getMethodRate()
	{
		return methodRate;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	protected java.lang.Integer getMethodRateCount()
	{
		return methodRateCount;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getMethodStopType()
	{
		return methodStopType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPercentReduction()
	{
		return percentReduction;
	}
	
	public java.lang.Integer getRampInInterval()
	{
		return rampInInterval;
	}
	
	public java.lang.Integer getRampInPercent()
	{
		return rampInPercent;
	}
	
	public java.lang.Integer getRampOutInterval()
	{
		return rampOutInterval;
	}
	
	public java.lang.Integer getRampOutPercent()
	{
		return rampOutPercent;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		Object constraintValues[] = { getGearID() };
		Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

		if (results.length == SETTER_COLUMNS.length)
		{
			setDeviceID((Integer) results[0]);
			setGearName((String) results[1]);
			setGearNumber((Integer) results[2]);
			setControlMethod((String) results[3]);
			setMethodRate((Integer) results[4]);
			setMethodPeriod((Integer) results[5]);
			setMethodRateCount((Integer) results[6]);
			setCycleRefreshRate((Integer) results[7]);
			setMethodStopType((String) results[8]);
			setChangeCondition((String) results[9]);
			setChangeDuration((Integer) results[10]);

			setChangePriority((Integer) results[11]);
			setChangeTriggerNumber((Integer) results[12]);
			setChangeTriggerOffset((Double) results[13]);
			setPercentReduction((Integer) results[14]);
			setGroupSelectionMethod((String) results[15]);
			setMethodOptionType((String) results[16]);
			setMethodOptionMax((Integer) results[17]);
			setRampInInterval((Integer) results[18]);
			setRampInPercent((Integer) results[19]);
			setRampOutInterval((Integer) results[20]);
			setRampOutPercent((Integer) results[21]);
		}
		else
			throw new Error(
				getClass() + " - Incorrect Number of results retrieved");

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newChangeCondition java.lang.String
	 */
	public void setChangeCondition(java.lang.String newChangeCondition)
	{
		changeCondition = newChangeCondition;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newChangeDuration java.lang.Integer
	 */
	public void setChangeDuration(java.lang.Integer newChangeDuration)
	{
		changeDuration = newChangeDuration;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newChangePriority java.lang.Integer
	 */
	public void setChangePriority(java.lang.Integer newChangePriority)
	{
		changePriority = newChangePriority;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newChangeTriggerNumber java.lang.Integer
	 */
	public void setChangeTriggerNumber(java.lang.Integer newChangeTriggerNumber)
	{
		changeTriggerNumber = newChangeTriggerNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newChangeTriggerOffset java.lang.Double
	 */
	public void setChangeTriggerOffset(java.lang.Double newChangeTriggerOffset)
	{
		changeTriggerOffset = newChangeTriggerOffset;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newControlMethod java.lang.String
	 */
	public void setControlMethod(java.lang.String newControlMethod)
	{
		controlMethod = newControlMethod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newCycleRefreshRate java.lang.Integer
	 */
	protected void setCycleRefreshRate(java.lang.Integer newCycleRefreshRate)
	{
		cycleRefreshRate = newCycleRefreshRate;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setDeviceID(Integer newValue)
	{
		this.deviceID = newValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newGearName java.lang.String
	 */
	public void setGearName(java.lang.String newGearName)
	{
		gearName = newGearName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newGearNumber java.lang.Integer
	 */
	public void setGearNumber(java.lang.Integer newGearNumber)
	{
		gearNumber = newGearNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/11/2002 2:39:03 PM)
	 * @param newGroupSelectionMethod java.lang.String
	 */
	protected void setGroupSelectionMethod(
		java.lang.String newGroupSelectionMethod)
	{
		groupSelectionMethod = newGroupSelectionMethod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2002 10:49:05 AM)
	 * @param newMethodOptionMax java.lang.Integer
	 */
	public void setMethodOptionMax(java.lang.Integer newMethodOptionMax)
	{
		methodOptionMax = newMethodOptionMax;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2002 10:49:05 AM)
	 * @param newMethodOptionType java.lang.String
	 */
	public void setMethodOptionType(java.lang.String newMethodOptionType)
	{
		methodOptionType = newMethodOptionType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newMethodPeriod java.lang.Integer
	 */
	protected void setMethodPeriod(java.lang.Integer newMethodPeriod)
	{
		methodPeriod = newMethodPeriod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newMethodRate java.lang.Integer
	 */
	protected void setMethodRate(java.lang.Integer newMethodRate)
	{
		methodRate = newMethodRate;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newMethodRateCount java.lang.Integer
	 */
	protected void setMethodRateCount(java.lang.Integer newMethodRateCount)
	{
		methodRateCount = newMethodRateCount;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newMethodStopType java.lang.String
	 */
	public void setMethodStopType(java.lang.String newMethodStopType)
	{
		methodStopType = newMethodStopType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/16/2001 5:20:28 PM)
	 * @param newPercentReduction java.lang.Integer
	 */
	public void setPercentReduction(java.lang.Integer newPercentReduction)
	{
		percentReduction = newPercentReduction;
	}
	
	public void setRampInInterval(java.lang.Integer newInterval)
	{
		rampInInterval = newInterval;
	}
	
	public void setRampInPercent(java.lang.Integer newPercent)
	{
		rampInPercent = newPercent;
	}
	
	public void setRampOutInterval(java.lang.Integer newInterval)
	{
		rampOutInterval = newInterval;
	}
	
	public void setRampOutPercent(java.lang.Integer newPercent)
	{
		rampOutPercent = newPercent;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/11/2002 3:34:44 PM)
	 * @return java.lang.String
	 */
	public String toString()
	{
		return getGearName() + " (" + getGearNumber() + ")";
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException
	{
		Object setValues[] =
		{ 
			getDeviceID(), getGearName(), getGearNumber(), getControlMethod(),
			getMethodRate(), getMethodPeriod(), getMethodRateCount(),
			getCycleRefreshRate(), getMethodStopType(), getChangeCondition(),
			getChangeDuration(), getChangePriority(), getChangeTriggerNumber(),
			getChangeTriggerOffset(), getPercentReduction(), getGroupSelectionMethod(),
			getMethodOptionType(), getMethodOptionMax(), getRampInInterval(),
			getRampInPercent(), getRampOutInterval(), getRampOutPercent()
		};

		Object constraintValues[] = { getGearID() };

		update(
			TABLE_NAME,
			SETTER_COLUMNS,
			setValues,
			CONSTRAINT_COLUMNS,
			constraintValues);
	}
	/**
	 * Returns the gearID.
	 * @return Integer
	 */
	public Integer getGearID()
	{
		return gearID;
	}

	/**
	 * Sets the gearID.
	 * @param gearID The gearID to set
	 */
	public void setGearID(Integer gearID)
	{
		this.gearID = gearID;
	}



   /**
    * This method was created in VisualAge.
    * @return LMProgramDirectGear[]
    * @param stateGroup java.lang.Integer
    */
   public static final int getNextGearID( java.sql.Connection conn ) throws java.sql.SQLException
   {
      java.sql.PreparedStatement pstmt = null;
      java.sql.ResultSet rset = null;

      String sql = "select MAX(GearID) + 1 from " + TABLE_NAME;
      
      try
      {
         if (conn == null)
            throw new IllegalArgumentException("Received a (null) database connection");

         pstmt = conn.prepareStatement(sql.toString());

         rset = pstmt.executeQuery();

         while( rset.next() )
         {
            return rset.getInt(1);
         }

      }
      catch (java.sql.SQLException e)
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      finally
      {
         try
         {
            if (pstmt != null)
               pstmt.close();
         }
         catch (java.sql.SQLException e2)
         {
            com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
         }
      }

      throw new java.sql.SQLException("Unable to retrieve the next GearID");
   }

}
