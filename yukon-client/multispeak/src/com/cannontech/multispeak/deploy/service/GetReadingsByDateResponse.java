/**
 * GetReadingsByDateResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetReadingsByDateResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDateResult;

    public GetReadingsByDateResponse() {
    }

    public GetReadingsByDateResponse(
           com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDateResult) {
           this.getReadingsByDateResult = getReadingsByDateResult;
    }


    /**
     * Gets the getReadingsByDateResult value for this GetReadingsByDateResponse.
     * 
     * @return getReadingsByDateResult
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getGetReadingsByDateResult() {
        return getReadingsByDateResult;
    }


    /**
     * Sets the getReadingsByDateResult value for this GetReadingsByDateResponse.
     * 
     * @param getReadingsByDateResult
     */
    public void setGetReadingsByDateResult(com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDateResult) {
        this.getReadingsByDateResult = getReadingsByDateResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetReadingsByDateResponse)) return false;
        GetReadingsByDateResponse other = (GetReadingsByDateResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getReadingsByDateResult==null && other.getGetReadingsByDateResult()==null) || 
             (this.getReadingsByDateResult!=null &&
              java.util.Arrays.equals(this.getReadingsByDateResult, other.getGetReadingsByDateResult())));
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
        if (getGetReadingsByDateResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetReadingsByDateResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetReadingsByDateResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetReadingsByDateResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetReadingsByDateResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getReadingsByDateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByDateResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
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
