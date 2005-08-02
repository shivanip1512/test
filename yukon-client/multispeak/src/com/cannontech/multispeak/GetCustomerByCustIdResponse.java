/**
 * GetCustomerByCustIdResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetCustomerByCustIdResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.Customer getCustomerByCustIdResult;

    public GetCustomerByCustIdResponse() {
    }

    public GetCustomerByCustIdResponse(
           com.cannontech.multispeak.Customer getCustomerByCustIdResult) {
           this.getCustomerByCustIdResult = getCustomerByCustIdResult;
    }


    /**
     * Gets the getCustomerByCustIdResult value for this GetCustomerByCustIdResponse.
     * 
     * @return getCustomerByCustIdResult
     */
    public com.cannontech.multispeak.Customer getGetCustomerByCustIdResult() {
        return getCustomerByCustIdResult;
    }


    /**
     * Sets the getCustomerByCustIdResult value for this GetCustomerByCustIdResponse.
     * 
     * @param getCustomerByCustIdResult
     */
    public void setGetCustomerByCustIdResult(com.cannontech.multispeak.Customer getCustomerByCustIdResult) {
        this.getCustomerByCustIdResult = getCustomerByCustIdResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByCustIdResponse)) return false;
        GetCustomerByCustIdResponse other = (GetCustomerByCustIdResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByCustIdResult==null && other.getGetCustomerByCustIdResult()==null) || 
             (this.getCustomerByCustIdResult!=null &&
              this.getCustomerByCustIdResult.equals(other.getGetCustomerByCustIdResult())));
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
        if (getGetCustomerByCustIdResult() != null) {
            _hashCode += getGetCustomerByCustIdResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByCustIdResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByCustIdResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByCustIdResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByCustIdResult"));
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
