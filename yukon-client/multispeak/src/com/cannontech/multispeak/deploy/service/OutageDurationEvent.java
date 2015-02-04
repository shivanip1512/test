/**
 * OutageDurationEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OutageDurationEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String outageEventID;

    private java.lang.String outageDescription;

    private java.lang.String meterNo;

    private java.lang.String servLoc;

    private java.lang.String accountNumber;

    private java.util.Calendar timeOfInterruption;

    private java.util.Calendar timeRestored;

    private java.math.BigInteger interruptionDuration;

    private java.lang.Boolean customerResponsible;

    private java.lang.String durationDisplay;

    public OutageDurationEvent() {
    }

    public OutageDurationEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String outageEventID,
           java.lang.String outageDescription,
           java.lang.String meterNo,
           java.lang.String servLoc,
           java.lang.String accountNumber,
           java.util.Calendar timeOfInterruption,
           java.util.Calendar timeRestored,
           java.math.BigInteger interruptionDuration,
           java.lang.Boolean customerResponsible,
           java.lang.String durationDisplay) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.outageEventID = outageEventID;
        this.outageDescription = outageDescription;
        this.meterNo = meterNo;
        this.servLoc = servLoc;
        this.accountNumber = accountNumber;
        this.timeOfInterruption = timeOfInterruption;
        this.timeRestored = timeRestored;
        this.interruptionDuration = interruptionDuration;
        this.customerResponsible = customerResponsible;
        this.durationDisplay = durationDisplay;
    }


    /**
     * Gets the outageEventID value for this OutageDurationEvent.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this OutageDurationEvent.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }


    /**
     * Gets the outageDescription value for this OutageDurationEvent.
     * 
     * @return outageDescription
     */
    public java.lang.String getOutageDescription() {
        return outageDescription;
    }


    /**
     * Sets the outageDescription value for this OutageDurationEvent.
     * 
     * @param outageDescription
     */
    public void setOutageDescription(java.lang.String outageDescription) {
        this.outageDescription = outageDescription;
    }


    /**
     * Gets the meterNo value for this OutageDurationEvent.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this OutageDurationEvent.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the servLoc value for this OutageDurationEvent.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this OutageDurationEvent.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the accountNumber value for this OutageDurationEvent.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this OutageDurationEvent.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the timeOfInterruption value for this OutageDurationEvent.
     * 
     * @return timeOfInterruption
     */
    public java.util.Calendar getTimeOfInterruption() {
        return timeOfInterruption;
    }


    /**
     * Sets the timeOfInterruption value for this OutageDurationEvent.
     * 
     * @param timeOfInterruption
     */
    public void setTimeOfInterruption(java.util.Calendar timeOfInterruption) {
        this.timeOfInterruption = timeOfInterruption;
    }


    /**
     * Gets the timeRestored value for this OutageDurationEvent.
     * 
     * @return timeRestored
     */
    public java.util.Calendar getTimeRestored() {
        return timeRestored;
    }


    /**
     * Sets the timeRestored value for this OutageDurationEvent.
     * 
     * @param timeRestored
     */
    public void setTimeRestored(java.util.Calendar timeRestored) {
        this.timeRestored = timeRestored;
    }


    /**
     * Gets the interruptionDuration value for this OutageDurationEvent.
     * 
     * @return interruptionDuration
     */
    public java.math.BigInteger getInterruptionDuration() {
        return interruptionDuration;
    }


    /**
     * Sets the interruptionDuration value for this OutageDurationEvent.
     * 
     * @param interruptionDuration
     */
    public void setInterruptionDuration(java.math.BigInteger interruptionDuration) {
        this.interruptionDuration = interruptionDuration;
    }


    /**
     * Gets the customerResponsible value for this OutageDurationEvent.
     * 
     * @return customerResponsible
     */
    public java.lang.Boolean getCustomerResponsible() {
        return customerResponsible;
    }


    /**
     * Sets the customerResponsible value for this OutageDurationEvent.
     * 
     * @param customerResponsible
     */
    public void setCustomerResponsible(java.lang.Boolean customerResponsible) {
        this.customerResponsible = customerResponsible;
    }


    /**
     * Gets the durationDisplay value for this OutageDurationEvent.
     * 
     * @return durationDisplay
     */
    public java.lang.String getDurationDisplay() {
        return durationDisplay;
    }


    /**
     * Sets the durationDisplay value for this OutageDurationEvent.
     * 
     * @param durationDisplay
     */
    public void setDurationDisplay(java.lang.String durationDisplay) {
        this.durationDisplay = durationDisplay;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageDurationEvent)) return false;
        OutageDurationEvent other = (OutageDurationEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageEventID==null && other.getOutageEventID()==null) || 
             (this.outageEventID!=null &&
              this.outageEventID.equals(other.getOutageEventID()))) &&
            ((this.outageDescription==null && other.getOutageDescription()==null) || 
             (this.outageDescription!=null &&
              this.outageDescription.equals(other.getOutageDescription()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.timeOfInterruption==null && other.getTimeOfInterruption()==null) || 
             (this.timeOfInterruption!=null &&
              this.timeOfInterruption.equals(other.getTimeOfInterruption()))) &&
            ((this.timeRestored==null && other.getTimeRestored()==null) || 
             (this.timeRestored!=null &&
              this.timeRestored.equals(other.getTimeRestored()))) &&
            ((this.interruptionDuration==null && other.getInterruptionDuration()==null) || 
             (this.interruptionDuration!=null &&
              this.interruptionDuration.equals(other.getInterruptionDuration()))) &&
            ((this.customerResponsible==null && other.getCustomerResponsible()==null) || 
             (this.customerResponsible!=null &&
              this.customerResponsible.equals(other.getCustomerResponsible()))) &&
            ((this.durationDisplay==null && other.getDurationDisplay()==null) || 
             (this.durationDisplay!=null &&
              this.durationDisplay.equals(other.getDurationDisplay())));
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
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        if (getOutageDescription() != null) {
            _hashCode += getOutageDescription().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getTimeOfInterruption() != null) {
            _hashCode += getTimeOfInterruption().hashCode();
        }
        if (getTimeRestored() != null) {
            _hashCode += getTimeRestored().hashCode();
        }
        if (getInterruptionDuration() != null) {
            _hashCode += getInterruptionDuration().hashCode();
        }
        if (getCustomerResponsible() != null) {
            _hashCode += getCustomerResponsible().hashCode();
        }
        if (getDurationDisplay() != null) {
            _hashCode += getDurationDisplay().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageDurationEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDurationEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
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
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfInterruption");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeOfInterruption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeRestored");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeRestored"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interruptionDuration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interruptionDuration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerResponsible");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerResponsible"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("durationDisplay");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "durationDisplay"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
