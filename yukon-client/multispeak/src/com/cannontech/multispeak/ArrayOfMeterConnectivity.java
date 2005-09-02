/**
 * ArrayOfMeterConnectivity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMeterConnectivity  implements java.io.Serializable {
    private com.cannontech.multispeak.MeterConnectivity[] meterConnectivity;

    public ArrayOfMeterConnectivity() {
    }

    public ArrayOfMeterConnectivity(
           com.cannontech.multispeak.MeterConnectivity[] meterConnectivity) {
           this.meterConnectivity = meterConnectivity;
    }


    /**
     * Gets the meterConnectivity value for this ArrayOfMeterConnectivity.
     * 
     * @return meterConnectivity
     */
    public com.cannontech.multispeak.MeterConnectivity[] getMeterConnectivity() {
        return meterConnectivity;
    }


    /**
     * Sets the meterConnectivity value for this ArrayOfMeterConnectivity.
     * 
     * @param meterConnectivity
     */
    public void setMeterConnectivity(com.cannontech.multispeak.MeterConnectivity[] meterConnectivity) {
        this.meterConnectivity = meterConnectivity;
    }

    public com.cannontech.multispeak.MeterConnectivity getMeterConnectivity(int i) {
        return this.meterConnectivity[i];
    }

    public void setMeterConnectivity(int i, com.cannontech.multispeak.MeterConnectivity _value) {
        this.meterConnectivity[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMeterConnectivity)) return false;
        ArrayOfMeterConnectivity other = (ArrayOfMeterConnectivity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterConnectivity==null && other.getMeterConnectivity()==null) || 
             (this.meterConnectivity!=null &&
              java.util.Arrays.equals(this.meterConnectivity, other.getMeterConnectivity())));
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
        if (getMeterConnectivity() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterConnectivity());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterConnectivity(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMeterConnectivity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterConnectivity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
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
