package com.cannontech.cbc.web;

/**
 * Maintains information from the capcontrol server
 *
 *
 */
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.Timer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CapControlFeeder;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubAreaNames;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.cbc.CBCUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.conns.ConnPool;

public class CapControlCache implements MessageListener, ActionListener
{
	private static final int STARTUP_REF_RATE = 15 * 1000;
	private static final int NORMAL_REF_RATE = 60 * 5 * 1000; //5 minutes
	private Timer refreshTimer = new Timer(STARTUP_REF_RATE, this );
	
	// Map<suBusID(Integer), SubBus>
	private Hashtable subBusMap = new Hashtable();
	// Map<feederID(Integer), Feeder>
	private Hashtable feederMap = new Hashtable();
	// Map<capBankID(Integer), CapBankDevice>
	private Hashtable capBankMap = new Hashtable();

	// Map<subBusID(Integer), capBankIDs(int[])>
	private HashMap subToBankMap = new HashMap();
	// Map<areaName(String), subIDs(NativeIntVector)>
	private HashMap subToAreaMap = new HashMap();


	// Vector:String
	private Vector areaNames = new Vector(16);


/**
 * CapControlCache constructor.
 */
public CapControlCache()
{
	super();

	ConnPool.getInstance().getDefCapControlConn().addMessageListener( this );	
	refreshTimer.setRepeats(true);
	refreshTimer.start();
}

/**
 * Invoked when an action occurs.
 */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	refresh();
}

/**
 * 
 * @return SubBus
 */
public SubBus getSubBus( Integer subID )
{
	return (SubBus)subBusMap.get( subID );
}

/**
 * 
 * @return Feeder
 */
public Feeder getFeeder( Integer feederID )
{
	return (Feeder)feederMap.get( feederID );
}

/**
 * @return CapBankDevice
 */
public CapBankDevice getCapBankDevice( Integer capBankDeviceID )
{
	return (CapBankDevice)capBankMap.get( capBankDeviceID );
}

/**
 * @return Feeder[]
 */
public synchronized Feeder[] getFeedersBySub(Integer subBusID)
{
	SubBus subBus = getSubBus(subBusID);
	Feeder[] retVal = new Feeder[0];
	
	if( subBus != null )
		retVal = (Feeder[])subBus.getCcFeeders().toArray( retVal );		
	
	return retVal;
}

/**
 * @return CapBankDevice[]
 */
public synchronized CapBankDevice[] getCapBanksByFeeder(Integer feederID)
{
	Feeder feeder = getFeeder(feederID);
	CapBankDevice[] retVal = new CapBankDevice[0];
	
	if( feeder != null )
		retVal = (CapBankDevice[])feeder.getCcCapBanks().toArray( retVal );		
	
	return retVal;
}

/**
 * @return CapBankDevice[]
 * @param subBusID long
 */
public synchronized CapBankDevice[] getCapBanksBySub(Integer subBusID)
{
	int[] bankIDs = (int[])subToBankMap.get( subBusID );
	CapBankDevice[] retVal = new CapBankDevice[ bankIDs.length ];
	
	for( int i = 0; i < bankIDs.length; i++ )
		retVal[i] = getCapBankDevice( new Integer(bankIDs[i]) );

	return retVal;
}

/**
 * @return SubBus[]
 * @param String
 */
public synchronized SubBus[] getSubsByArea(String area)
{
	NativeIntVector subIDs = (NativeIntVector)subToAreaMap.get( area );
	SubBus[] retVal = new SubBus[ subIDs.size() ];
	
	for( int i = 0; i < subIDs.size(); i++ )
		retVal[i] = getSubBus( new Integer(subIDs.elementAt(i)) );

	return retVal;
}

/**
 * Find the orphaned CapBanks in the system. This requires a database hit since
 * the CapControl server does not know about these. Store the results locally of
 * this expensive lookup.
 * 
 * @return LiteYukonPAObject[]
 * @param
 */
public synchronized LiteYukonPAObject[] getOrphanedCapBanks()
{
	//hits the DB
	List unassignedBanks = CapBank.getUnassignedCapBanksList();
	LiteYukonPAObject[] retVal = new LiteYukonPAObject[ unassignedBanks.size() ];
	
	for( int i = 0 ; i < unassignedBanks.size(); i++ )
	{
		CapBank capBank = (CapBank)unassignedBanks.get(i);		
		retVal[i] = PAOFuncs.getLiteYukonPAO( capBank.getDeviceID().intValue() );
	}

	return retVal;
}

/**
 * Find the orphaned Feeders in the system. This requires a database hit since
 * the CapControl server does not know about these. Store the results locally of
 * this expensive lookup.
 * 
 * @return LiteYukonPAObject[]
 * @param
 */
public synchronized LiteYukonPAObject[] getOrphanedFeeders()
{
	//hits the DB
	CapControlFeeder[] unassignedFeeders = CapControlFeeder.getUnassignedFeeders();
	LiteYukonPAObject[] retVal = new LiteYukonPAObject[ unassignedFeeders.length ];
	
	for( int i = 0 ; i < unassignedFeeders.length; i++ )
	{
		CapControlFeeder feeder = (CapControlFeeder)unassignedFeeders[i];		
		retVal[i] = PAOFuncs.getLiteYukonPAO( feeder.getFeederID().intValue() );
	}

	return retVal;
}

/**
 * Create an array of SubBuses. Best usage is to store the results of this call
 * instead of repeatingly calling this method. Never returns null.
 * 
 * @return SubBus[]
 * @param
 */
public synchronized SubBus[] getAllSubBuses()
{
	//sort the list base on the names
	SubBus[] subs = new SubBus[ subBusMap.values().size() ];
	subs = (SubBus[])subBusMap.values().toArray( subs );
	Arrays.sort( subs, CBCUtils.SUB_AREA_COMPARATOR );

	return subs;
}

/**
 * Distinct area Strings that are used by substations
 * 
 * @return List
 */
public List getAreaNames()
{
	return areaNames;
}

/**
 * State group & states to use for CapBanks
 * 
 * @return LiteState
 */
public LiteState getCapBankState( int rawState )
{
	return StateFuncs.getLiteState( StateGroupUtils.STATEGROUPID_CAPBANK, rawState );
}


/**
* Stores all areas in memory. We need to ensure that the areas we make available
* have at least 1 subbus. We derive the current areas from the message and the
* current SubBuses
* 
* @param CBCSubAreaNames
*/
private synchronized void handleAreaList(CBCSubAreaNames areaNames_) 
{
	// remove all the values in the list
	areaNames.removeAllElements();
	
	//create the temporary map to have the needed areas in it, use a map to ensure we
	// only have 1 area string for each area, better performance than list.contains()
	SubBus[] subBuses = getAllSubBuses();
	HashMap areaMap = new HashMap( areaNames_.getNumberOfAreas() );
	for( int i = 0; i < subBuses.length; i++ )
		areaMap.put( subBuses[i].getCcArea(), "temp" );

	// add all area names to the list
	for( int i = 0; i < areaNames_.getNumberOfAreas(); i++ )
		if( areaMap.containsKey(areaNames_.getAreaName(i)) )
			areaNames.add( areaNames_.getAreaName(i) );
}

/**
 * Removes this subbus from all the structures in cache. 
 * @param msg
 */
private void handleDeletedSubs( CBCSubstationBuses msg )
{
	Vector deleteSubs = new Vector( msg.getNumberOfBuses() );
	
	for( int i = 0; i < msg.getNumberOfBuses(); i++ )
	{
		subBusMap.remove( msg.getSubBusAt(i).getCcId() );
		subToBankMap.remove( msg.getSubBusAt(i).getCcId() );


		//remove mapping of subs to areas
		NativeIntVector subIDs =
			(NativeIntVector)subToAreaMap.get( msg.getSubBusAt(i).getCcArea() );

		subIDs.removeElement( msg.getSubBusAt(i).getCcId().intValue() );
		if( subIDs.isEmpty() )
			subToAreaMap.remove( msg.getSubBusAt(i).getCcArea() );
			
	}

}

/**
 * Process multiple SubBuses
 * @param busesMsg
 */
private void handleSubBuses( CBCSubstationBuses busesMsg )
{
	for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- )
	{
		CTILogger.debug(
				new ModifiedDate(new Date().getTime()).toString()
				+ " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
				+ "/" + busesMsg.getSubBusAt(i).getCcArea() );

		//if the user can not see this sub, let us remove it
		//if( !AuthFuncs.userHasAccessPAO( ownerUser, busesMsg.getSubBusAt(i).getCcId().intValue() ) )
			//busesMsg.removeSubBusAt( i );
	}


	//process the subs that need to be removed
	if( busesMsg.isSubDeleted() )
		handleDeletedSubs( busesMsg );


	//add the each subbus to the cache
	for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ )
		handleSubBus( busesMsg.getSubBusAt(i) );
}

/**
 * Processes a single SubBus, breaking it up into Feeders and Capbanks
 * @param SubBus
 */
private synchronized void handleSubBus( SubBus subBus ) 
{	
	subBusMap.put( subBus.getCcId(), subBus );
	Vector feeders = subBus.getCcFeeders();

	NativeIntVector capBankIDs = new NativeIntVector(32);
	for( int i = 0; i < feeders.size(); i++ )
	{		
		Feeder feeder = (Feeder)feeders.elementAt(i);
		feederMap.put( feeder.getCcId(), feeder );
		
		for( int j = 0; j < feeder.getCcCapBanks().size(); j++ )
		{
			CapBankDevice capBank = (CapBankDevice)feeder.getCcCapBanks().get(j);
			capBankMap.put( capBank.getCcId(), capBank );

			capBankIDs.add( capBank.getCcId().intValue() );
		}
	}
	
	//map all capbanks to their parent SubBus
	subToBankMap.put( subBus.getCcId(), capBankIDs.toArray() );


	//map all SubBuses to their parent Area
	NativeIntVector subIDs = null;
	if( (subIDs = (NativeIntVector)subToAreaMap.get(subBus.getCcArea())) != null )
		subIDs.add( subBus.getCcId().intValue() );
	else
	{
		subIDs = new NativeIntVector(32);
		subIDs.add( subBus.getCcId().intValue() );
		subToAreaMap.put( subBus.getCcArea(), subIDs );
	}

}

/**
 * Allows access the a CBCClientConnection instance
 * @return
 */
protected CBCClientConnection getConnection()
{
	return (CBCClientConnection)ConnPool.getInstance().getDefCapControlConn();
}

/**
 * Renew the cache.
 * Creation date: (6/11/2001 3:36:24 PM)
 */
public synchronized void refresh()
{
	CTILogger.debug("Refreshing CapControl Cache");
	
	try  {
		getConnection().executeCommand( 0, CBCCommand.REQUEST_ALL_SUBS );
	} catch ( IOException ioe ) {
		CTILogger.error( "Exception occured during a refresh_cache operation", ioe );
	}

	refreshTimer.setDelay(NORMAL_REF_RATE);
}

/**
 * Handles any type of message recieved from the connection.
 * 
 * @param MessageEvent
 */
public void messageReceived( MessageEvent e )
{
	Message in = e.getMessage();

	if( in instanceof CBCSubstationBuses )
	{
		handleSubBuses( (CBCSubstationBuses)in );
	}
	else if( in instanceof CBCSubAreaNames )
	{
		handleAreaList( (CBCSubAreaNames)in );
	}

}



}