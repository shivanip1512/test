/**
 * DisableReadingScheduleResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class DisableReadingScheduleResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] disableReadingScheduleResult;

    public DisableReadingScheduleResponse() {
    }

    public DisableReadingScheduleResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] disableReadingScheduleResult) {
           this.disableReadingScheduleResult = disableReadingScheduleResult;
    }


    /**
     * Gets the disableReadingScheduleResult value for this DisableReadingScheduleResponse.
     * 
     * @return disableReadingScheduleResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getDisableReadingScheduleResult() {
        return disableReadingScheduleResult;
    }


    /**
     * Sets the disableReadingScheduleResult value for this DisableReadingScheduleResponse.
     * 
     * @param disableReadingScheduleResult
     */
    public void setDisableReadingScheduleResult(com.cannontech.multispeak.deploy.service.ErrorObject[] disableReadingScheduleResult) {
        this.disableReadingScheduleResult = disableReadingScheduleResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DisableReadingScheduleResponse)) return false;
        DisableReadingScheduleResponse other = (DisableReadingScheduleResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.disableReadingScheduleResult==null && other.getDisableReadingScheduleResult()==null) || 
             (this.disableReadingScheduleResult!=null &&
              java.util.Arrays.equals(this.disableReadingScheduleResult, other.getDisableReadingScheduleResult())));
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
        if (getDisableReadingScheduleResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDisableReadingScheduleResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDisableReadingScheduleResult(), i);
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
        new org.apache.axis.description.TypeDesc(DisableReadingScheduleResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DisableReadingScheduleResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disableReadingScheduleResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DisableReadingScheduleResult"));
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
