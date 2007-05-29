/**
 * PayableItemListAllowableTransactionTypes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class PayableItemListAllowableTransactionTypes  implements java.io.Serializable {
    private java.lang.String[] transactionType;

    public PayableItemListAllowableTransactionTypes() {
    }

    public PayableItemListAllowableTransactionTypes(
           java.lang.String[] transactionType) {
           this.transactionType = transactionType;
    }


    /**
     * Gets the transactionType value for this PayableItemListAllowableTransactionTypes.
     * 
     * @return transactionType
     */
    public java.lang.String[] getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this PayableItemListAllowableTransactionTypes.
     * 
     * @param transactionType
     */
    public void setTransactionType(java.lang.String[] transactionType) {
        this.transactionType = transactionType;
    }

    public java.lang.String getTransactionType(int i) {
        return this.transactionType[i];
    }

    public void setTransactionType(int i, java.lang.String _value) {
        this.transactionType[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayableItemListAllowableTransactionTypes)) return false;
        PayableItemListAllowableTransactionTypes other = (PayableItemListAllowableTransactionTypes) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              java.util.Arrays.equals(this.transactionType, other.getTransactionType())));
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
        if (getTransactionType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTransactionType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTransactionType(), i);
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
        new org.apache.axis.description.TypeDesc(PayableItemListAllowableTransactionTypes.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">payableItemList>allowableTransactionTypes"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionType"));
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
