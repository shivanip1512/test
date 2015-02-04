/**
 * OutageReasonCodeList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OutageReasonCodeList  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.OutageReason outageCause;

    private com.cannontech.multispeak.deploy.service.OutageReason equipmentFailure;

    private com.cannontech.multispeak.deploy.service.OutageReason voltageLevel;

    private com.cannontech.multispeak.deploy.service.OutageReason weatherCondition;

    private com.cannontech.multispeak.deploy.service.OutageReason systemCharacterization;

    private com.cannontech.multispeak.deploy.service.OutageReason responsibleSystem;

    private com.cannontech.multispeak.deploy.service.OutageReason outageCondition;

    private com.cannontech.multispeak.deploy.service.OutageReason interruptingDevice;

    private com.cannontech.multispeak.deploy.service.OutageReason interruptingDeviceInitiation;

    private com.cannontech.multispeak.deploy.service.OutageReason customerRestoration;

    public OutageReasonCodeList() {
    }

    public OutageReasonCodeList(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.OutageReason outageCause,
           com.cannontech.multispeak.deploy.service.OutageReason equipmentFailure,
           com.cannontech.multispeak.deploy.service.OutageReason voltageLevel,
           com.cannontech.multispeak.deploy.service.OutageReason weatherCondition,
           com.cannontech.multispeak.deploy.service.OutageReason systemCharacterization,
           com.cannontech.multispeak.deploy.service.OutageReason responsibleSystem,
           com.cannontech.multispeak.deploy.service.OutageReason outageCondition,
           com.cannontech.multispeak.deploy.service.OutageReason interruptingDevice,
           com.cannontech.multispeak.deploy.service.OutageReason interruptingDeviceInitiation,
           com.cannontech.multispeak.deploy.service.OutageReason customerRestoration) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.outageCause = outageCause;
        this.equipmentFailure = equipmentFailure;
        this.voltageLevel = voltageLevel;
        this.weatherCondition = weatherCondition;
        this.systemCharacterization = systemCharacterization;
        this.responsibleSystem = responsibleSystem;
        this.outageCondition = outageCondition;
        this.interruptingDevice = interruptingDevice;
        this.interruptingDeviceInitiation = interruptingDeviceInitiation;
        this.customerRestoration = customerRestoration;
    }


    /**
     * Gets the outageCause value for this OutageReasonCodeList.
     * 
     * @return outageCause
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getOutageCause() {
        return outageCause;
    }


    /**
     * Sets the outageCause value for this OutageReasonCodeList.
     * 
     * @param outageCause
     */
    public void setOutageCause(com.cannontech.multispeak.deploy.service.OutageReason outageCause) {
        this.outageCause = outageCause;
    }


    /**
     * Gets the equipmentFailure value for this OutageReasonCodeList.
     * 
     * @return equipmentFailure
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getEquipmentFailure() {
        return equipmentFailure;
    }


    /**
     * Sets the equipmentFailure value for this OutageReasonCodeList.
     * 
     * @param equipmentFailure
     */
    public void setEquipmentFailure(com.cannontech.multispeak.deploy.service.OutageReason equipmentFailure) {
        this.equipmentFailure = equipmentFailure;
    }


    /**
     * Gets the voltageLevel value for this OutageReasonCodeList.
     * 
     * @return voltageLevel
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getVoltageLevel() {
        return voltageLevel;
    }


    /**
     * Sets the voltageLevel value for this OutageReasonCodeList.
     * 
     * @param voltageLevel
     */
    public void setVoltageLevel(com.cannontech.multispeak.deploy.service.OutageReason voltageLevel) {
        this.voltageLevel = voltageLevel;
    }


    /**
     * Gets the weatherCondition value for this OutageReasonCodeList.
     * 
     * @return weatherCondition
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getWeatherCondition() {
        return weatherCondition;
    }


    /**
     * Sets the weatherCondition value for this OutageReasonCodeList.
     * 
     * @param weatherCondition
     */
    public void setWeatherCondition(com.cannontech.multispeak.deploy.service.OutageReason weatherCondition) {
        this.weatherCondition = weatherCondition;
    }


    /**
     * Gets the systemCharacterization value for this OutageReasonCodeList.
     * 
     * @return systemCharacterization
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getSystemCharacterization() {
        return systemCharacterization;
    }


    /**
     * Sets the systemCharacterization value for this OutageReasonCodeList.
     * 
     * @param systemCharacterization
     */
    public void setSystemCharacterization(com.cannontech.multispeak.deploy.service.OutageReason systemCharacterization) {
        this.systemCharacterization = systemCharacterization;
    }


    /**
     * Gets the responsibleSystem value for this OutageReasonCodeList.
     * 
     * @return responsibleSystem
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getResponsibleSystem() {
        return responsibleSystem;
    }


    /**
     * Sets the responsibleSystem value for this OutageReasonCodeList.
     * 
     * @param responsibleSystem
     */
    public void setResponsibleSystem(com.cannontech.multispeak.deploy.service.OutageReason responsibleSystem) {
        this.responsibleSystem = responsibleSystem;
    }


    /**
     * Gets the outageCondition value for this OutageReasonCodeList.
     * 
     * @return outageCondition
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getOutageCondition() {
        return outageCondition;
    }


    /**
     * Sets the outageCondition value for this OutageReasonCodeList.
     * 
     * @param outageCondition
     */
    public void setOutageCondition(com.cannontech.multispeak.deploy.service.OutageReason outageCondition) {
        this.outageCondition = outageCondition;
    }


    /**
     * Gets the interruptingDevice value for this OutageReasonCodeList.
     * 
     * @return interruptingDevice
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getInterruptingDevice() {
        return interruptingDevice;
    }


    /**
     * Sets the interruptingDevice value for this OutageReasonCodeList.
     * 
     * @param interruptingDevice
     */
    public void setInterruptingDevice(com.cannontech.multispeak.deploy.service.OutageReason interruptingDevice) {
        this.interruptingDevice = interruptingDevice;
    }


    /**
     * Gets the interruptingDeviceInitiation value for this OutageReasonCodeList.
     * 
     * @return interruptingDeviceInitiation
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getInterruptingDeviceInitiation() {
        return interruptingDeviceInitiation;
    }


    /**
     * Sets the interruptingDeviceInitiation value for this OutageReasonCodeList.
     * 
     * @param interruptingDeviceInitiation
     */
    public void setInterruptingDeviceInitiation(com.cannontech.multispeak.deploy.service.OutageReason interruptingDeviceInitiation) {
        this.interruptingDeviceInitiation = interruptingDeviceInitiation;
    }


    /**
     * Gets the customerRestoration value for this OutageReasonCodeList.
     * 
     * @return customerRestoration
     */
    public com.cannontech.multispeak.deploy.service.OutageReason getCustomerRestoration() {
        return customerRestoration;
    }


    /**
     * Sets the customerRestoration value for this OutageReasonCodeList.
     * 
     * @param customerRestoration
     */
    public void setCustomerRestoration(com.cannontech.multispeak.deploy.service.OutageReason customerRestoration) {
        this.customerRestoration = customerRestoration;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageReasonCodeList)) return false;
        OutageReasonCodeList other = (OutageReasonCodeList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageCause==null && other.getOutageCause()==null) || 
             (this.outageCause!=null &&
              this.outageCause.equals(other.getOutageCause()))) &&
            ((this.equipmentFailure==null && other.getEquipmentFailure()==null) || 
             (this.equipmentFailure!=null &&
              this.equipmentFailure.equals(other.getEquipmentFailure()))) &&
            ((this.voltageLevel==null && other.getVoltageLevel()==null) || 
             (this.voltageLevel!=null &&
              this.voltageLevel.equals(other.getVoltageLevel()))) &&
            ((this.weatherCondition==null && other.getWeatherCondition()==null) || 
             (this.weatherCondition!=null &&
              this.weatherCondition.equals(other.getWeatherCondition()))) &&
            ((this.systemCharacterization==null && other.getSystemCharacterization()==null) || 
             (this.systemCharacterization!=null &&
              this.systemCharacterization.equals(other.getSystemCharacterization()))) &&
            ((this.responsibleSystem==null && other.getResponsibleSystem()==null) || 
             (this.responsibleSystem!=null &&
              this.responsibleSystem.equals(other.getResponsibleSystem()))) &&
            ((this.outageCondition==null && other.getOutageCondition()==null) || 
             (this.outageCondition!=null &&
              this.outageCondition.equals(other.getOutageCondition()))) &&
            ((this.interruptingDevice==null && other.getInterruptingDevice()==null) || 
             (this.interruptingDevice!=null &&
              this.interruptingDevice.equals(other.getInterruptingDevice()))) &&
            ((this.interruptingDeviceInitiation==null && other.getInterruptingDeviceInitiation()==null) || 
             (this.interruptingDeviceInitiation!=null &&
              this.interruptingDeviceInitiation.equals(other.getInterruptingDeviceInitiation()))) &&
            ((this.customerRestoration==null && other.getCustomerRestoration()==null) || 
             (this.customerRestoration!=null &&
              this.customerRestoration.equals(other.getCustomerRestoration())));
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
        if (getOutageCause() != null) {
            _hashCode += getOutageCause().hashCode();
        }
        if (getEquipmentFailure() != null) {
            _hashCode += getEquipmentFailure().hashCode();
        }
        if (getVoltageLevel() != null) {
            _hashCode += getVoltageLevel().hashCode();
        }
        if (getWeatherCondition() != null) {
            _hashCode += getWeatherCondition().hashCode();
        }
        if (getSystemCharacterization() != null) {
            _hashCode += getSystemCharacterization().hashCode();
        }
        if (getResponsibleSystem() != null) {
            _hashCode += getResponsibleSystem().hashCode();
        }
        if (getOutageCondition() != null) {
            _hashCode += getOutageCondition().hashCode();
        }
        if (getInterruptingDevice() != null) {
            _hashCode += getInterruptingDevice().hashCode();
        }
        if (getInterruptingDeviceInitiation() != null) {
            _hashCode += getInterruptingDeviceInitiation().hashCode();
        }
        if (getCustomerRestoration() != null) {
            _hashCode += getCustomerRestoration().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageReasonCodeList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonCodeList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageCause");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OutageCause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipmentFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EquipmentFailure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voltageLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "VoltageLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("weatherCondition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "WeatherCondition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("systemCharacterization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SystemCharacterization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responsibleSystem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ResponsibleSystem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageCondition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OutageCondition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interruptingDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InterruptingDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interruptingDeviceInitiation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InterruptingDeviceInitiation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerRestoration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CustomerRestoration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReason"));
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
