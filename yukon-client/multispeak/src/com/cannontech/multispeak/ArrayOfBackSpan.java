/**
 * ArrayOfBackSpan.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfBackSpan  implements java.io.Serializable {
    private com.cannontech.multispeak.BackSpan[] backSpan;

    public ArrayOfBackSpan() {
    }

    public ArrayOfBackSpan(
           com.cannontech.multispeak.BackSpan[] backSpan) {
           this.backSpan = backSpan;
    }


    /**
     * Gets the backSpan value for this ArrayOfBackSpan.
     * 
     * @return backSpan
     */
    public com.cannontech.multispeak.BackSpan[] getBackSpan() {
        return backSpan;
    }


    /**
     * Sets the backSpan value for this ArrayOfBackSpan.
     * 
     * @param backSpan
     */
    public void setBackSpan(com.cannontech.multispeak.BackSpan[] backSpan) {
        this.backSpan = backSpan;
    }

    public com.cannontech.multispeak.BackSpan getBackSpan(int i) {
        return this.backSpan[i];
    }

    public void setBackSpan(int i, com.cannontech.multispeak.BackSpan _value) {
        this.backSpan[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfBackSpan)) return false;
        ArrayOfBackSpan other = (ArrayOfBackSpan) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.backSpan==null && other.getBackSpan()==null) || 
             (this.backSpan!=null &&
              java.util.Arrays.equals(this.backSpan, other.getBackSpan())));
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
        if (getBackSpan() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBackSpan());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBackSpan(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfBackSpan.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBackSpan"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("backSpan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan"));
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
