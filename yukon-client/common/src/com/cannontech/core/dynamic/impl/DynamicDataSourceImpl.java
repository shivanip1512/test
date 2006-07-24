package com.cannontech.core.dynamic.impl;

import java.util.Arrays;
import java.util.List;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.yukon.IServerConnection;

public class DynamicDataSourceImpl implements DynamicDataSource {

    public AsyncDynamicDataSource asynDynamicDataSource;
    public IServerConnection dispatchConnection;
    public ServerRequest serverRequest;
    
    public void putValue(PointData pointData) {
        
    }

    public void putValue(int pointId, double value) {
        // TODO Auto-generated method stub

    }
    
    
    public PointData getPointData(int pointId) {
        Command cmd = new Command();
        cmd.setOperation(Command.POINT_DATA_REQUEST);
        cmd.setOpArgList(Arrays.asList(pointId));
        ServerResponseMsg resp = serverRequest.makeServerRequest(dispatchConnection,cmd);
        if(resp.getStatus() == ServerResponseMsg.STATUS_ERROR) {
            throw new DynamicDataAccessException(resp.getStatusStr());
        }

        PointData pointData = (PointData) resp.getPayload();
        return pointData;
    }

    public List<PointData> getPointData(List<Integer> pointIds) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Signal> getSignals(int pointId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Signal> getSignalsByCategory(int pointId, int alarmCategoryId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getTags(int pointId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public List<Integer> getTags(List<Integer> pointIds) {
        // TODO Auto-generated method stub
        return null;
    }
    public void setAsynDynamicDataSource(
            AsyncDynamicDataSource asynDynamicDataSource) {
        this.asynDynamicDataSource = asynDynamicDataSource;
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
    
    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }
}
