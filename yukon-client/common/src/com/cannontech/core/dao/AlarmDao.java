package com.cannontech.core.dao;

import java.util.List;

public interface AlarmDao {

    /**
     * Return a list of signals for the given point id
     * @param 
     * @return	List<Signal>
     */
    public List getSignalsForPoint(int pointId);

    /**
     * Return a list of signals for the given point ids
     * @param 
     * @return	List<Signal>
     */
    public List getSignalsForPoints(int[] pointIds);

    /**
     * Return a list of signals for the given pao id
     * @param paoId
     * @return	List<Signal>
     */
    public List getSignalsForPao(int paoId);

    /**
     * Return a list of signals for the given pao ids
     * @param paoId
     * @return	List<Signal>
     */
    public List getSignalsForPao(int[] paoIds);

    /**
     * Return a list of signals for the given alarm category
     * @param acId
     * @return	List<Signal>
     */
    public List getSignalsForAlarmCategory(int acId);

    /**
     * Return a list of signals for the given alarm categories
     * @param acId
     * @return	List<Signal>
     */
    public List getSignalsForAlarmCategories(int[] acIds);

}