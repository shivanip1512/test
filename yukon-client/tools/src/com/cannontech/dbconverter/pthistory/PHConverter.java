package com.cannontech.dbconverter.pthistory;



import java.io.*;

import com.cannontech.database.data.point.PointQualities;

import com.cannontech.database.data.point.PointTypes;

import com.cannontech.message.dispatch.ClientConnection;

import com.cannontech.message.dispatch.message.Multi;

import com.cannontech.message.dispatch.message.PointData;

import com.cannontech.message.dispatch.message.Registration;





/**

* Import DSM/2 point history into yukon via Dispatch.

* Consistent with the DSM/2 database to yukon database converstion program

* load survey point id's are assumed to be offset by 100000

 * Creation date: (2/26/2002 11:30:46 AM)

 * @author: alauinger

 */

class PHConverter {

	private static final String usage =

		"PHConverter srcdir [ pointidbase ]\r\nsrcdir= DSM2 root";

/**

 * PHConverter constructor comment.

 */

public PHConverter() {

	super();

}

private static ClientConnection connect() throws java.io.IOException {

	ClientConnection conn;

	String host;

	int port;



	//figure out where vangogh is

	java.io.InputStream is = PHConverter.class.getResourceAsStream("/config.properties");

	java.util.Properties props = new java.util.Properties();



	try {

		props.load(is);

	}

	catch (Exception e) {		

		System.out.println("Can't read the properties file. " + "Make sure config.properties is in the CLASSPATH");

		return null;

	}

	

	host = props.getProperty("dispatch_machine", "localhost");	

	String portStr = props.getProperty("dispatch_port", "1510");



	try	{

		port = Integer.parseInt(portStr);

	}

	catch (NumberFormatException ne) {		

		System.out.println("Unable to determine vangog_port");

		return null;

	}

		



	System.out.println("Connecting to dispatch @" + host + ":" + port);



	conn = new ClientConnection();

	conn.setHost(host);

	conn.setPort(port);



	Registration reg = new Registration();

	

	reg.setAppName("DSM2PointHistoryConverter " + (new java.util.Date()).getTime());

	reg.setAppIsUnique(0);	

	reg.setAppKnownPort(0);	

	reg.setAppExpirationDelay(5000);

		

	conn.setAutoReconnect(true);

	conn.setTimeToReconnect(30);



	conn.connect();



	return ( conn.isValid() ? conn : null );

}

/**

 * Creation date: (2/27/2002 4:12:18 PM)

 */

public void convert(String dsm2Root) throws Exception {



	

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



	//connect to dispatch

	ClientConnection conn = connect();



	if( conn == null ) {

		System.out.println("Couldn't connect to dispatch, check config.properties and make sure dispatch is running");

		System.exit(1);

	}



	//check database connection

	java.sql.Connection c = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

	if( c == null ) {

		System.out.println("Couldn't get a connection to the database, check db.properties");

		System.exit(1);

	}

	try {

		c.close();

	} catch(java.sql.SQLException e) { }

	

	//start processing files

	System.out.println("Processing " + dsm2LSHIST.getCanonicalPath());



	File[] entries = dsm2LSHIST.listFiles();

	for( int i = 0; i < entries.length; i++ ) {

	 	System.out.print(entries[i].getName());

	 	

		int id = stripID(entries[i].getName());



		if( id == -1 ) {

			System.out.println(" ...skipping");

			continue;

		}



		float multiplier = getMultiplier(id);

		

		if( multiplier < 0 ) {

			System.out.println(" ...no yukon point found skipping");

			continue;

		}

						 	

		DSM2PointData[] data = DSM2PointData.loadPointData(entries[i].getCanonicalPath());

		System.out.println("Read " + data.length + " values");



		sendPointData(conn, id+100000, 1.0f, data);

	}



	entries = dsm2READHIST.listFiles();

	for( int i = 0; i < entries.length; i++ ) {

		System.out.print(entries[i].getName());

		

		int id = stripID(entries[i].getName());



		if( id == -1 ) {

			System.out.println(" ...skipping");

			continue;

		}

		

		float multiplier = getMultiplier(id);

		

		if( multiplier < 0 ) {

			System.out.println(" ...no yukon point found skipping");

			continue;

		}

			

		DSM2PointData[] data = DSM2PointData.loadPointData(entries[i].getCanonicalPath());

		System.out.println(" read " + data.length + " values");

		

		sendPointData(conn, id, multiplier, data);	

	}



	while( conn.getNumOutMessages() > 0 ) {

		System.out.println("Waiting for dispatch to consume " + conn.getNumOutMessages() + " messages");

		Thread.sleep(1000);

	}



	System.out.println("Finished");

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

 * Creation date: (2/26/2002 11:38:03 AM)

 * @param args java.lang.String[]

 */

public static void main(String[] args) throws Exception {



	if( args.length != 1 ) {

		System.out.println(usage);

		System.exit(1);

	}



	PHConverter p = new PHConverter();

	p.convert(args[0]);

	

	System.exit(0);

}

/**

 * Creation date: (2/26/2002 4:02:53 PM)

 * @param conn com.cannontech.vangogh.ClientConnection

 * @param data com.cannontech.dbconverter.pthistory.DSM2PointData

 * @param multiplier float

 */

private static void sendPointData(ClientConnection conn, int id, float multiplier, DSM2PointData[] data)

{	

	java.util.Date now = new java.util.Date();



	Multi multi = new Multi();

	

	for( int i = 0; i < data.length; i++ ) {



		if( data[i] == null )

			continue;



		PointData pData = new PointData();

		

        pData.setTags(0x00002000);

		pData.setType(PointTypes.INVALID_POINT);

  

   	 	pData.setId( id );

    	pData.setValue( data[i].value * multiplier );

    	setQuality( pData, data[i].quality );

    	pData.setTimeStamp(now);

    	pData.setTime( new java.util.Date( (long) data[i].time * 1000) );

		

    	

		multi.getVector().add(pData);

		

    	if( i != 0 && i % 500 == 0 ) {

	    	System.out.println("Sending 500 to dispatch");

			conn.write(multi);

			multi = new Multi();



			try {

			//Thread.sleep(3000); 

			} catch(Throwable t ) { }

			

    	}

    	

	}



	if( multi.getVector().size() > 0 ) {

		System.out.println("Sending " + multi.getVector().size() + " to dispatch");

		conn.write(multi);

	}



	try {

	while( conn.getNumOutMessages() > 0 ) {

		System.out.println("Waiting for dispatch to consume " + conn.getNumOutMessages() + " messages");

		Thread.sleep(1000);

	}

	} catch(InterruptedException e ) {

		e.printStackTrace();

	}

}

/**

 * Creation date: (3/1/2002 12:00:46 PM)

 * @param pData com.cannontech.vangogh.messages.PointData

 * @param dsmQuality short

 */

private static void setQuality(PointData pData, short dsmQuality) {



	long yukonQuality;

	

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

	

	pData.setQuality(yukonQuality);	

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

}

