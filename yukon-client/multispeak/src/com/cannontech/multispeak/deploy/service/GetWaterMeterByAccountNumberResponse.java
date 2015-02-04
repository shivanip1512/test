/**
 * GetWaterMeterByAccountNumberResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetWaterMeterByAccountNumberResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Meters getWaterMeterByAccountNumberResult;

    public GetWaterMeterByAccountNumberResponse() {
    }

    public GetWaterMeterByAccountNumberResponse(
           com.cannontech.multispeak.deploy.service.Meters getWaterMeterByAccountNumberResult) {
           this.getWaterMeterByAccountNumberResult = getWaterMeterByAccountNumberResult;
    }


    /**
     * Gets the getWaterMeterByAccountNumberResult value for this GetWaterMeterByAccountNumberResponse.
     * 
     * @return getWaterMeterByAccountNumberResult
     */
    public com.cannontech.multispeak.deploy.service.Meters getGetWaterMeterByAccountNumberResult() {
        return getWaterMeterByAccountNumberResult;
    }


    /**
     * Sets the getWaterMeterByAccountNumberResult value for this GetWaterMeterByAccountNumberResponse.
     * 
     * @param getWaterMeterByAccountNumberResult
     */
    public void setGetWaterMeterByAccountNumberResult(com.cannontech.multispeak.deploy.service.Meters getWaterMeterByAccountNumberResult) {
        this.getWaterMeterByAccountNumberResult = getWaterMeterByAccountNumberResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetWaterMeterByAccountNumberResponse)) return false;
        GetWaterMeterByAccountNumberResponse other = (GetWaterMeterByAccountNumberResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getWaterMeterByAccountNumberResult==null && other.getGetWaterMeterByAccountNumberResult()==null) || 
             (this.getWaterMeterByAccountNumberResult!=null &&
              this.getWaterMeterByAccountNumberResult.equals(other.getGetWaterMeterByAccountNumberResult())));
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
        if (getGetWaterMeterByAccountNumberResult() != null) {
            _hashCode += getGetWaterMeterByAccountNumberResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetWaterMeterByAccountNumberResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetWaterMeterByAccountNumberResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getWaterMeterByAccountNumberResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetWaterMeterByAccountNumberResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
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
