/**
 * InitiateUsageMonitoringResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class InitiateUsageMonitoringResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoringResult;

    public InitiateUsageMonitoringResponse() {
    }

    public InitiateUsageMonitoringResponse(
           com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoringResult) {
           this.initiateUsageMonitoringResult = initiateUsageMonitoringResult;
    }


    /**
     * Gets the initiateUsageMonitoringResult value for this InitiateUsageMonitoringResponse.
     * 
     * @return initiateUsageMonitoringResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getInitiateUsageMonitoringResult() {
        return initiateUsageMonitoringResult;
    }


    /**
     * Sets the initiateUsageMonitoringResult value for this InitiateUsageMonitoringResponse.
     * 
     * @param initiateUsageMonitoringResult
     */
    public void setInitiateUsageMonitoringResult(com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoringResult) {
        this.initiateUsageMonitoringResult = initiateUsageMonitoringResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateUsageMonitoringResponse)) return false;
        InitiateUsageMonitoringResponse other = (InitiateUsageMonitoringResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.initiateUsageMonitoringResult==null && other.getInitiateUsageMonitoringResult()==null) || 
             (this.initiateUsageMonitoringResult!=null &&
              this.initiateUsageMonitoringResult.equals(other.getInitiateUsageMonitoringResult())));
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
        if (getInitiateUsageMonitoringResult() != null) {
            _hashCode += getInitiateUsageMonitoringResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateUsageMonitoringResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateUsageMonitoringResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initiateUsageMonitoringResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateUsageMonitoringResult"));
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
