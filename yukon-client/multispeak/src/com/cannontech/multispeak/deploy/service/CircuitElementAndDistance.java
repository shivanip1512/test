/**
 * CircuitElementAndDistance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CircuitElementAndDistance  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CircuitElement circuitElement;

    private com.cannontech.multispeak.deploy.service.LengthUnitValue distance;

    public CircuitElementAndDistance() {
    }

    public CircuitElementAndDistance(
           com.cannontech.multispeak.deploy.service.CircuitElement circuitElement,
           com.cannontech.multispeak.deploy.service.LengthUnitValue distance) {
           this.circuitElement = circuitElement;
           this.distance = distance;
    }


    /**
     * Gets the circuitElement value for this CircuitElementAndDistance.
     * 
     * @return circuitElement
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement getCircuitElement() {
        return circuitElement;
    }


    /**
     * Sets the circuitElement value for this CircuitElementAndDistance.
     * 
     * @param circuitElement
     */
    public void setCircuitElement(com.cannontech.multispeak.deploy.service.CircuitElement circuitElement) {
        this.circuitElement = circuitElement;
    }


    /**
     * Gets the distance value for this CircuitElementAndDistance.
     * 
     * @return distance
     */
    public com.cannontech.multispeak.deploy.service.LengthUnitValue getDistance() {
        return distance;
    }


    /**
     * Sets the distance value for this CircuitElementAndDistance.
     * 
     * @param distance
     */
    public void setDistance(com.cannontech.multispeak.deploy.service.LengthUnitValue distance) {
        this.distance = distance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CircuitElementAndDistance)) return false;
        CircuitElementAndDistance other = (CircuitElementAndDistance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.circuitElement==null && other.getCircuitElement()==null) || 
             (this.circuitElement!=null &&
              this.circuitElement.equals(other.getCircuitElement()))) &&
            ((this.distance==null && other.getDistance()==null) || 
             (this.distance!=null &&
              this.distance.equals(other.getDistance())));
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
        if (getCircuitElement() != null) {
            _hashCode += getCircuitElement().hashCode();
        }
        if (getDistance() != null) {
            _hashCode += getDistance().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CircuitElementAndDistance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementAndDistance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "distance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnitValue"));
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
