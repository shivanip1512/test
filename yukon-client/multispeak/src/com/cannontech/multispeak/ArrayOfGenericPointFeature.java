/**
 * ArrayOfGenericPointFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfGenericPointFeature  implements java.io.Serializable {
    private com.cannontech.multispeak.GenericPointFeature[] genericPointFeature;

    public ArrayOfGenericPointFeature() {
    }

    public ArrayOfGenericPointFeature(
           com.cannontech.multispeak.GenericPointFeature[] genericPointFeature) {
           this.genericPointFeature = genericPointFeature;
    }


    /**
     * Gets the genericPointFeature value for this ArrayOfGenericPointFeature.
     * 
     * @return genericPointFeature
     */
    public com.cannontech.multispeak.GenericPointFeature[] getGenericPointFeature() {
        return genericPointFeature;
    }


    /**
     * Sets the genericPointFeature value for this ArrayOfGenericPointFeature.
     * 
     * @param genericPointFeature
     */
    public void setGenericPointFeature(com.cannontech.multispeak.GenericPointFeature[] genericPointFeature) {
        this.genericPointFeature = genericPointFeature;
    }

    public com.cannontech.multispeak.GenericPointFeature getGenericPointFeature(int i) {
        return this.genericPointFeature[i];
    }

    public void setGenericPointFeature(int i, com.cannontech.multispeak.GenericPointFeature _value) {
        this.genericPointFeature[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfGenericPointFeature)) return false;
        ArrayOfGenericPointFeature other = (ArrayOfGenericPointFeature) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.genericPointFeature==null && other.getGenericPointFeature()==null) || 
             (this.genericPointFeature!=null &&
              java.util.Arrays.equals(this.genericPointFeature, other.getGenericPointFeature())));
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
        if (getGenericPointFeature() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericPointFeature());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericPointFeature(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfGenericPointFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericPointFeature"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericPointFeature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature"));
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
