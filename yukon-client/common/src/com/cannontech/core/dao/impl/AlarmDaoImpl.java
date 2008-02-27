package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
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
	
    public List<Signal> getSignalsForPoints(int[] pointIds) {
		List<Signal> sigList = new ArrayList<Signal>();
		for (int i = 0; i < pointIds.length; i++) {
			List<Signal> sl = getSignalsForPoint(pointIds[i]);
			sigList.addAll(sl);
		}
		return sigList;
	}
	
    public List<Signal> getSignalsForPao(int paoId) {
		List<Signal> paoSignals = new ArrayList<Signal>();
		List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
		for (LitePoint point : points) {
			List<Signal> signals = getSignalsForPoint(point.getPointID());
			paoSignals.addAll(signals);
		}
		return paoSignals;
	}
	
    public List<Signal> getSignalsForPao(int[] paoIds) {
		List<Signal> paoSignals = new ArrayList<Signal>();
		
		for (int i = 0; i < paoIds.length; i++) {
			int paoId = paoIds[i];
            List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
			for (LitePoint point : points) {
				List<Signal> signals = getSignalsForPoint(point.getPointID());
				paoSignals.addAll(signals);
			}
		}
		return paoSignals;
	}
	
	public List<Signal> getSignalsForAlarmCategory(int acId) {
        return new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acId));
	}
	
	public List<Signal> getSignalsForAlarmCategories(int[] acIds) {
		List<Signal> acSignals = new ArrayList<Signal>();
		for (int i = 0; i < acIds.length; i++) {
			List<Signal> signals = new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acIds[i]));
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
