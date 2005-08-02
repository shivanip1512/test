/**
 * MR_CBSoap_BindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MR_CBSoap_BindingSkeleton implements com.cannontech.multispeak.MR_CBSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.MR_CBSoap_PortType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReadingsByBillingCycle", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByBillingCycleResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), com.cannontech.multispeak.EventCode.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"), com.cannontech.multispeak.EventCode.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"), com.cannontech.multispeak.ArrayOfString.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedCustomers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"), com.cannontech.multispeak.ArrayOfCustomer.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedServiceLocations"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"), com.cannontech.multispeak.ArrayOfServiceLocation.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.ArrayOfMeter.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "removedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.ArrayOfMeter.class, false, false), 
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.ArrayOfMeter.class, false, false), 
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
    }

    public MR_CBSoap_BindingSkeleton() {
        this.impl = new com.cannontech.multispeak.MR_CBSoap_BindingImpl();
    }

    public MR_CBSoap_BindingSkeleton(com.cannontech.multispeak.MR_CBSoap_PortType impl) {
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

    public com.cannontech.multispeak.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getAMRSupportedMeters(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getModifiedAMRMeters(previousSessionID, lastReceived);
        return ret;
    }

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        boolean ret = impl.isAMRMeter(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterRead ret = impl.getReadingsByDate(startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterRead ret = impl.getReadingsByMeterNo(meterNo, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.MeterRead ret = impl.getLatestReadingByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterRead ret = impl.getReadingsByBillingCycle(billingCycle, startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfHistoryLog ret = impl.getHistoryLogByMeterNo(meterNo, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfHistoryLog ret = impl.getHistoryLogsByDate(startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfHistoryLog ret = impl.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfHistoryLog ret = impl.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.initiatePlannedOutage(meterNos, startDate, endDate);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.cancelPlannedOutage(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.initiateUsageMonitoring(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.cancelUsageMonitoring(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.initiateDisconnectedStatus(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.cancelDisconnectedStatus(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.initiateMeterReadByMeterNumber(meterNos);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.customerChangedNotification(changedCustomers);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.serviceLocationChangedNotification(changedServiceLocations);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter changedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.meterChangedNotification(changedMeters);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.ArrayOfMeter removedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.meterRemoveNotification(removedMeters);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.ArrayOfMeter addedMeters) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.meterAddNotification(addedMeters);
        return ret;
    }

}
