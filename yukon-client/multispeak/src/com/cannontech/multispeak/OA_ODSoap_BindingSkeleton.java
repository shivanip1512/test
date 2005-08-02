/**
 * OA_ODSoap_BindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OA_ODSoap_BindingSkeleton implements com.cannontech.multispeak.OA_ODSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.OA_ODSoap_PortType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutageEventStatus", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageEventStatusResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageEventStatus"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutageEventStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutageEventStatus") == null) {
            _myOperations.put("getOutageEventStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutageEventStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getActiveOutages", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetActiveOutagesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetActiveOutages"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetActiveOutages");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getActiveOutages") == null) {
            _myOperations.put("getActiveOutages", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getActiveOutages")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "location"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"), com.cannontech.multispeak.OutageLocation.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutageEventStatusByOutageLocation", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageEventStatusByOutageLocationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageEventStatusByOutageLocation"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutageEventStatusByOutageLocation");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutageEventStatusByOutageLocation") == null) {
            _myOperations.put("getOutageEventStatusByOutageLocation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutageEventStatusByOutageLocation")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODEvents"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionEvent"), com.cannontech.multispeak.ArrayOfOutageDetectionEvent.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("ODEventNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODEventNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODEventNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ODEventNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("ODEventNotification") == null) {
            _myOperations.put("ODEventNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("ODEventNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODDevices"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"), com.cannontech.multispeak.ArrayOfOutageDetectionDevice.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("ODDeviceChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODDeviceChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODDeviceChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ODDeviceChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("ODDeviceChangedNotification") == null) {
            _myOperations.put("ODDeviceChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("ODDeviceChangedNotification")).add(_oper);
    }

    public OA_ODSoap_BindingSkeleton() {
        this.impl = new com.cannontech.multispeak.OA_ODSoap_BindingImpl();
    }

    public OA_ODSoap_BindingSkeleton(com.cannontech.multispeak.OA_ODSoap_PortType impl) {
        this.impl = impl;
    }
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.pingURL();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getMethods();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getDomainNames();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfDomainMember ret = impl.getDomainMembers(domainName);
        return ret;
    }

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.OutageEventStatus ret = impl.getOutageEventStatus(outageEventID);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getActiveOutages() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getActiveOutages();
        return ret;
    }

    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.OutageLocation location) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.OutageEventStatus ret = impl.getOutageEventStatusByOutageLocation(location);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODEventNotification(com.cannontech.multispeak.ArrayOfOutageDetectionEvent ODEvents) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.ODEventNotification(ODEvents);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject ODDeviceChangedNotification(com.cannontech.multispeak.ArrayOfOutageDetectionDevice ODDevices) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.ODDeviceChangedNotification(ODDevices);
        return ret;
    }

}
