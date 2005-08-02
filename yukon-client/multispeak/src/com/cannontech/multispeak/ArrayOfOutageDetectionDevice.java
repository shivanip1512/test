/**
 * ArrayOfOutageDetectionDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfOutageDetectionDevice  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice;

    public ArrayOfOutageDetectionDevice() {
    }

    public ArrayOfOutageDetectionDevice(
           com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice) {
           this.outageDetectionDevice = outageDetectionDevice;
    }


    /**
     * Gets the outageDetectionDevice value for this ArrayOfOutageDetectionDevice.
     * 
     * @return outageDetectionDevice
     */
    public com.cannontech.multispeak.OutageDetectionDevice[] getOutageDetectionDevice() {
        return outageDetectionDevice;
    }


    /**
     * Sets the outageDetectionDevice value for this ArrayOfOutageDetectionDevice.
     * 
     * @param outageDetectionDevice
     */
    public void setOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice) {
        this.outageDetectionDevice = outageDetectionDevice;
    }

    public com.cannontech.multispeak.OutageDetectionDevice getOutageDetectionDevice(int i) {
        return this.outageDetectionDevice[i];
    }

    public void setOutageDetectionDevice(int i, com.cannontech.multispeak.OutageDetectionDevice _value) {
        this.outageDetectionDevice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfOutageDetectionDevice)) return false;
        ArrayOfOutageDetectionDevice other = (ArrayOfOutageDetectionDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.outageDetectionDevice==null && other.getOutageDetectionDevice()==null) || 
             (this.outageDetectionDevice!=null &&
              java.util.Arrays.equals(this.outageDetectionDevice, other.getOutageDetectionDevice())));
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
        if (getOutageDetectionDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutageDetectionDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutageDetectionDevice(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfOutageDetectionDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectionDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
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
