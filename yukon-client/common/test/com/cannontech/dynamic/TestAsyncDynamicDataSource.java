package com.cannontech.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.impl.AsyncDynamicDataSourceImpl;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.util.Command;

public class TestAsyncDynamicDataSource extends TestCase {
    AsyncDynamicDataSourceImpl asyncDynamicDataSource = new AsyncDynamicDataSourceImpl();
    
    MockDispatchConnection dispatchConnection = new MockDispatchConnection();
    MockServerRequest serverRequest = new MockServerRequest();
    
    PointDataListener pdl = new PointDataListener() { 
        public void pointDataReceived(PointData pointData) {};
    };
    
    SignalListener sl = new SignalListener() {
        public void signalReceived(com.cannontech.message.dispatch.message.Signal signal) {};
    };
    
    @Override
    protected void setUp() throws Exception {
        asyncDynamicDataSource.setDispatchConnection(dispatchConnection);
        asyncDynamicDataSource.setServerRequest(serverRequest);
    }
    
    public void testRegisterForPointData() {
        Set<Integer> ids = CtiUtilities.asSet(1,2,3,4,5,6,7,8,9,10);
        clearMessagesToDispatch();

        // Register a point data listener should generate some messages
        asyncDynamicDataSource.registerForPointData(pdl, ids);
        
        List<Object> msgs = getMessagesToDispatch();
        assertTrue("Two messages should have been sent to dispatch", msgs.size()==2);
        assertTrue("The point registration message to register for point data was bad or non-existent", checkRegMsg(ids, msgs));
        assertTrue("The command message to retrieve point data was bad or non-existent", checkPointDataReq(ids, msgs));
        assertTrue("PointDataListener isn't correctly associated with the correct point ids", checkPointDataListener(pdl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a signle PointDataListener", checkPointId(id, CtiUtilities.asSet(pdl)));
        }
        
        clearMessagesToDispatch();
        
        // Register the same point ids, nothing new should be sent to dispatch
        asyncDynamicDataSource.registerForPointData(pdl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("No messages should have been sent to dispatch, we should have already been registered for these point ids",
                   msgs.size()==0);
        assertTrue("PointDataListener isn't correctly associated with the correct point ids", checkPointDataListener(pdl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a signle PointDataListener", checkPointId(id, CtiUtilities.asSet(pdl)));
        }
        clearMessagesToDispatch();
        
        // Unregister our point data listener, an unregistration message should be sent
        asyncDynamicDataSource.unRegisterForPointData(pdl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("One message was expected to dispatch", msgs.size() == 1);
        assertTrue("The command message to unregister point ids was bad or non-existent", checkUnregMsg(ids, msgs));
        assertTrue("PointDataListener shouldn't be associated with any point ids", checkPointDataListener(pdl, Collections.EMPTY_SET));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should not be associated with any PointDataListener", checkPointId(id, Collections.EMPTY_SET));
        }
        
        clearMessagesToDispatch();
        
        // Register a signal listener and throw away the messages
        asyncDynamicDataSource.registerForSignals(sl, ids);
        clearMessagesToDispatch();
        
        // Register our point data listener, nothing should sent since was are already 
        // registered for these points via signal listener
        asyncDynamicDataSource.registerForPointData(pdl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("No messages were expected to be sent to dispatch", msgs.size() == 0);
        assertTrue("PointDataListener isn't correctly associated with the correct point ids", checkPointDataListener(pdl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a signle PointDataListener", checkPointId(id, CtiUtilities.asSet(pdl)));
        }
        clearMessagesToDispatch();
    }
    
    public void testRegisterForSignals() {
        Set<Integer> ids = CtiUtilities.asSet(1,2,3,4,5,6,7,8,9,10);
        clearMessagesToDispatch();

        // Register a signal listener should generate some messages
        asyncDynamicDataSource.registerForSignals(sl, ids);
        
        List<Object> msgs = getMessagesToDispatch();
        assertTrue("Two messages should have been sent to dispatch", msgs.size()==2);
        assertTrue("The point registration message to register for point data was bad or non-existent", checkRegMsg(ids, msgs));
        assertTrue("The command message to retrieve point data was bad or non-existent", checkPointDataReq(ids, msgs));
        assertTrue("SignalListener isn't correctly associated with the correct point ids", checkSignalListener(sl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a single SignalListener", checkPointId(id, CtiUtilities.asSet(sl), null));
        }
        clearMessagesToDispatch();
        
        // Register the same point ids, nothing new should be sent to dispatch
        asyncDynamicDataSource.registerForSignals(sl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("No messages should have been sent to dispatch, we should have already been registered for these point ids",
                   msgs.size()==0);
        assertTrue("SignalListener isn't correctly associated with the correct point ids", checkSignalListener(sl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a signle SignalListener", checkPointId(id, CtiUtilities.asSet(sl), null));
        }
        clearMessagesToDispatch();
        
        // Unregister our signal listener, an unregistration message should be sent
        asyncDynamicDataSource.unRegisterForSignals(sl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("One message was expected to dispatch", msgs.size() == 1);
        assertTrue("The command message to unregister point ids was bad or non-existent", checkUnregMsg(ids, msgs));
        assertTrue("SignalListener shouldn't be associated with any point ids", checkSignalListener(sl, Collections.EMPTY_SET));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should not be associated with any SignalListener", checkPointId(id, Collections.EMPTY_SET, null));
        }
        clearMessagesToDispatch();
        
        // Register a point data listener and throw away the messages
        asyncDynamicDataSource.registerForPointData(pdl, ids);
        clearMessagesToDispatch();
        
        // Register our signal listener, nothing should sent since was are already 
        // registered for these points via point data listener
        asyncDynamicDataSource.registerForSignals(sl, ids);
        
        msgs = getMessagesToDispatch();
        assertTrue("No messages were expected to be sent to dispatch", msgs.size() == 0);
        assertTrue("SignalListener isn't correctly associated with the correct point ids", checkSignalListener(sl, ids));
        for(Integer id : ids) {
            assertTrue("Point id: " + id + " should be associated with a signle SignalListener", checkPointId(id, CtiUtilities.asSet(sl), null));
        }
        clearMessagesToDispatch();
    }
    
    /**
     * Check that a PointRegistration message is in the correct position in the list
     * of messages and that it contains the correct point ids to register for.
     * @param pointIds
     * @param messages
     * @return
     */
    private boolean checkRegMsg(Set<Integer> pointIds, List<Object> messages) {       
        PointRegistration pReg = (PointRegistration) messages.get(0);
        return (pReg.getRegFlags() == PointRegistration.REG_ADD_POINTS &&
                pReg.getPointIds().equals(pointIds));
    }
    
    /**
     * Check that a Command message is in the correct position in the list of messages
     * and that it contains the correct point ids to request.
     * @param pointIds
     * @param messages
     * @return
     */
    private boolean checkPointDataReq(Set<Integer> pointIds, List<Object> messages) {
        Command cmd = (Command) messages.get(1);
        return (cmd.getOperation() == Command.POINT_DATA_REQUEST &&
                new HashSet<Integer>(cmd.getOpArgList()).equals(pointIds));        
    }
    
    /**
     * Check that a PointRegistration is in the correct position in the list of messages
     * and that it contains the correct point ids to unregister for.
     * @param pointIds
     * @param messages
     * @return
     */
    private boolean checkUnregMsg(Set<Integer> pointIds, List<Object> messages) {
        PointRegistration pReg = (PointRegistration) messages.get(0);
        return (pReg.getRegFlags() == PointRegistration.REG_REMOVE_POINTS &&
                pReg.getPointIds().equals(pointIds)); 
    }
    
    /**
     * Returns true if the given listener is registered for the given set of point ids
     * @param l
     * @param pointIds
     * @return
     */
    private boolean checkPointDataListener(PointDataListener l, Set<Integer> pointIds) {
        Set<Integer> regIds = asyncDynamicDataSource.getPointIds(l);
        return regIds.equals(pointIds);
    }
    
    /**
     * Returns true if the given listener is registered for the given set of point ids
     * @param l
     * @param pointIds
     * @return
     */
    private boolean checkSignalListener(SignalListener l, Set<Integer> pointIds) {
        Set<Integer> regIds = asyncDynamicDataSource.getPointIds(l);
        return regIds.equals(pointIds);        
    }
    
    /**
     * Returns true if the given set of listeners are registered for the given pointid
     * @param pointId
     * @param pdl
     * @return
     */
    private boolean checkPointId(int pointId, Set<PointDataListener> pdl) {
        Set<PointDataListener> regPdl = asyncDynamicDataSource.getPointDataListeners(pointId);
        return regPdl.equals(pdl); 
    }
    
    /**
     * Returns true if the given set of listeners are registered for the given pointid
     * @param pointId
     * @param sl
     * @param dummy - Java rules.
     * @return
     */
    private boolean checkPointId(int pointId, Set<SignalListener> sl, String dummy) {
        Set<SignalListener> regSl= asyncDynamicDataSource.getSignalListeners(pointId);
        return regSl.equals(sl);        
    }
    
    /**
     * REturn all of the accumulated messages intended for dispatch
     */
    private List<Object> getMessagesToDispatch() {
        List<Object> msgs = new ArrayList<Object>();
        msgs.addAll(dispatchConnection.messagesWritten);
        msgs.addAll(serverRequest.messagesWritten);
        return msgs;
    }
    
    /**
     * Clear out all of the accumulated messages intended for dispatch
     *
     */
    private void clearMessagesToDispatch() {
        dispatchConnection.messagesWritten.clear();
        serverRequest.messagesWritten.clear();
    }
}
