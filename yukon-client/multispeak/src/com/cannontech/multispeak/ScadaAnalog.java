/**
 * ScadaAnalog.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ScadaAnalog  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.Float value;
    private com.cannontech.multispeak.Uom unit;
    private com.cannontech.multispeak.QualityDescription quality;
    private com.cannontech.multispeak.AnalogCondition analogCondition;
    private java.util.Calendar timeStamp;

    public ScadaAnalog() {
    }

    public ScadaAnalog(
           java.lang.Float value,
           com.cannontech.multispeak.Uom unit,
           com.cannontech.multispeak.QualityDescription quality,
           com.cannontech.multispeak.AnalogCondition analogCondition,
           java.util.Calendar timeStamp) {
           this.value = value;
           this.unit = unit;
           this.quality = quality;
           this.analogCondition = analogCondition;
           this.timeStamp = timeStamp;
    }


    /**
     * Gets the value value for this ScadaAnalog.
     * 
     * @return value
     */
    public java.lang.Float getValue() {
        return value;
    }


    /**
     * Sets the value value for this ScadaAnalog.
     * 
     * @param value
     */
    public void setValue(java.lang.Float value) {
        this.value = value;
    }


    /**
     * Gets the unit value for this ScadaAnalog.
     * 
     * @return unit
     */
    public com.cannontech.multispeak.Uom getUnit() {
        return unit;
    }


    /**
     * Sets the unit value for this ScadaAnalog.
     * 
     * @param unit
     */
    public void setUnit(com.cannontech.multispeak.Uom unit) {
        this.unit = unit;
    }


    /**
     * Gets the quality value for this ScadaAnalog.
     * 
     * @return quality
     */
    public com.cannontech.multispeak.QualityDescription getQuality() {
        return quality;
    }


    /**
     * Sets the quality value for this ScadaAnalog.
     * 
     * @param quality
     */
    public void setQuality(com.cannontech.multispeak.QualityDescription quality) {
        this.quality = quality;
    }


    /**
     * Gets the analogCondition value for this ScadaAnalog.
     * 
     * @return analogCondition
     */
    public com.cannontech.multispeak.AnalogCondition getAnalogCondition() {
        return analogCondition;
    }


    /**
     * Sets the analogCondition value for this ScadaAnalog.
     * 
     * @param analogCondition
     */
    public void setAnalogCondition(com.cannontech.multispeak.AnalogCondition analogCondition) {
        this.analogCondition = analogCondition;
    }


    /**
     * Gets the timeStamp value for this ScadaAnalog.
     * 
     * @return timeStamp
     */
    public java.util.Calendar getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this ScadaAnalog.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(java.util.Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScadaAnalog)) return false;
        ScadaAnalog other = (ScadaAnalog) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.unit==null && other.getUnit()==null) || 
             (this.unit!=null &&
              this.unit.equals(other.getUnit()))) &&
            ((this.quality==null && other.getQuality()==null) || 
             (this.quality!=null &&
              this.quality.equals(other.getQuality()))) &&
            ((this.analogCondition==null && other.getAnalogCondition()==null) || 
             (this.analogCondition!=null &&
              this.analogCondition.equals(other.getAnalogCondition()))) &&
            ((this.timeStamp==null && other.getTimeStamp()==null) || 
             (this.timeStamp!=null &&
              this.timeStamp.equals(other.getTimeStamp())));
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
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getUnit() != null) {
            _hashCode += getUnit().hashCode();
        }
        if (getQuality() != null) {
            _hashCode += getQuality().hashCode();
        }
        if (getAnalogCondition() != null) {
            _hashCode += getAnalogCondition().hashCode();
        }
        if (getTimeStamp() != null) {
            _hashCode += getTimeStamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScadaAnalog.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
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
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
