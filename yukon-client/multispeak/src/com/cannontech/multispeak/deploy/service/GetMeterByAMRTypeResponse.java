/**
 * GetMeterByAMRTypeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetMeterByAMRTypeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRTypeResult;

    public GetMeterByAMRTypeResponse() {
    }

    public GetMeterByAMRTypeResponse(
           com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRTypeResult) {
           this.getMeterByAMRTypeResult = getMeterByAMRTypeResult;
    }


    /**
     * Gets the getMeterByAMRTypeResult value for this GetMeterByAMRTypeResponse.
     * 
     * @return getMeterByAMRTypeResult
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getGetMeterByAMRTypeResult() {
        return getMeterByAMRTypeResult;
    }


    /**
     * Sets the getMeterByAMRTypeResult value for this GetMeterByAMRTypeResponse.
     * 
     * @param getMeterByAMRTypeResult
     */
    public void setGetMeterByAMRTypeResult(com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRTypeResult) {
        this.getMeterByAMRTypeResult = getMeterByAMRTypeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetMeterByAMRTypeResponse)) return false;
        GetMeterByAMRTypeResponse other = (GetMeterByAMRTypeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getMeterByAMRTypeResult==null && other.getGetMeterByAMRTypeResult()==null) || 
             (this.getMeterByAMRTypeResult!=null &&
              java.util.Arrays.equals(this.getMeterByAMRTypeResult, other.getGetMeterByAMRTypeResult())));
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
        if (getGetMeterByAMRTypeResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetMeterByAMRTypeResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetMeterByAMRTypeResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetMeterByAMRTypeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRTypeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getMeterByAMRTypeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRTypeResult"));
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
