package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.cbc.cyme.CymeConfigurationException;
import com.cannontech.cbc.cyme.CymePointDataCache;
import com.cannontech.cbc.cyme.CymeSimulationListener;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IServerConnection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CymePointDataCacheImpl implements CymePointDataCache, PointDataListener, DBChangeListener {
    
    private static final Logger log = YukonLogManager.getLogger(CymePointDataCacheImpl.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;  
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private PointDao pointDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private PaoDao paoDao;
    @Autowired private IServerConnection dispatchConnection;
    
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private List<CymeSimulationListener> cymeListeners = Lists.newArrayList();

    /* start Caching maps */
    private Map<Integer,PointPaoIdentifier> pointIdToPointPaoIdentifier;
    private Map<Integer,PointValueQualityHolder> pointIdToValue;
    
    private Map<Integer,Integer> busToEnabledPointIds;
    private Map<Integer,Integer> loadValuePointIdToBusId;
    private Map<Integer,Integer> simulationPointIdToBusId;
    private Map<Integer,Integer> bankStatusPointIdToBusId;
    
    private Set<Integer> subBusPoints;
    private Map<Integer,Integer> zoneIdToBusId;
    private PaoIdentifier cacheBusPao;
    
    /* end Caching maps */
    
    private void resetEntireCache() {
        pointIdToPointPaoIdentifier = new ConcurrentHashMap<Integer,PointPaoIdentifier>();
        pointIdToValue = new ConcurrentHashMap<Integer,PointValueQualityHolder>();
        
        busToEnabledPointIds = new ConcurrentHashMap<Integer,Integer>();
        loadValuePointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
        simulationPointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
        bankStatusPointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
        
        subBusPoints = Sets.newHashSet();
        zoneIdToBusId = new ConcurrentHashMap<Integer,Integer>();
        asyncDynamicDataSource.unRegisterForPointData(this);
        cacheBusPao = null;
    }
    
    @PostConstruct
    public void init() {
        boolean enabled = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.CYME_ENABLED,false);
        if (enabled) {
            asyncDynamicDataSource.addDBChangeListener(this);
        } else {
            log.debug("CYME_ENABLED CPARM is false. Ignoring DB Changes");            
        }
    }
    
    @Override
    public void registerListener(CymeSimulationListener listener) { 
        cymeListeners.add(listener);        
    }
    
    @Override
    public void registerPointsForSubStationBus(PaoIdentifier subbus) {
        log.debug("Starting Registering for bus with paoId:" + subbus.getPaoId());
        //TODO marker for YUK-11478
        //Clean maps first.
        resetEntireCache();
        
        //TODO marker for YUK-11478        
        cacheBusPao = subbus;
        
        //Now Register
        Set<Integer> pointsToRegister = loadCymeSimulationControlPoints(subbus);
        subBusPoints.addAll(pointsToRegister);
        
        // Determine the Bank Status points to watch for Capcontrol Server changes.
        Collection<PointPaoIdentifier> bankPoints = substationBusDao.getBankStatusPointPaoIdsBySubbusId(subbus.getPaoId());
        for ( PointPaoIdentifier pointPaoId : bankPoints) {
            pointIdToPointPaoIdentifier.put(pointPaoId.getPointId(), pointPaoId);
            bankStatusPointIdToBusId.put(pointPaoId.getPointId(),subbus.getPaoId());
        }
        
        Collection<PointPaoIdentifier> regulatorPoints = zoneDao.getTapPointsBySubBusId(subbus.getPaoId());
        for (PointPaoIdentifier regulator : regulatorPoints) {
            pointIdToPointPaoIdentifier.put(regulator.getPointId(), regulator);
        }
        
        //TODO marker for YUK-11478
        //May not need this later. Just tracking for DB change
        List<Zone> zones = zoneDao.getZonesBySubBusId(subbus.getPaoId());
        for(Zone zone : zones) {
            zoneIdToBusId.put(zone.getId(),subbus.getPaoId());
        }
        
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {            
            @Override
            public void run() {
                if(registerPoints()) {
                    log.info("Dispatch connection online, registered points successfully.");
                   throw new RuntimeException();//Terminates future calls 
                } else {
                    log.info("Could not register for point updates. Dispatch is not connected. Attempting to register again in 30 seconds.");
                }
            }
        }, 0,30,TimeUnit.SECONDS);
        
        log.debug("Done Registering for bus with paoId:" + subbus.getPaoId());
    }

    private boolean registerPoints() {
        if (dispatchConnection.isValid()) {
            //Register Bank Statuses
            Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getAndRegisterForPointData(this, pointIdToPointPaoIdentifier.keySet());

            for (PointValueQualityHolder pvqh : pointValues) {
                pointIdToValue.put(pvqh.getId(), pvqh);
            }
            
            pointValues = asyncDynamicDataSource.getAndRegisterForPointData(this, subBusPoints);
            for (PointValueQualityHolder pvqh : pointValues) {
                pointIdToValue.put(pvqh.getId(), pvqh);
            }
            return true;
        }
        return false;
    }
    
    private Set<Integer> loadCymeSimulationControlPoints(PaoIdentifier subbus) {
        PointIdentifier cymeEnabled = new PointIdentifier(PointType.Status, 350);
        PointIdentifier startSimulation = new PointIdentifier(PointType.Status, 351);
        PointIdentifier loadFactor = new PointIdentifier(PointType.Analog, 352);
        
        LitePoint enabledPoint;
        LitePoint simulationPoint;
        LitePoint loadPoint;
        
        try {
            enabledPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, cymeEnabled));
            simulationPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, startSimulation));
            loadPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, loadFactor));
        } catch (NotFoundException e) {
            throw new CymeConfigurationException("Error loading CYME control points from Subbus with paoId '"+ subbus.getPaoId() + "' ");
        }
        
        Set<Integer> pointIds = Sets.newHashSet();
        int pointId = enabledPoint.getLiteID();
        busToEnabledPointIds.put(subbus.getPaoId(), pointId);
        pointIds.add(pointId);

        
        pointId = simulationPoint.getLiteID();
        simulationPointIdToBusId.put(pointId, subbus.getPaoId());
        pointIds.add(pointId);
        
        pointId = loadPoint.getLiteID();
        loadValuePointIdToBusId.put(pointId, subbus.getPaoId());
        //Hijacking this into the pointIdToPointPaoIdentifier so the XML builder can identify the load value
        //A better method would be good.
        PointPaoIdentifier pointPaoIdentifier = new PointPaoIdentifier();
        pointPaoIdentifier.setPaoIdentifier(subbus);
        pointPaoIdentifier.setPaoName("LOADPOINT");//Not used by XMLBuilder in subbus case
        pointPaoIdentifier.setPointId(pointId);
        pointIdToPointPaoIdentifier.put(pointId, pointPaoIdentifier);
        pointIds.add(pointId);
        
        return pointIds;
    }
    
    @Override
    public Collection<PointPaoIdentifier> getPaosInSystem() {
        pointIdToPointPaoIdentifier.values();
        return ImmutableList.copyOf(pointIdToPointPaoIdentifier.values());
    }
    
    @Override
    public Map<Integer,PointValueQualityHolder> getCurrentValues() {
        return ImmutableMap.copyOf(pointIdToValue);
    }

    @Override
    public PointValueQualityHolder getCurrentValue(int pointId) {
        return pointIdToValue.get(pointId);
    }
    
    @Override
    public PointValueQualityHolder getCurrentLoadFactor(int subbusId) {
        for (Entry<Integer, Integer> entry : loadValuePointIdToBusId.entrySet()) {
            if (entry.getValue() == subbusId) {
                return pointIdToValue.get(entry.getKey());
            }
        }
        throw new NotFoundException("Simulation Listener it no registered to the cache.");
    }
    
    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        pointIdToValue.put(pointData.getId(), pointData);
        
        Integer subbusId = loadValuePointIdToBusId.get(pointData.getId());
        if (subbusId != null) {
            for (CymeSimulationListener listener : cymeListeners) {
                listener.notifyNewLoadFactor(pointData);
            }
        }
        
        subbusId = simulationPointIdToBusId.get(pointData.getId());
        if (subbusId != null) {
            for (CymeSimulationListener listener : cymeListeners) {
                listener.notifyNewSimulation(pointData);
            }
        }

        subbusId = bankStatusPointIdToBusId.get(pointData.getId());
        if (subbusId != null) {
            for (CymeSimulationListener listener : cymeListeners) {
                listener.notifyCbcControl(pointData);
            }
        }
    }

    @Override
    public boolean isCymeEnabled(int subbusId) {
        Integer pointId = busToEnabledPointIds.get(subbusId);
        if (pointId == null) {
            return false;
        }
        
        PointValueQualityHolder pointData = pointIdToValue.get(pointId);
        if (pointData == null) {
            return false;
        }
        
        TrueFalse state = TrueFalse.getForAnalogValue((int)pointData.getValue());
        return (state == TrueFalse.TRUE)?true:false;
    }
    
    @Override
    public void unRegisterCymeSimulationListener(int subbusId) {
        // TODO Unregister for pData for this listener?
    }

    @Override
    public void dbChangeReceived(DBChangeMsg dbChange) {

        DBChangeMsg.CAT_POINT.equals(dbChange.getCategory());
        
        switch (dbChange.getDatabase()) {
            case DBChangeMsg.CHANGE_POINT_DB:
                handlePointChange(dbChange);
                break;
            case DBChangeMsg.CHANGE_PAO_DB:
                handlePaoChange(dbChange);
                break;
            case DBChangeMsg.CHANGE_IVVC_ZONE:
                handleZoneChange(dbChange);
                break;
        }
    }
    
    private void handlePointChange(DBChangeMsg dbChange) {
        //CYME config points on a bus only.
        int pointId = dbChange.getId();
        switch (dbChange.getDbChangeType()) {
            case DELETE:
                if (! subBusPoints.contains(pointId)) {
                    break;
                } //Purposefully falling through to UPDATE
            case UPDATE:
                //Was this on our radar?
                if (subBusPoints.contains(pointId)) {
                    int busId = 0;
                    if (busToEnabledPointIds.containsValue(pointId)) {
                        for( Entry<Integer, Integer> entry : busToEnabledPointIds.entrySet()) {
                             if (entry.getValue() == pointId) {
                                 busId = entry.getKey();
                                 break;
                             }
                        }
                    } else if (loadValuePointIdToBusId.containsKey(pointId)) {
                        busId = loadValuePointIdToBusId.get(pointId);
                    } else if (simulationPointIdToBusId.containsKey(pointId)) {
                        busId = simulationPointIdToBusId.get(pointId);
                    } else {
                        throw new NotFoundException("Expected to find a Subbus with a CYME point id:" + pointId);
                    }

                    //We are tracking this point.
                    PaoIdentifier subBus = new PaoIdentifier(busId, PaoType.CAP_CONTROL_SUBBUS);
                    registerPointsForSubStationBus(subBus);

                    break;
                } else {
                    //Purposefully falling through
                }                
                //don't break!
            case ADD:
                //is this new point on our bus?
                LitePoint litePoint = pointDao.getLitePoint(pointId);
                if (litePoint.getPaobjectID() == cacheBusPao.getPaoId()) { 
                    registerPointsForSubStationBus(new PaoIdentifier(litePoint.getPaobjectID(), PaoType.CAP_CONTROL_SUBBUS));
                }
            case NONE:
                break;
        }
    }
    
    private void handlePaoChange(DBChangeMsg dbChange) {
        int paoId = dbChange.getId();
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(paoId);
        //Only if it is one of these
        if (liteYukonPAO.getPaoType() == PaoType.CAP_CONTROL_SUBBUS) {
            switch (dbChange.getDbChangeType()) {
                case DELETE:
                case UPDATE:
                    //Is this something we are already tracking?
                    for (Entry<Integer,PointPaoIdentifier> entry : pointIdToPointPaoIdentifier.entrySet()) {
                        if (entry.getValue().getPaoIdentifier().getPaoId() == paoId) {
                            //TODO marker for YUK-11478
                            //reload bus
                            PaoIdentifier subBus = new PaoIdentifier(paoId, PaoType.CAP_CONTROL_SUBBUS);
                            registerPointsForSubStationBus(subBus);
                            return;
                        }
                    }                        
                    break;
                case ADD:
                    //Only if we are a bus. Adding regs and zones will trigger updates
                    try {
                        substationBusDao.getSubstationBusPointIds(paoId);
                        //If we are here, we are a bus. 
                        PaoIdentifier subBus = new PaoIdentifier(paoId, PaoType.CAP_CONTROL_SUBBUS);
                        if (isBusCymeEnabled(subBus)) {
                            //reload bus
                            registerPointsForSubStationBus(subBus);
                        }
                    } catch (NotFoundException e) {
                        //Not a bus.
                    }
                    break;
                case NONE:
                    break;
            }
        } else if (liteYukonPAO.getPaoType() == PaoType.LOAD_TAP_CHANGER ||
                    liteYukonPAO.getPaoType() == PaoType.GANG_OPERATED ||
                    liteYukonPAO.getPaoType() == PaoType.PHASE_OPERATED) { 
            switch (dbChange.getDbChangeType()) {
                case DELETE:
                case UPDATE:
                    //Is this something we are already tracking?
                    for (Entry<Integer,PointPaoIdentifier> entry : pointIdToPointPaoIdentifier.entrySet()) {
                        if (entry.getValue().getPaoIdentifier().getPaoId() == paoId) {
                            //Found a Reg with this ID. reload its bus.
                            //TODO marker for YUK-11478
                            registerPointsForSubStationBus(cacheBusPao);
                            return;
                        }
                    }                        
                    break;
                case ADD:
                    //Only if we are a bus. Adding regs and zones will trigger updates
                case NONE:
                    break;
            }
        }
    }
    
    private void handleZoneChange(DBChangeMsg dbChange) {
        switch (dbChange.getDbChangeType()) {
            case DELETE:
            case UPDATE:
                //If on this bus, reload
                Integer busId = zoneIdToBusId.get(dbChange.getId());
                
                if (busId != null) {
                    //TODO marker for YUK-11478
                    //reload bus
                    PaoIdentifier subBus = new PaoIdentifier(busId, PaoType.CAP_CONTROL_SUBBUS);
                    registerPointsForSubStationBus(subBus);
                }
                break;
            case ADD:
                Zone zone = zoneDao.getZoneById(dbChange.getId());
                if (zone.getSubstationBusId() == cacheBusPao.getPaoId() ) {
                    //reload bus
                    registerPointsForSubStationBus(cacheBusPao);
                }
                break;
            case NONE:
                break;
        }
    }
    
    private boolean isBusCymeEnabled(PaoIdentifier subbus) {
        PointIdentifier cymeEnabled = new PointIdentifier(PointType.Status, 350);
        PointIdentifier startSimulation = new PointIdentifier(PointType.Status, 351);
        PointIdentifier loadFactor = new PointIdentifier(PointType.Analog, 352);
        
        try {
            pointDao.getLitePoint(new PaoPointIdentifier(subbus, cymeEnabled));
            pointDao.getLitePoint(new PaoPointIdentifier(subbus, startSimulation));
            pointDao.getLitePoint(new PaoPointIdentifier(subbus, loadFactor));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
