package com.cannontech.billing.format.itron;

import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;


public class ItronClientImpl implements ItronClient {
    private URL endPoint;
    private String operationName;
    private Service service;
    
    public void setEndPoint(final URL endPoint) {
        this.endPoint = endPoint;
    }
    
    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }
    
    public void setService(final Service service) {
        this.service = service;
    }
    
    public void invokeOneWay(Object...objects) throws Exception {
        createCall().invokeOneWay(objects);
    }
    
    public Object invoke(Object... objects) throws Exception {
        return createCall().invoke(objects);
    }
    
    private Call createCall() throws ServiceException {
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(endPoint);
        call.setOperationName(operationName);
        return call;
    }
}
