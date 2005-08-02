/**
 * ExtensionsList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ExtensionsList  implements java.io.Serializable {
    private com.cannontech.multispeak.ExtensionsItem[] extensionsItem;

    public ExtensionsList() {
    }

    public ExtensionsList(
           com.cannontech.multispeak.ExtensionsItem[] extensionsItem) {
           this.extensionsItem = extensionsItem;
    }


    /**
     * Gets the extensionsItem value for this ExtensionsList.
     * 
     * @return extensionsItem
     */
    public com.cannontech.multispeak.ExtensionsItem[] getExtensionsItem() {
        return extensionsItem;
    }


    /**
     * Sets the extensionsItem value for this ExtensionsList.
     * 
     * @param extensionsItem
     */
    public void setExtensionsItem(com.cannontech.multispeak.ExtensionsItem[] extensionsItem) {
        this.extensionsItem = extensionsItem;
    }

    public com.cannontech.multispeak.ExtensionsItem getExtensionsItem(int i) {
        return this.extensionsItem[i];
    }

    public void setExtensionsItem(int i, com.cannontech.multispeak.ExtensionsItem _value) {
        this.extensionsItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExtensionsList)) return false;
        ExtensionsList other = (ExtensionsList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensionsItem==null && other.getExtensionsItem()==null) || 
             (this.extensionsItem!=null &&
              java.util.Arrays.equals(this.extensionsItem, other.getExtensionsItem())));
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
        if (getExtensionsItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExtensionsItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExtensionsItem(), i);
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
        new org.apache.axis.description.TypeDesc(ExtensionsList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionsItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
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
