/**
 * FixedChargeCodeList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class FixedChargeCodeList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.FixedCharge[] fixedChargeCode;

    public FixedChargeCodeList() {
    }

    public FixedChargeCodeList(
           com.cannontech.multispeak.service.FixedCharge[] fixedChargeCode) {
           this.fixedChargeCode = fixedChargeCode;
    }


    /**
     * Gets the fixedChargeCode value for this FixedChargeCodeList.
     * 
     * @return fixedChargeCode
     */
    public com.cannontech.multispeak.service.FixedCharge[] getFixedChargeCode() {
        return fixedChargeCode;
    }


    /**
     * Sets the fixedChargeCode value for this FixedChargeCodeList.
     * 
     * @param fixedChargeCode
     */
    public void setFixedChargeCode(com.cannontech.multispeak.service.FixedCharge[] fixedChargeCode) {
        this.fixedChargeCode = fixedChargeCode;
    }

    public com.cannontech.multispeak.service.FixedCharge getFixedChargeCode(int i) {
        return this.fixedChargeCode[i];
    }

    public void setFixedChargeCode(int i, com.cannontech.multispeak.service.FixedCharge _value) {
        this.fixedChargeCode[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FixedChargeCodeList)) return false;
        FixedChargeCodeList other = (FixedChargeCodeList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.fixedChargeCode==null && other.getFixedChargeCode()==null) || 
             (this.fixedChargeCode!=null &&
              java.util.Arrays.equals(this.fixedChargeCode, other.getFixedChargeCode())));
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
        if (getFixedChargeCode() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFixedChargeCode());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFixedChargeCode(), i);
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
        new org.apache.axis.description.TypeDesc(FixedChargeCodeList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCodeList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fixedChargeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedCharge"));
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
