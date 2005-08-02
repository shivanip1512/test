/**
 * ArrayOfGenericAnnotationFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfGenericAnnotationFeature  implements java.io.Serializable {
    private com.cannontech.multispeak.GenericAnnotationFeature[] genericAnnotationFeature;

    public ArrayOfGenericAnnotationFeature() {
    }

    public ArrayOfGenericAnnotationFeature(
           com.cannontech.multispeak.GenericAnnotationFeature[] genericAnnotationFeature) {
           this.genericAnnotationFeature = genericAnnotationFeature;
    }


    /**
     * Gets the genericAnnotationFeature value for this ArrayOfGenericAnnotationFeature.
     * 
     * @return genericAnnotationFeature
     */
    public com.cannontech.multispeak.GenericAnnotationFeature[] getGenericAnnotationFeature() {
        return genericAnnotationFeature;
    }


    /**
     * Sets the genericAnnotationFeature value for this ArrayOfGenericAnnotationFeature.
     * 
     * @param genericAnnotationFeature
     */
    public void setGenericAnnotationFeature(com.cannontech.multispeak.GenericAnnotationFeature[] genericAnnotationFeature) {
        this.genericAnnotationFeature = genericAnnotationFeature;
    }

    public com.cannontech.multispeak.GenericAnnotationFeature getGenericAnnotationFeature(int i) {
        return this.genericAnnotationFeature[i];
    }

    public void setGenericAnnotationFeature(int i, com.cannontech.multispeak.GenericAnnotationFeature _value) {
        this.genericAnnotationFeature[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfGenericAnnotationFeature)) return false;
        ArrayOfGenericAnnotationFeature other = (ArrayOfGenericAnnotationFeature) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.genericAnnotationFeature==null && other.getGenericAnnotationFeature()==null) || 
             (this.genericAnnotationFeature!=null &&
              java.util.Arrays.equals(this.genericAnnotationFeature, other.getGenericAnnotationFeature())));
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
        if (getGenericAnnotationFeature() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericAnnotationFeature());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericAnnotationFeature(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfGenericAnnotationFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericAnnotationFeature"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericAnnotationFeature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
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
