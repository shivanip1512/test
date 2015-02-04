/**
 * GetModifiedCircuitElementsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetModifiedCircuitElementsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CircuitElement[] getModifiedCircuitElementsResult;

    public GetModifiedCircuitElementsResponse() {
    }

    public GetModifiedCircuitElementsResponse(
           com.cannontech.multispeak.deploy.service.CircuitElement[] getModifiedCircuitElementsResult) {
           this.getModifiedCircuitElementsResult = getModifiedCircuitElementsResult;
    }


    /**
     * Gets the getModifiedCircuitElementsResult value for this GetModifiedCircuitElementsResponse.
     * 
     * @return getModifiedCircuitElementsResult
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getGetModifiedCircuitElementsResult() {
        return getModifiedCircuitElementsResult;
    }


    /**
     * Sets the getModifiedCircuitElementsResult value for this GetModifiedCircuitElementsResponse.
     * 
     * @param getModifiedCircuitElementsResult
     */
    public void setGetModifiedCircuitElementsResult(com.cannontech.multispeak.deploy.service.CircuitElement[] getModifiedCircuitElementsResult) {
        this.getModifiedCircuitElementsResult = getModifiedCircuitElementsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedCircuitElementsResponse)) return false;
        GetModifiedCircuitElementsResponse other = (GetModifiedCircuitElementsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedCircuitElementsResult==null && other.getGetModifiedCircuitElementsResult()==null) || 
             (this.getModifiedCircuitElementsResult!=null &&
              java.util.Arrays.equals(this.getModifiedCircuitElementsResult, other.getGetModifiedCircuitElementsResult())));
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
        if (getGetModifiedCircuitElementsResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetModifiedCircuitElementsResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetModifiedCircuitElementsResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetModifiedCircuitElementsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedCircuitElementsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedCircuitElementsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCircuitElementsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
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
