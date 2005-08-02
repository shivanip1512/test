/**
 * ArrayOfMeterReading.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMeterReading  implements java.io.Serializable {
    private com.cannontech.multispeak.MeterReading[] meterReading;

    public ArrayOfMeterReading() {
    }

    public ArrayOfMeterReading(
           com.cannontech.multispeak.MeterReading[] meterReading) {
           this.meterReading = meterReading;
    }


    /**
     * Gets the meterReading value for this ArrayOfMeterReading.
     * 
     * @return meterReading
     */
    public com.cannontech.multispeak.MeterReading[] getMeterReading() {
        return meterReading;
    }


    /**
     * Sets the meterReading value for this ArrayOfMeterReading.
     * 
     * @param meterReading
     */
    public void setMeterReading(com.cannontech.multispeak.MeterReading[] meterReading) {
        this.meterReading = meterReading;
    }

    public com.cannontech.multispeak.MeterReading getMeterReading(int i) {
        return this.meterReading[i];
    }

    public void setMeterReading(int i, com.cannontech.multispeak.MeterReading _value) {
        this.meterReading[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeterReading)) return false;
        ArrayOfMeterReading other = (ArrayOfMeterReading) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterReading==null && other.getMeterReading()==null) || 
             (this.meterReading!=null &&
              java.util.Arrays.equals(this.meterReading, other.getMeterReading())));
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
        if (getMeterReading() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterReading());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterReading(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMeterReading.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterReading"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading"));
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
