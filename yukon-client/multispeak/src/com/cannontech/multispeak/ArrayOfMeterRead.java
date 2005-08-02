/**
 * ArrayOfMeterRead.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMeterRead  implements java.io.Serializable {
    private com.cannontech.multispeak.MeterRead[] meterRead;

    public ArrayOfMeterRead() {
    }

    public ArrayOfMeterRead(
           com.cannontech.multispeak.MeterRead[] meterRead) {
           this.meterRead = meterRead;
    }


    /**
     * Gets the meterRead value for this ArrayOfMeterRead.
     * 
     * @return meterRead
     */
    public com.cannontech.multispeak.MeterRead[] getMeterRead() {
        return meterRead;
    }


    /**
     * Sets the meterRead value for this ArrayOfMeterRead.
     * 
     * @param meterRead
     */
    public void setMeterRead(com.cannontech.multispeak.MeterRead[] meterRead) {
        this.meterRead = meterRead;
    }

    public com.cannontech.multispeak.MeterRead getMeterRead(int i) {
        return this.meterRead[i];
    }

    public void setMeterRead(int i, com.cannontech.multispeak.MeterRead _value) {
        this.meterRead[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeterRead)) return false;
        ArrayOfMeterRead other = (ArrayOfMeterRead) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterRead==null && other.getMeterRead()==null) || 
             (this.meterRead!=null &&
              java.util.Arrays.equals(this.meterRead, other.getMeterRead())));
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
        if (getMeterRead() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterRead());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterRead(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMeterRead.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
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
