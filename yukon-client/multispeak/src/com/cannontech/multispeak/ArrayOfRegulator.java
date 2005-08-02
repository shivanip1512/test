/**
 * ArrayOfRegulator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfRegulator  implements java.io.Serializable {
    private com.cannontech.multispeak.Regulator[] regulator;

    public ArrayOfRegulator() {
    }

    public ArrayOfRegulator(
           com.cannontech.multispeak.Regulator[] regulator) {
           this.regulator = regulator;
    }


    /**
     * Gets the regulator value for this ArrayOfRegulator.
     * 
     * @return regulator
     */
    public com.cannontech.multispeak.Regulator[] getRegulator() {
        return regulator;
    }


    /**
     * Sets the regulator value for this ArrayOfRegulator.
     * 
     * @param regulator
     */
    public void setRegulator(com.cannontech.multispeak.Regulator[] regulator) {
        this.regulator = regulator;
    }

    public com.cannontech.multispeak.Regulator getRegulator(int i) {
        return this.regulator[i];
    }

    public void setRegulator(int i, com.cannontech.multispeak.Regulator _value) {
        this.regulator[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfRegulator)) return false;
        ArrayOfRegulator other = (ArrayOfRegulator) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.regulator==null && other.getRegulator()==null) || 
             (this.regulator!=null &&
              java.util.Arrays.equals(this.regulator, other.getRegulator())));
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
        if (getRegulator() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegulator());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegulator(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfRegulator.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfRegulator"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regulator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
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
