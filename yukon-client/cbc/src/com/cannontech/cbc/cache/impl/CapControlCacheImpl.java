package com.cannontech.cbc.cache.impl;

/**
 * Maintains information from the capcontrol server
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.dao.CcSubstationDao;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CapControlClientConnection;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CCSubAreas;
import com.cannontech.yukon.cbc.CCSubSpecialAreas;
import com.cannontech.yukon.cbc.CCSubStations;
import com.cannontech.yukon.cbc.CCSubstationBuses;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;
import com.cannontech.yukon.conns.ConnPool;

public class CapControlCacheImpl implements MessageListener, CapControlCache {
    private static final int STARTUP_REF_RATE = 15 * 1000;
    private static final int NORMAL_REF_RATE = 30 * 60 * 1000; //5 minutes
    private Hashtable<Integer, SubStation> subStationMap = new Hashtable<Integer, SubStation>();
    private Hashtable<Integer, SubBus> subBusMap = new Hashtable<Integer, SubBus>();
    private Hashtable<Integer, Feeder>  feederMap = new Hashtable<Integer, Feeder>();
    private Hashtable<Integer, CapBankDevice> capBankMap = new Hashtable<Integer, CapBankDevice> ();
    private HashMap<Integer, int[]> subToBankMap = new HashMap<Integer, int[]>();
    private CBCWebUpdatedObjectMap updatedObjMap = null;
    private Map<Integer, CCArea> cbcAreaMap = Collections.synchronizedMap(new HashMap<Integer, CCArea>());
    private Map<Integer, CCSpecialArea> cbcSpecialAreaMap = Collections.synchronizedMap(new HashMap<Integer, CCSpecialArea>());
    private Boolean systemStatusOn = Boolean.TRUE;

    private PaoDao paoDao;
    private StateDao stateDao;
    private CcSubstationDao substationDao;
    private FeederDao feederDao;
    private SubstationBusDao substationBusDao;
    private CapbankDao capbankDao;
    private IServerConnection defCapControlConn;
    private ScheduledExecutor refreshTimer;

    public CapControlCacheImpl() {
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
    
    public synchronized StreamableCapObject getArea(final int paoId) throws NotFoundException {
        StreamableCapObject object;
        try {
            object = this.getCBCArea(paoId);
        } catch (NotFoundException checkingForCBCSpecialArea) {
            object = this.getCBCSpecialArea(paoId);
        }
        return object;
    }
    
    /**
     * @return SubBus
     */
    public synchronized SubBus getSubBus(int subBusId) throws NotFoundException {        
    	SubBus bus = subBusMap.get(subBusId);
    	checkObjectFound(bus, subBusId, SubBus.class);
    	return bus;
    }
    
    /**
     * @return SubBus
     */
    public synchronized SubStation getSubstation(int subStationId) throws NotFoundException {
    	SubStation station = subStationMap.get(subStationId);
    	checkObjectFound(station, subStationId, SubStation.class);
    	return station;
    }
    
    public synchronized String getSubBusNameForFeeder(Feeder feeder) {
        Validate.notNull(feeder,"Feeder cannot be null, method: getSubBusNameForFeeder");
    	
        int parentId = feeder.getParentID();
        
    	if (parentId > 0) {
            SubBus bus = subBusMap.get(parentId);
            if (bus != null) return bus.getCcName();
        }
    	
        return null;
    }
    
    /**
     * Returns the base object type for a SubBus, Feeder or CapBankDevice
     */
    public synchronized StreamableCapObject getCapControlPAO(int paoId) {
        StreamableCapObject retObj = subBusMap.get(paoId);
        if( retObj == null ) retObj = subStationMap.get(paoId);
        if( retObj == null ) retObj = feederMap.get(paoId);
        if( retObj == null ) retObj = capBankMap.get(paoId);
        if( retObj == null ) retObj = cbcAreaMap.get(paoId);
        if( retObj == null ) retObj = cbcSpecialAreaMap.get(paoId);
        
        return retObj;
    }
    
    public synchronized Feeder getFeeder(int feederId) throws NotFoundException{
        Feeder feeder = feederMap.get( feederId );
        checkObjectFound(feeder, feederId, Feeder.class);
        return feeder;
    }
    
    /**
     * @return CapBankDevice
     */
    public synchronized CapBankDevice getCapBankDevice(int capBankDeviceID) throws NotFoundException {
    	CapBankDevice cap = capBankMap.get( capBankDeviceID );
    	checkObjectFound(cap, capBankDeviceID, CapBankDevice.class);
    	return cap;
    }
    
    public List<Feeder> getFeedersBySubBus(int subBusId) {
    	SubBus subBus = getSubBus(subBusId);
        try {
            List<Feeder> list = new ArrayList<Feeder>(subBus.getCcFeeders());
            return list;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * @return List<Feeder>
     */
    public synchronized List<Feeder> getFeedersBySubStation(SubStation sub) {
    	Validate.notNull(sub, "Substation cannot be null");
    	
        int[] subBusIds = sub.getSubBusIds();
        List<Feeder> feeders = new ArrayList<Feeder>(subBusIds.length);
        
        for (final int id : subBusIds) {
            try {
                SubBus subBus = getSubBus(id);
                feeders.addAll(subBus.getCcFeeders());
            } catch (NotFoundException ignore) {}  
        }

        return feeders;
    }
    
    /**
     * @return List<CapBankDevice>
     */
    public synchronized List<CapBankDevice> getCapBanksBySubStation(SubStation sub) {
    	Validate.notNull(sub, "Substation cannot be null");
        
    	int[] subBusIds = sub.getSubBusIds();
        List<CapBankDevice> capBanks = new ArrayList<CapBankDevice>(subBusIds.length);
        
        for (final int id : subBusIds) {
            try {
                SubBus subBus = getSubBus(id);
                for (final Feeder feeder : subBus.getCcFeeders()) {
                    Vector<CapBankDevice> caps = feeder.getCcCapBanks();
                    capBanks.addAll(caps);
                }
            } catch (NotFoundException ignore) {}
        }
        
        return capBanks;
    }
    
    /**
     * @return CapBankDevice[]
     */
    public CapBankDevice[] getCapBanksByFeeder(int feederId) {
        try {
            Feeder feeder = getFeeder(feederId);
            Vector<CapBankDevice> capBanks = feeder.getCcCapBanks();
            CapBankDevice[] toArray = capBanks.toArray(new CapBankDevice[capBanks.size()]);
            return toArray;
        } catch (NotFoundException e) {
            return new CapBankDevice[0];
        }
    }
    
    /**
     * Instant lookup to check if this paoID is used by an Area
     * 
     */
    public boolean isArea(int id) {
        try {
            getArea(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }    
    /**
     * Instant lookup to check if this paoID is used by a SubBus
     * 
     */
    public boolean isSubBus(int id) {
        try {
            getSubBus(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    /**
     * Instant lookup to check if this paoID is used by a SubStation
     * 
     */
    public boolean isSubstation(int id) {
        try {
            getSubstation(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }    
    /**
     * Instant lookup to check if this paoID is used by a Feeder
     * 
     */
    public boolean isFeeder(int id) {
        try {
            getFeeder(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    
    /**
     * Instant lookup to check if this paoID is used by a CapBankDevice
     * 
     */
    public boolean isCapBank(int id) {
        try {
            getCapBankDevice(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    
    public boolean isCBCArea(int deviceID) {
        try {
            getCBCArea(deviceID);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    
    public synchronized boolean isSpecialCBCArea(int deviceID) {
        List<CCSpecialArea> areaList = getSpecialCbcAreas();
        for (final CCSpecialArea area : areaList) {
            boolean result = area.getPaoID().equals(deviceID);
            if (result) return true;
        }
        return false;
    }
    
    /**
     * @return CapBankDevice[]
     * @param subBusID long
     * 
     * Can throw a not found exception if the subToBankMap is holding bad bank ID's.
     */
    public synchronized List<CapBankDevice> getCapBanksBySubBus(int subBusId) {
        int[] bankIds = subToBankMap.get( subBusId );
        
        if (bankIds == null) return Collections.emptyList();

        List<CapBankDevice> retVal = new ArrayList<CapBankDevice>(bankIds.length);
        
        for (final int bankId : bankIds) {
            try {
                CapBankDevice capBank = getCapBankDevice(bankId); 
                retVal.add(capBank);
            } catch (NotFoundException ignore) {}     
        }
        return retVal;
    }
    
    /**
     * This method will return a list of assigned subbuses
     * for Areas or SpecialAreas.
     * @return SubBus[]
     * @param areaId Integer
     */
    public synchronized List<SubBus> getSubBusesByArea(int areaId) throws NotFoundException {
        
        try {
            getCBCArea(areaId);
            try {
                List<SubBus> subsForArea = new ArrayList<SubBus>();
                List<SubStation> allAreaSubstations = getSubstationsByArea(areaId);
                for(SubStation substation : allAreaSubstations) {
                    List<SubBus> subbuses = getSubBusesBySubStation(substation);
                    subsForArea.addAll(subbuses);
                }
                Collections.sort(subsForArea,CBCUtils.CCNAME_COMPARATOR );
                return subsForArea;
            } catch (NotFoundException e) {
                return Collections.emptyList();
            }
        } catch (NotFoundException areaNotFoundTrySpecialArea) {
            getCBCSpecialArea(areaId);
            try {
                List<SubBus> subsForArea = new ArrayList<SubBus>();
                List<SubStation> allAreaSubstations = getSubstationsBySpecialArea(areaId);
                for(SubStation substation : allAreaSubstations) {
                    List<SubBus> subbuses = getSubBusesBySubStation(substation);
                    subsForArea.addAll(subbuses);
                }
                Collections.sort(subsForArea,CBCUtils.CCNAME_COMPARATOR );
                return subsForArea;
            } catch (NotFoundException e) {
                return Collections.emptyList();
            }
        }
    }
    
    public synchronized List<SubBus> getSubBusesBySubStation(SubStation sub) {
        int[] busIds = sub.getSubBusIds();
        List<SubBus> buses = new ArrayList<SubBus>(busIds.length);

        for (final int id : busIds) {
            try {
                SubBus subBus = getSubBus(id);
                buses.add(subBus);
            } catch (NotFoundException ignore) {} 
        }
        
        return buses;
    }
    
    /**
     * Returns all CapBanks for a given Area
     */
    public synchronized List<CapBankDevice> getCapBanksByArea(int areaID) {
        try {
            List<SubBus> subs = getSubBusesByArea( areaID );
            List<CapBankDevice> allBanks = new ArrayList<CapBankDevice>(subs.size());

            for (final SubBus subBus : subs) {
                if (subBus == null) continue;
                try {
                    List<CapBankDevice> capBanks = getCapBanksBySubBus( subBus.getCcId() );        
                    allBanks.addAll( capBanks );
                } catch (NotFoundException ignore) {}
            }

            Collections.sort(allBanks, CBCUtils.CCNAME_COMPARATOR );
            return allBanks;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * Returns all Feeders for a given Area
     */
    public synchronized List<Feeder> getFeedersByArea(int areaID) {
        try {
            List<SubBus> subs = getSubBusesByArea(areaID);
            List<Feeder> allFeeders = new ArrayList<Feeder>(subs.size());

            for (final SubBus subBus : subs) {
                Integer subBusId = subBus.getCcId();
                try {
                    List<Feeder> feeders = getFeedersBySubBus(subBusId);        
                    allFeeders.addAll( feeders );
                } catch (NotFoundException ignore) {}
            }

            Collections.sort( allFeeders, CBCUtils.CCNAME_COMPARATOR );
            return allFeeders;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * Find all the orphaned CBC's in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public LiteWrapper[] getOrphanedCBCs() {
        //hits the DB
        int[] unassignedCBCsIds = CapBankController.getUnassignedDeviceCBCIds();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedCBCsIds.length ];
        
        for( int i = 0 ; i < unassignedCBCsIds.length; i++ ) {
            retVal[i] = new LiteWrapper(paoDao.getLiteYukonPAO(unassignedCBCsIds[i]) );
        }
    
        return retVal;
    }
    
    /**
     * Find the orphaned CapBanks in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public LiteWrapper[] getOrphanedCapBanks() {
        //hits the DB
        List<Integer> unassignedBanks = capbankDao.getUnassignedCapBankIds();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedBanks.size() ];
        
        for( Integer i : unassignedBanks ) {   
            retVal[unassignedBanks.indexOf(i)] = new LiteWrapper(paoDao.getLiteYukonPAO(i) );
        }
        Arrays.sort(retVal, LiteComparators.liteNameComparator);
        return retVal;
    }
    
    /**
     * Find the orphaned Feeders in the system. This requires a database hit since
     * the CapControl server does not know about these.
     */
    public LiteWrapper[] getOrphanedFeeders() {
        //hits the DB
        List<Integer> unassignedFeeders = feederDao.getUnassignedFeederIds();
        LiteWrapper[] retVal = new LiteWrapper[ unassignedFeeders.size() ];
        
        for( Integer i : unassignedFeeders ) {
            retVal[unassignedFeeders.indexOf(i)] = new LiteWrapper(paoDao.getLiteYukonPAO(i) );
        }
        return retVal;
    }
    
    public LiteWrapper[] getOrphanedSubstations() {
        //hits the DB
        List<Integer> allUnassignedSubstations = substationDao.getAllUnassignedSubstationIds();
        LiteWrapper[] retVal = new LiteWrapper[ allUnassignedSubstations.size() ];
        for (Integer id : allUnassignedSubstations) {
            retVal[allUnassignedSubstations.indexOf(id)] = new LiteWrapper(paoDao.getLiteYukonPAO(id) );
        }
        return retVal;
    }
    
    public LiteWrapper[] getOrphanedSubBuses() {
        //hits the DB
        List<Integer> allUnassignedBuses = substationBusDao.getAllUnassignedBuses();
        LiteWrapper[] retVal = new LiteWrapper[ allUnassignedBuses.size() ];
        for (Integer id : allUnassignedBuses) {
            retVal[allUnassignedBuses.indexOf(id)] = new LiteWrapper(paoDao.getLiteYukonPAO(id) );
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
        Collection<SubBus> values = subBusMap.values();
        SubBus[] subs = values.toArray(new SubBus[values.size()]);
        Arrays.sort(subs, CBCUtils.SUB_AREA_COMPARATOR );
        return subs;
    }
    
    /**
     * Distinct area Strings that are used by substations
     * @return List
     */
    public synchronized List<CCArea> getCbcAreas() {
        List<CCArea> list = new ArrayList<CCArea>(cbcAreaMap.values());
        Collections.sort(list, CBCUtils.CBC_AREA_COMPARATOR);
        return list;
    }
    
    /**
     * Distinct special area Strings that are used by substations
     * @return List
     */
    public synchronized List<CCSpecialArea> getSpecialCbcAreas() {
        List<CCSpecialArea> list = new ArrayList<CCSpecialArea>(cbcSpecialAreaMap.values());
        Collections.sort(list, CBCUtils.CBC_SPECIAL_AREA_COMPARATOR);
        return list;
    }
    
    /**
     * State group & states to use for CapBanks
     * @return LiteState
     */
    public LiteState getCapBankState( int rawState ) {
        return stateDao.getLiteState( StateGroupUtils.STATEGROUPID_CAPBANK, rawState );
    }
    
    /**
     * Returns the Parent SubBus ID for the given child id
     */
    public synchronized int getParentSubBusID(int childID) throws NotFoundException {
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
    public synchronized int getParentAreaID(int childID) throws NotFoundException {
        int id;
        SubStation station;
        
    	if( isArea(childID)){
        	return childID;
        }
    	else if( isSubstation(childID) ) {
            station = getSubstation(childID);
        }else if( isSubBus(childID)){
        	station = getSubstation(getSubBus(childID).getParentID());
        }else if( isFeeder(childID) ) {
        	station = getSubstation(getSubBus(getFeeder(new Integer(childID)).getParentID()).getParentID());
        } else if( isCapBank(childID) ) {
        	station = getSubstation(getSubBus(getFeeder(new Integer(getCapBankDevice(new Integer(childID)).getParentID())).getParentID()).getParentID());
        } else {
            station = null;
        }
        
        if( station == null) {
        	id = CtiUtilities.NONE_ZERO_ID;
        } else {
	        id = station.getParentID();
			if (id < 0) {
				id = station.getSpecialAreaId();
			}
        }
    	return id;
    }    
    /**
    * @param CCSubAreas
    */
    private synchronized void handleSpecialAreaList(CCSubSpecialAreas areas) {
        clearCacheMap(cbcSpecialAreaMap);
        
        final Date date = new Date();
        
        List<CCSpecialArea> list = areas.getAreas();
        for (final CCSpecialArea area : list) {
        	int areaId = area.getPaoID();
            cbcSpecialAreaMap.put(areaId, area);
            getUpdatedObjMap().handleCBCChangeEvent(area, date);
        }
    }
    
    /**
    * @param CCSubAreas
    */
    private synchronized void handleAreaList(CCSubAreas areas) {
        clearCacheMap(cbcAreaMap);
        
        final Date date = new Date();
        
        List<CCArea> list = areas.getAreas();
        for (final CCArea area : list) {
            Integer areaId = area.getPaoID();
            cbcAreaMap.put(areaId, area);
            getUpdatedObjMap().handleCBCChangeEvent(area, date);
        }
    }
    
    /**
     * Removes this subbus from all the structures in cache. 
     * @param msg
     */
    private synchronized void handleDeletedSub(int id) {   
        subBusMap.remove(id);
        subToBankMap.remove(id);
        getUpdatedObjMap().remove(id);
    }
    
    /**
     * Removes this subbus from all the structures in cache. 
     * @param msg
     */
    private synchronized void handleDeletedSubstation(int id) {   
        subStationMap.remove(id);
        getUpdatedObjMap().remove(id);
    }
    
    /**
     * Process multiple SubBuses
     * @param busesMsg
     */
    private synchronized void handleSubBuses( CCSubstationBuses busesMsg ) {
        logAllSubs(busesMsg);
        //If this is a full reload of all subs.
    	if (busesMsg.isAllSubs()) {
    	    clearCacheMap(subBusMap);
    	    clearCacheMap(feederMap);
    	    clearCacheMap(capBankMap);
    	    clearCacheMap(subToBankMap);
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
    private synchronized void handleSubStations( CCSubStations stationsMsg ) {
    	logAllSubStations(stationsMsg);
        //If this is a full reload of all subs.
    	if (stationsMsg.isAllSubStations()) {
    	    clearCacheMap(subStationMap);
        }
        else if( stationsMsg.isUpdateSubStations()){
        	//If this is an update to an existing sub.
            handleDeletedSubStations(stationsMsg);
        }
        //add the each subbus to the cache
        handleAllSubStation(stationsMsg);
    }

    
    private synchronized void handleDeletedSubs(CCSubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ){
        	//remove old
        	SubBus bus = busesMsg.getSubBusAt(i);
        	handleDeletedFeeders( bus.getCcFeeders() );
        	handleDeletedSub( bus.getCcId() );
        }
    }
    
    private synchronized void handleDeletedSubStations(CCSubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ){
        	//remove old
        	handleDeletedSubstation( stationsMsg.getSubAt(i).getCcId() );
        }
    }
    
    private synchronized void logAllSubs(CCSubstationBuses busesMsg) {
        for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- ){
        	CTILogger.debug( new Date().toString()
        			+ " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
        			+ "/" + busesMsg.getSubBusAt(i).getCcArea() );
        }
    }
    
    private synchronized void logAllSubStations(CCSubStations stationsMsg) {
        for( int i = (stationsMsg.getNumberOfStations()-1); i >= 0; i-- ){
        	CTILogger.debug( new Date().toString()
        			+ " : Received SubStations - " + stationsMsg.getSubAt(i).getCcName() 
        			+ "/" + stationsMsg.getSubAt(i).getCcArea() );
        }
    }   
    private synchronized void handleAllSubs(CCSubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ) {
        	handleSubBus( busesMsg.getSubBusAt(i) );
        }
    }
    
    private synchronized void handleAllSubStation(CCSubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ) {
        	handleSubStation( stationsMsg.getSubAt(i) );
        }
    }
    /**
     * Process a command message from the server
     */
    private synchronized void handleCBCCommand( CapControlCommand serverConfirmation ) {
    
        int deviceID = serverConfirmation.getDeviceID();
        switch( serverConfirmation.getCommand() ) {
            //delete the given subID
            case CapControlCommand.DELETE_ITEM:
                handleDeleteItem(deviceID);
                break;
            case CapControlCommand.SYSTEM_STATUS:
                handleSystemCommand(serverConfirmation);
                break;
        }
    }
    
    private synchronized void handleDeleteItem(int deviceID) {
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
    
    private synchronized void handleDeleteArea(int deviceID) {
        removeFromCacheMap(cbcAreaMap, deviceID);
    }
    
    private synchronized void handleDeleteSpecialArea(int deviceID) {
        removeFromCacheMap(cbcSpecialAreaMap, deviceID);
    }
    
    private synchronized void handleDeletedCaps( Vector<CapBankDevice> vec ) {
    	for( CapBankDevice cap : vec ) {
    		handleDeletedCap(cap.getCcId());
    	}
    }

    private synchronized void handleDeletedCap(int deviceID) {
        removeFromCacheMap(capBankMap, deviceID);
    }
    
    private synchronized void handleDeletedFeeders( Vector<Feeder> v ) {
    	for( Feeder f : v ) {
    		handleDeletedCaps( f.getCcCapBanks() );
    		handleDeletedFeeder(f.getCcId());
    	}
    }
    
    private synchronized void handleDeletedFeeder(int deviceID) {
        removeFromCacheMap(feederMap, deviceID);    
    }
    
    private void handleSystemCommand(CapControlCommand serverConfirmation) {
        synchronized (systemStatusOn ) {
            if (serverConfirmation.isSystemDisabled()) {
                systemStatusOn = Boolean.FALSE;
            } else {
                systemStatusOn = Boolean.TRUE;
            }
        }
    }
    
    /**
     * Processes a single SubBus, breaking it up into Feeders and Cap Banks
     * @param SubBus
     */
    private synchronized void handleSubBus( SubBus subBus ) {   
        Validate.notNull(subBus, "subBus can't be null");

        final Integer subBusId = subBus.getCcId();
        
        removeFromCacheMap(subBusMap, subBusId);
        subBusMap.put(subBusId, subBus );
        
        Vector<Feeder> feeders = subBus.getCcFeeders();
    
        NativeIntVector capBankIDs = new NativeIntVector(32);
        for (final Feeder feeder : feeders) {
            Integer feederId = feeder.getCcId();
            removeFromCacheMap(feederMap, feederId);
            feederMap.put(feederId, feeder );
            
            Vector<CapBankDevice> capBanks = feeder.getCcCapBanks();
            for (final CapBankDevice capBank : capBanks) {
                Integer capBankId = capBank.getCcId();
                removeFromCacheMap(capBankMap, capBankId);
                capBankMap.put(capBankId, capBank);
                capBankIDs.add(capBankId);
            }
        }
        
        removeFromCacheMap(subToBankMap, subBusId);
        subToBankMap.put(subBusId, capBankIDs.toArray());
        getUpdatedObjMap().handleCBCChangeEvent(subBus, new Date());
    }
    
    /**
     * Processes a single SubStation.
     * @param SubStation
     */
    private synchronized void handleSubStation( SubStation sub ) {   
        Validate.notNull(sub, "substation can't be null");

        //remove the old sub from the area hashmap just in case the area changed
        final Integer subStationId = sub.getCcId();
        removeFromCacheMap(subStationMap, subStationId);
        subStationMap.put(subStationId, sub);
    
        //not linking to sub busses, feeders, and capbanks.
        
        getUpdatedObjMap().handleCBCChangeEvent(sub, new Date());
    }
    
    /**
     * Allows access the a CBCClientConnection instance
     * @return
     */
    public CapControlClientConnection getConnection() {
        return (CapControlClientConnection)ConnPool.getInstance().getDefCapControlConn();
    }
    
    /**
     * Renew the cache.
     */
    public synchronized boolean refresh() {
        CTILogger.debug("Refreshing CapControl Cache");
        
        try  {
            getConnection().executeCommand( 0, CapControlCommand.REQUEST_ALL_AREAS );
        } catch ( IOException ioe ) {
            CTILogger.error( "Exception occurred during a refresh_cache operation", ioe );
            return false;
        }
        return true;
    }
    
    /**
     * Handles any type of message received from the connection.
     * @param MessageEvent
     */
    public synchronized void messageReceived( MessageEvent e ) {
        Message in = e.getMessage();
        if (in instanceof CCSubstationBuses) {
            handleSubBuses((CCSubstationBuses) in);
        } else if (in instanceof CCSubStations) {
            handleSubStations((CCSubStations) in);
        } else if (in instanceof CCSubSpecialAreas) {
            handleSpecialAreaList((CCSubSpecialAreas) in);
        } else if (in instanceof CCSubAreas) {
            handleAreaList((CCSubAreas) in);
        } else if (in instanceof CapControlCommand) {
            handleCBCCommand((CapControlCommand) in);
        } 
    }
    
    public synchronized CCArea getCBCArea(int id) throws NotFoundException {
        CCArea area = cbcAreaMap.get(id);
        checkObjectFound(area, id, CCArea.class);
        return area;
    }
    
    public synchronized CCSpecialArea getCBCSpecialArea(int id) throws NotFoundException {
        CCSpecialArea area = cbcSpecialAreaMap.get(id);
        checkObjectFound(area, id, CCSpecialArea.class);        
        return area;
    }
    
    public synchronized StreamableCapObject getObject(int id) throws NotFoundException {
        StreamableCapObject object = cbcAreaMap.get(id);
        if (object != null) return object;
        
        object = cbcSpecialAreaMap.get(id);
        if (object != null) return object;
        
        object = subStationMap.get(id);
        if (object != null) return object;
        
        object = subBusMap.get(id);
        if (object != null) return object;
        
        object = feederMap.get(id);
        if (object != null) return object;
        
        object = capBankMap.get(id);
        if (object != null) return object;
        
        throw new NotFoundException("StreamableCapObject with id: " + id + " not found.");
    }
    
    public synchronized CBCWebUpdatedObjectMap getUpdatedObjMap() {
        if (updatedObjMap == null)
            updatedObjMap = new CBCWebUpdatedObjectMap();
        return updatedObjMap;
    }
    
    public Boolean getSystemStatusOn() {
        return systemStatusOn;
    }

    public synchronized List<SubStation> getSubstationsByArea(int areaId) throws NotFoundException {
        try {
            getCBCArea(areaId);
            List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(areaId);
            List<Integer>intList = CCSubAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubStation> subList = new ArrayList<SubStation>();
            for (Integer id : intList) {
                SubStation sub = subStationMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            return subList;
        } catch (NotFoundException areaNotFoundTryingSpecialArea) {
            getCBCSpecialArea(areaId);
            List<CCSubSpecialAreaAssignment> allAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaId);
            List<Integer>intList = CCSubSpecialAreaAssignment.getAsIntegerList(allAreaSubs);
            List<SubStation> subList = new ArrayList<SubStation>();
            for (Integer id : intList) {
                SubStation sub = subStationMap.get(id);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            return subList;
        }
    }
    
    private void checkObjectFound(Object obj, Integer id, Class<?> type) throws NotFoundException {
        if (obj != null) return;
        String message = type.getCanonicalName() + " with id " + id + " not found";
        throw new NotFoundException(message);
    }
    
    public List<CapBankDevice> getCapBanksBySpecialArea(int areaID) {
        return getCapBanksByArea(areaID);
    }
    
    public List<SubStation> getSubstationsBySpecialArea(int areaId) {
        return getSubstationsByArea(areaId);
    }

    private void clearCacheMap(final Map<Integer,?> map) {
        Set<Integer> keySet = map.keySet();
        getUpdatedObjMap().remove(keySet);
        map.clear();
    }
    
    private void removeFromCacheMap(final Map<Integer,?> map, final Integer id) {
        map.remove(id);
        getUpdatedObjMap().remove(id);
    }
    
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }

    public void setCapbankDao(CapbankDao capbankDao) {
        this.capbankDao = capbankDao;
    }

    public void setFeederDao(FeederDao feederDao) {
        this.feederDao = feederDao;
    }

    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
        this.substationBusDao = substationBusDao;
    }

    public void setSubstationDao(CcSubstationDao substationDao) {
        this.substationDao = substationDao;
    }
    
    public void setDefCapControlConn(IServerConnection defCapControlConn) {
        this.defCapControlConn = defCapControlConn;
    }

    public void setRefreshTimer(ScheduledExecutor refreshTimer) {
        this.refreshTimer = refreshTimer;
    }

}