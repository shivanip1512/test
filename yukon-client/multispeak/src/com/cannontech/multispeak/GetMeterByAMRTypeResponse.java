/**
 * GetMeterByAMRTypeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetMeterByAMRTypeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeter getMeterByAMRTypeResult;

    public GetMeterByAMRTypeResponse() {
    }

    public GetMeterByAMRTypeResponse(
           com.cannontech.multispeak.ArrayOfMeter getMeterByAMRTypeResult) {
           this.getMeterByAMRTypeResult = getMeterByAMRTypeResult;
    }


    /**
     * Gets the getMeterByAMRTypeResult value for this GetMeterByAMRTypeResponse.
     * 
     * @return getMeterByAMRTypeResult
     */
    public com.cannontech.multispeak.ArrayOfMeter getGetMeterByAMRTypeResult() {
        return getMeterByAMRTypeResult;
    }


    /**
     * Sets the getMeterByAMRTypeResult value for this GetMeterByAMRTypeResponse.
     * 
     * @param getMeterByAMRTypeResult
     */
    public void setGetMeterByAMRTypeResult(com.cannontech.multispeak.ArrayOfMeter getMeterByAMRTypeResult) {
        this.getMeterByAMRTypeResult = getMeterByAMRTypeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetMeterByAMRTypeResponse)) return false;
        GetMeterByAMRTypeResponse other = (GetMeterByAMRTypeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getMeterByAMRTypeResult==null && other.getGetMeterByAMRTypeResult()==null) || 
             (this.getMeterByAMRTypeResult!=null &&
              this.getMeterByAMRTypeResult.equals(other.getGetMeterByAMRTypeResult())));
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
        if (getGetMeterByAMRTypeResult() != null) {
            _hashCode += getGetMeterByAMRTypeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMeterByAMRTypeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetMeterByAMRTypeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getMeterByAMRTypeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMeterByAMRTypeResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
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
