/**
 * GetCustomerByDBANameResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetCustomerByDBANameResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.Customer getCustomerByDBANameResult;

    public GetCustomerByDBANameResponse() {
    }

    public GetCustomerByDBANameResponse(
           com.cannontech.multispeak.Customer getCustomerByDBANameResult) {
           this.getCustomerByDBANameResult = getCustomerByDBANameResult;
    }


    /**
     * Gets the getCustomerByDBANameResult value for this GetCustomerByDBANameResponse.
     * 
     * @return getCustomerByDBANameResult
     */
    public com.cannontech.multispeak.Customer getGetCustomerByDBANameResult() {
        return getCustomerByDBANameResult;
    }


    /**
     * Sets the getCustomerByDBANameResult value for this GetCustomerByDBANameResponse.
     * 
     * @param getCustomerByDBANameResult
     */
    public void setGetCustomerByDBANameResult(com.cannontech.multispeak.Customer getCustomerByDBANameResult) {
        this.getCustomerByDBANameResult = getCustomerByDBANameResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByDBANameResponse)) return false;
        GetCustomerByDBANameResponse other = (GetCustomerByDBANameResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByDBANameResult==null && other.getGetCustomerByDBANameResult()==null) || 
             (this.getCustomerByDBANameResult!=null &&
              this.getCustomerByDBANameResult.equals(other.getGetCustomerByDBANameResult())));
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
        if (getGetCustomerByDBANameResult() != null) {
            _hashCode += getGetCustomerByDBANameResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByDBANameResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByDBANameResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByDBANameResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByDBANameResult"));
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
