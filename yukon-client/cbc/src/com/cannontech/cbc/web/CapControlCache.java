package com.cannontech.cbc.web;

/**
 * Maintains information from the capcontrol server
 *
 *
 */
import java.io.IOException;
import java.util.ArrayList;
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
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CapControlFeeder;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubAreas;
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
    private static final int NORMAL_REF_RATE = 5 * 60 * 1000; //5 minutes
    private ScheduledExecutor refreshTimer = YukonSpringHook.getGlobalExecutor();
    
    // Map<suBusID(Integer), sub(SubBus)>
    private Hashtable subBusMap = new Hashtable();
    // Map<feederID(Integer), feeder(Feeder)>
    private Hashtable feederMap = new Hashtable();
    // Map<capBankID(Integer), capBank(CapBankDevice)>
    private Hashtable capBankMap = new Hashtable();

    // Map<subBusID(Integer), capBankIDs(int[])>
    private HashMap subToBankMap = new HashMap();
    // Map<areaName(Integer), subIDs(NativeIntVector)>
    //private HashMap subIDToAreaMap = new HashMap();
    private CBCWebUpdatedObjectMap updatedObjMap = null;

    // Vector:CBCArea
    private Vector cbcAreas = new Vector();

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
public  SubBus[] getSubsByArea(Integer areaID)
{
    List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubs(areaID);
    List<Integer>intList = CCSubAreaAssignment.getAsIntegerList(allAreaSubs);
    //SubBus[] subs = new SubBus[intList.size()];
    List<SubBus> subList = new ArrayList<SubBus>();
    synchronized (subList)
    {
        for (Integer id : intList) {
            SubBus sub = (SubBus) subBusMap.get(id);
            if (sub != null)
            {
                subList.add(sub);
            }
        }
    }
    SubBus[] subs = subList.toArray(new SubBus[]{});
    //before returning, sort our SubBuses based on the name
    Arrays.sort( subs, CBCUtils.CCNAME_COMPARATOR );

    return subs;


}

/**
 * Returns all CapBanks for a given Area
 * 
 */
public synchronized CapBankDevice[] getCapBanksByArea(Integer areaID)
{
    SubBus[] subs = getSubsByArea( areaID );
    if( subs == null )
        subs = new SubBus[0];

    Vector allBanks = new Vector(64);
    for( int i = 0; i < subs.length; i++ )
    {
        SubBus subBus = subs[i];
        if (subBus != null)
        {
            CapBankDevice[] capBanks = getCapBanksBySub( subBus.getCcId() );        
            allBanks.addAll( Arrays.asList(capBanks) );
        }
    }

    Collections.sort( allBanks, CBCUtils.CCNAME_COMPARATOR );
    return (CapBankDevice[])allBanks.toArray( new CapBankDevice[allBanks.size()]);
}

/**
 * Returns all Feeders for a given Area
 * 
 */
public synchronized Feeder[] getFeedersByArea(Integer areaID)
{
    SubBus[] subs = getSubsByArea( areaID);
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

public synchronized LiteWrapper[] getOrphanedSubstations()
{
    //hits the DB
    List<Integer> allUnassignedBuses = CapControlSubBus.getAllUnassignedBuses();
    LiteWrapper[] retVal = new LiteWrapper[ allUnassignedBuses.size() ];
    for (Integer id : allUnassignedBuses) {
        retVal[allUnassignedBuses.indexOf(id)] = new LiteWrapper(
            DaoFactory.getPaoDao().getLiteYukonPAO(id) );
                        
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
public List getCbcAreas()
{
    return cbcAreas;
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
* Stores all areas in memory. 
* 
* @param CBCSubAreas
*/
private synchronized void handleAreaList(CBCSubAreas areas) 
{
    //for every area in the currently in cache
    List<CBCArea> cachedAreas = getCbcAreas();
    List<CBCArea> workCopy = new Vector<CBCArea>();
    workCopy.addAll(cachedAreas);
    for (CBCArea area : cachedAreas) {
        Vector allAreas = areas.getAreas();
        //if current cache has an area and sent object doesn't 
        //discard the cached object
        if (!allAreas.contains(area))
        {
            workCopy.remove(area);
        }
        //if the object is cached currently - then replace the object
        else if (allAreas.contains(area))
        {
            int idx = workCopy.indexOf(area);
            workCopy.set(idx, (CBCArea) allAreas.get( allAreas.indexOf(area)));
        }
        
    }
    cachedAreas.clear();
    cachedAreas.addAll(workCopy);
    //if the object is not in cache add it to cache
    Vector<CBCArea> sentAreas = areas.getAreas();
    for (CBCArea area : sentAreas) {
        if (!cachedAreas.contains(area))
        {
            cachedAreas.add(area);
        }
    }
    

    Collections.sort(cbcAreas, CBCUtils.CBC_AREA_COMPARATOR);

    resetAreaStateMap();
}


/**
 * Removes this subbus from all the structures in cache. 
 * @param msg
 */
private void handleDeletedSub( int itemID )
{   
    Integer id = new Integer(itemID);
    subBusMap.remove( id );
    subToBankMap.remove( id );

}



/**
 * Process multiple SubBuses
 * @param busesMsg
 * @param owner TODO
 */
private void handleSubBuses( CBCSubstationBuses busesMsg )
{
    logAllSubs(busesMsg);
    //If this is a full reload of all subs.
	if (busesMsg.isAllSubs())
    {
	    clearAllMaps();
    }
    else if( busesMsg.isUpdateSub()){
    	//If this is an update to an existing sub.
        handleDeletedSubs(busesMsg);
    }
    //add the each subbus to the cache
    handleAllSubs(busesMsg);
}

private void handleDeletedSubs(CBCSubstationBuses busesMsg) {
    for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ){
    	//remove old
    	handleDeletedSub( busesMsg.getSubBusAt(i).getCcId() );
    }
}

private void logAllSubs(CBCSubstationBuses busesMsg) {
    for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- ){
    	CTILogger.debug(
    			new ModifiedDate(new Date().getTime()).toString()
    			+ " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
    			+ "/" + busesMsg.getSubBusAt(i).getCcArea() );
    }
}

private void handleAllSubs(CBCSubstationBuses busesMsg) {
    for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ )
    	handleSubBus( busesMsg.getSubBusAt(i) );
}

/**
 * Process a command message from the server
 */
private void handleCBCCommand( CBCCommand cbcCmd ) {

    switch( cbcCmd.getCommand() ) {

        //delete the given subID
        case CBCCommand.DELETE_ITEM:
            if( isSubBus(cbcCmd.getDeviceID()) )
                handleDeletedSub( cbcCmd.getDeviceID() );
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
    subBusMap.remove(subBus.getCcId());
    subBusMap.put( subBus.getCcId(), subBus );
    Vector feeders = subBus.getCcFeeders();

    NativeIntVector capBankIDs = new NativeIntVector(32);
    for( int i = 0; i < feeders.size(); i++ )
    {       
        Feeder feeder = (Feeder)feeders.elementAt(i);
        feederMap.remove(feeder.getCcId());
        feederMap.put( feeder.getCcId(), feeder );
        
        for( int j = 0; j < feeder.getCcCapBanks().size(); j++ )
        {
            CapBankDevice capBank = (CapBankDevice)feeder.getCcCapBanks().get(j);
            capBankMap.remove(capBank.getCcId());
            capBankMap.put( capBank.getCcId(), capBank );
            capBankIDs.add( capBank.getCcId().intValue() );
        }
    }
    
    subToBankMap.remove(subBus.getCcId());
    //map all capbanks to their parent SubBus
    subToBankMap.put( subBus.getCcId(), capBankIDs.toArray() );

    //server side update to the objMap
    getUpdatedObjMap().handleCBCChangeEvent(subBus, new Date());
}


/**
 * Adds or replaces and element inside a hashmap
 * @param subBus
 */



/**
 * Removes an element inside the subIDtoArea hashmap
 * @param subBus
 */


private synchronized void clearAllMaps() {
    subBusMap.clear();
    feederMap.clear();
    capBankMap.clear();
    subToBankMap.clear();
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
public synchronized boolean refresh()
{
    CTILogger.debug("Refreshing CapControl Cache");
    
    try  {
        getConnection().executeCommand( 0, CBCCommand.REQUEST_ALL_AREAS );
    } catch ( IOException ioe ) {
        CTILogger.error( "Exception occured during a refresh_cache operation", ioe );
        return false;
    }
    return true;
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
    else if( in instanceof CBCSubAreas ) {
        handleAreaList( (CBCSubAreas)in );
    }   
    else if( in instanceof CBCCommand ) {       
        handleCBCCommand( (CBCCommand)in );
    }

}


public CBCArea getCBCArea(int id) {
  for (Iterator iter = cbcAreas.iterator(); iter.hasNext();) {
    CBCArea area = (CBCArea) iter.next();
    if (area.getPaoID().intValue() == id)
    {
        return area;
    }
  }
  return null;
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
    List areaNames = getCbcAreas();
    for (Iterator iter = areaNames.iterator(); iter.hasNext();) {
        CBCArea area = (CBCArea) iter.next();
        areaStateMap.put(area.getPaoName(), !area.getDisableFlag());

    }
}


private void resetAreaStateMap() {
    areaStateMap = new HashMap();
    getAreaStateMap();
}




}