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

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPoint(int)
     */
	public List getSignalsForPoint(int pointId) {
	    Set<Signal> signals = dynamicDataSource.getSignals(pointId);
	    ArrayList<Signal> array = new ArrayList<Signal>();
	    if( !signals.isEmpty() ) {
	        array.addAll(signals);
	    }
	    return array;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPoints(int[])
     */
	public List getSignalsForPoints(int[] pointIds) {
		List sigList = new ArrayList();
		for (int i = 0; i < pointIds.length; i++) {
			List sl = getSignalsForPoint(pointIds[i]);
			sigList.addAll(sl);
		}
		return sigList;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPao(int)
     */
	public List getSignalsForPao(int paoId) {
		List paoSignals = new ArrayList();
		List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
		for (LitePoint point : points) {
			List signals = getSignalsForPoint(point.getPointID());
			paoSignals.addAll(signals);
		}
		return paoSignals;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPao(int[])
     */
	public List getSignalsForPao(int[] paoIds) {
		List paoSignals = new ArrayList();
		
		for (int i = 0; i < paoIds.length; i++) {
			int paoId = paoIds[i];
            List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paoId);
			for (LitePoint point : points) {
				List signals = getSignalsForPoint(point.getPointID());
				paoSignals.addAll(signals);
			}
		}
		return paoSignals;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForAlarmCategory(int)
     */
	public List getSignalsForAlarmCategory(int acId) {
        return new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acId));
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForAlarmCategories(int[])
     */
	public List getSignalsForAlarmCategories(int[] acIds) {
		List acSignals = new ArrayList();
		for (int i = 0; i < acIds.length; i++) {
			List signals = new ArrayList<Signal>(dynamicDataSource.getSignalsByCategory(acIds[i]));
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
