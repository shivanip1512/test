/**
 * GetConfigurationGroupNamesByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetConfigurationGroupNamesByMeterNoResponse  implements java.io.Serializable {
    private java.lang.String[] getConfigurationGroupNamesByMeterNoResult;

    public GetConfigurationGroupNamesByMeterNoResponse() {
    }

    public GetConfigurationGroupNamesByMeterNoResponse(
           java.lang.String[] getConfigurationGroupNamesByMeterNoResult) {
           this.getConfigurationGroupNamesByMeterNoResult = getConfigurationGroupNamesByMeterNoResult;
    }


    /**
     * Gets the getConfigurationGroupNamesByMeterNoResult value for this GetConfigurationGroupNamesByMeterNoResponse.
     * 
     * @return getConfigurationGroupNamesByMeterNoResult
     */
    public java.lang.String[] getGetConfigurationGroupNamesByMeterNoResult() {
        return getConfigurationGroupNamesByMeterNoResult;
    }


    /**
     * Sets the getConfigurationGroupNamesByMeterNoResult value for this GetConfigurationGroupNamesByMeterNoResponse.
     * 
     * @param getConfigurationGroupNamesByMeterNoResult
     */
    public void setGetConfigurationGroupNamesByMeterNoResult(java.lang.String[] getConfigurationGroupNamesByMeterNoResult) {
        this.getConfigurationGroupNamesByMeterNoResult = getConfigurationGroupNamesByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetConfigurationGroupNamesByMeterNoResponse)) return false;
        GetConfigurationGroupNamesByMeterNoResponse other = (GetConfigurationGroupNamesByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getConfigurationGroupNamesByMeterNoResult==null && other.getGetConfigurationGroupNamesByMeterNoResult()==null) || 
             (this.getConfigurationGroupNamesByMeterNoResult!=null &&
              java.util.Arrays.equals(this.getConfigurationGroupNamesByMeterNoResult, other.getGetConfigurationGroupNamesByMeterNoResult())));
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
        if (getGetConfigurationGroupNamesByMeterNoResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetConfigurationGroupNamesByMeterNoResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetConfigurationGroupNamesByMeterNoResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetConfigurationGroupNamesByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConfigurationGroupNamesByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getConfigurationGroupNamesByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConfigurationGroupNamesByMeterNoResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
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
