/**
 * EnableReadingScheduleResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EnableReadingScheduleResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] enableReadingScheduleResult;

    public EnableReadingScheduleResponse() {
    }

    public EnableReadingScheduleResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] enableReadingScheduleResult) {
           this.enableReadingScheduleResult = enableReadingScheduleResult;
    }


    /**
     * Gets the enableReadingScheduleResult value for this EnableReadingScheduleResponse.
     * 
     * @return enableReadingScheduleResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getEnableReadingScheduleResult() {
        return enableReadingScheduleResult;
    }


    /**
     * Sets the enableReadingScheduleResult value for this EnableReadingScheduleResponse.
     * 
     * @param enableReadingScheduleResult
     */
    public void setEnableReadingScheduleResult(com.cannontech.multispeak.deploy.service.ErrorObject[] enableReadingScheduleResult) {
        this.enableReadingScheduleResult = enableReadingScheduleResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EnableReadingScheduleResponse)) return false;
        EnableReadingScheduleResponse other = (EnableReadingScheduleResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.enableReadingScheduleResult==null && other.getEnableReadingScheduleResult()==null) || 
             (this.enableReadingScheduleResult!=null &&
              java.util.Arrays.equals(this.enableReadingScheduleResult, other.getEnableReadingScheduleResult())));
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
        if (getEnableReadingScheduleResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEnableReadingScheduleResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEnableReadingScheduleResult(), i);
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
        new org.apache.axis.description.TypeDesc(EnableReadingScheduleResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">EnableReadingScheduleResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableReadingScheduleResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EnableReadingScheduleResult"));
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
