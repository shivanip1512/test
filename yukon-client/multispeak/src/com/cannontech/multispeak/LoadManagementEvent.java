/**
 * LoadManagementEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LoadManagementEvent  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String substationName;
    private java.lang.String feederName;
    private java.lang.String feederNumber;
    private java.lang.Long groupName;
    private com.cannontech.multispeak.ControlEventType controlEventType;
    private java.lang.Float applicationRate;
    private java.lang.Float duration;
    private java.lang.Float cycleTime;
    private java.lang.Long dutyCycle;
    private java.lang.Float controlLoad;

    public LoadManagementEvent() {
    }

    public LoadManagementEvent(
           java.lang.String substationName,
           java.lang.String feederName,
           java.lang.String feederNumber,
           java.lang.Long groupName,
           com.cannontech.multispeak.ControlEventType controlEventType,
           java.lang.Float applicationRate,
           java.lang.Float duration,
           java.lang.Float cycleTime,
           java.lang.Long dutyCycle,
           java.lang.Float controlLoad) {
           this.substationName = substationName;
           this.feederName = feederName;
           this.feederNumber = feederNumber;
           this.groupName = groupName;
           this.controlEventType = controlEventType;
           this.applicationRate = applicationRate;
           this.duration = duration;
           this.cycleTime = cycleTime;
           this.dutyCycle = dutyCycle;
           this.controlLoad = controlLoad;
    }


    /**
     * Gets the substationName value for this LoadManagementEvent.
     * 
     * @return substationName
     */
    public java.lang.String getSubstationName() {
        return substationName;
    }


    /**
     * Sets the substationName value for this LoadManagementEvent.
     * 
     * @param substationName
     */
    public void setSubstationName(java.lang.String substationName) {
        this.substationName = substationName;
    }


    /**
     * Gets the feederName value for this LoadManagementEvent.
     * 
     * @return feederName
     */
    public java.lang.String getFeederName() {
        return feederName;
    }


    /**
     * Sets the feederName value for this LoadManagementEvent.
     * 
     * @param feederName
     */
    public void setFeederName(java.lang.String feederName) {
        this.feederName = feederName;
    }


    /**
     * Gets the feederNumber value for this LoadManagementEvent.
     * 
     * @return feederNumber
     */
    public java.lang.String getFeederNumber() {
        return feederNumber;
    }


    /**
     * Sets the feederNumber value for this LoadManagementEvent.
     * 
     * @param feederNumber
     */
    public void setFeederNumber(java.lang.String feederNumber) {
        this.feederNumber = feederNumber;
    }


    /**
     * Gets the groupName value for this LoadManagementEvent.
     * 
     * @return groupName
     */
    public java.lang.Long getGroupName() {
        return groupName;
    }


    /**
     * Sets the groupName value for this LoadManagementEvent.
     * 
     * @param groupName
     */
    public void setGroupName(java.lang.Long groupName) {
        this.groupName = groupName;
    }


    /**
     * Gets the controlEventType value for this LoadManagementEvent.
     * 
     * @return controlEventType
     */
    public com.cannontech.multispeak.ControlEventType getControlEventType() {
        return controlEventType;
    }


    /**
     * Sets the controlEventType value for this LoadManagementEvent.
     * 
     * @param controlEventType
     */
    public void setControlEventType(com.cannontech.multispeak.ControlEventType controlEventType) {
        this.controlEventType = controlEventType;
    }


    /**
     * Gets the applicationRate value for this LoadManagementEvent.
     * 
     * @return applicationRate
     */
    public java.lang.Float getApplicationRate() {
        return applicationRate;
    }


    /**
     * Sets the applicationRate value for this LoadManagementEvent.
     * 
     * @param applicationRate
     */
    public void setApplicationRate(java.lang.Float applicationRate) {
        this.applicationRate = applicationRate;
    }


    /**
     * Gets the duration value for this LoadManagementEvent.
     * 
     * @return duration
     */
    public java.lang.Float getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this LoadManagementEvent.
     * 
     * @param duration
     */
    public void setDuration(java.lang.Float duration) {
        this.duration = duration;
    }


    /**
     * Gets the cycleTime value for this LoadManagementEvent.
     * 
     * @return cycleTime
     */
    public java.lang.Float getCycleTime() {
        return cycleTime;
    }


    /**
     * Sets the cycleTime value for this LoadManagementEvent.
     * 
     * @param cycleTime
     */
    public void setCycleTime(java.lang.Float cycleTime) {
        this.cycleTime = cycleTime;
    }


    /**
     * Gets the dutyCycle value for this LoadManagementEvent.
     * 
     * @return dutyCycle
     */
    public java.lang.Long getDutyCycle() {
        return dutyCycle;
    }


    /**
     * Sets the dutyCycle value for this LoadManagementEvent.
     * 
     * @param dutyCycle
     */
    public void setDutyCycle(java.lang.Long dutyCycle) {
        this.dutyCycle = dutyCycle;
    }


    /**
     * Gets the controlLoad value for this LoadManagementEvent.
     * 
     * @return controlLoad
     */
    public java.lang.Float getControlLoad() {
        return controlLoad;
    }


    /**
     * Sets the controlLoad value for this LoadManagementEvent.
     * 
     * @param controlLoad
     */
    public void setControlLoad(java.lang.Float controlLoad) {
        this.controlLoad = controlLoad;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadManagementEvent)) return false;
        LoadManagementEvent other = (LoadManagementEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.substationName==null && other.getSubstationName()==null) || 
             (this.substationName!=null &&
              this.substationName.equals(other.getSubstationName()))) &&
            ((this.feederName==null && other.getFeederName()==null) || 
             (this.feederName!=null &&
              this.feederName.equals(other.getFeederName()))) &&
            ((this.feederNumber==null && other.getFeederNumber()==null) || 
             (this.feederNumber!=null &&
              this.feederNumber.equals(other.getFeederNumber()))) &&
            ((this.groupName==null && other.getGroupName()==null) || 
             (this.groupName!=null &&
              this.groupName.equals(other.getGroupName()))) &&
            ((this.controlEventType==null && other.getControlEventType()==null) || 
             (this.controlEventType!=null &&
              this.controlEventType.equals(other.getControlEventType()))) &&
            ((this.applicationRate==null && other.getApplicationRate()==null) || 
             (this.applicationRate!=null &&
              this.applicationRate.equals(other.getApplicationRate()))) &&
            ((this.duration==null && other.getDuration()==null) || 
             (this.duration!=null &&
              this.duration.equals(other.getDuration()))) &&
            ((this.cycleTime==null && other.getCycleTime()==null) || 
             (this.cycleTime!=null &&
              this.cycleTime.equals(other.getCycleTime()))) &&
            ((this.dutyCycle==null && other.getDutyCycle()==null) || 
             (this.dutyCycle!=null &&
              this.dutyCycle.equals(other.getDutyCycle()))) &&
            ((this.controlLoad==null && other.getControlLoad()==null) || 
             (this.controlLoad!=null &&
              this.controlLoad.equals(other.getControlLoad())));
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
        if (getSubstationName() != null) {
            _hashCode += getSubstationName().hashCode();
        }
        if (getFeederName() != null) {
            _hashCode += getFeederName().hashCode();
        }
        if (getFeederNumber() != null) {
            _hashCode += getFeederNumber().hashCode();
        }
        if (getGroupName() != null) {
            _hashCode += getGroupName().hashCode();
        }
        if (getControlEventType() != null) {
            _hashCode += getControlEventType().hashCode();
        }
        if (getApplicationRate() != null) {
            _hashCode += getApplicationRate().hashCode();
        }
        if (getDuration() != null) {
            _hashCode += getDuration().hashCode();
        }
        if (getCycleTime() != null) {
            _hashCode += getCycleTime().hashCode();
        }
        if (getDutyCycle() != null) {
            _hashCode += getDutyCycle().hashCode();
        }
        if (getControlLoad() != null) {
            _hashCode += getControlLoad().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoadManagementEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "groupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlEventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicationRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cycleTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cycleTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dutyCycle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dutyCycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlLoad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlLoad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
