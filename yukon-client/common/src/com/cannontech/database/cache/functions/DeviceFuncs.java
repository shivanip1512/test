package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

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
		List devices = cache.getAllDevices();
		Collections.sort(devices);
		ArrayList retList = new ArrayList(devices.size());

		for(int i=0;i<devices.size();i++)
		{
            retList.add( new Integer( ((LiteYukonPAObject)devices.get(i)).getYukonID() ) );
		}	
        Integer[] ids = new Integer[retList.size()];
		return (Integer[])retList.toArray(ids);
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

public static LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID)
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
    List allDevMtrGrps = cache.getAllDeviceMeterGroups();
    
    LiteDeviceMeterNumber ldmn = null;
    for (int i = 0; i < allDevMtrGrps.size(); i++)
    {
        ldmn = (LiteDeviceMeterNumber)allDevMtrGrps.get(i);
        if (ldmn.getDeviceID() == deviceID)
            return ldmn;
    }
    return null;
}

/**
 * This returns a LiteYukonPaobject for the meterNumber.
 * WARNING: This is a "BEST GUESS" (or the first one in the deviceMeterNumber cache) as
 *  MeterNumber is NOT distinct for all general purposes, but may be a utilities distinct field.
 * @param deviceID
 * @return
 */
public static LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber)
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	List allDevMtrGrps = cache.getAllDeviceMeterGroups();
    
	LiteDeviceMeterNumber ldmn = null;
	for (int i = 0; i < allDevMtrGrps.size(); i++)
	{
		ldmn = (LiteDeviceMeterNumber)allDevMtrGrps.get(i);
		if (ldmn.getMeterNumber().equals(meterNumber))
		{
			LiteYukonPAObject lPao = (LiteYukonPAObject)cache.getAllPAOsMap().get(new Integer(ldmn.getDeviceID()));
			return lPao;
		}
	}
	return null;
}

/**
 * This returns a LiteYukonPaobject for the PaoName.
 * WARNING: This is a "BEST GUESS" (or the first one in the DEVICE cache) as
 *  PaoName is NOT necessarily distinct for all general purposes, but may be a utilities distinct field.
 * @param deviceID
 * @return
 */
public static LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName)
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
    List allDevices = cache.getAllDevices();
    
    LiteYukonPAObject lPao = null;
    for (int i = 0; i < allDevices.size(); i++)
    {
        lPao = (LiteYukonPAObject)allDevices.get(i);
        if (lPao.getPaoName().equals(deviceName))
            return lPao;
    }
    return null;
}

/**
 * Will find a device based on the four parameters that make up its unique key.
 * This method will return null if a device couldn't be found.
 * @param deviceName
 * @param category
 * @param paoClass
 * @param type
 * @return the LiteYukonPaoObject that matches the criteria
 */
public static LiteYukonPAObject getLiteYukonPAObject(String deviceName, int category, int paoClass, int type)
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
    List allDevices = cache.getAllDevices();
    for (Object obj : allDevices) {
        LiteYukonPAObject lPao = (LiteYukonPAObject) obj;
        boolean foundMatch = true;
        foundMatch &= lPao.getPaoName().equals(deviceName);
        foundMatch &= lPao.getCategory() == category;
        foundMatch &= lPao.getPaoClass() == paoClass;
        foundMatch &= lPao.getType() == type;
        if (foundMatch) {
            return lPao;
        }
    }
    return null;
}

/**
 * Will find a device based on the four parameters that make up its unique key.
 * This method will return null if a device couldn't be found.
 * @param deviceName
 * @param category
 * @param paoClass
 * @param type
 * @return the LiteYukonPaoObject that matches the criteria
 */
public static LiteYukonPAObject getLiteYukonPAObject(String deviceName, String category, String paoClass, String type)
{
    int categoryInt = PAOGroups.getCategory(category);
    int paoClassInt = PAOGroups.getPAOClass(category, paoClass);
    int typeInt = PAOGroups.getPAOType(category, type);
    return getLiteYukonPAObject(deviceName, categoryInt, paoClassInt, typeInt);
}

public static List getDevicesByPort(int portId)
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
    List devices = cache.getDevicesByCommPort(portId);
        
    return devices;  
}
public static List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
    
    List devicesByAddress = cache.getDevicesByDeviceAddress(masterAddress, slaveAddress);
        
    return devicesByAddress;
}
 
}
