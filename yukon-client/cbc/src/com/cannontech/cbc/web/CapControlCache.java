package com.cannontech.cbc.web;

/**
 * Maintains information from the capcontrol server
 */
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.*;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.capcontrol.*;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.util.*;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.cbc.*;
import com.cannontech.yukon.conns.ConnPool;

public class CapControlCache implements MessageListener, CapControlDAO {
    private static final int STARTUP_REF_RATE = 15 * 1000;
    private static final int NORMAL_REF_RATE = 30 * 60 * 1000; //5 minutes
    private ScheduledExecutor refreshTimer;
    private Hashtable<Integer, SubStation> subStationMap = new Hashtable<Integer, SubStation>();
    private Hashtable<Integer, SubBus> subBusMap = new Hashtable<Integer, SubBus>();
    private Hashtable<Integer, Feeder>  feederMap = new Hashtable<Integer, Feeder>();
    private Hashtable<Integer, CapBankDevice> capBankMap = new Hashtable<Integer, CapBankDevice> ();
    private HashMap<Integer, int[]> subToBankMap = new HashMap<Integer, int[]>();
    private CBCWebUpdatedObjectMap updatedObjMap = null;
    private List<CBCArea> cbcAreas = new ArrayList<CBCArea>();
    private List<CBCSpecialArea> cbcSpecialAreas = new ArrayList<CBCSpecialArea>();
    private HashMap<String, Boolean> areaStateMap = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> specialAreaStateMap = new HashMap<String, Boolean>();
    private Boolean systemStatusOn = Boolean.TRUE;
    private IServerConnection defCapControlConn;
    
    /**
     * CapControlCache constructor.
     */
    public CapControlCache() {
        super();
    }
    
    public void initialize() {
        Runnable task = new Runnable() {
            public void run() {
                refresh();
            }
        };
        defCapControlConn.addMessageListener( this );   
        refreshTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, NORMAL_REF_RATE, TimeUnit.MILLISECONDS);
    }
    
    /**
     * @return SubBus
     */
    public synchronized SubBus  getSubBus( Integer subID ) {
        return subBusMap.get( subID );
    }
    
    /**
     * @return SubBus
     */
    public synchronized SubStation getSubstation( Integer subId) {
        return subStationMap.get( subId );
    }
    
    /**
     * @return Area
     */
    public synchronized CBCArea getArea( Integer areaId )
    {
    	for ( CBCArea a : cbcAreas )
    	{
    		if( a.getCcId() == areaId )
    		{
    			return a;
    		}
    	}
    	return null;
    }
    /**
     * Returs the base object type for a SubBus, Feeder or CapBankDevice
     */
    public StreamableCapObject getCapControlPAO( Integer paoID ) {
        StreamableCapObject retObj = null;
    
        if( retObj == null ) retObj = getSubBus(paoID);
        if( retObj == null ) retObj = getFeeder(paoID);
        if( retObj == null ) retObj = getCapBankDevice(paoID);
        
        return retObj;
    }
    
    public Feeder getFeeder( Integer feederID ) {
        return feederMap.get( feederID );
    }
    
    /**
     * @return CapBankDevice
     */
    public CapBankDevice getCapBankDevice( Integer capBankDeviceID ) {
        return capBankMap.get( capBankDeviceID );
    }
    
    /**
     * @return Feeder[]
     */
    public synchronized Feeder[] getFeedersBySubBus(Integer subBusID) {
        SubBus subBus = getSubBus(subBusID);
        Feeder[] retVal = new Feeder[0];
        
        if( subBus != null ) {
            retVal = subBus.getCcFeeders().toArray( retVal );
        }
        return retVal;
    }
    
    /**
     * @return List<Feeder>
     */
    public synchronized List<Feeder> getFeedersBySubStation(SubStation sub) {
        
        int[] subBusIds = sub.getSubBusIds();
        List<Feeder> feeders = new ArrayList<Feeder>();
        for(int i = 0; i < subBusIds.length; i++ ) {
            SubBus subBus = getSubBus(subBusIds[i]);
            if( subBus != null ) {
                feeders.addAll(subBus.getCcFeeders());
            }
        }
        return feeders;
    }
    
    /**
     * @return List<CapBankDevice>
     */
    public synchronized List<CapBankDevice> getCapBanksBySubStation(SubStation sub) {
        
        int[] subBusIds = sub.getSubBusIds();
        List<CapBankDevice> capBanks = new ArrayList<CapBankDevice>();
        for(int i = 0; i < subBusIds.length; i++ ) {
            SubBus subBus = getSubBus(subBusIds[i]);
            if( subBus != null ) {
                Vector<Feeder> feeders = subBus.getCcFeeders();
                for(Feeder feeder: feeders) {
                    Vector<CapBankDevice> caps = feeder.getCcCapBanks();
                    capBanks.addAll(caps);
                }
            }
        }
        return capBanks;
    }
    
    /**
     * @return CapBankDevice[]
     */
    public synchronized CapBankDevice[] getCapBanksByFeeder(Integer feederID) {
        Feeder feeder = getFeeder(feederID);
        CapBankDevice[] retVal = new CapBankDevice[0];
        
        if( feeder != null ) {
            retVal = feeder.getCcCapBanks().toArray( retVal );
        }
        return retVal;
    }
    
    /**
     * Instant lookup to check if this paoID is used by a SubBus
     * 
     */
    public boolean isArea( int id ) {
        return getArea( new Integer(id) ) != null;
    }    
    /**
     * Instant lookup to check if this paoID is used by a SubBus
     * 
     */
    public boolean isSubBus( int id ) {
        return getSubBus( new Integer(id) ) != null;
    }
    /**
     * Instant lookup to check if this paoID is used by a SubBus
     * 
     */
    public boolean isSubstation( int id ) {
        return getSubstation( new Integer(id) ) != null;
    }    
    /**
     * Instant lookup to check if this paoID is used by a Feeder
     * 
     */
    public boolean isFeeder( int id ) {
        return getFeeder( new Integer(id) ) != null;
    }
    
    /**
     * Instant lookup to check if this paoID is used by a CapBankDevice
     * 
     */
    public boolean isCapBank( int id ) {
        return getCapBankDevice( new Integer(id) ) != null;
    }
    
    
    /**
     * @return CapBankDevice[]
     * @param subBusID long
     */
    public synchronized CapBankDevice[] getCapBanksBySub(Integer subBusID)
    {
        int[] bankIDs = subToBankMap.get( subBusID );
        if( bankIDs == null ) {
            bankIDs = new int[0];
        }
        CapBankDevice[] retVal = new CapBankDevice[ bankIDs.length ];
        
        for( int i = 0; i < bankIDs.length; i++ ) {
            retVal[i] = getCapBankDevice( new Integer(bankIDs[i]) );
        }
        return retVal;
    }
    
    /**
     * This method will return a list of assigned subs
     * for Areas or SpecialAreas.
     * @return SubBus[]
     * @param areaId Integer
     */
    public  SubBus[] getSubBusesByArea(Integer areaId) {
        if(getCBCArea(areaId) != null) {
            List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubs(areaId);
            List<Integer>intList = CCSubAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubBus> subList = new ArrayList<SubBus>();
            for (Integer id : intList) {
                SubBus sub = subBusMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            SubBus[] subs = subList.toArray(new SubBus[]{});
            Arrays.sort( subs, CBCUtils.CCNAME_COMPARATOR );
            return subs;
        }else if(getCBCSpecialArea(areaId) != null) {
            List<CCSubSpecialAreaAssignment> allAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaId);
            List<Integer>intList = CCSubSpecialAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubBus> subList = new ArrayList<SubBus>();
            for (Integer id : intList) {
                SubBus sub = subBusMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            SubBus[] subs = subList.toArray(new SubBus[]{});
            Arrays.sort( subs, CBCUtils.CCNAME_COMPARATOR );
            return subs;
        }else {
            return null;
        }
    }
    
    public List<SubBus> getSubBusesBySubStation(SubStation sub){
        List<SubBus> buses = new ArrayList<SubBus>();
        int[] busIds = sub.getSubBusIds();
        for(int i = 0; i < busIds.length; i++) {
            buses.add(subBusMap.get(busIds[i]));
        }
        return buses;
    }
    
    /**
     * Returns all CapBanks for a given Area
     */
    public synchronized CapBankDevice[] getCapBanksByArea(Integer areaID) {
        SubBus[] subs = getSubBusesByArea( areaID );
        if( subs == null ) {
            subs = new SubBus[0];
        }
        Vector<CapBankDevice> allBanks = new Vector<CapBankDevice>(64);
        for( int i = 0; i < subs.length; i++ ) {
            SubBus subBus = subs[i];
            if (subBus != null) {
                CapBankDevice[] capBanks = getCapBanksBySub( subBus.getCcId() );        
                allBanks.addAll( Arrays.asList(capBanks) );
            }
        }
    
        Collections.sort( allBanks, CBCUtils.CCNAME_COMPARATOR );
        return allBanks.toArray( new CapBankDevice[allBanks.size()]);
    }
    
    /**
     * Returns all Feeders for a given Area
     */
    public synchronized Feeder[] getFeedersByArea(Integer areaID) {
        SubBus[] subs = getSubBusesByArea( areaID);
        if( subs == null ) {
            subs = new SubBus[0];
        }
        Vector<Feeder> allFeeders = new Vector<Feeder>(64);
        for( int i = 0; i < subs.length; i++ ) {
            Feeder[] feeders = getFeedersBySubBus( subs[i].getCcId() );        
            allFeeders.addAll( Arrays.asList(feeders) );
        }
    
        Collections.sort( allFeeders, CBCUtils.CCNAME_COMPARATOR );
        return allFeeders.toArray( new Feeder[allFeeders.size()]);
    }
    
    /**
     * Find all the orphaned CBC's in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public synchronized LiteWrapper[] getOrphanedCBCs() {
        //hits the DB
        int[] unassignedCBCsIds = CapBankController.getUnassignedDeviceCBCIds();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedCBCsIds.length ];
        
        for( int i = 0 ; i < unassignedCBCsIds.length; i++ ) {
            retVal[i] = new LiteWrapper( DaoFactory.getPaoDao().getLiteYukonPAO(unassignedCBCsIds[i]) );
        }
    
        return retVal;
    }
    
    /**
     * Find the orphaned CapBanks in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public synchronized LiteWrapper[] getOrphanedCapBanks() {
        //hits the DB
        CapBank[] unassignedBanks = CapBank.getUnassignedCapBanksList();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedBanks.length ];
        
        for( int i = 0 ; i < unassignedBanks.length; i++ ) {
            CapBank capBank = unassignedBanks[i];       
            retVal[i] = new LiteWrapper( DaoFactory.getPaoDao().getLiteYukonPAO(capBank.getDeviceID().intValue()) );
        }
        Arrays.sort(retVal, LiteComparators.liteNameComparator);
        return retVal;
    }
    
    /**
     * Find the orphaned Feeders in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public synchronized LiteWrapper[] getOrphanedFeeders() {
        //hits the DB
        CapControlFeeder[] unassignedFeeders = CapControlFeeder.getUnassignedFeeders();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedFeeders.length ];
        
        for( int i = 0 ; i < unassignedFeeders.length; i++ ) {
            CapControlFeeder feeder = unassignedFeeders[i];     
            retVal[i] = new LiteWrapper( DaoFactory.getPaoDao().getLiteYukonPAO(feeder.getFeederID().intValue()) );
        }
        return retVal;
    }
    
    public synchronized LiteWrapper[] getOrphanedSubstations() {
        //hits the DB
        List<Integer> allUnassignedSubstations = CapControlSubstation.getAllUnassignedSubstations();
        LiteWrapper[] retVal = new LiteWrapper[ allUnassignedSubstations.size() ];
        for (Integer id : allUnassignedSubstations) {
            retVal[allUnassignedSubstations.indexOf(id)] = new LiteWrapper( DaoFactory.getPaoDao().getLiteYukonPAO(id) );
        }
        return retVal;
    }
    
    public synchronized LiteWrapper[] getOrphanedSubBuses() {
        //hits the DB
        List<Integer> allUnassignedBuses = CapControlSubBus.getAllUnassignedBuses();
        LiteWrapper[] retVal = new LiteWrapper[ allUnassignedBuses.size() ];
        for (Integer id : allUnassignedBuses) {
            retVal[allUnassignedBuses.indexOf(id)] = new LiteWrapper( DaoFactory.getPaoDao().getLiteYukonPAO(id) );
        }
        return retVal;
    }
    
    /**
     * Create an array of SubBuses. Best usage is to store the results of this call
     * instead of repeatingly calling this method. Never returns null.
     * @return SubBus[]
     * @param
     */
    public synchronized SubBus[] getAllSubBuses() {
        //sort the list base on the names
        SubBus[] subs = new SubBus[ subBusMap.values().size() ];
        subs = subBusMap.values().toArray( subs );
        Arrays.sort( subs, CBCUtils.SUB_AREA_COMPARATOR );
    
        return subs;
    }
    
    /**
     * Distinct area Strings that are used by substations
     * @return List
     */
    public List<CBCArea> getCbcAreas() {
        return cbcAreas;
    }
    
    /**
     * Distinct special area Strings that are used by substations
     * @return List
     */
    public List<CBCSpecialArea> getSpecialCbcAreas() {
        return cbcSpecialAreas;
    }
    
    /**
     * State group & states to use for CapBanks
     * @return LiteState
     */
    public LiteState getCapBankState( int rawState ) {
        return DaoFactory.getStateDao().getLiteState( StateGroupUtils.STATEGROUPID_CAPBANK, rawState );
    }
    
    /**
     * Returns the Parent SubBus ID for the given child id
     */
    public int getParentSubBusID( int childID ) {
        if( isSubBus(childID) ) {
            return childID;
        } else if( isFeeder(childID) ) {
            return getFeeder(new Integer(childID)).getParentID();
        } else if( isCapBank(childID) ) {
            return getFeeder(new Integer(getCapBankDevice(new Integer(childID)).getParentID())).getParentID();
        } else {
            return CtiUtilities.NONE_ZERO_ID;
        }
    }
    /**
     * Returns the Parent Area ID for the given child id
     */
    public int getParentAreaID( int childID ) {
        if( isArea(childID)){
        	return childID;
        }
    	else if( isSubstation(childID) ) {
            return getSubstation(childID).getParentID();
        }else if( isSubBus(childID)){
        	return getSubstation(getSubBus(childID).getParentID()).getParentID();
        }else if( isFeeder(childID) ) {
            return getSubstation(getSubBus(getFeeder(new Integer(childID)).getParentID()).getParentID()).getParentID();
        } else if( isCapBank(childID) ) {
            return getSubstation(getSubBus(getFeeder(new Integer(getCapBankDevice(new Integer(childID)).getParentID())).getParentID()).getParentID()).getParentID();
        } else {
            return CtiUtilities.NONE_ZERO_ID;
        }
    }    
    /**
    * Stores all areas in memory. 
    * @param CBCSubAreas
    */
    private synchronized void handleSpecialAreaList(CBCSubSpecialAreas areas) {
        //for every area in the currently in cache
        List<CBCSpecialArea> cachedSpecialAreas = getSpecialCbcAreas();
        List<CBCSpecialArea> workCopy = new Vector<CBCSpecialArea>();
        workCopy.addAll(cachedSpecialAreas);
        for (CBCSpecialArea area : cachedSpecialAreas) {
            List<CBCSpecialArea> allAreas = areas.getAreas();
            //if current cache has an area and sent object doesn't 
            //discard the cached object
            if (!allAreas.contains(area)) {
                workCopy.remove(area);
            }
            //if the object is cached currently - then replace the object
            else if (allAreas.contains(area)) {
                int idx = workCopy.indexOf(area);
                workCopy.set(idx, allAreas.get(allAreas.indexOf(area)));
            }
        }
        cachedSpecialAreas.clear();
        cachedSpecialAreas.addAll(workCopy);
        //if the object is not in cache add it to cache
        List<CBCSpecialArea> sentAreas = areas.getAreas();
        for (CBCSpecialArea area : sentAreas) {
            if (!cachedSpecialAreas.contains(area)) {
                cachedSpecialAreas.add(area);
            }
        }
    
        Collections.sort(getSpecialCbcAreas(), CBCUtils.CBC_SPECIAL_AREA_COMPARATOR);
    
        resetSpecialAreaStateMap();
    }
    
    /**
    * Stores all areas in memory. 
    * 
    * @param CBCSubAreas
    */
    private synchronized void handleAreaList(CBCSubAreas areas) {
        //for every area in the currently in cache
        List<CBCArea> cachedAreas = getCbcAreas();
        List<CBCArea> workCopy = new Vector<CBCArea>();
        workCopy.addAll(cachedAreas);
        for (CBCArea area : cachedAreas) {
            List<CBCArea> allAreas = areas.getAreas();
            //if current cache has an area and sent object doesn't 
            //discard the cached object
            if (!allAreas.contains(area)) {
                workCopy.remove(area);
            }
            //if the object is cached currently - then replace the object
            else if (allAreas.contains(area)) {
                int idx = workCopy.indexOf(area);
                workCopy.set(idx, allAreas.get( allAreas.indexOf(area)));
            }
            
        }
        cachedAreas.clear();
        cachedAreas.addAll(workCopy);
        //if the object is not in cache add it to cache
        List<CBCArea> sentAreas = areas.getAreas();
        for (CBCArea area : sentAreas) {
            if (!cachedAreas.contains(area)) {
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
    private void handleDeletedSub( int itemID ) {   
        Integer id = new Integer(itemID);
        subBusMap.remove( id );
        subToBankMap.remove( id );
    }
    
    /**
     * Process multiple SubBuses
     * @param busesMsg
     */
    private void handleSubBuses( CBCSubstationBuses busesMsg ) {
        logAllSubs(busesMsg);
        //If this is a full reload of all subs.
    	if (busesMsg.isAllSubs()) {
    	    clearAllMaps();
        }
        else if( busesMsg.isUpdateSub()){
        	//If this is an update to an existing sub.
            handleDeletedSubs(busesMsg);
        }
        //add the each subbus to the cache
        handleAllSubs(busesMsg);
    }

    /**
     * Process multiple SubBuses
     * @param busesMsg
     */
    private void handleSubStations( CBCSubStations stationsMsg ) {
    	logAllSubStations(stationsMsg);
        //If this is a full reload of all subs.
    	if (stationsMsg.isAllSubStations()) {
    	    clearAllMaps();
        }
        else if( stationsMsg.isUpdateSubStations()){
        	//If this is an update to an existing sub.
            handleDeletedSubStation(stationsMsg);
        }
        //add the each subbus to the cache
        handleAllSubStation(stationsMsg);
    }

    
    private void handleDeletedSubs(CBCSubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ){
        	//remove old
        	handleDeletedSub( busesMsg.getSubBusAt(i).getCcId() );
        }
    }
    
    private void handleDeletedSubStation(CBCSubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ){
        	//remove old
        	handleDeletedSub( stationsMsg.getSubAt(i).getCcId() );
        }
    }
    @SuppressWarnings("deprecation")
    private void logAllSubs(CBCSubstationBuses busesMsg) {
        for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- ){
        	CTILogger.debug( new Date().toString()
        			+ " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
        			+ "/" + busesMsg.getSubBusAt(i).getCcArea() );
        }
    }
    
    @SuppressWarnings("deprecation")
    private void logAllSubStations(CBCSubStations stationsMsg) {
        for( int i = (stationsMsg.getNumberOfStations()-1); i >= 0; i-- ){
        	CTILogger.debug( new Date().toString()
        			+ " : Received SubStations - " + stationsMsg.getSubAt(i).getCcName() 
        			+ "/" + stationsMsg.getSubAt(i).getCcArea() );
        	//TODO change to getCcStation when it is implemented.
        }
    }   
    private void handleAllSubs(CBCSubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ) {
        	handleSubBus( busesMsg.getSubBusAt(i) );
        }
    }
    
    private void handleAllSubStation(CBCSubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ) {
        	handleSubStation( stationsMsg.getSubAt(i) );
        }
    }
    /**
     * Process a command message from the server
     */
    private void handleCBCCommand( CBCCommand serverConfirmation ) {
    
        int deviceID = serverConfirmation.getDeviceID();
        switch( serverConfirmation.getCommand() ) {
            //delete the given subID
            case CBCCommand.DELETE_ITEM:
                handleDeleteItem(deviceID);
                break;
            case CBCCommand.SYSTEM_STATUS:
                handleSystemCommand(serverConfirmation);
                break;
        }
    }
    
    private void handleDeleteItem(int deviceID) {
        if( isSubBus(deviceID) ) {
            handleDeletedSub( deviceID );
        } else if (isFeeder(deviceID)) {
            handleDeletedFeeder(deviceID);
        } else if (isCapBank(deviceID)) {
            handleDeletedCap(deviceID);
        } else if (isCBCArea (deviceID)) {
            handleDeleteArea (deviceID);
        } else if (isSpecialCBCArea(deviceID)) {
            handleDeleteSpecialArea(deviceID);
        }
    }
    
    private void handleDeleteArea(int deviceID) {
        synchronized (cbcAreas) {
            List<CBCArea> cachedAreas = getCbcAreas();
            List<CBCArea> workCopy = new Vector<CBCArea>();
            workCopy.addAll(cachedAreas);
            for (Iterator iter = cachedAreas.iterator(); iter.hasNext();) {
                CBCArea area = (CBCArea) iter.next();
                if (area.getPaoID().intValue() == deviceID) {
                    workCopy.remove(area);
                }
            }
            cachedAreas.clear();
            cachedAreas.addAll(workCopy);
        }
    }
    
    private void handleDeleteSpecialArea(int deviceID) {
        synchronized (getSpecialCbcAreas()) {
            List<CBCSpecialArea> cachedSpecialAreas = getSpecialCbcAreas();
            List<CBCSpecialArea> workCopy = new Vector<CBCSpecialArea>();
            workCopy.addAll(cachedSpecialAreas);
            for (Iterator iter = cachedSpecialAreas.iterator(); iter.hasNext();) {
                CBCSpecialArea area = (CBCSpecialArea) iter.next();
                if (area.getPaoID().intValue() == deviceID) {
                    workCopy.remove(area);
                }
            }
            cachedSpecialAreas.clear();
            cachedSpecialAreas.addAll(workCopy);
        }
    }
    
    public boolean isCBCArea(int deviceID) {
        synchronized (cbcAreas) {
            for (Iterator iter = cbcAreas.iterator(); iter.hasNext();) {
                CBCArea area = (CBCArea) iter.next();
                if (area.getPaoID().intValue() == deviceID) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public boolean isSpecialCBCArea(int deviceID) {
        synchronized (getSpecialCbcAreas()) {
            for (Iterator iter = getSpecialCbcAreas().iterator(); iter.hasNext();) {
                CBCSpecialArea area = (CBCSpecialArea) iter.next();
                if (area.getPaoID().intValue() == deviceID) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private void handleDeletedCap(int deviceID) {
        capBankMap.remove(deviceID);
    }
    
    private void handleDeletedFeeder(int deviceID) {
        feederMap.remove(deviceID);    
    }
    
    private void handleSystemCommand(CBCCommand serverConfirmation) {
        synchronized (systemStatusOn ) {
            if (serverConfirmation.isSystemDisabled()) {
                systemStatusOn = Boolean.FALSE;
            } else {
                systemStatusOn = Boolean.TRUE;
            }
        }
    }
    
    /**
     * Processes a single SubBus, breaking it up into Feeders and Capbanks
     * @param SubBus
     */
    private synchronized void handleSubBus( SubBus subBus ) {   
        
        Validate.notNull(subBus, "subBus can't be null");
        //remove the old subBus from the area hashmap just in case the area changed
        subBusMap.remove(subBus.getCcId());
        subBusMap.put( subBus.getCcId(), subBus );
        Vector feeders = subBus.getCcFeeders();
    
        NativeIntVector capBankIDs = new NativeIntVector(32);
        for( int i = 0; i < feeders.size(); i++ ) {
            Feeder feeder = (Feeder)feeders.elementAt(i);
            feederMap.remove(feeder.getCcId());
            feederMap.put( feeder.getCcId(), feeder );
            
            for( int j = 0; j < feeder.getCcCapBanks().size(); j++ ) {
                CapBankDevice capBank = feeder.getCcCapBanks().get(j);
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
     * Processes a single SubStation.
     * @param SubStation
     */
    private synchronized void handleSubStation( SubStation sub ) {   
        
        Validate.notNull(sub, "sub can't be null");
        //remove the old sub from the area hashmap just in case the area changed
        subStationMap.remove(sub.getCcId());
        subStationMap.put( sub.getCcId(), sub );
    
        //not linking to sub busses, feeders, and capbanks.
        
        getUpdatedObjMap().handleCBCChangeEvent(sub, new Date());
    }
    
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
    protected CBCClientConnection getConnection() {
        return (CBCClientConnection)ConnPool.getInstance().getDefCapControlConn();
    }
    
    /**
     * Renew the cache.
     */
    public synchronized boolean refresh() {
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
     * @param MessageEvent
     */
    public void messageReceived( MessageEvent e ) {
        Message in = e.getMessage();
        if( in instanceof CBCSubstationBuses )
        {
            handleSubBuses( (CBCSubstationBuses)in );
        }
        else if( in instanceof CBCSubStations )
        {
        	handleSubStations( (CBCSubStations)in );
        }        
        else if( in instanceof CBCSubSpecialAreas )
        {
            handleSpecialAreaList( (CBCSubSpecialAreas)in );
        }
        else if( in instanceof CBCSubAreas ) 
        {
            handleAreaList( (CBCSubAreas)in );
        }
        else if( in instanceof CBCCommand ) 
        {
            handleCBCCommand( (CBCCommand)in );
        }
    
    }
    
    
    public CBCArea getCBCArea(int id) {
        for (Iterator<CBCArea> iter = cbcAreas.iterator(); iter.hasNext();) {
            CBCArea area = iter.next();
            if (area.getPaoID().intValue() == id) {
                return area;
            }
        }
        return null;
    }
    
    public CBCSpecialArea getCBCSpecialArea(int id) {
        for (Iterator<CBCSpecialArea> iter = getSpecialCbcAreas().iterator(); iter.hasNext();) {
            CBCSpecialArea area = iter.next();
            if (area.getPaoID().intValue() == id) {
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
    
    public HashMap getSpecialAreaStateMap() {    
        if (specialAreaStateMap.isEmpty()) { //init the our map
            initSpecialAreaStateMap();
        }
        return specialAreaStateMap;
    }
    
    private void initAreaStateMap() {
        List areaNames = getCbcAreas();
        for (Iterator iter = areaNames.iterator(); iter.hasNext();) {
            CBCArea area = (CBCArea) iter.next();
            areaStateMap.put(area.getPaoName(), !area.getDisableFlag());
        }
    }
    
    private void initSpecialAreaStateMap() {
        List<CBCSpecialArea> areaNames = getSpecialCbcAreas();
        for (Iterator<CBCSpecialArea> iter = areaNames.iterator(); iter.hasNext();) {
            CBCSpecialArea area = iter.next();
            specialAreaStateMap.put(area.getPaoName(), !area.getDisableFlag());
        }
    }
    
    private void resetAreaStateMap() {
        areaStateMap = new HashMap<String, Boolean>();
        getAreaStateMap();
    }
    
    private void resetSpecialAreaStateMap() {
        specialAreaStateMap = new HashMap<String, Boolean>();
        getSpecialAreaStateMap();
    }
    
    public Boolean getSystemStatusOn() {
        return systemStatusOn;
    }

    public void setRefreshTimer(ScheduledExecutor refreshTimer) {
        this.refreshTimer = refreshTimer;
    }

    public void setDefCapControlConn(IServerConnection defCapControlConn) {
        this.defCapControlConn = defCapControlConn;
    }

    public SubStation[] getSubstationsByArea(Integer areaId) {
        if(getCBCArea(areaId) != null) {
            List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubs(areaId);
            List<Integer>intList = CCSubAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubStation> subList = new ArrayList<SubStation>();
            for (Integer id : intList) {
                SubStation sub = subStationMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            SubStation[] subs = subList.toArray(new SubStation[]{});
            Arrays.sort( subs, CBCUtils.CCNAME_COMPARATOR );
            return subs;
        }else if(getCBCSpecialArea(areaId) != null) {
            List<CCSubSpecialAreaAssignment> allAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaId);
            List<Integer>intList = CCSubSpecialAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubStation> subList = new ArrayList<SubStation>();
            for (Integer id : intList) {
                SubStation sub = subStationMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            SubStation[] subs = subList.toArray(new SubStation[]{});
            Arrays.sort( subs, CBCUtils.CCNAME_COMPARATOR );
            return subs;
        }else {
            return null;
        }
    }
}