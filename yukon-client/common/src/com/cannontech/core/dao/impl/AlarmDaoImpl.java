package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.core.dao.AlarmDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;

/**
 * Provides access to real time alarming information.
 * @author alauinger
 *
 */
public final class AlarmDaoImpl implements AlarmDao {
    
    private PointDao pointDao;
    private PointChangeCache pointChangeCache;

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPoint(int)
     */
	public List getSignalsForPoint(int pointId) {
		return pointChangeCache.getSignals(pointId);
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForPoints(int[])
     */
	public List getSignalsForPoints(int[] pointIds) {
		List sigList = new ArrayList();
		for (int i = 0; i < pointIds.length; i++) {
			List sl = pointChangeCache.getSignals(pointIds[i]);
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
			List signals = pointChangeCache.getSignals(point.getPointID());
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
				List signals = pointChangeCache.getSignals(point.getPointID());
				paoSignals.addAll(signals);
			}
		}
		return paoSignals;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForAlarmCategory(int)
     */
	public List getSignalsForAlarmCategory(int acId) {
		return pointChangeCache.getSignalsForCategory(acId);
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmDao#getSignalsForAlarmCategories(int[])
     */
	public List getSignalsForAlarmCategories(int[] acIds) {
		List acSignals = new ArrayList();
		for (int i = 0; i < acIds.length; i++) {
			List signals = pointChangeCache.getSignalsForCategory(acIds[i]);
			acSignals.addAll(signals);
		}
		return acSignals;
	}
    
    public void setPointChangeCache(PointChangeCache pointChangeCache) {
        this.pointChangeCache = pointChangeCache;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
