/**
 * GetPropaneMeterByAccountNumberResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetPropaneMeterByAccountNumberResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Meters getPropaneMeterByAccountNumberResult;

    public GetPropaneMeterByAccountNumberResponse() {
    }

    public GetPropaneMeterByAccountNumberResponse(
           com.cannontech.multispeak.deploy.service.Meters getPropaneMeterByAccountNumberResult) {
           this.getPropaneMeterByAccountNumberResult = getPropaneMeterByAccountNumberResult;
    }


    /**
     * Gets the getPropaneMeterByAccountNumberResult value for this GetPropaneMeterByAccountNumberResponse.
     * 
     * @return getPropaneMeterByAccountNumberResult
     */
    public com.cannontech.multispeak.deploy.service.Meters getGetPropaneMeterByAccountNumberResult() {
        return getPropaneMeterByAccountNumberResult;
    }


    /**
     * Sets the getPropaneMeterByAccountNumberResult value for this GetPropaneMeterByAccountNumberResponse.
     * 
     * @param getPropaneMeterByAccountNumberResult
     */
    public void setGetPropaneMeterByAccountNumberResult(com.cannontech.multispeak.deploy.service.Meters getPropaneMeterByAccountNumberResult) {
        this.getPropaneMeterByAccountNumberResult = getPropaneMeterByAccountNumberResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPropaneMeterByAccountNumberResponse)) return false;
        GetPropaneMeterByAccountNumberResponse other = (GetPropaneMeterByAccountNumberResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getPropaneMeterByAccountNumberResult==null && other.getGetPropaneMeterByAccountNumberResult()==null) || 
             (this.getPropaneMeterByAccountNumberResult!=null &&
              this.getPropaneMeterByAccountNumberResult.equals(other.getGetPropaneMeterByAccountNumberResult())));
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
        if (getGetPropaneMeterByAccountNumberResult() != null) {
            _hashCode += getGetPropaneMeterByAccountNumberResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetPropaneMeterByAccountNumberResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPropaneMeterByAccountNumberResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getPropaneMeterByAccountNumberResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPropaneMeterByAccountNumberResult"));
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
