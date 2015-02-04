/**
 * GetServLocIDByMeterResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetServLocIDByMeterResponse  implements java.io.Serializable {
    private java.lang.String getServLocIDByMeterResult;

    public GetServLocIDByMeterResponse() {
    }

    public GetServLocIDByMeterResponse(
           java.lang.String getServLocIDByMeterResult) {
           this.getServLocIDByMeterResult = getServLocIDByMeterResult;
    }


    /**
     * Gets the getServLocIDByMeterResult value for this GetServLocIDByMeterResponse.
     * 
     * @return getServLocIDByMeterResult
     */
    public java.lang.String getGetServLocIDByMeterResult() {
        return getServLocIDByMeterResult;
    }


    /**
     * Sets the getServLocIDByMeterResult value for this GetServLocIDByMeterResponse.
     * 
     * @param getServLocIDByMeterResult
     */
    public void setGetServLocIDByMeterResult(java.lang.String getServLocIDByMeterResult) {
        this.getServLocIDByMeterResult = getServLocIDByMeterResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServLocIDByMeterResponse)) return false;
        GetServLocIDByMeterResponse other = (GetServLocIDByMeterResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServLocIDByMeterResult==null && other.getGetServLocIDByMeterResult()==null) || 
             (this.getServLocIDByMeterResult!=null &&
              this.getServLocIDByMeterResult.equals(other.getGetServLocIDByMeterResult())));
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
        if (getGetServLocIDByMeterResult() != null) {
            _hashCode += getGetServLocIDByMeterResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServLocIDByMeterResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServLocIDByMeterResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServLocIDByMeterResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServLocIDByMeterResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
