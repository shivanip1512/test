package com.cannontech.cbc.cache.impl;

/**
 * Maintains information from the capcontrol server
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.DeleteItem;
import com.cannontech.message.capcontrol.model.SpecialAreas;
import com.cannontech.message.capcontrol.model.SubAreas;
import com.cannontech.message.capcontrol.model.SubStations;
import com.cannontech.message.capcontrol.model.SubstationBuses;
import com.cannontech.message.capcontrol.model.SystemStatus;
import com.cannontech.message.capcontrol.model.VoltageRegulatorFlagMessage;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.CapControlClientConnection;
import com.cannontech.yukon.conns.ConnPool;

public class CapControlCacheImpl implements MessageListener, CapControlCache {
    
    private static final Logger log = YukonLogManager.getLogger(CapControlCacheImpl.class);
    
    private static final int STARTUP_REF_RATE = 15 * 1000;
    private static final int NORMAL_REF_RATE = 30 * 60 * 1000; //5 minutes
    
    private Hashtable<Integer, SubStation> subStationMap = new Hashtable<Integer, SubStation>();
    private Hashtable<Integer, SubBus> subBusMap = new Hashtable<Integer, SubBus>();
    private Hashtable<Integer, Feeder>  feederMap = new Hashtable<Integer, Feeder>();
    private Hashtable<Integer, CapBankDevice> capBankMap = new Hashtable<Integer, CapBankDevice> ();
    private Hashtable<Integer, VoltageRegulatorFlags> voltageRegulatorMap = new Hashtable<Integer, VoltageRegulatorFlags> ();
    
    private HashMap<Integer, int[]> subToBankMap = new HashMap<Integer, int[]>();
    private CBCWebUpdatedObjectMap updatedObjMap = null;
    private Map<Integer, Area> cbcAreaMap = Collections.synchronizedMap(new HashMap<Integer, Area>());
    private Map<Integer, SpecialArea> cbcSpecialAreaMap = Collections.synchronizedMap(new HashMap<Integer, SpecialArea>());
    
    private Boolean systemStatusOn = Boolean.TRUE;

    private StateDao stateDao;
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
        if( retObj == null ) retObj = voltageRegulatorMap.get(paoId);
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
    public synchronized CapBankDevice getCapBankDevice(int capBankDeviceId) throws NotFoundException {
    	CapBankDevice cap = capBankMap.get( capBankDeviceId );
    	checkObjectFound(cap, capBankDeviceId, CapBankDevice.class);
    	return cap;
    }
    
    /**
     * @return List<Feeder>
     */
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
    public List<CapBankDevice> getCapBanksByFeeder(int feederId) {
        try {
            Feeder feeder = getFeeder(feederId);
            return feeder.getCcCapBanks();
        } catch (NotFoundException e) {
            return Collections.emptyList();
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
    
    public boolean isCBCArea(int deviceId) {
        try {
            getCBCArea(deviceId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    
    public synchronized boolean isSpecialCBCArea(int deviceId) {
        List<SpecialArea> areaList = getSpecialCbcAreas();
        for (final SpecialArea area : areaList) {
            boolean result = area.getPaoId().equals(deviceId);
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
                Collections.sort(subsForArea,CapControlUtils.CCNAME_COMPARATOR );
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
                Collections.sort(subsForArea,CapControlUtils.CCNAME_COMPARATOR );
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

            Collections.sort(allBanks, CapControlUtils.CCNAME_COMPARATOR );
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

            Collections.sort( allFeeders, CapControlUtils.CCNAME_COMPARATOR );
            return allFeeders;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * Distinct area Strings that are used by substations
     * @return List
     */
    public synchronized List<Area> getCbcAreas() {
        List<Area> list = new ArrayList<Area>(cbcAreaMap.values());
        Collections.sort(list, CapControlUtils.CBC_AREA_COMPARATOR);
        return list;
    }
    
    /**
     * Distinct special area Strings that are used by substations
     * @return List
     */
    public synchronized List<SpecialArea> getSpecialCbcAreas() {
        List<SpecialArea> list = new ArrayList<SpecialArea>(cbcSpecialAreaMap.values());
        Collections.sort(list, CapControlUtils.CBC_SPECIAL_AREA_COMPARATOR);
        return list;
    }
    
    /**
     * State group & states to use for CapBanks
     * @return LiteState
     */
    public LiteState getCapBankState( int rawState ) {
        return stateDao.findLiteState( StateGroupUtils.STATEGROUPID_CAPBANK, rawState );
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
     * Returns the Parent SubStation ID for the given child id
     */
    @Override
    public synchronized int getParentSubStationId(int childId) {
        if (isSubstation(childId)) {
            return childId;
        } else if(isSubBus(childId)) {
            return getSubBus(childId).getParentID();
        } else if( isFeeder(childId) ) {
            return getSubBus(getFeeder(new Integer(childId)).getParentID()).getParentID();
        } else if( isCapBank(childId) ) {
            return getSubBus(getFeeder(new Integer(getCapBankDevice(new Integer(childId)).getParentID())).getParentID()).getParentID();
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
    * @param SubAreas
    */
    private synchronized void handleSpecialAreaList(SpecialAreas areas) {
        clearCacheMap(cbcSpecialAreaMap);
        
        List<SpecialArea> list = areas.getAreas();
        for (final SpecialArea area : list) {
        	int areaId = area.getPaoId();
            cbcSpecialAreaMap.put(areaId, area);
            getUpdatedObjMap().handleCBCChangeEvent(area);
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
     * Removes this area from all the structures in cache. 
     * @param msg
     */
    private synchronized void handleDeletedArea(int id) {   
        cbcAreaMap.remove(id);
        getUpdatedObjMap().remove(id);
    }
    
    /**
     * Process multiple SubBuses
     * @param busesMsg
     */
    private synchronized void handleSubBuses( SubstationBuses busesMsg ) {
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
    private synchronized void handleSubStations( SubStations stationsMsg ) {
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
    
    /**
    * Process multiple Areas
    * @param areaMsg
    */
   private synchronized void handleAreas( SubAreas areasMsg ) {
   	logAllAreas(areasMsg);
       //If this is a full reload of all subs.
   	if (areasMsg.isAllAreas()) {
   	    clearCacheMap(cbcAreaMap);
       }
       else if( areasMsg.isUpdateAreas()){
       	//If this is an update to an existing sub.
           handleDeletedAreas(areasMsg);
       }
       //add the each area to the cache
       handleAllArea(areasMsg);
   }

    
    private synchronized void handleDeletedSubs(SubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ){
        	//remove old
        	SubBus bus = busesMsg.getSubBusAt(i);
        	handleDeletedFeeders( bus.getCcFeeders() );
        	handleDeletedSub( bus.getCcId() );
        }
    }
    
    private synchronized void handleDeletedSubStations(SubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ){
        	//remove old
        	handleDeletedSubstation( stationsMsg.getSubAt(i).getCcId() );
        }
    }
    
    private synchronized void handleDeletedAreas(SubAreas areaMsg) {
        for( int i = 0; i < areaMsg.getNumberOfAreas(); i++ ){
        	//remove old
        	handleDeletedArea( areaMsg.getArea(i).getCcId() );
        }
    }
    
    private synchronized void logAllSubs(SubstationBuses busesMsg) {
        if(!log.isDebugEnabled()) {
            return;
        }
        
        for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- ){
        	log.debug("Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
        			+ "/" + busesMsg.getSubBusAt(i).getCcArea() );
        }
    }
    
    private synchronized void logAllSubStations(SubStations stationsMsg) {
        if(!log.isDebugEnabled()) {
            return;
        }
        
        for( int i = (stationsMsg.getNumberOfStations()-1); i >= 0; i-- ){
        	log.debug("Received SubStations - " + stationsMsg.getSubAt(i).getCcName() 
        			+ "/" + stationsMsg.getSubAt(i).getCcArea() );
        }
    }  
    
    private synchronized void logAllAreas(SubAreas areasMsg) {
        if(!log.isDebugEnabled()) {
            return;
        }
        
        for( int i = (areasMsg.getNumberOfAreas()-1); i >= 0; i-- ){
        	log.debug("Received Areas - " + areasMsg.getArea(i).getCcName());
        }
    }   
    
    private synchronized void handleAllSubs(SubstationBuses busesMsg) {
        for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ ) {
        	handleSubBus( busesMsg.getSubBusAt(i) );
        }
    }
    
    private synchronized void handleAllSubStation(SubStations stationsMsg) {
        for( int i = 0; i < stationsMsg.getNumberOfStations(); i++ ) {
        	handleSubStation( stationsMsg.getSubAt(i) );
        }
    }
    
    private synchronized void handleAllArea(SubAreas areasMsg) {
        for( int i = 0; i < areasMsg.getNumberOfAreas(); i++ ) {
        	handleArea( areasMsg.getArea(i) );
        }
    }
    
    private void handleVoltageRegulator(VoltageRegulatorFlagMessage regulatorMessage) {
        List<VoltageRegulatorFlags> regulators = regulatorMessage.getVoltageRegulatorFlags();
        
        for (VoltageRegulatorFlags regulator : regulators) {
            Validate.notNull(regulator, "regulator can't be null");
    
            //remove the old regulator
            final Integer regulatorId = regulator.getCcId();
            removeFromCacheMap(voltageRegulatorMap, regulatorId);
            voltageRegulatorMap.put(regulatorId, regulator);
            
            getUpdatedObjMap().handleCBCChangeEvent(regulator);
        }
    }
    
    private synchronized void handleDeleteItem(int deviceId) {
        if( isSubBus(deviceId) ) {
            handleDeletedSub( deviceId );
        } else if (isFeeder(deviceId)) {
            handleDeletedFeeder(deviceId);
        } else if (isCapBank(deviceId)) {
            handleDeletedCap(deviceId);
        } else if (isCBCArea (deviceId)) {
            handleDeleteArea (deviceId);
        } else if (isSpecialCBCArea(deviceId)) {
            handleDeleteSpecialArea(deviceId);
        }
    }
    
    private synchronized void handleDeleteArea(int deviceId) {
        removeFromCacheMap(cbcAreaMap, deviceId);
    }
    
    private synchronized void handleDeleteSpecialArea(int deviceId) {
        removeFromCacheMap(cbcSpecialAreaMap, deviceId);
    }
    
    private synchronized void handleDeletedCaps( Vector<CapBankDevice> vec ) {
    	for( CapBankDevice cap : vec ) {
    		handleDeletedCap(cap.getCcId());
    	}
    }

    private synchronized void handleDeletedCap(int deviceId) {
        removeFromCacheMap(capBankMap, deviceId);
    }
    
    private synchronized void handleDeletedFeeders( Vector<Feeder> v ) {
    	for( Feeder f : v ) {
    		handleDeletedCaps( f.getCcCapBanks() );
    		handleDeletedFeeder(f.getCcId());
    	}
    }
    
    private synchronized void handleDeletedFeeder(int deviceId) {
        removeFromCacheMap(feederMap, deviceId);    
    }
    
    private void handleSystemStatus(SystemStatus status) {
        synchronized (systemStatusOn ) {
            systemStatusOn = status.isEnabled();
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
        getUpdatedObjMap().handleCBCChangeEvent(subBus);
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
        
        getUpdatedObjMap().handleCBCChangeEvent(sub);
    }
    
    /**
     * Processes a single SubStation.
     * @param SubStation
     */
    private synchronized void handleArea( Area area ) {   
        Validate.notNull(area, "area can't be null");

        //remove the old sub from the area hashmap just in case the area changed
        final Integer areaId = area.getCcId();
        removeFromCacheMap(cbcAreaMap, areaId);
        cbcAreaMap.put(areaId, area);
    
        //not linking to sub busses, feeders, and capbanks.
        
        getUpdatedObjMap().handleCBCChangeEvent(area);
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
        log.debug("Refreshing CapControl Cache");
        
        try  {
            getConnection().sendCommand(new CapControlCommand(CommandType.REQUEST_ALL_DATA.getCommandId()));
        } catch (Exception e) {
            log.error("Exception occurred during a refresh_cache operation", e);
            return false;
        }
        return true;
    }
    
    /**
     * Handles any type of message received from the connection.
     * @param MessageEvent
     */
    public synchronized void messageReceived(MessageEvent event) {
        Message message = event.getMessage();
        
        if (message instanceof SubstationBuses) {
            handleSubBuses((SubstationBuses) message);
        } else if (message instanceof SubStations) {
            handleSubStations((SubStations) message);
        } else if (message instanceof SpecialAreas) {
            handleSpecialAreaList((SpecialAreas) message);
        } else if (message instanceof SubAreas) {
        	handleAreas((SubAreas) message);
        } else if (message instanceof DeleteItem) {
            handleDeleteItem(((DeleteItem)message).getItemId());
        } else if (message instanceof SystemStatus) {
            handleSystemStatus((SystemStatus)message);
        } else if (message instanceof VoltageRegulatorFlagMessage) {
            handleVoltageRegulator((VoltageRegulatorFlagMessage)message);
        }
    }
    
    public synchronized Area getCBCArea(int id) throws NotFoundException {
        Area area = cbcAreaMap.get(id);
        checkObjectFound(area, id, Area.class);
        return area;
    }
    
    public synchronized VoltageRegulatorFlags getVoltageRegulatorFlags(int id) throws NotFoundException {
        VoltageRegulatorFlags regulatorFlags = voltageRegulatorMap.get(id);
        checkObjectFound(regulatorFlags, id, VoltageRegulatorFlags.class);
        return regulatorFlags;
    }
    
    public synchronized SpecialArea getCBCSpecialArea(int id) throws NotFoundException {
        SpecialArea area = cbcSpecialAreaMap.get(id);
        checkObjectFound(area, id, SpecialArea.class);        
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

        object = voltageRegulatorMap.get(id);
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

    public List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id) {
    	
    	List<CapBankDevice> deviceList = null;
    	
    	switch(type) {
    		case AREA:
    			deviceList = getCapBanksByArea(id);
    			break;
    		case SUBSTATION:
    			SubStation station = getSubstation(id);
    			deviceList = getCapBanksBySubStation(station);
    			break;
    		case SUBBUS:
    			deviceList = getCapBanksBySubBus(id);
    			break;
    		case FEEDER:
    			deviceList = getCapBanksByFeeder(id);
    			break;    		
    	}
    	
    	return deviceList;
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
    
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    public void setDefCapControlConn(IServerConnection defCapControlConn) {
        this.defCapControlConn = defCapControlConn;
    }

    public void setRefreshTimer(ScheduledExecutor refreshTimer) {
        this.refreshTimer = refreshTimer;
    }
}