/**
 * GetHistoryLogsByDateResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetHistoryLogsByDateResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateResult;

    public GetHistoryLogsByDateResponse() {
    }

    public GetHistoryLogsByDateResponse(
           com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateResult) {
           this.getHistoryLogsByDateResult = getHistoryLogsByDateResult;
    }


    /**
     * Gets the getHistoryLogsByDateResult value for this GetHistoryLogsByDateResponse.
     * 
     * @return getHistoryLogsByDateResult
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getGetHistoryLogsByDateResult() {
        return getHistoryLogsByDateResult;
    }


    /**
     * Sets the getHistoryLogsByDateResult value for this GetHistoryLogsByDateResponse.
     * 
     * @param getHistoryLogsByDateResult
     */
    public void setGetHistoryLogsByDateResult(com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateResult) {
        this.getHistoryLogsByDateResult = getHistoryLogsByDateResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetHistoryLogsByDateResponse)) return false;
        GetHistoryLogsByDateResponse other = (GetHistoryLogsByDateResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getHistoryLogsByDateResult==null && other.getGetHistoryLogsByDateResult()==null) || 
             (this.getHistoryLogsByDateResult!=null &&
              java.util.Arrays.equals(this.getHistoryLogsByDateResult, other.getGetHistoryLogsByDateResult())));
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
        if (getGetHistoryLogsByDateResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetHistoryLogsByDateResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetHistoryLogsByDateResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetHistoryLogsByDateResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetHistoryLogsByDateResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getHistoryLogsByDateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDateResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
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
