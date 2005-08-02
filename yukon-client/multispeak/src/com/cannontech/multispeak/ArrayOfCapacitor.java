/**
 * ArrayOfCapacitor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfCapacitor  implements java.io.Serializable {
    private com.cannontech.multispeak.Capacitor[] capacitor;

    public ArrayOfCapacitor() {
    }

    public ArrayOfCapacitor(
           com.cannontech.multispeak.Capacitor[] capacitor) {
           this.capacitor = capacitor;
    }


    /**
     * Gets the capacitor value for this ArrayOfCapacitor.
     * 
     * @return capacitor
     */
    public com.cannontech.multispeak.Capacitor[] getCapacitor() {
        return capacitor;
    }


    /**
     * Sets the capacitor value for this ArrayOfCapacitor.
     * 
     * @param capacitor
     */
    public void setCapacitor(com.cannontech.multispeak.Capacitor[] capacitor) {
        this.capacitor = capacitor;
    }

    public com.cannontech.multispeak.Capacitor getCapacitor(int i) {
        return this.capacitor[i];
    }

    public void setCapacitor(int i, com.cannontech.multispeak.Capacitor _value) {
        this.capacitor[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfCapacitor)) return false;
        ArrayOfCapacitor other = (ArrayOfCapacitor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.capacitor==null && other.getCapacitor()==null) || 
             (this.capacitor!=null &&
              java.util.Arrays.equals(this.capacitor, other.getCapacitor())));
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
        if (getCapacitor() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCapacitor());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCapacitor(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfCapacitor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCapacitor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capacitor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
