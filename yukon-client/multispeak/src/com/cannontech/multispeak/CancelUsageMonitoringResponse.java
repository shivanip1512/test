/**
 * CancelUsageMonitoringResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CancelUsageMonitoringResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoringResult;

    public CancelUsageMonitoringResponse() {
    }

    public CancelUsageMonitoringResponse(
           com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoringResult) {
           this.cancelUsageMonitoringResult = cancelUsageMonitoringResult;
    }


    /**
     * Gets the cancelUsageMonitoringResult value for this CancelUsageMonitoringResponse.
     * 
     * @return cancelUsageMonitoringResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getCancelUsageMonitoringResult() {
        return cancelUsageMonitoringResult;
    }


    /**
     * Sets the cancelUsageMonitoringResult value for this CancelUsageMonitoringResponse.
     * 
     * @param cancelUsageMonitoringResult
     */
    public void setCancelUsageMonitoringResult(com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoringResult) {
        this.cancelUsageMonitoringResult = cancelUsageMonitoringResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CancelUsageMonitoringResponse)) return false;
        CancelUsageMonitoringResponse other = (CancelUsageMonitoringResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cancelUsageMonitoringResult==null && other.getCancelUsageMonitoringResult()==null) || 
             (this.cancelUsageMonitoringResult!=null &&
              this.cancelUsageMonitoringResult.equals(other.getCancelUsageMonitoringResult())));
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
        if (getCancelUsageMonitoringResult() != null) {
            _hashCode += getCancelUsageMonitoringResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CancelUsageMonitoringResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CancelUsageMonitoringResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cancelUsageMonitoringResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelUsageMonitoringResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
