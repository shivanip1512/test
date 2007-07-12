package com.cannontech.database.db.esub;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (12/21/2000 5:24:26 PM)
 * @author: 
 */
public class XFMR extends com.cannontech.database.db.DBPersistent implements com.cannontech.esub.SubstationItem {
	private static final String tableName = "XFMR";
	
	private int ID;
	private String name;
	private int substationID;

	//point ids
	private int oilTempID;
	private int hsTempID;
	private int ThermalAgeID;
	private int ambientTempID;
	private int loadMVAID;
	private int busAVoltsID;
	private int busGVoltsID;
	private int ltcTodayID;
	private int ltcPreviousDayID;
	private int ltcOilTempID;
	private int ltcTankTempDiffID;
	private int coolingBank1TempID;
	private int coolingBank2TempID;
	private int coolingBank1StatusID;
	private int coolingBank2StatusID;
	private int coolingBank1AlarmID;
	private int coolingBank2AlarmID;
	private int powerGraphDefinitionID;
	private int ltcGraphDefinitionID;
/**
 * XFMR constructor comment.
 */
public XFMR() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException( getClass() + " add() not implemented");
		
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException( getClass() + " delete() not implemented");
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 7:30:55 AM)
 * @return com.cannontech.database.db.esub.XFMR[]
 * @param substationID int
 * @param dbAlias java.lang.String
 */
public static XFMR[] getAllXFMRs(int substationID) {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 7:30:55 AM)
 * @return com.cannontech.database.db.esub.XFMR[]
 * @param substationID int
 * @param dbAlias java.lang.String
 */
public static XFMR[] getAllXFMRs(int substationID, String dbAlias) {
	String sql = "SELECT XFMRID,Name,OilTempID,HSTempID,ThermalAgeID,AmbientTempID,LoadMVAID,BusAVoltsID,BusGVoltsID,LTCTodayID,LTCPrevDayID,LTCOilTempID,LTCTankTempDiffID,CoolingBank1TempID,CoolingBank2TempID,CoolingBank1StatusID, CoolingBank2StatusID, CoolingBank1AlarmID,CoolingBank2AlarmID,PWRGraphDefinitionID,LTCGraphDefinitionID FROM XFMR WHERE SubstationID=" + substationID;

	XFMR[] retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();

		rset = stmt.executeQuery(sql);

		java.util.ArrayList xfmrList = new java.util.ArrayList();
		while( rset.next() )
		{
			XFMR xfmr = new XFMR();

			xfmr.setID( rset.getInt(1) );
			xfmr.setName( rset.getString(2) );
			xfmr.setSubstationID( substationID );
			xfmr.setOilTempID( rset.getInt(3) );
			xfmr.setHsTempID( rset.getInt(4) );
			xfmr.setThermalAgeID( rset.getInt(5) );
			xfmr.setAmbientTempID( rset.getInt(6) );
			xfmr.setLoadMVAID( rset.getInt(7) );
			xfmr.setBusAVoltsID( rset.getInt(8) );
			xfmr.setBusGVoltsID( rset.getInt(9) );
			xfmr.setLtcTodayID( rset.getInt(10) );
			xfmr.setLtcPreviousDayID( rset.getInt(11) );
			xfmr.setLtcOilTempID( rset.getInt(12) );			
			xfmr.setLtcTankTempDiffID( rset.getInt(13) );
			xfmr.setCoolingBank1TempID( rset.getInt(14) );
			xfmr.setCoolingBank2TempID( rset.getInt(15) );
			xfmr.setCoolingBank1StatusID( rset.getInt(16) );
			xfmr.setCoolingBank2StatusID( rset.getInt(17) );
			xfmr.setCoolingBank1AlarmID( rset.getInt(18) );
			xfmr.setCoolingBank2AlarmID( rset.getInt(19) );
			xfmr.setPowerGraphDefinitionID( rset.getInt(20) );
			xfmr.setLtcGraphDefinitionID( rset.getInt(21) );
			
			xfmrList.add(xfmr);
		}

		retVal = new XFMR[xfmrList.size()];
		xfmrList.toArray(retVal);
		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );
	}
	

	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getAmbientTempID() {
	return ambientTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getBusAVoltsID() {
	return busAVoltsID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getBusGVoltsID() {
	return busGVoltsID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getCoolingBank1AlarmID() {
	return coolingBank1AlarmID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 8:53:33 AM)
 * @return int
 */
public int getCoolingBank1StatusID() {
	return coolingBank1StatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getCoolingBank1TempID() {
	return coolingBank1TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getCoolingBank2AlarmID() {
	return coolingBank2AlarmID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 8:53:33 AM)
 * @return int
 */
public int getCoolingBank2StatusID() {
	return coolingBank2StatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getCoolingBank2TempID() {
	return coolingBank2TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getHsTempID() {
	return hsTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:42:04 AM)
 * @return int
 */
public int getID() {
	return ID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getLoadMVAID() {
	return loadMVAID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getLtcGraphDefinitionID() {
	return ltcGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getLtcOilTempID() {
	return ltcOilTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getLtcPreviousDayID() {
	return ltcPreviousDayID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 7:51:57 AM)
 * @return int
 */
public int getLtcTankTempDiffID() {
	return ltcTankTempDiffID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getLtcTodayID() {
	return ltcTodayID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:41:45 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getOilTempID() {
	return oilTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getPowerGraphDefinitionID() {
	return powerGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:42:04 AM)
 * @return int
 */
public int getSubstationID() {
	return substationID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @return int
 */
public int getThermalAgeID() {
	return ThermalAgeID;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	String[] selectColumns =
	{	
		"Name",
		"SubstationID",
		"OilTempID",
		"HSTempID",
		"ThermalAgeID",
		"AmbientTempID",
		"LoadMVAID",
		"BusAVoltsID",
		"BusGVoltsID",
		"LTCTodayID",
		"LTCPrevDayID",
		"LTCOilTempID",
		"LTCTankTempDiffID",
		"CoolingBank1TempID",
		"CoolingBank2TempID",
		"CoolingBank1StatusID",
		"CoolingBank2StatusID",
		"CoolingBank1AlarmID",
		"CoolingBank2AlarmID",
		"PWRGraphDefinitionID",
		"LTCGraphDefinitionID"
	};

	String[] constraintColumns =
	{
		"XFMRID"
	};

	Object[] constraintValues =
	{
		new Integer( getID() )
	};

	Object[] results = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setName( (String) results[0] );
		setSubstationID( ((Integer) results[1]).intValue() );

		Integer oilTempID = (Integer) results[2];
		Integer hsTempID = (Integer) results[3];
		Integer thermalAgeID = (Integer) results[4];
		Integer ambientTempID = (Integer) results[5];
		Integer loadMVAID = (Integer) results[6];
		Integer busAVoltsID = (Integer) results[7];
		Integer busGVoltsID = (Integer) results[8];
		Integer ltcTodayID = (Integer) results[9];
		Integer ltcPrevDayID = (Integer) results[10];
		Integer ltcOilTempID = (Integer) results[11];
		Integer ltcTankTempDiffID = (Integer) results[12];
		Integer coolingBank1TempID = (Integer) results[13];
		Integer coolingBank2TempID = (Integer) results[14];
		Integer coolingBank1StatusID = (Integer) results[15];
		Integer coolingBank2StatusID = (Integer) results[16];
		Integer coolingBank1AlarmID = (Integer) results[17];
		Integer coolingBank2AlarmID = (Integer) results[18];
		Integer pwrGraphDefinitionID = (Integer) results[19];
		Integer ltcGraphDefinitionID = (Integer) results[20];
		
		if( oilTempID != null )
		 	setOilTempID( oilTempID.intValue() );

		if( hsTempID != null )
			setHsTempID( hsTempID.intValue() );

		if( thermalAgeID != null )
			setThermalAgeID( thermalAgeID.intValue() );

		if( ambientTempID != null )
			setAmbientTempID( ambientTempID.intValue() );

		if( loadMVAID != null )
			setLoadMVAID( loadMVAID.intValue() );

		if( busAVoltsID != null )
			setBusAVoltsID( busAVoltsID.intValue() );

		if( busGVoltsID != null )
			setBusGVoltsID( busGVoltsID.intValue() );

		if( ltcTodayID != null )
			setLtcTodayID( ltcTodayID.intValue() );

		if( ltcPrevDayID != null )
			setLtcPreviousDayID( ltcPrevDayID.intValue() );

		if( ltcOilTempID != null )
			setLtcOilTempID( ltcOilTempID.intValue() );
			
		if( ltcTankTempDiffID != null )
			setLtcTankTempDiffID( ltcTankTempDiffID.intValue() );

		if( coolingBank1TempID != null )
			setCoolingBank1TempID( coolingBank1TempID.intValue() );

		if( coolingBank2TempID != null )
			setCoolingBank2TempID( coolingBank2TempID.intValue() );

		if( coolingBank1StatusID != null )
			setCoolingBank1StatusID( coolingBank1StatusID.intValue() );

		if( coolingBank2StatusID != null )
			setCoolingBank2StatusID( coolingBank2StatusID.intValue() );
			
		if( coolingBank1AlarmID != null )
			setCoolingBank1AlarmID( coolingBank1AlarmID.intValue() );

		if( coolingBank2AlarmID != null )
			setCoolingBank2AlarmID( coolingBank2AlarmID.intValue() );

		if( pwrGraphDefinitionID != null )
			setPowerGraphDefinitionID( pwrGraphDefinitionID.intValue() );

		if( ltcGraphDefinitionID != null )
			setLtcGraphDefinitionID( ltcGraphDefinitionID.intValue() );
			
	}
	else
		throw new RuntimeException( getClass() + " - database returned an unexpected number of columns");		
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newAmbientTempID int
 */
public void setAmbientTempID(int newAmbientTempID) {
	ambientTempID = newAmbientTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newBusAVoltsID int
 */
public void setBusAVoltsID(int newBusAVoltsID) {
	busAVoltsID = newBusAVoltsID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newBusGVoltsID int
 */
public void setBusGVoltsID(int newBusGVoltsID) {
	busGVoltsID = newBusGVoltsID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newCoolingBank1AlarmID int
 */
public void setCoolingBank1AlarmID(int newCoolingBank1AlarmID) {
	coolingBank1AlarmID = newCoolingBank1AlarmID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 8:53:33 AM)
 * @param newCoolingBank1StatusID int
 */
public void setCoolingBank1StatusID(int newCoolingBank1StatusID) {
	coolingBank1StatusID = newCoolingBank1StatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newCoolingBank1TempID int
 */
public void setCoolingBank1TempID(int newCoolingBank1TempID) {
	coolingBank1TempID = newCoolingBank1TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newCoolingBank2AlarmID int
 */
public void setCoolingBank2AlarmID(int newCoolingBank2AlarmID) {
	coolingBank2AlarmID = newCoolingBank2AlarmID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 8:53:33 AM)
 * @param newCoolingBank2StatusID int
 */
public void setCoolingBank2StatusID(int newCoolingBank2StatusID) {
	coolingBank2StatusID = newCoolingBank2StatusID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newCoolingBank2TempID int
 */
public void setCoolingBank2TempID(int newCoolingBank2TempID) {
	coolingBank2TempID = newCoolingBank2TempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newHsTempID int
 */
public void setHsTempID(int newHsTempID) {
	hsTempID = newHsTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:42:04 AM)
 * @param newID int
 */
public void setID(int newID) {
	ID = newID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newLoadMVWID int
 */
public void setLoadMVAID(int newLoadMVAID) {
	loadMVAID = newLoadMVAID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newLtcGraphDefinitionID int
 */
public void setLtcGraphDefinitionID(int newLtcGraphDefinitionID) {
	ltcGraphDefinitionID = newLtcGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newLtcOilTempID int
 */
public void setLtcOilTempID(int newLtcOilTempID) {
	ltcOilTempID = newLtcOilTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newLtcPreviousDayID int
 */
public void setLtcPreviousDayID(int newLtcPreviousDayID) {
	ltcPreviousDayID = newLtcPreviousDayID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 7:51:57 AM)
 * @param newLtcTankTempDiffID int
 */
public void setLtcTankTempDiffID(int newLtcTankTempDiffID) {
	ltcTankTempDiffID = newLtcTankTempDiffID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newLtcTodayID int
 */
public void setLtcTodayID(int newLtcTodayID) {
	ltcTodayID = newLtcTodayID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:41:45 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newOilTempID int
 */
public void setOilTempID(int newOilTempID) {
	oilTempID = newOilTempID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newPowerGraphDefinitionID int
 */
public void setPowerGraphDefinitionID(int newPowerGraphDefinitionID) {
	powerGraphDefinitionID = newPowerGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:42:04 AM)
 * @param newSubstationID int
 */
public void setSubstationID(int newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 6:33:55 AM)
 * @param newThermalAgeID int
 */
public void setThermalAgeID(int newThermalAgeID) {
	ThermalAgeID = newThermalAgeID;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	String[] setColumns =
	{
		"Name",
		"SubstationID",
		"OilTempID",
		"HSTempID",
		"ThermalAgeID",
		"AmbientTempID",
		"LoadMVAID",
		"BusAVoltsID",
		"BusGVoltsID",
		"LTCTodayID",
		"LTCPrevDayID",
		"LTCOilTempID",
		"LTCTankTempDiffID",
		"CoolingBank1TempID",
		"CoolingBank2TempID",
		"CoolingBank1StatusID",
		"CoolingBank2StatusID",
		"CoolingBank1AlarmID",
		"CoolingBank2AlarmID",
		"PWRGraphDefinitionID",
		"LTCGraphDefinitionID"
	};

	Object[] setValues =
	{
		getName(),
		new Integer( getSubstationID() ),
		new Integer( getOilTempID() ),
		new Integer( getHsTempID() ),
		new Integer( getThermalAgeID() ),
		new Integer( getLoadMVAID() ),
		new Integer( getBusAVoltsID() ),
		new Integer( getBusGVoltsID() ),
		new Integer( getLtcTodayID() ),
		new Integer( getLtcPreviousDayID() ),
		new Integer( getLtcOilTempID() ),
		new Integer( getLtcTankTempDiffID() ),
		new Integer( getCoolingBank1TempID() ),
		new Integer( getCoolingBank2TempID() ),
		new Integer( getCoolingBank1StatusID() ),
		new Integer( getCoolingBank2StatusID() ),
		new Integer( getCoolingBank1AlarmID() ),
		new Integer( getCoolingBank2AlarmID() ),
		new Integer( getPowerGraphDefinitionID() ),
		new Integer( getLtcGraphDefinitionID() )
	};	 
}
}
