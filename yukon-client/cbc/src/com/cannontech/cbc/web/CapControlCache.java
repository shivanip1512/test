package com.cannontech.cbc.web;

/**
 * Maintains information from the capcontrol server
 *
 *
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CapControlFeeder;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubAreaNames;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.cbc.CBCUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.conns.ConnPool;

public class CapControlCache implements MessageListener, CapControlDAO
{
	private static final int STARTUP_REF_RATE = 15 * 1000;
	private static final int NORMAL_REF_RATE = 5 * 60* 1000; //5 minutes
	private ScheduledExecutor refreshTimer = YukonSpringHook.getGlobalExecutor();
	
	// Map<suBusID(Integer), sub(SubBus)>
	private Hashtable subBusMap = new Hashtable();
	// Map<feederID(Integer), feeder(Feeder)>
	private Hashtable feederMap = new Hashtable();
	// Map<capBankID(Integer), capBank(CapBankDevice)>
	private Hashtable capBankMap = new Hashtable();

	// Map<subBusID(Integer), capBankIDs(int[])>
	private HashMap subToBankMap = new HashMap();
	// Map<areaName(String), subIDs(NativeIntVector)>
	private HashMap subIDToAreaMap = new HashMap();
	
//	private LiteYukonUser yukonUser = null;
	
	private CBCWebUpdatedObjectMap updatedObjMap = null;


	// Vector:String
	private Vector areaNames = new Vector(16);

//Map<areaName(String)>, <state (Boolean)>

    private HashMap areaStateMap = new HashMap();
    /**
 * CapControlCache constructor.
 */
public CapControlCache()
{
	super();
    
    Runnable task = new Runnable() {
        public void run() {
            refresh();
        }
    };

	ConnPool.getInstance().getDefCapControlConn().addMessageListener( this );	
	refreshTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, NORMAL_REF_RATE, TimeUnit.MILLISECONDS);
}

/**
 * 
 * @return SubBus
 */
public synchronized SubBus  getSubBus( Integer subID )
{
	return (SubBus)subBusMap.get( subID );
}

/**
 * Returs the base object type for a SubBus, Feeder or CapBankDevice
 * 
 */
public StreamableCapObject getCapControlPAO( Integer paoID )
{
	StreamableCapObject retObj = null;

	if( retObj == null ) retObj = getSubBus(paoID);
	if( retObj == null ) retObj = getFeeder(paoID);
	if( retObj == null ) retObj = getCapBankDevice(paoID);
	
	return retObj;
}

/* (non-Javadoc)
 * @see com.cannontech.cbc.web.CapControlDAO#getFeeder(java.lang.Integer)
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
 * Instant lookup to check if this paoID is used by a SubBus
 * 
 */
public boolean isSubBus( int id )
{
	return getSubBus( new Integer(id) ) != null;
}

/**
 * Instant lookup to check if this paoID is used by a Feeder
 * 
 */
public boolean isFeeder( int id )
{
	return getFeeder( new Integer(id) ) != null;
}

/**
 * Instant lookup to check if this paoID is used by a CapBankDevice
 * 
 */
public boolean isCapBank( int id )
{
	return getCapBankDevice( new Integer(id) ) != null;
}


/**
 * @return CapBankDevice[]
 * @param subBusID long
 */
public synchronized CapBankDevice[] getCapBanksBySub(Integer subBusID)
{
	int[] bankIDs = (int[])subToBankMap.get( subBusID );
	if( bankIDs == null )
		bankIDs = new int[0];

	CapBankDevice[] retVal = new CapBankDevice[ bankIDs.length ];
	
	for( int i = 0; i < bankIDs.length; i++ )
		retVal[i] = getCapBankDevice( new Integer(bankIDs[i]) );

	return retVal;
}

/* (non-Javadoc)
 * @see com.cannontech.cbc.web.CapControlDAO#getSubsByArea(java.lang.String)
 */
public synchronized SubBus[] getSubsByArea(String area)
{
	NativeIntVector subIDs = (NativeIntVector)subIDToAreaMap.get( area );
	if( subIDs == null )
		subIDs = new NativeIntVector();

	SubBus[] retVal = new SubBus[ subIDs.size() ];
	
	for( int i = 0; i < subIDs.size(); i++ )
		retVal[i] = getSubBus( new Integer(subIDs.elementAt(i)) );

	//before returning, sort our SubBuses based on the name
	Arrays.sort( retVal, CBCUtils.CCNAME_COMPARATOR );

	return retVal;
}

/**
 * Returns all CapBanks for a given Area
 * 
 */
public synchronized CapBankDevice[] getCapBanksByArea(String area)
{
	SubBus[] subs = getSubsByArea( area );
	if( subs == null )
		subs = new SubBus[0];

	Vector allBanks = new Vector(64);
	for( int i = 0; i < subs.length; i++ )
	{
		CapBankDevice[] capBanks = getCapBanksBySub( subs[i].getCcId() );		
		allBanks.addAll( Arrays.asList(capBanks) );
	}

	Collections.sort( allBanks, CBCUtils.CCNAME_COMPARATOR );
	return (CapBankDevice[])allBanks.toArray( new CapBankDevice[allBanks.size()]);
}

/**
 * Returns all Feeders for a given Area
 * 
 */
public synchronized Feeder[] getFeedersByArea(String area)
{
	SubBus[] subs = getSubsByArea( area );
	if( subs == null )
		subs = new SubBus[0];

	Vector allFeeders = new Vector(64);
	for( int i = 0; i < subs.length; i++ )
	{
		Feeder[] feeders = getFeedersBySub( subs[i].getCcId() );		
		allFeeders.addAll( Arrays.asList(feeders) );
	}

	Collections.sort( allFeeders, CBCUtils.CCNAME_COMPARATOR );
	return (Feeder[])allFeeders.toArray( new Feeder[allFeeders.size()]);
}

/**
 * Find all the orphaned CBC's in the system. This requires a database hit since
 * the CapControl server does not know about these.
 * 
 */
public synchronized LiteWrapper[] getOrphanedCBCs()
{
	//hits the DB
	int[] unassignedCBCsIds = CapBankController.getUnassignedDeviceCBCIds();
	LiteWrapper[] retVal = new LiteWrapper[ unassignedCBCsIds.length ];
	
	for( int i = 0 ; i < unassignedCBCsIds.length; i++ ) {
		retVal[i] = new LiteWrapper(
				DaoFactory.getPaoDao().getLiteYukonPAO(unassignedCBCsIds[i]) );
	}

	return retVal;
}

/**
 * Find the orphaned CapBanks in the system. This requires a database hit since
 * the CapControl server does not know about these.
 * 
 */
public synchronized LiteWrapper[] getOrphanedCapBanks()
{
	//hits the DB
	CapBank[] unassignedBanks = CapBank.getUnassignedCapBanksList();
	LiteWrapper[] retVal = new LiteWrapper[ unassignedBanks.length ];
	
	for( int i = 0 ; i < unassignedBanks.length; i++ )
	{
		CapBank capBank = unassignedBanks[i];		
		retVal[i] = new LiteWrapper(
				DaoFactory.getPaoDao().getLiteYukonPAO(capBank.getDeviceID().intValue()) );
	}
    Arrays.sort(retVal, LiteComparators.liteNameComparator);
	return retVal;
}

/**
 * Find the orphaned Feeders in the system. This requires a database hit since
 * the CapControl server does not know about these.
 * 
 */
public synchronized LiteWrapper[] getOrphanedFeeders()
{
	//hits the DB
	CapControlFeeder[] unassignedFeeders = CapControlFeeder.getUnassignedFeeders();
	LiteWrapper[] retVal = new LiteWrapper[ unassignedFeeders.length ];
	
	for( int i = 0 ; i < unassignedFeeders.length; i++ )
	{
		CapControlFeeder feeder = unassignedFeeders[i];		
		retVal[i] = new LiteWrapper(
				DaoFactory.getPaoDao().getLiteYukonPAO(feeder.getFeederID().intValue()) );
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
	return DaoFactory.getStateDao().getLiteState( StateGroupUtils.STATEGROUPID_CAPBANK, rawState );
}

/**
 * Returns the Parent SubBus ID for the given child id
 */
public int getParentSubBusID( int childID ) {

	if( isSubBus(childID) )
		return childID;
	else if( isFeeder(childID) )
		return getFeeder(new Integer(childID)).getParentID();
	else if( isCapBank(childID) )
		return getFeeder(
			new Integer(getCapBankDevice(
				new Integer(childID)).getParentID())).getParentID();
	else
		return CtiUtilities.NONE_ZERO_ID;
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

	//add all area names to the list
	Iterator it = areaMap.keySet().iterator();
	while( it.hasNext() ) {
		String area = (String) it.next();
        areaNames.add( area );
	}
	
	//before returning, sort our Areas based on the name
	Collections.sort( areaNames );
    //add the new area to the area state map
    resetAreaStateMap();
}


/**
 * Removes this subbus from all the structures in cache. 
 * @param msg
 */
private void handleDeletedSubs( int itemID )
{
	Integer id = new Integer(itemID);
	String area = getSubBus(id).getCcArea();

	subBusMap.remove( id );
	subToBankMap.remove( id );


	//remove mapping of subs to areas by subId
	NativeIntVector subIDs =
		(NativeIntVector)subIDToAreaMap.get( area );
	subIDs.removeElement( id.intValue() );
	if( subIDs.isEmpty() )
		subIDToAreaMap.remove( area );
}

/**
 * Process multiple SubBuses
 * @param busesMsg
 * @param owner TODO
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
		//if( !DaoFactory.getAuthDao().userHasAccessPAO( ownerUser, busesMsg.getSubBusAt(i).getCcId().intValue() ) )
			//busesMsg.removeSubBusAt( i );
	}


	//add the each subbus to the cache
	for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ )
		handleSubBus( busesMsg.getSubBusAt(i) );
	
    handleAreaStateMap();
}

/**
 * Process a command message from the server
 */
private void handleCBCCommand( CBCCommand cbcCmd ) {

	switch( cbcCmd.getCommand() ) {

		//delete the given subID
		case CBCCommand.DELETE_ITEM:
			if( isSubBus(cbcCmd.getDeviceID()) )
				handleDeletedSubs( cbcCmd.getDeviceID() );
	}

}

/**
 * Processes a single SubBus, breaking it up into Feeders and Capbanks
 * @param SubBus
 */
private synchronized void handleSubBus( SubBus subBus ) 
{	
	Validate.notNull(subBus, "subBus can't be null");
	//remove the old subBus from the area hashmap just in case the area changed
	removeSubIDToAreaMap( subBus.getCcId() );

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

	addSubIDToAreaMap( subBus );
	//server side update to the objMap
	getUpdatedObjMap().handleCBCChangeEvent(subBus, new Date());
}


/**
 * Adds or replaces and element inside a hashmap
 * @param subBus
 */
private void addSubIDToAreaMap( final SubBus subBus )
{
	//map all SubBuses to their parent Area by subID
	NativeIntVector subIDs = null;
	if( (subIDs = (NativeIntVector)subIDToAreaMap.get(subBus.getCcArea())) != null )		
	{
		int indx = subIDs.indexOf( subBus.getCcId().intValue() );
		if( indx >= 0 )
			subIDs.setElementAt(subBus.getCcId().intValue(), indx);
		else
			subIDs.add( subBus.getCcId().intValue() );
	}		
	else
	{
		subIDs = new NativeIntVector(32);
		subIDs.add( subBus.getCcId().intValue() );
		subIDToAreaMap.put( subBus.getCcArea(), subIDs );
	}
}


/**
 * Removes an element inside the subIDtoArea hashmap
 * @param subBus
 */
private void removeSubIDToAreaMap( Integer subID )
{
	SubBus subBus = getSubBus( subID );
	if( subBus == null ) return;

	NativeIntVector subIDs = null;
	if( (subIDs = (NativeIntVector)subIDToAreaMap.get(subBus.getCcArea())) != null )		
	{
		int indx = subIDs.indexOf( subBus.getCcId().intValue() );
		if( indx >= 0 )
			subIDs.remove( indx );
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
}

/**
 * Handles any type of message recieved from the connection.
 * 
 * @param MessageEvent
 */
public void messageReceived( MessageEvent e )
{
	Message in = e.getMessage();

	if( in instanceof CBCSubstationBuses ) {
		handleSubBuses( (CBCSubstationBuses)in );
	}
	else if( in instanceof CBCSubAreaNames ) {
		handleAreaList( (CBCSubAreaNames)in );
	}	
	else if( in instanceof CBCCommand ) {		
		handleCBCCommand( (CBCCommand)in );
	}

}
/**
 * 
 * @param id - Point Id
 * @return String containing parent names for Point Id
 * @comments the fact that it is a point is checked on the front end    
 */

public String getParentNames(int id){
    if (isCapBank(id)){
        CapBankDevice cb = getCapBankDevice(new Integer(id));
        return getParentNames(cb.getParentID()) + ":" + cb.getCcName();
    }
    else if (isFeeder(id)){
        Feeder f = getFeeder(new Integer(id));
        return getParentNames(f.getParentID()) + ":" + f.getCcName();
    }
    else if (isSubBus(id)){
        SubBus sb = getSubBus(new Integer(id));
        return sb.getCcName();
    }
    else {
        return "Invalid device id passed - CapControl Search"; 
    }
 }

public CBCWebUpdatedObjectMap getUpdatedObjMap() {
	if (updatedObjMap == null)
		updatedObjMap = new CBCWebUpdatedObjectMap();
	return updatedObjMap;
}

public HashMap getAreaStateMap() {    
    if (areaStateMap.isEmpty()) { //init the our map
        initAreaStateMap();
    }
    return areaStateMap;
}

private void initAreaStateMap() {
    List areaNames = getAreaNames();
    for (Iterator iter = areaNames.iterator(); iter.hasNext();) {
        String area = (String) iter.next();
        areaStateMap.put(area, Boolean.FALSE);
        SubBus[] subs = getSubsByArea(area);
        for (int i = 0; i < subs.length; i++) {
            SubBus bus = subs[i];
            if (!bus.getCcDisableFlag()) {
                areaStateMap.put(area, Boolean.TRUE);
                break;
            }
        }
    }
}

private void handleAreaStateMap() {
HashMap map = getAreaStateMap();
    for (Iterator iter = areaNames.iterator(); iter.hasNext();) {
        String area = (String) iter.next();
        Boolean isAreaEnabled = (Boolean) map.get(area);
        SubBus[] subs = getSubsByArea(area);
        if (!isAreaEnabled)
            handleAreaDisabled (area, subs);        
        else
            handleAreaEnabled (area, subs);
    }
}
//state will only change if all subs in area are disabled
private void handleAreaEnabled(String area, SubBus[] subs) {
    boolean disableArea = true;
    for (int i = 0; i < subs.length; i++) {
        SubBus bus = subs[i];
        disableArea = bus.getCcDisableFlag() && disableArea;             
    }
    getAreaStateMap().put(area, !disableArea);
}

//state will change if at least 1 sub is enabled
private void handleAreaDisabled(String area, SubBus[] subs) {
    for (int i = 0; i < subs.length; i++) {
        SubBus bus = subs[i];
        if (!bus.getCcDisableFlag())
        {
            getAreaStateMap().put(area, Boolean.TRUE);
            break;
        }
    }
}

private void resetAreaStateMap() {
    HashMap map = getAreaStateMap();
    map = new HashMap();
    initAreaStateMap();
}


}