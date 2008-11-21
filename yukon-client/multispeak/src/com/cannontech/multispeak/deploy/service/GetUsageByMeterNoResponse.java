/**
 * GetUsageByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetUsageByMeterNoResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Usage getUsageByMeterNoResult;

    public GetUsageByMeterNoResponse() {
    }

    public GetUsageByMeterNoResponse(
           com.cannontech.multispeak.deploy.service.Usage getUsageByMeterNoResult) {
           this.getUsageByMeterNoResult = getUsageByMeterNoResult;
    }


    /**
     * Gets the getUsageByMeterNoResult value for this GetUsageByMeterNoResponse.
     * 
     * @return getUsageByMeterNoResult
     */
    public com.cannontech.multispeak.deploy.service.Usage getGetUsageByMeterNoResult() {
        return getUsageByMeterNoResult;
    }


    /**
     * Sets the getUsageByMeterNoResult value for this GetUsageByMeterNoResponse.
     * 
     * @param getUsageByMeterNoResult
     */
    public void setGetUsageByMeterNoResult(com.cannontech.multispeak.deploy.service.Usage getUsageByMeterNoResult) {
        this.getUsageByMeterNoResult = getUsageByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetUsageByMeterNoResponse)) return false;
        GetUsageByMeterNoResponse other = (GetUsageByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getUsageByMeterNoResult==null && other.getGetUsageByMeterNoResult()==null) || 
             (this.getUsageByMeterNoResult!=null &&
              this.getUsageByMeterNoResult.equals(other.getGetUsageByMeterNoResult())));
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
        if (getGetUsageByMeterNoResult() != null) {
            _hashCode += getGetUsageByMeterNoResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetUsageByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUsageByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getUsageByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUsageByMeterNoResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
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
