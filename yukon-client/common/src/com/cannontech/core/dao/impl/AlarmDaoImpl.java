package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.core.dao.AlarmDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Provides access to real time alarming information.
 * @author alauinger
 *
 */
public final class AlarmDaoImpl implements AlarmDao {
    
    private PointDao pointDao;
    private DynamicDataSource dynamicDataSource;

	public List<Signal> getSignalsForPoint(int pointId) {
	    Set<Signal> signals = dynamicDataSource.getSignals(pointId);
        ArrayList<Signal> array = new ArrayList<Signal>();
        if( !signals.isEmpty() ) {
            array.addAll(signals);
        }
        return array;
	}
	
    public List<Signal> getSignalsForPoints(List<Integer> pointIds) {
		List<Signal> sigList = new ArrayList<Signal>();
		Set<Integer> pointIdSet = new HashSet<Integer>(pointIds);
		Map<Integer, Set<Signal>> signalMap = dynamicDataSource.getSignals(pointIdSet);
		Collection<Set<Signal>> signalMapValues = signalMap.values();
        for(Set<Signal> signalSet : signalMapValues) {
            sigList.addAll(signalSet);
        }
		return sigList;
	}
	
    public List<Signal> getSignalsForPao(int paoId) {
		List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
		List<Integer> pointIds = new ArrayList<Integer>();
		for(LitePoint point : points) {
		    pointIds.add(point.getPointID());
		}
		
		return getSignalsForPoints(pointIds);
	}
	
    public List<Signal> getSignalsForPaos(List<Integer> paoIds) {
		List<Signal> paoSignals = new ArrayList<Signal>();
		for (Integer paoId : paoIds) {
           paoSignals.addAll(getSignalsForPao(paoId));
		}
		return paoSignals;
	}
	
	public List<Signal> getSignalsForAlarmCategory(int acId) {
        return new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acId));
	}
	
	public List<Signal> getSignalsForAlarmCategories(List<Integer> acIds) {
		List<Signal> acSignals = new ArrayList<Signal>();
		for (Integer acId : acIds) {
			List<Signal> signals = new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acId));
			acSignals.addAll(signals);
		}
		return acSignals;
	}

    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
