package com.cannontech.cbc.cache.impl;

/**
 * Maintains information from the capcontrol server
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.CapControlType;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class CapControlCacheImpl implements MessageListener, CapControlCache {
    
    private static final Logger log = YukonLogManager.getLogger(CapControlCacheImpl.class);
    
    private static final int STARTUP_REF_RATE = 15 * 1000;
    private static final int NORMAL_REF_RATE = 30 * 60 * 1000; //30 minutes
    
    private Map<Integer, Area> areas = new ConcurrentHashMap<>();
    private Map<Integer, SpecialArea> specialAreas = new ConcurrentHashMap<>();
    private Map<Integer, SubStation> substations = new ConcurrentHashMap<>();
    private Map<Integer, SubBus> subbuses = new ConcurrentHashMap<>();
    private Map<Integer, Feeder>  feeders = new ConcurrentHashMap<>();
    private Map<Integer, CapBankDevice> banks = new ConcurrentHashMap<>();
    private Map<Integer, VoltageRegulatorFlags> regulators = new ConcurrentHashMap<>();
    
    private Map<Integer, List<Integer>> subbusToBanks = new ConcurrentHashMap<>();
    private CBCWebUpdatedObjectMap updatedObjects;
    
    private boolean systemStatusOn = true;

    @Autowired private StateDao stateDao;
    @Autowired @Qualifier("cbc") private IServerConnection serverConnection;
    @Autowired @Qualifier("main") private ScheduledExecutor refreshTimer;

    @PostConstruct
    public void initialize() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        };
        serverConnection.addMessageListener(this);
        refreshTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, NORMAL_REF_RATE, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public synchronized StreamableCapObject getStreamableArea(final int areaId) throws NotFoundException {
        StreamableCapObject object;
        try {
            object = getArea(areaId);
        } catch (NotFoundException nfe) {
            object = getSpecialArea(areaId);
        }
        return object;
    }
    
    @Override
    public synchronized SubBus getSubBus(int subbusId) throws NotFoundException {
        SubBus bus = subbuses.get(subbusId);
        checkObjectFound(bus, subbusId, SubBus.class);
        return bus;
    }
    
    @Override
    public synchronized SubStation getSubstation(int substationId) throws NotFoundException {
        SubStation station = substations.get(substationId);
        checkObjectFound(station, substationId, SubStation.class);
        return station;
    }
    
    @Override
    public synchronized String getSubBusNameForFeeder(Feeder feeder) {
        Validate.notNull(feeder,"Feeder cannot be null, method: getSubBusNameForFeeder");
        
        int parentId = feeder.getParentID();
        
        if (parentId > 0) {
            SubBus bus = subbuses.get(parentId);
            if (bus != null) return bus.getCcName();
        }
        
        return null;
    }
    
    @Override
    public synchronized StreamableCapObject getCapControlPao(int paoId) {
        StreamableCapObject retObj = areas.get(paoId);
        if (retObj == null) retObj = specialAreas.get(paoId);
        if (retObj == null) retObj = substations.get(paoId);
        if (retObj == null) retObj = subbuses.get(paoId);
        if (retObj == null) retObj = feeders.get(paoId);
        if (retObj == null) retObj = banks.get(paoId);
        if (retObj == null) retObj = regulators.get(paoId);
        
        return retObj;
    }
    
    @Override
    public synchronized Feeder getFeeder(int feederId) throws NotFoundException {
        Feeder feeder = feeders.get(feederId);
        checkObjectFound(feeder, feederId, Feeder.class);
        return feeder;
    }
    
    @Override
    public synchronized CapBankDevice getCapBankDevice(int bankId) throws NotFoundException {
        CapBankDevice cap = banks.get(bankId);
        checkObjectFound(cap, bankId, CapBankDevice.class);
        return cap;
    }
    
    @Override
    public List<Feeder> getFeedersBySubBus(int subbusId) {
        SubBus subBus = getSubBus(subbusId);
        try {
            return subBus.getCcFeeders();
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public synchronized List<Feeder> getFeedersBySubStation(SubStation substation) {
        Validate.notNull(substation, "Substation cannot be null");
        
        int[] subBusIds = substation.getSubBusIds();
        List<Feeder> substationFeeders = new ArrayList<>();
        
        for (final int id : subBusIds) {
            try {
                SubBus subBus = getSubBus(id);
                substationFeeders.addAll(subBus.getCcFeeders());
            } catch (NotFoundException ignore) {}
        }

        return substationFeeders;
    }
    
    @Override
    public synchronized List<CapBankDevice> getCapBanksBySubStation(SubStation substation) {
        Validate.notNull(substation, "Substation cannot be null");

        int[] subBusIds = substation.getSubBusIds();
        List<CapBankDevice> capBanks = new ArrayList<>();

        for (final int id : subBusIds) {
            List<CapBankDevice> caps = getCapBanksBySubBus(id);
            capBanks.addAll(caps);
        }

        return capBanks;
    }
    
    @Override
    public List<CapBankDevice> getCapBanksByFeeder(int feederId) {
        try {
            Feeder feeder = getFeeder(feederId);
            return feeder.getCcCapBanks();
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public boolean isArea(int areaId) {
        try {
            getStreamableArea(areaId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public synchronized boolean isSpecialArea(int areaId) {
        try {
            getSpecialArea(areaId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isSubBus(int subbusId) {
        try {
            getSubBus(subbusId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isSubstation(int substationId) {
        try {
            getSubstation(substationId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }    
    
    @Override
    public boolean isFeeder(int feederId) {
        try {
            getFeeder(feederId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isCapBank(int bankId) {
        try {
            getCapBankDevice(bankId);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public synchronized List<CapBankDevice> getCapBanksBySubBus(int subbusId) throws NotFoundException {
        List<Integer> bankIds = subbusToBanks.get(subbusId);
        
        if (bankIds == null) return Collections.emptyList();

        List<CapBankDevice> retVal = new ArrayList<>();
        
        for (final int bankId : bankIds) {
            try {
                CapBankDevice capBank = getCapBankDevice(bankId); 
                retVal.add(capBank);
            } catch (NotFoundException ignore) {}     
        }
        return retVal;
    }
    
    @Override
    public synchronized List<SubBus> getSubBusesByArea(int areaId) throws NotFoundException {
        
        try {
            getArea(areaId);
            try {
                List<SubBus> subsForArea = new ArrayList<SubBus>();
                List<SubStation> allAreaSubstations = getSubstationsByArea(areaId);
                for (SubStation substation : allAreaSubstations) {
                    List<SubBus> subbuses = getSubBusesBySubStation(substation);
                    subsForArea.addAll(subbuses);
                }
                Collections.sort(subsForArea,CapControlUtils.CCNAME_COMPARATOR);
                return subsForArea;
            } catch (NotFoundException e) {
                return Collections.emptyList();
            }
        } catch (NotFoundException areaNotFoundTrySpecialArea) {
            getSpecialArea(areaId);
            try {
                List<SubBus> subsForArea = new ArrayList<SubBus>();
                List<SubStation> allAreaSubstations = getSubstationsBySpecialArea(areaId);
                for (SubStation substation : allAreaSubstations) {
                    List<SubBus> subbuses = getSubBusesBySubStation(substation);
                    subsForArea.addAll(subbuses);
                }
                Collections.sort(subsForArea,CapControlUtils.CCNAME_COMPARATOR);
                return subsForArea;
            } catch (NotFoundException e) {
                return Collections.emptyList();
            }
        }
    }
    
    @Override
    public synchronized List<SubBus> getAllBuses() {
        
        return new ArrayList<>(subbuses.values());
    }
    
    @Override
    public synchronized List<SubBus> getSubBusesBySubStation(SubStation substation) {
        List<SubBus> buses = new ArrayList<SubBus>();
        if(substation != null) {
            int[] busIds = substation.getSubBusIds();
            buses = new ArrayList<SubBus>(busIds.length);
    
            for (final int id : busIds) {
                try {
                    SubBus subBus = getSubBus(id);
                    buses.add(subBus);
                } catch (NotFoundException ignore) {} 
            }
        }
        
        return buses;
    }
    
    @Override
    public synchronized List<CapBankDevice> getCapBanksByArea(int areaId) {
        try {
            List<SubBus> subs = getSubBusesByArea(areaId);
            List<CapBankDevice> allBanks = new ArrayList<CapBankDevice>(subs.size());

            for (final SubBus subBus : subs) {
                if (subBus == null) continue;
                try {
                    List<CapBankDevice> capBanks = getCapBanksBySubBus(subBus.getCcId());        
                    allBanks.addAll(capBanks);
                } catch (NotFoundException ignore) {}
            }

            Collections.sort(allBanks, CapControlUtils.CCNAME_COMPARATOR);
            return allBanks;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public synchronized List<Feeder> getFeedersByArea(int areaId) {
        try {
            List<SubBus> subs = getSubBusesByArea(areaId);
            List<Feeder> allFeeders = new ArrayList<Feeder>(subs.size());

            for (final SubBus subBus : subs) {
                try {
                    List<Feeder> feeders = getFeedersBySubBus(subBus.getCcId());
                    allFeeders.addAll(feeders);
                } catch (NotFoundException ignore) {}
            }

            Collections.sort(allFeeders, CapControlUtils.CCNAME_COMPARATOR);
            return allFeeders;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public synchronized List<Area> getAreas() {
        List<Area> list = new ArrayList<Area>(areas.values());
        Collections.sort(list, CapControlUtils.CCNAME_COMPARATOR);
        return list;
    }
    
    @Override
    public synchronized List<SpecialArea> getSpecialAreas() {
        List<SpecialArea> list = new ArrayList<SpecialArea>(specialAreas.values());
        Collections.sort(list, CapControlUtils.CCNAME_COMPARATOR);
        return list;
    }
    
    @Override
    public LiteState getCapBankState(int rawState) {
        return stateDao.findLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, rawState);
    }
    
    @Override
    public synchronized int getParentSubBusId(int childId) throws NotFoundException {
        if (isSubBus(childId)) {
            return childId;
        } else if (isFeeder(childId)) {
            return getFeeder(childId).getParentID();
        } else if (isCapBank(childId)) {
            return getFeeder(getCapBankDevice(childId).getParentID()).getParentID();
        }
        throw new NotFoundException("could not find SubBus from child id of " + childId);
    }
    
    @Override
    public synchronized int getParentFeederId(int childId) throws NotFoundException {
        if (isFeeder(childId)) {
            return childId;
        } else if (isCapBank(childId)) {
            return getCapBankDevice(childId).getParentID();
        }
        throw new NotFoundException("could not find SubBus from child id of " + childId);
    }

    @Override
    public synchronized SubBus getParentSubBus(int childId) throws NotFoundException {
        return getSubBus(getParentSubBusId(childId));
    }
    
    @Override
    public synchronized Feeder getParentFeeder(int childId) throws NotFoundException {
        return getFeeder(getParentFeederId(childId));
    }
    
    @Override
    public synchronized SubStation getParentSubstation(int childId) throws NotFoundException {
        return getSubstation(getParentSubStationId(childId));
    }
    
    @Override
    public synchronized int getParentSubStationId(int childId) {
        if (isSubstation(childId)) {
            return childId;
        } else if (isSubBus(childId)) {
            return getSubBus(childId).getParentID();
        } else if (isFeeder(childId)) {
            return getSubBus(getFeeder(new Integer(childId)).getParentID()).getParentID();
        } else if (isCapBank(childId)) {
            return getSubBus(getFeeder(new Integer(getCapBankDevice(new Integer(childId)).getParentID())).getParentID()).getParentID();
        } else {
            return CtiUtilities.NONE_ZERO_ID;
        }
    }
    
    @Override
    public synchronized int getParentAreaId(int childId) throws NotFoundException {

        if (isArea(childId)) {
            return childId;
        }

        int id;
        SubStation station = null;
        try {
            if (isSubstation(childId)) {
                station = getSubstation(childId);
            } else if (isSubBus(childId)) {
                station = getSubstation(getSubBus(childId).getParentID());
            } else if (isFeeder(childId)) {
                station = getSubstation(getSubBus(getFeeder(childId).getParentID()).getParentID());
            } else if (isCapBank(childId)) {
                station = getSubstation(getSubBus(getFeeder(getCapBankDevice(childId).getParentID()).getParentID()).getParentID());
            }
        } catch (NotFoundException exception) {
            //Some level in the heirarchy does not exist. This means it is an orphan.
        }

        if (station == null) {
            id = CtiUtilities.NONE_ZERO_ID;
        } else {
            id = station.getParentID();
            if (id < 0) {
                id = station.getSpecialAreaId();
            }
        }
        return id;
    }
    
    private synchronized void handleSpecialAreaList(SpecialAreas areas) {
        clearCacheMap(specialAreas);
        
        List<SpecialArea> list = areas.getAreas();
        for (final SpecialArea area : list) {
            int areaId = area.getCcId();
            specialAreas.put(areaId, area);
            getUpdatedObjects().handleCBCChangeEvent(area);
        }
    }
    
    /**
     * Removes this subbus from all the structures in cache. 
     */
    private synchronized void handleDeletedSubBus(int subbusId) {

        SubBus bus = subbuses.get(subbusId);
        if (bus != null) {
            for (Feeder feeder : bus.getCcFeeders()) {
                for (CapBankDevice cap : feeder.getCcCapBanks()) {
                    handleDeletedCap(cap.getCcId());
                }
                handleDeletedFeeder(feeder.getCcId());
            }
        }

        subbusToBanks.remove(subbusId);
        removeFromCacheMap(subbuses, subbusId);
    }
    
    /**
     * Removes this subbus from all the structures in cache. 
     */
    private synchronized void handleDeletedSubstation(int substationId) {

        SubStation substation = substations.get(substationId);
        if (substation != null) {
            for (int bus : substation.getSubBusIds()) {
                handleDeletedSubBus(bus);
            }
        }

        removeFromCacheMap(substations, substationId);
    }

    private synchronized void handleDeletedArea(int areaId) {

        Area area = areas.get(areaId);
        if (area != null) {
            for (int stationId : area.getStations()) {
                handleDeletedSubstation(stationId);
            }
        }

        removeFromCacheMap(areas, areaId);
    }
    
    /**
     * Process multiple SubBuses
     */
    private synchronized void handleSubBuses(SubstationBuses busesMsg) {
        logAllSubs(busesMsg);
        //If this is a full reload of all subs.
        if (busesMsg.isAllSubs()) {
            
            for (int busId : subbuses.keySet()) {
                handleDeletedSubBus(busId);
            }
            clearCacheMap(feeders);
            clearCacheMap(banks);
            clearCacheMap(subbusToBanks);
        }

        for (int i = 0; i < busesMsg.getNumberOfBuses(); i++) {
            handleSubBus(busesMsg.getSubBusAt(i));
        }
    }

    private synchronized void handleSubStations(SubStations stationsMsg) {
        logAllSubStations(stationsMsg);

        if (stationsMsg.isAllSubStations()) {
            //Remove stations and their descendants if they are not in the message
            Set<Integer> messageIds = new HashSet<>();
            for (SubStation station : stationsMsg.getSubstations()) {
                messageIds.add(station.getCcId());
            }
            Set<Integer> existingIds = new HashSet<>(substations.keySet());
            Set<Integer> removedStations = Sets.difference(existingIds, messageIds);

            for (int stationId : removedStations) {
                handleDeletedSubstation(stationId);
            }
        }

        for (int i = 0; i < stationsMsg.getNumberOfStations(); i++) {
            handleSubStation(stationsMsg.getSubAt(i));
        }
    }

   private synchronized void handleAreas(SubAreas areasMsg) {
       logAllAreas(areasMsg);

       if (areasMsg.isAllAreas()) {
           //Remove areas and their descendants if they are not in the message
           Set<Integer> messageIds = new HashSet<>();
           for (Area area : areasMsg.getAreas()) {
               messageIds.add(area.getCcId());
           }
           Set<Integer> existingIds = new HashSet<>(areas.keySet());
           Set<Integer> removedAreaIds = Sets.difference(existingIds, messageIds);

           for (int areaId : removedAreaIds) {
               handleDeletedArea(areaId);
           }
       }

       for (int i = 0; i < areasMsg.getNumberOfAreas(); i++) {
           handleArea(areasMsg.getArea(i));
       }
   }

    private synchronized void logAllSubs(SubstationBuses busesMsg) {
        if (!log.isDebugEnabled()) {
            return;
        }
        
        for (int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i--) {
            log.debug("Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
                    + "/" + busesMsg.getSubBusAt(i).getCcArea());
        }
    }
    
    private synchronized void logAllSubStations(SubStations stationsMsg) {
        if (!log.isDebugEnabled()) {
            return;
        }
        
        for (int i = (stationsMsg.getNumberOfStations()-1); i >= 0; i--) {
            log.debug("Received SubStations - " + stationsMsg.getSubAt(i).getCcName() 
                    + "/" + stationsMsg.getSubAt(i).getCcArea());
        }
    }  
    
    private synchronized void logAllAreas(SubAreas areasMsg) {
        if (!log.isDebugEnabled()) {
            return;
        }
        
        for (int i = (areasMsg.getNumberOfAreas()-1); i >= 0; i--) {
            log.debug("Received Areas - " + areasMsg.getArea(i).getCcName());
        }
    }   

    private void handleVoltageRegulator(VoltageRegulatorFlagMessage regulatorMessage) {
        List<VoltageRegulatorFlags> flags = regulatorMessage.getVoltageRegulatorFlags();
        
        for (VoltageRegulatorFlags flag : flags) {
            Validate.notNull(flag, "regulator can't be null");
    
            regulators.put(flag.getCcId(), flag);
            
            getUpdatedObjects().handleCBCChangeEvent(flag);
        }
    }
    
    @Override
    public synchronized void handleDeleteItem(int id) {
        if (isSubBus(id)) {
            handleDeletedSubBus(id);
        } else if (isFeeder(id)) {
            handleDeletedFeeder(id);
        } else if (isCapBank(id)) {
            handleDeletedCap(id);
        } else if (isArea (id)) {
            handleDeletedArea (id);
        } else if (isSpecialArea(id)) {
            handleDeleteSpecialArea(id);
        } else if (isSubstation(id)) {
            handleDeletedSubstation(id);
        }
    }

    private synchronized void handleDeleteSpecialArea(int areaId) {
        removeFromCacheMap(specialAreas, areaId);
    }

    private synchronized void handleDeletedCap(int bankId) {

        for (Entry<Integer, List<Integer>> entry : subbusToBanks.entrySet()) {

            List<Integer> values = new ArrayList<>(entry.getValue());
            if (values.removeAll(ImmutableList.of(bankId))) {
                subbusToBanks.put(entry.getKey(), values);
            }
        }

        removeFromCacheMap(banks, bankId);
    }
    
    private synchronized void handleDeletedFeeder(int feederId) {
        removeFromCacheMap(feeders, feederId);
    }
    
    private void handleSystemStatus(SystemStatus status) {
        systemStatusOn = status.isEnabled();
    }
    
    /**
     * Processes a single SubBus, breaking it up into Feeders and Cap Banks
     */
    private synchronized void handleSubBus(SubBus subbus) {
        
        Validate.notNull(subbus, "subBus can't be null");

        final Integer subBusId = subbus.getCcId();
        
        subbuses.put(subBusId, subbus);

        List<Integer> capBankIds = new ArrayList<>();
        for (final Feeder feeder : subbus.getCcFeeders()) {
            feeders.put(feeder.getCcId(), feeder);
            
            for (final CapBankDevice capBank : feeder.getCcCapBanks()) {
                Integer capBankId = capBank.getCcId();
                banks.put(capBankId, capBank);
                capBankIds.add(capBankId);
            }
        }
        
        subbusToBanks.put(subBusId, capBankIds);
        getUpdatedObjects().handleCBCChangeEvent(subbus);
    }
    
    /**
     * Processes a single SubStation.
     */
    private synchronized void handleSubStation(SubStation substation) {
        Validate.notNull(substation, "substation can't be null");

        int subStationId = substation.getCcId();
        substations.put(subStationId, substation);

        getUpdatedObjects().handleCBCChangeEvent(substation);
    }
    
    /**
     * Processes a single SubStation.
     */
    private synchronized void handleArea(Area area) {
        Validate.notNull(area, "area can't be null");

        areas.put(area.getCcId(), area);

        getUpdatedObjects().handleCBCChangeEvent(area);
    }
    
    @Override
    public CapControlClientConnection getConnection() {
        return (CapControlClientConnection) serverConnection;
    }
    
    @Override
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
    
    @Override
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
    
    @Override
    public synchronized Area getArea(int areaId) throws NotFoundException {
        Area area = areas.get(areaId);
        checkObjectFound(area, areaId, Area.class);
        return area;
    }
    
    @Override
    public synchronized VoltageRegulatorFlags getVoltageRegulatorFlags(int regulatorId) throws NotFoundException {
        VoltageRegulatorFlags regulatorFlags = regulators.get(regulatorId);
        checkObjectFound(regulatorFlags, regulatorId, VoltageRegulatorFlags.class);
        return regulatorFlags;
    }
    
    @Override
    public synchronized SpecialArea getSpecialArea(int specialAreaId) throws NotFoundException {
        SpecialArea area = specialAreas.get(specialAreaId);
        checkObjectFound(area, specialAreaId, SpecialArea.class);
        return area;
    }
    
    @Override
    public synchronized StreamableCapObject getObject(int id) throws NotFoundException {
        StreamableCapObject object = areas.get(id);
        if (object != null) return object;
        
        object = specialAreas.get(id);
        if (object != null) return object;
        
        object = substations.get(id);
        if (object != null) return object;
        
        object = subbuses.get(id);
        if (object != null) return object;
        
        object = feeders.get(id);
        if (object != null) return object;
        
        object = banks.get(id);
        if (object != null) return object;

        object = regulators.get(id);
        if (object != null) return object;
        
        throw new NotFoundException("StreamableCapObject with id: " + id + " not found.");
    }
    
    @Override
    public synchronized CBCWebUpdatedObjectMap getUpdatedObjects() {
        if (updatedObjects == null)
            updatedObjects = new CBCWebUpdatedObjectMap();
        return updatedObjects;
    }
    
    @Override
    public boolean getSystemStatusOn() {
        return systemStatusOn;
    }

    @Override
    public synchronized List<SubStation> getSubstationsByArea(int areaId) throws NotFoundException {
        try {
            Area area = getArea(areaId);

            List<SubStation> subList = new ArrayList<SubStation>();
            for (int stationId : area.getStations()) {
                SubStation sub = substations.get(stationId);
                if (sub != null) {
                    subList.add(sub);
                }
            }
            return subList;
        } catch (NotFoundException areaNotFoundTryingSpecialArea) {
            SpecialArea specialArea = getSpecialArea(areaId);

            List<SubStation> subList = new ArrayList<SubStation>();
            for (int stationId : specialArea.getCcSubIds()) {
                SubStation sub = substations.get(stationId);
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
    
    @Override
    public List<CapBankDevice> getCapBanksBySpecialArea(int areaId) {
        return getCapBanksByArea(areaId);
    }
    
    @Override
    public List<SubStation> getSubstationsBySpecialArea(int areaId) {
        return getSubstationsByArea(areaId);
    }

    @Override
    public List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id) {
        
        List<CapBankDevice> banks = null;
        
        switch(type) {
            case AREA:
                banks = getCapBanksByArea(id);
                break;
            case SUBSTATION:
                SubStation station = getSubstation(id);
                banks = getCapBanksBySubStation(station);
                break;
            case SUBBUS:
                banks = getCapBanksBySubBus(id);
                break;
            case FEEDER:
                banks = getCapBanksByFeeder(id);
                break;
            default:
                throw new IllegalArgumentException("Illegal Type for Cap Bank Parent: " + type);
        }
        
        return banks;
    }
    
    private void clearCacheMap(final Map<Integer, ?> map) {
        Set<Integer> keySet = map.keySet();
        getUpdatedObjects().remove(keySet);
        map.clear();
    }
    
    private void removeFromCacheMap(final Map<Integer, ?> map, final Integer id) {
        map.remove(id);
        getUpdatedObjects().remove(id);
    }
    
}