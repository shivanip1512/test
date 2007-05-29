/**
 * MR_OASoap12Skeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MR_OASoap12Skeleton implements com.cannontech.multispeak.service.MR_OASoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.service.MR_OASoap_PortType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newOutages"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomersAffectedByOutage"), com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("customersAffectedByOutageNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomersAffectedByOutageNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomersAffectedByOutageNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CustomersAffectedByOutageNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("customersAffectedByOutageNotification") == null) {
            _myOperations.put("customersAffectedByOutageNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("customersAffectedByOutageNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newConnectivity"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"), com.cannontech.multispeak.service.ArrayOfMeterConnectivity.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterConnectivityNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterConnectivityNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterConnectivityNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterConnectivityNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterConnectivityNotification") == null) {
            _myOperations.put("meterConnectivityNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterConnectivityNotification")).add(_oper);
    }

    public MR_OASoap12Skeleton() {
        this.impl = new com.cannontech.multispeak.service.MR_OASoap12Impl();
    }

    public MR_OASoap12Skeleton(com.cannontech.multispeak.service.MR_OASoap_PortType impl) {
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

    public com.cannontech.multispeak.service.ArrayOfErrorObject customersAffectedByOutageNotification(com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage newOutages) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.customersAffectedByOutageNotification(newOutages);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterConnectivityNotification(com.cannontech.multispeak.service.ArrayOfMeterConnectivity newConnectivity) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterConnectivityNotification(newConnectivity);
        return ret;
    }

}
