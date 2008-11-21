/**
 * GetBilledUsageResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetBilledUsageResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.BilledUsage getBilledUsageResult;

    public GetBilledUsageResponse() {
    }

    public GetBilledUsageResponse(
           com.cannontech.multispeak.deploy.service.BilledUsage getBilledUsageResult) {
           this.getBilledUsageResult = getBilledUsageResult;
    }


    /**
     * Gets the getBilledUsageResult value for this GetBilledUsageResponse.
     * 
     * @return getBilledUsageResult
     */
    public com.cannontech.multispeak.deploy.service.BilledUsage getGetBilledUsageResult() {
        return getBilledUsageResult;
    }


    /**
     * Sets the getBilledUsageResult value for this GetBilledUsageResponse.
     * 
     * @param getBilledUsageResult
     */
    public void setGetBilledUsageResult(com.cannontech.multispeak.deploy.service.BilledUsage getBilledUsageResult) {
        this.getBilledUsageResult = getBilledUsageResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetBilledUsageResponse)) return false;
        GetBilledUsageResponse other = (GetBilledUsageResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getBilledUsageResult==null && other.getGetBilledUsageResult()==null) || 
             (this.getBilledUsageResult!=null &&
              this.getBilledUsageResult.equals(other.getGetBilledUsageResult())));
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
        if (getGetBilledUsageResult() != null) {
            _hashCode += getGetBilledUsageResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetBilledUsageResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBilledUsageResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getBilledUsageResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBilledUsageResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billedUsage"));
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
