/**
 * ArrayOfMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.Meter[] meter;

    public ArrayOfMeter() {
    }

    public ArrayOfMeter(
           com.cannontech.multispeak.Meter[] meter) {
           this.meter = meter;
    }


    /**
     * Gets the meter value for this ArrayOfMeter.
     * 
     * @return meter
     */
    public com.cannontech.multispeak.Meter[] getMeter() {
        return meter;
    }


    /**
     * Sets the meter value for this ArrayOfMeter.
     * 
     * @param meter
     */
    public void setMeter(com.cannontech.multispeak.Meter[] meter) {
        this.meter = meter;
    }

    public com.cannontech.multispeak.Meter getMeter(int i) {
        return this.meter[i];
    }

    public void setMeter(int i, com.cannontech.multispeak.Meter _value) {
        this.meter[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeter)) return false;
        ArrayOfMeter other = (ArrayOfMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meter==null && other.getMeter()==null) || 
             (this.meter!=null &&
              java.util.Arrays.equals(this.meter, other.getMeter())));
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
        if (getMeter() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeter());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeter(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
