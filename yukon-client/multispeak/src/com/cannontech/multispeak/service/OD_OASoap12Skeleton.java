/**
 * OD_OASoap12Skeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class OD_OASoap12Skeleton implements com.cannontech.multispeak.service.OD_OASoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.service.OD_OASoap_PortType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus"), com.cannontech.multispeak.service.OutageDetectDeviceStatus.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"), com.cannontech.multispeak.service.OutageDetectDeviceType.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nounType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PhaseCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"), com.cannontech.multispeak.service.PhaseCd.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateODEventRequestByObject", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODEventRequestByObjectResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODEventRequestByObject"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateODEventRequestByObject");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateODEventRequestByObject") == null) {
            _myOperations.put("initiateODEventRequestByObject", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateODEventRequestByObject")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nounType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PhaseCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"), com.cannontech.multispeak.service.PhaseCd.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "periodicity"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateODMonitoringRequestByObject", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODMonitoringRequestByObjectResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODMonitoringRequestByObject"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateODMonitoringRequestByObject");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateODMonitoringRequestByObject") == null) {
            _myOperations.put("initiateODMonitoringRequestByObject", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateODMonitoringRequestByObject")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("displayODMonitoringRequests", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DisplayODMonitoringRequestsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfObjectRef"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DisplayODMonitoringRequests"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/DisplayODMonitoringRequests");
        _myOperationsList.add(_oper);
        if (_myOperations.get("displayODMonitoringRequests") == null) {
            _myOperations.put("displayODMonitoringRequests", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("displayODMonitoringRequests")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ObjectRef"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfObjectRef"), com.cannontech.multispeak.service.ArrayOfObjectRef.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("cancelODMonitoringRequestByObject", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelODMonitoringRequestByObjectResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelODMonitoringRequestByObject"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CancelODMonitoringRequestByObject");
        _myOperationsList.add(_oper);
        if (_myOperations.get("cancelODMonitoringRequestByObject") == null) {
            _myOperations.put("cancelODMonitoringRequestByObject", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("cancelODMonitoringRequestByObject")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDevice"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"), com.cannontech.multispeak.service.OutageDetectionDevice.class, false, false), 
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

    public OD_OASoap12Skeleton() {
        this.impl = new com.cannontech.multispeak.service.OD_OASoap12Impl();
    }

    public OD_OASoap12Skeleton(com.cannontech.multispeak.service.OD_OASoap_PortType impl) {
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

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice ret = impl.getAllOutageDetectionDevices(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.service.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.service.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice ret = impl.getOutageDetectionDevicesByType(oDDType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice ret = impl.getOutagedODDevices();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.service.ArrayOfString meterNos, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateOutageDetectionEventRequest(meterNos, requestDate, responseURL);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODEventRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateODEventRequestByObject(objectName, nounType, phaseCode, requestDate, responseURL);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODMonitoringRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, int periodicity, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateODMonitoringRequestByObject(objectName, nounType, phaseCode, periodicity, requestDate, responseURL);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfObjectRef displayODMonitoringRequests() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfObjectRef ret = impl.displayODMonitoringRequests();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelODMonitoringRequestByObject(com.cannontech.multispeak.service.ArrayOfObjectRef objectRef, java.util.Calendar requestDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.cancelODMonitoringRequestByObject(objectRef, requestDate);
        return ret;
    }

    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.service.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException
    {
        impl.modifyODDataForOutageDetectionDevice(oDDevice);
    }

}
