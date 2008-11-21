/**
 * GetPoleByPoleNumberFromCBResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetPoleByPoleNumberFromCBResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Pole getPoleByPoleNumberFromCBResult;

    public GetPoleByPoleNumberFromCBResponse() {
    }

    public GetPoleByPoleNumberFromCBResponse(
           com.cannontech.multispeak.deploy.service.Pole getPoleByPoleNumberFromCBResult) {
           this.getPoleByPoleNumberFromCBResult = getPoleByPoleNumberFromCBResult;
    }


    /**
     * Gets the getPoleByPoleNumberFromCBResult value for this GetPoleByPoleNumberFromCBResponse.
     * 
     * @return getPoleByPoleNumberFromCBResult
     */
    public com.cannontech.multispeak.deploy.service.Pole getGetPoleByPoleNumberFromCBResult() {
        return getPoleByPoleNumberFromCBResult;
    }


    /**
     * Sets the getPoleByPoleNumberFromCBResult value for this GetPoleByPoleNumberFromCBResponse.
     * 
     * @param getPoleByPoleNumberFromCBResult
     */
    public void setGetPoleByPoleNumberFromCBResult(com.cannontech.multispeak.deploy.service.Pole getPoleByPoleNumberFromCBResult) {
        this.getPoleByPoleNumberFromCBResult = getPoleByPoleNumberFromCBResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPoleByPoleNumberFromCBResponse)) return false;
        GetPoleByPoleNumberFromCBResponse other = (GetPoleByPoleNumberFromCBResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getPoleByPoleNumberFromCBResult==null && other.getGetPoleByPoleNumberFromCBResult()==null) || 
             (this.getPoleByPoleNumberFromCBResult!=null &&
              this.getPoleByPoleNumberFromCBResult.equals(other.getGetPoleByPoleNumberFromCBResult())));
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
        if (getGetPoleByPoleNumberFromCBResult() != null) {
            _hashCode += getGetPoleByPoleNumberFromCBResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetPoleByPoleNumberFromCBResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPoleByPoleNumberFromCBResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getPoleByPoleNumberFromCBResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetPoleByPoleNumberFromCBResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
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
