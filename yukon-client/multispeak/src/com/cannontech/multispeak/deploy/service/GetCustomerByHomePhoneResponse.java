/**
 * GetCustomerByHomePhoneResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetCustomerByHomePhoneResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Customer getCustomerByHomePhoneResult;

    public GetCustomerByHomePhoneResponse() {
    }

    public GetCustomerByHomePhoneResponse(
           com.cannontech.multispeak.deploy.service.Customer getCustomerByHomePhoneResult) {
           this.getCustomerByHomePhoneResult = getCustomerByHomePhoneResult;
    }


    /**
     * Gets the getCustomerByHomePhoneResult value for this GetCustomerByHomePhoneResponse.
     * 
     * @return getCustomerByHomePhoneResult
     */
    public com.cannontech.multispeak.deploy.service.Customer getGetCustomerByHomePhoneResult() {
        return getCustomerByHomePhoneResult;
    }


    /**
     * Sets the getCustomerByHomePhoneResult value for this GetCustomerByHomePhoneResponse.
     * 
     * @param getCustomerByHomePhoneResult
     */
    public void setGetCustomerByHomePhoneResult(com.cannontech.multispeak.deploy.service.Customer getCustomerByHomePhoneResult) {
        this.getCustomerByHomePhoneResult = getCustomerByHomePhoneResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByHomePhoneResponse)) return false;
        GetCustomerByHomePhoneResponse other = (GetCustomerByHomePhoneResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByHomePhoneResult==null && other.getGetCustomerByHomePhoneResult()==null) || 
             (this.getCustomerByHomePhoneResult!=null &&
              this.getCustomerByHomePhoneResult.equals(other.getGetCustomerByHomePhoneResult())));
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
        if (getGetCustomerByHomePhoneResult() != null) {
            _hashCode += getGetCustomerByHomePhoneResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByHomePhoneResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByHomePhoneResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByHomePhoneResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByHomePhoneResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
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
