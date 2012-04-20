/**
 * LengthUnitValue.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LengthUnitValue  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private float _value;

    private com.cannontech.multispeak.deploy.service.LengthUnit lengthUnits;  // attribute

    public LengthUnitValue() {
    }

    // Simple Types must have a String constructor
    public LengthUnitValue(float _value) {
        this._value = _value;
    }
    public LengthUnitValue(java.lang.String _value) {
        this._value = new Float(_value).floatValue();
    }

    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return new Float(_value).toString();
    }


    /**
     * Gets the _value value for this LengthUnitValue.
     * 
     * @return _value
     */
    public float get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this LengthUnitValue.
     * 
     * @param _value
     */
    public void set_value(float _value) {
        this._value = _value;
    }


    /**
     * Gets the lengthUnits value for this LengthUnitValue.
     * 
     * @return lengthUnits
     */
    public com.cannontech.multispeak.deploy.service.LengthUnit getLengthUnits() {
        return lengthUnits;
    }


    /**
     * Sets the lengthUnits value for this LengthUnitValue.
     * 
     * @param lengthUnits
     */
    public void setLengthUnits(com.cannontech.multispeak.deploy.service.LengthUnit lengthUnits) {
        this.lengthUnits = lengthUnits;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LengthUnitValue)) return false;
        LengthUnitValue other = (LengthUnitValue) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this._value == other.get_value() &&
            ((this.lengthUnits==null && other.getLengthUnits()==null) || 
             (this.lengthUnits!=null &&
              this.lengthUnits.equals(other.getLengthUnits())));
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
        _hashCode += new Float(get_value()).hashCode();
        if (getLengthUnits() != null) {
            _hashCode += getLengthUnits().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LengthUnitValue.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnitValue"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("lengthUnits");
        attrField.setXmlName(new javax.xml.namespace.QName("", "lengthUnits"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
