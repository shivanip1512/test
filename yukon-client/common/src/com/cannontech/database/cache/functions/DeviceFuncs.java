package com.cannontech.database.cache.functions;

import com.cannontech.database.data.lite.LiteYukonPAObject;

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
			ids.add( new Integer( ((LiteYukonPAObject)devices.get(i)).getYukonID() ) );
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
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isInjector( ((LiteYukonPAObject)devices.get(i)).getType() )  )
			{
				ids.add( new Integer( ((LiteYukonPAObject)devices.get(i)).getYukonID() ) );
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
	java.util.Hashtable deviceTable = new java.util.Hashtable( 128 );

	synchronized (cache)
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		com.cannontech.database.data.lite.LitePoint litePoint = null;
		LiteYukonPAObject liteDevice = null;

		for (int i = 0; i < points.size(); i++)
		{
			litePoint = (com.cannontech.database.data.lite.LitePoint) points.get(i);
			liteDevice = PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );
			
			if(litePoint.getPaobjectID() == 0)
				continue;
			
			if( liteDevice.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE )
			{				
				java.util.ArrayList tmpPtLst= (java.util.ArrayList)deviceTable.get( liteDevice );
				
				if( tmpPtLst == null ) //not added yet
				{
					java.util.ArrayList pointList = new java.util.ArrayList();
					deviceTable.put( liteDevice, pointList );
				}
				else				 
				{
					//all ready added the device, just add the point
					tmpPtLst.add( litePoint );
				}
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
 * 
 * @deprecated replaced by PAOFuncs.getLiteYukonPAO( deviceID )
 */
public static LiteYukonPAObject getLiteDevice(final int deviceID) 
{
	return PAOFuncs.getLiteYukonPAO( deviceID );
}
}
