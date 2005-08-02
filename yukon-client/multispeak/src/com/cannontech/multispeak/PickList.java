/**
 * PickList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PickList  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private com.cannontech.multispeak.PickListItem[] pickListItem;

    public PickList() {
    }

    public PickList(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           com.cannontech.multispeak.PickListItem[] pickListItem) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.pickListItem = pickListItem;
    }


    /**
     * Gets the extensions value for this PickList.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this PickList.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this PickList.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this PickList.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the pickListItem value for this PickList.
     * 
     * @return pickListItem
     */
    public com.cannontech.multispeak.PickListItem[] getPickListItem() {
        return pickListItem;
    }


    /**
     * Sets the pickListItem value for this PickList.
     * 
     * @param pickListItem
     */
    public void setPickListItem(com.cannontech.multispeak.PickListItem[] pickListItem) {
        this.pickListItem = pickListItem;
    }

    public com.cannontech.multispeak.PickListItem getPickListItem(int i) {
        return this.pickListItem[i];
    }

    public void setPickListItem(int i, com.cannontech.multispeak.PickListItem _value) {
        this.pickListItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PickList)) return false;
        PickList other = (PickList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.extensionsList==null && other.getExtensionsList()==null) || 
             (this.extensionsList!=null &&
              this.extensionsList.equals(other.getExtensionsList()))) &&
            ((this.pickListItem==null && other.getPickListItem()==null) || 
             (this.pickListItem!=null &&
              java.util.Arrays.equals(this.pickListItem, other.getPickListItem())));
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
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getExtensionsList() != null) {
            _hashCode += getExtensionsList().hashCode();
        }
        if (getPickListItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPickListItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPickListItem(), i);
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
        new org.apache.axis.description.TypeDesc(PickList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickListItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickListItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickListItem"));
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
