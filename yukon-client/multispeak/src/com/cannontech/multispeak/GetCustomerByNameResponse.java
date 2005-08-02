/**
 * GetCustomerByNameResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetCustomerByNameResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfCustomer getCustomerByNameResult;

    public GetCustomerByNameResponse() {
    }

    public GetCustomerByNameResponse(
           com.cannontech.multispeak.ArrayOfCustomer getCustomerByNameResult) {
           this.getCustomerByNameResult = getCustomerByNameResult;
    }


    /**
     * Gets the getCustomerByNameResult value for this GetCustomerByNameResponse.
     * 
     * @return getCustomerByNameResult
     */
    public com.cannontech.multispeak.ArrayOfCustomer getGetCustomerByNameResult() {
        return getCustomerByNameResult;
    }


    /**
     * Sets the getCustomerByNameResult value for this GetCustomerByNameResponse.
     * 
     * @param getCustomerByNameResult
     */
    public void setGetCustomerByNameResult(com.cannontech.multispeak.ArrayOfCustomer getCustomerByNameResult) {
        this.getCustomerByNameResult = getCustomerByNameResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByNameResponse)) return false;
        GetCustomerByNameResponse other = (GetCustomerByNameResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByNameResult==null && other.getGetCustomerByNameResult()==null) || 
             (this.getCustomerByNameResult!=null &&
              this.getCustomerByNameResult.equals(other.getGetCustomerByNameResult())));
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
        if (getGetCustomerByNameResult() != null) {
            _hashCode += getGetCustomerByNameResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByNameResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByNameResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByNameResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByNameResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomer"));
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
