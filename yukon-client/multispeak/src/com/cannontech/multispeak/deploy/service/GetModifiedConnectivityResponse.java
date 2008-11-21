/**
 * GetModifiedConnectivityResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetModifiedConnectivityResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MultiSpeak getModifiedConnectivityResult;

    public GetModifiedConnectivityResponse() {
    }

    public GetModifiedConnectivityResponse(
           com.cannontech.multispeak.deploy.service.MultiSpeak getModifiedConnectivityResult) {
           this.getModifiedConnectivityResult = getModifiedConnectivityResult;
    }


    /**
     * Gets the getModifiedConnectivityResult value for this GetModifiedConnectivityResponse.
     * 
     * @return getModifiedConnectivityResult
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getGetModifiedConnectivityResult() {
        return getModifiedConnectivityResult;
    }


    /**
     * Sets the getModifiedConnectivityResult value for this GetModifiedConnectivityResponse.
     * 
     * @param getModifiedConnectivityResult
     */
    public void setGetModifiedConnectivityResult(com.cannontech.multispeak.deploy.service.MultiSpeak getModifiedConnectivityResult) {
        this.getModifiedConnectivityResult = getModifiedConnectivityResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedConnectivityResponse)) return false;
        GetModifiedConnectivityResponse other = (GetModifiedConnectivityResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedConnectivityResult==null && other.getGetModifiedConnectivityResult()==null) || 
             (this.getModifiedConnectivityResult!=null &&
              this.getModifiedConnectivityResult.equals(other.getGetModifiedConnectivityResult())));
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
        if (getGetModifiedConnectivityResult() != null) {
            _hashCode += getGetModifiedConnectivityResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetModifiedConnectivityResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedConnectivityResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedConnectivityResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedConnectivityResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeak"));
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
