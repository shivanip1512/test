package com.cannontech.database.cache.functions;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class DeviceFuncs {
/**
 * PointFuncs constructor comment.
 */
private DeviceFuncs() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public static Integer[] getAllDeviceIDs()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort(devices);
		java.util.ArrayList ids = new java.util.ArrayList(devices.size());

		for(int i=0;i<devices.size();i++)
		{
			ids.add( new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID() ) );
		}		
		
		return (Integer[])ids.toArray();
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public static Integer[] getAllInjectorIDs()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort(devices);
		java.util.ArrayList ids = new java.util.ArrayList(devices.size());

		String currentType;
		 														
		for(int i=0;i<devices.size();i++)
		{
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isInjector( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() )  )
			{
				ids.add( new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID() ) );
			}

		}		
		
		return (Integer[])ids.toArray();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
/* This method returns a HashTable that has a LiteDevice as the key and */
/*   an ArrayList of LitePoints as its values */
public static java.util.Hashtable getAllLiteDevicesWithPoints() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Hashtable deviceTable = null;

	synchronized (cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;

		deviceTable = new java.util.Hashtable( devices.size() );

		for (int i = 0; i < devices.size(); i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject) devices.get(i);

			if( liteDevice.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE )
			{
				java.util.ArrayList pointList = new java.util.ArrayList( points.size() );
				
				for (int j = 0; j < points.size(); j++)
				{				
					litePoint = (com.cannontech.database.data.lite.LitePoint) points.get(j);				
					if (litePoint.getPaobjectID() == liteDevice.getYukonID())
						pointList.add( litePoint );
				}

				//add the liteDevice along with its litePoints
				deviceTable.put( liteDevice, pointList );
			}
		}
	}

	return deviceTable;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static com.cannontech.database.data.lite.LiteYukonPAObject getLiteDevice(final int deviceID) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);
		
		for( int j = 0; j < devices.size(); j++ )
		{
			if( deviceID == ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getYukonID() )
				return (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j);
		}
	}

	return null;
}
}
