package com.cannontech.database.db.device;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author rneuharth
 * Aug 6, 2002 at 4:37:19 PM
 * 
 * A undefined generated comment
 */
public class DeviceAddress extends DBPersistent
{
   private Integer deviceID = null;
   private Integer masterAddress = new Integer(1);
   private Integer slaveAddress = new Integer(0);
   private Integer postCommWait = new Integer(0);

   public static final String SETTER_COLUMNS[] = 
   { 
      "MasterAddress", "SlaveAddress", "PostCommWait"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
   
   public static final String TABLE_NAME = "DeviceAddress";
   

	/**
	 * Constructor for DeviceAddress.
	 */
	public DeviceAddress()
	{
		super();
	}


   public DeviceAddress( Integer deviceID_, Integer masterAddress_, 
                      Integer slaveAddress_, Integer postCommWait_ )
   {
      super();
      
      setDeviceID( deviceID_ );
      setMasterAddress( masterAddress_ );
      setSlaveAddress( slaveAddress_ );
      setPostCommWait( postCommWait_ );
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
   {
      Object[] addValues = { getDeviceID(), getMasterAddress(), 
            getSlaveAddress(), getPostCommWait() };
   
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
         setMasterAddress( (Integer) results[0] );
         setSlaveAddress( (Integer) results[1] );
         setPostCommWait( (Integer) results[2] );
      }
      else
         throw new Error(getClass() + " - Incorrect Number of results retrieved");
   
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException
   {
      Object setValues[]= { getMasterAddress(), getSlaveAddress(), getPostCommWait() };
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
	 * Returns the masterAddress.
	 * @return Integer
	 */
	public Integer getMasterAddress()
	{
		return masterAddress;
	}

	/**
	 * Returns the postCommWait.
	 * @return Integer
	 */
	public Integer getPostCommWait()
	{
		return postCommWait;
	}

	/**
	 * Returns the slaveAddress.
	 * @return Integer
	 */
	public Integer getSlaveAddress()
	{
		return slaveAddress;
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
	 * Sets the masterAddress.
	 * @param masterAddress The masterAddress to set
	 */
	public void setMasterAddress(Integer masterAddress)
	{
		this.masterAddress = masterAddress;
	}

	/**
	 * Sets the postCommWait.
	 * @param postCommWait The postCommWait to set
	 */
	public void setPostCommWait(Integer postCommWait)
	{
		this.postCommWait = postCommWait;
	}

	/**
	 * Sets the slaveAddress.
	 * @param slaveAddress The slaveAddress to set
	 */
	public void setSlaveAddress(Integer slaveAddress)
	{
		this.slaveAddress = slaveAddress;
	}

}
