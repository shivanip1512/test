/**
 * MR_EASoap12Skeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MR_EASoap12Skeleton implements com.cannontech.multispeak.service.MR_EASoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.service.MR_EASoap_PortType impl;
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
        _oper = new org.apache.axis.description.OperationDesc("getAMRSupportedMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAMRSupportedMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAMRSupportedMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAMRSupportedMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAMRSupportedMeters") == null) {
            _myOperations.put("getAMRSupportedMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAMRSupportedMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedAMRMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedAMRMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedAMRMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedAMRMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedAMRMeters") == null) {
            _myOperations.put("getModifiedAMRMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedAMRMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByDate", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByDateResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByDate"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByDate");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByDate") == null) {
            _myOperations.put("getReadingsByDate", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByDate")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByMeterNo") == null) {
            _myOperations.put("getReadingsByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLatestReadings", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadings"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetLatestReadings");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLatestReadings") == null) {
            _myOperations.put("getLatestReadings", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLatestReadings")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLatestReadingByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetLatestReadingByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLatestReadingByMeterNo") == null) {
            _myOperations.put("getLatestReadingByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLatestReadingByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uomData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByUOMAndDate", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByUOMAndDateResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByUOMAndDate"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByUOMAndDate");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByUOMAndDate") == null) {
            _myOperations.put("getReadingsByUOMAndDate", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByUOMAndDate")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHistoryLogByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetHistoryLogByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHistoryLogByMeterNo") == null) {
            _myOperations.put("getHistoryLogByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHistoryLogByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHistoryLogsByDate", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDateResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDate"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetHistoryLogsByDate");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHistoryLogsByDate") == null) {
            _myOperations.put("getHistoryLogsByDate", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHistoryLogsByDate")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), com.cannontech.multispeak.service.EventCode.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHistoryLogsByMeterNoAndEventCode", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByMeterNoAndEventCodeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByMeterNoAndEventCode"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetHistoryLogsByMeterNoAndEventCode");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHistoryLogsByMeterNoAndEventCode") == null) {
            _myOperations.put("getHistoryLogsByMeterNoAndEventCode", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHistoryLogsByMeterNoAndEventCode")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), com.cannontech.multispeak.service.EventCode.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHistoryLogsByDateAndEventCode", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDateAndEventCodeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDateAndEventCode"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetHistoryLogsByDateAndEventCode");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHistoryLogsByDateAndEventCode") == null) {
            _myOperations.put("getHistoryLogsByDateAndEventCode", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHistoryLogsByDateAndEventCode")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLatestReadingByMeterNoAndType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByMeterNoAndTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByMeterNoAndType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetLatestReadingByMeterNoAndType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLatestReadingByMeterNoAndType") == null) {
            _myOperations.put("getLatestReadingByMeterNoAndType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLatestReadingByMeterNoAndType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLatestReadingByType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFormattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetLatestReadingByType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLatestReadingByType") == null) {
            _myOperations.put("getLatestReadingByType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLatestReadingByType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByDateAndType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByDateAndTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFormattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByDateAndType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByDateAndType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByDateAndType") == null) {
            _myOperations.put("getReadingsByDateAndType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByDateAndType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getSupportedReadingTypes", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSupportedReadingTypesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSupportedReadingTypes"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetSupportedReadingTypes");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSupportedReadingTypes") == null) {
            _myOperations.put("getSupportedReadingTypes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSupportedReadingTypes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByMeterNoAndType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByMeterNoAndTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFormattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByMeterNoAndType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByMeterNoAndType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByMeterNoAndType") == null) {
            _myOperations.put("getReadingsByMeterNoAndType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByMeterNoAndType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateMeterReadByMeterNoAndType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateMeterReadByMeterNoAndTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateMeterReadByMeterNoAndType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateMeterReadByMeterNoAndType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateMeterReadByMeterNoAndType") == null) {
            _myOperations.put("initiateMeterReadByMeterNoAndType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateMeterReadByMeterNoAndType")).add(_oper);
    }

    public MR_EASoap12Skeleton() {
        this.impl = new com.cannontech.multispeak.service.MR_EASoap12Impl();
    }

    public MR_EASoap12Skeleton(com.cannontech.multispeak.service.MR_EASoap_PortType impl) {
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

    public com.cannontech.multispeak.service.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeter ret = impl.getAMRSupportedMeters(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeter ret = impl.getModifiedAMRMeters(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeterRead ret = impl.getReadingsByDate(startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeterRead ret = impl.getReadingsByMeterNo(meterNo, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeterRead ret = impl.getLatestReadings(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.MeterRead ret = impl.getLatestReadingByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfMeterRead ret = impl.getReadingsByUOMAndDate(uomData, startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfHistoryLog ret = impl.getHistoryLogByMeterNo(meterNo, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfHistoryLog ret = impl.getHistoryLogsByDate(startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfHistoryLog ret = impl.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfHistoryLog ret = impl.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.FormattedBlock getLatestReadingByMeterNoAndType(java.lang.String meterNo, java.lang.String readingType) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.FormattedBlock ret = impl.getLatestReadingByMeterNoAndType(meterNo, readingType);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfFormattedBlock getLatestReadingByType(java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfFormattedBlock ret = impl.getLatestReadingByType(readingType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfFormattedBlock getReadingsByDateAndType(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfFormattedBlock ret = impl.getReadingsByDateAndType(startDate, endDate, readingType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfString getSupportedReadingTypes() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfString ret = impl.getSupportedReadingTypes();
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfFormattedBlock getReadingsByMeterNoAndType(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfFormattedBlock ret = impl.getReadingsByMeterNoAndType(meterNo, startDate, endDate, readingType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateMeterReadByMeterNoAndType(com.cannontech.multispeak.service.ArrayOfString meterNos, java.lang.String responseURL, java.lang.String readingType) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateMeterReadByMeterNoAndType(meterNos, responseURL, readingType);
        return ret;
    }

}
