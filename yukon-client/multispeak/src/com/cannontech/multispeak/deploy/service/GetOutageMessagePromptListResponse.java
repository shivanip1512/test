/**
 * GetOutageMessagePromptListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetOutageMessagePromptListResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Message[] getOutageMessagePromptListResult;

    public GetOutageMessagePromptListResponse() {
    }

    public GetOutageMessagePromptListResponse(
           com.cannontech.multispeak.deploy.service.Message[] getOutageMessagePromptListResult) {
           this.getOutageMessagePromptListResult = getOutageMessagePromptListResult;
    }


    /**
     * Gets the getOutageMessagePromptListResult value for this GetOutageMessagePromptListResponse.
     * 
     * @return getOutageMessagePromptListResult
     */
    public com.cannontech.multispeak.deploy.service.Message[] getGetOutageMessagePromptListResult() {
        return getOutageMessagePromptListResult;
    }


    /**
     * Sets the getOutageMessagePromptListResult value for this GetOutageMessagePromptListResponse.
     * 
     * @param getOutageMessagePromptListResult
     */
    public void setGetOutageMessagePromptListResult(com.cannontech.multispeak.deploy.service.Message[] getOutageMessagePromptListResult) {
        this.getOutageMessagePromptListResult = getOutageMessagePromptListResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetOutageMessagePromptListResponse)) return false;
        GetOutageMessagePromptListResponse other = (GetOutageMessagePromptListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getOutageMessagePromptListResult==null && other.getGetOutageMessagePromptListResult()==null) || 
             (this.getOutageMessagePromptListResult!=null &&
              java.util.Arrays.equals(this.getOutageMessagePromptListResult, other.getGetOutageMessagePromptListResult())));
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
        if (getGetOutageMessagePromptListResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetOutageMessagePromptListResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetOutageMessagePromptListResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetOutageMessagePromptListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetOutageMessagePromptListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getOutageMessagePromptListResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageMessagePromptListResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
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
