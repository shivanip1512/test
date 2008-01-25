/**
 * GetCustomerByMeterNumberAndServiceTypeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetCustomerByMeterNumberAndServiceTypeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceTypeResult;

    public GetCustomerByMeterNumberAndServiceTypeResponse() {
    }

    public GetCustomerByMeterNumberAndServiceTypeResponse(
           com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceTypeResult) {
           this.getCustomerByMeterNumberAndServiceTypeResult = getCustomerByMeterNumberAndServiceTypeResult;
    }


    /**
     * Gets the getCustomerByMeterNumberAndServiceTypeResult value for this GetCustomerByMeterNumberAndServiceTypeResponse.
     * 
     * @return getCustomerByMeterNumberAndServiceTypeResult
     */
    public com.cannontech.multispeak.deploy.service.Customer getGetCustomerByMeterNumberAndServiceTypeResult() {
        return getCustomerByMeterNumberAndServiceTypeResult;
    }


    /**
     * Sets the getCustomerByMeterNumberAndServiceTypeResult value for this GetCustomerByMeterNumberAndServiceTypeResponse.
     * 
     * @param getCustomerByMeterNumberAndServiceTypeResult
     */
    public void setGetCustomerByMeterNumberAndServiceTypeResult(com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceTypeResult) {
        this.getCustomerByMeterNumberAndServiceTypeResult = getCustomerByMeterNumberAndServiceTypeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCustomerByMeterNumberAndServiceTypeResponse)) return false;
        GetCustomerByMeterNumberAndServiceTypeResponse other = (GetCustomerByMeterNumberAndServiceTypeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCustomerByMeterNumberAndServiceTypeResult==null && other.getGetCustomerByMeterNumberAndServiceTypeResult()==null) || 
             (this.getCustomerByMeterNumberAndServiceTypeResult!=null &&
              this.getCustomerByMeterNumberAndServiceTypeResult.equals(other.getGetCustomerByMeterNumberAndServiceTypeResult())));
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
        if (getGetCustomerByMeterNumberAndServiceTypeResult() != null) {
            _hashCode += getGetCustomerByMeterNumberAndServiceTypeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCustomerByMeterNumberAndServiceTypeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCustomerByMeterNumberAndServiceTypeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCustomerByMeterNumberAndServiceTypeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCustomerByMeterNumberAndServiceTypeResult"));
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
