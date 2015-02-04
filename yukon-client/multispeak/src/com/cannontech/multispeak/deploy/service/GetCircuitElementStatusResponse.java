/**
 * GetCircuitElementStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetCircuitElementStatusResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CircuitElementStatus getCircuitElementStatusResult;

    public GetCircuitElementStatusResponse() {
    }

    public GetCircuitElementStatusResponse(
           com.cannontech.multispeak.deploy.service.CircuitElementStatus getCircuitElementStatusResult) {
           this.getCircuitElementStatusResult = getCircuitElementStatusResult;
    }


    /**
     * Gets the getCircuitElementStatusResult value for this GetCircuitElementStatusResponse.
     * 
     * @return getCircuitElementStatusResult
     */
    public com.cannontech.multispeak.deploy.service.CircuitElementStatus getGetCircuitElementStatusResult() {
        return getCircuitElementStatusResult;
    }


    /**
     * Sets the getCircuitElementStatusResult value for this GetCircuitElementStatusResponse.
     * 
     * @param getCircuitElementStatusResult
     */
    public void setGetCircuitElementStatusResult(com.cannontech.multispeak.deploy.service.CircuitElementStatus getCircuitElementStatusResult) {
        this.getCircuitElementStatusResult = getCircuitElementStatusResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCircuitElementStatusResponse)) return false;
        GetCircuitElementStatusResponse other = (GetCircuitElementStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCircuitElementStatusResult==null && other.getGetCircuitElementStatusResult()==null) || 
             (this.getCircuitElementStatusResult!=null &&
              this.getCircuitElementStatusResult.equals(other.getGetCircuitElementStatusResult())));
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
        if (getGetCircuitElementStatusResult() != null) {
            _hashCode += getGetCircuitElementStatusResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCircuitElementStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCircuitElementStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCircuitElementStatusResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCircuitElementStatusResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementStatus"));
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
