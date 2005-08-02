/**
 * GetCustomerByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetCustomerByMeterNoResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.Customer getCustomerByMeterNoResult;

    public GetCustomerByMeterNoResponse() {
    }

    public GetCustomerByMeterNoResponse(
           com.cannontech.multispeak.Customer getCustomerByMeterNoResult) {
           this.getCustomerByMeterNoResult = getCustomerByMeterNoResult;
    }


    /**
     * Gets the getCustomerByMeterNoResult value for this GetCustomerByMeterNoResponse.
     * 
     * @return getCustomerByMeterNoResult
     */
    public com.cannontech.multispeak.Customer getGetCustomerByMeterNoResult() {
        return getCustomerByMeterNoResult;
    }


    /**
     * Sets the getCustomerByMeterNoResult value for this GetCustomerByMeterNoResponse.
     * 
     * @param getCustomerByMeterNoResult
     */
    public void setGetCustomerByMeterNoResult(com.cannontech.multispeak.Customer getCustomerByMeterNoResult) {
        this.getCustomerByMeterNoResult = getCustomerByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByMeterNoResponse)) return false;
        GetCustomerByMeterNoResponse other = (GetCustomerByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByMeterNoResult==null && other.getGetCustomerByMeterNoResult()==null) || 
             (this.getCustomerByMeterNoResult!=null &&
              this.getCustomerByMeterNoResult.equals(other.getGetCustomerByMeterNoResult())));
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
        if (getGetCustomerByMeterNoResult() != null) {
            _hashCode += getGetCustomerByMeterNoResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNoResult"));
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
