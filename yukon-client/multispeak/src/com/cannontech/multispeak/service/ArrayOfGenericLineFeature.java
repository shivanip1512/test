/**
 * ArrayOfGenericLineFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ArrayOfGenericLineFeature  implements java.io.Serializable {
    private com.cannontech.multispeak.service.GenericLineFeature[] genericLineFeature;

    public ArrayOfGenericLineFeature() {
    }

    public ArrayOfGenericLineFeature(
           com.cannontech.multispeak.service.GenericLineFeature[] genericLineFeature) {
           this.genericLineFeature = genericLineFeature;
    }


    /**
     * Gets the genericLineFeature value for this ArrayOfGenericLineFeature.
     * 
     * @return genericLineFeature
     */
    public com.cannontech.multispeak.service.GenericLineFeature[] getGenericLineFeature() {
        return genericLineFeature;
    }


    /**
     * Sets the genericLineFeature value for this ArrayOfGenericLineFeature.
     * 
     * @param genericLineFeature
     */
    public void setGenericLineFeature(com.cannontech.multispeak.service.GenericLineFeature[] genericLineFeature) {
        this.genericLineFeature = genericLineFeature;
    }

    public com.cannontech.multispeak.service.GenericLineFeature getGenericLineFeature(int i) {
        return this.genericLineFeature[i];
    }

    public void setGenericLineFeature(int i, com.cannontech.multispeak.service.GenericLineFeature _value) {
        this.genericLineFeature[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfGenericLineFeature)) return false;
        ArrayOfGenericLineFeature other = (ArrayOfGenericLineFeature) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.genericLineFeature==null && other.getGenericLineFeature()==null) || 
             (this.genericLineFeature!=null &&
              java.util.Arrays.equals(this.genericLineFeature, other.getGenericLineFeature())));
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
        if (getGenericLineFeature() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericLineFeature());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericLineFeature(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfGenericLineFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericLineFeature"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericLineFeature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature"));
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
