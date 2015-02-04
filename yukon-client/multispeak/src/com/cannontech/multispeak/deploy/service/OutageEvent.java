/**
 * OutageEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OutageEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String area;

    private java.lang.String problemLocation;

    private java.lang.String deviceID;

    private java.lang.String deviceType;

    private java.lang.String substationCode;

    private java.lang.String feeder;

    private java.lang.String actualFault;

    private java.lang.String faultType;

    private java.lang.String outageDescription;

    private com.cannontech.multispeak.deploy.service.OutageStatus outageStatus;

    private java.lang.Long priority;

    private java.util.Calendar startTime;

    private java.util.Calendar outageDefined;

    private java.util.Calendar firstDispatch;

    private java.util.Calendar firstETA;

    private java.util.Calendar firstArrival;

    private java.util.Calendar ETOR;

    private java.util.Calendar completed;

    private com.cannontech.multispeak.deploy.service.Message message;

    private java.lang.String[] crewsDispatched;

    private java.lang.Boolean isPlanned;

    private com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase;

    private java.math.BigInteger customersAffected;

    private java.math.BigInteger priorityCustomersCount;

    private java.math.BigInteger ODEventCount;

    private java.math.BigInteger customersRestored;

    private java.lang.Boolean isCustomerResponsible;

    private com.cannontech.multispeak.deploy.service.OutageReasonCodeList outageReasonCodeList;

    public OutageEvent() {
    }

    public OutageEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String area,
           java.lang.String problemLocation,
           java.lang.String deviceID,
           java.lang.String deviceType,
           java.lang.String substationCode,
           java.lang.String feeder,
           java.lang.String actualFault,
           java.lang.String faultType,
           java.lang.String outageDescription,
           com.cannontech.multispeak.deploy.service.OutageStatus outageStatus,
           java.lang.Long priority,
           java.util.Calendar startTime,
           java.util.Calendar outageDefined,
           java.util.Calendar firstDispatch,
           java.util.Calendar firstETA,
           java.util.Calendar firstArrival,
           java.util.Calendar ETOR,
           java.util.Calendar completed,
           com.cannontech.multispeak.deploy.service.Message message,
           java.lang.String[] crewsDispatched,
           java.lang.Boolean isPlanned,
           com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase,
           java.math.BigInteger customersAffected,
           java.math.BigInteger priorityCustomersCount,
           java.math.BigInteger ODEventCount,
           java.math.BigInteger customersRestored,
           java.lang.Boolean isCustomerResponsible,
           com.cannontech.multispeak.deploy.service.OutageReasonCodeList outageReasonCodeList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.area = area;
        this.problemLocation = problemLocation;
        this.deviceID = deviceID;
        this.deviceType = deviceType;
        this.substationCode = substationCode;
        this.feeder = feeder;
        this.actualFault = actualFault;
        this.faultType = faultType;
        this.outageDescription = outageDescription;
        this.outageStatus = outageStatus;
        this.priority = priority;
        this.startTime = startTime;
        this.outageDefined = outageDefined;
        this.firstDispatch = firstDispatch;
        this.firstETA = firstETA;
        this.firstArrival = firstArrival;
        this.ETOR = ETOR;
        this.completed = completed;
        this.message = message;
        this.crewsDispatched = crewsDispatched;
        this.isPlanned = isPlanned;
        this.outagedPhase = outagedPhase;
        this.customersAffected = customersAffected;
        this.priorityCustomersCount = priorityCustomersCount;
        this.ODEventCount = ODEventCount;
        this.customersRestored = customersRestored;
        this.isCustomerResponsible = isCustomerResponsible;
        this.outageReasonCodeList = outageReasonCodeList;
    }


    /**
     * Gets the area value for this OutageEvent.
     * 
     * @return area
     */
    public java.lang.String getArea() {
        return area;
    }


    /**
     * Sets the area value for this OutageEvent.
     * 
     * @param area
     */
    public void setArea(java.lang.String area) {
        this.area = area;
    }


    /**
     * Gets the problemLocation value for this OutageEvent.
     * 
     * @return problemLocation
     */
    public java.lang.String getProblemLocation() {
        return problemLocation;
    }


    /**
     * Sets the problemLocation value for this OutageEvent.
     * 
     * @param problemLocation
     */
    public void setProblemLocation(java.lang.String problemLocation) {
        this.problemLocation = problemLocation;
    }


    /**
     * Gets the deviceID value for this OutageEvent.
     * 
     * @return deviceID
     */
    public java.lang.String getDeviceID() {
        return deviceID;
    }


    /**
     * Sets the deviceID value for this OutageEvent.
     * 
     * @param deviceID
     */
    public void setDeviceID(java.lang.String deviceID) {
        this.deviceID = deviceID;
    }


    /**
     * Gets the deviceType value for this OutageEvent.
     * 
     * @return deviceType
     */
    public java.lang.String getDeviceType() {
        return deviceType;
    }


    /**
     * Sets the deviceType value for this OutageEvent.
     * 
     * @param deviceType
     */
    public void setDeviceType(java.lang.String deviceType) {
        this.deviceType = deviceType;
    }


    /**
     * Gets the substationCode value for this OutageEvent.
     * 
     * @return substationCode
     */
    public java.lang.String getSubstationCode() {
        return substationCode;
    }


    /**
     * Sets the substationCode value for this OutageEvent.
     * 
     * @param substationCode
     */
    public void setSubstationCode(java.lang.String substationCode) {
        this.substationCode = substationCode;
    }


    /**
     * Gets the feeder value for this OutageEvent.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this OutageEvent.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the actualFault value for this OutageEvent.
     * 
     * @return actualFault
     */
    public java.lang.String getActualFault() {
        return actualFault;
    }


    /**
     * Sets the actualFault value for this OutageEvent.
     * 
     * @param actualFault
     */
    public void setActualFault(java.lang.String actualFault) {
        this.actualFault = actualFault;
    }


    /**
     * Gets the faultType value for this OutageEvent.
     * 
     * @return faultType
     */
    public java.lang.String getFaultType() {
        return faultType;
    }


    /**
     * Sets the faultType value for this OutageEvent.
     * 
     * @param faultType
     */
    public void setFaultType(java.lang.String faultType) {
        this.faultType = faultType;
    }


    /**
     * Gets the outageDescription value for this OutageEvent.
     * 
     * @return outageDescription
     */
    public java.lang.String getOutageDescription() {
        return outageDescription;
    }


    /**
     * Sets the outageDescription value for this OutageEvent.
     * 
     * @param outageDescription
     */
    public void setOutageDescription(java.lang.String outageDescription) {
        this.outageDescription = outageDescription;
    }


    /**
     * Gets the outageStatus value for this OutageEvent.
     * 
     * @return outageStatus
     */
    public com.cannontech.multispeak.deploy.service.OutageStatus getOutageStatus() {
        return outageStatus;
    }


    /**
     * Sets the outageStatus value for this OutageEvent.
     * 
     * @param outageStatus
     */
    public void setOutageStatus(com.cannontech.multispeak.deploy.service.OutageStatus outageStatus) {
        this.outageStatus = outageStatus;
    }


    /**
     * Gets the priority value for this OutageEvent.
     * 
     * @return priority
     */
    public java.lang.Long getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this OutageEvent.
     * 
     * @param priority
     */
    public void setPriority(java.lang.Long priority) {
        this.priority = priority;
    }


    /**
     * Gets the startTime value for this OutageEvent.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this OutageEvent.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }


    /**
     * Gets the outageDefined value for this OutageEvent.
     * 
     * @return outageDefined
     */
    public java.util.Calendar getOutageDefined() {
        return outageDefined;
    }


    /**
     * Sets the outageDefined value for this OutageEvent.
     * 
     * @param outageDefined
     */
    public void setOutageDefined(java.util.Calendar outageDefined) {
        this.outageDefined = outageDefined;
    }


    /**
     * Gets the firstDispatch value for this OutageEvent.
     * 
     * @return firstDispatch
     */
    public java.util.Calendar getFirstDispatch() {
        return firstDispatch;
    }


    /**
     * Sets the firstDispatch value for this OutageEvent.
     * 
     * @param firstDispatch
     */
    public void setFirstDispatch(java.util.Calendar firstDispatch) {
        this.firstDispatch = firstDispatch;
    }


    /**
     * Gets the firstETA value for this OutageEvent.
     * 
     * @return firstETA
     */
    public java.util.Calendar getFirstETA() {
        return firstETA;
    }


    /**
     * Sets the firstETA value for this OutageEvent.
     * 
     * @param firstETA
     */
    public void setFirstETA(java.util.Calendar firstETA) {
        this.firstETA = firstETA;
    }


    /**
     * Gets the firstArrival value for this OutageEvent.
     * 
     * @return firstArrival
     */
    public java.util.Calendar getFirstArrival() {
        return firstArrival;
    }


    /**
     * Sets the firstArrival value for this OutageEvent.
     * 
     * @param firstArrival
     */
    public void setFirstArrival(java.util.Calendar firstArrival) {
        this.firstArrival = firstArrival;
    }


    /**
     * Gets the ETOR value for this OutageEvent.
     * 
     * @return ETOR
     */
    public java.util.Calendar getETOR() {
        return ETOR;
    }


    /**
     * Sets the ETOR value for this OutageEvent.
     * 
     * @param ETOR
     */
    public void setETOR(java.util.Calendar ETOR) {
        this.ETOR = ETOR;
    }


    /**
     * Gets the completed value for this OutageEvent.
     * 
     * @return completed
     */
    public java.util.Calendar getCompleted() {
        return completed;
    }


    /**
     * Sets the completed value for this OutageEvent.
     * 
     * @param completed
     */
    public void setCompleted(java.util.Calendar completed) {
        this.completed = completed;
    }


    /**
     * Gets the message value for this OutageEvent.
     * 
     * @return message
     */
    public com.cannontech.multispeak.deploy.service.Message getMessage() {
        return message;
    }


    /**
     * Sets the message value for this OutageEvent.
     * 
     * @param message
     */
    public void setMessage(com.cannontech.multispeak.deploy.service.Message message) {
        this.message = message;
    }


    /**
     * Gets the crewsDispatched value for this OutageEvent.
     * 
     * @return crewsDispatched
     */
    public java.lang.String[] getCrewsDispatched() {
        return crewsDispatched;
    }


    /**
     * Sets the crewsDispatched value for this OutageEvent.
     * 
     * @param crewsDispatched
     */
    public void setCrewsDispatched(java.lang.String[] crewsDispatched) {
        this.crewsDispatched = crewsDispatched;
    }


    /**
     * Gets the isPlanned value for this OutageEvent.
     * 
     * @return isPlanned
     */
    public java.lang.Boolean getIsPlanned() {
        return isPlanned;
    }


    /**
     * Sets the isPlanned value for this OutageEvent.
     * 
     * @param isPlanned
     */
    public void setIsPlanned(java.lang.Boolean isPlanned) {
        this.isPlanned = isPlanned;
    }


    /**
     * Gets the outagedPhase value for this OutageEvent.
     * 
     * @return outagedPhase
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getOutagedPhase() {
        return outagedPhase;
    }


    /**
     * Sets the outagedPhase value for this OutageEvent.
     * 
     * @param outagedPhase
     */
    public void setOutagedPhase(com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase) {
        this.outagedPhase = outagedPhase;
    }


    /**
     * Gets the customersAffected value for this OutageEvent.
     * 
     * @return customersAffected
     */
    public java.math.BigInteger getCustomersAffected() {
        return customersAffected;
    }


    /**
     * Sets the customersAffected value for this OutageEvent.
     * 
     * @param customersAffected
     */
    public void setCustomersAffected(java.math.BigInteger customersAffected) {
        this.customersAffected = customersAffected;
    }


    /**
     * Gets the priorityCustomersCount value for this OutageEvent.
     * 
     * @return priorityCustomersCount
     */
    public java.math.BigInteger getPriorityCustomersCount() {
        return priorityCustomersCount;
    }


    /**
     * Sets the priorityCustomersCount value for this OutageEvent.
     * 
     * @param priorityCustomersCount
     */
    public void setPriorityCustomersCount(java.math.BigInteger priorityCustomersCount) {
        this.priorityCustomersCount = priorityCustomersCount;
    }


    /**
     * Gets the ODEventCount value for this OutageEvent.
     * 
     * @return ODEventCount
     */
    public java.math.BigInteger getODEventCount() {
        return ODEventCount;
    }


    /**
     * Sets the ODEventCount value for this OutageEvent.
     * 
     * @param ODEventCount
     */
    public void setODEventCount(java.math.BigInteger ODEventCount) {
        this.ODEventCount = ODEventCount;
    }


    /**
     * Gets the customersRestored value for this OutageEvent.
     * 
     * @return customersRestored
     */
    public java.math.BigInteger getCustomersRestored() {
        return customersRestored;
    }


    /**
     * Sets the customersRestored value for this OutageEvent.
     * 
     * @param customersRestored
     */
    public void setCustomersRestored(java.math.BigInteger customersRestored) {
        this.customersRestored = customersRestored;
    }


    /**
     * Gets the isCustomerResponsible value for this OutageEvent.
     * 
     * @return isCustomerResponsible
     */
    public java.lang.Boolean getIsCustomerResponsible() {
        return isCustomerResponsible;
    }


    /**
     * Sets the isCustomerResponsible value for this OutageEvent.
     * 
     * @param isCustomerResponsible
     */
    public void setIsCustomerResponsible(java.lang.Boolean isCustomerResponsible) {
        this.isCustomerResponsible = isCustomerResponsible;
    }


    /**
     * Gets the outageReasonCodeList value for this OutageEvent.
     * 
     * @return outageReasonCodeList
     */
    public com.cannontech.multispeak.deploy.service.OutageReasonCodeList getOutageReasonCodeList() {
        return outageReasonCodeList;
    }


    /**
     * Sets the outageReasonCodeList value for this OutageEvent.
     * 
     * @param outageReasonCodeList
     */
    public void setOutageReasonCodeList(com.cannontech.multispeak.deploy.service.OutageReasonCodeList outageReasonCodeList) {
        this.outageReasonCodeList = outageReasonCodeList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageEvent)) return false;
        OutageEvent other = (OutageEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.area==null && other.getArea()==null) || 
             (this.area!=null &&
              this.area.equals(other.getArea()))) &&
            ((this.problemLocation==null && other.getProblemLocation()==null) || 
             (this.problemLocation!=null &&
              this.problemLocation.equals(other.getProblemLocation()))) &&
            ((this.deviceID==null && other.getDeviceID()==null) || 
             (this.deviceID!=null &&
              this.deviceID.equals(other.getDeviceID()))) &&
            ((this.deviceType==null && other.getDeviceType()==null) || 
             (this.deviceType!=null &&
              this.deviceType.equals(other.getDeviceType()))) &&
            ((this.substationCode==null && other.getSubstationCode()==null) || 
             (this.substationCode!=null &&
              this.substationCode.equals(other.getSubstationCode()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.actualFault==null && other.getActualFault()==null) || 
             (this.actualFault!=null &&
              this.actualFault.equals(other.getActualFault()))) &&
            ((this.faultType==null && other.getFaultType()==null) || 
             (this.faultType!=null &&
              this.faultType.equals(other.getFaultType()))) &&
            ((this.outageDescription==null && other.getOutageDescription()==null) || 
             (this.outageDescription!=null &&
              this.outageDescription.equals(other.getOutageDescription()))) &&
            ((this.outageStatus==null && other.getOutageStatus()==null) || 
             (this.outageStatus!=null &&
              this.outageStatus.equals(other.getOutageStatus()))) &&
            ((this.priority==null && other.getPriority()==null) || 
             (this.priority!=null &&
              this.priority.equals(other.getPriority()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            ((this.outageDefined==null && other.getOutageDefined()==null) || 
             (this.outageDefined!=null &&
              this.outageDefined.equals(other.getOutageDefined()))) &&
            ((this.firstDispatch==null && other.getFirstDispatch()==null) || 
             (this.firstDispatch!=null &&
              this.firstDispatch.equals(other.getFirstDispatch()))) &&
            ((this.firstETA==null && other.getFirstETA()==null) || 
             (this.firstETA!=null &&
              this.firstETA.equals(other.getFirstETA()))) &&
            ((this.firstArrival==null && other.getFirstArrival()==null) || 
             (this.firstArrival!=null &&
              this.firstArrival.equals(other.getFirstArrival()))) &&
            ((this.ETOR==null && other.getETOR()==null) || 
             (this.ETOR!=null &&
              this.ETOR.equals(other.getETOR()))) &&
            ((this.completed==null && other.getCompleted()==null) || 
             (this.completed!=null &&
              this.completed.equals(other.getCompleted()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.crewsDispatched==null && other.getCrewsDispatched()==null) || 
             (this.crewsDispatched!=null &&
              java.util.Arrays.equals(this.crewsDispatched, other.getCrewsDispatched()))) &&
            ((this.isPlanned==null && other.getIsPlanned()==null) || 
             (this.isPlanned!=null &&
              this.isPlanned.equals(other.getIsPlanned()))) &&
            ((this.outagedPhase==null && other.getOutagedPhase()==null) || 
             (this.outagedPhase!=null &&
              this.outagedPhase.equals(other.getOutagedPhase()))) &&
            ((this.customersAffected==null && other.getCustomersAffected()==null) || 
             (this.customersAffected!=null &&
              this.customersAffected.equals(other.getCustomersAffected()))) &&
            ((this.priorityCustomersCount==null && other.getPriorityCustomersCount()==null) || 
             (this.priorityCustomersCount!=null &&
              this.priorityCustomersCount.equals(other.getPriorityCustomersCount()))) &&
            ((this.ODEventCount==null && other.getODEventCount()==null) || 
             (this.ODEventCount!=null &&
              this.ODEventCount.equals(other.getODEventCount()))) &&
            ((this.customersRestored==null && other.getCustomersRestored()==null) || 
             (this.customersRestored!=null &&
              this.customersRestored.equals(other.getCustomersRestored()))) &&
            ((this.isCustomerResponsible==null && other.getIsCustomerResponsible()==null) || 
             (this.isCustomerResponsible!=null &&
              this.isCustomerResponsible.equals(other.getIsCustomerResponsible()))) &&
            ((this.outageReasonCodeList==null && other.getOutageReasonCodeList()==null) || 
             (this.outageReasonCodeList!=null &&
              this.outageReasonCodeList.equals(other.getOutageReasonCodeList())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getArea() != null) {
            _hashCode += getArea().hashCode();
        }
        if (getProblemLocation() != null) {
            _hashCode += getProblemLocation().hashCode();
        }
        if (getDeviceID() != null) {
            _hashCode += getDeviceID().hashCode();
        }
        if (getDeviceType() != null) {
            _hashCode += getDeviceType().hashCode();
        }
        if (getSubstationCode() != null) {
            _hashCode += getSubstationCode().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getActualFault() != null) {
            _hashCode += getActualFault().hashCode();
        }
        if (getFaultType() != null) {
            _hashCode += getFaultType().hashCode();
        }
        if (getOutageDescription() != null) {
            _hashCode += getOutageDescription().hashCode();
        }
        if (getOutageStatus() != null) {
            _hashCode += getOutageStatus().hashCode();
        }
        if (getPriority() != null) {
            _hashCode += getPriority().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        if (getOutageDefined() != null) {
            _hashCode += getOutageDefined().hashCode();
        }
        if (getFirstDispatch() != null) {
            _hashCode += getFirstDispatch().hashCode();
        }
        if (getFirstETA() != null) {
            _hashCode += getFirstETA().hashCode();
        }
        if (getFirstArrival() != null) {
            _hashCode += getFirstArrival().hashCode();
        }
        if (getETOR() != null) {
            _hashCode += getETOR().hashCode();
        }
        if (getCompleted() != null) {
            _hashCode += getCompleted().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getCrewsDispatched() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCrewsDispatched());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCrewsDispatched(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIsPlanned() != null) {
            _hashCode += getIsPlanned().hashCode();
        }
        if (getOutagedPhase() != null) {
            _hashCode += getOutagedPhase().hashCode();
        }
        if (getCustomersAffected() != null) {
            _hashCode += getCustomersAffected().hashCode();
        }
        if (getPriorityCustomersCount() != null) {
            _hashCode += getPriorityCustomersCount().hashCode();
        }
        if (getODEventCount() != null) {
            _hashCode += getODEventCount().hashCode();
        }
        if (getCustomersRestored() != null) {
            _hashCode += getCustomersRestored().hashCode();
        }
        if (getIsCustomerResponsible() != null) {
            _hashCode += getIsCustomerResponsible().hashCode();
        }
        if (getOutageReasonCodeList() != null) {
            _hashCode += getOutageReasonCodeList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("area");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "area"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("problemLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "problemLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actualFault");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actualFault"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faultType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "faultType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDefined");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDefined"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstDispatch");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstDispatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstETA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstETA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstArrival");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstArrival"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ETOR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ETOR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "completed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField.setFieldName("crewsDispatched");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewsDispatched"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewID"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isPlanned");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isPlanned"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outagedPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outagedPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customersAffected");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffected"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityCustomersCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityCustomersCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ODEventCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ODEventCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customersRestored");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersRestored"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isCustomerResponsible");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isCustomerResponsible"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageReasonCodeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonCodeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonCodeList"));
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
