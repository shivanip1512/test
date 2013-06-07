package com.cannontech.core.dynamic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.PointRegistrationMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.ServerRequest;
import com.cannontech.yukon.IServerConnection;
import com.google.common.collect.Iterables;

/**
 * Intended as a simple package scope interface to hide talking to dispatch.
 * This class knows how to get dispatch to send us current point data as well
 * as register/subscribe for point data.
 * 
 * It also knows the format of Dispatch Multi messages.
 * @author alauinger
 */
class DispatchProxy {
    private IServerConnection dispatchConnection;
    private ServerRequest serverRequest;   
    private Logger log = YukonLogManager.getLogger(DispatchProxy.class);
        
    /**
     * Get the current PointData for a single point
     * @param pointId
     * @return
     */
    PointDataMessage getPointData(int pointId) {
        MultiMessage m = getPointDataMulti(CtiUtilities.asSet(pointId));
        List<PointDataMessage> pointData = new ArrayList<PointDataMessage>(1);
        extractPointData(pointData, m);
        Validate.isTrue(pointData.size() != 0, "Returned multi was empty: ", pointData.size());
        if (pointData.size() > 1) {
            log.warn("Returned multi was bigger than expected, ignoring other elements: " + pointData.subList(1, pointData.size()));
        }
        return (PointDataMessage) pointData.get(0);
    }
    
    /**
     * Get the current set of PointData's for the given set of point ids
     * @param pointIds
     * @return
     */
    Set<PointDataMessage> getPointData(Set<Integer> pointIds) {
        Set<PointDataMessage> pointData = new HashSet<PointDataMessage>((int)(pointIds.size()/0.75f)+1);
        if(!pointIds.isEmpty()) {
            MultiMessage m = getPointDataMulti(pointIds);
            extractPointData(pointData, m);
        }
        return pointData;
    }
    
    /**
     * Get the current set of signals for a given point id
     * @param pointId
     * @return
     */
    Set<SignalMessage> getSignals(int pointId) {
        Map<Integer, Set<SignalMessage>> sigs = getSignals(CtiUtilities.asSet(pointId)); 
        Set<SignalMessage> ret = sigs.get(pointId);
        if (ret == null) {
            ret = Collections.emptySet();
        }
        return ret;
    }
    
    /**
     * Get a current map of sets of signals for a given set of point ids
     * Each point can potentially have a set of signals associated with it
     * @param pointIds
     * @return
     */
    Map<Integer, Set<SignalMessage>> getSignals(Set<Integer> pointIds) {
        Map<Integer, Set<SignalMessage>> signals = new HashMap<Integer, Set<SignalMessage>>((int)(pointIds.size()/0.75f)+1);        
        if(!pointIds.isEmpty()) {
            MultiMessage m = getPointDataMulti(pointIds);
            extractSignals(signals, m);
        }
        
        return signals;
    }
    
    /**
     * Get a current set of Signals for a given alarm category
     * @param alarmCategoryId
     * @return
     */
    Set<SignalMessage> getSignalsByCategory(int alarmCategoryId) {
        CommandMessage cmd = makeCommandMsg(CommandMessage.ALARM_CATEGORY_REQUEST, CtiUtilities.asSet(alarmCategoryId));
        MultiMessage m = (MultiMessage) makeRequest(cmd);
        Set<SignalMessage> signals = new HashSet<SignalMessage>();
        extractSignals(signals, m);
        return signals;
    }
    
    void putPointData(PointDataMessage pointData) {
        putPointData(Collections.singleton(pointData));
    }
    
    void putPointData(Iterable<PointDataMessage> pointDatas) {
        validateDispatchConnection();
        MultiMessage<PointDataMessage> multi = new MultiMessage<PointDataMessage>();
        Vector<PointDataMessage> vector = new Vector<PointDataMessage>();
        Iterables.addAll(vector, pointDatas);
        multi.setVector(vector);
        dispatchConnection.write(multi);
    }
    
    /**
     * Registers a set of point ids with dispatch
     * @param pointIds
     */
    public void registerForPointIds(Set<Integer> pointIds) throws DispatchNotConnectedException{
        if (pointIds.isEmpty()) return;
        validateDispatchConnection();
        PointRegistrationMessage pReg = new PointRegistrationMessage();
        pReg.setRegFlags(PointRegistrationMessage.REG_ADD_POINTS);
        pReg.setPointIds(pointIds);
        dispatchConnection.write(pReg);
    }
    
    public void registerForPoints() {
        validateDispatchConnection();
        PointRegistrationMessage pReg = new PointRegistrationMessage();
        pReg.setRegFlags(PointRegistrationMessage.REG_ALL_PTS_MASK | PointRegistrationMessage.REG_NO_UPLOAD);
        dispatchConnection.write(pReg);
    }
    
    /**
     * Return the raw multi from dispatch for a set of point ids
     * Also registers for point ids
     * @param pointIds
     * @return
     */
    private MultiMessage getPointDataMulti(Set<Integer> pointIds) {
        if (log.isDebugEnabled()) {
            log.debug("Making getPointDataMulti request for: " + pointIds);
        }
        MultiMessage m;
        try {
            CommandMessage cmd = makeCommandMsg(CommandMessage.POINT_DATA_REQUEST, pointIds);
            m = (MultiMessage) makeRequest(cmd);
            registerForPointIds(pointIds);
        } catch (DynamicDataAccessException e) {
            throw new DynamicDataAccessException("There was an error retreiving the value for pointIds: " + pointIds, e);
        }
        return m;
    }
    
    /**
     * Handles sending a request to dispatch and waiting for a response
     * Will throw a DynamicDataAccessException if there is a problem.
     * @param cmd
     * @return
     */
    private Object makeRequest(CommandMessage cmd) {
        validateDispatchConnection();
        ServerResponseMessage resp = serverRequest.makeServerRequest(dispatchConnection,cmd);
        if(resp.getStatus() == ServerResponseMessage.STATUS_ERROR) {
            log.error("Dispatch returned the following message: " + resp.getMessage());
            throw new DynamicDataAccessException(resp.getStatusStr() + ": " + resp.getMessage());
        }        
        // returns multi of signals??
        return resp.getPayload();
    }

    private void validateDispatchConnection() throws DispatchNotConnectedException {
        if(!dispatchConnection.isValid()) {
            throw new DispatchNotConnectedException();
        }
    }
    
    /**
     * Helper to create a command message 
     * @param op
     * @param pointIds
     * @return
     */
    private CommandMessage makeCommandMsg(int op, Set<Integer> pointIds) {
        return makeCommandMsg(op, new ArrayList<Integer>(pointIds));
    }
    
    /**
     * Helper to create a command message
     * @param op
     * @param pointIds
     * @return
     */
    private CommandMessage makeCommandMsg(int op, List<Integer> pointIds) {
        CommandMessage cmd = new CommandMessage();
        cmd.setOperation(op);
        cmd.setOpArgList(pointIds);
        return cmd;
    }
    
    /**
     * Given a raw multi from dispatch, insert all the PointData messages
     * found in the multi into the given set
     * @param pointData
     * @param m
     */
    private void extractPointData(Collection<PointDataMessage> pointData, MultiMessage m) {
        for (Object o : m.getVector()) {
            if (o instanceof PointDataMessage) {
                pointData.add((PointDataMessage) o);
            } else if (o instanceof MultiMessage) {
                extractPointData(pointData, (MultiMessage) o);
            } else if (o instanceof SignalMessage) {
                /*
                 *  When asked for a point data, Dispatch will send back
                 *  Singals in the multi if their are any active for that point.
                 */
                continue; 
            }else {
                log.info("Received unknown type in multi (expecting PointData): " 
                               + o.getClass());
            }
        }
    }
    
    /**
     * Given a raw multi from dispatch, insert all of Signal messages
     * found in the multi in the given map
     * @param signals
     * @param m
     */
    private void extractSignals(Map<Integer, Set<SignalMessage>> signals, MultiMessage m) {
        for(Object o : m.getVector()) {
            if(o instanceof SignalMessage) {
                SignalMessage signal = (SignalMessage) o;
                Set<SignalMessage> sigs = signals.get(signal.getPointId());
                if(sigs == null)  {
                    sigs = new HashSet<SignalMessage>();
                    signals.put(signal.getPointId(), sigs);
                }
                sigs.add((SignalMessage)o);
            }
            else if(o instanceof MultiMessage) {
                extractSignals(signals, (MultiMessage) o);
            }
        }
    }
    
    /**
     * Given a raw multi from dispatch, insert all of the signals messages
     * found in the multi into the given set
     * @param signals
     * @param m
     */
    private void extractSignals(Set<SignalMessage> signals, MultiMessage m) {
        for (Object o : m.getVector()) {
            if (o instanceof SignalMessage) {
                SignalMessage signal = (SignalMessage) o;
                signals.add(signal);
            } else if (o instanceof MultiMessage) {
                extractSignals(signals, (MultiMessage) o);
            } else {
                log.info("Received unknown type in multi (expecting Signal): " + o.getClass());
            }
        }
    }
    
    public void registerForAlarms() {
        PointRegistrationMessage pReg = new PointRegistrationMessage();
        pReg.setRegFlags(PointRegistrationMessage.REG_ALARMS);
        dispatchConnection.queue(pReg);
    }

    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }

}
