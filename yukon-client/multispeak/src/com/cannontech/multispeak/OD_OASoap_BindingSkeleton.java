/**
 * OD_OASoap_BindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OD_OASoap_BindingSkeleton implements com.cannontech.multispeak.OD_OASoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.OD_OASoap_PortType impl;
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
        _oper = new org.apache.axis.description.OperationDesc("getAllOutageDetectionDevices", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllOutageDetectionDevicesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllOutageDetectionDevices"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAllOutageDetectionDevices");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllOutageDetectionDevices") == null) {
            _myOperations.put("getAllOutageDetectionDevices", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllOutageDetectionDevices")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutageDetectionDevicesByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutageDetectionDevicesByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutageDetectionDevicesByMeterNo") == null) {
            _myOperations.put("getOutageDetectionDevicesByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutageDetectionDevicesByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus"), com.cannontech.multispeak.OutageDetectDeviceStatus.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutageDetectionDevicesByStatus", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByStatusResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByStatus"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutageDetectionDevicesByStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutageDetectionDevicesByStatus") == null) {
            _myOperations.put("getOutageDetectionDevicesByStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutageDetectionDevicesByStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"), com.cannontech.multispeak.OutageDetectDeviceType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutageDetectionDevicesByType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDetectionDevicesByType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutageDetectionDevicesByType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutageDetectionDevicesByType") == null) {
            _myOperations.put("getOutageDetectionDevicesByType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutageDetectionDevicesByType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getOutagedODDevices", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutagedODDevicesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutagedODDevices"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetOutagedODDevices");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOutagedODDevices") == null) {
            _myOperations.put("getOutagedODDevices", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOutagedODDevices")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateOutageDetectionEventRequest", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateOutageDetectionEventRequestResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateOutageDetectionEventRequest"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateOutageDetectionEventRequest");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateOutageDetectionEventRequest") == null) {
            _myOperations.put("initiateOutageDetectionEventRequest", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateOutageDetectionEventRequest")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDevice"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"), com.cannontech.multispeak.OutageDetectionDevice.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("modifyODDataForOutageDetectionDevice", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyODDataForOutageDetectionDevice"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ModifyODDataForOutageDetectionDevice");
        _myOperationsList.add(_oper);
        if (_myOperations.get("modifyODDataForOutageDetectionDevice") == null) {
            _myOperations.put("modifyODDataForOutageDetectionDevice", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("modifyODDataForOutageDetectionDevice")).add(_oper);
    }

    public OD_OASoap_BindingSkeleton() {
        this.impl = new com.cannontech.multispeak.OD_OASoap_BindingImpl();
    }

    public OD_OASoap_BindingSkeleton(com.cannontech.multispeak.OD_OASoap_PortType impl) {
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

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfOutageDetectionDevice ret = impl.getAllOutageDetectionDevices(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByType(oDDType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfOutageDetectionDevice ret = impl.getOutagedODDevices();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar requestDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.initiateOutageDetectionEventRequest(meterNos, requestDate);
        return ret;
    }

    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException
    {
        impl.modifyODDataForOutageDetectionDevice(oDDevice);
    }

}
