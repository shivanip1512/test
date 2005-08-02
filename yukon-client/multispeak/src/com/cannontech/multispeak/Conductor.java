/**
 * Conductor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Conductor  implements java.io.Serializable {
    private java.lang.String conductorType;
    private com.cannontech.multispeak.MspPhase phase;

    public Conductor() {
    }

    public Conductor(
           java.lang.String conductorType,
           com.cannontech.multispeak.MspPhase phase) {
           this.conductorType = conductorType;
           this.phase = phase;
    }


    /**
     * Gets the conductorType value for this Conductor.
     * 
     * @return conductorType
     */
    public java.lang.String getConductorType() {
        return conductorType;
    }


    /**
     * Sets the conductorType value for this Conductor.
     * 
     * @param conductorType
     */
    public void setConductorType(java.lang.String conductorType) {
        this.conductorType = conductorType;
    }


    /**
     * Gets the phase value for this Conductor.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.MspPhase getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this Conductor.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.MspPhase phase) {
        this.phase = phase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Conductor)) return false;
        Conductor other = (Conductor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conductorType==null && other.getConductorType()==null) || 
             (this.conductorType!=null &&
              this.conductorType.equals(other.getConductorType()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase())));
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
        if (getConductorType() != null) {
            _hashCode += getConductorType().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Conductor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conductorType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductorType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase"));
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
