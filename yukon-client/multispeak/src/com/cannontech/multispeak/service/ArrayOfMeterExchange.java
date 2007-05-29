/**
 * ArrayOfMeterExchange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ArrayOfMeterExchange  implements java.io.Serializable {
    private com.cannontech.multispeak.service.MeterExchange[] meterExchange;

    public ArrayOfMeterExchange() {
    }

    public ArrayOfMeterExchange(
           com.cannontech.multispeak.service.MeterExchange[] meterExchange) {
           this.meterExchange = meterExchange;
    }


    /**
     * Gets the meterExchange value for this ArrayOfMeterExchange.
     * 
     * @return meterExchange
     */
    public com.cannontech.multispeak.service.MeterExchange[] getMeterExchange() {
        return meterExchange;
    }


    /**
     * Sets the meterExchange value for this ArrayOfMeterExchange.
     * 
     * @param meterExchange
     */
    public void setMeterExchange(com.cannontech.multispeak.service.MeterExchange[] meterExchange) {
        this.meterExchange = meterExchange;
    }

    public com.cannontech.multispeak.service.MeterExchange getMeterExchange(int i) {
        return this.meterExchange[i];
    }

    public void setMeterExchange(int i, com.cannontech.multispeak.service.MeterExchange _value) {
        this.meterExchange[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeterExchange)) return false;
        ArrayOfMeterExchange other = (ArrayOfMeterExchange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterExchange==null && other.getMeterExchange()==null) || 
             (this.meterExchange!=null &&
              java.util.Arrays.equals(this.meterExchange, other.getMeterExchange())));
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
        if (getMeterExchange() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterExchange());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterExchange(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMeterExchange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterExchange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterExchange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange"));
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
