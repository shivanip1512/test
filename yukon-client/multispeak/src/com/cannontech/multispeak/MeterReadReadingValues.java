/**
 * MeterReadReadingValues.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterReadReadingValues  implements java.io.Serializable {
    private com.cannontech.multispeak.ReadingValue[] readingValue;

    public MeterReadReadingValues() {
    }

    public MeterReadReadingValues(
           com.cannontech.multispeak.ReadingValue[] readingValue) {
           this.readingValue = readingValue;
    }


    /**
     * Gets the readingValue value for this MeterReadReadingValues.
     * 
     * @return readingValue
     */
    public com.cannontech.multispeak.ReadingValue[] getReadingValue() {
        return readingValue;
    }


    /**
     * Sets the readingValue value for this MeterReadReadingValues.
     * 
     * @param readingValue
     */
    public void setReadingValue(com.cannontech.multispeak.ReadingValue[] readingValue) {
        this.readingValue = readingValue;
    }

    public com.cannontech.multispeak.ReadingValue getReadingValue(int i) {
        return this.readingValue[i];
    }

    public void setReadingValue(int i, com.cannontech.multispeak.ReadingValue _value) {
        this.readingValue[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterReadReadingValues)) return false;
        MeterReadReadingValues other = (MeterReadReadingValues) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.readingValue==null && other.getReadingValue()==null) || 
             (this.readingValue!=null &&
              java.util.Arrays.equals(this.readingValue, other.getReadingValue())));
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
        if (getReadingValue() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReadingValue());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReadingValue(), i);
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
        new org.apache.axis.description.TypeDesc(MeterReadReadingValues.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReadReadingValues"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue"));
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
