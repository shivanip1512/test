/**
 * ArrayOfMeasurementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMeasurementType  implements java.io.Serializable {
    private com.cannontech.multispeak.MeasurementType[] measurementType;

    public ArrayOfMeasurementType() {
    }

    public ArrayOfMeasurementType(
           com.cannontech.multispeak.MeasurementType[] measurementType) {
           this.measurementType = measurementType;
    }


    /**
     * Gets the measurementType value for this ArrayOfMeasurementType.
     * 
     * @return measurementType
     */
    public com.cannontech.multispeak.MeasurementType[] getMeasurementType() {
        return measurementType;
    }


    /**
     * Sets the measurementType value for this ArrayOfMeasurementType.
     * 
     * @param measurementType
     */
    public void setMeasurementType(com.cannontech.multispeak.MeasurementType[] measurementType) {
        this.measurementType = measurementType;
    }

    public com.cannontech.multispeak.MeasurementType getMeasurementType(int i) {
        return this.measurementType[i];
    }

    public void setMeasurementType(int i, com.cannontech.multispeak.MeasurementType _value) {
        this.measurementType[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeasurementType)) return false;
        ArrayOfMeasurementType other = (ArrayOfMeasurementType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.measurementType==null && other.getMeasurementType()==null) || 
             (this.measurementType!=null &&
              java.util.Arrays.equals(this.measurementType, other.getMeasurementType())));
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
        if (getMeasurementType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeasurementType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeasurementType(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfMeasurementType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeasurementType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
