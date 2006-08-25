/**
 * ArrayOfObjectRef.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ArrayOfObjectRef  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ObjectRef[] objectRef;

    public ArrayOfObjectRef() {
    }

    public ArrayOfObjectRef(
           com.cannontech.multispeak.service.ObjectRef[] objectRef) {
           this.objectRef = objectRef;
    }


    /**
     * Gets the objectRef value for this ArrayOfObjectRef.
     * 
     * @return objectRef
     */
    public com.cannontech.multispeak.service.ObjectRef[] getObjectRef() {
        return objectRef;
    }


    /**
     * Sets the objectRef value for this ArrayOfObjectRef.
     * 
     * @param objectRef
     */
    public void setObjectRef(com.cannontech.multispeak.service.ObjectRef[] objectRef) {
        this.objectRef = objectRef;
    }

    public com.cannontech.multispeak.service.ObjectRef getObjectRef(int i) {
        return this.objectRef[i];
    }

    public void setObjectRef(int i, com.cannontech.multispeak.service.ObjectRef _value) {
        this.objectRef[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfObjectRef)) return false;
        ArrayOfObjectRef other = (ArrayOfObjectRef) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectRef==null && other.getObjectRef()==null) || 
             (this.objectRef!=null &&
              java.util.Arrays.equals(this.objectRef, other.getObjectRef())));
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
        if (getObjectRef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObjectRef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectRef(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfObjectRef.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfObjectRef"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
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
