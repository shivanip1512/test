/**
 * MeterBaseRetireNotificationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeterBaseRetireNotificationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseRetireNotificationResult;

    public MeterBaseRetireNotificationResponse() {
    }

    public MeterBaseRetireNotificationResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseRetireNotificationResult) {
           this.meterBaseRetireNotificationResult = meterBaseRetireNotificationResult;
    }


    /**
     * Gets the meterBaseRetireNotificationResult value for this MeterBaseRetireNotificationResponse.
     * 
     * @return meterBaseRetireNotificationResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getMeterBaseRetireNotificationResult() {
        return meterBaseRetireNotificationResult;
    }


    /**
     * Sets the meterBaseRetireNotificationResult value for this MeterBaseRetireNotificationResponse.
     * 
     * @param meterBaseRetireNotificationResult
     */
    public void setMeterBaseRetireNotificationResult(com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseRetireNotificationResult) {
        this.meterBaseRetireNotificationResult = meterBaseRetireNotificationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterBaseRetireNotificationResponse)) return false;
        MeterBaseRetireNotificationResponse other = (MeterBaseRetireNotificationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterBaseRetireNotificationResult==null && other.getMeterBaseRetireNotificationResult()==null) || 
             (this.meterBaseRetireNotificationResult!=null &&
              java.util.Arrays.equals(this.meterBaseRetireNotificationResult, other.getMeterBaseRetireNotificationResult())));
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
        if (getMeterBaseRetireNotificationResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterBaseRetireNotificationResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterBaseRetireNotificationResult(), i);
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
        new org.apache.axis.description.TypeDesc(MeterBaseRetireNotificationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterBaseRetireNotificationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterBaseRetireNotificationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MeterBaseRetireNotificationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
