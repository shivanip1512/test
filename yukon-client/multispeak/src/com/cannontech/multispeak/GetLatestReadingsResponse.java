/**
 * GetLatestReadingsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetLatestReadingsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeterRead getLatestReadingsResult;

    public GetLatestReadingsResponse() {
    }

    public GetLatestReadingsResponse(
           com.cannontech.multispeak.ArrayOfMeterRead getLatestReadingsResult) {
           this.getLatestReadingsResult = getLatestReadingsResult;
    }


    /**
     * Gets the getLatestReadingsResult value for this GetLatestReadingsResponse.
     * 
     * @return getLatestReadingsResult
     */
    public com.cannontech.multispeak.ArrayOfMeterRead getGetLatestReadingsResult() {
        return getLatestReadingsResult;
    }


    /**
     * Sets the getLatestReadingsResult value for this GetLatestReadingsResponse.
     * 
     * @param getLatestReadingsResult
     */
    public void setGetLatestReadingsResult(com.cannontech.multispeak.ArrayOfMeterRead getLatestReadingsResult) {
        this.getLatestReadingsResult = getLatestReadingsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLatestReadingsResponse)) return false;
        GetLatestReadingsResponse other = (GetLatestReadingsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getLatestReadingsResult==null && other.getGetLatestReadingsResult()==null) || 
             (this.getLatestReadingsResult!=null &&
              this.getLatestReadingsResult.equals(other.getGetLatestReadingsResult())));
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
        if (getGetLatestReadingsResult() != null) {
            _hashCode += getGetLatestReadingsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetLatestReadingsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLatestReadingsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getLatestReadingsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLatestReadingsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
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
