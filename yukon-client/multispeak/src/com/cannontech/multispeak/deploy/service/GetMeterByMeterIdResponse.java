/**
 * GetMeterByMeterIdResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetMeterByMeterIdResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Meter getMeterByMeterIdResult;

    public GetMeterByMeterIdResponse() {
    }

    public GetMeterByMeterIdResponse(
           com.cannontech.multispeak.deploy.service.Meter getMeterByMeterIdResult) {
           this.getMeterByMeterIdResult = getMeterByMeterIdResult;
    }


    /**
     * Gets the getMeterByMeterIdResult value for this GetMeterByMeterIdResponse.
     * 
     * @return getMeterByMeterIdResult
     */
    public com.cannontech.multispeak.deploy.service.Meter getGetMeterByMeterIdResult() {
        return getMeterByMeterIdResult;
    }


    /**
     * Sets the getMeterByMeterIdResult value for this GetMeterByMeterIdResponse.
     * 
     * @param getMeterByMeterIdResult
     */
    public void setGetMeterByMeterIdResult(com.cannontech.multispeak.deploy.service.Meter getMeterByMeterIdResult) {
        this.getMeterByMeterIdResult = getMeterByMeterIdResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetMeterByMeterIdResponse)) return false;
        GetMeterByMeterIdResponse other = (GetMeterByMeterIdResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getMeterByMeterIdResult==null && other.getGetMeterByMeterIdResult()==null) || 
             (this.getMeterByMeterIdResult!=null &&
              this.getMeterByMeterIdResult.equals(other.getGetMeterByMeterIdResult())));
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
        if (getGetMeterByMeterIdResult() != null) {
            _hashCode += getGetMeterByMeterIdResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMeterByMeterIdResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterIdResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getMeterByMeterIdResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterIdResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
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
