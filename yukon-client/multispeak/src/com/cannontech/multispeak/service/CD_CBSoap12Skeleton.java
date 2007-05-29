/**
 * CD_CBSoap12Skeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CD_CBSoap12Skeleton implements com.cannontech.multispeak.service.CD_CBSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.service.CD_CBSoap_PortType impl;
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
        };
        _oper = new org.apache.axis.description.OperationDesc("pingURL", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURLResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURL"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/PingURL");
        _myOperationsList.add(_oper);
        if (_myOperations.get("pingURL") == null) {
            _myOperations.put("pingURL", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("pingURL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getMethods", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethodsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethods"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMethods");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMethods") == null) {
            _myOperations.put("getMethods", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMethods")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getDomainNames", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNames"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDomainNames");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDomainNames") == null) {
            _myOperations.put("getDomainNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDomainNames")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDomainMembers", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembers"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDomainMembers");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDomainMembers") == null) {
            _myOperations.put("getDomainMembers", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDomainMembers")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCDSupportedMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCDSupportedMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCDSupportedMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCDSupportedMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCDSupportedMeters") == null) {
            _myOperations.put("getCDSupportedMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCDSupportedMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedCDMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCDMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCDMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedCDMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedCDMeters") == null) {
            _myOperations.put("getModifiedCDMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedCDMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCDMeterState", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCDMeterStateResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCDMeterState"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCDMeterState");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCDMeterState") == null) {
            _myOperations.put("getCDMeterState", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCDMeterState")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cdEvents"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConnectDisconnectEvent"), com.cannontech.multispeak.service.ArrayOfConnectDisconnectEvent.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateConnectDisconnect", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateConnectDisconnectResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateConnectDisconnect"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateConnectDisconnect");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateConnectDisconnect") == null) {
            _myOperations.put("initiateConnectDisconnect", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateConnectDisconnect")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedCustomers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"), com.cannontech.multispeak.service.ArrayOfCustomer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("customerChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomerChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomerChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CustomerChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("customerChangedNotification") == null) {
            _myOperations.put("customerChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("customerChangedNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedServiceLocations"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"), com.cannontech.multispeak.service.ArrayOfServiceLocation.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("serviceLocationChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ServiceLocationChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("serviceLocationChangedNotification") == null) {
            _myOperations.put("serviceLocationChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("serviceLocationChangedNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.service.ArrayOfMeter.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterChangedNotification") == null) {
            _myOperations.put("meterChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterChangedNotification")).add(_oper);
    }

    public CD_CBSoap12Skeleton() {
        this.impl = new com.cannontech.multispeak.service.CD_CBSoap12Impl();
    }

    public CD_CBSoap12Skeleton(com.cannontech.multispeak.service.CD_CBSoap_PortType impl) {
        this.impl = impl;
    }
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.pingURL();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfString ret = impl.getMethods();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfString ret = impl.getDomainNames();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfDomainMember ret = impl.getDomainMembers(domainName);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeter ret = impl.getCDSupportedMeters(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeter ret = impl.getModifiedCDMeters(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.LoadActionCode ret = impl.getCDMeterState(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateConnectDisconnect(com.cannontech.multispeak.service.ArrayOfConnectDisconnectEvent cdEvents) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateConnectDisconnect(cdEvents);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.service.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.customerChangedNotification(changedCustomers);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.service.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.serviceLocationChangedNotification(changedServiceLocations);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.service.ArrayOfMeter changedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterChangedNotification(changedMeters);
        return ret;
    }

}
