package com.cannontech.database.db.device;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroup extends com.cannontech.database.db.DBPersistent 
{
    public static final String DISCONNECTED_GROUP_PREFIX = "@_DISC_";
    public static final String USAGE_MONITORING_GROUP_PREFIX = "@_UM_";
    public static final String INVENTORY_GROUP_PREFIX = "@_INV_";

    public static final String REMOVED_METER_NUMBER_SUFFIX = "_REM";
    
	//group types (used for billing mainly)
	public static final int COLLECTION_GROUP = 0;
	public static final int TEST_COLLECTION_GROUP = 1;
	public static final int BILLING_GROUP = 2;
	
	public static final String COLLECTIONGROUP_DISPLAY_STRING = "Collection Group";
	public static final String ALTGROUP_DISPLAY_STRING = "Alternate Group";
	public static final String BILLINGGROUP_DISPLAY_STRING = "Billing Group";
	// The index values are relied upon greatly.  They CANNOT be changed without being in sync with the above id's.
	public static final String[] validBillGroupTypeDisplayStrings = 
		{COLLECTIONGROUP_DISPLAY_STRING, ALTGROUP_DISPLAY_STRING, BILLINGGROUP_DISPLAY_STRING};
	public static final String[] validBillGroupTypeStrings = 
		{"COLLECTIONGROUP", "TESTCOLLECTIONGROUP", "BILLINGGROUP"};
	public static final int[] validBillGroupTypeIDs = 
		{DeviceMeterGroup.COLLECTION_GROUP, 
		 DeviceMeterGroup.TEST_COLLECTION_GROUP,
		 DeviceMeterGroup.BILLING_GROUP};


	private Integer deviceID = null;
	private String collectionGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String testCollectionGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String meterNumber = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
	private String billingGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CollectionGroup", "TestCollectionGroup", "MeterNumber", "BillingGroup"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	public static final String TABLE_NAME = "DeviceMeterGroup";

	/**
	 * DeviceMeterGroup constructor comment.
	 */
	public DeviceMeterGroup() 
	{
		super();
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = { getDeviceID(), getCollectionGroup(), 
			getTestCollectionGroup(), getMeterNumber(), getBillingGroup() };
	
		add( TABLE_NAME, addValues );
	}
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException {
	
		Object values [] = {getDeviceID()};
		
		delete( DeviceMeterGroup.TABLE_NAME, CONSTRAINT_COLUMNS, values);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getBillingGroup() {
		return billingGroup;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getCollectionGroup() {
		return collectionGroup;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer[]
	 */
	public final static String[] getDeviceBillingGroups() throws java.sql.SQLException
	{
	   String retVal[] = null; 
	   
	   com.cannontech.database.SqlStatement stmt =
	      new com.cannontech.database.SqlStatement(
	         "SELECT DISTINCT BillingGroup FROM " + TABLE_NAME + " ORDER BY BillingGroup", 
	         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	                                     
	   try
	   {                                
	      stmt.execute();
	
	      retVal = new String[stmt.getRowCount()];
	   
	      for( int i = 0; i < stmt.getRowCount(); i++ )
	         retVal[i] = new String( ((String)stmt.getRow(i)[0]) );   
	   }
	   catch( Exception e )
	   {
	      com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	   }  
	
	   return retVal;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer[]
	 */
	public final static String[] getDeviceCollectionGroups() throws java.sql.SQLException
	{
		String[] retVal = null;	
	 	
	 	com.cannontech.database.SqlStatement stmt =
	 		new com.cannontech.database.SqlStatement(
		 		"SELECT DISTINCT CollectionGroup FROM " + TABLE_NAME + " ORDER BY COLLECTIONGROUP", 
	         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	 												 
	 	try
	 	{											
			stmt.execute();
	
			retVal = new String[stmt.getRowCount()];
			for( int i = 0; i < stmt.getRowCount(); i++ )
				retVal[i] = new String( ((String)stmt.getRow(i)[0]) );	
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	
	 	return retVal;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getDeviceID() {
		return deviceID;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer[]
	 */
	public final static Integer[] getDeviceIDs_CollectionGroups(String databaseAlias, String cycleGroup) throws java.sql.SQLException
	{
		Integer retVal[] = null;	
	
		String sqlString = "SELECT DISTINCT DEVICEID FROM " + TABLE_NAME + 
			" WHERE CollectionGroup = \'" + cycleGroup + "\'";
				
	 	com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(sqlString, databaseAlias );
			
		try
	 	{											
			stmt.execute();
	
			retVal = new Integer[stmt.getRowCount()];
	
			for( int i = 0; i < stmt.getRowCount(); i++ )
				retVal[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0] ).intValue());
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	
	 	return retVal;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer[]
	 */
	public final static Integer[] getDeviceIDs_TestCollectionGroups(String databaseAlias, String areaCodeGroup) throws java.sql.SQLException
	{
		Integer retVal[] = null;	
	
		String sqlString = "SELECT DISTINCT DEVICEID FROM " + TABLE_NAME + 
			" WHERE TestCollectionGroup = \'" + areaCodeGroup + "\'";
	
	 	com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(sqlString, databaseAlias );
			
		try
	 	{											
			stmt.execute();
	
			retVal = new Integer[stmt.getRowCount()];
	
			for( int i = 0; i < stmt.getRowCount(); i++ )
				retVal[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0] ).intValue());
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	
	 	return retVal;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer[]
	 */
	public final static String[] getDeviceTestCollectionGroups() throws java.sql.SQLException
	{
		String retVal[] = null;	
	 	
	 	com.cannontech.database.SqlStatement stmt =
	 		new com.cannontech.database.SqlStatement(
		 		"SELECT DISTINCT TestCollectionGroup FROM " + TABLE_NAME + " ORDER BY TESTCOLLECTIONGROUP", 
	         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	 												 
	 	try
	 	{											
			stmt.execute();
	
			retVal = new String[stmt.getRowCount()];
		
			for( int i = 0; i < stmt.getRowCount(); i++ )
				retVal[i] = new String( ((String)stmt.getRow(i)[0]) );	
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	
	 	return retVal;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public String getMeterNumber() {
		return meterNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getTestCollectionGroup() {
		return testCollectionGroup;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
	
		Object constraintValues[] = { getDeviceID() };
	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setCollectionGroup( (String) results[0] );
			setTestCollectionGroup( (String) results[1] );
			setMeterNumber( (String) results[2] );
			setBillingGroup( (String) results[3] );
		}
	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @param newBillingGroup java.lang.String
	 */
	public void setBillingGroup(java.lang.String newBillingGroup) {
		billingGroup = newBillingGroup;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @param newCollectionGroup java.lang.String
	 */
	public void setCollectionGroup(java.lang.String newCollectionGroup) {
		collectionGroup = newCollectionGroup;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setDeviceID(Integer newValue) {
		this.deviceID = newValue;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMeterNumber(String newValue) {
		this.meterNumber = newValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2001 1:00:46 PM)
	 * @param newTestCollectionGroup java.lang.String
	 */
	public void setTestCollectionGroup(java.lang.String newTestCollectionGroup) {
		testCollectionGroup = newTestCollectionGroup;
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] = { getCollectionGroup(), 
			getTestCollectionGroup(), getMeterNumber(), getBillingGroup() };
	
		Object constraintValues[] = { getDeviceID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	/**
	 * Returns the validBillGroupTypeDisplayStrings.
	 * @return String[]
	 */
	public static String[] getValidBillGroupTypeDisplayStrings()
	{
		return validBillGroupTypeDisplayStrings;
	}
	
	/**
	 * Returns the validBillGroupTypeIDs.
	 * @return int[]
	 */
	public static int[] getValidBillGroupTypeIDs()
	{
		return validBillGroupTypeIDs;
	}
	
	/**
	 * Returns the validBillGroupTypeStrings.
	 * @return String[]
	 */
	public static String[] getValidBillGroupTypeStrings()
	{
		return validBillGroupTypeStrings;
	}
    
    /**
     * Method to get a list of device names for devices that have a given meternumber
     * @param meterNumber - Meternumber to search for
     * @param excludedDeviceId - Id of device to exclude when searching or null if no exclusions
     * @return A list of device names or an empty list if none found
     */
    public static List<String> checkMeterNumber(String meterNumber, Integer excludedDeviceId){
        
        List<String> deviceNameList = new ArrayList<String>();

        if(!"Default".equalsIgnoreCase(meterNumber)){

            String exclude = "";
            Object[] parameters = new Object[] { meterNumber };
            if (excludedDeviceId != null) {
                exclude = " AND ypo.paobjectid <> ?";
                parameters = new Object[] { meterNumber, excludedDeviceId };
            }
    
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            String sql = "SELECT ypo.paoname FROM " + TABLE_NAME + " dmg, " + YukonPAObject.TABLE_NAME
                    + " ypo WHERE ypo.paobjectid=dmg.deviceid AND dmg.meternumber = ?" + exclude;
    
            SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                         parameters,
                                                         new SqlRowSetResultSetExtractor());
            while (rowSet.next()) {
                deviceNameList.add(rowSet.getString(1));
            }
        }        
        return deviceNameList;
    }

}
