/**
 * UsageItemList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class UsageItemList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.UsageItem[] usageItem;

    public UsageItemList() {
    }

    public UsageItemList(
           com.cannontech.multispeak.service.UsageItem[] usageItem) {
           this.usageItem = usageItem;
    }


    /**
     * Gets the usageItem value for this UsageItemList.
     * 
     * @return usageItem
     */
    public com.cannontech.multispeak.service.UsageItem[] getUsageItem() {
        return usageItem;
    }


    /**
     * Sets the usageItem value for this UsageItemList.
     * 
     * @param usageItem
     */
    public void setUsageItem(com.cannontech.multispeak.service.UsageItem[] usageItem) {
        this.usageItem = usageItem;
    }

    public com.cannontech.multispeak.service.UsageItem getUsageItem(int i) {
        return this.usageItem[i];
    }

    public void setUsageItem(int i, com.cannontech.multispeak.service.UsageItem _value) {
        this.usageItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UsageItemList)) return false;
        UsageItemList other = (UsageItemList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.usageItem==null && other.getUsageItem()==null) || 
             (this.usageItem!=null &&
              java.util.Arrays.equals(this.usageItem, other.getUsageItem())));
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
        if (getUsageItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUsageItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUsageItem(), i);
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
        new org.apache.axis.description.TypeDesc(UsageItemList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItemList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usageItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem"));
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
