package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
abstract class PointCreate 
{
	private static boolean disconnectCreate = false;
	private static boolean powerFailCreate = false;
	private static boolean oneDeviceAnalogPointCreate = false;
	private static boolean loadGroupPointCreate = false;
	private static boolean mct410PointCreate = false;
	private static boolean capBankOpCntPointCreate = false;
	private static boolean outageLogCreate = false;
	private static boolean mct410FrozenPointCreate = false;
	private static boolean tamperFlagCreate = false;
	private static boolean zeroUsageFlagCreate = false;
	private static boolean reversePowerFlagCreate = false;
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
			else if( args[i].toLowerCase().startsWith("p") || args[i].toLowerCase().startsWith("b"))
			{
				powerFailCreate = true;	//Power Fail Points Will Be Created
			}
			else if( args[i].toLowerCase().startsWith("lg"))
			{
				loadGroupPointCreate = true;	//Load Group Control History Points Will Be Created
			}
            else if( args[i].toLowerCase().startsWith("mct410frozen"))
            {
                mct410FrozenPointCreate = true;   //MCT 410 Missing Points FROZEN Will Be Created
            }
			else if( args[i].toLowerCase().startsWith("mct410"))
			{
				mct410PointCreate = true;	//MCT 410 Missing Points Will Be Created
			}
			else if( args[i].toLowerCase().startsWith("cbop"))
			{
				capBankOpCntPointCreate = true;    //Cap Bank missing Operations Count points will be created
			}
			else if( args[i].toLowerCase().startsWith("o"))
			{
				outageLogCreate = true;    //MCT 410 missing OutageLog points will be created
			}
			else if( args[i].toLowerCase().startsWith("t"))
			{
				tamperFlagCreate = true;    //MCT 410 missing tamper flag points will be created
			}
			else if( args[i].toLowerCase().startsWith("z"))
			{
				zeroUsageFlagCreate = true;    //MCT 410 missing zero usage flag points will be created
			}
			else if( args[i].toLowerCase().startsWith("r"))
			{
				reversePowerFlagCreate = true;    //MCT 410 missing reverse power flag points will be created
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
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for OneDevice_AnalogPointCreate to complete" );
			
		}
		if(loadGroupPointCreate)
		{
			timerStart = new java.util.Date();
			LoadGroup_ControlPointCreate loadGroupControlPointCreate= new LoadGroup_ControlPointCreate();
			loadGroupControlPointCreate.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for LoadGroup_ControlPointCreate to complete" );
			
		}
		if( powerFailCreate )
		{
			timerStart = new java.util.Date();
			PowerFailPointCreate powerFailCreator = new PowerFailPointCreate();
			powerFailCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for PowerFailPointCreate to complete" );
		}
		if( disconnectCreate)
		{
			timerStart = new java.util.Date();
			DisconnectPointCreate disconnectCreator = new DisconnectPointCreate();
			disconnectCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for DisconnectPointCreate to complete" );
		}
		if( mct410PointCreate)
		{
			timerStart = new java.util.Date();
			MCT410AllPointCreate mct410Creator = new MCT410AllPointCreate();
			mct410Creator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for MCT410AllPointCreate to complete" );
		}
        if( mct410FrozenPointCreate)
        {
            timerStart = new java.util.Date();
            MCT410FrozenPointCreate mct410FrozenCreator = new MCT410FrozenPointCreate();
            mct410FrozenCreator.create();
            timerStop = new java.util.Date();
            CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for MCT410FrozenPointCreate to complete" );
        }
		if(capBankOpCntPointCreate)			
		{
			timerStart = new java.util.Date();
			CapBank_OpCntPointCreate capBank_OpCntPointCreator = new CapBank_OpCntPointCreate();
			capBank_OpCntPointCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for CapBankOpCntPointCreate to complete" );
		}	
		if( outageLogCreate)
		{
			timerStart = new java.util.Date();
			OutageLogPointCreate outageLogPointCreator = new OutageLogPointCreate();
			outageLogPointCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for OutageLogPointCreate to complete" );
		}
		if( tamperFlagCreate)
		{
			timerStart = new java.util.Date();
			TamperFlagPointCreate tamperFlagPointCreator = new TamperFlagPointCreate();
			tamperFlagPointCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for TamperFlagPointCreate to complete" );
		}
		if( zeroUsageFlagCreate)
		{
			timerStart = new java.util.Date();
			ZeroUsageFlagPointCreate zeroUsageFlagPointCreator = new ZeroUsageFlagPointCreate();
			zeroUsageFlagPointCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for ZeroUsageFlagPointCreate to complete" );
		}
		if( reversePowerFlagCreate)
		{
			timerStart = new java.util.Date();
			ReversePowerFlagPointCreate reversePowerFlagPointCreator = new ReversePowerFlagPointCreate();
			reversePowerFlagPointCreator.create();
			timerStop = new java.util.Date();
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for ReversePowerFlagCreate to complete" );
		}
		System.exit(0);
	}

	/**
	 * Returns true if the Device is a valid container of the point.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	abstract boolean isDeviceValid(LiteYukonPAObject litePaobject_);
	
	/**
	 * Returns true if the Point already exists for the Device
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	abstract boolean isPointCreated(LitePoint lp);
	
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected Vector<LiteYukonPAObject> getDeviceVector() {
		Vector<LiteYukonPAObject> paobjects = new Vector<LiteYukonPAObject>();
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			List<LiteYukonPAObject> devices = cache.getAllDevices();
			
			for (LiteYukonPAObject litePaobject: devices) {

				if( isDeviceValid(litePaobject) ) {
					
					int deviceDevID = litePaobject.getLiteID();
                    boolean foundPoint = false;
					List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceDevID);
                    for (LitePoint point : points) {
                        if(isPointCreated(point)) {
                           foundPoint = true;
                        }
                    }
                    // make a list of devices to add points to
                    if(!foundPoint) {
                        paobjects.add(litePaobject);
                    }
				}
			}
			CTILogger.info(paobjects.size() + " Total Devices needing points added.");
		} //synch
		return paobjects;
	}
	
	protected boolean writeToSQLDatabase(SmartMultiDBPersistent multi) 
	{
		//write all the collected data to the SQL database
		try
		{
		 	CTILogger.info("Creating Transaction to insert multi");
	      	multi = (SmartMultiDBPersistent)Transaction.createTransaction( Transaction.INSERT, multi).execute();
	
			return true;
		}
		catch( TransactionException t )
		{
			CTILogger.error( t.getMessage(), t );
			return false;
		}
	}
	
}