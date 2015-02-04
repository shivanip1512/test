/**
 * GetDomainNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetDomainNamesResponse  implements java.io.Serializable {
    private java.lang.String[] getDomainNamesResult;

    public GetDomainNamesResponse() {
    }

    public GetDomainNamesResponse(
           java.lang.String[] getDomainNamesResult) {
           this.getDomainNamesResult = getDomainNamesResult;
    }


    /**
     * Gets the getDomainNamesResult value for this GetDomainNamesResponse.
     * 
     * @return getDomainNamesResult
     */
    public java.lang.String[] getGetDomainNamesResult() {
        return getDomainNamesResult;
    }


    /**
     * Sets the getDomainNamesResult value for this GetDomainNamesResponse.
     * 
     * @param getDomainNamesResult
     */
    public void setGetDomainNamesResult(java.lang.String[] getDomainNamesResult) {
        this.getDomainNamesResult = getDomainNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetDomainNamesResponse)) return false;
        GetDomainNamesResponse other = (GetDomainNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getDomainNamesResult==null && other.getGetDomainNamesResult()==null) || 
             (this.getDomainNamesResult!=null &&
              java.util.Arrays.equals(this.getDomainNamesResult, other.getGetDomainNamesResult())));
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
        if (getGetDomainNamesResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetDomainNamesResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetDomainNamesResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetDomainNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getDomainNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
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
