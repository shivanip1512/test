/**
 * GetLatestReadingByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetLatestReadingByMeterNoResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.MeterRead getLatestReadingByMeterNoResult;

    public GetLatestReadingByMeterNoResponse() {
    }

    public GetLatestReadingByMeterNoResponse(
           com.cannontech.multispeak.MeterRead getLatestReadingByMeterNoResult) {
           this.getLatestReadingByMeterNoResult = getLatestReadingByMeterNoResult;
    }


    /**
     * Gets the getLatestReadingByMeterNoResult value for this GetLatestReadingByMeterNoResponse.
     * 
     * @return getLatestReadingByMeterNoResult
     */
    public com.cannontech.multispeak.MeterRead getGetLatestReadingByMeterNoResult() {
        return getLatestReadingByMeterNoResult;
    }


    /**
     * Sets the getLatestReadingByMeterNoResult value for this GetLatestReadingByMeterNoResponse.
     * 
     * @param getLatestReadingByMeterNoResult
     */
    public void setGetLatestReadingByMeterNoResult(com.cannontech.multispeak.MeterRead getLatestReadingByMeterNoResult) {
        this.getLatestReadingByMeterNoResult = getLatestReadingByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLatestReadingByMeterNoResponse)) return false;
        GetLatestReadingByMeterNoResponse other = (GetLatestReadingByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getLatestReadingByMeterNoResult==null && other.getGetLatestReadingByMeterNoResult()==null) || 
             (this.getLatestReadingByMeterNoResult!=null &&
              this.getLatestReadingByMeterNoResult.equals(other.getGetLatestReadingByMeterNoResult())));
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
        if (getGetLatestReadingByMeterNoResult() != null) {
            _hashCode += getGetLatestReadingByMeterNoResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetLatestReadingByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLatestReadingByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getLatestReadingByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingByMeterNoResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
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
