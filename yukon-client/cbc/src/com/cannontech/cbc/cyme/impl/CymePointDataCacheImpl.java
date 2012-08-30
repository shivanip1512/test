package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.cbc.cyme.CymePointDataCache;
import com.cannontech.cbc.cyme.CymeSimulationListener;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CymePointDataCacheImpl implements CymePointDataCache, PointDataListener {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;  
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private PointDao pointDao;
    @Autowired private ZoneDao zoneDao;
    
    private Map<Integer,PointPaoIdentifier> pointIdToPointPaoIdentifier = new ConcurrentHashMap<Integer,PointPaoIdentifier>();
    private Map<Integer,PointValueQualityHolder> pointIdToValue = new ConcurrentHashMap<Integer,PointValueQualityHolder>();
    
    private Map<Integer,Integer> busToEnabledPointIds = new ConcurrentHashMap<Integer,Integer>();
    private Map<Integer,Integer> loadValuePointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
    private Map<Integer,Integer> simulationPointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
    private Map<Integer,Integer> bankStatusPointIdToBusId = new ConcurrentHashMap<Integer,Integer>();
    
    private Map<Integer,CymeSimulationListener> busIdtoListener = new ConcurrentHashMap<Integer,CymeSimulationListener>();
    private List<CymeSimulationListener> cymeListeners = Lists.newArrayList();
        
    @Override
    public void registerPointsForSubStationBus(CymeSimulationListener listener, PaoIdentifier subbus) {
        loadCymeSimulationControlPoints(listener, subbus);
        
        // Determine the Bank Status points to watch for Capcontrol Server changes.
        Collection<PointPaoIdentifier> bankPoints = substationBusDao.getBankStatusPointPaoIdsBySubbusId(subbus.getPaoId());
        for ( PointPaoIdentifier pointPaoId : bankPoints) {
            pointIdToPointPaoIdentifier.put(pointPaoId.getPointId(), pointPaoId);
            bankStatusPointIdToBusId.put(pointPaoId.getPointId(),subbus.getPaoId());
            busIdtoListener.put(subbus.getPaoId(),listener);
        }
        
        Collection<PointPaoIdentifier> regulatorPoints = zoneDao.getTapPointsBySubBusId(subbus.getPaoId());
        for (PointPaoIdentifier zone : regulatorPoints) {
            pointIdToPointPaoIdentifier.put(zone.getPointId(), zone);
        }
        
        //Register Bank Statuses
        Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getAndRegisterForPointData(this, pointIdToPointPaoIdentifier.keySet());

        for (PointValueQualityHolder pvqh : pointValues) {
            pointIdToValue.put(pvqh.getId(), pvqh);
        }
        
        cymeListeners.add(listener);
    }

    private void loadCymeSimulationControlPoints(CymeSimulationListener listener, PaoIdentifier subbus) {
        PointIdentifier cymeEnabled = new PointIdentifier(PointType.Status, 350);
        PointIdentifier startSimulation = new PointIdentifier(PointType.Status, 351);
        PointIdentifier loadFactor = new PointIdentifier(PointType.Analog, 352);
        
        LitePoint enabledPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, cymeEnabled));
        LitePoint simulationPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, startSimulation));
        LitePoint loadPoint = pointDao.getLitePoint(new PaoPointIdentifier(subbus, loadFactor));
        
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
        //NOT putting in pointIds because it will get registered above.
        
        asyncDynamicDataSource.getAndRegisterForPointData(this,pointIds);
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
            CymeSimulationListener listener = busIdtoListener.get(subbusId);
            listener.notifyNewLoadFactor(pointData);
        }
        
        subbusId = simulationPointIdToBusId.get(pointData.getId());
        if (subbusId != null) {
            CymeSimulationListener listener = busIdtoListener.get(subbusId);
            listener.notifyNewSimulation(pointData);
        }

        subbusId = bankStatusPointIdToBusId.get(pointData.getId());
        if (subbusId != null) {
            CymeSimulationListener listener = busIdtoListener.get(subbusId);
            listener.notifyCbcControl(pointData);
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
}
