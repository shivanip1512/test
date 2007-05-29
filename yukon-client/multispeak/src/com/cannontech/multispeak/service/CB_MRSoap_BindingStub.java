/**
 * CB_MRSoap_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CB_MRSoap_BindingStub extends org.apache.axis.client.Stub implements com.cannontech.multispeak.service.CB_MRSoap_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[39];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PingURL");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURLResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMethods");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethodsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDomainNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDomainMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfDomainMember.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllCustomers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfCustomer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedCustomers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfCustomer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCustomersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocationsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByCustId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfCustomer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByNameResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByDBAName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dBAName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBANameResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocationsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServiceStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceStatusResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLocId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLocResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByCustId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByGridLocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByPhaseCode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"), com.cannontech.multispeak.service.PhaseCd.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByPhaseCodeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByLoadGroup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadGroup"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroupResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByShutOffDate");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shutOffDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDateResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMetersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMetersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByMeterId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setReturnClass(com.cannontech.multispeak.service.Meter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setReturnClass(com.cannontech.multispeak.service.Meter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByServLocResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByCustID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByCustIDResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByAMRType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "aMRType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfMeter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterGroupNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterGroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupMembersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForCustomer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"), com.cannontech.multispeak.service.Customer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForServiceLocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"), com.cannontech.multispeak.service.ServiceLocation.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForMeter");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"), com.cannontech.multispeak.service.Meter.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReadingChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"), com.cannontech.multispeak.service.ArrayOfMeterRead.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotificationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("HistoryLogChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedHistoryLogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"), com.cannontech.multispeak.service.ArrayOfHistoryLog.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotificationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FormattedBlockNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"), com.cannontech.multispeak.service.FormattedBlock.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "FormattedBlockNotificationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.service.ArrayOfMeter.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.service.ArrayOfErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterInstalledNotificationResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[38] = oper;

    }

    public CB_MRSoap_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CB_MRSoap_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CB_MRSoap_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
        addBindings2();
        addBindings3();
        addBindings4();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">adjustmentItem>balanceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AdjustmentItemBalanceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">anchor>status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AnchorStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">chargeableDevice>actionFlag");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChargeableDeviceActionFlag.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">circuitElement>elementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CircuitElementElementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>eMailList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ContactInfoEMailList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>phoneList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ContactInfoPhoneList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">eMailAddress>eMailType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.EMailAddressEMailType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">extensionsItem>extType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ExtensionsItemExtType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">formattedBlock>valSyntax");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FormattedBlockValSyntax.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">formattedBlock>valueList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FormattedBlockValueList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">FormattedBlockNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FormattedBlockNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">FormattedBlockNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FormattedBlockNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllCustomers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllCustomersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetAllServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByCustId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByCustId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByCustIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByCustIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByDBAName");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByDBAName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByDBANameResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByDBANameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByName");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByNameResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetCustomerByNameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetDomainMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetDomainMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetDomainNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetDomainNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByAMRType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByAMRTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByCustID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByCustID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByCustIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByCustIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByMeterId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByMeterIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterByServLocResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterGroupMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterGroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterGroupNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMeterGroupNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMethods");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMethods.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMethodsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetMethodsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedCustomers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedCustomers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedCustomersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedCustomersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetModifiedServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByCustId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByCustId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByCustIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByCustIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByGridLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByGridLocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByLoadGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByLoadGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByLoadGroupResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByLoadGroupResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByPhaseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByPhaseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByPhaseCodeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByPhaseCodeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServiceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceStatusResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServiceStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByServLocResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByShutOffDate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDateResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GetServiceLocationByShutOffDateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyAgainst");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GuyGuyAgainst.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GuyGuyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GuyStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">HistoryLogChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.HistoryLogChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">HistoryLogChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.HistoryLogChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">linkedTransformer>unitList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LinkedTransformerUnitList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterRead>readingValues");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterReadReadingValues.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterRead>TOUReadings");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterReadTOUReadings.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForCustomerResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForCustomerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForMeterResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForMeterResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForServiceLocationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ModifyCBDataForServiceLocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ohSecondaryLine>secondaryType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OhSecondaryLineSecondaryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">outageCustomer>timeToCall");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageCustomerTimeToCall.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">payableItemList>allowableTransactionTypes");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PayableItemListAllowableTransactionTypes.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">phoneNumber>phoneType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PhoneNumberPhoneType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PingURL");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PingURL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PingURLResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PingURLResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingValue>readingValueType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingValueReadingValueType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ugSecondaryLine>uGSecType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UgSecondaryLineUGSecType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AbstractGeometryType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AbstractGeometryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Accountability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "action");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Action.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AdjustmentItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AdjustmentItemList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AffectedMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AffectedMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AllocatedLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "analogCondition");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AnalogCondition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Anchor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchorList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AnchorList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfAllocatedLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfAllocatedLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBackSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfBackSpan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCapacitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfCapacitor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfCDCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfChannel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannelBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfChannelBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConductor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfConductor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCPR");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfCPR.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfDomainMember.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployee");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfEmployee.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployeeTimeRecord");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfEmployeeTimeRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEquipment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfEquipment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfErrorObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFeederObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfFeederObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericAnnotationFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfGenericAnnotationFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericLineFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfGenericLineFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericPointFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfGenericPointFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGraphicSymbol");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfGraphicSymbol.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfHistoryLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJobWorked");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfJobWorked.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJointUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfJointUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLaborComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfLaborComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMaterialComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfMaterialComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeasurementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfMeasurementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfMeterRead.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfMeterReading.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfOutageCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfProfileType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfRegulator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfRegulator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfScadaPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSource");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfSource.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfStation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString1");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfString1.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfString2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTruck");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfTruck.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsageInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfUsageInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWarehouseLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ArrayOfWarehouseLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Assembly.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assemblyList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.AssemblyList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BackgroundGraphics.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BackSpan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BaseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billedUsage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BilledUsage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BillingAccountLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BillingData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BillingDetail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "BoxType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.BoxType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "breaker");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Breaker.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CabinetContentsList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CallBackList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CallBackStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CallBackType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CallType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Capacitor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CapacitorBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CDCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Channel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChannelBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChargeableDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChargeableDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChargeItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ChargeItemList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CircuitElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coincidentalValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CoincidentalValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ComplexNum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Conductor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "config");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Config.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ConnectDisconnectEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ConnectDisconnectList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ConnectionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constGrade");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ConstGrade.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ContactInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ControlEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordinatesType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CoordinatesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CoordType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CPR.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crew");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Crew.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewAction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CrewAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewActionEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CrewActionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemand");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CumDemand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemandType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CumDemandType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Customer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings2() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CustomerCall.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedByOutage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CustomersAffectedByOutage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAttachedToDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.CustomersAttachedToDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.DeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayFormat");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.DisplayFormat.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.DomainMember.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "durationDescription");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.DurationDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.EaLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementsVoltage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ElementsVoltage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.EMailAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Employee.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.EmployeeTimeRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Equipment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ErrorObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "event");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Event.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.EventCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Extensions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ExtensionsItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ExtensionsList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fakeNodeSection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FakeNodeSection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FeederObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedCharge");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FixedCharge.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCodeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FixedChargeCodeList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "flowDemand");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FlowDemand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "flowDemandType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FlowDemandType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.FormattedBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "frequency");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Frequency.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Fuse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "generator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Generator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GenericAnnotationFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GenericLineFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GenericPointFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPolygonFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GenericPolygonFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GpsLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.GraphicSymbol.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "guy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Guy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.HistoryLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "innerBoundaryIs");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.InnerBoundaryIs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instrumentTransformers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.InstrumentTransformers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interval");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Interval.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.JobWorked.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.JointUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategory");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LaborCategory.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LaborComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LdCon.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LengthUnit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LinearRingType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LinearRingType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LineStringType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LineStringType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkedTransformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LinkedTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistr");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadDistr.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistrict");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadDistrict.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadFlowResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadFlowResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadInterruptibleType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadInterruptibleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadManagementDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadSection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.LoadSection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MaterialComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MaterialItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialManagementAssembly");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MaterialManagementAssembly.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MaterialUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Measurement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeasurementDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeasurementDeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeasurementDeviceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeasurementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Message.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MessageHeaderCSUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MessageHeaderCSUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "messageList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MessageList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Meter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterConnectivity.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterRead.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MeterReading.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "motor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Motor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Mounting.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspBankObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspBankObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspConnectivityLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspConnectivityPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspElectricLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspElectricPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLineObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspLineObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLoadGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspLoadGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspMotorGenerator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspMotorGenerator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspOverCurrentDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspOverCurrentDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspPhase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPointObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspPointObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPolygonObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspPolygonObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspResultsBase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspResultsBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspSwitchDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspSwitchingBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MspSwitchingDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.MultiSpeakMsgHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Nameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings3() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "network");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Network.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.NodeIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfElement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.NumberOfElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ObjectRef.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohPrimaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OhPrimaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohSecondaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OhSecondaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageDetectDeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageDetectDeviceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageDetectionDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageDetectionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageEventStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OutageStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outerBoundaryIs");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OuterBoundaryIs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.OvercurrentDeviceBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parcel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Parcel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PayableItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PayableItemList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PcbTestList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseAssociation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PhaseAssociation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PhaseCd.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PhoneNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "physicalObjectList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PhysicalObjectList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PickList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickListItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PickListItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pmEventCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PmEventCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Pole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PoleClass.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PoleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PoleUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PoleUseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PolygonType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PolygonType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "position");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Position.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PowerFactorManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimitationUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PowerLimitationUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerMonitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PowerMonitor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PowerStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerSystemDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PowerSystemDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PpmBalanceAdjustment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PpmLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmMeterExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PpmMeterExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PpmTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premise");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Premise.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PremiseList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabinet");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PrimaryCabinet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.PriorityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ProfileObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ProfileType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.QualityDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantityType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.QuantityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReadingValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ReasonCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recloser");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Recloser.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Registers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Regulator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.RegulatorBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ResolvedLevel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ResponseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultsType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ResultsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riser");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Riser.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ScadaAnalog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ScadaPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ScadaPoints.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ScadaPointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ScadaStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryJunctionBox");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SecondaryJunctionBox.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionalizer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Sectionalizer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ServiceLocationList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shortCircuitAnalysisResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ShortCircuitAnalysisResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Source.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanGuy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SpanGuy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanTyp");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SpanTyp.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spatialFeatureGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SpatialFeatureGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "staStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.StaStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "station");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Station.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusIdentifier");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.StatusIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.StreetLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Substation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Summation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(simplesf);
            cachedDeserFactories.add(simpledf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supplyVoltage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SupplyVoltage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SwitchDeviceBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SwStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SwType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.SyntaxItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tender");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Tender.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TestInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TimePeriod.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timesheet");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Timesheet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TimeSpan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TOUReading.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Transformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TransformerBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings4() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerRatio");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.TransformerRatio.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "treatment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Treatment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Truck.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugPrimaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UgPrimaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UgSecondaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UnitActn.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UnitPrefix.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UnType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Uom.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.Usage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UsageInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UsageItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UsageItemList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.UtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.WarehouseLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.WdgCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrder");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.WorkOrder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrderSelection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.WorkOrderSelection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workTicket");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.WorkTicket.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zUnit");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.service.ZUnit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/PingURL");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURL"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMethods");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethods"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetDomainNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetDomainMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {domainName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfDomainMember) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfDomainMember) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfDomainMember.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllCustomers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfCustomer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetModifiedCustomers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCustomers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {previousSessionID, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfCustomer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetModifiedServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {previousSessionID, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByCustId");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustId"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {custId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByName");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByName"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {firstName, lastName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfCustomer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfCustomer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByDBAName");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBAName"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dBAName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByServiceStatus");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servStatus, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByServLoc");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLoc"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servLocId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByCustId");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustId"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {custId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByGridLocation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {gridLocation});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByPhaseCode(com.cannontech.multispeak.service.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByPhaseCode");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByPhaseCode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {phaseCode, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByLoadGroup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {loadGroup, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServiceLocationByShutOffDate");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDate"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {shutOffDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetModifiedMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {previousSessionID, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByMeterId");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterId"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.Meter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.Meter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.Meter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.Meter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.Meter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.Meter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByServLoc");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByServLoc"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servLoc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByCustID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByCustID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {custID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByAMRType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {aMRType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfMeter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfMeter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfMeter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfString getMeterGroupNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterGroupNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfString getMeterGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterGroupMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterGroupName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void modifyCBDataForCustomer(com.cannontech.multispeak.service.Customer customerData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForCustomer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForCustomer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {customerData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.service.ServiceLocation serviceLocationData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForServiceLocation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForServiceLocation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceLocationData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void modifyCBDataForMeter(com.cannontech.multispeak.service.Meter meterData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForMeter");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForMeter"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject readingChangedNotification(com.cannontech.multispeak.service.ArrayOfMeterRead changedMeterReads) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ReadingChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedMeterReads});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject historyLogChangedNotification(com.cannontech.multispeak.service.ArrayOfHistoryLog changedHistoryLogs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/HistoryLogChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedHistoryLogs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject formattedBlockNotification(com.cannontech.multispeak.service.FormattedBlock changedMeterReads) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/FormattedBlockNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "FormattedBlockNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedMeterReads});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterInstalledNotification(com.cannontech.multispeak.service.ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterInstalledNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterInstalledNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addedMeters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.service.ArrayOfErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.service.ArrayOfErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
