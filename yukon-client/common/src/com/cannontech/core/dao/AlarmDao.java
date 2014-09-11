package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.message.dispatch.message.Signal;

public interface AlarmDao {

    /**
     * Return a list of signals for the given point ids
     * @param
     * @return	List<Signal>
     */
    public List<Signal> getSignalsForPoints(List<Integer> pointIds);

    /**
     * Return a list of signals for the given pao id
     * @param paoId
     * @return	List<Signal>
     */
    public List<Signal> getSignalsForPao(int paoId);

    /**
     * Return a list of signals for the given pao ids
     * @param paoId
     * @return	List<Signal>
     */
    public List<Signal> getSignalsForPaos(List<Integer> paoIds);

    /**
     * Return a list of signals for the given alarm category
     * @param acId
     * @return	List<Signal>
     */
    public List<Signal> getSignalsForAlarmCategory(int acId);

    /**
     * Return a list of signals for the given alarm categories
     * @param acId
     * @return	List<Signal>
     */
    public List<Signal> getSignalsForAlarmCategories(List<Integer> acIds);

}