/**
 * InitiateCutResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InitiateCutResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject initiateCutResult;

    public InitiateCutResponse() {
    }

    public InitiateCutResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject initiateCutResult) {
           this.initiateCutResult = initiateCutResult;
    }


    /**
     * Gets the initiateCutResult value for this InitiateCutResponse.
     * 
     * @return initiateCutResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject getInitiateCutResult() {
        return initiateCutResult;
    }


    /**
     * Sets the initiateCutResult value for this InitiateCutResponse.
     * 
     * @param initiateCutResult
     */
    public void setInitiateCutResult(com.cannontech.multispeak.deploy.service.ErrorObject initiateCutResult) {
        this.initiateCutResult = initiateCutResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateCutResponse)) return false;
        InitiateCutResponse other = (InitiateCutResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.initiateCutResult==null && other.getInitiateCutResult()==null) || 
             (this.initiateCutResult!=null &&
              this.initiateCutResult.equals(other.getInitiateCutResult())));
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
        if (getInitiateCutResult() != null) {
            _hashCode += getInitiateCutResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateCutResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateCutResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initiateCutResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateCutResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
