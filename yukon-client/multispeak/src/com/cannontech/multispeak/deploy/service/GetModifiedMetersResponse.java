/**
 * GetModifiedMetersResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetModifiedMetersResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Meter[] getModifiedMetersResult;

    public GetModifiedMetersResponse() {
    }

    public GetModifiedMetersResponse(
           com.cannontech.multispeak.deploy.service.Meter[] getModifiedMetersResult) {
           this.getModifiedMetersResult = getModifiedMetersResult;
    }


    /**
     * Gets the getModifiedMetersResult value for this GetModifiedMetersResponse.
     * 
     * @return getModifiedMetersResult
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getGetModifiedMetersResult() {
        return getModifiedMetersResult;
    }


    /**
     * Sets the getModifiedMetersResult value for this GetModifiedMetersResponse.
     * 
     * @param getModifiedMetersResult
     */
    public void setGetModifiedMetersResult(com.cannontech.multispeak.deploy.service.Meter[] getModifiedMetersResult) {
        this.getModifiedMetersResult = getModifiedMetersResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedMetersResponse)) return false;
        GetModifiedMetersResponse other = (GetModifiedMetersResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedMetersResult==null && other.getGetModifiedMetersResult()==null) || 
             (this.getModifiedMetersResult!=null &&
              java.util.Arrays.equals(this.getModifiedMetersResult, other.getGetModifiedMetersResult())));
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
        if (getGetModifiedMetersResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetModifiedMetersResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetModifiedMetersResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetModifiedMetersResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMetersResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedMetersResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedMetersResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
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
