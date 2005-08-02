/**
 * PrimaryCabinet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PrimaryCabinet  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.String structType;
    private com.cannontech.multispeak.CabinetContentsList cabinetContentsList;

    public PrimaryCabinet() {
    }

    public PrimaryCabinet(
           java.lang.String structType,
           com.cannontech.multispeak.CabinetContentsList cabinetContentsList) {
           this.structType = structType;
           this.cabinetContentsList = cabinetContentsList;
    }


    /**
     * Gets the structType value for this PrimaryCabinet.
     * 
     * @return structType
     */
    public java.lang.String getStructType() {
        return structType;
    }


    /**
     * Sets the structType value for this PrimaryCabinet.
     * 
     * @param structType
     */
    public void setStructType(java.lang.String structType) {
        this.structType = structType;
    }


    /**
     * Gets the cabinetContentsList value for this PrimaryCabinet.
     * 
     * @return cabinetContentsList
     */
    public com.cannontech.multispeak.CabinetContentsList getCabinetContentsList() {
        return cabinetContentsList;
    }


    /**
     * Sets the cabinetContentsList value for this PrimaryCabinet.
     * 
     * @param cabinetContentsList
     */
    public void setCabinetContentsList(com.cannontech.multispeak.CabinetContentsList cabinetContentsList) {
        this.cabinetContentsList = cabinetContentsList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrimaryCabinet)) return false;
        PrimaryCabinet other = (PrimaryCabinet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.structType==null && other.getStructType()==null) || 
             (this.structType!=null &&
              this.structType.equals(other.getStructType()))) &&
            ((this.cabinetContentsList==null && other.getCabinetContentsList()==null) || 
             (this.cabinetContentsList!=null &&
              this.cabinetContentsList.equals(other.getCabinetContentsList())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getStructType() != null) {
            _hashCode += getStructType().hashCode();
        }
        if (getCabinetContentsList() != null) {
            _hashCode += getCabinetContentsList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrimaryCabinet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabinet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("structType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "structType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cabinetContentsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsList"));
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
