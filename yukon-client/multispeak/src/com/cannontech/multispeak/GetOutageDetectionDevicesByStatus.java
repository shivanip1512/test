/**
 * GetOutageDetectionDevicesByStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetOutageDetectionDevicesByStatus  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus;
    private java.lang.String lastReceived;

    public GetOutageDetectionDevicesByStatus() {
    }

    public GetOutageDetectionDevicesByStatus(
           com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus,
           java.lang.String lastReceived) {
           this.oDDStatus = oDDStatus;
           this.lastReceived = lastReceived;
    }


    /**
     * Gets the oDDStatus value for this GetOutageDetectionDevicesByStatus.
     * 
     * @return oDDStatus
     */
    public com.cannontech.multispeak.OutageDetectDeviceStatus getODDStatus() {
        return oDDStatus;
    }


    /**
     * Sets the oDDStatus value for this GetOutageDetectionDevicesByStatus.
     * 
     * @param oDDStatus
     */
    public void setODDStatus(com.cannontech.multispeak.OutageDetectDeviceStatus oDDStatus) {
        this.oDDStatus = oDDStatus;
    }


    /**
     * Gets the lastReceived value for this GetOutageDetectionDevicesByStatus.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetOutageDetectionDevicesByStatus.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetOutageDetectionDevicesByStatus)) return false;
        GetOutageDetectionDevicesByStatus other = (GetOutageDetectionDevicesByStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.oDDStatus==null && other.getODDStatus()==null) || 
             (this.oDDStatus!=null &&
              this.oDDStatus.equals(other.getODDStatus()))) &&
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
        if (getODDStatus() != null) {
            _hashCode += getODDStatus().hashCode();
        }
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetOutageDetectionDevicesByStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetOutageDetectionDevicesByStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ODDStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oDDStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus"));
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
