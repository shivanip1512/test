package com.cannontech.dbtools.dbcreator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
abstract class PointCreate 
{
	//a mutable lite point used for comparisons
	private static final LitePoint DUMMY_LITE_POINT = 
					new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
					
	private static boolean disconnectCreate = false;
	private static boolean powerFailCreate = false;
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public PointCreate() 
	{
		super();
	}
	abstract boolean create();
	/**
	 * Main. Start the Power Fail Point creation/insertion process.
	 * Creation date: (1/10/2001 11:18:55 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) 
	{
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i].toLowerCase();
			if( arg.startsWith("d"))	//Disconnect Points Will Be Created
			{
				disconnectCreate = true;
			}
			else if( arg.startsWith("p"))
			{
				powerFailCreate = true;	//Power Fail Points Will Be Created
			}
		}
			
		java.util.Date timerStart = null;
		java.util.Date timerStop = null;
		if( powerFailCreate )
		{
			timerStart = new java.util.Date();
			PowerFailPointCreate powerFailCreator = new PowerFailPointCreate();
			powerFailCreator.create();
			timerStop = new java.util.Date();
			com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
					" Secs for PowerFailPointCreate to complete" );
		}
		if( disconnectCreate)
		{
			timerStart = new java.util.Date();
			DisconnectPointCreate disconnectCreator = new DisconnectPointCreate();
			disconnectCreator.create();
			timerStop = new java.util.Date();
			com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
					" Secs for DisconnectPointCreate to complete" );
		}		
		System.exit(0);
	}

	/**
	 * Returns true when the deviceDevID does not already have 
	 * a POWER FAIL point attached to it.  
	 * The list points contains all points to search through.
	 * Creation date: (2/27/2002 10:37:56 AM)
	 * @param points java.util.List
	 * @param deviceDevID int
	 * @return boolean
	 */
	protected boolean addPointToDevice(java.util.List points, int deviceDevID )
	{
		java.util.List pointTempList = new java.util.Vector(10);
		
		DUMMY_LITE_POINT.setPaobjectID(deviceDevID);	//needed for binarySearchRepetition
		
		//searches and sorts the list!
		com.cannontech.common.util.CtiUtilities.binarySearchRepetition( 
						points,
						DUMMY_LITE_POINT, //must have the needed DeviceID set!!
						com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator,
						pointTempList );
					
		for (int i = 0; i < pointTempList.size(); i++)
		{
			com.cannontech.database.data.lite.LitePoint lp = (LitePoint)pointTempList.get(i);
			if( isPointCreated(lp.getPointOffset(), lp.getPointType()))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns true if the Device is a valid container of the point.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	protected boolean isDeviceValid( int type_ )
	{
		return true;
	}

	/**
	 * Returns true if the Point already exists for the Device
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	protected boolean isPointCreated(int pointOffset_, int pointType_)
	{
		return false;
	}
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected void getDeviceVector(java.util.Vector deviceVector)
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			java.util.List devices = cache.getAllDevices();
			java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
			
			for (int i = 0; i < devices.size(); i++)
			{
				com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i));
				if( isDeviceValid(litePaobject.getType()) )
				{
					int deviceDevID = litePaobject.getLiteID();
					
					//makes a list of points devices to add power fail points to
					if( addPointToDevice( points, deviceDevID ))
					{
						deviceVector.addElement(litePaobject);
					}
				}
			}
			com.cannontech.clientutils.CTILogger.info(deviceVector.size() + " Total Devices needing points added.");
		} //synch
	}
	
	/**
	 * Returns true if multi is successfully inserted into the Database.
	 * Creation date: (3/31/2001 12:07:17 PM)
	 * @param multi com.cannontech.database.data.multi.MultiDBPersistent
	 */
	protected boolean writeToSQLDatabase(com.cannontech.database.data.multi.MultiDBPersistent multi) 
	{
		//write all the collected data to the SQL database
		try
		{
		  com.cannontech.clientutils.CTILogger.info("Creating Transaction to insert multi");
	      multi = (com.cannontech.database.data.multi.MultiDBPersistent)com.cannontech.database.Transaction.createTransaction( com.cannontech.database.Transaction.INSERT, multi).execute();
	
			return true;
		}
		catch( com.cannontech.database.TransactionException t )
		{
			com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
			return false;
		}
	}
}