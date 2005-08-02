/**
 * ArrayOfErrorObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfErrorObject  implements java.io.Serializable {
    private com.cannontech.multispeak.ErrorObject[] errorObject;

    public ArrayOfErrorObject() {
    }

    public ArrayOfErrorObject(
           com.cannontech.multispeak.ErrorObject[] errorObject) {
           this.errorObject = errorObject;
    }


    /**
     * Gets the errorObject value for this ArrayOfErrorObject.
     * 
     * @return errorObject
     */
    public com.cannontech.multispeak.ErrorObject[] getErrorObject() {
        return errorObject;
    }


    /**
     * Sets the errorObject value for this ArrayOfErrorObject.
     * 
     * @param errorObject
     */
    public void setErrorObject(com.cannontech.multispeak.ErrorObject[] errorObject) {
        this.errorObject = errorObject;
    }

    public com.cannontech.multispeak.ErrorObject getErrorObject(int i) {
        return this.errorObject[i];
    }

    public void setErrorObject(int i, com.cannontech.multispeak.ErrorObject _value) {
        this.errorObject[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfErrorObject)) return false;
        ArrayOfErrorObject other = (ArrayOfErrorObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errorObject==null && other.getErrorObject()==null) || 
             (this.errorObject!=null &&
              java.util.Arrays.equals(this.errorObject, other.getErrorObject())));
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
        if (getErrorObject() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErrorObject());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErrorObject(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfErrorObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
