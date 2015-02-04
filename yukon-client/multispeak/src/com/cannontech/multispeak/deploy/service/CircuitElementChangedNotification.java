/**
 * CircuitElementChangedNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CircuitElementChangedNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MultiSpeak circuitElements;

    public CircuitElementChangedNotification() {
    }

    public CircuitElementChangedNotification(
           com.cannontech.multispeak.deploy.service.MultiSpeak circuitElements) {
           this.circuitElements = circuitElements;
    }


    /**
     * Gets the circuitElements value for this CircuitElementChangedNotification.
     * 
     * @return circuitElements
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getCircuitElements() {
        return circuitElements;
    }


    /**
     * Sets the circuitElements value for this CircuitElementChangedNotification.
     * 
     * @param circuitElements
     */
    public void setCircuitElements(com.cannontech.multispeak.deploy.service.MultiSpeak circuitElements) {
        this.circuitElements = circuitElements;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CircuitElementChangedNotification)) return false;
        CircuitElementChangedNotification other = (CircuitElementChangedNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.circuitElements==null && other.getCircuitElements()==null) || 
             (this.circuitElements!=null &&
              this.circuitElements.equals(other.getCircuitElements())));
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
        if (getCircuitElements() != null) {
            _hashCode += getCircuitElements().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CircuitElementChangedNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CircuitElementChangedNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitElements");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElements"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeak"));
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
