package com.cannontech.dbconverter.pthistory;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.DBPersistent;

/**
* * Import DSM/2 point history into the yukon database by inserting data directly into the rawpointhistory table.
* Consistent with the DSM/2 database to yukon database converstion program
* load survey point id's are assumed to be offset by 100000
 * Creation date: (2/26/2002 11:30:46 AM)
 * @author: 
 */
class PHConverter2 {
	private static final String usage =
		"PHConverter srcdir [ FORCE (insert timestamps ignoring RPH maxTimestamp) ]\r\nsrcdir=<DSM2 root> FORCE";

	private long lastTimestamp = 0;
	private int changeID = 0;
	private boolean forceInsert = false;
	
	private java.util.Vector pointVector = null;
	
/**
 * PHConverter constructor comment.
 */
public PHConverter2() {
	super();
}
/**
 * Creation date: (2/27/2002 4:12:18 PM)
 */
public void convert(String dsm2Root) throws Exception {

	long start = System.currentTimeMillis();
	
	//check dsm directories
	File dsm2LSHIST = new File(dsm2Root + "/LSHIST");
	File dsm2READHIST = new File(dsm2Root + "/READHIST");
	
	if( !dsm2LSHIST.isDirectory() ||
		!dsm2READHIST.isDirectory() ) {
		System.out.println("Bad dsm2 root directory");
		System.out.println(dsm2LSHIST.getCanonicalPath() + " and " + dsm2READHIST.getCanonicalPath() + " do not exist or are not directories");
		System.out.println(usage);
		System.exit(1);		
	}

	
	//check database connection and
 	// initialize the changeid and timestamp to allow for cumalitive action
	if( !initYukonStuff() ) {
		System.out.println("Couldn't get a connection to the database, check db.properties");
		System.exit(1);
	}
	
	//start processing files
	System.out.println("Processing " + dsm2LSHIST.getCanonicalPath());

	File[] entries = dsm2LSHIST.listFiles();
	for( int i = 0; i < entries.length; i++ ) {
	 	System.out.print(entries[i].getName());
	 	
		int id = stripID(entries[i].getName());

		if( id == -1 || !exists(id))
		{
			System.out.println(" ...skipping");
			continue;
		}

		float multiplier = getMultiplier(id);
		
		if( multiplier < 0 ) {
			System.out.println(" ...no yukon point found skipping");
			continue;
		}
						 	
		DSM2PointData[] data = DSM2PointData.loadPointData(entries[i].getCanonicalPath());
		System.out.println(" Read " + data.length + " values");

		writePointData(id+100000, multiplier, data);
	}

	entries = dsm2READHIST.listFiles();
	for( int i = 0; i < entries.length; i++ ) {
		System.out.print(entries[i].getName());
		
		int id = stripID(entries[i].getName());

		if( id == -1 ) {
			System.out.println(" ...skipping");
			continue;
		}
		
		DSM2PointData[] data = DSM2PointData.loadPointData(entries[i].getCanonicalPath());
		System.out.println(" read " + data.length + " values");
		
		writePointData(id,1.0f, data);	
	}

	System.out.println("Finished in " + (System.currentTimeMillis() - start) + " millis");
}
/**
 * Creation date: (2/26/2002 4:01:41 PM)
 * @return float
 * @param id int
 */
private static float getAccumulatorMultiplier(int id)
{
	float multiplier = -1.0f;
	
    java.sql.Connection conn = null;
    java.sql.Statement stmt = null;
    java.sql.ResultSet rset = null;

    try
        {
        conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
        stmt = conn.createStatement();
        rset = stmt.executeQuery("SELECT Multiplier FROM PointAccumulator WHERE PointID=" + id);

        if (rset.next()) {
        	multiplier = rset.getFloat(1);
        }
    }
    catch (java.sql.SQLException e) {
    }
    finally
        {
        try
            {
            if (rset != null)
                rset.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        catch (java.sql.SQLException e2) {            
        }

        return multiplier;
    }       
}
/**
 * Creation date: (2/26/2002 4:01:41 PM)
 * @return float
 * @param id int
 */
private static float getAnalogMultiplier(int id)
{
	float multiplier = -1.0f;
	
    java.sql.Connection conn = null;
    java.sql.Statement stmt = null;
    java.sql.ResultSet rset = null;

    try
        {
        conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
        stmt = conn.createStatement();
        rset = stmt.executeQuery("SELECT Multiplier FROM PointAnalog WHERE PointID=" + id);

        if (rset.next()) {
        	multiplier = rset.getFloat(1);
        }
    }
    catch (java.sql.SQLException e) {
    }
    finally
        {
        try
            {
            if (rset != null)
                rset.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        catch (java.sql.SQLException e2) {            
        }

        return multiplier;
    }       
}
/**
 * Creation date: (2/26/2002 4:01:41 PM)
 * @return float
 * @param id int
 */
private static float getMultiplier(int id)
{
	float mult = getAnalogMultiplier(id);

	if( mult < 0 )
		mult = getAccumulatorMultiplier(id);

	return mult;
}
/**
 * Creation date: (3/1/2002 12:00:46 PM)
 * @param pData com.cannontech.vangogh.messages.PointData
 * @param dsmQuality short
 */
private static int getQuality(short dsmQuality) {

	int yukonQuality;
	
	//Bits 11, 13
	if( (dsmQuality & (short) 0x0800) > 0 || 
		(dsmQuality & (short) 0x2000) > 0 ) {
		
		yukonQuality = PointQualities.NON_UPDATED_QUALITY;			
	}
	else {
		short dqMask = (short) (dsmQuality & (short) 0x003F);

		switch(dqMask) {

			case 0: //NORMAL
				yukonQuality = PointQualities.NORMAL_QUALITY;
			break;

			case 1: //INVALID
				yukonQuality = PointQualities.INVALID_QUALITY;
			break;
			
			case 2:
				yukonQuality = PointQualities.UNKNOWN_QUALITY;
			break;
			
			case 3: //HIGH-LIMIT
			case 4: //HIGH-WARN
				yukonQuality = PointQualities.EXCEEDS_HIGH_QUALITY;	
			break;
			
			case 5: //LO-LIMIT
			case 6: //LO-WARN
				yukonQuality = PointQualities.EXCEEDS_LOW_QUALITY;
			break;
			
			case 16: //OVERFLOW
				yukonQuality = PointQualities.OVERFLOW_QUALITY;
			break;

			case 17: //DEV FILLER
				yukonQuality = PointQualities.DEVICE_FILLER_QUALITY;
			break;

			case 18: //POWER FAIL
				yukonQuality = PointQualities.POWERFAIL_QUALITY;
			break;

			case 19: //PARTIAL
				yukonQuality = PointQualities.PARTIAL_INTERVAL_QUALITY;
			break;

			case 20: //HARD ARM
			case 21: //QUESTIONABLE
			case 22: //MS FILLER
			case 23: //MS SHIFT
			case 24: //PF SHIFT
			case 25: //STATE CHG
				yukonQuality = PointQualities.QUESTIONABLE_QUALITY;
			break;
			
			default:
				System.out.println("Unknown DSM2 DQ Status Mask: " + dqMask );
				yukonQuality = PointQualities.UNKNOWN_QUALITY;							
		}
	}		
	
	return yukonQuality;
}
/**
 * Creation date: (2/26/2002 11:38:03 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) throws Exception {

	if( args.length < 1  ) {
		System.out.println(usage);
		System.exit(1);
	}
	PHConverter2 p = new PHConverter2();

	// Forcing allows us to insert Point History independently of lastTimestamp.	
	if( args.length == 2)
	{
		String lowerCaseArg = args[1].toLowerCase();
		if( lowerCaseArg.startsWith("force"))
			p.forceInsert = true;
	}
	System.out.println(" FORCE = " + p.forceInsert);
	p.convert(args[0]);
	System.exit(0);
}
/**
 * Creation date: (2/26/2002 3:58:03 PM)
 * @return int
 * @param datFile java.lang.String
 */
private static int stripID(String datFile) {

	int id = -1;
	int i = datFile.indexOf(".");

	if( i >= 0 ) {

		String idStr = datFile.substring(0,i);
	
		try {
			id = Integer.parseInt(idStr);
		}
		catch(NumberFormatException e) {
		}		
	}	
	
	return id;	
}
/**
 * Creation date: (2/26/2002 4:02:53 PM)
 * @param conn com.cannontech.vangogh.ClientConnection
 * @param data com.cannontech.dbconverter.pthistory.DSM2PointData
 * @param multiplier float
 */
private void writePointData(int id, float multiplier, DSM2PointData[] data)
{
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	
	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		conn.setAutoCommit(false);
		
		pstmt = conn.prepareStatement("INSERT INTO RAWPOINTHISTORY VALUES(?,?,?,?,?)");

		int count = 0;
		for(int i = 0; i < data.length; i++ ) {

			if( data[i] == null)
				continue;
				
			java.sql.Timestamp timestamp = new java.sql.Timestamp( (long) data[i].time*1000L);

			// We only want two decimal places so we'll do a little trick	
			double value = (double) Math.round(data[i].value*multiplier*100.0) / 100.0;
			int quality = getQuality(data[i].quality);

			if( forceInsert )
			{
			}
			else if(timestamp.getTime() <= lastTimestamp)
				continue;

			pstmt.setInt(1, changeID++);
			pstmt.setInt(2, id);			
			pstmt.setTimestamp(3, timestamp);
			pstmt.setInt(4, quality);
			pstmt.setDouble(5, value);

			pstmt.execute();

			count++;
		 }

		System.out.print("\r\nWriting " + count + " values for ID="+id+"...");
		long start = System.currentTimeMillis();		
		conn.commit();
		System.out.println(" took "  + (System.currentTimeMillis() - start) + " millis");
	}
	catch(java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try {
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} catch(java.sql.SQLException e2) { e2.printStackTrace(); }
	}
}
/**
 * If there is nothing in RawPointHistory, lastTimestamp will remain at 0 (its init value) - 20041214 SN
 * @return
 */
private boolean initYukonStuff() {
	Connection conn = null;
	Statement stmt = null;
	ResultSet rset = null;
		
	try {
		conn = PoolManager.getInstance().getConnection("yukon");
		stmt = conn.createStatement();
		rset = stmt.executeQuery("select max(changeid),max(timestamp) from rawpointhistory");
	
		if(rset.next()) {
			changeID = rset.getInt(1) + 1;
			if( rset.getTimestamp(2) != null)
			    lastTimestamp = rset.getTimestamp(2).getTime();	
		}	
		rset.close();
		return true;
	}
	catch(SQLException e) {
		e.printStackTrace();
	}	
	finally {
		try {
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
		}
		catch(SQLException e2) {}
	}	
	
	return false;
}

private boolean exists(int pointID)
{
	for (int i = 0; i < pointVector.size(); i++)
	{
		if( ((Integer)pointVector.get(i)).intValue() == pointID)
			return true;
	}
	return false;
}
private java.util.Vector getPointVector()
{
	if( pointVector == null)
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
		{
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator);

			pointVector = new java.util.Vector(points.size());
			for (int i = 0; i < points.size(); i++)
			{
				com.cannontech.database.data.lite.LitePoint litePoint = ((com.cannontech.database.data.lite.LitePoint) points.get(i));
				pointVector.add(new Integer(litePoint.getLiteID()));
			}
		} //synch
	}
	return pointVector;
}
}