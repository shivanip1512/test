/**
 * VoltageAlarmItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class VoltageAlarmItem  implements java.io.Serializable {
    private java.lang.Float voltageValue;

    private com.cannontech.multispeak.deploy.service.UnitPrefix unitPrefix;

    private com.cannontech.multispeak.deploy.service.QualityDescription quality;

    private com.cannontech.multispeak.deploy.service.AnalogCondition analogCondition;

    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCode;

    public VoltageAlarmItem() {
    }

    public VoltageAlarmItem(
           java.lang.Float voltageValue,
           com.cannontech.multispeak.deploy.service.UnitPrefix unitPrefix,
           com.cannontech.multispeak.deploy.service.QualityDescription quality,
           com.cannontech.multispeak.deploy.service.AnalogCondition analogCondition,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode) {
           this.voltageValue = voltageValue;
           this.unitPrefix = unitPrefix;
           this.quality = quality;
           this.analogCondition = analogCondition;
           this.phaseCode = phaseCode;
    }


    /**
     * Gets the voltageValue value for this VoltageAlarmItem.
     * 
     * @return voltageValue
     */
    public java.lang.Float getVoltageValue() {
        return voltageValue;
    }


    /**
     * Sets the voltageValue value for this VoltageAlarmItem.
     * 
     * @param voltageValue
     */
    public void setVoltageValue(java.lang.Float voltageValue) {
        this.voltageValue = voltageValue;
    }


    /**
     * Gets the unitPrefix value for this VoltageAlarmItem.
     * 
     * @return unitPrefix
     */
    public com.cannontech.multispeak.deploy.service.UnitPrefix getUnitPrefix() {
        return unitPrefix;
    }


    /**
     * Sets the unitPrefix value for this VoltageAlarmItem.
     * 
     * @param unitPrefix
     */
    public void setUnitPrefix(com.cannontech.multispeak.deploy.service.UnitPrefix unitPrefix) {
        this.unitPrefix = unitPrefix;
    }


    /**
     * Gets the quality value for this VoltageAlarmItem.
     * 
     * @return quality
     */
    public com.cannontech.multispeak.deploy.service.QualityDescription getQuality() {
        return quality;
    }


    /**
     * Sets the quality value for this VoltageAlarmItem.
     * 
     * @param quality
     */
    public void setQuality(com.cannontech.multispeak.deploy.service.QualityDescription quality) {
        this.quality = quality;
    }


    /**
     * Gets the analogCondition value for this VoltageAlarmItem.
     * 
     * @return analogCondition
     */
    public com.cannontech.multispeak.deploy.service.AnalogCondition getAnalogCondition() {
        return analogCondition;
    }


    /**
     * Sets the analogCondition value for this VoltageAlarmItem.
     * 
     * @param analogCondition
     */
    public void setAnalogCondition(com.cannontech.multispeak.deploy.service.AnalogCondition analogCondition) {
        this.analogCondition = analogCondition;
    }


    /**
     * Gets the phaseCode value for this VoltageAlarmItem.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this VoltageAlarmItem.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoltageAlarmItem)) return false;
        VoltageAlarmItem other = (VoltageAlarmItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.voltageValue==null && other.getVoltageValue()==null) || 
             (this.voltageValue!=null &&
              this.voltageValue.equals(other.getVoltageValue()))) &&
            ((this.unitPrefix==null && other.getUnitPrefix()==null) || 
             (this.unitPrefix!=null &&
              this.unitPrefix.equals(other.getUnitPrefix()))) &&
            ((this.quality==null && other.getQuality()==null) || 
             (this.quality!=null &&
              this.quality.equals(other.getQuality()))) &&
            ((this.analogCondition==null && other.getAnalogCondition()==null) || 
             (this.analogCondition!=null &&
              this.analogCondition.equals(other.getAnalogCondition()))) &&
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode())));
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
        if (getVoltageValue() != null) {
            _hashCode += getVoltageValue().hashCode();
        }
        if (getUnitPrefix() != null) {
            _hashCode += getUnitPrefix().hashCode();
        }
        if (getQuality() != null) {
            _hashCode += getQuality().hashCode();
        }
        if (getAnalogCondition() != null) {
            _hashCode += getAnalogCondition().hashCode();
        }
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoltageAlarmItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voltageValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitPrefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("analogCondition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "analogCondition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "analogCondition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
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
