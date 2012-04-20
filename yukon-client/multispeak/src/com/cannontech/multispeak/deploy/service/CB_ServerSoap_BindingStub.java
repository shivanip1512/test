/**
 * CB_ServerSoap_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CB_ServerSoap_BindingStub extends org.apache.axis.client.Stub implements com.cannontech.multispeak.deploy.service.CB_ServerSoap_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[141];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
        _initOperationDesc7();
        _initOperationDesc8();
        _initOperationDesc9();
        _initOperationDesc10();
        _initOperationDesc11();
        _initOperationDesc12();
        _initOperationDesc13();
        _initOperationDesc14();
        _initOperationDesc15();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetNextNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"), com.cannontech.multispeak.deploy.service.ExtensionsItem[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNumber"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.RequestedNumber.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetNextNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersByEALocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByEALocationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersByFacilityID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByFacilityIDResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersBySiteID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "siteID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersBySiteIDResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersByCustomerName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByCustomerNameResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersByHomePhone");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homeAC"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homePhone"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByHomePhoneResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMetersBySearchString");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "searchString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersBySearchStringResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetConfigurationGroupNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetConfigurationGroupNamesByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), com.cannontech.multispeak.deploy.service.ServiceType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupNamesByMeterNoResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetConfigurationGroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.MeterGroups.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupMembersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllStreetLights");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStreetLight"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.StreetLight[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllStreetLightsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllSecurityLights");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSecurityLight"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.SecurityLight[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllSecurityLightsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetIHDGroupNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetIHDGroupNamesByInHomeDisplayID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupNamesByInHomeDisplayIDResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetIHDGroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IHDGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.InHomeDisplayGroup.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupMembersResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterBaseByObjectId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBaseID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.MeterBase.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterBaseByObjectIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllConnectDisconnectEventsByReasonCode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConnectDisconnectEvent"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllConnectDisconnectEventsByReasonCodeResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForCustomer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"), com.cannontech.multispeak.deploy.service.Customer[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForCustomerResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForServiceLocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"), com.cannontech.multispeak.deploy.service.ServiceLocation[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForServiceLocationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForMeter");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.deploy.service.Meter[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForMeterResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForPole");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPole"), com.cannontech.multispeak.deploy.service.Pole[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForPoleResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForTransformerBank");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "xfmrBankData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformerBank"), com.cannontech.multispeak.deploy.service.TransformerBank[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForTransformerBankResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForUsage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsage"), com.cannontech.multispeak.deploy.service.Usage[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForUsageResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForStreetLight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLightData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStreetLight"), com.cannontech.multispeak.deploy.service.StreetLight[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForStreetLightResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyCBDataForSecurityLight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLightData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSecurityLight"), com.cannontech.multispeak.deploy.service.SecurityLight[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForSecurityLightResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReadingChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"), com.cannontech.multispeak.deploy.service.MeterRead[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("HistoryLogChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedHistoryLogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"), com.cannontech.multispeak.deploy.service.HistoryLog[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FormattedBlockNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"), com.cannontech.multispeak.deploy.service.FormattedBlock.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "FormattedBlockNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedMeters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"), com.cannontech.multispeak.deploy.service.Meter[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterInstalledNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterExchangeNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterExchange"), com.cannontech.multispeak.deploy.service.MeterExchange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterExchangeNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LMDeviceExchangeNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLMDeviceExchange"), com.cannontech.multispeak.deploy.service.LMDeviceExchange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchangeNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LMDeviceInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "installedLMDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"), com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceInstalledNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDStateChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stateChange"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"), com.cannontech.multispeak.deploy.service.LoadActionCode.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDStatesChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stateChanges"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDStateChange"), com.cannontech.multispeak.deploy.service.CDStateChange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChange"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStatesChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ProcessPaymentTransaction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"), com.cannontech.multispeak.deploy.service.PaymentTransaction[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PaymentTransaction[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ProcessPaymentTransactionResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CommitPaymentTransaction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"), com.cannontech.multispeak.deploy.service.PaymentTransaction[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PaymentTransaction[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CommitPaymentTransactionResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RequestPaymentExtension");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtensionList"), com.cannontech.multispeak.deploy.service.PaymentExtension[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtensionList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PaymentExtension[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RequestPaymentExtensionResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ExtendPayment");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtensionList"), com.cannontech.multispeak.deploy.service.PaymentExtension[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ExtendPaymentResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LoadProfileChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedLoadProfiles"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileObject"), com.cannontech.multispeak.deploy.service.ProfileObject[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LoadProfileChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PoleChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedPoles"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPole"), com.cannontech.multispeak.deploy.service.Pole[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PoleChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ServiceLocationNetworkChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedServiceLocations"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"), com.cannontech.multispeak.deploy.service.ServiceLocation[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationNetworkChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("TransformerBankChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedXfmrBanks"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformerBank"), com.cannontech.multispeak.deploy.service.TransformerBank[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TransformerBankChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("EndDeviceShipmentNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shipment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDeviceShipment"), com.cannontech.multispeak.deploy.service.EndDeviceShipment.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EndDeviceShipmentNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterTestTransaction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "test"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTestList"), com.cannontech.multispeak.deploy.service.MeterTest[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterTestTransactionResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InHomeDisplayInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedIHDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfInHomeDisplay"), com.cannontech.multispeak.deploy.service.InHomeDisplay[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InHomeDisplayInstalledNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InHomeDisplayExchangeNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IHDChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfInHomeDisplayExchange"), com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InHomeDisplayExchangeNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDDeviceExchangeNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDDeviceExchange"), com.cannontech.multispeak.deploy.service.CDDeviceExchange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchangeNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDDeviceInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "installedCDDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDDevice"), com.cannontech.multispeak.deploy.service.CDDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceInstalledNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReturnGeneratedNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNum"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNumber"), com.cannontech.multispeak.deploy.service.RequestedNumber.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReturnGeneratedNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("WriteAccountHistoryComments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "comments"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryComment"), com.cannontech.multispeak.deploy.service.HistoryComment[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyComment"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "WriteAccountHistoryCommentsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDStateNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "state"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState"), com.cannontech.multispeak.deploy.service.CDState.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CDStatesNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "states"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDState"), com.cannontech.multispeak.deploy.service.CDState[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStatesNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReadingScheduleResultNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scheduleResult"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfReadingScheduleResult"), com.cannontech.multispeak.deploy.service.ReadingScheduleResult[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleResult"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingScheduleResultNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterEventNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "events"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterEventList"), com.cannontech.multispeak.deploy.service.MeterEventList.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterEventNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterConnectivityNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newConnectivity"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"), com.cannontech.multispeak.deploy.service.MeterConnectivity[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterConnectivityNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterBaseExchangeNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MBChangeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterBaseExchange"), com.cannontech.multispeak.deploy.service.MeterBaseExchange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBaseExchange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterBaseExchangeNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MeterBaseInstalledNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedMBs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterBase"), com.cannontech.multispeak.deploy.service.MeterBase[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterBaseInstalledNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PingURL");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURLResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[57] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMethods");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethodsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[58] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDomainNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[59] = oper;

    }

    private static void _initOperationDesc7(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDomainMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.DomainMember[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[60] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RequestRegistrationID");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RequestRegistrationIDResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[61] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RegisterForService");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationDetails"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationInfo"), com.cannontech.multispeak.deploy.service.RegistrationInfo.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RegisterForServiceResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[62] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UnregisterForService");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "UnregisterForServiceResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[63] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetRegistrationInfoByID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationInfo"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.RegistrationInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetRegistrationInfoByIDResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[64] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPublishMethods");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPublishMethodsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[65] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DomainMembersChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedDomainMembers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"), com.cannontech.multispeak.deploy.service.DomainMember[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DomainMembersChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[66] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DomainNamesChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedDomainNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainNameChange"), com.cannontech.multispeak.deploy.service.DomainNameChange[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameChange"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DomainNamesChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[67] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllOCDB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOvercurrentDeviceBank"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllOCDBResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[68] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllCustomers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[69] = oper;

    }

    private static void _initOperationDesc8(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedCustomers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCustomersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[70] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[71] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByCustId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[72] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[73] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByNameResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[74] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByDBAName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dBAName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBANameResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[75] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[76] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServiceStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceStatusResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[77] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLocId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLocResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[78] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByCustId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustIdResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[79] = oper;

    }

    private static void _initOperationDesc9(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[80] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumberResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[81] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByGridLocation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[82] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByPhaseCode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"), com.cannontech.multispeak.deploy.service.PhaseCd.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByPhaseCodeResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[83] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByLoadGroup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadGroup"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroupResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[84] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServiceTypeResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[85] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceLocationByShutOffDate");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shutOffDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDateResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[86] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[87] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[88] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByMeterId");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterIdResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[89] = oper;

    }

    private static void _initOperationDesc10(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[90] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByServLocResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[91] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumberResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[92] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByCustID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByCustIDResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[93] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByAMRType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "aMRType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRTypeResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[94] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterGroupNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[95] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterGroupNamesByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupNamesByMeterNoResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[96] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterGroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupMembersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[97] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPPMCustomer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PpmLocation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMCustomerResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[98] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetChargeableDevicesByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChargeableDevice"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ChargeableDevice[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetChargeableDevicesByAccountNumberResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[99] = oper;

    }

    private static void _initOperationDesc11(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPPMPayments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stopTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPaymentTransactionList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PaymentTransaction[][].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMPaymentsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[100] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPPMBalanceAdjustments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stopTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPpmBalanceAdjustment"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMBalanceAdjustmentsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[101] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBillingData");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startBillDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endBillDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingData"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.BillingData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingDataResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[102] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBillingDetail");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cisBillDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingDetail"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.BillingDetail[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingDetailResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[103] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBilledUsage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cisBillDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billedUsage"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.BilledUsage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBilledUsageResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[104] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterHistoryByMeterID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), com.cannontech.multispeak.deploy.service.ServiceType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterHistoryEvent"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.MeterHistoryEvent[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterHistoryByMeterIDResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterHistoryEvent"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[105] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPayableItemsList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PayableItemList.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPayableItemsListResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[106] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetConvenienceFees");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList"), com.cannontech.multispeak.deploy.service.PaymentTransaction[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeList"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConvenienceFeesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[107] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBillingAccountLoadDataByMonth");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "month"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingAccountLoad"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.BillingAccountLoad[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingAccountLoadDataByMonthResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[108] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServLocIDByHomePhone");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homeAc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homePhone"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByHomePhoneResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[109] = oper;

    }

    private static void _initOperationDesc12(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServLocIDByMeter");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByMeterResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[110] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServLocIDByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[111] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByHomePhone");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homePhone"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homeAc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByHomePhoneResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[112] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedPolesFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPole"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Pole[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedPolesFromCBResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[113] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllPolesFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPole"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Pole[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPolesFromCBResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[114] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPoleByPoleNumberFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Pole.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPoleByPoleNumberFromCBResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[115] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllTransformerBanksFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformerBank"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.TransformerBank[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllTransformerBanksFromCBResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[116] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetModifiedTransformerBanksFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformerBank"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.TransformerBank[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedTransformerBanksFromCBResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[117] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetTransformerBankByBankIDFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bankID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.TransformerBank.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetTransformerBankByBankIDFromCBResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[118] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetTransformerBankByUnitIDFromCB");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.TransformerBank.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetTransformerBankByUnitIDFromCBResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[119] = oper;

    }

    private static void _initOperationDesc13(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetUsageByMonth");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "monthNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsage"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Usage[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByMonthResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[120] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetUsageByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsage"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Usage[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByServLocResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[121] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetUsageByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsage"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Usage[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByAccountNumberResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[122] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetUsageByMeterNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Usage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByMeterNoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[123] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllMetersByServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMetersByServiceTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[124] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllServiceLocationsByServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocations"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ServiceLocations.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocationsByServiceTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[125] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMeterByAccountNumberAndServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumberAndServiceTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[126] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllElectricMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfElectricMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ElectricMeter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllElectricMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[127] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllGasMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGasMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.GasMeter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllGasMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[128] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllWaterMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWaterMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.WaterMeter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllWaterMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[129] = oper;

    }

    private static void _initOperationDesc14(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllPropaneMeters");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPropaneMeter"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PropaneMeter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPropaneMetersResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[130] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllElectricServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfElectricServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ElectricServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllElectricServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[131] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllGasServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGasServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.GasServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllGasServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[132] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllWaterServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWaterServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.WaterServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllWaterServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[133] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllPropaneServiceLocations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPropaneServiceLocation"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.PropaneServiceLocation[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPropaneServiceLocationsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[134] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetElectricMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetElectricMeterByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[135] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetGasMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetGasMeterByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[136] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetWaterMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetWaterMeterByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[137] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetPropaneMeterByAccountNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Meters.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPropaneMeterByAccountNumberResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[138] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllCustomersByServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomersByServiceTypeResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[139] = oper;

    }

    private static void _initOperationDesc15(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCustomerByMeterNumberAndServiceType");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.Customer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNumberAndServiceTypeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[140] = oper;

    }

    public CB_ServerSoap_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CB_ServerSoap_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CB_ServerSoap_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
        addBindings5();
        addBindings6();
        addBindings7();
        addBindings8();
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
            cls = com.cannontech.multispeak.deploy.service.AdjustmentItemBalanceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">anchor>status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AnchorStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDDeviceExchangeNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceExchangeNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDDeviceExchangeNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceExchangeNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDDeviceInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDDeviceInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStateChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStateChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStateNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStateNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStatesChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStatesChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStatesChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStatesChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStatesNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStatesNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CDStatesNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStatesNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">chargeableDevice>actionFlag");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeableDeviceActionFlag.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">circuitElement>elementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CircuitElementElementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CommitPaymentTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CommitPaymentTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CommitPaymentTransactionResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CommitPaymentTransactionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">cut>actionTaken");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CutActionTaken.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DomainMembersChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainMembersChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DomainMembersChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainMembersChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DomainNamesChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainNamesChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DomainNamesChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainNamesChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">eMailAddress>eMailType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EMailAddressEMailType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">EndDeviceShipmentNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EndDeviceShipmentNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">EndDeviceShipmentNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EndDeviceShipmentNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ExtendPayment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtendPayment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ExtendPaymentResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtendPaymentResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">extensionsItem>extType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtensionsItemExtType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">FormattedBlockNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FormattedBlockNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">FormattedBlockNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FormattedBlockNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasFlow");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateGasFlow.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasPressure");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateGasPressure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gearDriveSize");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateGearDriveSize.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>internalPipeDiameter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateInternalPipeDiameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>measurementSystem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateMeasurementSystem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>mechanicalForm");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateMechanicalForm.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>pressureCompensationType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplatePressureCompensationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>temperatureCompensationType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplateTemperatureCompensationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllConnectDisconnectEventsByReasonCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllConnectDisconnectEventsByReasonCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllConnectDisconnectEventsByReasonCodeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllConnectDisconnectEventsByReasonCodeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllCustomers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomersByServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllCustomersByServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomersByServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllCustomersByServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllCustomersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllCustomersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllElectricMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllElectricMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllElectricMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllElectricMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllElectricServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllElectricServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllElectricServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllElectricServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllGasMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllGasMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllGasMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllGasMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllGasServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllGasServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllGasServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllGasServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMetersByServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllMetersByServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMetersByServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllMetersByServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllOCDB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllOCDB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllOCDBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllOCDBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPolesFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPolesFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPolesFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPolesFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPropaneMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPropaneMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPropaneMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPropaneMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPropaneServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPropaneServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllPropaneServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllPropaneServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllSecurityLights");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllSecurityLights.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllSecurityLightsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllSecurityLightsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocationsByServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllServiceLocationsByServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocationsByServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllServiceLocationsByServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllStreetLights");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllStreetLights.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllStreetLightsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllStreetLightsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllTransformerBanksFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllTransformerBanksFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllTransformerBanksFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllTransformerBanksFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllWaterMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllWaterMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllWaterMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllWaterMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllWaterServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllWaterServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllWaterServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllWaterServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBilledUsage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBilledUsage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBilledUsageResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBilledUsageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingAccountLoadDataByMonth");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingAccountLoadDataByMonth.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingAccountLoadDataByMonthResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingAccountLoadDataByMonthResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingData");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingDataResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingDataResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingDetail");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingDetail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingDetailResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetBillingDetailResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetChargeableDevicesByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetChargeableDevicesByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetChargeableDevicesByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetChargeableDevicesByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupNamesByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupNamesByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupNamesByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupNamesByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConfigurationGroupNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConvenienceFees");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConvenienceFees.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConvenienceFeesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetConvenienceFeesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByCustId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByCustId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByCustIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByCustIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByDBAName");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByDBAName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByDBANameResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByDBANameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByHomePhone");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByHomePhone.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByHomePhoneResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByHomePhoneResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNumberAndServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByMeterNumberAndServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNumberAndServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByMeterNumberAndServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByName");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByNameResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetCustomerByNameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetDomainMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetDomainMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetDomainNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetDomainNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetElectricMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetElectricMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetElectricMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetElectricMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetGasMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetGasMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetGasMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetGasMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNamesByInHomeDisplayID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupNamesByInHomeDisplayID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNamesByInHomeDisplayIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupNamesByInHomeDisplayIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetIHDGroupNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterBaseByObjectId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterBaseByObjectId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterBaseByObjectIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterBaseByObjectIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumberAndServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAccountNumberAndServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumberAndServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAccountNumberAndServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAMRType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByAMRTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByCustID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByCustID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByCustIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByCustIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByMeterId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByMeterIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterByServLocResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupMembers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupMembers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNames");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupNames.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNamesByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupNamesByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNamesByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupNamesByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterGroupNamesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterGroupNamesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterHistoryByMeterID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterHistoryByMeterID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterHistoryByMeterIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMeterHistoryByMeterIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByCustomerName");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByCustomerName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByCustomerNameResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByCustomerNameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByEALocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByEALocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByEALocationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByEALocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByFacilityID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByFacilityID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByFacilityIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByFacilityIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByHomePhone");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByHomePhone.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersByHomePhoneResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersByHomePhoneResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersBySearchString");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersBySearchString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersBySearchStringResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersBySearchStringResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersBySiteID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersBySiteID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMetersBySiteIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMetersBySiteIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMethods");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMethods.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMethodsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetMethodsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedCustomers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedCustomers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedCustomersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedCustomersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedMeters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMetersResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedMetersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedPolesFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedPolesFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedPolesFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedPolesFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocationsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedServiceLocationsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedTransformerBanksFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedTransformerBanksFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedTransformerBanksFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetModifiedTransformerBanksFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetNextNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetNextNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetNextNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetNextNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPayableItemsList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPayableItemsList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPayableItemsListResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPayableItemsListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPoleByPoleNumberFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPoleByPoleNumberFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPoleByPoleNumberFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPoleByPoleNumberFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMBalanceAdjustments");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMBalanceAdjustments.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMBalanceAdjustmentsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMBalanceAdjustmentsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMCustomerResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMCustomerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMPayments");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMPayments.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPPMPaymentsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPPMPaymentsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPropaneMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPropaneMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPropaneMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPropaneMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPublishMethods");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPublishMethods.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPublishMethodsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetPublishMethodsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetRegistrationInfoByID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetRegistrationInfoByID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetRegistrationInfoByIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetRegistrationInfoByIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByCustId");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByCustId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByCustIdResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByCustIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByGridLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByGridLocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByLoadGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByLoadGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByLoadGroupResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByLoadGroupResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByPhaseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByPhaseCode.class;
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByPhaseCodeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByPhaseCodeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServiceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceStatusResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServiceStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServiceTypeResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServiceTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByServLocResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByShutOffDate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDateResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServiceLocationByShutOffDateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByHomePhone");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByHomePhone.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByHomePhoneResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByHomePhoneResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByMeterResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetServLocIDByMeterResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetTransformerBankByBankIDFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetTransformerBankByBankIDFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetTransformerBankByBankIDFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetTransformerBankByBankIDFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetTransformerBankByUnitIDFromCB");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetTransformerBankByUnitIDFromCB.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetTransformerBankByUnitIDFromCBResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetTransformerBankByUnitIDFromCBResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByMeterNo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByMeterNo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByMeterNoResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByMeterNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByMonth");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByMonth.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByMonthResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByMonthResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetUsageByServLocResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetWaterMeterByAccountNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetWaterMeterByAccountNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetWaterMeterByAccountNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetWaterMeterByAccountNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyAgainst");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GuyGuyAgainst.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GuyGuyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GuyStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">HistoryLogChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryLogChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">HistoryLogChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryLogChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayExchangeNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayExchangeNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayExchangeNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayExchangeNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceExchangeNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceExchangeNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceExchangeNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceExchangeNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LoadProfileChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadProfileChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LoadProfileChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadProfileChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterBaseExchangeNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseExchangeNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterBaseExchangeNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseExchangeNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterBaseInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterBaseInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterConnectivityNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterConnectivityNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterConnectivityNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterConnectivityNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterEventNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterEventNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterEventNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterEventNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterExchangeNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterExchangeNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterExchangeNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterExchangeNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterInstalledNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterInstalledNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterInstalledNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterInstalledNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterTestTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterTestTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterTestTransactionResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterTestTransactionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForCustomerResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForCustomerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForMeterResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForMeterResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForPole");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForPole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForPoleResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForPoleResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForSecurityLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForSecurityLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForSecurityLightResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForSecurityLightResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForServiceLocationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForServiceLocationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForStreetLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForStreetLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForStreetLightResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForStreetLightResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForTransformerBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForTransformerBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForTransformerBankResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForTransformerBankResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForUsage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForUsage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForUsageResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ModifyCBDataForUsageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ohSecondaryLine>secondaryType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OhSecondaryLineSecondaryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">phoneNumber>phoneType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhoneNumberPhoneType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PingURL");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PingURL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PingURLResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PingURLResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PoleChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">PoleChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ProcessPaymentTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProcessPaymentTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ProcessPaymentTransactionResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProcessPaymentTransactionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingScheduleResult>result");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingScheduleResultResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingScheduleResultNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingScheduleResultNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReadingScheduleResultNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingScheduleResultNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingValue>readingValueType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RegisterForService");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RegisterForService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RegisterForServiceResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RegisterForServiceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RequestPaymentExtension");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RequestPaymentExtension.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RequestPaymentExtensionResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RequestPaymentExtensionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RequestRegistrationID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RequestRegistrationID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RequestRegistrationIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RequestRegistrationIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReturnGeneratedNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReturnGeneratedNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ReturnGeneratedNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReturnGeneratedNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ServiceLocationNetworkChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocationNetworkChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ServiceLocationNetworkChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocationNetworkChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">summaryItem>itemAmount");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SummaryItemItemAmount.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">summaryItem>itemQuantity");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SummaryItemItemQuantity.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">TransformerBankChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransformerBankChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">TransformerBankChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransformerBankChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ugSecondaryLine>uGSecType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">UnregisterForService");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UnregisterForService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">UnregisterForServiceResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UnregisterForServiceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>driveType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterNameplateDriveType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>fluidType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterNameplateFluidType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>installType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterNameplateInstallType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>pipeSize");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterNameplatePipeSize.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">WriteAccountHistoryComments");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WriteAccountHistoryComments.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">WriteAccountHistoryCommentsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WriteAccountHistoryCommentsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "absoluteSchedule");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TimePoint[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePoint");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePoint");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AbstractGeometryType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AbstractGeometryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Accountability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accumulatedValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AccumulatedValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "achPayment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AchPayment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "action");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Action.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ActionTaken.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Address.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AdjustmentItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AdjustmentItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AffectedMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AffectedMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AllocatedLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allowableTransactionTypes");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionType");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "analogCondition");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AnalogCondition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Anchor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchorList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Anchor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationPointList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ObjectRef[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationPoint");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfAllocatedLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AllocatedLoad[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBackSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BackSpan[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingAccountLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingAccountLoad[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingData");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBillingDetail");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingDetail[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCapacitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Capacitor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDCustomer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDDeviceExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceExchange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDState");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDState[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDStateChange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateChange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Channel[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channel");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channel");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannelBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChannelBlock[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelBlock");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "block");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChargeableDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeableDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConductor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Conductor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConnectDisconnectEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCPR");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CPR[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Customer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainMember[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainNameChange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainNameChange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameChange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameChange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfElectricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfElectricServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployee");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Employee[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployeeTimeRecord");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EmployeeTimeRecord[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEquipment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Equipment[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ErrorObject[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Event[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "event");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "event");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFeederObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FeederObject[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGasMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGasServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericAnnotationFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericLineFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericLineFeature[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericPointFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericPointFeature[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGraphicSymbol");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GraphicSymbol[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryComment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryComment[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyComment");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyComment");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryLog[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfInHomeDisplay");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplay[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfInHomeDisplayExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJobWorked");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.JobWorked[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJointUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.JointUse[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUse");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUse");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLaborComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LaborComponent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLMDeviceExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceExchange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMaterialComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MaterialComponent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeasurementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeasurementType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Meter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterBase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBase[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterBaseExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseExchange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBaseExchange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBaseExchange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterConnectivity[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterExchange[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterHistoryEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterHistoryEvent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterHistoryEvent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterHistoryEvent");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRead[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterReading[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageCustomer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOvercurrentDeviceBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPaymentTransactionList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PaymentTransaction[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPole");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Pole[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPpmBalanceAdjustment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProfileObject[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProfileType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadProfile");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPropaneMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfPropaneServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfReadingScheduleResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingScheduleResult[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleResult");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleResult");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfRegulator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Regulator[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaPoint[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSecurityLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SecurityLight[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSource");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Source[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Station[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "station");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "station");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStreetLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StreetLight[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString1");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sealNumber");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString2");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Transformer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformerBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransformerBank[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTruck");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Truck[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Usage[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsageInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageInstance[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfVoltageAlarmItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.VoltageAlarmItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWarehouseLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WarehouseLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWaterMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWaterServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asFound");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AsFound.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asLeft");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AsLeft.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Assembly.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assemblyList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Assembly[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Assessment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Assessment[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AssessmentLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "averageConsumption");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConsumptionItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consumptionItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consumptionItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLAddress");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AVLAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.AVLEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BackgroundGraphics.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BackSpan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "base64Image");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Base64Image.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BaseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billedUsage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BilledUsage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingAccountLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BillingDetail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "blinkAlarm");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BlinkAlarm.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "BoxType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.BoxType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "breaker");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Breaker.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ObjectRef[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "calculatedCurrentBillReadings");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRead[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CallBackList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CallBackStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CallBackType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CallType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Capacitor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CapacitorBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cashPayment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CashPayment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDeviceExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDState.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateChange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Channel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChannelBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeableDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeableDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ChargeItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "checkInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CheckInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CircuitElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Clearance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearanceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Clearance[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coincidentalValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CoincidentalValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ComplexNum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Conductor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "config");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Config.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConfigurationGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroupList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroupID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConfiguredReadingType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingTypes");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConfiguredReadingType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingType");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConnectDisconnectList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConnectionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constGrade");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConstGrade.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consumptionItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConsumptionItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ContactInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ControlEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ControlItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlledItems");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ControlItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConvenienceFeeItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordinatesType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CoordinatesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CoordType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CPR.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CreditCardInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardPayment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CreditCardPayment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crew");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Crew.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewAction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CrewAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewActionEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CrewActionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemand");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CumDemand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemandType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CumDemandType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Customer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CustomerCall.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedByOutage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAttachedToDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CustomersAttachedToDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cut");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Cut.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayFormat");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DisplayFormat.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainMember.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameAction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainNameAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameChange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DomainNameChange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "durationDescription");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.DurationDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EaLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eCheckPayment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ECheckPayment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings5() {
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elbow");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Elbow.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricNameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricNameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementsVoltage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElementsVoltage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EMailAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EMailAddress[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Employee.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EmployeeTimeRecord.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDeviceShipment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EndDeviceShipment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Equipment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ErrorObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "event");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Event.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EventCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EventInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventInstances");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EventInstance[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventInstance");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventInstance");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Extensions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtensionsItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtensionsItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fakeNodeSection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FakeNodeSection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FeederObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedCharge");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FixedCharge.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCodeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FixedCharge[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedCharge");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCode");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "flowDemand");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FlowDemand.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "flowDemandType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FlowDemandType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FormattedBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.FormattedBlockTemplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "frequency");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Frequency.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Fuse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasNameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasNameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasUtilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasUtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "generator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Generator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericAnnotationFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericLineFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericPointFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPolygonFeature");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GenericPolygonFeature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "geometry");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Geometry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMLLines");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LineStringType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LineStringType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexLine");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMLPolygons");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PolygonType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PolygonType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "Polygon");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GPS.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GpsLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GpsPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GraphicSymbol.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "guy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Guy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyComment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryComment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "imageType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ImageType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "informationList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "informationItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplay.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayBillingMessage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayMessage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "innerBoundaryIs");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InnerBoundaryIs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instrumentTransformers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InstrumentTransformers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interval");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Interval.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.JobWorked.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.JointUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jumper");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Jumper.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategory");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LaborCategory.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LaborComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LdCon.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LengthUnit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LinearRingType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LinearRingType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LineStringType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LineStringType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkedTransformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LinkedTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistr");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadDistr.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistrict");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadDistrict.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadFlowResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadFlowResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadInterruptibleType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadInterruptibleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDeviceList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDeviceID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadSection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadSection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationReferences");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ObjectRef[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationReference");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedODEventList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MaterialComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MaterialItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialManagementAssembly");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MaterialManagementAssembly.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MaterialUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Measurement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeasurementDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeasurementDeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeasurementDeviceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeasurementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Message.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MessageHeaderCSUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MessageHeaderCSUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "messageList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Message[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }
    private void addBindings6() {
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Meter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBaseExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterConnectivity.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterEventList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterEventList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroups.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterHistoryEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterHistoryEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRead.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterReading.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRef.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRefList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRef[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Meters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterStatusList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterStatus");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterTest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTestList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterTest[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "methodsList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "methodName");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mixedMeterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroups[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Module.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "moduleList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Module[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "motor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Motor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Mounting.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MsgLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLineList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MsgLine[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLine");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLine");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspAlarm");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspAlarm.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspAssetHistoryEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspAssetHistoryEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspBankObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspBankObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspConnectivityLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspConnectivityPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDeviceExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspDeviceExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspElectricLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspElectricPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLineObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspLineObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLoadGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspLoadGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspMotorGenerator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspMotorGenerator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspNetwork");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspNetwork.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspOverCurrentDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspOverCurrentDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspPhase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPointObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspPointObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPolygonObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspPolygonObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspResultsBase");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspResultsBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspSwitchDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspSwitchingBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MspSwitchingDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MultiSpeakMsgHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Nameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "network");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Network.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.NodeIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfElement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.NumberOfElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.NumberType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ObjectRef.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohPrimaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OhPrimaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohSecondaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OhSecondaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageCustomer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDetectDeviceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDetectDeviceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDetectionDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDetectionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDetectionLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDurationEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageDurationEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageEventStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReason.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonCodeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReasonCodeList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonContainer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReasonContainer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReasonItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReasonItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReportingCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReportingCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReportingCodeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageReportingCode[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReportingCode");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReportingCode");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outerBoundaryIs");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OuterBoundaryIs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parcel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Parcel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentMeterList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentMeterID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PayableItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PayableItemList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PaymentExtension.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtensionList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PaymentExtension[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PaymentTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransactionList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PaymentTransaction[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TestInstance[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "periodicSchedule");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PeriodicSchedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseAssociation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhaseAssociation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhaseCd.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhoneNumber[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhoneNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "physicalObjectList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhysicalObjectList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PickList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickListItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PickListItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings7() {
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pmEventCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PmEventCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Pole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleClass.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PoleUseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PolygonType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PolygonType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "position");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Position.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimitationUnits");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PowerLimitationUnits.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerMonitor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PowerMonitor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PowerStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerSystemDevice");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PowerSystemDevice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PpmLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmMeterExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PpmMeterExchange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmTransaction");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PpmTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premise");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Premise.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PremiseService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseServiceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PremiseService[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabinet");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PrimaryCabinet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PriorityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProfileObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ProfileType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneNameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneNameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneUtilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneUtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.QualityDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantityType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.QuantityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RCDState");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RCDState.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingSchedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingScheduleResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingTypeCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingTypeID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValues");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValue[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReasonCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedCDDevices");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReceivedElectricMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedLoadManagementDevices");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementlDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedModules");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Module[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recloser");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Recloser.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registers");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Registers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RegistrationInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Regulator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RegulatorBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.RequestedNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ResolvedLevel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ResponseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultsType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ResultsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riser");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Riser.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaAnalog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaPoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaPoints.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaPointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "schedule");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Schedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryJunctionBox");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SecondaryJunctionBox.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionalizer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Sectionalizer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SecurityLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shortCircuitAnalysisResult");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Source.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanGuy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SpanGuy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanTyp");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SpanTyp.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spatialFeatureGroup");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SpatialFeatureGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "staStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StaStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "station");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Station.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statisticalSummary");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SummaryItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summaryItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summaryItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusIdentifier");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StatusIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "strategy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Strategy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StreetLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StreetLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "subMeterList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "subMeterID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Substation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summaryItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SummaryItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Summation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supplyVoltage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SupplyVoltage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwitchDeviceBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingOrder");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwitchingOrder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingSchedule");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwitchingSchedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwitchingStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStepList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwitchingStep[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

    }
    private void addBindings8() {
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SyntaxItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "telemetry");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Telemetry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tender");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Tender.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testedElectricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TestedElectricMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TestInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TimePeriod.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePoint");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TimePoint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timesheet");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Timesheet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeSpan");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TimeSpan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeZone");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TimeZone.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TOUReading.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReadings");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TOUReading[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "trafficLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TrafficLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Transformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransformerBank.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerRatio");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransformerRatio.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderIDRange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TransponderIDRange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "treatment");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Treatment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Truck.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugPrimaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UgPrimaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLine");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UgSecondaryLine.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UnitActn.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UnitPrefix.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UnType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Uom.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Usage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItemList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriod");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageOtherPeriod.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriodList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UsageOtherPeriod[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriod");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriod");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valSyntax");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SyntaxItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valueList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "val");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vehicle");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Vehicle.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarm");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.VoltageAlarm.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.VoltageAlarmItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WarehouseLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeterGroups");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterNameplate");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterNameplate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterServiceLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterUtilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterUtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WdgCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WorkLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrder");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WorkOrder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrderSelection");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WorkOrderSelection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workTicket");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WorkTicket.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zUnit");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ZUnit.class;
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

    public com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumber(com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensions, java.lang.String numberType) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetNextNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetNextNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {extensions, numberType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.RequestedNumber) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.RequestedNumber) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.RequestedNumber.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByEALocation(java.lang.String eaLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersByEALocation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByEALocation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {eaLoc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByFacilityID(java.lang.String facilityID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersByFacilityID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByFacilityID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {facilityID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersBySiteID(java.lang.String siteID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersBySiteID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersBySiteID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {siteID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByCustomerName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersByCustomerName");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByCustomerName"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {firstName, lastName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByHomePhone(java.lang.String homeAC, java.lang.String homePhone) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersByHomePhone");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersByHomePhone"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {homeAC, homePhone});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMetersBySearchString(java.lang.String searchString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMetersBySearchString");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMetersBySearchString"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getConfigurationGroupNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetConfigurationGroupNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getConfigurationGroupNamesByMeterNo(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetConfigurationGroupNamesByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupNamesByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo, serviceType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.MeterGroups getConfigurationGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetConfigurationGroupMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterGroupName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.MeterGroups) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.MeterGroups) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.MeterGroups.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.StreetLight[] getAllStreetLights(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllStreetLights");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllStreetLights"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.StreetLight[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.StreetLight[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.StreetLight[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.SecurityLight[] getAllSecurityLights(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllSecurityLights");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllSecurityLights"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.SecurityLight[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.SecurityLight[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.SecurityLight[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getIHDGroupNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetIHDGroupNames");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getIHDGroupNamesByInHomeDisplayID(java.lang.String inHomeDisplayID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetIHDGroupNamesByInHomeDisplayID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupNamesByInHomeDisplayID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inHomeDisplayID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.InHomeDisplayGroup getIHDGroupMembers(java.lang.String IHDGroupName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetIHDGroupMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {IHDGroupName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.InHomeDisplayGroup) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.InHomeDisplayGroup) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.InHomeDisplayGroup.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.MeterBase getMeterBaseByObjectId(java.lang.String meterBaseID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterBaseByObjectId");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterBaseByObjectId"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterBaseID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.MeterBase) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.MeterBase) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.MeterBase.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] getAllConnectDisconnectEventsByReasonCode(java.lang.String reasonCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllConnectDisconnectEventsByReasonCode");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllConnectDisconnectEventsByReasonCode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {reasonCode, startDate, endDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForCustomer(com.cannontech.multispeak.deploy.service.Customer[] customerData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
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
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForServiceLocation(com.cannontech.multispeak.deploy.service.ServiceLocation[] serviceLocationData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
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
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForMeter(com.cannontech.multispeak.deploy.service.Meter[] meterData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
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
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForPole(com.cannontech.multispeak.deploy.service.Pole[] poleData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForPole");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForPole"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {poleData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForTransformerBank(com.cannontech.multispeak.deploy.service.TransformerBank[] xfmrBankData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForTransformerBank");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForTransformerBank"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {xfmrBankData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForUsage(com.cannontech.multispeak.deploy.service.Usage[] usageData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForUsage");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForUsage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {usageData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForStreetLight(com.cannontech.multispeak.deploy.service.StreetLight[] streetLightData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForStreetLight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForStreetLight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {streetLightData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForSecurityLight(com.cannontech.multispeak.deploy.service.SecurityLight[] securityLightData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ModifyCBDataForSecurityLight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForSecurityLight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {securityLightData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingChangedNotification(com.cannontech.multispeak.deploy.service.MeterRead[] changedMeterReads, java.lang.String transactionID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ReadingChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedMeterReads, transactionID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] historyLogChangedNotification(com.cannontech.multispeak.deploy.service.HistoryLog[] changedHistoryLogs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
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
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] formattedBlockNotification(com.cannontech.multispeak.deploy.service.FormattedBlock changedMeterReads, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/FormattedBlockNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "FormattedBlockNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedMeterReads, transactionID, errorString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterInstalledNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
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
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterExchangeNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterExchangeNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterChangeout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.LMDeviceExchange[] LMDChangeout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceExchangeNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceExchangeNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {LMDChangeout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] installedLMDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceInstalledNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceInstalledNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {installedLMDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.LoadActionCode stateChange, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDStateChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo, stateChange, transactionID, errorString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesChangedNotification(com.cannontech.multispeak.deploy.service.CDStateChange[] stateChanges, java.lang.String transactionID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDStatesChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStatesChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {stateChanges, transactionID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PaymentTransaction[] processPaymentTransaction(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ProcessPaymentTransaction");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ProcessPaymentTransaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PaymentTransaction[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PaymentTransaction[] commitPaymentTransaction(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CommitPaymentTransaction");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CommitPaymentTransaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PaymentTransaction[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PaymentExtension[] requestPaymentExtension(com.cannontech.multispeak.deploy.service.PaymentExtension[] extensionList) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/RequestPaymentExtension");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RequestPaymentExtension"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {extensionList});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PaymentExtension[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PaymentExtension[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PaymentExtension[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] extendPayment(com.cannontech.multispeak.deploy.service.PaymentExtension[] extensionList) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ExtendPayment");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ExtendPayment"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {extensionList});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] loadProfileChangedNotification(com.cannontech.multispeak.deploy.service.ProfileObject[] changedLoadProfiles) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LoadProfileChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LoadProfileChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedLoadProfiles});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] poleChangedNotification(com.cannontech.multispeak.deploy.service.Pole[] changedPoles) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/PoleChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PoleChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedPoles});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationNetworkChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ServiceLocationNetworkChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationNetworkChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedServiceLocations});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] transformerBankChangedNotification(com.cannontech.multispeak.deploy.service.TransformerBank[] changedXfmrBanks) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/TransformerBankChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TransformerBankChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedXfmrBanks});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] endDeviceShipmentNotification(com.cannontech.multispeak.deploy.service.EndDeviceShipment shipment) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/EndDeviceShipmentNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EndDeviceShipmentNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {shipment});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterTestTransaction(com.cannontech.multispeak.deploy.service.MeterTest[] test) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterTestTransaction");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterTestTransaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {test});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayInstalledNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/InHomeDisplayInstalledNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InHomeDisplayInstalledNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addedIHDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/InHomeDisplayExchangeNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InHomeDisplayExchangeNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {IHDChangeout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.CDDeviceExchange[] CDDChangeout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDDeviceExchangeNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceExchangeNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {CDDChangeout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.CDDevice[] installedCDDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDDeviceInstalledNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceInstalledNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {installedCDDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject returnGeneratedNumber(com.cannontech.multispeak.deploy.service.RequestedNumber requestedNum) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ReturnGeneratedNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReturnGeneratedNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {requestedNum});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] writeAccountHistoryComments(com.cannontech.multispeak.deploy.service.HistoryComment[] comments) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/WriteAccountHistoryComments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "WriteAccountHistoryComments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {comments});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void CDStateNotification(com.cannontech.multispeak.deploy.service.CDState state, java.lang.String transactionID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDStateNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {state, transactionID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesNotification(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String transactionID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CDStatesNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStatesNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {states, transactionID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingScheduleResultNotification(com.cannontech.multispeak.deploy.service.ReadingScheduleResult[] scheduleResult, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ReadingScheduleResultNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ReadingScheduleResultNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scheduleResult, transactionID, errorString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterEventNotification(com.cannontech.multispeak.deploy.service.MeterEventList events) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterEventNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterEventNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {events});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterConnectivityNotification(com.cannontech.multispeak.deploy.service.MeterConnectivity[] newConnectivity) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterConnectivityNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterConnectivityNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {newConnectivity});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseExchangeNotification(com.cannontech.multispeak.deploy.service.MeterBaseExchange[] MBChangeout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterBaseExchangeNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterBaseExchangeNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MBChangeout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseInstalledNotification(com.cannontech.multispeak.deploy.service.MeterBase[] addedMBs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/MeterBaseInstalledNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterBaseInstalledNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addedMBs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
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
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getMethods() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[58]);
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
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[59]);
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
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[60]);
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
                return (com.cannontech.multispeak.deploy.service.DomainMember[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.DomainMember[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.DomainMember[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String requestRegistrationID() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[61]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/RequestRegistrationID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RequestRegistrationID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] registerForService(com.cannontech.multispeak.deploy.service.RegistrationInfo registrationDetails) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[62]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/RegisterForService");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RegisterForService"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {registrationDetails});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] unregisterForService(java.lang.String registrationID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[63]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/UnregisterForService");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "UnregisterForService"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {registrationID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.RegistrationInfo getRegistrationInfoByID(java.lang.String registrationID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[64]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetRegistrationInfoByID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetRegistrationInfoByID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {registrationID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.RegistrationInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.RegistrationInfo) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.RegistrationInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getPublishMethods() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[65]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPublishMethods");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPublishMethods"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainMembersChangedNotification(com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[66]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/DomainMembersChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DomainMembersChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedDomainMembers});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainNamesChangedNotification(com.cannontech.multispeak.deploy.service.DomainNameChange[] changedDomainNames) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[67]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/DomainNamesChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DomainNamesChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedDomainNames});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ErrorObject[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ErrorObject[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[] getAllOCDB(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[68]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllOCDB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllOCDB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer[] getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[69]);
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
                return (com.cannontech.multispeak.deploy.service.Customer[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer[] getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[70]);
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
                return (com.cannontech.multispeak.deploy.service.Customer[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[71]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[72]);
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
                return (com.cannontech.multispeak.deploy.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[73]);
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
                return (com.cannontech.multispeak.deploy.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer[] getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[74]);
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
                return (com.cannontech.multispeak.deploy.service.Customer[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[75]);
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
                return (com.cannontech.multispeak.deploy.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[76]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[77]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[78]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[79]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[80]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[81]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[82]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[83]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[84]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[85]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[86]);
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
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[87]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[88]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[89]);
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
                return (com.cannontech.multispeak.deploy.service.Meter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[90]);
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
                return (com.cannontech.multispeak.deploy.service.Meter) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[91]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[92]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[93]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[94]);
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
                return (com.cannontech.multispeak.deploy.service.Meter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getMeterGroupNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[95]);
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
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getMeterGroupNamesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[96]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterGroupNamesByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterGroupNamesByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getMeterGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[97]);
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
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PpmLocation getPPMCustomer(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[98]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPPMCustomer");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMCustomer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PpmLocation) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PpmLocation) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PpmLocation.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ChargeableDevice[] getChargeableDevicesByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[99]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetChargeableDevicesByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetChargeableDevicesByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ChargeableDevice[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ChargeableDevice[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ChargeableDevice[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PaymentTransaction[][] getPPMPayments(java.util.Calendar startTime, java.util.Calendar stopTime) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[100]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPPMPayments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMPayments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {startTime, stopTime});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[][]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PaymentTransaction[][]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PaymentTransaction[][].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[] getPPMBalanceAdjustments(java.util.Calendar startTime, java.util.Calendar stopTime) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[101]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPPMBalanceAdjustments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPPMBalanceAdjustments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {startTime, stopTime});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.BillingData[] getBillingData(java.util.Calendar startBillDate, java.util.Calendar endBillDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[102]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetBillingData");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {startBillDate, endBillDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.BillingData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.BillingData[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.BillingData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.BillingDetail[] getBillingDetail(java.lang.String accountNumber, java.util.Calendar cisBillDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[103]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetBillingDetail");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingDetail"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, cisBillDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.BillingDetail[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.BillingDetail[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.BillingDetail[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.BilledUsage getBilledUsage(java.lang.String accountNumber, java.util.Calendar cisBillDate) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[104]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetBilledUsage");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBilledUsage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, cisBillDate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.BilledUsage) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.BilledUsage) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.BilledUsage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.MeterHistoryEvent[] getMeterHistoryByMeterID(java.lang.String meterID, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[105]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterHistoryByMeterID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterHistoryByMeterID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterID, serviceType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.MeterHistoryEvent[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.MeterHistoryEvent[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.MeterHistoryEvent[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PayableItemList getPayableItemsList(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[106]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPayableItemsList");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPayableItemsList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PayableItemList) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PayableItemList) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PayableItemList.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getConvenienceFees(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[107]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetConvenienceFees");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConvenienceFees"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.BillingAccountLoad[] getBillingAccountLoadDataByMonth(int month, int year, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[108]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetBillingAccountLoadDataByMonth");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingAccountLoadDataByMonth"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(month), new java.lang.Integer(year), lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.BillingAccountLoad[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.BillingAccountLoad[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.BillingAccountLoad[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getServLocIDByHomePhone(java.lang.String homeAc, java.lang.String homePhone) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[109]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServLocIDByHomePhone");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByHomePhone"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {homeAc, homePhone});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String getServLocIDByMeter(java.lang.String meterNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[110]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServLocIDByMeter");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByMeter"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String getServLocIDByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[111]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetServLocIDByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer getCustomerByHomePhone(java.lang.String homePhone, java.lang.String homeAc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[112]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByHomePhone");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByHomePhone"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {homePhone, homeAc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Pole[] getModifiedPolesFromCB(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[113]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetModifiedPolesFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedPolesFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {previousSessionID, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Pole[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Pole[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Pole[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Pole[] getAllPolesFromCB(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[114]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllPolesFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPolesFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Pole[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Pole[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Pole[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Pole getPoleByPoleNumberFromCB(java.lang.String poleNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[115]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPoleByPoleNumberFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPoleByPoleNumberFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {poleNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Pole) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Pole) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Pole.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.TransformerBank[] getAllTransformerBanksFromCB(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[116]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllTransformerBanksFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllTransformerBanksFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.TransformerBank[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.TransformerBank[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.TransformerBank[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.TransformerBank[] getModifiedTransformerBanksFromCB(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[117]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetModifiedTransformerBanksFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedTransformerBanksFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {previousSessionID, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.TransformerBank[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.TransformerBank[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.TransformerBank[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.TransformerBank getTransformerBankByBankIDFromCB(java.lang.String bankID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[118]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetTransformerBankByBankIDFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetTransformerBankByBankIDFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bankID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.TransformerBank) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.TransformerBank) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.TransformerBank.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.TransformerBank getTransformerBankByUnitIDFromCB(java.lang.String unitID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[119]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetTransformerBankByUnitIDFromCB");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetTransformerBankByUnitIDFromCB"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {unitID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.TransformerBank) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.TransformerBank) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.TransformerBank.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByMonth(int monthNumber, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[120]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetUsageByMonth");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByMonth"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(monthNumber), lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Usage[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Usage[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Usage[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[121]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetUsageByServLoc");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByServLoc"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servLoc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Usage[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Usage[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Usage[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[122]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetUsageByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Usage[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Usage[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Usage[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Usage getUsageByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[123]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetUsageByMeterNo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByMeterNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Usage) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Usage) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Usage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getAllMetersByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[124]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllMetersByServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllMetersByServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ServiceLocations getAllServiceLocationsByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[125]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllServiceLocationsByServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllServiceLocationsByServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ServiceLocations) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ServiceLocations) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ServiceLocations.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getMeterByAccountNumberAndServiceType(java.lang.String accountNumber, java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[126]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetMeterByAccountNumberAndServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAccountNumberAndServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, serviceType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ElectricMeter[] getAllElectricMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[127]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllElectricMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllElectricMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ElectricMeter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ElectricMeter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ElectricMeter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.GasMeter[] getAllGasMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[128]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllGasMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllGasMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.GasMeter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.GasMeter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.GasMeter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.WaterMeter[] getAllWaterMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[129]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllWaterMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllWaterMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.WaterMeter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.WaterMeter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.WaterMeter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PropaneMeter[] getAllPropaneMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[130]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllPropaneMeters");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPropaneMeters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PropaneMeter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PropaneMeter[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PropaneMeter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] getAllElectricServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[131]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllElectricServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllElectricServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.ElectricServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.ElectricServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.ElectricServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[132]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllGasServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllGasServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.GasServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.GasServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.GasServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.WaterServiceLocation[] getAllWaterServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[133]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllWaterServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllWaterServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.WaterServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.WaterServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.WaterServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] getAllPropaneServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[134]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllPropaneServiceLocations");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllPropaneServiceLocations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.PropaneServiceLocation[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.PropaneServiceLocation[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.PropaneServiceLocation[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getElectricMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[135]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetElectricMeterByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetElectricMeterByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getGasMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[136]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetGasMeterByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetGasMeterByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getWaterMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[137]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetWaterMeterByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetWaterMeterByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Meters getPropaneMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[138]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetPropaneMeterByAccountNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPropaneMeterByAccountNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {accountNumber, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Meters) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Meters) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Meters.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer[] getAllCustomersByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[139]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllCustomersByServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCustomersByServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceType, lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Customer[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceType(java.lang.String meterNo, java.lang.String serviceType) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[140]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetCustomerByMeterNumberAndServiceType");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNumberAndServiceType"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo, serviceType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.Customer) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.Customer) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.Customer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
