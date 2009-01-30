/**
 * LM_ServerSoap_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LM_ServerSoap_BindingStub extends org.apache.axis.client.Stub implements com.cannontech.multispeak.deploy.service.LM_ServerSoap_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[30];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PingURL");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURLResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetMethods");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethodsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDomainNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

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
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllLoadManagementDevices");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllLoadManagementDevicesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetLoadManagementDeviceByMeterNumber");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLoadManagementDeviceByMeterNumberResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetLoadManagementDeviceByServLoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLoadManagementDeviceByServLocResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IsLoadManagementActive");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IsLoadManagementActiveResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAmountOfControllableLoad");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        oper.setReturnClass(float.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAmountOfControllableLoadResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAmountOfControlledLoad");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        oper.setReturnClass(float.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAmountOfControlledLoadResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetAllSubstationLoadControlStatuses");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSubstationLoadControlStatus"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllSubstationLoadControlStatusesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InitiateLoadManagementEvent");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "theLMEvent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"), com.cannontech.multispeak.deploy.service.LoadManagementEvent.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateLoadManagementEventResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InitiateLoadManagementEvents");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "theLMEvents"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementEvent"), com.cannontech.multispeak.deploy.service.LoadManagementEvent[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateLoadManagementEventsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InitiatePowerFactorManagementEvent");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thePFMEvent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent"), com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiatePowerFactorManagementEventResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CustomerChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedCustomers"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"), com.cannontech.multispeak.deploy.service.Customer[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomerChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ServiceLocationChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedServiceLocations"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"), com.cannontech.multispeak.deploy.service.ServiceLocation[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LMDeviceAddNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedLMDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"), com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceAddNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LMDeviceChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedLMDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"), com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

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
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("LMDeviceRemoveNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "removedLMDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"), com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceRemoveNotificationResult"));
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
        oper.setName("LMDeviceRetireNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "retiredLMDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementDevice"), com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceRetireNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAAnalogChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaAnalog"), com.cannontech.multispeak.deploy.service.ScadaAnalog[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAStatusChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatuses"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaStatus"), com.cannontech.multispeak.deploy.service.ScadaStatus[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAStatusChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAPointChangedNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint1"), com.cannontech.multispeak.deploy.service.ScadaPoint[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotificationResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAPointChangedNotificationForAnalog");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint1"), com.cannontech.multispeak.deploy.service.ScadaPoint[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotificationForAnalogResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAPointChangedNotificationForStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint1"), com.cannontech.multispeak.deploy.service.ScadaPoint[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotificationForStatusResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAAnalogChangedNotificationByPointID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"), com.cannontech.multispeak.deploy.service.ScadaAnalog.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAAnalogChangedNotificationForPower");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaAnalog"), com.cannontech.multispeak.deploy.service.ScadaAnalog[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationForPowerResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAAnalogChangedNotificationForVoltage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaAnalog"), com.cannontech.multispeak.deploy.service.ScadaAnalog[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        oper.setReturnClass(com.cannontech.multispeak.deploy.service.ErrorObject[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationForVoltageResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SCADAStatusChangedNotificationByPointID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"), com.cannontech.multispeak.deploy.service.ScadaStatus.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[29] = oper;

    }

    public LM_ServerSoap_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public LM_ServerSoap_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public LM_ServerSoap_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>loadManagementEvent>strategy>applicationPointList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ObjectRef[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationPoint");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>meterBase>deviceList>loadManagementlDeviceList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDeviceID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>substationLoadControlStatus>controlledItems>controlItem");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledItemsControlItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ArrayOfOutageCustomerListOutageCustomer>outageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ArrayOfOutageCustomerListOutageCustomerOutageCustomer.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>eMailList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EMailAddress[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>phoneList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PhoneNumber[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CustomerChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CustomerChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CustomerChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CustomerChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">eMailAddress>eMailType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EMailAddressEMailType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>receivedCDDevices");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>receivedLoadManagementDevices");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementlDevice");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>receivedMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>receivedModules");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Module[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>transponderIDRange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.EndDeviceShipmentTransponderIDRange.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">extensionsItem>extType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ExtensionsItemExtType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllLoadManagementDevices");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllLoadManagementDevices.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllLoadManagementDevicesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllLoadManagementDevicesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllSubstationLoadControlStatuses");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllSubstationLoadControlStatuses.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllSubstationLoadControlStatusesResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAllSubstationLoadControlStatusesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAmountOfControllableLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAmountOfControllableLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAmountOfControllableLoadResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAmountOfControllableLoadResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAmountOfControlledLoad");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAmountOfControlledLoad.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAmountOfControlledLoadResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetAmountOfControlledLoadResponse.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLoadManagementDeviceByMeterNumber");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetLoadManagementDeviceByMeterNumber.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLoadManagementDeviceByMeterNumberResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetLoadManagementDeviceByMeterNumberResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLoadManagementDeviceByServLoc");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetLoadManagementDeviceByServLoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLoadManagementDeviceByServLocResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GetLoadManagementDeviceByServLocResponse.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateLoadManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiateLoadManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateLoadManagementEventResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiateLoadManagementEventResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateLoadManagementEvents");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiateLoadManagementEvents.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateLoadManagementEventsResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiateLoadManagementEventsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiatePowerFactorManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiatePowerFactorManagementEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiatePowerFactorManagementEventResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InitiatePowerFactorManagementEventResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">IsLoadManagementActive");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.IsLoadManagementActive.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">IsLoadManagementActiveResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.IsLoadManagementActiveResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">linkedTransformer>unitList");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceAddNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceAddNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceAddNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceAddNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceChangedNotificationResponse.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceRemoveNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceRemoveNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceRemoveNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceRemoveNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceRetireNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceRetireNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceRetireNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LMDeviceRetireNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">loadManagementEvent>strategy");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementEventStrategy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterBase>deviceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterBaseDeviceList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterRead>readingValues");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValue[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterRead>TOUReadings");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TOUReading[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meters>electricMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meters>gasMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meters>propaneMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meters>waterMeters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterMeter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ohSecondaryLine>secondaryType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OhSecondaryLineSecondaryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">outageCustomer>timeToCall");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.OutageCustomerTimeToCall.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">payableItemList>allowableTransactionTypes");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionType");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">premise>premiseServiceList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PremiseService[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingValue>readingValueType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationByPointID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationByPointID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationByPointIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationByPointIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationForPower");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationForPower.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationForPowerResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationForPowerResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationForVoltage");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationForVoltage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationForVoltageResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationForVoltageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAAnalogChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAAnalogChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotificationForAnalog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotificationForAnalog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotificationForAnalogResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotificationForAnalogResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotificationForStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotificationForStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotificationForStatusResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotificationForStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAPointChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAPointChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAStatusChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAStatusChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAStatusChangedNotificationByPointID");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAStatusChangedNotificationByPointID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAStatusChangedNotificationByPointIDResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAStatusChangedNotificationByPointIDResponse.class;
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
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAStatusChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SCADAStatusChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">serviceLocation>calculatedCurrentBillReadings");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterRead[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ServiceLocationChangedNotification");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocationChangedNotification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ServiceLocationChangedNotificationResponse");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ServiceLocationChangedNotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">serviceLocations>electricServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">serviceLocations>gasServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.GasServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">serviceLocations>propaneServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PropaneServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">serviceLocations>waterServiceLocations");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WaterServiceLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">substationLoadControlStatus>controlledItems");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledItemsControlItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>substationLoadControlStatus>controlledItems>controlItem");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ugSecondaryLine>uGSecType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConductor");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Conductor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLoadManagementEvent");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.LoadManagementEvent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterReading[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageCustomerListOutageCustomer");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ArrayOfOutageCustomerListOutageCustomerOutageCustomer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ArrayOfOutageCustomerListOutageCustomer>outageCustomer");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfRegulator");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Regulator[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaAnalog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaAnalog[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaPoint1");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaPoint[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfScadaStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ScadaStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string");
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
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewID");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSubstationLoadControlStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTruck");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Truck[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWarehouseLocation");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.WarehouseLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation");
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDStateChange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CDStateChange.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.CircuitElement.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ElectricMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.HistoryLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplay.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.InHomeDisplayExchange.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.MeterConnectivity.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Meters.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TestInstance[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            qName2 = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabinet");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.PrimaryCabinet.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReadingValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReasonCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.ReceivedElectricMeter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.StreetLight.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swStatus");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swType");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.SwType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.TOUReading.class;
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

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.UtilityInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vehicle");
            cachedSerQNames.add(qName);
            cls = com.cannontech.multispeak.deploy.service.Vehicle.class;
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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException {
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
                return (com.cannontech.multispeak.deploy.service.DomainMember[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.DomainMember[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.DomainMember[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getAllLoadManagementDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllLoadManagementDevices");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllLoadManagementDevices"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {lastReceived});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(java.lang.String meterNo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetLoadManagementDeviceByMeterNumber");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLoadManagementDeviceByMeterNumber"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {meterNo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetLoadManagementDeviceByServLoc");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLoadManagementDeviceByServLoc"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servLoc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.LoadManagementDevice[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.LoadManagementDevice[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public boolean isLoadManagementActive(java.lang.String servLoc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/IsLoadManagementActive");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IsLoadManagementActive"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {servLoc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public float getAmountOfControllableLoad() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAmountOfControllableLoad");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAmountOfControllableLoad"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Float) _resp).floatValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Float) org.apache.axis.utils.JavaUtils.convert(_resp, float.class)).floatValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public float getAmountOfControlledLoad() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAmountOfControlledLoad");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAmountOfControlledLoad"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Float) _resp).floatValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Float) org.apache.axis.utils.JavaUtils.convert(_resp, float.class)).floatValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/GetAllSubstationLoadControlStatuses");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllSubstationLoadControlStatuses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject initiateLoadManagementEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/InitiateLoadManagementEvent");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateLoadManagementEvent"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {theLMEvent});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateLoadManagementEvents(com.cannontech.multispeak.deploy.service.LoadManagementEvent[] theLMEvents) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/InitiateLoadManagementEvents");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateLoadManagementEvents"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {theLMEvents});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject initiatePowerFactorManagementEvent(com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent thePFMEvent) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/InitiatePowerFactorManagementEvent");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiatePowerFactorManagementEvent"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thePFMEvent});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/CustomerChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomerChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedCustomers});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/ServiceLocationChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ServiceLocationChangedNotification"));

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceAddNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] addedLMDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceAddNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceAddNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addedLMDs});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceChangedNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] changedLMDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {changedLMDs});

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
        _call.setOperation(_operations[18]);
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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] removedLMDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceRemoveNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceRemoveNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {removedLMDs});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRetireNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] retiredLMDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/LMDeviceRetireNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceRetireNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {retiredLMDs});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAAnalogChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaAnalogs});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAStatusChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAStatusChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaStatuses});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAPointChangedNotification");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaPoints});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAPointChangedNotificationForAnalog");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotificationForAnalog"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaPoints});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAPointChangedNotificationForStatus");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAPointChangedNotificationForStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaPoints});

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

    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAAnalogChangedNotificationByPointID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationByPointID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaAnalog});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAAnalogChangedNotificationForPower");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationForPower"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaAnalogs});

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

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAAnalogChangedNotificationForVoltage");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAAnalogChangedNotificationForVoltage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaAnalogs});

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

    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.multispeak.org/Version_3.0/SCADAStatusChangedNotificationByPointID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SCADAStatusChangedNotificationByPointID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scadaStatus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
