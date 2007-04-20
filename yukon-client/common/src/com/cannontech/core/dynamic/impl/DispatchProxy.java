package com.cannontech.core.dynamic.impl;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.yukon.IServerConnection;

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
        
    /**
     * Get the current PointData for a single point
     * @param pointId
     * @return
     */
    PointData getPointData(int pointId) {
        Multi m = getPointDataMulti(CtiUtilities.asSet(pointId));
        return (PointData) m.getVector().iterator().next();
    }
    
    /**
     * Get the current set of PointData's for the given set of point ids
     * @param pointId
     * @return
     */
    Set<PointData> getPointData(Set<Integer> pointId) {
        Multi m = getPointDataMulti(pointId);
        Set<PointData> pointData = 
            new HashSet<PointData>((int)(pointId.size()/0.75f)+1);
        extractPointData(pointData, m);
        return pointData;
    }
    
    /**
     * Get the current set of signals for a given point id
     * @param pointId
     * @return
     */
    Set<Signal> getSignals(int pointId) {
        Map<Integer, Set<Signal>> sigs = getSignals(CtiUtilities.asSet(pointId)); 
        Set<Signal> ret = sigs.get(pointId);
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
    Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) {
        Multi m = getPointDataMulti(pointIds);
        Map<Integer, Set<Signal>> signals = 
            new HashMap<Integer, Set<Signal>>((int)(pointIds.size()/0.75f)+1);        
        
        extractSignals(signals, m);
        return signals;
    }
    
    /**
     * Get a current set of Signals for a given alarm category
     * @param alarmCategoryId
     * @return
     */
    Set<Signal> getSignalsByCategory(int alarmCategoryId) {
        Command cmd = makeCommandMsg(Command.ALARM_CATEGORY_REQUEST, CtiUtilities.asSet(alarmCategoryId));
        Multi m = (Multi) makeRequest(cmd);
        Set<Signal> signals = new HashSet<Signal>();
        extractSignals(signals, m);
        return signals;
    }
    
    void putPointData(PointData pointData) {
        putPointData(CtiUtilities.asSet(pointData));
    }
    
    void putPointData(Set<PointData> pointData) {
        if(!dispatchConnection.isValid()) {
            throw new DynamicDataAccessException("Invalid connection to dispatch");
        }
        else {
            Multi multi = new Multi();
            multi.setVector(new Vector<PointData>(pointData));
            dispatchConnection.write(multi);
        }
    }
    
    /**
     * Registers a set of point ids with dispatch
     * @param pointIds
     */
    public void registerForPointIds(Set<Integer> pointIds) {
        if(!dispatchConnection.isValid()) {
            throw new DynamicDataAccessException("Connection to dispatch is invalid");
        }
        PointRegistration pReg = new PointRegistration();
        pReg.setRegFlags(PointRegistration.REG_ADD_POINTS);
        pReg.setPointIds(pointIds);
        dispatchConnection.write(pReg);
    }
    
    /**
     * Return the raw multi from dispatch for a set of point ids
     * Also registers for point ids
     * @param pointIds
     * @return
     */
    private Multi getPointDataMulti(Set<Integer> pointIds) {
        Command cmd = makeCommandMsg(Command.POINT_DATA_REQUEST, pointIds);
        Multi m = (Multi) makeRequest(cmd);
        registerForPointIds(pointIds);
        return m;
    }
    
    
    
    /**
     * Handles sending a request to dispatch and waiting for a response
     * Will throw a DynamicDataAccessException if there is a problem.
     * @param cmd
     * @return
     */
    private Object makeRequest(Command cmd) {
        if(!dispatchConnection.isValid()) {
            throw new DynamicDataAccessException("Connection to dispatch is invalid");
        }
        ServerResponseMsg resp = serverRequest.makeServerRequest(dispatchConnection,cmd);
        if(resp.getStatus() == ServerResponseMsg.STATUS_ERROR) {
            CTILogger.error(resp.getMessage());
            throw new DynamicDataAccessException(resp.getStatusStr());
        }        
        // returns multi of signals??
        return resp.getPayload();
    }
    
    /**
     * Helper to create a command message 
     * @param op
     * @param pointIds
     * @return
     */
    private Command makeCommandMsg(int op, Set<Integer> pointIds) {
        return makeCommandMsg(op, new ArrayList<Integer>(pointIds));
    }
    
    /**
     * Helper to create a command message
     * @param op
     * @param pointIds
     * @return
     */
    private Command makeCommandMsg(int op, List<Integer> pointIds) {
        Command cmd = new Command();
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
    private void extractPointData(Set<PointData> pointData, Multi m) {
        for(Object o : m.getVector()) {
            if(o instanceof PointData) {
                pointData.add((PointData)o);
            }
            else if(o instanceof Multi) {
                extractPointData(pointData, (Multi) o);
            }
        }
    }
    
    /**
     * Given a raw multi from dispatch, insert all of Signal messages
     * found in the multi in the given map
     * @param signals
     * @param m
     */
    private void extractSignals(Map<Integer, Set<Signal>> signals, Multi m) {
        for(Object o : m.getVector()) {
            if(o instanceof Signal) {
                Signal signal = (Signal) o;
                Set<Signal> sigs = signals.get(signal.getPointID());
                if(sigs == null)  {
                    sigs = new HashSet<Signal>();
                    signals.put(signal.getPointID(), sigs);
                }
                sigs.add((Signal)o);
            }
            else if(o instanceof Multi) {
                extractSignals(signals, (Multi) o);
            }
        }
    }
    
    /**
     * Given a raw multi from dispatch, insert all of the signals messages
     * found in the multi into the given set
     * @param signals
     * @param m
     */
    private void extractSignals(Set<Signal> signals, Multi m) {
        for(Object o : m.getVector()) {
            if(o instanceof Signal) {
                Signal signal = (Signal) o;
                signals.add(signal);
            }
            else if(o instanceof Multi) {
                extractSignals(signals, (Multi) o);
            }
        }
    }

    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
}
