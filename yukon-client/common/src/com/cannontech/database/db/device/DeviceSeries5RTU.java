package com.cannontech.database.db.device;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author rneuharth
 * Aug 6, 2002 at 4:37:19 PM
 * 
 * 
 */
public class DeviceSeries5RTU extends DBPersistent
{
   private Integer deviceID = null;
   private Integer tickTime = new Integer(0);
   private Integer transmitOffset = new Integer(0);
	private String saveHistory = "N";
   private Integer powerValueHighLimit = new Integer(0);
	private Integer powerValueLowLimit = new Integer(0);
	private Double powerValueMultiplier = new Double(0);
	private Double powerValueOffset = new Double(0);
	private Integer startCode = new Integer(0);
	private Integer stopCode = new Integer(0);
	
		
   public static final String SETTER_COLUMNS[] = 
   { 
      "TickTime", "TransmitOffset", "SaveHistory", "PowerValueHighLimit",
      "PowerValueLowLimit", "PowerValueMultiplier",
		"PowerValueOffset", "StartCode", "StopCode"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
   
   public static final String TABLE_NAME = "DeviceSeries5RTU";
   

	/**
	 * Constructor for DeviceSeries5RTU.
	 */
	public DeviceSeries5RTU()
	{
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
   {
      Object[] addValues =
      { 
      	getDeviceID(), getTickTime(), getTransmitOffset(), getSaveHistory(), 
         getPowerValueHighLimit(), getPowerValueLowLimit(),
         getPowerValueMultiplier(), getPowerValueOffset(),
         getStartCode(), getStopCode()
		};
   
      add( TABLE_NAME, addValues );
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException
   {   
      delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException
   {
      Object constraintValues[] = { getDeviceID() };
   
      Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
   
      if( results.length == SETTER_COLUMNS.length )
      {
         setTickTime( (Integer) results[0] );
         setTransmitOffset( (Integer) results[1] );
			setSaveHistory( (String) results[2] );			
         setPowerValueHighLimit( (Integer) results[3] );
			setPowerValueLowLimit( (Integer) results[4] );
			setPowerValueMultiplier( (Double) results[5] );
			setPowerValueOffset( (Double) results[6] );
			setStartCode( (Integer) results[7] );
			setStopCode( (Integer) results[8] );
      }
      else
         throw new Error(getClass() + " - Incorrect Number of results retrieved");
   
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException
   {
		Object[] setValues =
		{ 
			getTickTime(), getTransmitOffset(), getSaveHistory(),
			getPowerValueHighLimit(), getPowerValueLowLimit(),
			getPowerValueMultiplier(), getPowerValueOffset(),
			getStartCode(), getStopCode()
		};
      
      Object constraintValues[] = { getDeviceID() };
   
      update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
   }

	/**
	 * Returns the deviceID.
	 * @return Integer
	 */
	public Integer getDeviceID()
	{
		return deviceID;
	}

	/**
	 * Sets the deviceID.
	 * @param deviceID The deviceID to set
	 */
	public void setDeviceID(Integer deviceID)
	{
		this.deviceID = deviceID;
	}


	/**
	 * @return
	 */
	public Integer getPowerValueHighLimit()
	{
		return powerValueHighLimit;
	}

	/**
	 * @return
	 */
	public Integer getPowerValueLowLimit()
	{
		return powerValueLowLimit;
	}

	/**
	 * @return
	 */
	public Double getPowerValueMultiplier()
	{
		return powerValueMultiplier;
	}

	/**
	 * @return
	 */
	public Double getPowerValueOffset()
	{
		return powerValueOffset;
	}

	/**
	 * @return
	 */
	public Integer getStartCode()
	{
		return startCode;
	}

	/**
	 * @return
	 */
	public Integer getStopCode()
	{
		return stopCode;
	}

	/**
	 * @return
	 */
	public Integer getTickTime()
	{
		return tickTime;
	}

	/**
	 * @return
	 */
	public Integer getTransmitOffset()
	{
		return transmitOffset;
	}

	/**
	 * @param integer
	 */
	public void setPowerValueHighLimit(Integer integer)
	{
		powerValueHighLimit = integer;
	}

	/**
	 * @param integer
	 */
	public void setPowerValueLowLimit(Integer integer)
	{
		powerValueLowLimit = integer;
	}

	/**
	 * @param double1
	 */
	public void setPowerValueMultiplier(Double double1)
	{
		powerValueMultiplier = double1;
	}

	/**
	 * @param double1
	 */
	public void setPowerValueOffset(Double double1)
	{
		powerValueOffset = double1;
	}

	/**
	 * @param integer
	 */
	public void setStartCode(Integer integer)
	{
		startCode = integer;
	}

	/**
	 * @param integer
	 */
	public void setStopCode(Integer integer)
	{
		stopCode = integer;
	}

	/**
	 * @param integer
	 */
	public void setTickTime(Integer integer)
	{
		tickTime = integer;
	}

	/**
	 * @param integer
	 */
	public void setTransmitOffset(Integer integer)
	{
		transmitOffset = integer;
	}

	/**
	 * @return
	 */
	public String getSaveHistory()
	{
		return saveHistory;
	}

	/**
	 * @param string
	 */
	public void setSaveHistory(String string)
	{
		saveHistory = string;
	}

}
