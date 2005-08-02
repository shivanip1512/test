/**
 * ModifyODDataForOutageDetectionDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ModifyODDataForOutageDetectionDevice  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageDetectionDevice oDDevice;

    public ModifyODDataForOutageDetectionDevice() {
    }

    public ModifyODDataForOutageDetectionDevice(
           com.cannontech.multispeak.OutageDetectionDevice oDDevice) {
           this.oDDevice = oDDevice;
    }


    /**
     * Gets the oDDevice value for this ModifyODDataForOutageDetectionDevice.
     * 
     * @return oDDevice
     */
    public com.cannontech.multispeak.OutageDetectionDevice getODDevice() {
        return oDDevice;
    }


    /**
     * Sets the oDDevice value for this ModifyODDataForOutageDetectionDevice.
     * 
     * @param oDDevice
     */
    public void setODDevice(com.cannontech.multispeak.OutageDetectionDevice oDDevice) {
        this.oDDevice = oDDevice;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModifyODDataForOutageDetectionDevice)) return false;
        ModifyODDataForOutageDetectionDevice other = (ModifyODDataForOutageDetectionDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.oDDevice==null && other.getODDevice()==null) || 
             (this.oDDevice!=null &&
              this.oDDevice.equals(other.getODDevice())));
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
        if (getODDevice() != null) {
            _hashCode += getODDevice().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ModifyODDataForOutageDetectionDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyODDataForOutageDetectionDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ODDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
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
