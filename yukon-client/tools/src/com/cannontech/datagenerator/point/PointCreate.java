package com.cannontech.datagenerator.point;
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
	private static boolean oneDeviceAnalogPointCreate = false;
	private static boolean loadGroupPointCreate = false;
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public PointCreate() 
	{
		super();
	}
	protected abstract boolean create();
	/**
	 * Main. Start the Power Fail Point creation/insertion process.
	 * Creation date: (1/10/2001 11:18:55 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) 
	{
		int devID = -1;
		int count = 0;
		for (int i = 0; i < args.length; i++)
		{

			if( args[i].toLowerCase().startsWith("analog"))
			{
				if( args.length < 3)	//must have 'analog devid count' args specified.
					break;
				oneDeviceAnalogPointCreate = true;
				devID = Integer.parseInt(args[++i]);
				count = Integer.parseInt(args[++i]);
			}
			else if( args[i].toLowerCase().startsWith("d"))	//Disconnect Points Will Be Created
			{
				disconnectCreate = true;
			}
			else if( args[i].toLowerCase().startsWith("p"))
			{
				powerFailCreate = true;	//Power Fail Points Will Be Created
			}
			else if( args[i].toLowerCase().startsWith("lg"))
			{
				loadGroupPointCreate = true;	//Load Group Control History Points Will Be Created
			}
		}
			
		java.util.Date timerStart = null;
		java.util.Date timerStop = null;
		if(oneDeviceAnalogPointCreate)
		{
			timerStart = new java.util.Date();
			OneDevice_AnalogPointCreate analogPointCreator = new OneDevice_AnalogPointCreate(devID, count);
			analogPointCreator.create();
			timerStop = new java.util.Date();
			com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
					" Secs for OneDevice_AnalogPointCreate to complete" );
			
		}
		if(loadGroupPointCreate)
		{
			timerStart = new java.util.Date();
			LoadGroup_ControlPointCreate loadGroupControlPointCreate= new LoadGroup_ControlPointCreate();
			loadGroupControlPointCreate.create();
			timerStop = new java.util.Date();
			com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
					" Secs for LoadGroup_ControlPointCreate to complete" );
			
		}
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
			if( isPointCreated(lp))
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
	protected boolean isDeviceValid(com.cannontech.database.data.lite.LiteYukonPAObject litePaobject_)
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
	protected boolean isPointCreated(LitePoint lp)
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
				if( isDeviceValid(litePaobject) )
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
	
	protected boolean writeToSQLDatabase(com.cannontech.database.data.multi.SmartMultiDBPersistent multi) 
	{
		//write all the collected data to the SQL database
		try
		{
		  com.cannontech.clientutils.CTILogger.info("Creating Transaction to insert multi");
	      multi = (com.cannontech.database.data.multi.SmartMultiDBPersistent)com.cannontech.database.Transaction.createTransaction( com.cannontech.database.Transaction.INSERT, multi).execute();
	
			return true;
		}
		catch( com.cannontech.database.TransactionException t )
		{
			com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
			return false;
		}
	}
	
}