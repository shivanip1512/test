/**
 * MultiSpeak.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MultiSpeak  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.SwitchingStep switchingStep;

    private com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent powerFactorManagementEvent;

    private com.cannontech.multispeak.deploy.service.ProfileObject profileObject;

    private com.cannontech.multispeak.deploy.service.ReadingObject readingObject;

    private com.cannontech.multispeak.deploy.service.ReadingSchedule readingSchedule;

    private com.cannontech.multispeak.deploy.service.Regulator regulator;

    private com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog;

    private com.cannontech.multispeak.deploy.service.ScadaPoints scadaPoints;

    private com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus;

    private com.cannontech.multispeak.deploy.service.Schedule schedule;

    private com.cannontech.multispeak.deploy.service.Crew crew;

    private com.cannontech.multispeak.deploy.service.OutageLocation outageLocation;

    private com.cannontech.multispeak.deploy.service.SecurityLight securityLight;

    private com.cannontech.multispeak.deploy.service.OutageEventStatus outageEventStatus;

    private com.cannontech.multispeak.deploy.service.SpatialFeatureGroup spatialFeatureGroup;

    private com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus substationLoadControlStatus;

    private com.cannontech.multispeak.deploy.service.SwitchingOrder switchingOrder;

    private com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule;

    private com.cannontech.multispeak.deploy.service.Timesheet timesheet;

    private com.cannontech.multispeak.deploy.service.TrafficLight trafficLight;

    private com.cannontech.multispeak.deploy.service.Transformer transformer;

    private com.cannontech.multispeak.deploy.service.Truck truck;

    private com.cannontech.multispeak.deploy.service.Usage usage;

    private com.cannontech.multispeak.deploy.service.WorkOrder workOrder;

    private com.cannontech.multispeak.deploy.service.WorkOrderSelection workOrderSelection;

    private com.cannontech.multispeak.deploy.service.WorkTicket workTicket;

    private com.cannontech.multispeak.deploy.service.Equipment equipment;

    private com.cannontech.multispeak.deploy.service.FeederObject feederObject;

    private com.cannontech.multispeak.deploy.service.OutageEvent outageEvent;

    private com.cannontech.multispeak.deploy.service.Assessment assessment;

    private com.cannontech.multispeak.deploy.service.PowerMonitor powerMonitor;

    private com.cannontech.multispeak.deploy.service.AssessmentLocation assessmentLocation;

    private com.cannontech.multispeak.deploy.service.BackgroundGraphics backgroundGraphics;

    private com.cannontech.multispeak.deploy.service.BillingAccountLoad billingAccountLoad;

    private com.cannontech.multispeak.deploy.service.CallBackList callBackList;

    private com.cannontech.multispeak.deploy.service.Capacitor capacitor;

    private com.cannontech.multispeak.deploy.service.CircuitElement circuitElement;

    private com.cannontech.multispeak.deploy.service.ConfigurationGroup configurationGroup;

    private com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent connectDisconnectEvent;

    private com.cannontech.multispeak.deploy.service.ConnectDisconnectList connectDisconnectList;

    private com.cannontech.multispeak.deploy.service.ServiceLocations serviceLocations;

    private com.cannontech.multispeak.deploy.service.CrewActionEvent crewActionEvent;

    private com.cannontech.multispeak.deploy.service.Customer customer;

    private com.cannontech.multispeak.deploy.service.CustomerCall customerCall;

    private com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage customersAffectedbyOutage;

    private com.cannontech.multispeak.deploy.service.CustomersAttachedToDevice customersAttachedToDevice;

    private com.cannontech.multispeak.deploy.service.Employee employee;

    private com.cannontech.multispeak.deploy.service.EmployeeTimeRecord employeeTimeRecord;

    private com.cannontech.multispeak.deploy.service.CDState CDState;

    private com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation;

    private com.cannontech.multispeak.deploy.service.HistoryLog historyLog;

    private com.cannontech.multispeak.deploy.service.InHomeDisplay inHomeDisplay;

    private com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage inHomeDisplayBillingMessage;

    private com.cannontech.multispeak.deploy.service.InHomeDisplayExchange inHomeDisplayExchange;

    private com.cannontech.multispeak.deploy.service.InHomeDisplayGroup inHomeDisplayGroup;

    private com.cannontech.multispeak.deploy.service.InHomeDisplayMessage inHomeDisplayMessage;

    private com.cannontech.multispeak.deploy.service.LaborCategory laborCategory;

    private com.cannontech.multispeak.deploy.service.LoadManagementEvent loadManagementEvent;

    private com.cannontech.multispeak.deploy.service.MaterialItem materialItem;

    private com.cannontech.multispeak.deploy.service.MaterialManagementAssembly materialManagementAssembly;

    private com.cannontech.multispeak.deploy.service.Measurement measurement;

    private com.cannontech.multispeak.deploy.service.Message message;

    private com.cannontech.multispeak.deploy.service.MeterConnectivity meterConnectivity;

    private com.cannontech.multispeak.deploy.service.MeterGroup meterGroup;

    private com.cannontech.multispeak.deploy.service.MeterGroups meterGroups;

    private com.cannontech.multispeak.deploy.service.MeterRead meterRead;

    private com.cannontech.multispeak.deploy.service.MspDevice mspDevice;

    private com.cannontech.multispeak.deploy.service.MspLineObject mspLineObject;

    private com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice;

    private com.cannontech.multispeak.deploy.service.OutageDetectionEvent outageDetectionEvent;

    private com.cannontech.multispeak.deploy.service.Extensions extensions;

    private com.cannontech.multispeak.deploy.service.DocumentType documentType;  // attribute

    private com.cannontech.multispeak.deploy.service.ConnectivityModel connectivityModel;  // attribute

    public MultiSpeak() {
    }

    public MultiSpeak(
           com.cannontech.multispeak.deploy.service.SwitchingStep switchingStep,
           com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent powerFactorManagementEvent,
           com.cannontech.multispeak.deploy.service.ProfileObject profileObject,
           com.cannontech.multispeak.deploy.service.ReadingObject readingObject,
           com.cannontech.multispeak.deploy.service.ReadingSchedule readingSchedule,
           com.cannontech.multispeak.deploy.service.Regulator regulator,
           com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog,
           com.cannontech.multispeak.deploy.service.ScadaPoints scadaPoints,
           com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus,
           com.cannontech.multispeak.deploy.service.Schedule schedule,
           com.cannontech.multispeak.deploy.service.Crew crew,
           com.cannontech.multispeak.deploy.service.OutageLocation outageLocation,
           com.cannontech.multispeak.deploy.service.SecurityLight securityLight,
           com.cannontech.multispeak.deploy.service.OutageEventStatus outageEventStatus,
           com.cannontech.multispeak.deploy.service.SpatialFeatureGroup spatialFeatureGroup,
           com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus substationLoadControlStatus,
           com.cannontech.multispeak.deploy.service.SwitchingOrder switchingOrder,
           com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule,
           com.cannontech.multispeak.deploy.service.Timesheet timesheet,
           com.cannontech.multispeak.deploy.service.TrafficLight trafficLight,
           com.cannontech.multispeak.deploy.service.Transformer transformer,
           com.cannontech.multispeak.deploy.service.Truck truck,
           com.cannontech.multispeak.deploy.service.Usage usage,
           com.cannontech.multispeak.deploy.service.WorkOrder workOrder,
           com.cannontech.multispeak.deploy.service.WorkOrderSelection workOrderSelection,
           com.cannontech.multispeak.deploy.service.WorkTicket workTicket,
           com.cannontech.multispeak.deploy.service.Equipment equipment,
           com.cannontech.multispeak.deploy.service.FeederObject feederObject,
           com.cannontech.multispeak.deploy.service.OutageEvent outageEvent,
           com.cannontech.multispeak.deploy.service.Assessment assessment,
           com.cannontech.multispeak.deploy.service.PowerMonitor powerMonitor,
           com.cannontech.multispeak.deploy.service.AssessmentLocation assessmentLocation,
           com.cannontech.multispeak.deploy.service.BackgroundGraphics backgroundGraphics,
           com.cannontech.multispeak.deploy.service.BillingAccountLoad billingAccountLoad,
           com.cannontech.multispeak.deploy.service.CallBackList callBackList,
           com.cannontech.multispeak.deploy.service.Capacitor capacitor,
           com.cannontech.multispeak.deploy.service.CircuitElement circuitElement,
           com.cannontech.multispeak.deploy.service.ConfigurationGroup configurationGroup,
           com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent connectDisconnectEvent,
           com.cannontech.multispeak.deploy.service.ConnectDisconnectList connectDisconnectList,
           com.cannontech.multispeak.deploy.service.ServiceLocations serviceLocations,
           com.cannontech.multispeak.deploy.service.CrewActionEvent crewActionEvent,
           com.cannontech.multispeak.deploy.service.Customer customer,
           com.cannontech.multispeak.deploy.service.CustomerCall customerCall,
           com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage customersAffectedbyOutage,
           com.cannontech.multispeak.deploy.service.CustomersAttachedToDevice customersAttachedToDevice,
           com.cannontech.multispeak.deploy.service.Employee employee,
           com.cannontech.multispeak.deploy.service.EmployeeTimeRecord employeeTimeRecord,
           com.cannontech.multispeak.deploy.service.CDState CDState,
           com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation,
           com.cannontech.multispeak.deploy.service.HistoryLog historyLog,
           com.cannontech.multispeak.deploy.service.InHomeDisplay inHomeDisplay,
           com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage inHomeDisplayBillingMessage,
           com.cannontech.multispeak.deploy.service.InHomeDisplayExchange inHomeDisplayExchange,
           com.cannontech.multispeak.deploy.service.InHomeDisplayGroup inHomeDisplayGroup,
           com.cannontech.multispeak.deploy.service.InHomeDisplayMessage inHomeDisplayMessage,
           com.cannontech.multispeak.deploy.service.LaborCategory laborCategory,
           com.cannontech.multispeak.deploy.service.LoadManagementEvent loadManagementEvent,
           com.cannontech.multispeak.deploy.service.MaterialItem materialItem,
           com.cannontech.multispeak.deploy.service.MaterialManagementAssembly materialManagementAssembly,
           com.cannontech.multispeak.deploy.service.Measurement measurement,
           com.cannontech.multispeak.deploy.service.Message message,
           com.cannontech.multispeak.deploy.service.MeterConnectivity meterConnectivity,
           com.cannontech.multispeak.deploy.service.MeterGroup meterGroup,
           com.cannontech.multispeak.deploy.service.MeterGroups meterGroups,
           com.cannontech.multispeak.deploy.service.MeterRead meterRead,
           com.cannontech.multispeak.deploy.service.MspDevice mspDevice,
           com.cannontech.multispeak.deploy.service.MspLineObject mspLineObject,
           com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice,
           com.cannontech.multispeak.deploy.service.OutageDetectionEvent outageDetectionEvent,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           com.cannontech.multispeak.deploy.service.DocumentType documentType,
           com.cannontech.multispeak.deploy.service.ConnectivityModel connectivityModel) {
           this.switchingStep = switchingStep;
           this.powerFactorManagementEvent = powerFactorManagementEvent;
           this.profileObject = profileObject;
           this.readingObject = readingObject;
           this.readingSchedule = readingSchedule;
           this.regulator = regulator;
           this.scadaAnalog = scadaAnalog;
           this.scadaPoints = scadaPoints;
           this.scadaStatus = scadaStatus;
           this.schedule = schedule;
           this.crew = crew;
           this.outageLocation = outageLocation;
           this.securityLight = securityLight;
           this.outageEventStatus = outageEventStatus;
           this.spatialFeatureGroup = spatialFeatureGroup;
           this.substationLoadControlStatus = substationLoadControlStatus;
           this.switchingOrder = switchingOrder;
           this.switchingSchedule = switchingSchedule;
           this.timesheet = timesheet;
           this.trafficLight = trafficLight;
           this.transformer = transformer;
           this.truck = truck;
           this.usage = usage;
           this.workOrder = workOrder;
           this.workOrderSelection = workOrderSelection;
           this.workTicket = workTicket;
           this.equipment = equipment;
           this.feederObject = feederObject;
           this.outageEvent = outageEvent;
           this.assessment = assessment;
           this.powerMonitor = powerMonitor;
           this.assessmentLocation = assessmentLocation;
           this.backgroundGraphics = backgroundGraphics;
           this.billingAccountLoad = billingAccountLoad;
           this.callBackList = callBackList;
           this.capacitor = capacitor;
           this.circuitElement = circuitElement;
           this.configurationGroup = configurationGroup;
           this.connectDisconnectEvent = connectDisconnectEvent;
           this.connectDisconnectList = connectDisconnectList;
           this.serviceLocations = serviceLocations;
           this.crewActionEvent = crewActionEvent;
           this.customer = customer;
           this.customerCall = customerCall;
           this.customersAffectedbyOutage = customersAffectedbyOutage;
           this.customersAttachedToDevice = customersAttachedToDevice;
           this.employee = employee;
           this.employeeTimeRecord = employeeTimeRecord;
           this.CDState = CDState;
           this.gpsLocation = gpsLocation;
           this.historyLog = historyLog;
           this.inHomeDisplay = inHomeDisplay;
           this.inHomeDisplayBillingMessage = inHomeDisplayBillingMessage;
           this.inHomeDisplayExchange = inHomeDisplayExchange;
           this.inHomeDisplayGroup = inHomeDisplayGroup;
           this.inHomeDisplayMessage = inHomeDisplayMessage;
           this.laborCategory = laborCategory;
           this.loadManagementEvent = loadManagementEvent;
           this.materialItem = materialItem;
           this.materialManagementAssembly = materialManagementAssembly;
           this.measurement = measurement;
           this.message = message;
           this.meterConnectivity = meterConnectivity;
           this.meterGroup = meterGroup;
           this.meterGroups = meterGroups;
           this.meterRead = meterRead;
           this.mspDevice = mspDevice;
           this.mspLineObject = mspLineObject;
           this.mspSwitchingDevice = mspSwitchingDevice;
           this.outageDetectionEvent = outageDetectionEvent;
           this.extensions = extensions;
           this.documentType = documentType;
           this.connectivityModel = connectivityModel;
    }


    /**
     * Gets the switchingStep value for this MultiSpeak.
     * 
     * @return switchingStep
     */
    public com.cannontech.multispeak.deploy.service.SwitchingStep getSwitchingStep() {
        return switchingStep;
    }


    /**
     * Sets the switchingStep value for this MultiSpeak.
     * 
     * @param switchingStep
     */
    public void setSwitchingStep(com.cannontech.multispeak.deploy.service.SwitchingStep switchingStep) {
        this.switchingStep = switchingStep;
    }


    /**
     * Gets the powerFactorManagementEvent value for this MultiSpeak.
     * 
     * @return powerFactorManagementEvent
     */
    public com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent getPowerFactorManagementEvent() {
        return powerFactorManagementEvent;
    }


    /**
     * Sets the powerFactorManagementEvent value for this MultiSpeak.
     * 
     * @param powerFactorManagementEvent
     */
    public void setPowerFactorManagementEvent(com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent powerFactorManagementEvent) {
        this.powerFactorManagementEvent = powerFactorManagementEvent;
    }


    /**
     * Gets the profileObject value for this MultiSpeak.
     * 
     * @return profileObject
     */
    public com.cannontech.multispeak.deploy.service.ProfileObject getProfileObject() {
        return profileObject;
    }


    /**
     * Sets the profileObject value for this MultiSpeak.
     * 
     * @param profileObject
     */
    public void setProfileObject(com.cannontech.multispeak.deploy.service.ProfileObject profileObject) {
        this.profileObject = profileObject;
    }


    /**
     * Gets the readingObject value for this MultiSpeak.
     * 
     * @return readingObject
     */
    public com.cannontech.multispeak.deploy.service.ReadingObject getReadingObject() {
        return readingObject;
    }


    /**
     * Sets the readingObject value for this MultiSpeak.
     * 
     * @param readingObject
     */
    public void setReadingObject(com.cannontech.multispeak.deploy.service.ReadingObject readingObject) {
        this.readingObject = readingObject;
    }


    /**
     * Gets the readingSchedule value for this MultiSpeak.
     * 
     * @return readingSchedule
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule getReadingSchedule() {
        return readingSchedule;
    }


    /**
     * Sets the readingSchedule value for this MultiSpeak.
     * 
     * @param readingSchedule
     */
    public void setReadingSchedule(com.cannontech.multispeak.deploy.service.ReadingSchedule readingSchedule) {
        this.readingSchedule = readingSchedule;
    }


    /**
     * Gets the regulator value for this MultiSpeak.
     * 
     * @return regulator
     */
    public com.cannontech.multispeak.deploy.service.Regulator getRegulator() {
        return regulator;
    }


    /**
     * Sets the regulator value for this MultiSpeak.
     * 
     * @param regulator
     */
    public void setRegulator(com.cannontech.multispeak.deploy.service.Regulator regulator) {
        this.regulator = regulator;
    }


    /**
     * Gets the scadaAnalog value for this MultiSpeak.
     * 
     * @return scadaAnalog
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog getScadaAnalog() {
        return scadaAnalog;
    }


    /**
     * Sets the scadaAnalog value for this MultiSpeak.
     * 
     * @param scadaAnalog
     */
    public void setScadaAnalog(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) {
        this.scadaAnalog = scadaAnalog;
    }


    /**
     * Gets the scadaPoints value for this MultiSpeak.
     * 
     * @return scadaPoints
     */
    public com.cannontech.multispeak.deploy.service.ScadaPoints getScadaPoints() {
        return scadaPoints;
    }


    /**
     * Sets the scadaPoints value for this MultiSpeak.
     * 
     * @param scadaPoints
     */
    public void setScadaPoints(com.cannontech.multispeak.deploy.service.ScadaPoints scadaPoints) {
        this.scadaPoints = scadaPoints;
    }


    /**
     * Gets the scadaStatus value for this MultiSpeak.
     * 
     * @return scadaStatus
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus getScadaStatus() {
        return scadaStatus;
    }


    /**
     * Sets the scadaStatus value for this MultiSpeak.
     * 
     * @param scadaStatus
     */
    public void setScadaStatus(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) {
        this.scadaStatus = scadaStatus;
    }


    /**
     * Gets the schedule value for this MultiSpeak.
     * 
     * @return schedule
     */
    public com.cannontech.multispeak.deploy.service.Schedule getSchedule() {
        return schedule;
    }


    /**
     * Sets the schedule value for this MultiSpeak.
     * 
     * @param schedule
     */
    public void setSchedule(com.cannontech.multispeak.deploy.service.Schedule schedule) {
        this.schedule = schedule;
    }


    /**
     * Gets the crew value for this MultiSpeak.
     * 
     * @return crew
     */
    public com.cannontech.multispeak.deploy.service.Crew getCrew() {
        return crew;
    }


    /**
     * Sets the crew value for this MultiSpeak.
     * 
     * @param crew
     */
    public void setCrew(com.cannontech.multispeak.deploy.service.Crew crew) {
        this.crew = crew;
    }


    /**
     * Gets the outageLocation value for this MultiSpeak.
     * 
     * @return outageLocation
     */
    public com.cannontech.multispeak.deploy.service.OutageLocation getOutageLocation() {
        return outageLocation;
    }


    /**
     * Sets the outageLocation value for this MultiSpeak.
     * 
     * @param outageLocation
     */
    public void setOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation outageLocation) {
        this.outageLocation = outageLocation;
    }


    /**
     * Gets the securityLight value for this MultiSpeak.
     * 
     * @return securityLight
     */
    public com.cannontech.multispeak.deploy.service.SecurityLight getSecurityLight() {
        return securityLight;
    }


    /**
     * Sets the securityLight value for this MultiSpeak.
     * 
     * @param securityLight
     */
    public void setSecurityLight(com.cannontech.multispeak.deploy.service.SecurityLight securityLight) {
        this.securityLight = securityLight;
    }


    /**
     * Gets the outageEventStatus value for this MultiSpeak.
     * 
     * @return outageEventStatus
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus() {
        return outageEventStatus;
    }


    /**
     * Sets the outageEventStatus value for this MultiSpeak.
     * 
     * @param outageEventStatus
     */
    public void setOutageEventStatus(com.cannontech.multispeak.deploy.service.OutageEventStatus outageEventStatus) {
        this.outageEventStatus = outageEventStatus;
    }


    /**
     * Gets the spatialFeatureGroup value for this MultiSpeak.
     * 
     * @return spatialFeatureGroup
     */
    public com.cannontech.multispeak.deploy.service.SpatialFeatureGroup getSpatialFeatureGroup() {
        return spatialFeatureGroup;
    }


    /**
     * Sets the spatialFeatureGroup value for this MultiSpeak.
     * 
     * @param spatialFeatureGroup
     */
    public void setSpatialFeatureGroup(com.cannontech.multispeak.deploy.service.SpatialFeatureGroup spatialFeatureGroup) {
        this.spatialFeatureGroup = spatialFeatureGroup;
    }


    /**
     * Gets the substationLoadControlStatus value for this MultiSpeak.
     * 
     * @return substationLoadControlStatus
     */
    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus getSubstationLoadControlStatus() {
        return substationLoadControlStatus;
    }


    /**
     * Sets the substationLoadControlStatus value for this MultiSpeak.
     * 
     * @param substationLoadControlStatus
     */
    public void setSubstationLoadControlStatus(com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus substationLoadControlStatus) {
        this.substationLoadControlStatus = substationLoadControlStatus;
    }


    /**
     * Gets the switchingOrder value for this MultiSpeak.
     * 
     * @return switchingOrder
     */
    public com.cannontech.multispeak.deploy.service.SwitchingOrder getSwitchingOrder() {
        return switchingOrder;
    }


    /**
     * Sets the switchingOrder value for this MultiSpeak.
     * 
     * @param switchingOrder
     */
    public void setSwitchingOrder(com.cannontech.multispeak.deploy.service.SwitchingOrder switchingOrder) {
        this.switchingOrder = switchingOrder;
    }


    /**
     * Gets the switchingSchedule value for this MultiSpeak.
     * 
     * @return switchingSchedule
     */
    public com.cannontech.multispeak.deploy.service.SwitchingSchedule getSwitchingSchedule() {
        return switchingSchedule;
    }


    /**
     * Sets the switchingSchedule value for this MultiSpeak.
     * 
     * @param switchingSchedule
     */
    public void setSwitchingSchedule(com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule) {
        this.switchingSchedule = switchingSchedule;
    }


    /**
     * Gets the timesheet value for this MultiSpeak.
     * 
     * @return timesheet
     */
    public com.cannontech.multispeak.deploy.service.Timesheet getTimesheet() {
        return timesheet;
    }


    /**
     * Sets the timesheet value for this MultiSpeak.
     * 
     * @param timesheet
     */
    public void setTimesheet(com.cannontech.multispeak.deploy.service.Timesheet timesheet) {
        this.timesheet = timesheet;
    }


    /**
     * Gets the trafficLight value for this MultiSpeak.
     * 
     * @return trafficLight
     */
    public com.cannontech.multispeak.deploy.service.TrafficLight getTrafficLight() {
        return trafficLight;
    }


    /**
     * Sets the trafficLight value for this MultiSpeak.
     * 
     * @param trafficLight
     */
    public void setTrafficLight(com.cannontech.multispeak.deploy.service.TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }


    /**
     * Gets the transformer value for this MultiSpeak.
     * 
     * @return transformer
     */
    public com.cannontech.multispeak.deploy.service.Transformer getTransformer() {
        return transformer;
    }


    /**
     * Sets the transformer value for this MultiSpeak.
     * 
     * @param transformer
     */
    public void setTransformer(com.cannontech.multispeak.deploy.service.Transformer transformer) {
        this.transformer = transformer;
    }


    /**
     * Gets the truck value for this MultiSpeak.
     * 
     * @return truck
     */
    public com.cannontech.multispeak.deploy.service.Truck getTruck() {
        return truck;
    }


    /**
     * Sets the truck value for this MultiSpeak.
     * 
     * @param truck
     */
    public void setTruck(com.cannontech.multispeak.deploy.service.Truck truck) {
        this.truck = truck;
    }


    /**
     * Gets the usage value for this MultiSpeak.
     * 
     * @return usage
     */
    public com.cannontech.multispeak.deploy.service.Usage getUsage() {
        return usage;
    }


    /**
     * Sets the usage value for this MultiSpeak.
     * 
     * @param usage
     */
    public void setUsage(com.cannontech.multispeak.deploy.service.Usage usage) {
        this.usage = usage;
    }


    /**
     * Gets the workOrder value for this MultiSpeak.
     * 
     * @return workOrder
     */
    public com.cannontech.multispeak.deploy.service.WorkOrder getWorkOrder() {
        return workOrder;
    }


    /**
     * Sets the workOrder value for this MultiSpeak.
     * 
     * @param workOrder
     */
    public void setWorkOrder(com.cannontech.multispeak.deploy.service.WorkOrder workOrder) {
        this.workOrder = workOrder;
    }


    /**
     * Gets the workOrderSelection value for this MultiSpeak.
     * 
     * @return workOrderSelection
     */
    public com.cannontech.multispeak.deploy.service.WorkOrderSelection getWorkOrderSelection() {
        return workOrderSelection;
    }


    /**
     * Sets the workOrderSelection value for this MultiSpeak.
     * 
     * @param workOrderSelection
     */
    public void setWorkOrderSelection(com.cannontech.multispeak.deploy.service.WorkOrderSelection workOrderSelection) {
        this.workOrderSelection = workOrderSelection;
    }


    /**
     * Gets the workTicket value for this MultiSpeak.
     * 
     * @return workTicket
     */
    public com.cannontech.multispeak.deploy.service.WorkTicket getWorkTicket() {
        return workTicket;
    }


    /**
     * Sets the workTicket value for this MultiSpeak.
     * 
     * @param workTicket
     */
    public void setWorkTicket(com.cannontech.multispeak.deploy.service.WorkTicket workTicket) {
        this.workTicket = workTicket;
    }


    /**
     * Gets the equipment value for this MultiSpeak.
     * 
     * @return equipment
     */
    public com.cannontech.multispeak.deploy.service.Equipment getEquipment() {
        return equipment;
    }


    /**
     * Sets the equipment value for this MultiSpeak.
     * 
     * @param equipment
     */
    public void setEquipment(com.cannontech.multispeak.deploy.service.Equipment equipment) {
        this.equipment = equipment;
    }


    /**
     * Gets the feederObject value for this MultiSpeak.
     * 
     * @return feederObject
     */
    public com.cannontech.multispeak.deploy.service.FeederObject getFeederObject() {
        return feederObject;
    }


    /**
     * Sets the feederObject value for this MultiSpeak.
     * 
     * @param feederObject
     */
    public void setFeederObject(com.cannontech.multispeak.deploy.service.FeederObject feederObject) {
        this.feederObject = feederObject;
    }


    /**
     * Gets the outageEvent value for this MultiSpeak.
     * 
     * @return outageEvent
     */
    public com.cannontech.multispeak.deploy.service.OutageEvent getOutageEvent() {
        return outageEvent;
    }


    /**
     * Sets the outageEvent value for this MultiSpeak.
     * 
     * @param outageEvent
     */
    public void setOutageEvent(com.cannontech.multispeak.deploy.service.OutageEvent outageEvent) {
        this.outageEvent = outageEvent;
    }


    /**
     * Gets the assessment value for this MultiSpeak.
     * 
     * @return assessment
     */
    public com.cannontech.multispeak.deploy.service.Assessment getAssessment() {
        return assessment;
    }


    /**
     * Sets the assessment value for this MultiSpeak.
     * 
     * @param assessment
     */
    public void setAssessment(com.cannontech.multispeak.deploy.service.Assessment assessment) {
        this.assessment = assessment;
    }


    /**
     * Gets the powerMonitor value for this MultiSpeak.
     * 
     * @return powerMonitor
     */
    public com.cannontech.multispeak.deploy.service.PowerMonitor getPowerMonitor() {
        return powerMonitor;
    }


    /**
     * Sets the powerMonitor value for this MultiSpeak.
     * 
     * @param powerMonitor
     */
    public void setPowerMonitor(com.cannontech.multispeak.deploy.service.PowerMonitor powerMonitor) {
        this.powerMonitor = powerMonitor;
    }


    /**
     * Gets the assessmentLocation value for this MultiSpeak.
     * 
     * @return assessmentLocation
     */
    public com.cannontech.multispeak.deploy.service.AssessmentLocation getAssessmentLocation() {
        return assessmentLocation;
    }


    /**
     * Sets the assessmentLocation value for this MultiSpeak.
     * 
     * @param assessmentLocation
     */
    public void setAssessmentLocation(com.cannontech.multispeak.deploy.service.AssessmentLocation assessmentLocation) {
        this.assessmentLocation = assessmentLocation;
    }


    /**
     * Gets the backgroundGraphics value for this MultiSpeak.
     * 
     * @return backgroundGraphics
     */
    public com.cannontech.multispeak.deploy.service.BackgroundGraphics getBackgroundGraphics() {
        return backgroundGraphics;
    }


    /**
     * Sets the backgroundGraphics value for this MultiSpeak.
     * 
     * @param backgroundGraphics
     */
    public void setBackgroundGraphics(com.cannontech.multispeak.deploy.service.BackgroundGraphics backgroundGraphics) {
        this.backgroundGraphics = backgroundGraphics;
    }


    /**
     * Gets the billingAccountLoad value for this MultiSpeak.
     * 
     * @return billingAccountLoad
     */
    public com.cannontech.multispeak.deploy.service.BillingAccountLoad getBillingAccountLoad() {
        return billingAccountLoad;
    }


    /**
     * Sets the billingAccountLoad value for this MultiSpeak.
     * 
     * @param billingAccountLoad
     */
    public void setBillingAccountLoad(com.cannontech.multispeak.deploy.service.BillingAccountLoad billingAccountLoad) {
        this.billingAccountLoad = billingAccountLoad;
    }


    /**
     * Gets the callBackList value for this MultiSpeak.
     * 
     * @return callBackList
     */
    public com.cannontech.multispeak.deploy.service.CallBackList getCallBackList() {
        return callBackList;
    }


    /**
     * Sets the callBackList value for this MultiSpeak.
     * 
     * @param callBackList
     */
    public void setCallBackList(com.cannontech.multispeak.deploy.service.CallBackList callBackList) {
        this.callBackList = callBackList;
    }


    /**
     * Gets the capacitor value for this MultiSpeak.
     * 
     * @return capacitor
     */
    public com.cannontech.multispeak.deploy.service.Capacitor getCapacitor() {
        return capacitor;
    }


    /**
     * Sets the capacitor value for this MultiSpeak.
     * 
     * @param capacitor
     */
    public void setCapacitor(com.cannontech.multispeak.deploy.service.Capacitor capacitor) {
        this.capacitor = capacitor;
    }


    /**
     * Gets the circuitElement value for this MultiSpeak.
     * 
     * @return circuitElement
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement getCircuitElement() {
        return circuitElement;
    }


    /**
     * Sets the circuitElement value for this MultiSpeak.
     * 
     * @param circuitElement
     */
    public void setCircuitElement(com.cannontech.multispeak.deploy.service.CircuitElement circuitElement) {
        this.circuitElement = circuitElement;
    }


    /**
     * Gets the configurationGroup value for this MultiSpeak.
     * 
     * @return configurationGroup
     */
    public com.cannontech.multispeak.deploy.service.ConfigurationGroup getConfigurationGroup() {
        return configurationGroup;
    }


    /**
     * Sets the configurationGroup value for this MultiSpeak.
     * 
     * @param configurationGroup
     */
    public void setConfigurationGroup(com.cannontech.multispeak.deploy.service.ConfigurationGroup configurationGroup) {
        this.configurationGroup = configurationGroup;
    }


    /**
     * Gets the connectDisconnectEvent value for this MultiSpeak.
     * 
     * @return connectDisconnectEvent
     */
    public com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent getConnectDisconnectEvent() {
        return connectDisconnectEvent;
    }


    /**
     * Sets the connectDisconnectEvent value for this MultiSpeak.
     * 
     * @param connectDisconnectEvent
     */
    public void setConnectDisconnectEvent(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent connectDisconnectEvent) {
        this.connectDisconnectEvent = connectDisconnectEvent;
    }


    /**
     * Gets the connectDisconnectList value for this MultiSpeak.
     * 
     * @return connectDisconnectList
     */
    public com.cannontech.multispeak.deploy.service.ConnectDisconnectList getConnectDisconnectList() {
        return connectDisconnectList;
    }


    /**
     * Sets the connectDisconnectList value for this MultiSpeak.
     * 
     * @param connectDisconnectList
     */
    public void setConnectDisconnectList(com.cannontech.multispeak.deploy.service.ConnectDisconnectList connectDisconnectList) {
        this.connectDisconnectList = connectDisconnectList;
    }


    /**
     * Gets the serviceLocations value for this MultiSpeak.
     * 
     * @return serviceLocations
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocations getServiceLocations() {
        return serviceLocations;
    }


    /**
     * Sets the serviceLocations value for this MultiSpeak.
     * 
     * @param serviceLocations
     */
    public void setServiceLocations(com.cannontech.multispeak.deploy.service.ServiceLocations serviceLocations) {
        this.serviceLocations = serviceLocations;
    }


    /**
     * Gets the crewActionEvent value for this MultiSpeak.
     * 
     * @return crewActionEvent
     */
    public com.cannontech.multispeak.deploy.service.CrewActionEvent getCrewActionEvent() {
        return crewActionEvent;
    }


    /**
     * Sets the crewActionEvent value for this MultiSpeak.
     * 
     * @param crewActionEvent
     */
    public void setCrewActionEvent(com.cannontech.multispeak.deploy.service.CrewActionEvent crewActionEvent) {
        this.crewActionEvent = crewActionEvent;
    }


    /**
     * Gets the customer value for this MultiSpeak.
     * 
     * @return customer
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomer() {
        return customer;
    }


    /**
     * Sets the customer value for this MultiSpeak.
     * 
     * @param customer
     */
    public void setCustomer(com.cannontech.multispeak.deploy.service.Customer customer) {
        this.customer = customer;
    }


    /**
     * Gets the customerCall value for this MultiSpeak.
     * 
     * @return customerCall
     */
    public com.cannontech.multispeak.deploy.service.CustomerCall getCustomerCall() {
        return customerCall;
    }


    /**
     * Sets the customerCall value for this MultiSpeak.
     * 
     * @param customerCall
     */
    public void setCustomerCall(com.cannontech.multispeak.deploy.service.CustomerCall customerCall) {
        this.customerCall = customerCall;
    }


    /**
     * Gets the customersAffectedbyOutage value for this MultiSpeak.
     * 
     * @return customersAffectedbyOutage
     */
    public com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage getCustomersAffectedbyOutage() {
        return customersAffectedbyOutage;
    }


    /**
     * Sets the customersAffectedbyOutage value for this MultiSpeak.
     * 
     * @param customersAffectedbyOutage
     */
    public void setCustomersAffectedbyOutage(com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage customersAffectedbyOutage) {
        this.customersAffectedbyOutage = customersAffectedbyOutage;
    }


    /**
     * Gets the customersAttachedToDevice value for this MultiSpeak.
     * 
     * @return customersAttachedToDevice
     */
    public com.cannontech.multispeak.deploy.service.CustomersAttachedToDevice getCustomersAttachedToDevice() {
        return customersAttachedToDevice;
    }


    /**
     * Sets the customersAttachedToDevice value for this MultiSpeak.
     * 
     * @param customersAttachedToDevice
     */
    public void setCustomersAttachedToDevice(com.cannontech.multispeak.deploy.service.CustomersAttachedToDevice customersAttachedToDevice) {
        this.customersAttachedToDevice = customersAttachedToDevice;
    }


    /**
     * Gets the employee value for this MultiSpeak.
     * 
     * @return employee
     */
    public com.cannontech.multispeak.deploy.service.Employee getEmployee() {
        return employee;
    }


    /**
     * Sets the employee value for this MultiSpeak.
     * 
     * @param employee
     */
    public void setEmployee(com.cannontech.multispeak.deploy.service.Employee employee) {
        this.employee = employee;
    }


    /**
     * Gets the employeeTimeRecord value for this MultiSpeak.
     * 
     * @return employeeTimeRecord
     */
    public com.cannontech.multispeak.deploy.service.EmployeeTimeRecord getEmployeeTimeRecord() {
        return employeeTimeRecord;
    }


    /**
     * Sets the employeeTimeRecord value for this MultiSpeak.
     * 
     * @param employeeTimeRecord
     */
    public void setEmployeeTimeRecord(com.cannontech.multispeak.deploy.service.EmployeeTimeRecord employeeTimeRecord) {
        this.employeeTimeRecord = employeeTimeRecord;
    }


    /**
     * Gets the CDState value for this MultiSpeak.
     * 
     * @return CDState
     */
    public com.cannontech.multispeak.deploy.service.CDState getCDState() {
        return CDState;
    }


    /**
     * Sets the CDState value for this MultiSpeak.
     * 
     * @param CDState
     */
    public void setCDState(com.cannontech.multispeak.deploy.service.CDState CDState) {
        this.CDState = CDState;
    }


    /**
     * Gets the gpsLocation value for this MultiSpeak.
     * 
     * @return gpsLocation
     */
    public com.cannontech.multispeak.deploy.service.GpsLocation getGpsLocation() {
        return gpsLocation;
    }


    /**
     * Sets the gpsLocation value for this MultiSpeak.
     * 
     * @param gpsLocation
     */
    public void setGpsLocation(com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
    }


    /**
     * Gets the historyLog value for this MultiSpeak.
     * 
     * @return historyLog
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog getHistoryLog() {
        return historyLog;
    }


    /**
     * Sets the historyLog value for this MultiSpeak.
     * 
     * @param historyLog
     */
    public void setHistoryLog(com.cannontech.multispeak.deploy.service.HistoryLog historyLog) {
        this.historyLog = historyLog;
    }


    /**
     * Gets the inHomeDisplay value for this MultiSpeak.
     * 
     * @return inHomeDisplay
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplay getInHomeDisplay() {
        return inHomeDisplay;
    }


    /**
     * Sets the inHomeDisplay value for this MultiSpeak.
     * 
     * @param inHomeDisplay
     */
    public void setInHomeDisplay(com.cannontech.multispeak.deploy.service.InHomeDisplay inHomeDisplay) {
        this.inHomeDisplay = inHomeDisplay;
    }


    /**
     * Gets the inHomeDisplayBillingMessage value for this MultiSpeak.
     * 
     * @return inHomeDisplayBillingMessage
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage getInHomeDisplayBillingMessage() {
        return inHomeDisplayBillingMessage;
    }


    /**
     * Sets the inHomeDisplayBillingMessage value for this MultiSpeak.
     * 
     * @param inHomeDisplayBillingMessage
     */
    public void setInHomeDisplayBillingMessage(com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage inHomeDisplayBillingMessage) {
        this.inHomeDisplayBillingMessage = inHomeDisplayBillingMessage;
    }


    /**
     * Gets the inHomeDisplayExchange value for this MultiSpeak.
     * 
     * @return inHomeDisplayExchange
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayExchange getInHomeDisplayExchange() {
        return inHomeDisplayExchange;
    }


    /**
     * Sets the inHomeDisplayExchange value for this MultiSpeak.
     * 
     * @param inHomeDisplayExchange
     */
    public void setInHomeDisplayExchange(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange inHomeDisplayExchange) {
        this.inHomeDisplayExchange = inHomeDisplayExchange;
    }


    /**
     * Gets the inHomeDisplayGroup value for this MultiSpeak.
     * 
     * @return inHomeDisplayGroup
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayGroup getInHomeDisplayGroup() {
        return inHomeDisplayGroup;
    }


    /**
     * Sets the inHomeDisplayGroup value for this MultiSpeak.
     * 
     * @param inHomeDisplayGroup
     */
    public void setInHomeDisplayGroup(com.cannontech.multispeak.deploy.service.InHomeDisplayGroup inHomeDisplayGroup) {
        this.inHomeDisplayGroup = inHomeDisplayGroup;
    }


    /**
     * Gets the inHomeDisplayMessage value for this MultiSpeak.
     * 
     * @return inHomeDisplayMessage
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayMessage getInHomeDisplayMessage() {
        return inHomeDisplayMessage;
    }


    /**
     * Sets the inHomeDisplayMessage value for this MultiSpeak.
     * 
     * @param inHomeDisplayMessage
     */
    public void setInHomeDisplayMessage(com.cannontech.multispeak.deploy.service.InHomeDisplayMessage inHomeDisplayMessage) {
        this.inHomeDisplayMessage = inHomeDisplayMessage;
    }


    /**
     * Gets the laborCategory value for this MultiSpeak.
     * 
     * @return laborCategory
     */
    public com.cannontech.multispeak.deploy.service.LaborCategory getLaborCategory() {
        return laborCategory;
    }


    /**
     * Sets the laborCategory value for this MultiSpeak.
     * 
     * @param laborCategory
     */
    public void setLaborCategory(com.cannontech.multispeak.deploy.service.LaborCategory laborCategory) {
        this.laborCategory = laborCategory;
    }


    /**
     * Gets the loadManagementEvent value for this MultiSpeak.
     * 
     * @return loadManagementEvent
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementEvent getLoadManagementEvent() {
        return loadManagementEvent;
    }


    /**
     * Sets the loadManagementEvent value for this MultiSpeak.
     * 
     * @param loadManagementEvent
     */
    public void setLoadManagementEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent loadManagementEvent) {
        this.loadManagementEvent = loadManagementEvent;
    }


    /**
     * Gets the materialItem value for this MultiSpeak.
     * 
     * @return materialItem
     */
    public com.cannontech.multispeak.deploy.service.MaterialItem getMaterialItem() {
        return materialItem;
    }


    /**
     * Sets the materialItem value for this MultiSpeak.
     * 
     * @param materialItem
     */
    public void setMaterialItem(com.cannontech.multispeak.deploy.service.MaterialItem materialItem) {
        this.materialItem = materialItem;
    }


    /**
     * Gets the materialManagementAssembly value for this MultiSpeak.
     * 
     * @return materialManagementAssembly
     */
    public com.cannontech.multispeak.deploy.service.MaterialManagementAssembly getMaterialManagementAssembly() {
        return materialManagementAssembly;
    }


    /**
     * Sets the materialManagementAssembly value for this MultiSpeak.
     * 
     * @param materialManagementAssembly
     */
    public void setMaterialManagementAssembly(com.cannontech.multispeak.deploy.service.MaterialManagementAssembly materialManagementAssembly) {
        this.materialManagementAssembly = materialManagementAssembly;
    }


    /**
     * Gets the measurement value for this MultiSpeak.
     * 
     * @return measurement
     */
    public com.cannontech.multispeak.deploy.service.Measurement getMeasurement() {
        return measurement;
    }


    /**
     * Sets the measurement value for this MultiSpeak.
     * 
     * @param measurement
     */
    public void setMeasurement(com.cannontech.multispeak.deploy.service.Measurement measurement) {
        this.measurement = measurement;
    }


    /**
     * Gets the message value for this MultiSpeak.
     * 
     * @return message
     */
    public com.cannontech.multispeak.deploy.service.Message getMessage() {
        return message;
    }


    /**
     * Sets the message value for this MultiSpeak.
     * 
     * @param message
     */
    public void setMessage(com.cannontech.multispeak.deploy.service.Message message) {
        this.message = message;
    }


    /**
     * Gets the meterConnectivity value for this MultiSpeak.
     * 
     * @return meterConnectivity
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity getMeterConnectivity() {
        return meterConnectivity;
    }


    /**
     * Sets the meterConnectivity value for this MultiSpeak.
     * 
     * @param meterConnectivity
     */
    public void setMeterConnectivity(com.cannontech.multispeak.deploy.service.MeterConnectivity meterConnectivity) {
        this.meterConnectivity = meterConnectivity;
    }


    /**
     * Gets the meterGroup value for this MultiSpeak.
     * 
     * @return meterGroup
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup getMeterGroup() {
        return meterGroup;
    }


    /**
     * Sets the meterGroup value for this MultiSpeak.
     * 
     * @param meterGroup
     */
    public void setMeterGroup(com.cannontech.multispeak.deploy.service.MeterGroup meterGroup) {
        this.meterGroup = meterGroup;
    }


    /**
     * Gets the meterGroups value for this MultiSpeak.
     * 
     * @return meterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroups getMeterGroups() {
        return meterGroups;
    }


    /**
     * Sets the meterGroups value for this MultiSpeak.
     * 
     * @param meterGroups
     */
    public void setMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroups meterGroups) {
        this.meterGroups = meterGroups;
    }


    /**
     * Gets the meterRead value for this MultiSpeak.
     * 
     * @return meterRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getMeterRead() {
        return meterRead;
    }


    /**
     * Sets the meterRead value for this MultiSpeak.
     * 
     * @param meterRead
     */
    public void setMeterRead(com.cannontech.multispeak.deploy.service.MeterRead meterRead) {
        this.meterRead = meterRead;
    }


    /**
     * Gets the mspDevice value for this MultiSpeak.
     * 
     * @return mspDevice
     */
    public com.cannontech.multispeak.deploy.service.MspDevice getMspDevice() {
        return mspDevice;
    }


    /**
     * Sets the mspDevice value for this MultiSpeak.
     * 
     * @param mspDevice
     */
    public void setMspDevice(com.cannontech.multispeak.deploy.service.MspDevice mspDevice) {
        this.mspDevice = mspDevice;
    }


    /**
     * Gets the mspLineObject value for this MultiSpeak.
     * 
     * @return mspLineObject
     */
    public com.cannontech.multispeak.deploy.service.MspLineObject getMspLineObject() {
        return mspLineObject;
    }


    /**
     * Sets the mspLineObject value for this MultiSpeak.
     * 
     * @param mspLineObject
     */
    public void setMspLineObject(com.cannontech.multispeak.deploy.service.MspLineObject mspLineObject) {
        this.mspLineObject = mspLineObject;
    }


    /**
     * Gets the mspSwitchingDevice value for this MultiSpeak.
     * 
     * @return mspSwitchingDevice
     */
    public com.cannontech.multispeak.deploy.service.MspSwitchingDevice getMspSwitchingDevice() {
        return mspSwitchingDevice;
    }


    /**
     * Sets the mspSwitchingDevice value for this MultiSpeak.
     * 
     * @param mspSwitchingDevice
     */
    public void setMspSwitchingDevice(com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice) {
        this.mspSwitchingDevice = mspSwitchingDevice;
    }


    /**
     * Gets the outageDetectionEvent value for this MultiSpeak.
     * 
     * @return outageDetectionEvent
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionEvent getOutageDetectionEvent() {
        return outageDetectionEvent;
    }


    /**
     * Sets the outageDetectionEvent value for this MultiSpeak.
     * 
     * @param outageDetectionEvent
     */
    public void setOutageDetectionEvent(com.cannontech.multispeak.deploy.service.OutageDetectionEvent outageDetectionEvent) {
        this.outageDetectionEvent = outageDetectionEvent;
    }


    /**
     * Gets the extensions value for this MultiSpeak.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.deploy.service.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this MultiSpeak.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.deploy.service.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the documentType value for this MultiSpeak.
     * 
     * @return documentType
     */
    public com.cannontech.multispeak.deploy.service.DocumentType getDocumentType() {
        return documentType;
    }


    /**
     * Sets the documentType value for this MultiSpeak.
     * 
     * @param documentType
     */
    public void setDocumentType(com.cannontech.multispeak.deploy.service.DocumentType documentType) {
        this.documentType = documentType;
    }


    /**
     * Gets the connectivityModel value for this MultiSpeak.
     * 
     * @return connectivityModel
     */
    public com.cannontech.multispeak.deploy.service.ConnectivityModel getConnectivityModel() {
        return connectivityModel;
    }


    /**
     * Sets the connectivityModel value for this MultiSpeak.
     * 
     * @param connectivityModel
     */
    public void setConnectivityModel(com.cannontech.multispeak.deploy.service.ConnectivityModel connectivityModel) {
        this.connectivityModel = connectivityModel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MultiSpeak)) return false;
        MultiSpeak other = (MultiSpeak) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.switchingStep==null && other.getSwitchingStep()==null) || 
             (this.switchingStep!=null &&
              this.switchingStep.equals(other.getSwitchingStep()))) &&
            ((this.powerFactorManagementEvent==null && other.getPowerFactorManagementEvent()==null) || 
             (this.powerFactorManagementEvent!=null &&
              this.powerFactorManagementEvent.equals(other.getPowerFactorManagementEvent()))) &&
            ((this.profileObject==null && other.getProfileObject()==null) || 
             (this.profileObject!=null &&
              this.profileObject.equals(other.getProfileObject()))) &&
            ((this.readingObject==null && other.getReadingObject()==null) || 
             (this.readingObject!=null &&
              this.readingObject.equals(other.getReadingObject()))) &&
            ((this.readingSchedule==null && other.getReadingSchedule()==null) || 
             (this.readingSchedule!=null &&
              this.readingSchedule.equals(other.getReadingSchedule()))) &&
            ((this.regulator==null && other.getRegulator()==null) || 
             (this.regulator!=null &&
              this.regulator.equals(other.getRegulator()))) &&
            ((this.scadaAnalog==null && other.getScadaAnalog()==null) || 
             (this.scadaAnalog!=null &&
              this.scadaAnalog.equals(other.getScadaAnalog()))) &&
            ((this.scadaPoints==null && other.getScadaPoints()==null) || 
             (this.scadaPoints!=null &&
              this.scadaPoints.equals(other.getScadaPoints()))) &&
            ((this.scadaStatus==null && other.getScadaStatus()==null) || 
             (this.scadaStatus!=null &&
              this.scadaStatus.equals(other.getScadaStatus()))) &&
            ((this.schedule==null && other.getSchedule()==null) || 
             (this.schedule!=null &&
              this.schedule.equals(other.getSchedule()))) &&
            ((this.crew==null && other.getCrew()==null) || 
             (this.crew!=null &&
              this.crew.equals(other.getCrew()))) &&
            ((this.outageLocation==null && other.getOutageLocation()==null) || 
             (this.outageLocation!=null &&
              this.outageLocation.equals(other.getOutageLocation()))) &&
            ((this.securityLight==null && other.getSecurityLight()==null) || 
             (this.securityLight!=null &&
              this.securityLight.equals(other.getSecurityLight()))) &&
            ((this.outageEventStatus==null && other.getOutageEventStatus()==null) || 
             (this.outageEventStatus!=null &&
              this.outageEventStatus.equals(other.getOutageEventStatus()))) &&
            ((this.spatialFeatureGroup==null && other.getSpatialFeatureGroup()==null) || 
             (this.spatialFeatureGroup!=null &&
              this.spatialFeatureGroup.equals(other.getSpatialFeatureGroup()))) &&
            ((this.substationLoadControlStatus==null && other.getSubstationLoadControlStatus()==null) || 
             (this.substationLoadControlStatus!=null &&
              this.substationLoadControlStatus.equals(other.getSubstationLoadControlStatus()))) &&
            ((this.switchingOrder==null && other.getSwitchingOrder()==null) || 
             (this.switchingOrder!=null &&
              this.switchingOrder.equals(other.getSwitchingOrder()))) &&
            ((this.switchingSchedule==null && other.getSwitchingSchedule()==null) || 
             (this.switchingSchedule!=null &&
              this.switchingSchedule.equals(other.getSwitchingSchedule()))) &&
            ((this.timesheet==null && other.getTimesheet()==null) || 
             (this.timesheet!=null &&
              this.timesheet.equals(other.getTimesheet()))) &&
            ((this.trafficLight==null && other.getTrafficLight()==null) || 
             (this.trafficLight!=null &&
              this.trafficLight.equals(other.getTrafficLight()))) &&
            ((this.transformer==null && other.getTransformer()==null) || 
             (this.transformer!=null &&
              this.transformer.equals(other.getTransformer()))) &&
            ((this.truck==null && other.getTruck()==null) || 
             (this.truck!=null &&
              this.truck.equals(other.getTruck()))) &&
            ((this.usage==null && other.getUsage()==null) || 
             (this.usage!=null &&
              this.usage.equals(other.getUsage()))) &&
            ((this.workOrder==null && other.getWorkOrder()==null) || 
             (this.workOrder!=null &&
              this.workOrder.equals(other.getWorkOrder()))) &&
            ((this.workOrderSelection==null && other.getWorkOrderSelection()==null) || 
             (this.workOrderSelection!=null &&
              this.workOrderSelection.equals(other.getWorkOrderSelection()))) &&
            ((this.workTicket==null && other.getWorkTicket()==null) || 
             (this.workTicket!=null &&
              this.workTicket.equals(other.getWorkTicket()))) &&
            ((this.equipment==null && other.getEquipment()==null) || 
             (this.equipment!=null &&
              this.equipment.equals(other.getEquipment()))) &&
            ((this.feederObject==null && other.getFeederObject()==null) || 
             (this.feederObject!=null &&
              this.feederObject.equals(other.getFeederObject()))) &&
            ((this.outageEvent==null && other.getOutageEvent()==null) || 
             (this.outageEvent!=null &&
              this.outageEvent.equals(other.getOutageEvent()))) &&
            ((this.assessment==null && other.getAssessment()==null) || 
             (this.assessment!=null &&
              this.assessment.equals(other.getAssessment()))) &&
            ((this.powerMonitor==null && other.getPowerMonitor()==null) || 
             (this.powerMonitor!=null &&
              this.powerMonitor.equals(other.getPowerMonitor()))) &&
            ((this.assessmentLocation==null && other.getAssessmentLocation()==null) || 
             (this.assessmentLocation!=null &&
              this.assessmentLocation.equals(other.getAssessmentLocation()))) &&
            ((this.backgroundGraphics==null && other.getBackgroundGraphics()==null) || 
             (this.backgroundGraphics!=null &&
              this.backgroundGraphics.equals(other.getBackgroundGraphics()))) &&
            ((this.billingAccountLoad==null && other.getBillingAccountLoad()==null) || 
             (this.billingAccountLoad!=null &&
              this.billingAccountLoad.equals(other.getBillingAccountLoad()))) &&
            ((this.callBackList==null && other.getCallBackList()==null) || 
             (this.callBackList!=null &&
              this.callBackList.equals(other.getCallBackList()))) &&
            ((this.capacitor==null && other.getCapacitor()==null) || 
             (this.capacitor!=null &&
              this.capacitor.equals(other.getCapacitor()))) &&
            ((this.circuitElement==null && other.getCircuitElement()==null) || 
             (this.circuitElement!=null &&
              this.circuitElement.equals(other.getCircuitElement()))) &&
            ((this.configurationGroup==null && other.getConfigurationGroup()==null) || 
             (this.configurationGroup!=null &&
              this.configurationGroup.equals(other.getConfigurationGroup()))) &&
            ((this.connectDisconnectEvent==null && other.getConnectDisconnectEvent()==null) || 
             (this.connectDisconnectEvent!=null &&
              this.connectDisconnectEvent.equals(other.getConnectDisconnectEvent()))) &&
            ((this.connectDisconnectList==null && other.getConnectDisconnectList()==null) || 
             (this.connectDisconnectList!=null &&
              this.connectDisconnectList.equals(other.getConnectDisconnectList()))) &&
            ((this.serviceLocations==null && other.getServiceLocations()==null) || 
             (this.serviceLocations!=null &&
              this.serviceLocations.equals(other.getServiceLocations()))) &&
            ((this.crewActionEvent==null && other.getCrewActionEvent()==null) || 
             (this.crewActionEvent!=null &&
              this.crewActionEvent.equals(other.getCrewActionEvent()))) &&
            ((this.customer==null && other.getCustomer()==null) || 
             (this.customer!=null &&
              this.customer.equals(other.getCustomer()))) &&
            ((this.customerCall==null && other.getCustomerCall()==null) || 
             (this.customerCall!=null &&
              this.customerCall.equals(other.getCustomerCall()))) &&
            ((this.customersAffectedbyOutage==null && other.getCustomersAffectedbyOutage()==null) || 
             (this.customersAffectedbyOutage!=null &&
              this.customersAffectedbyOutage.equals(other.getCustomersAffectedbyOutage()))) &&
            ((this.customersAttachedToDevice==null && other.getCustomersAttachedToDevice()==null) || 
             (this.customersAttachedToDevice!=null &&
              this.customersAttachedToDevice.equals(other.getCustomersAttachedToDevice()))) &&
            ((this.employee==null && other.getEmployee()==null) || 
             (this.employee!=null &&
              this.employee.equals(other.getEmployee()))) &&
            ((this.employeeTimeRecord==null && other.getEmployeeTimeRecord()==null) || 
             (this.employeeTimeRecord!=null &&
              this.employeeTimeRecord.equals(other.getEmployeeTimeRecord()))) &&
            ((this.CDState==null && other.getCDState()==null) || 
             (this.CDState!=null &&
              this.CDState.equals(other.getCDState()))) &&
            ((this.gpsLocation==null && other.getGpsLocation()==null) || 
             (this.gpsLocation!=null &&
              this.gpsLocation.equals(other.getGpsLocation()))) &&
            ((this.historyLog==null && other.getHistoryLog()==null) || 
             (this.historyLog!=null &&
              this.historyLog.equals(other.getHistoryLog()))) &&
            ((this.inHomeDisplay==null && other.getInHomeDisplay()==null) || 
             (this.inHomeDisplay!=null &&
              this.inHomeDisplay.equals(other.getInHomeDisplay()))) &&
            ((this.inHomeDisplayBillingMessage==null && other.getInHomeDisplayBillingMessage()==null) || 
             (this.inHomeDisplayBillingMessage!=null &&
              this.inHomeDisplayBillingMessage.equals(other.getInHomeDisplayBillingMessage()))) &&
            ((this.inHomeDisplayExchange==null && other.getInHomeDisplayExchange()==null) || 
             (this.inHomeDisplayExchange!=null &&
              this.inHomeDisplayExchange.equals(other.getInHomeDisplayExchange()))) &&
            ((this.inHomeDisplayGroup==null && other.getInHomeDisplayGroup()==null) || 
             (this.inHomeDisplayGroup!=null &&
              this.inHomeDisplayGroup.equals(other.getInHomeDisplayGroup()))) &&
            ((this.inHomeDisplayMessage==null && other.getInHomeDisplayMessage()==null) || 
             (this.inHomeDisplayMessage!=null &&
              this.inHomeDisplayMessage.equals(other.getInHomeDisplayMessage()))) &&
            ((this.laborCategory==null && other.getLaborCategory()==null) || 
             (this.laborCategory!=null &&
              this.laborCategory.equals(other.getLaborCategory()))) &&
            ((this.loadManagementEvent==null && other.getLoadManagementEvent()==null) || 
             (this.loadManagementEvent!=null &&
              this.loadManagementEvent.equals(other.getLoadManagementEvent()))) &&
            ((this.materialItem==null && other.getMaterialItem()==null) || 
             (this.materialItem!=null &&
              this.materialItem.equals(other.getMaterialItem()))) &&
            ((this.materialManagementAssembly==null && other.getMaterialManagementAssembly()==null) || 
             (this.materialManagementAssembly!=null &&
              this.materialManagementAssembly.equals(other.getMaterialManagementAssembly()))) &&
            ((this.measurement==null && other.getMeasurement()==null) || 
             (this.measurement!=null &&
              this.measurement.equals(other.getMeasurement()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.meterConnectivity==null && other.getMeterConnectivity()==null) || 
             (this.meterConnectivity!=null &&
              this.meterConnectivity.equals(other.getMeterConnectivity()))) &&
            ((this.meterGroup==null && other.getMeterGroup()==null) || 
             (this.meterGroup!=null &&
              this.meterGroup.equals(other.getMeterGroup()))) &&
            ((this.meterGroups==null && other.getMeterGroups()==null) || 
             (this.meterGroups!=null &&
              this.meterGroups.equals(other.getMeterGroups()))) &&
            ((this.meterRead==null && other.getMeterRead()==null) || 
             (this.meterRead!=null &&
              this.meterRead.equals(other.getMeterRead()))) &&
            ((this.mspDevice==null && other.getMspDevice()==null) || 
             (this.mspDevice!=null &&
              this.mspDevice.equals(other.getMspDevice()))) &&
            ((this.mspLineObject==null && other.getMspLineObject()==null) || 
             (this.mspLineObject!=null &&
              this.mspLineObject.equals(other.getMspLineObject()))) &&
            ((this.mspSwitchingDevice==null && other.getMspSwitchingDevice()==null) || 
             (this.mspSwitchingDevice!=null &&
              this.mspSwitchingDevice.equals(other.getMspSwitchingDevice()))) &&
            ((this.outageDetectionEvent==null && other.getOutageDetectionEvent()==null) || 
             (this.outageDetectionEvent!=null &&
              this.outageDetectionEvent.equals(other.getOutageDetectionEvent()))) &&
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.documentType==null && other.getDocumentType()==null) || 
             (this.documentType!=null &&
              this.documentType.equals(other.getDocumentType()))) &&
            ((this.connectivityModel==null && other.getConnectivityModel()==null) || 
             (this.connectivityModel!=null &&
              this.connectivityModel.equals(other.getConnectivityModel())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSwitchingStep() != null) {
            _hashCode += getSwitchingStep().hashCode();
        }
        if (getPowerFactorManagementEvent() != null) {
            _hashCode += getPowerFactorManagementEvent().hashCode();
        }
        if (getProfileObject() != null) {
            _hashCode += getProfileObject().hashCode();
        }
        if (getReadingObject() != null) {
            _hashCode += getReadingObject().hashCode();
        }
        if (getReadingSchedule() != null) {
            _hashCode += getReadingSchedule().hashCode();
        }
        if (getRegulator() != null) {
            _hashCode += getRegulator().hashCode();
        }
        if (getScadaAnalog() != null) {
            _hashCode += getScadaAnalog().hashCode();
        }
        if (getScadaPoints() != null) {
            _hashCode += getScadaPoints().hashCode();
        }
        if (getScadaStatus() != null) {
            _hashCode += getScadaStatus().hashCode();
        }
        if (getSchedule() != null) {
            _hashCode += getSchedule().hashCode();
        }
        if (getCrew() != null) {
            _hashCode += getCrew().hashCode();
        }
        if (getOutageLocation() != null) {
            _hashCode += getOutageLocation().hashCode();
        }
        if (getSecurityLight() != null) {
            _hashCode += getSecurityLight().hashCode();
        }
        if (getOutageEventStatus() != null) {
            _hashCode += getOutageEventStatus().hashCode();
        }
        if (getSpatialFeatureGroup() != null) {
            _hashCode += getSpatialFeatureGroup().hashCode();
        }
        if (getSubstationLoadControlStatus() != null) {
            _hashCode += getSubstationLoadControlStatus().hashCode();
        }
        if (getSwitchingOrder() != null) {
            _hashCode += getSwitchingOrder().hashCode();
        }
        if (getSwitchingSchedule() != null) {
            _hashCode += getSwitchingSchedule().hashCode();
        }
        if (getTimesheet() != null) {
            _hashCode += getTimesheet().hashCode();
        }
        if (getTrafficLight() != null) {
            _hashCode += getTrafficLight().hashCode();
        }
        if (getTransformer() != null) {
            _hashCode += getTransformer().hashCode();
        }
        if (getTruck() != null) {
            _hashCode += getTruck().hashCode();
        }
        if (getUsage() != null) {
            _hashCode += getUsage().hashCode();
        }
        if (getWorkOrder() != null) {
            _hashCode += getWorkOrder().hashCode();
        }
        if (getWorkOrderSelection() != null) {
            _hashCode += getWorkOrderSelection().hashCode();
        }
        if (getWorkTicket() != null) {
            _hashCode += getWorkTicket().hashCode();
        }
        if (getEquipment() != null) {
            _hashCode += getEquipment().hashCode();
        }
        if (getFeederObject() != null) {
            _hashCode += getFeederObject().hashCode();
        }
        if (getOutageEvent() != null) {
            _hashCode += getOutageEvent().hashCode();
        }
        if (getAssessment() != null) {
            _hashCode += getAssessment().hashCode();
        }
        if (getPowerMonitor() != null) {
            _hashCode += getPowerMonitor().hashCode();
        }
        if (getAssessmentLocation() != null) {
            _hashCode += getAssessmentLocation().hashCode();
        }
        if (getBackgroundGraphics() != null) {
            _hashCode += getBackgroundGraphics().hashCode();
        }
        if (getBillingAccountLoad() != null) {
            _hashCode += getBillingAccountLoad().hashCode();
        }
        if (getCallBackList() != null) {
            _hashCode += getCallBackList().hashCode();
        }
        if (getCapacitor() != null) {
            _hashCode += getCapacitor().hashCode();
        }
        if (getCircuitElement() != null) {
            _hashCode += getCircuitElement().hashCode();
        }
        if (getConfigurationGroup() != null) {
            _hashCode += getConfigurationGroup().hashCode();
        }
        if (getConnectDisconnectEvent() != null) {
            _hashCode += getConnectDisconnectEvent().hashCode();
        }
        if (getConnectDisconnectList() != null) {
            _hashCode += getConnectDisconnectList().hashCode();
        }
        if (getServiceLocations() != null) {
            _hashCode += getServiceLocations().hashCode();
        }
        if (getCrewActionEvent() != null) {
            _hashCode += getCrewActionEvent().hashCode();
        }
        if (getCustomer() != null) {
            _hashCode += getCustomer().hashCode();
        }
        if (getCustomerCall() != null) {
            _hashCode += getCustomerCall().hashCode();
        }
        if (getCustomersAffectedbyOutage() != null) {
            _hashCode += getCustomersAffectedbyOutage().hashCode();
        }
        if (getCustomersAttachedToDevice() != null) {
            _hashCode += getCustomersAttachedToDevice().hashCode();
        }
        if (getEmployee() != null) {
            _hashCode += getEmployee().hashCode();
        }
        if (getEmployeeTimeRecord() != null) {
            _hashCode += getEmployeeTimeRecord().hashCode();
        }
        if (getCDState() != null) {
            _hashCode += getCDState().hashCode();
        }
        if (getGpsLocation() != null) {
            _hashCode += getGpsLocation().hashCode();
        }
        if (getHistoryLog() != null) {
            _hashCode += getHistoryLog().hashCode();
        }
        if (getInHomeDisplay() != null) {
            _hashCode += getInHomeDisplay().hashCode();
        }
        if (getInHomeDisplayBillingMessage() != null) {
            _hashCode += getInHomeDisplayBillingMessage().hashCode();
        }
        if (getInHomeDisplayExchange() != null) {
            _hashCode += getInHomeDisplayExchange().hashCode();
        }
        if (getInHomeDisplayGroup() != null) {
            _hashCode += getInHomeDisplayGroup().hashCode();
        }
        if (getInHomeDisplayMessage() != null) {
            _hashCode += getInHomeDisplayMessage().hashCode();
        }
        if (getLaborCategory() != null) {
            _hashCode += getLaborCategory().hashCode();
        }
        if (getLoadManagementEvent() != null) {
            _hashCode += getLoadManagementEvent().hashCode();
        }
        if (getMaterialItem() != null) {
            _hashCode += getMaterialItem().hashCode();
        }
        if (getMaterialManagementAssembly() != null) {
            _hashCode += getMaterialManagementAssembly().hashCode();
        }
        if (getMeasurement() != null) {
            _hashCode += getMeasurement().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getMeterConnectivity() != null) {
            _hashCode += getMeterConnectivity().hashCode();
        }
        if (getMeterGroup() != null) {
            _hashCode += getMeterGroup().hashCode();
        }
        if (getMeterGroups() != null) {
            _hashCode += getMeterGroups().hashCode();
        }
        if (getMeterRead() != null) {
            _hashCode += getMeterRead().hashCode();
        }
        if (getMspDevice() != null) {
            _hashCode += getMspDevice().hashCode();
        }
        if (getMspLineObject() != null) {
            _hashCode += getMspLineObject().hashCode();
        }
        if (getMspSwitchingDevice() != null) {
            _hashCode += getMspSwitchingDevice().hashCode();
        }
        if (getOutageDetectionEvent() != null) {
            _hashCode += getOutageDetectionEvent().hashCode();
        }
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getDocumentType() != null) {
            _hashCode += getDocumentType().hashCode();
        }
        if (getConnectivityModel() != null) {
            _hashCode += getConnectivityModel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MultiSpeak.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeak"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("documentType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "documentType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "documentType"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("connectivityModel");
        attrField.setXmlName(new javax.xml.namespace.QName("", "connectivityModel"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectivityModel"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchingStep");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerFactorManagementEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("profileObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingSchedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regulator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaAnalog");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaPoints");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoints"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "schedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "schedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crew");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crew"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crew"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("securityLight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("spatialFeatureGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spatialFeatureGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spatialFeatureGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationLoadControlStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationLoadControlStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchingOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingOrder"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchingSchedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingSchedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingSchedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timesheet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timesheet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timesheet"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trafficLight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "trafficLight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "trafficLight"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("truck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrder"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workOrderSelection");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrderSelection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrderSelection"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workTicket");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workTicket"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workTicket"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assessment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerMonitor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerMonitor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerMonitor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assessmentLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("backgroundGraphics");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingAccountLoad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capacitor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configurationGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectDisconnectEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectDisconnectList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocations"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewActionEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewActionEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewActionEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerCall");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customersAffectedbyOutage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedbyOutage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedByOutage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customersAttachedToDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAttachedToDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAttachedToDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeTimeRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CDState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDState"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gpsLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("historyLog");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplay");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayBillingMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayBillingMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayBillingMessage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayExchange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayExchange"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayMessage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategory"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadManagementEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialManagementAssembly");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialManagementAssembly"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialManagementAssembly"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterConnectivity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspLineObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLineObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLineObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspSwitchingDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectionEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
