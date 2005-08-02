/**
 * ArrayOfString1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfString1  implements java.io.Serializable {
    private java.lang.String[] sealNumber;

    public ArrayOfString1() {
    }

    public ArrayOfString1(
           java.lang.String[] sealNumber) {
           this.sealNumber = sealNumber;
    }


    /**
     * Gets the sealNumber value for this ArrayOfString1.
     * 
     * @return sealNumber
     */
    public java.lang.String[] getSealNumber() {
        return sealNumber;
    }


    /**
     * Sets the sealNumber value for this ArrayOfString1.
     * 
     * @param sealNumber
     */
    public void setSealNumber(java.lang.String[] sealNumber) {
        this.sealNumber = sealNumber;
    }

    public java.lang.String getSealNumber(int i) {
        return this.sealNumber[i];
    }

    public void setSealNumber(int i, java.lang.String _value) {
        this.sealNumber[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfString1)) return false;
        ArrayOfString1 other = (ArrayOfString1) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sealNumber==null && other.getSealNumber()==null) || 
             (this.sealNumber!=null &&
              java.util.Arrays.equals(this.sealNumber, other.getSealNumber())));
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
        if (getSealNumber() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSealNumber());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSealNumber(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfString1.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString1"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sealNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sealNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
