/**
 * CB_MRSoap_BindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CB_MRSoap_BindingSkeleton implements com.cannontech.multispeak.CB_MRSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.CB_MRSoap_PortType impl;
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
        _oper = new org.apache.axis.description.OperationDesc("getAllCustomers", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomers"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAllCustomers");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllCustomers") == null) {
            _myOperations.put("getAllCustomers", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllCustomers")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedCustomers", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCustomersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCustomers"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedCustomers");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedCustomers") == null) {
            _myOperations.put("getModifiedCustomers", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedCustomers")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedServiceLocations", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocationsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocations"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedServiceLocations");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedServiceLocations") == null) {
            _myOperations.put("getModifiedServiceLocations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedServiceLocations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCustomerByCustId", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustIdResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustId"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCustomerByCustId");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCustomerByCustId") == null) {
            _myOperations.put("getCustomerByCustId", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCustomerByCustId")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCustomerByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCustomerByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCustomerByMeterNo") == null) {
            _myOperations.put("getCustomerByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCustomerByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCustomerByName", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByNameResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByName"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCustomerByName");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCustomerByName") == null) {
            _myOperations.put("getCustomerByName", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCustomerByName")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dBAName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCustomerByDBAName", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBANameResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBAName"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetCustomerByDBAName");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCustomerByDBAName") == null) {
            _myOperations.put("getCustomerByDBAName", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCustomerByDBAName")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAllServiceLocations", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocationsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocations"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAllServiceLocations");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllServiceLocations") == null) {
            _myOperations.put("getAllServiceLocations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllServiceLocations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByServiceStatus", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceStatusResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceStatus"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByServiceStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByServiceStatus") == null) {
            _myOperations.put("getServiceLocationByServiceStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByServiceStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLocId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByServLoc", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLocResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLoc"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByServLoc");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByServLoc") == null) {
            _myOperations.put("getServiceLocationByServLoc", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByServLoc")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByCustId", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustIdResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustId"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByCustId");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByCustId") == null) {
            _myOperations.put("getServiceLocationByCustId", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByCustId")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByAccountNumber", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumberResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumber"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByAccountNumber");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByAccountNumber") == null) {
            _myOperations.put("getServiceLocationByAccountNumber", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByAccountNumber")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByGridLocation", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocation"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByGridLocation");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByGridLocation") == null) {
            _myOperations.put("getServiceLocationByGridLocation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByGridLocation")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"), com.cannontech.multispeak.PhaseCd.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByPhaseCode", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByPhaseCodeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByPhaseCode"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByPhaseCode");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByPhaseCode") == null) {
            _myOperations.put("getServiceLocationByPhaseCode", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByPhaseCode")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadGroup"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByLoadGroup", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroupResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroup"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByLoadGroup");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByLoadGroup") == null) {
            _myOperations.put("getServiceLocationByLoadGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByLoadGroup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByServiceType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByServiceType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByServiceType") == null) {
            _myOperations.put("getServiceLocationByServiceType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByServiceType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shutOffDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceLocationByShutOffDate", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDateResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDate"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetServiceLocationByShutOffDate");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceLocationByShutOffDate") == null) {
            _myOperations.put("getServiceLocationByShutOffDate", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceLocationByShutOffDate")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAllMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAllMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllMeters") == null) {
            _myOperations.put("getAllMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedMeters", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMetersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMeters"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedMeters");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedMeters") == null) {
            _myOperations.put("getModifiedMeters", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedMeters")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByMeterId", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterIdResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterId"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByMeterId");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByMeterId") == null) {
            _myOperations.put("getMeterByMeterId", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByMeterId")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByMeterNo", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNo"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByMeterNo");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByMeterNo") == null) {
            _myOperations.put("getMeterByMeterNo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByMeterNo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByServLoc", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByServLocResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByServLoc"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByServLoc");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByServLoc") == null) {
            _myOperations.put("getMeterByServLoc", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByServLoc")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByAccountNumber", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumberResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumber"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByAccountNumber");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByAccountNumber") == null) {
            _myOperations.put("getMeterByAccountNumber", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByAccountNumber")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByCustID", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByCustIDResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByCustID"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByCustID");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByCustID") == null) {
            _myOperations.put("getMeterByCustID", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByCustID")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "aMRType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMeterByAMRType", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRTypeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRType"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMeterByAMRType");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMeterByAMRType") == null) {
            _myOperations.put("getMeterByAMRType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMeterByAMRType")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"), com.cannontech.multispeak.Customer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("modifyCBDataForCustomer", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForCustomer"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ModifyCBDataForCustomer");
        _myOperationsList.add(_oper);
        if (_myOperations.get("modifyCBDataForCustomer") == null) {
            _myOperations.put("modifyCBDataForCustomer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("modifyCBDataForCustomer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"), com.cannontech.multispeak.ServiceLocation.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("modifyCBDataForServiceLocation", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForServiceLocation"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ModifyCBDataForServiceLocation");
        _myOperationsList.add(_oper);
        if (_myOperations.get("modifyCBDataForServiceLocation") == null) {
            _myOperations.put("modifyCBDataForServiceLocation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("modifyCBDataForServiceLocation")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"), com.cannontech.multispeak.Meter.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("modifyCBDataForMeter", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForMeter"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ModifyCBDataForMeter");
        _myOperationsList.add(_oper);
        if (_myOperations.get("modifyCBDataForMeter") == null) {
            _myOperations.put("modifyCBDataForMeter", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("modifyCBDataForMeter")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"), com.cannontech.multispeak.ArrayOfMeterRead.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("readingChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/ReadingChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("readingChangedNotification") == null) {
            _myOperations.put("readingChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("readingChangedNotification")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedHistoryLogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"), com.cannontech.multispeak.ArrayOfHistoryLog.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("historyLogChangedNotification", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotificationResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotification"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/HistoryLogChangedNotification");
        _myOperationsList.add(_oper);
        if (_myOperations.get("historyLogChangedNotification") == null) {
            _myOperations.put("historyLogChangedNotification", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("historyLogChangedNotification")).add(_oper);
    }

    public CB_MRSoap_BindingSkeleton() {
        this.impl = new com.cannontech.multispeak.CB_MRSoap_BindingImpl();
    }

    public CB_MRSoap_BindingSkeleton(com.cannontech.multispeak.CB_MRSoap_PortType impl) {
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

    public com.cannontech.multispeak.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCustomer ret = impl.getAllCustomers(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCustomer ret = impl.getModifiedCustomers(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getModifiedServiceLocations(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.Customer ret = impl.getCustomerByCustId(custId);
        return ret;
    }

    public com.cannontech.multispeak.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.Customer ret = impl.getCustomerByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCustomer ret = impl.getCustomerByName(firstName, lastName);
        return ret;
    }

    public com.cannontech.multispeak.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.Customer ret = impl.getCustomerByDBAName(dBAName);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getAllServiceLocations(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByServiceStatus(servStatus, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ServiceLocation ret = impl.getServiceLocationByServLoc(servLocId);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByCustId(custId);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByAccountNumber(accountNumber);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByGridLocation(gridLocation);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByPhaseCode(com.cannontech.multispeak.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByPhaseCode(phaseCode, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByLoadGroup(loadGroup, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByServiceType(serviceType, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfServiceLocation ret = impl.getServiceLocationByShutOffDate(shutOffDate);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getAllMeters(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getModifiedMeters(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.Meter ret = impl.getMeterByMeterId(meterID);
        return ret;
    }

    public com.cannontech.multispeak.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.Meter ret = impl.getMeterByMeterNo(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getMeterByServLoc(servLoc);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getMeterByAccountNumber(accountNumber);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getMeterByCustID(custID);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeter getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeter ret = impl.getMeterByAMRType(aMRType, lastReceived);
        return ret;
    }

    public void modifyCBDataForCustomer(com.cannontech.multispeak.Customer customerData) throws java.rmi.RemoteException
    {
        impl.modifyCBDataForCustomer(customerData);
    }

    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.ServiceLocation serviceLocationData) throws java.rmi.RemoteException
    {
        impl.modifyCBDataForServiceLocation(serviceLocationData);
    }

    public void modifyCBDataForMeter(com.cannontech.multispeak.Meter meterData) throws java.rmi.RemoteException
    {
        impl.modifyCBDataForMeter(meterData);
    }

    public com.cannontech.multispeak.ArrayOfErrorObject readingChangedNotification(com.cannontech.multispeak.ArrayOfMeterRead changedMeterReads) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.readingChangedNotification(changedMeterReads);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotification(com.cannontech.multispeak.ArrayOfHistoryLog changedHistoryLogs) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.historyLogChangedNotification(changedHistoryLogs);
        return ret;
    }

}
