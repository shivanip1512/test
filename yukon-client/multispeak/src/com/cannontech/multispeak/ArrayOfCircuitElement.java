/**
 * ArrayOfCircuitElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfCircuitElement  implements java.io.Serializable {
    private com.cannontech.multispeak.CircuitElement[] circuitElement;

    public ArrayOfCircuitElement() {
    }

    public ArrayOfCircuitElement(
           com.cannontech.multispeak.CircuitElement[] circuitElement) {
           this.circuitElement = circuitElement;
    }


    /**
     * Gets the circuitElement value for this ArrayOfCircuitElement.
     * 
     * @return circuitElement
     */
    public com.cannontech.multispeak.CircuitElement[] getCircuitElement() {
        return circuitElement;
    }


    /**
     * Sets the circuitElement value for this ArrayOfCircuitElement.
     * 
     * @param circuitElement
     */
    public void setCircuitElement(com.cannontech.multispeak.CircuitElement[] circuitElement) {
        this.circuitElement = circuitElement;
    }

    public com.cannontech.multispeak.CircuitElement getCircuitElement(int i) {
        return this.circuitElement[i];
    }

    public void setCircuitElement(int i, com.cannontech.multispeak.CircuitElement _value) {
        this.circuitElement[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfCircuitElement)) return false;
        ArrayOfCircuitElement other = (ArrayOfCircuitElement) obj;
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
              java.util.Arrays.equals(this.circuitElement, other.getCircuitElement())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCircuitElement());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCircuitElement(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfCircuitElement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
