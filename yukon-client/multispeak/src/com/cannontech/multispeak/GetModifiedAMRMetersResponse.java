/**
 * GetModifiedAMRMetersResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetModifiedAMRMetersResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMetersResult;

    public GetModifiedAMRMetersResponse() {
    }

    public GetModifiedAMRMetersResponse(
           com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMetersResult) {
           this.getModifiedAMRMetersResult = getModifiedAMRMetersResult;
    }


    /**
     * Gets the getModifiedAMRMetersResult value for this GetModifiedAMRMetersResponse.
     * 
     * @return getModifiedAMRMetersResult
     */
    public com.cannontech.multispeak.ArrayOfMeter getGetModifiedAMRMetersResult() {
        return getModifiedAMRMetersResult;
    }


    /**
     * Sets the getModifiedAMRMetersResult value for this GetModifiedAMRMetersResponse.
     * 
     * @param getModifiedAMRMetersResult
     */
    public void setGetModifiedAMRMetersResult(com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMetersResult) {
        this.getModifiedAMRMetersResult = getModifiedAMRMetersResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedAMRMetersResponse)) return false;
        GetModifiedAMRMetersResponse other = (GetModifiedAMRMetersResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedAMRMetersResult==null && other.getGetModifiedAMRMetersResult()==null) || 
             (this.getModifiedAMRMetersResult!=null &&
              this.getModifiedAMRMetersResult.equals(other.getGetModifiedAMRMetersResult())));
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
        if (getGetModifiedAMRMetersResult() != null) {
            _hashCode += getGetModifiedAMRMetersResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetModifiedAMRMetersResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedAMRMetersResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedAMRMetersResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedAMRMetersResult"));
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
