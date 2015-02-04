/**
 * ChargeableDeviceChangedNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ChargeableDeviceChangedNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ChargeableDeviceList[] deviceList;

    public ChargeableDeviceChangedNotification() {
    }

    public ChargeableDeviceChangedNotification(
           com.cannontech.multispeak.deploy.service.ChargeableDeviceList[] deviceList) {
           this.deviceList = deviceList;
    }


    /**
     * Gets the deviceList value for this ChargeableDeviceChangedNotification.
     * 
     * @return deviceList
     */
    public com.cannontech.multispeak.deploy.service.ChargeableDeviceList[] getDeviceList() {
        return deviceList;
    }


    /**
     * Sets the deviceList value for this ChargeableDeviceChangedNotification.
     * 
     * @param deviceList
     */
    public void setDeviceList(com.cannontech.multispeak.deploy.service.ChargeableDeviceList[] deviceList) {
        this.deviceList = deviceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChargeableDeviceChangedNotification)) return false;
        ChargeableDeviceChangedNotification other = (ChargeableDeviceChangedNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.deviceList==null && other.getDeviceList()==null) || 
             (this.deviceList!=null &&
              java.util.Arrays.equals(this.deviceList, other.getDeviceList())));
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
        if (getDeviceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDeviceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDeviceList(), i);
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
        new org.apache.axis.description.TypeDesc(ChargeableDeviceChangedNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ChargeableDeviceChangedNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDeviceList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDeviceList"));
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
