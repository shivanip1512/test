/**
 * GetConvenienceFeesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetConvenienceFeesResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getConvenienceFeesResult;

    public GetConvenienceFeesResponse() {
    }

    public GetConvenienceFeesResponse(
           com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getConvenienceFeesResult) {
           this.getConvenienceFeesResult = getConvenienceFeesResult;
    }


    /**
     * Gets the getConvenienceFeesResult value for this GetConvenienceFeesResponse.
     * 
     * @return getConvenienceFeesResult
     */
    public com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getGetConvenienceFeesResult() {
        return getConvenienceFeesResult;
    }


    /**
     * Sets the getConvenienceFeesResult value for this GetConvenienceFeesResponse.
     * 
     * @param getConvenienceFeesResult
     */
    public void setGetConvenienceFeesResult(com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getConvenienceFeesResult) {
        this.getConvenienceFeesResult = getConvenienceFeesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetConvenienceFeesResponse)) return false;
        GetConvenienceFeesResponse other = (GetConvenienceFeesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getConvenienceFeesResult==null && other.getGetConvenienceFeesResult()==null) || 
             (this.getConvenienceFeesResult!=null &&
              java.util.Arrays.equals(this.getConvenienceFeesResult, other.getGetConvenienceFeesResult())));
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
        if (getGetConvenienceFeesResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetConvenienceFeesResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetConvenienceFeesResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetConvenienceFeesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetConvenienceFeesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getConvenienceFeesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetConvenienceFeesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem"));
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
