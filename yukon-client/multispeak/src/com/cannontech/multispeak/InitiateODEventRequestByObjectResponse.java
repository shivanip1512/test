/**
 * InitiateODEventRequestByObjectResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class InitiateODEventRequestByObjectResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject initiateODEventRequestByObjectResult;

    public InitiateODEventRequestByObjectResponse() {
    }

    public InitiateODEventRequestByObjectResponse(
           com.cannontech.multispeak.ArrayOfErrorObject initiateODEventRequestByObjectResult) {
           this.initiateODEventRequestByObjectResult = initiateODEventRequestByObjectResult;
    }


    /**
     * Gets the initiateODEventRequestByObjectResult value for this InitiateODEventRequestByObjectResponse.
     * 
     * @return initiateODEventRequestByObjectResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getInitiateODEventRequestByObjectResult() {
        return initiateODEventRequestByObjectResult;
    }


    /**
     * Sets the initiateODEventRequestByObjectResult value for this InitiateODEventRequestByObjectResponse.
     * 
     * @param initiateODEventRequestByObjectResult
     */
    public void setInitiateODEventRequestByObjectResult(com.cannontech.multispeak.ArrayOfErrorObject initiateODEventRequestByObjectResult) {
        this.initiateODEventRequestByObjectResult = initiateODEventRequestByObjectResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateODEventRequestByObjectResponse)) return false;
        InitiateODEventRequestByObjectResponse other = (InitiateODEventRequestByObjectResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.initiateODEventRequestByObjectResult==null && other.getInitiateODEventRequestByObjectResult()==null) || 
             (this.initiateODEventRequestByObjectResult!=null &&
              this.initiateODEventRequestByObjectResult.equals(other.getInitiateODEventRequestByObjectResult())));
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
        if (getInitiateODEventRequestByObjectResult() != null) {
            _hashCode += getInitiateODEventRequestByObjectResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateODEventRequestByObjectResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateODEventRequestByObjectResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initiateODEventRequestByObjectResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateODEventRequestByObjectResult"));
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
