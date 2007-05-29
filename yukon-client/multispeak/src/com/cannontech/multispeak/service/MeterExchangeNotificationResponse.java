/**
 * MeterExchangeNotificationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MeterExchangeNotificationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ArrayOfErrorObject meterExchangeNotificationResult;

    public MeterExchangeNotificationResponse() {
    }

    public MeterExchangeNotificationResponse(
           com.cannontech.multispeak.service.ArrayOfErrorObject meterExchangeNotificationResult) {
           this.meterExchangeNotificationResult = meterExchangeNotificationResult;
    }


    /**
     * Gets the meterExchangeNotificationResult value for this MeterExchangeNotificationResponse.
     * 
     * @return meterExchangeNotificationResult
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject getMeterExchangeNotificationResult() {
        return meterExchangeNotificationResult;
    }


    /**
     * Sets the meterExchangeNotificationResult value for this MeterExchangeNotificationResponse.
     * 
     * @param meterExchangeNotificationResult
     */
    public void setMeterExchangeNotificationResult(com.cannontech.multispeak.service.ArrayOfErrorObject meterExchangeNotificationResult) {
        this.meterExchangeNotificationResult = meterExchangeNotificationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterExchangeNotificationResponse)) return false;
        MeterExchangeNotificationResponse other = (MeterExchangeNotificationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterExchangeNotificationResult==null && other.getMeterExchangeNotificationResult()==null) || 
             (this.meterExchangeNotificationResult!=null &&
              this.meterExchangeNotificationResult.equals(other.getMeterExchangeNotificationResult())));
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
        if (getMeterExchangeNotificationResult() != null) {
            _hashCode += getMeterExchangeNotificationResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterExchangeNotificationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterExchangeNotificationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterExchangeNotificationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterExchangeNotificationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
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
