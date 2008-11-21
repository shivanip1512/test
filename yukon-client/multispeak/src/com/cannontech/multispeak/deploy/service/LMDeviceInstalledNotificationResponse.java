/**
 * LMDeviceInstalledNotificationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LMDeviceInstalledNotificationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotificationResult;

    public LMDeviceInstalledNotificationResponse() {
    }

    public LMDeviceInstalledNotificationResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotificationResult) {
           this.LMDeviceInstalledNotificationResult = LMDeviceInstalledNotificationResult;
    }


    /**
     * Gets the LMDeviceInstalledNotificationResult value for this LMDeviceInstalledNotificationResponse.
     * 
     * @return LMDeviceInstalledNotificationResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getLMDeviceInstalledNotificationResult() {
        return LMDeviceInstalledNotificationResult;
    }


    /**
     * Sets the LMDeviceInstalledNotificationResult value for this LMDeviceInstalledNotificationResponse.
     * 
     * @param LMDeviceInstalledNotificationResult
     */
    public void setLMDeviceInstalledNotificationResult(com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotificationResult) {
        this.LMDeviceInstalledNotificationResult = LMDeviceInstalledNotificationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LMDeviceInstalledNotificationResponse)) return false;
        LMDeviceInstalledNotificationResponse other = (LMDeviceInstalledNotificationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.LMDeviceInstalledNotificationResult==null && other.getLMDeviceInstalledNotificationResult()==null) || 
             (this.LMDeviceInstalledNotificationResult!=null &&
              java.util.Arrays.equals(this.LMDeviceInstalledNotificationResult, other.getLMDeviceInstalledNotificationResult())));
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
        if (getLMDeviceInstalledNotificationResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLMDeviceInstalledNotificationResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLMDeviceInstalledNotificationResult(), i);
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
        new org.apache.axis.description.TypeDesc(LMDeviceInstalledNotificationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">LMDeviceInstalledNotificationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("LMDeviceInstalledNotificationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LMDeviceInstalledNotificationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
