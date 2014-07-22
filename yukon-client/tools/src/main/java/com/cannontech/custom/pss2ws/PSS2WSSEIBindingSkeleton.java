/**
 * PSS2WSSEIBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.custom.pss2ws;

public class PSS2WSSEIBindingSkeleton implements com.cannontech.custom.pss2ws.PSS2WSSEI, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.custom.pss2ws.PSS2WSSEI impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "PriceSchedule_1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pss2.lbl.gov", "PriceSchedule"), com.cannontech.custom.pss2ws.PriceSchedule.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getPriceSchedule", _params, new javax.xml.namespace.QName("", "result"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://pss2.lbl.gov", "PriceSchedule"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://pss2.lbl.gov", "getPriceSchedule"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPriceSchedule") == null) {
            _myOperations.put("getPriceSchedule", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPriceSchedule")).add(_oper);
    }

    public PSS2WSSEIBindingSkeleton() {
        this.impl = new com.cannontech.custom.pss2ws.PSS2WSSEIBindingImpl();
    }

    public PSS2WSSEIBindingSkeleton(com.cannontech.custom.pss2ws.PSS2WSSEI impl) {
        this.impl = impl;
    }
    public com.cannontech.custom.pss2ws.PriceSchedule getPriceSchedule(com.cannontech.custom.pss2ws.PriceSchedule priceSchedule_1) throws java.rmi.RemoteException
    {
        com.cannontech.custom.pss2ws.PriceSchedule ret = impl.getPriceSchedule(priceSchedule_1);
        return ret;
    }

}
