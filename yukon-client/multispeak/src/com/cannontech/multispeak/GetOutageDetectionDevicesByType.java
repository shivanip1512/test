/**
 * GetOutageDetectionDevicesByType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetOutageDetectionDevicesByType  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageDetectDeviceType oDDType;
    private java.lang.String lastReceived;

    public GetOutageDetectionDevicesByType() {
    }

    public GetOutageDetectionDevicesByType(
           com.cannontech.multispeak.OutageDetectDeviceType oDDType,
           java.lang.String lastReceived) {
           this.oDDType = oDDType;
           this.lastReceived = lastReceived;
    }


    /**
     * Gets the oDDType value for this GetOutageDetectionDevicesByType.
     * 
     * @return oDDType
     */
    public com.cannontech.multispeak.OutageDetectDeviceType getODDType() {
        return oDDType;
    }


    /**
     * Sets the oDDType value for this GetOutageDetectionDevicesByType.
     * 
     * @param oDDType
     */
    public void setODDType(com.cannontech.multispeak.OutageDetectDeviceType oDDType) {
        this.oDDType = oDDType;
    }


    /**
     * Gets the lastReceived value for this GetOutageDetectionDevicesByType.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetOutageDetectionDevicesByType.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetOutageDetectionDevicesByType)) return false;
        GetOutageDetectionDevicesByType other = (GetOutageDetectionDevicesByType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.oDDType==null && other.getODDType()==null) || 
             (this.oDDType!=null &&
              this.oDDType.equals(other.getODDType()))) &&
            ((this.lastReceived==null && other.getLastReceived()==null) || 
             (this.lastReceived!=null &&
              this.lastReceived.equals(other.getLastReceived())));
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
        if (getODDType() != null) {
            _hashCode += getODDType().hashCode();
        }
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetOutageDetectionDevicesByType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetOutageDetectionDevicesByType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ODDType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReceived");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
