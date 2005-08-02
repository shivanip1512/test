/**
 * MspOverCurrentDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspOverCurrentDevice  extends com.cannontech.multispeak.MspSwitchingDevice  implements java.io.Serializable {
    private java.lang.Boolean bypassExists;
    private java.util.Calendar lastService;

    public MspOverCurrentDevice() {
    }

    public MspOverCurrentDevice(
           java.lang.Boolean bypassExists,
           java.util.Calendar lastService) {
           this.bypassExists = bypassExists;
           this.lastService = lastService;
    }


    /**
     * Gets the bypassExists value for this MspOverCurrentDevice.
     * 
     * @return bypassExists
     */
    public java.lang.Boolean getBypassExists() {
        return bypassExists;
    }


    /**
     * Sets the bypassExists value for this MspOverCurrentDevice.
     * 
     * @param bypassExists
     */
    public void setBypassExists(java.lang.Boolean bypassExists) {
        this.bypassExists = bypassExists;
    }


    /**
     * Gets the lastService value for this MspOverCurrentDevice.
     * 
     * @return lastService
     */
    public java.util.Calendar getLastService() {
        return lastService;
    }


    /**
     * Sets the lastService value for this MspOverCurrentDevice.
     * 
     * @param lastService
     */
    public void setLastService(java.util.Calendar lastService) {
        this.lastService = lastService;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspOverCurrentDevice)) return false;
        MspOverCurrentDevice other = (MspOverCurrentDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.bypassExists==null && other.getBypassExists()==null) || 
             (this.bypassExists!=null &&
              this.bypassExists.equals(other.getBypassExists()))) &&
            ((this.lastService==null && other.getLastService()==null) || 
             (this.lastService!=null &&
              this.lastService.equals(other.getLastService())));
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
        if (getBypassExists() != null) {
            _hashCode += getBypassExists().hashCode();
        }
        if (getLastService() != null) {
            _hashCode += getLastService().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspOverCurrentDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bypassExists");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bypassExists"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastService");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastService"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
