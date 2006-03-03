package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.data.lite.LitePoint;

/**
 * Provides access to real time alarming information.
 * @author alauinger
 *
 */
public final class AlarmFuncs {
	
	/**
	 * Return a list of signals for the given point id
	 * @param 
	 * @return	List<Signal>
	 */
	public static List getSignalsForPoint(int pointId) {
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();
		return pcCache.getSignals(pointId);
	}
	
	/**
	 * Return a list of signals for the given point ids
	 * @param 
	 * @return	List<Signal>
	 */
	public static List getSignalsForPoints(int[] pointIds) {
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();
		List sigList = new ArrayList();
		for (int i = 0; i < pointIds.length; i++) {
			List sl = pcCache.getSignals(pointIds[i]);
			sigList.addAll(sl);
		}
		return sigList;
	}
	
	/**
	 * Return a list of signals for the given pao id
	 * @param paoId
	 * @return	List<Signal>
	 */
	public static List getSignalsForPao(int paoId) {
		List paoSignals = new ArrayList();
		
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();		
		LitePoint[] points = PAOFuncs.getLitePointsForPAObject(paoId);
		for (int i = 0; i < points.length; i++) {
			LitePoint point = points[i];
			List signals = pcCache.getSignals(point.getPointID());
			paoSignals.addAll(signals);
		}
		return paoSignals;
	}
	
	/**
	 * Return a list of signals for the given pao ids
	 * @param paoId
	 * @return	List<Signal>
	 */
	public static List getSignalsForPao(int[] paoIds) {
		List paoSignals = new ArrayList();
		
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();
		for (int i = 0; i < paoIds.length; i++) {
			int paoId = paoIds[i];
			LitePoint[] points = PAOFuncs.getLitePointsForPAObject(paoId);
			for (int j = 0; j < points.length; j++) {
				LitePoint point = points[j];
				List signals = pcCache.getSignals(point.getPointID());
				paoSignals.addAll(signals);
			}
		}
		return paoSignals;
	}
	
	/**
	 * Return a list of signals for the given alarm category
	 * @param acId
	 * @return	List<Signal>
	 */
	public static List getSignalsForAlarmCategory(int acId) {
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();
		return pcCache.getSignalsForCategory(acId);
	}
	
	/**
	 * Return a list of signals for the given alarm categories
	 * @param acId
	 * @return	List<Signal>
	 */
	public static List getSignalsForAlarmCategories(int[] acIds) {
		List acSignals = new ArrayList();
		PointChangeCache pcCache = PointChangeCache.getPointChangeCache();
		for (int i = 0; i < acIds.length; i++) {
			List signals = pcCache.getSignals(acIds[i]);
			acSignals.addAll(signals);
		}
		return acSignals;
	}
}
