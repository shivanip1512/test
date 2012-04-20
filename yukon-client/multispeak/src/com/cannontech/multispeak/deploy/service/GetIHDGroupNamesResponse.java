/**
 * GetIHDGroupNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetIHDGroupNamesResponse  implements java.io.Serializable {
    private java.lang.String[] getIHDGroupNamesResult;

    public GetIHDGroupNamesResponse() {
    }

    public GetIHDGroupNamesResponse(
           java.lang.String[] getIHDGroupNamesResult) {
           this.getIHDGroupNamesResult = getIHDGroupNamesResult;
    }


    /**
     * Gets the getIHDGroupNamesResult value for this GetIHDGroupNamesResponse.
     * 
     * @return getIHDGroupNamesResult
     */
    public java.lang.String[] getGetIHDGroupNamesResult() {
        return getIHDGroupNamesResult;
    }


    /**
     * Sets the getIHDGroupNamesResult value for this GetIHDGroupNamesResponse.
     * 
     * @param getIHDGroupNamesResult
     */
    public void setGetIHDGroupNamesResult(java.lang.String[] getIHDGroupNamesResult) {
        this.getIHDGroupNamesResult = getIHDGroupNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetIHDGroupNamesResponse)) return false;
        GetIHDGroupNamesResponse other = (GetIHDGroupNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getIHDGroupNamesResult==null && other.getGetIHDGroupNamesResult()==null) || 
             (this.getIHDGroupNamesResult!=null &&
              java.util.Arrays.equals(this.getIHDGroupNamesResult, other.getGetIHDGroupNamesResult())));
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
        if (getGetIHDGroupNamesResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetIHDGroupNamesResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetIHDGroupNamesResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetIHDGroupNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getIHDGroupNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetIHDGroupNamesResult"));
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
