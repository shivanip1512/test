/**
 * GetSubstationNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetSubstationNamesResponse  implements java.io.Serializable {
    private java.lang.String[] getSubstationNamesResult;

    public GetSubstationNamesResponse() {
    }

    public GetSubstationNamesResponse(
           java.lang.String[] getSubstationNamesResult) {
           this.getSubstationNamesResult = getSubstationNamesResult;
    }


    /**
     * Gets the getSubstationNamesResult value for this GetSubstationNamesResponse.
     * 
     * @return getSubstationNamesResult
     */
    public java.lang.String[] getGetSubstationNamesResult() {
        return getSubstationNamesResult;
    }


    /**
     * Sets the getSubstationNamesResult value for this GetSubstationNamesResponse.
     * 
     * @param getSubstationNamesResult
     */
    public void setGetSubstationNamesResult(java.lang.String[] getSubstationNamesResult) {
        this.getSubstationNamesResult = getSubstationNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetSubstationNamesResponse)) return false;
        GetSubstationNamesResponse other = (GetSubstationNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getSubstationNamesResult==null && other.getGetSubstationNamesResult()==null) || 
             (this.getSubstationNamesResult!=null &&
              java.util.Arrays.equals(this.getSubstationNamesResult, other.getGetSubstationNamesResult())));
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
        if (getGetSubstationNamesResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetSubstationNamesResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetSubstationNamesResult(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetSubstationNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetSubstationNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getSubstationNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSubstationNamesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
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
