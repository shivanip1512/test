/**
 * GasNameplateGasPressure.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasNameplateGasPressure  implements java.io.Serializable {
    private java.lang.Float maxPressure;

    private com.cannontech.multispeak.deploy.service.Uom maxPressureUOM;

    public GasNameplateGasPressure() {
    }

    public GasNameplateGasPressure(
           java.lang.Float maxPressure,
           com.cannontech.multispeak.deploy.service.Uom maxPressureUOM) {
           this.maxPressure = maxPressure;
           this.maxPressureUOM = maxPressureUOM;
    }


    /**
     * Gets the maxPressure value for this GasNameplateGasPressure.
     * 
     * @return maxPressure
     */
    public java.lang.Float getMaxPressure() {
        return maxPressure;
    }


    /**
     * Sets the maxPressure value for this GasNameplateGasPressure.
     * 
     * @param maxPressure
     */
    public void setMaxPressure(java.lang.Float maxPressure) {
        this.maxPressure = maxPressure;
    }


    /**
     * Gets the maxPressureUOM value for this GasNameplateGasPressure.
     * 
     * @return maxPressureUOM
     */
    public com.cannontech.multispeak.deploy.service.Uom getMaxPressureUOM() {
        return maxPressureUOM;
    }


    /**
     * Sets the maxPressureUOM value for this GasNameplateGasPressure.
     * 
     * @param maxPressureUOM
     */
    public void setMaxPressureUOM(com.cannontech.multispeak.deploy.service.Uom maxPressureUOM) {
        this.maxPressureUOM = maxPressureUOM;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GasNameplateGasPressure)) return false;
        GasNameplateGasPressure other = (GasNameplateGasPressure) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.maxPressure==null && other.getMaxPressure()==null) || 
             (this.maxPressure!=null &&
              this.maxPressure.equals(other.getMaxPressure()))) &&
            ((this.maxPressureUOM==null && other.getMaxPressureUOM()==null) || 
             (this.maxPressureUOM!=null &&
              this.maxPressureUOM.equals(other.getMaxPressureUOM())));
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
        if (getMaxPressure() != null) {
            _hashCode += getMaxPressure().hashCode();
        }
        if (getMaxPressureUOM() != null) {
            _hashCode += getMaxPressureUOM().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GasNameplateGasPressure.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasPressure"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxPressure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxPressure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxPressureUOM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxPressureUOM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
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
