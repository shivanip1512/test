/**
 * GetNextNumberResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetNextNumberResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumberResult;

    public GetNextNumberResponse() {
    }

    public GetNextNumberResponse(
           com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumberResult) {
           this.getNextNumberResult = getNextNumberResult;
    }


    /**
     * Gets the getNextNumberResult value for this GetNextNumberResponse.
     * 
     * @return getNextNumberResult
     */
    public com.cannontech.multispeak.deploy.service.RequestedNumber getGetNextNumberResult() {
        return getNextNumberResult;
    }


    /**
     * Sets the getNextNumberResult value for this GetNextNumberResponse.
     * 
     * @param getNextNumberResult
     */
    public void setGetNextNumberResult(com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumberResult) {
        this.getNextNumberResult = getNextNumberResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetNextNumberResponse)) return false;
        GetNextNumberResponse other = (GetNextNumberResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getNextNumberResult==null && other.getGetNextNumberResult()==null) || 
             (this.getNextNumberResult!=null &&
              this.getNextNumberResult.equals(other.getGetNextNumberResult())));
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
        if (getGetNextNumberResult() != null) {
            _hashCode += getGetNextNumberResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetNextNumberResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetNextNumberResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getNextNumberResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetNextNumberResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNumber"));
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
