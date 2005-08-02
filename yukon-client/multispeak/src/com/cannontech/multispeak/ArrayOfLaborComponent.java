/**
 * ArrayOfLaborComponent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfLaborComponent  implements java.io.Serializable {
    private com.cannontech.multispeak.LaborComponent[] laborComponent;

    public ArrayOfLaborComponent() {
    }

    public ArrayOfLaborComponent(
           com.cannontech.multispeak.LaborComponent[] laborComponent) {
           this.laborComponent = laborComponent;
    }


    /**
     * Gets the laborComponent value for this ArrayOfLaborComponent.
     * 
     * @return laborComponent
     */
    public com.cannontech.multispeak.LaborComponent[] getLaborComponent() {
        return laborComponent;
    }


    /**
     * Sets the laborComponent value for this ArrayOfLaborComponent.
     * 
     * @param laborComponent
     */
    public void setLaborComponent(com.cannontech.multispeak.LaborComponent[] laborComponent) {
        this.laborComponent = laborComponent;
    }

    public com.cannontech.multispeak.LaborComponent getLaborComponent(int i) {
        return this.laborComponent[i];
    }

    public void setLaborComponent(int i, com.cannontech.multispeak.LaborComponent _value) {
        this.laborComponent[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfLaborComponent)) return false;
        ArrayOfLaborComponent other = (ArrayOfLaborComponent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.laborComponent==null && other.getLaborComponent()==null) || 
             (this.laborComponent!=null &&
              java.util.Arrays.equals(this.laborComponent, other.getLaborComponent())));
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
        if (getLaborComponent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLaborComponent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLaborComponent(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfLaborComponent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLaborComponent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborComponent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent"));
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
