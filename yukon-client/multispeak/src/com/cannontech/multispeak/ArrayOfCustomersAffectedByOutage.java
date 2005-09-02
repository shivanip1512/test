/**
 * ArrayOfCustomersAffectedByOutage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfCustomersAffectedByOutage  implements java.io.Serializable {
    private com.cannontech.multispeak.CustomersAffectedByOutage[] customersAffectedByOutage;

    public ArrayOfCustomersAffectedByOutage() {
    }

    public ArrayOfCustomersAffectedByOutage(
           com.cannontech.multispeak.CustomersAffectedByOutage[] customersAffectedByOutage) {
           this.customersAffectedByOutage = customersAffectedByOutage;
    }


    /**
     * Gets the customersAffectedByOutage value for this ArrayOfCustomersAffectedByOutage.
     * 
     * @return customersAffectedByOutage
     */
    public com.cannontech.multispeak.CustomersAffectedByOutage[] getCustomersAffectedByOutage() {
        return customersAffectedByOutage;
    }


    /**
     * Sets the customersAffectedByOutage value for this ArrayOfCustomersAffectedByOutage.
     * 
     * @param customersAffectedByOutage
     */
    public void setCustomersAffectedByOutage(com.cannontech.multispeak.CustomersAffectedByOutage[] customersAffectedByOutage) {
        this.customersAffectedByOutage = customersAffectedByOutage;
    }

    public com.cannontech.multispeak.CustomersAffectedByOutage getCustomersAffectedByOutage(int i) {
        return this.customersAffectedByOutage[i];
    }

    public void setCustomersAffectedByOutage(int i, com.cannontech.multispeak.CustomersAffectedByOutage _value) {
        this.customersAffectedByOutage[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfCustomersAffectedByOutage)) return false;
        ArrayOfCustomersAffectedByOutage other = (ArrayOfCustomersAffectedByOutage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.customersAffectedByOutage==null && other.getCustomersAffectedByOutage()==null) || 
             (this.customersAffectedByOutage!=null &&
              java.util.Arrays.equals(this.customersAffectedByOutage, other.getCustomersAffectedByOutage())));
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
        if (getCustomersAffectedByOutage() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCustomersAffectedByOutage());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCustomersAffectedByOutage(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfCustomersAffectedByOutage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomersAffectedByOutage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customersAffectedByOutage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedByOutage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAffectedByOutage"));
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
