/**
 * InitiateConnectDisconnectResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class InitiateConnectDisconnectResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject initiateConnectDisconnectResult;

    public InitiateConnectDisconnectResponse() {
    }

    public InitiateConnectDisconnectResponse(
           com.cannontech.multispeak.ArrayOfErrorObject initiateConnectDisconnectResult) {
           this.initiateConnectDisconnectResult = initiateConnectDisconnectResult;
    }


    /**
     * Gets the initiateConnectDisconnectResult value for this InitiateConnectDisconnectResponse.
     * 
     * @return initiateConnectDisconnectResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getInitiateConnectDisconnectResult() {
        return initiateConnectDisconnectResult;
    }


    /**
     * Sets the initiateConnectDisconnectResult value for this InitiateConnectDisconnectResponse.
     * 
     * @param initiateConnectDisconnectResult
     */
    public void setInitiateConnectDisconnectResult(com.cannontech.multispeak.ArrayOfErrorObject initiateConnectDisconnectResult) {
        this.initiateConnectDisconnectResult = initiateConnectDisconnectResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateConnectDisconnectResponse)) return false;
        InitiateConnectDisconnectResponse other = (InitiateConnectDisconnectResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.initiateConnectDisconnectResult==null && other.getInitiateConnectDisconnectResult()==null) || 
             (this.initiateConnectDisconnectResult!=null &&
              this.initiateConnectDisconnectResult.equals(other.getInitiateConnectDisconnectResult())));
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
        if (getInitiateConnectDisconnectResult() != null) {
            _hashCode += getInitiateConnectDisconnectResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateConnectDisconnectResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateConnectDisconnectResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initiateConnectDisconnectResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "InitiateConnectDisconnectResult"));
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
