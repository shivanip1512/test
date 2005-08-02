/**
 * ArrayOfFeederObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfFeederObject  implements java.io.Serializable {
    private com.cannontech.multispeak.FeederObject[] feederObject;

    public ArrayOfFeederObject() {
    }

    public ArrayOfFeederObject(
           com.cannontech.multispeak.FeederObject[] feederObject) {
           this.feederObject = feederObject;
    }


    /**
     * Gets the feederObject value for this ArrayOfFeederObject.
     * 
     * @return feederObject
     */
    public com.cannontech.multispeak.FeederObject[] getFeederObject() {
        return feederObject;
    }


    /**
     * Sets the feederObject value for this ArrayOfFeederObject.
     * 
     * @param feederObject
     */
    public void setFeederObject(com.cannontech.multispeak.FeederObject[] feederObject) {
        this.feederObject = feederObject;
    }

    public com.cannontech.multispeak.FeederObject getFeederObject(int i) {
        return this.feederObject[i];
    }

    public void setFeederObject(int i, com.cannontech.multispeak.FeederObject _value) {
        this.feederObject[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfFeederObject)) return false;
        ArrayOfFeederObject other = (ArrayOfFeederObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.feederObject==null && other.getFeederObject()==null) || 
             (this.feederObject!=null &&
              java.util.Arrays.equals(this.feederObject, other.getFeederObject())));
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
        if (getFeederObject() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFeederObject());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFeederObject(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfFeederObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFeederObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject"));
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
