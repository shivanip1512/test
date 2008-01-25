/**
 * GetOutageEventStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetOutageEventStatusResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusResult;

    public GetOutageEventStatusResponse() {
    }

    public GetOutageEventStatusResponse(
           com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusResult) {
           this.getOutageEventStatusResult = getOutageEventStatusResult;
    }


    /**
     * Gets the getOutageEventStatusResult value for this GetOutageEventStatusResponse.
     * 
     * @return getOutageEventStatusResult
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getGetOutageEventStatusResult() {
        return getOutageEventStatusResult;
    }


    /**
     * Sets the getOutageEventStatusResult value for this GetOutageEventStatusResponse.
     * 
     * @param getOutageEventStatusResult
     */
    public void setGetOutageEventStatusResult(com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusResult) {
        this.getOutageEventStatusResult = getOutageEventStatusResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetOutageEventStatusResponse)) return false;
        GetOutageEventStatusResponse other = (GetOutageEventStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getOutageEventStatusResult==null && other.getGetOutageEventStatusResult()==null) || 
             (this.getOutageEventStatusResult!=null &&
              this.getOutageEventStatusResult.equals(other.getGetOutageEventStatusResult())));
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
        if (getGetOutageEventStatusResult() != null) {
            _hashCode += getGetOutageEventStatusResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetOutageEventStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetOutageEventStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getOutageEventStatusResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageEventStatusResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
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
