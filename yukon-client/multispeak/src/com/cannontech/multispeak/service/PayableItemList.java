/**
 * PayableItemList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class PayableItemList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.PayableItem[] payableItem;
    private com.cannontech.multispeak.service.PayableItemListAllowableTransactionTypes allowableTransactionTypes;

    public PayableItemList() {
    }

    public PayableItemList(
           com.cannontech.multispeak.service.PayableItem[] payableItem,
           com.cannontech.multispeak.service.PayableItemListAllowableTransactionTypes allowableTransactionTypes) {
           this.payableItem = payableItem;
           this.allowableTransactionTypes = allowableTransactionTypes;
    }


    /**
     * Gets the payableItem value for this PayableItemList.
     * 
     * @return payableItem
     */
    public com.cannontech.multispeak.service.PayableItem[] getPayableItem() {
        return payableItem;
    }


    /**
     * Sets the payableItem value for this PayableItemList.
     * 
     * @param payableItem
     */
    public void setPayableItem(com.cannontech.multispeak.service.PayableItem[] payableItem) {
        this.payableItem = payableItem;
    }

    public com.cannontech.multispeak.service.PayableItem getPayableItem(int i) {
        return this.payableItem[i];
    }

    public void setPayableItem(int i, com.cannontech.multispeak.service.PayableItem _value) {
        this.payableItem[i] = _value;
    }


    /**
     * Gets the allowableTransactionTypes value for this PayableItemList.
     * 
     * @return allowableTransactionTypes
     */
    public com.cannontech.multispeak.service.PayableItemListAllowableTransactionTypes getAllowableTransactionTypes() {
        return allowableTransactionTypes;
    }


    /**
     * Sets the allowableTransactionTypes value for this PayableItemList.
     * 
     * @param allowableTransactionTypes
     */
    public void setAllowableTransactionTypes(com.cannontech.multispeak.service.PayableItemListAllowableTransactionTypes allowableTransactionTypes) {
        this.allowableTransactionTypes = allowableTransactionTypes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayableItemList)) return false;
        PayableItemList other = (PayableItemList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.payableItem==null && other.getPayableItem()==null) || 
             (this.payableItem!=null &&
              java.util.Arrays.equals(this.payableItem, other.getPayableItem()))) &&
            ((this.allowableTransactionTypes==null && other.getAllowableTransactionTypes()==null) || 
             (this.allowableTransactionTypes!=null &&
              this.allowableTransactionTypes.equals(other.getAllowableTransactionTypes())));
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
        if (getPayableItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPayableItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPayableItem(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAllowableTransactionTypes() != null) {
            _hashCode += getAllowableTransactionTypes().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayableItemList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payableItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowableTransactionTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allowableTransactionTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">payableItemList>allowableTransactionTypes"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
