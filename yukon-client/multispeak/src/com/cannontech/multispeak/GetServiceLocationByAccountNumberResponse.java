/**
 * GetServiceLocationByAccountNumberResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByAccountNumberResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumberResult;

    public GetServiceLocationByAccountNumberResponse() {
    }

    public GetServiceLocationByAccountNumberResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumberResult) {
           this.getServiceLocationByAccountNumberResult = getServiceLocationByAccountNumberResult;
    }


    /**
     * Gets the getServiceLocationByAccountNumberResult value for this GetServiceLocationByAccountNumberResponse.
     * 
     * @return getServiceLocationByAccountNumberResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetServiceLocationByAccountNumberResult() {
        return getServiceLocationByAccountNumberResult;
    }


    /**
     * Sets the getServiceLocationByAccountNumberResult value for this GetServiceLocationByAccountNumberResponse.
     * 
     * @param getServiceLocationByAccountNumberResult
     */
    public void setGetServiceLocationByAccountNumberResult(com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumberResult) {
        this.getServiceLocationByAccountNumberResult = getServiceLocationByAccountNumberResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByAccountNumberResponse)) return false;
        GetServiceLocationByAccountNumberResponse other = (GetServiceLocationByAccountNumberResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByAccountNumberResult==null && other.getGetServiceLocationByAccountNumberResult()==null) || 
             (this.getServiceLocationByAccountNumberResult!=null &&
              this.getServiceLocationByAccountNumberResult.equals(other.getGetServiceLocationByAccountNumberResult())));
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
        if (getGetServiceLocationByAccountNumberResult() != null) {
            _hashCode += getGetServiceLocationByAccountNumberResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByAccountNumberResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByAccountNumberResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByAccountNumberResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByAccountNumberResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
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
