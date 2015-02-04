/**
 * InHomeDisplayBillingMessageNotificationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InHomeDisplayBillingMessageNotificationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayBillingMessageNotificationResult;

    public InHomeDisplayBillingMessageNotificationResponse() {
    }

    public InHomeDisplayBillingMessageNotificationResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayBillingMessageNotificationResult) {
           this.inHomeDisplayBillingMessageNotificationResult = inHomeDisplayBillingMessageNotificationResult;
    }


    /**
     * Gets the inHomeDisplayBillingMessageNotificationResult value for this InHomeDisplayBillingMessageNotificationResponse.
     * 
     * @return inHomeDisplayBillingMessageNotificationResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getInHomeDisplayBillingMessageNotificationResult() {
        return inHomeDisplayBillingMessageNotificationResult;
    }


    /**
     * Sets the inHomeDisplayBillingMessageNotificationResult value for this InHomeDisplayBillingMessageNotificationResponse.
     * 
     * @param inHomeDisplayBillingMessageNotificationResult
     */
    public void setInHomeDisplayBillingMessageNotificationResult(com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayBillingMessageNotificationResult) {
        this.inHomeDisplayBillingMessageNotificationResult = inHomeDisplayBillingMessageNotificationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InHomeDisplayBillingMessageNotificationResponse)) return false;
        InHomeDisplayBillingMessageNotificationResponse other = (InHomeDisplayBillingMessageNotificationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.inHomeDisplayBillingMessageNotificationResult==null && other.getInHomeDisplayBillingMessageNotificationResult()==null) || 
             (this.inHomeDisplayBillingMessageNotificationResult!=null &&
              java.util.Arrays.equals(this.inHomeDisplayBillingMessageNotificationResult, other.getInHomeDisplayBillingMessageNotificationResult())));
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
        if (getInHomeDisplayBillingMessageNotificationResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInHomeDisplayBillingMessageNotificationResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInHomeDisplayBillingMessageNotificationResult(), i);
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
        new org.apache.axis.description.TypeDesc(InHomeDisplayBillingMessageNotificationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayBillingMessageNotificationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayBillingMessageNotificationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InHomeDisplayBillingMessageNotificationResult"));
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
