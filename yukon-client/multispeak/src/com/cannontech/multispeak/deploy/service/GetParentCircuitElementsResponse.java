/**
 * GetParentCircuitElementsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetParentCircuitElementsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CircuitElement[] getParentCircuitElementsResult;

    public GetParentCircuitElementsResponse() {
    }

    public GetParentCircuitElementsResponse(
           com.cannontech.multispeak.deploy.service.CircuitElement[] getParentCircuitElementsResult) {
           this.getParentCircuitElementsResult = getParentCircuitElementsResult;
    }


    /**
     * Gets the getParentCircuitElementsResult value for this GetParentCircuitElementsResponse.
     * 
     * @return getParentCircuitElementsResult
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getGetParentCircuitElementsResult() {
        return getParentCircuitElementsResult;
    }


    /**
     * Sets the getParentCircuitElementsResult value for this GetParentCircuitElementsResponse.
     * 
     * @param getParentCircuitElementsResult
     */
    public void setGetParentCircuitElementsResult(com.cannontech.multispeak.deploy.service.CircuitElement[] getParentCircuitElementsResult) {
        this.getParentCircuitElementsResult = getParentCircuitElementsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetParentCircuitElementsResponse)) return false;
        GetParentCircuitElementsResponse other = (GetParentCircuitElementsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getParentCircuitElementsResult==null && other.getGetParentCircuitElementsResult()==null) || 
             (this.getParentCircuitElementsResult!=null &&
              java.util.Arrays.equals(this.getParentCircuitElementsResult, other.getGetParentCircuitElementsResult())));
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
        if (getGetParentCircuitElementsResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetParentCircuitElementsResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetParentCircuitElementsResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetParentCircuitElementsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetParentCircuitElementsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getParentCircuitElementsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetParentCircuitElementsResult"));
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
