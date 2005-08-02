/**
 * GetMeterByMeterNoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetMeterByMeterNoResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.Meter getMeterByMeterNoResult;

    public GetMeterByMeterNoResponse() {
    }

    public GetMeterByMeterNoResponse(
           com.cannontech.multispeak.Meter getMeterByMeterNoResult) {
           this.getMeterByMeterNoResult = getMeterByMeterNoResult;
    }


    /**
     * Gets the getMeterByMeterNoResult value for this GetMeterByMeterNoResponse.
     * 
     * @return getMeterByMeterNoResult
     */
    public com.cannontech.multispeak.Meter getGetMeterByMeterNoResult() {
        return getMeterByMeterNoResult;
    }


    /**
     * Sets the getMeterByMeterNoResult value for this GetMeterByMeterNoResponse.
     * 
     * @param getMeterByMeterNoResult
     */
    public void setGetMeterByMeterNoResult(com.cannontech.multispeak.Meter getMeterByMeterNoResult) {
        this.getMeterByMeterNoResult = getMeterByMeterNoResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetMeterByMeterNoResponse)) return false;
        GetMeterByMeterNoResponse other = (GetMeterByMeterNoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getMeterByMeterNoResult==null && other.getGetMeterByMeterNoResult()==null) || 
             (this.getMeterByMeterNoResult!=null &&
              this.getMeterByMeterNoResult.equals(other.getGetMeterByMeterNoResult())));
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
        if (getGetMeterByMeterNoResult() != null) {
            _hashCode += getGetMeterByMeterNoResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMeterByMeterNoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByMeterNoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getMeterByMeterNoResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByMeterNoResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
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
