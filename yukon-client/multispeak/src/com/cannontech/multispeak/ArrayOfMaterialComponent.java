/**
 * ArrayOfMaterialComponent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfMaterialComponent  implements java.io.Serializable {
    private com.cannontech.multispeak.MaterialComponent[] materialComponent;

    public ArrayOfMaterialComponent() {
    }

    public ArrayOfMaterialComponent(
           com.cannontech.multispeak.MaterialComponent[] materialComponent) {
           this.materialComponent = materialComponent;
    }


    /**
     * Gets the materialComponent value for this ArrayOfMaterialComponent.
     * 
     * @return materialComponent
     */
    public com.cannontech.multispeak.MaterialComponent[] getMaterialComponent() {
        return materialComponent;
    }


    /**
     * Sets the materialComponent value for this ArrayOfMaterialComponent.
     * 
     * @param materialComponent
     */
    public void setMaterialComponent(com.cannontech.multispeak.MaterialComponent[] materialComponent) {
        this.materialComponent = materialComponent;
    }

    public com.cannontech.multispeak.MaterialComponent getMaterialComponent(int i) {
        return this.materialComponent[i];
    }

    public void setMaterialComponent(int i, com.cannontech.multispeak.MaterialComponent _value) {
        this.materialComponent[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfMaterialComponent)) return false;
        ArrayOfMaterialComponent other = (ArrayOfMaterialComponent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.materialComponent==null && other.getMaterialComponent()==null) || 
             (this.materialComponent!=null &&
              java.util.Arrays.equals(this.materialComponent, other.getMaterialComponent())));
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
        if (getMaterialComponent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMaterialComponent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMaterialComponent(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfMaterialComponent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMaterialComponent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialComponent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent"));
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
