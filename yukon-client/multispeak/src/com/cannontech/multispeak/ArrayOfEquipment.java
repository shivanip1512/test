/**
 * ArrayOfEquipment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfEquipment  implements java.io.Serializable {
    private com.cannontech.multispeak.Equipment[] equipment;

    public ArrayOfEquipment() {
    }

    public ArrayOfEquipment(
           com.cannontech.multispeak.Equipment[] equipment) {
           this.equipment = equipment;
    }


    /**
     * Gets the equipment value for this ArrayOfEquipment.
     * 
     * @return equipment
     */
    public com.cannontech.multispeak.Equipment[] getEquipment() {
        return equipment;
    }


    /**
     * Sets the equipment value for this ArrayOfEquipment.
     * 
     * @param equipment
     */
    public void setEquipment(com.cannontech.multispeak.Equipment[] equipment) {
        this.equipment = equipment;
    }

    public com.cannontech.multispeak.Equipment getEquipment(int i) {
        return this.equipment[i];
    }

    public void setEquipment(int i, com.cannontech.multispeak.Equipment _value) {
        this.equipment[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfEquipment)) return false;
        ArrayOfEquipment other = (ArrayOfEquipment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.equipment==null && other.getEquipment()==null) || 
             (this.equipment!=null &&
              java.util.Arrays.equals(this.equipment, other.getEquipment())));
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
        if (getEquipment() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEquipment());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEquipment(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfEquipment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEquipment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
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
