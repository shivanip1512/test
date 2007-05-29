/**
 * MR_CBSoap12Skeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MR_CBSoap12Skeleton implements com.cannontech.multispeak.service.MR_CBSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.service.MR_CBSoap_PortType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("isAMRMeter", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IsAMRMeterResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IsAMRMeter"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/IsAMRMeter");
        _myOperationsList.add(_oper);
        if (_myOperations.get("isAMRMeter") == null) {
            _myOperations.put("isAMRMeter", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("isAMRMeter")).add(_oper);
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingCycle"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWhLookBack"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookBack"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookForward"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByBillingCycle", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByBillingCycleResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFormattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByBillingCycle"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetReadingsByBillingCycle");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReadingsByBillingCycle") == null) {
            _myOperations.put("getReadingsByBillingCycle", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReadingsByBillingCycle")).add(_oper);
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLatestMeterReadingsByMeterGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestMeterReadingsByMeterGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestMeterReadingsByMeterGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetLatestMeterReadingsByMeterGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLatestMeterReadingsByMeterGroup") == null) {
            _myOperations.put("getLatestMeterReadingsByMeterGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLatestMeterReadingsByMeterGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiatePlannedOutage", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiatePlannedOutageResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiatePlannedOutage"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiatePlannedOutage");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiatePlannedOutage") == null) {
            _myOperations.put("initiatePlannedOutage", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiatePlannedOutage")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("cancelPlannedOutage", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelPlannedOutageResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelPlannedOutage"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CancelPlannedOutage");
        _myOperationsList.add(_oper);
        if (_myOperations.get("cancelPlannedOutage") == null) {
            _myOperations.put("cancelPlannedOutage", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("cancelPlannedOutage")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateUsageMonitoring", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateUsageMonitoringResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateUsageMonitoring"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateUsageMonitoring");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateUsageMonitoring") == null) {
            _myOperations.put("initiateUsageMonitoring", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateUsageMonitoring")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("cancelUsageMonitoring", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelUsageMonitoringResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelUsageMonitoring"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CancelUsageMonitoring");
        _myOperationsList.add(_oper);
        if (_myOperations.get("cancelUsageMonitoring") == null) {
            _myOperations.put("cancelUsageMonitoring", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("cancelUsageMonitoring")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateDisconnectedStatus", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateDisconnectedStatusResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateDisconnectedStatus"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateDisconnectedStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateDisconnectedStatus") == null) {
            _myOperations.put("initiateDisconnectedStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateDisconnectedStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("cancelDisconnectedStatus", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelDisconnectedStatusResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelDisconnectedStatus"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/CancelDisconnectedStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("cancelDisconnectedStatus") == null) {
            _myOperations.put("cancelDisconnectedStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("cancelDisconnectedStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateMeterReadByMeterNumber", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateMeterReadByMeterNumberResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateMeterReadByMeterNumber"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateMeterReadByMeterNumber");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateMeterReadByMeterNumber") == null) {
            _myOperations.put("initiateMeterReadByMeterNumber", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateMeterReadByMeterNumber")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"), com.cannontech.multispeak.service.MeterGroup.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("establishMeterGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EstablishMeterGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EstablishMeterGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/EstablishMeterGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("establishMeterGroup") == null) {
            _myOperations.put("establishMeterGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("establishMeterGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteMeterGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DeleteMeterGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DeleteMeterGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/DeleteMeterGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteMeterGroup") == null) {
            _myOperations.put("deleteMeterGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteMeterGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumbers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("insertMeterInMeterGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InsertMeterInMeterGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InsertMeterInMeterGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InsertMeterInMeterGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("insertMeterInMeterGroup") == null) {
            _myOperations.put("insertMeterInMeterGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("insertMeterInMeterGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumbers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.service.ArrayOfString.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("removeMetersFromMeterGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RemoveMetersFromMeterGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RemoveMetersFromMeterGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/RemoveMetersFromMeterGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("removeMetersFromMeterGroup") == null) {
            _myOperations.put("removeMetersFromMeterGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("removeMetersFromMeterGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initiateGroupMeterRead", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateGroupMeterReadResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateGroupMeterRead"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/InitiateGroupMeterRead");
        _myOperationsList.add(_oper);
        if (_myOperations.get("initiateGroupMeterRead") == null) {
            _myOperations.put("initiateGroupMeterRead", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initiateGroupMeterRead")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeToRead"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("scheduleGroupMeterRead", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ScheduleGroupMeterReadResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ScheduleGroupMeterRead"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ScheduleGroupMeterRead");
        _myOperationsList.add(_oper);
        if (_myOperations.get("scheduleGroupMeterRead") == null) {
            _myOperations.put("scheduleGroupMeterRead", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("scheduleGroupMeterRead")).add(_oper);
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
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "removedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.service.ArrayOfMeter.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterRemoveNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterRemoveNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterRemoveNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterRemoveNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterRemoveNotification") == null) {
            _myOperations.put("meterRemoveNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterRemoveNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "retiredMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.service.ArrayOfMeter.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterRetireNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterRetireNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterRetireNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterRetireNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterRetireNotification") == null) {
            _myOperations.put("meterRetireNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterRetireNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.service.ArrayOfMeter.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterAddNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterAddNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterAddNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterAddNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterAddNotification") == null) {
            _myOperations.put("meterAddNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterAddNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterExchange"), com.cannontech.multispeak.service.ArrayOfMeterExchange.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("meterExchangeNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterExchangeNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterExchangeNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/MeterExchangeNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("meterExchangeNotification") == null) {
            _myOperations.put("meterExchangeNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("meterExchangeNotification")).add(_oper);
    }

    public MR_CBSoap12Skeleton() {
        this.impl = new com.cannontech.multispeak.service.MR_CBSoap12Impl();
    }

    public MR_CBSoap12Skeleton(com.cannontech.multispeak.service.MR_CBSoap_PortType impl) {
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

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        boolean ret = impl.isAMRMeter(meterNo);
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

    public com.cannontech.multispeak.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.MeterRead ret = impl.getLatestReadingByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfFormattedBlock getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfFormattedBlock ret = impl.getReadingsByBillingCycle(billingCycle, billingDate, kWhLookBack, kWLookBack, kWLookForward, lastReceived);
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

    public com.cannontech.multispeak.service.FormattedBlock getLatestMeterReadingsByMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.FormattedBlock ret = impl.getLatestMeterReadingsByMeterGroup(meterGroupID);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.service.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiatePlannedOutage(meterNos, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.cancelPlannedOutage(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateUsageMonitoring(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.cancelUsageMonitoring(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateDisconnectedStatus(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.cancelDisconnectedStatus(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.service.ArrayOfString meterNos, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateMeterReadByMeterNumber(meterNos, responseURL);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject establishMeterGroup(com.cannontech.multispeak.service.MeterGroup meterGroup) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.establishMeterGroup(meterGroup);
        return ret;
    }

    public com.cannontech.multispeak.service.ErrorObject deleteMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ErrorObject ret = impl.deleteMeterGroup(meterGroupID);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject insertMeterInMeterGroup(com.cannontech.multispeak.service.ArrayOfString meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.insertMeterInMeterGroup(meterNumbers, meterGroupID);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject removeMetersFromMeterGroup(com.cannontech.multispeak.service.ArrayOfString meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateGroupMeterRead(java.lang.String meterGroupName, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.initiateGroupMeterRead(meterGroupName, responseURL);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject scheduleGroupMeterRead(java.lang.String meterGroupName, java.util.Calendar timeToRead, java.lang.String responseURL) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.scheduleGroupMeterRead(meterGroupName, timeToRead, responseURL);
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

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.service.ArrayOfMeter removedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterRemoveNotification(removedMeters);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterRetireNotification(com.cannontech.multispeak.service.ArrayOfMeter retiredMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterRetireNotification(retiredMeters);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.service.ArrayOfMeter addedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterAddNotification(addedMeters);
        return ret;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterExchangeNotification(com.cannontech.multispeak.service.ArrayOfMeterExchange meterChangeout) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.service.ArrayOfErrorObject ret = impl.meterExchangeNotification(meterChangeout);
        return ret;
    }

}
