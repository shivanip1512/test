/**
 * ArrayOfTransformer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfTransformer  implements java.io.Serializable {
    private com.cannontech.multispeak.Transformer[] transformer;

    public ArrayOfTransformer() {
    }

    public ArrayOfTransformer(
           com.cannontech.multispeak.Transformer[] transformer) {
           this.transformer = transformer;
    }


    /**
     * Gets the transformer value for this ArrayOfTransformer.
     * 
     * @return transformer
     */
    public com.cannontech.multispeak.Transformer[] getTransformer() {
        return transformer;
    }


    /**
     * Sets the transformer value for this ArrayOfTransformer.
     * 
     * @param transformer
     */
    public void setTransformer(com.cannontech.multispeak.Transformer[] transformer) {
        this.transformer = transformer;
    }

    public com.cannontech.multispeak.Transformer getTransformer(int i) {
        return this.transformer[i];
    }

    public void setTransformer(int i, com.cannontech.multispeak.Transformer _value) {
        this.transformer[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfTransformer)) return false;
        ArrayOfTransformer other = (ArrayOfTransformer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transformer==null && other.getTransformer()==null) || 
             (this.transformer!=null &&
              java.util.Arrays.equals(this.transformer, other.getTransformer())));
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
        if (getTransformer() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTransformer());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTransformer(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfTransformer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer"));
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
