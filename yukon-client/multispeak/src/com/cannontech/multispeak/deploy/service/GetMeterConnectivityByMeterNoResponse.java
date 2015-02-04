/**
 * GetMeterConnectivityByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetMeterConnectivityByMeterNoResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterConnectivity getMeterConnectivityByMeterNoResult;

    public GetMeterConnectivityByMeterNoResponse() {
    }

    public GetMeterConnectivityByMeterNoResponse(
           com.cannontech.multispeak.deploy.service.MeterConnectivity getMeterConnectivityByMeterNoResult) {
           this.getMeterConnectivityByMeterNoResult = getMeterConnectivityByMeterNoResult;
    }


    /**
     * Gets the getMeterConnectivityByMeterNoResult value for this GetMeterConnectivityByMeterNoResponse.
     * 
     * @return getMeterConnectivityByMeterNoResult
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity getGetMeterConnectivityByMeterNoResult() {
        return getMeterConnectivityByMeterNoResult;
    }


    /**
     * Sets the getMeterConnectivityByMeterNoResult value for this GetMeterConnectivityByMeterNoResponse.
     * 
     * @param getMeterConnectivityByMeterNoResult
     */
    public void setGetMeterConnectivityByMeterNoResult(com.cannontech.multispeak.deploy.service.MeterConnectivity getMeterConnectivityByMeterNoResult) {
        this.getMeterConnectivityByMeterNoResult = getMeterConnectivityByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetMeterConnectivityByMeterNoResponse)) return false;
        GetMeterConnectivityByMeterNoResponse other = (GetMeterConnectivityByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getMeterConnectivityByMeterNoResult==null && other.getGetMeterConnectivityByMeterNoResult()==null) || 
             (this.getMeterConnectivityByMeterNoResult!=null &&
              this.getMeterConnectivityByMeterNoResult.equals(other.getGetMeterConnectivityByMeterNoResult())));
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
        if (getGetMeterConnectivityByMeterNoResult() != null) {
            _hashCode += getGetMeterConnectivityByMeterNoResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMeterConnectivityByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterConnectivityByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getMeterConnectivityByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterConnectivityByMeterNoResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
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
