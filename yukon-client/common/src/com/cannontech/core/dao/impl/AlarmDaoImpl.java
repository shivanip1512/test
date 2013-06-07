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
import com.cannontech.messaging.message.dispatch.SignalMessage;

/**
 * Provides access to real time alarming information.
 * @author alauinger
 *
 */
public final class AlarmDaoImpl implements AlarmDao {
    
    private PointDao pointDao;
    private DynamicDataSource dynamicDataSource;

	public List<SignalMessage> getSignalsForPoint(int pointId) {
	    Set<SignalMessage> signals = dynamicDataSource.getSignals(pointId);
        ArrayList<SignalMessage> array = new ArrayList<SignalMessage>();
        if( !signals.isEmpty() ) {
            array.addAll(signals);
        }
        return array;
	}
	
    public List<SignalMessage> getSignalsForPoints(List<Integer> pointIds) {
		List<SignalMessage> sigList = new ArrayList<SignalMessage>();
		Set<Integer> pointIdSet = new HashSet<Integer>(pointIds);
		Map<Integer, Set<SignalMessage>> signalMap = dynamicDataSource.getSignals(pointIdSet);
		Collection<Set<SignalMessage>> signalMapValues = signalMap.values();
        for(Set<SignalMessage> signalSet : signalMapValues) {
            sigList.addAll(signalSet);
        }
		return sigList;
	}
	
    public List<SignalMessage> getSignalsForPao(int paoId) {
		List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
		List<Integer> pointIds = new ArrayList<Integer>();
		for(LitePoint point : points) {
		    pointIds.add(point.getPointID());
		}
		
		return getSignalsForPoints(pointIds);
	}
	
    public List<SignalMessage> getSignalsForPaos(List<Integer> paoIds) {
		List<SignalMessage> paoSignals = new ArrayList<SignalMessage>();
		for (Integer paoId : paoIds) {
           paoSignals.addAll(getSignalsForPao(paoId));
		}
		return paoSignals;
	}
	
	public List<SignalMessage> getSignalsForAlarmCategory(int acId) {
        return new ArrayList<SignalMessage>(dynamicDataSource.getSignalsByCategory(acId));
	}
	
	public List<SignalMessage> getSignalsForAlarmCategories(List<Integer> acIds) {
		List<SignalMessage> acSignals = new ArrayList<SignalMessage>();
		for (Integer acId : acIds) {
			List<SignalMessage> signals = new ArrayList<SignalMessage>(dynamicDataSource.getSignalsByCategory(acId));
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
