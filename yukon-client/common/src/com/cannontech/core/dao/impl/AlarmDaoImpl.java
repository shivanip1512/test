package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AlarmDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Provides access to real time alarming information.
 * @author alauinger
 *
 */
public final class AlarmDaoImpl implements AlarmDao {
    
    @Autowired private PointDao pointDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

	public List<Signal> getSignalsForPoint(int pointId) {
	    Set<Signal> signals = asyncDynamicDataSource.getSignals(pointId);
        ArrayList<Signal> array = new ArrayList<Signal>();
        if( !signals.isEmpty() ) {
            array.addAll(signals);
        }
        return array;
	}
	
    @Override
    public List<Signal> getSignalsForPoints(List<Integer> pointIds) {
		List<Signal> sigList = new ArrayList<Signal>();
		Set<Integer> pointIdSet = new HashSet<Integer>(pointIds);
		Map<Integer, Set<Signal>> signalMap = asyncDynamicDataSource.getSignals(pointIdSet);
		Collection<Set<Signal>> signalMapValues = signalMap.values();
        for(Set<Signal> signalSet : signalMapValues) {
            sigList.addAll(signalSet);
        }
		return sigList;
	}
	
    @Override
    public List<Signal> getSignalsForPao(int paoId) {
		List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
		List<Integer> pointIds = new ArrayList<Integer>();
		for(LitePoint point : points) {
		    pointIds.add(point.getPointID());
		}
		
		return getSignalsForPoints(pointIds);
	}
	
    @Override
    public List<Signal> getSignalsForPaos(List<Integer> paoIds) {
		List<Signal> paoSignals = new ArrayList<Signal>();
		for (Integer paoId : paoIds) {
           paoSignals.addAll(getSignalsForPao(paoId));
		}
		return paoSignals;
	}
	
	@Override
    public List<Signal> getSignalsForAlarmCategory(int acId) {
        return new ArrayList<Signal>(asyncDynamicDataSource.getSignalsByCategory(acId));
	}
	
	@Override
    public List<Signal> getSignalsForAlarmCategories(List<Integer> acIds) {
		List<Signal> acSignals = new ArrayList<Signal>();
		for (Integer acId : acIds) {
			List<Signal> signals = new ArrayList<Signal>(asyncDynamicDataSource.getSignalsByCategory(acId));
			acSignals.addAll(signals);
		}
		return acSignals;
	}
}
