/**
 * InitiateODMonitoringRequestByObjectResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class InitiateODMonitoringRequestByObjectResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject initiateODMonitoringRequestByObjectResult;

    public InitiateODMonitoringRequestByObjectResponse() {
    }

    public InitiateODMonitoringRequestByObjectResponse(
           com.cannontech.multispeak.ArrayOfErrorObject initiateODMonitoringRequestByObjectResult) {
           this.initiateODMonitoringRequestByObjectResult = initiateODMonitoringRequestByObjectResult;
    }


    /**
     * Gets the initiateODMonitoringRequestByObjectResult value for this InitiateODMonitoringRequestByObjectResponse.
     * 
     * @return initiateODMonitoringRequestByObjectResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getInitiateODMonitoringRequestByObjectResult() {
        return initiateODMonitoringRequestByObjectResult;
    }


    /**
     * Sets the initiateODMonitoringRequestByObjectResult value for this InitiateODMonitoringRequestByObjectResponse.
     * 
     * @param initiateODMonitoringRequestByObjectResult
     */
    public void setInitiateODMonitoringRequestByObjectResult(com.cannontech.multispeak.ArrayOfErrorObject initiateODMonitoringRequestByObjectResult) {
        this.initiateODMonitoringRequestByObjectResult = initiateODMonitoringRequestByObjectResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateODMonitoringRequestByObjectResponse)) return false;
        InitiateODMonitoringRequestByObjectResponse other = (InitiateODMonitoringRequestByObjectResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.initiateODMonitoringRequestByObjectResult==null && other.getInitiateODMonitoringRequestByObjectResult()==null) || 
             (this.initiateODMonitoringRequestByObjectResult!=null &&
              this.initiateODMonitoringRequestByObjectResult.equals(other.getInitiateODMonitoringRequestByObjectResult())));
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
        if (getInitiateODMonitoringRequestByObjectResult() != null) {
            _hashCode += getInitiateODMonitoringRequestByObjectResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateODMonitoringRequestByObjectResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateODMonitoringRequestByObjectResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initiateODMonitoringRequestByObjectResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODMonitoringRequestByObjectResult"));
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
