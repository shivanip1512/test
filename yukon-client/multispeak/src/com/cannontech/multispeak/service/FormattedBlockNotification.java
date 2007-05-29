/**
 * FormattedBlockNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class FormattedBlockNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.service.FormattedBlock changedMeterReads;

    public FormattedBlockNotification() {
    }

    public FormattedBlockNotification(
           com.cannontech.multispeak.service.FormattedBlock changedMeterReads) {
           this.changedMeterReads = changedMeterReads;
    }


    /**
     * Gets the changedMeterReads value for this FormattedBlockNotification.
     * 
     * @return changedMeterReads
     */
    public com.cannontech.multispeak.service.FormattedBlock getChangedMeterReads() {
        return changedMeterReads;
    }


    /**
     * Sets the changedMeterReads value for this FormattedBlockNotification.
     * 
     * @param changedMeterReads
     */
    public void setChangedMeterReads(com.cannontech.multispeak.service.FormattedBlock changedMeterReads) {
        this.changedMeterReads = changedMeterReads;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FormattedBlockNotification)) return false;
        FormattedBlockNotification other = (FormattedBlockNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.changedMeterReads==null && other.getChangedMeterReads()==null) || 
             (this.changedMeterReads!=null &&
              this.changedMeterReads.equals(other.getChangedMeterReads())));
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
        if (getChangedMeterReads() != null) {
            _hashCode += getChangedMeterReads().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FormattedBlockNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">FormattedBlockNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changedMeterReads");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedMeterReads"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"));
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
