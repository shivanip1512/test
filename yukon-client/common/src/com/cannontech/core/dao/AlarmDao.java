package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.messaging.message.dispatch.SignalMessage;

public interface AlarmDao {

    /**
     * Return a list of signals for the given point id
     * @param 
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForPoint(int pointId);

    /**
     * Return a list of signals for the given point ids
     * @param 
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForPoints(List<Integer> pointIds);

    /**
     * Return a list of signals for the given pao id
     * @param paoId
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForPao(int paoId);

    /**
     * Return a list of signals for the given pao ids
     * @param paoId
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForPaos(List<Integer> paoIds);

    /**
     * Return a list of signals for the given alarm category
     * @param acId
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForAlarmCategory(int acId);

    /**
     * Return a list of signals for the given alarm categories
     * @param acId
     * @return	List<Signal>
     */
    public List<SignalMessage> getSignalsForAlarmCategories(List<Integer> acIds);

}