/**
 * ChargeItemList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ChargeItemList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ChargeItem[] chargeItem;

    public ChargeItemList() {
    }

    public ChargeItemList(
           com.cannontech.multispeak.service.ChargeItem[] chargeItem) {
           this.chargeItem = chargeItem;
    }


    /**
     * Gets the chargeItem value for this ChargeItemList.
     * 
     * @return chargeItem
     */
    public com.cannontech.multispeak.service.ChargeItem[] getChargeItem() {
        return chargeItem;
    }


    /**
     * Sets the chargeItem value for this ChargeItemList.
     * 
     * @param chargeItem
     */
    public void setChargeItem(com.cannontech.multispeak.service.ChargeItem[] chargeItem) {
        this.chargeItem = chargeItem;
    }

    public com.cannontech.multispeak.service.ChargeItem getChargeItem(int i) {
        return this.chargeItem[i];
    }

    public void setChargeItem(int i, com.cannontech.multispeak.service.ChargeItem _value) {
        this.chargeItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChargeItemList)) return false;
        ChargeItemList other = (ChargeItemList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chargeItem==null && other.getChargeItem()==null) || 
             (this.chargeItem!=null &&
              java.util.Arrays.equals(this.chargeItem, other.getChargeItem())));
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
        if (getChargeItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChargeItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChargeItem(), i);
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
        new org.apache.axis.description.TypeDesc(ChargeItemList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItemList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem"));
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
