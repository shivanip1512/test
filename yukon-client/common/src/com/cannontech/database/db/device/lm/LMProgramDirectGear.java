package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */

public abstract class LMProgramDirectGear
	extends com.cannontech.database.db.DBPersistent
	implements DeviceListItem, LMProgramDirectGearDefines
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

	public static final String SETTER_COLUMNS[] =
   {
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
		"GearID" 
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
				getDeviceID(),
				getGearName(),
				getGearNumber(),
				getControlMethod(),
				getMethodRate(),
				getMethodPeriod(),
				getMethodRateCount(),
				getCycleRefreshRate(),
				getMethodStopType(),
				getChangeCondition(),
				getChangeDuration(),
				getChangePriority(),
				getChangeTriggerNumber(),
				getChangeTriggerOffset(),
				getPercentReduction(),
				getGroupSelectionMethod(),
				getMethodOptionType(),
				getMethodOptionMax(),
				getGearID()};

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
		else if (gearType.equalsIgnoreCase(CONTROL_TIME_REFRESH))
		{
			return new com.cannontech.database.data.device.lm.TimeRefreshGear();
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
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

		if (conn == null)
			throw new IllegalArgumentException("Received a (null) database connection");

		java.sql.PreparedStatement pstmt = null;

		try
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.execute();
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

	}
   
	/**
	 * This method was created in VisualAge.
	 * @return LMProgramDirectGear[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final LMProgramDirectGear[] getAllDirectGears( Integer deviceID, java.sql.Connection conn)
		throws java.sql.SQLException
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		//get all the gears that have the passed in DeviceID
		String[] tNames = { TABLE_NAME };
		String sql =
			com.cannontech.database.SqlUtils.createSqlString(
				"select ",
				SETTER_COLUMNS,
				tNames,
				" where deviceid=? order by GearNumber");
		try
		{
			if (conn == null)
				throw new IllegalArgumentException("Received a (null) database connection");

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, deviceID.intValue());

			rset = pstmt.executeQuery();

			while (rset.next())
			{
				//some ugly stuff below!
				String gName = rset.getString(1); //"GearName");
				Integer gNumber = new Integer(rset.getInt(2)); //"GearNumber"));

				LMProgramDirectGear gear =
					LMProgramDirectGear.createGearFactory(rset.getString(3));
				//"ControlMethod") );

				gear.setDeviceID(deviceID);
				gear.setGearName(gName);
				gear.setGearNumber(gNumber);

				//GearMethod is gotten in the creteGearFactory call above
				//gear.setControlMethod( rset.getString("ControlMethod") );

				gear.setMethodRate(new Integer(rset.getInt(4)));
				gear.setMethodPeriod(new Integer(rset.getInt(5)));
				gear.setMethodRateCount(new Integer(rset.getInt(6)));
				gear.setCycleRefreshRate(new Integer(rset.getInt(7)));
				gear.setMethodStopType(rset.getString(8));
				gear.setChangeCondition(rset.getString(9));
				gear.setChangeDuration(new Integer(rset.getInt(10)));

				gear.setChangePriority(new Integer(rset.getInt(11)));
				gear.setChangeTriggerNumber(new Integer(rset.getInt(12)));
				gear.setChangeTriggerOffset(new Double(rset.getDouble(13)));
				gear.setPercentReduction(new Integer(rset.getInt(14)));
				gear.setGroupSelectionMethod(rset.getString(15));
				gear.setMethodOptionType(rset.getString(16));
				gear.setMethodOptionMax(new Integer(rset.getInt(17)));
				gear.setGearID(new Integer(rset.getInt(18)));

				gear.setDbConnection(null);

				tmpList.add(gear);
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

		LMProgramDirectGear retVal[] = new LMProgramDirectGear[tmpList.size()];
		tmpList.toArray(retVal);

		return retVal;
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
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		Object constraintValues[] = { getGearID() };
		Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

		if (results.length == SETTER_COLUMNS.length)
		{
			setGearName((String) results[0]);
			setGearNumber((Integer) results[1]);
			setControlMethod((String) results[2]);
			setMethodRate((Integer) results[3]);
			setMethodPeriod((Integer) results[4]);
			setMethodRateCount((Integer) results[5]);
			setCycleRefreshRate((Integer) results[6]);
			setMethodStopType((String) results[7]);
			setChangeCondition((String) results[8]);
			setChangeDuration((Integer) results[9]);

			setChangePriority((Integer) results[10]);
			setChangeTriggerNumber((Integer) results[11]);
			setChangeTriggerOffset((Double) results[12]);
			setPercentReduction((Integer) results[13]);
			setGroupSelectionMethod((String) results[14]);
			setMethodOptionType((String) results[15]);
			setMethodOptionMax((Integer) results[16]);
			setGearID((Integer) results[17]);
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
			getDeviceID(),
         getGearName(),
			getGearNumber(),
			getControlMethod(),
			getMethodRate(),
			getMethodPeriod(),
			getMethodRateCount(),
			getCycleRefreshRate(),
			getMethodStopType(),
			getChangeCondition(),
			getChangeDuration(),
			getChangePriority(),
			getChangeTriggerNumber(),
			getChangeTriggerOffset(),
			getPercentReduction(),
			getGroupSelectionMethod(),
			getMethodOptionType(),
			getMethodOptionMax()
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
            return rset.getInt(0);
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

      throw new java.sql.SQLException("Unable to retrieve the next GearID");
   }

}
