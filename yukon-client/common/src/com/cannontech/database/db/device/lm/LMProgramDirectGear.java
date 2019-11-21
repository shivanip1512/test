package com.cannontech.database.db.device.lm;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.data.device.lm.EcobeeSetpointGear;
import com.cannontech.database.data.device.lm.ItronCycleGear;
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
	private GearControlMethod controlMethod = null;
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
    private String frontRampOption = CtiUtilities.STRING_NONE;
    private Integer frontRampTime = new Integer(0);
    private String backRampOption = CtiUtilities.STRING_NONE;
    private Integer backRampTime = new Integer(0);
    private Double kWReduction = new Double(0.0);
    private Integer stopCommandRepeat = new Integer(0);
    
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
		"RampOutPercent",
        "FrontRampOption",
        "FrontRampTime",
        "BackRampOption",
        "BackRampTime",
        "KWReduction",
        "StopCommandRepeat"
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
	@Override
    public void add() throws SQLException
	{
		if (getGearID() == null) {
            setGearID( new Integer(getNextGearID(getDbConnection())) );
        }

		Object addValues[] =
		{ 
			getDeviceID(), getGearName(), getGearNumber(), getControlMethod().getDatabaseRepresentation(),
			getMethodRate(), getMethodPeriod(), getMethodRateCount(),
			getCycleRefreshRate(), getMethodStopType(), getChangeCondition(),
			getChangeDuration(), getChangePriority(), getChangeTriggerNumber(),
			getChangeTriggerOffset(), getPercentReduction(), getGroupSelectionMethod(),
			getMethodOptionType(), getMethodOptionMax(), getGearID(), getRampInInterval(),
			getRampInPercent(), getRampOutInterval(), getRampOutPercent(),
            getFrontRampOption(), getFrontRampTime(), getBackRampOption(),
            getBackRampTime(), getKWReduction(), getStopCommandRepeat()
		};

		add(TABLE_NAME, addValues);
	}

	/**
	 * delete method comment.
	 */
	@Override
    public void delete() throws SQLException
	{
		delete(TABLE_NAME, "GearID", getGearID());
	}
	
    public final static void deleteAllDirectGearsForProgram(Integer deviceID) {
        Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        deleteAllDirectGears(deviceID, conn);
    }
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final void deleteAllDirectGears(
		Integer deviceID,
		java.sql.Connection conn)
	{
		String tGear = "DELETE FROM " + LMThermostatGear.TABLE_NAME +
							" where gearid in (select gearid from " + TABLE_NAME + 
							" where deviceID=" + deviceID + ")";

		String dGear = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

		if (conn == null) {
            throw new IllegalArgumentException("Received a (null) database connection");
        }

		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			stmt.execute( tGear );
			stmt.execute( dGear );
		}
		catch (SQLException e) {
			CTILogger.error( e.getMessage(), e );
		} finally {
			SqlUtils.close(stmt);
		}
	}
   
	/**
	 * This method was created in VisualAge.
	 * @return LMProgramDirectGear[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final Vector<LMProgramDirectGear> getAllDirectGears( Integer deviceID, java.sql.Connection conn)
	{
		Vector<LMProgramDirectGear> gearList = new Vector<>();
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
                    + ", " + SETTER_COLUMNS[22] + ", " + SETTER_COLUMNS[24] + ", " + SETTER_COLUMNS[26]
                    + ", " + SETTER_COLUMNS[27]
                    + " from " + TABLE_NAME +
				" where deviceid=? order by GearNumber";
		try
		{
			if (conn == null) {
                throw new IllegalArgumentException("Received a (null) database connection");
            }

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, deviceID.intValue());

			rset = pstmt.executeQuery();

			while (rset.next())
			{
				Integer gID = new Integer(rset.getInt(1)); //"GearID"));
				String name = new String(rset.getString(2));
				Integer gearNum = new Integer(rset.getInt(3));
				GearControlMethod method = GearControlMethod.getGearControlMethod(rset.getString(4));
				LMProgramDirectGear gear = method.createNewGear();
				
				gear.setDeviceID(deviceID);
				gear.setGearID(gID);
				gear.setDbConnection(conn);
				
				//need to make sure we get the Thermostat/Beat the Peak/Itron specific information from its separate table
				if (gear instanceof LMThermostatGear || gear instanceof BeatThePeakGear || gear instanceof LMNestGear || 
				        gear instanceof ItronCycleGear|| gear instanceof EcobeeSetpointGear) {
					gear.retrieve();
                } else {
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
                    gear.setFrontRampOption(rset.getString(23));
                    gear.setBackRampOption(rset.getString(24));
                    gear.setStopCommandRepeat(rset.getInt(26));
                    /*
                     * Other ramp fields not used
                     */
                    gear.setKWReduction(new Double(rset.getDouble(25)));
				}
				
				gearList.add(gear);
			}

		} catch (SQLException e) {
			CTILogger.error( e.getMessage(), e );
		} finally {
			SqlUtils.close(rset, pstmt);
		}

		return gearList;
	}
	
public static final Vector<Integer> getTheGearIDs(
		Integer deviceID,
		java.sql.Connection conn)
		throws SQLException
{
		String sql = "SELECT GearID FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

	Vector<Integer> someIDs = new Vector<>();
		
	if (conn == null) {
        throw new IllegalArgumentException("Received a (null) database connection");
    }

	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
   	{
       	pstmt = conn.prepareStatement(sql.toString());

       	rset = pstmt.executeQuery();

       	while( rset.next() )
        {
           	someIDs.addElement(new Integer(rset.getInt(1)));
       	}
		return someIDs;
        
   	} catch (SQLException e) {
       	e.printStackTrace();
   	} finally {
   		SqlUtils.close(rset, pstmt);
   	}
 
    throw new SQLException("Unable to retrieve the gearIDs where deviceID = " + deviceID);
}

public static final Integer getDefaultGearID(Integer programID, java.sql.Connection conn)
		throws SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "select MIN(GearID) from " + TABLE_NAME + " where deviceID = " + programID;
      
	try
	  {
		 if (conn == null) {
            throw new IllegalArgumentException("Received a (null) database connection");
        }

		 pstmt = conn.prepareStatement(sql.toString());

		 rset = pstmt.executeQuery();

		 while( rset.next() )
		 {
			return new Integer(rset.getInt(1));
		 }
	}
	catch (SQLException e) {
		e.printStackTrace();
	} finally {
		SqlUtils.close(rset, pstmt);
	}
 
	throw new SQLException("Unable to retrieve a gearid for the program with id " + programID);
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
	public GearControlMethod getControlMethod()
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
	@Override
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
	public java.lang.Integer getMethodRate()
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
    
    protected java.lang.String getFrontRampOption()
    {
        return frontRampOption;
    }
    
    public java.lang.Integer getFrontRampTime()
    {
        return frontRampTime;
    }
    
    protected java.lang.String getBackRampOption()
    {
        return backRampOption;
    }
    
    public java.lang.Integer getBackRampTime()
    {
        return backRampTime;
    }
    
    public Integer getStopCommandRepeat() {
        return stopCommandRepeat;
    }
    
	/**
	 * retrieve method comment.
	 */
	@Override
    public void retrieve() throws SQLException
	{
		Object constraintValues[] = { getGearID() };
		Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

		if (results.length == SETTER_COLUMNS.length)
		{
			setDeviceID((Integer) results[0]);
			setGearName((String) results[1]);
			setGearNumber((Integer) results[2]);
			
			String method = (String) results[3];			
			setControlMethod(GearControlMethod.getGearControlMethod(method));
			
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
            setFrontRampOption((String) results[22]);
            setBackRampOption((String) results[24]);
            setStopCommandRepeat((Integer) results[27]);
            /*
             * Front ramp time/back ramp time unused
             */
            setKWReduction((Double) results[26]);
		}
		else {
			throw new Error(
				getClass() + " - Incorrect Number of results retrieved");
		}
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
	public void setControlMethod(GearControlMethod newControlMethod)
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
	@Override
    public void setDeviceID(Integer newValue)
	{
		deviceID = newValue;
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
	public void setMethodRate(java.lang.Integer newMethodRate)
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
    
    public void setFrontRampOption(String newOption)
    {
        frontRampOption = newOption;
    }
    
    public void setBackRampOption(String newOption)
    {
        backRampOption = newOption;
    }
    
    public void setStopCommandRepeat(Integer stopCommandRepeat) {
        this.stopCommandRepeat = stopCommandRepeat;
    }
    
	/**
	 * Insert the method's description here.
	 * Creation date: (2/11/2002 3:34:44 PM)
	 * @return java.lang.String
	 */
	@Override
    public String toString()
	{
		return getGearName() + " (" + getGearNumber() + ")";
	}
	/**
	 * update method comment.
	 */
	@Override
    public void update() throws SQLException
	{
		Object setValues[] =
		{ 
			getDeviceID(), getGearName(), getGearNumber(), getControlMethod().getDatabaseRepresentation(),
			getMethodRate(), getMethodPeriod(), getMethodRateCount(),
			getCycleRefreshRate(), getMethodStopType(), getChangeCondition(),
			getChangeDuration(), getChangePriority(), getChangeTriggerNumber(),
			getChangeTriggerOffset(), getPercentReduction(), getGroupSelectionMethod(),
			getMethodOptionType(), getMethodOptionMax(), getRampInInterval(),
			getRampInPercent(), getRampOutInterval(), getRampOutPercent(),
            getFrontRampOption(), getFrontRampTime(), getBackRampOption(),
            getBackRampTime(), getKWReduction(), getStopCommandRepeat()
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
   public static final int getNextGearID( java.sql.Connection conn ) throws SQLException
   {
      java.sql.PreparedStatement pstmt = null;
      java.sql.ResultSet rset = null;

      String sql = "select MAX(GearID) + 1 from " + TABLE_NAME;
      
      try
      {
         if (conn == null) {
            throw new IllegalArgumentException("Received a (null) database connection");
        }

         pstmt = conn.prepareStatement(sql.toString());

         rset = pstmt.executeQuery();

         while( rset.next() )
         {
            return rset.getInt(1);
         }

      } catch (SQLException e) {
    	  CTILogger.error( e.getMessage(), e );
      } finally {
    	  SqlUtils.close(rset, pstmt);
      }

      throw new SQLException("Unable to retrieve the next GearID");
   }
   
public Double getKWReduction() {
    return kWReduction;
}
public void setKWReduction(Double reduction) {
    kWReduction = reduction;
}

}
