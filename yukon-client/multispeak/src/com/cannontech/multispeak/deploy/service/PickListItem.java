/**
 * PickListItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PickListItem  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Extensions extensions;

    private com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList;

    private java.lang.String stockNumber;

    private java.lang.Float quantity;

    private com.cannontech.multispeak.deploy.service.UnitActn unitAction;

    private com.cannontech.multispeak.deploy.service.ObjectRef materialItemID;

    public PickListItem() {
    }

    public PickListItem(
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String stockNumber,
           java.lang.Float quantity,
           com.cannontech.multispeak.deploy.service.UnitActn unitAction,
           com.cannontech.multispeak.deploy.service.ObjectRef materialItemID) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.stockNumber = stockNumber;
           this.quantity = quantity;
           this.unitAction = unitAction;
           this.materialItemID = materialItemID;
    }


    /**
     * Gets the extensions value for this PickListItem.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.deploy.service.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this PickListItem.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.deploy.service.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this PickListItem.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.deploy.service.ExtensionsItem[] getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this PickListItem.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the stockNumber value for this PickListItem.
     * 
     * @return stockNumber
     */
    public java.lang.String getStockNumber() {
        return stockNumber;
    }


    /**
     * Sets the stockNumber value for this PickListItem.
     * 
     * @param stockNumber
     */
    public void setStockNumber(java.lang.String stockNumber) {
        this.stockNumber = stockNumber;
    }


    /**
     * Gets the quantity value for this PickListItem.
     * 
     * @return quantity
     */
    public java.lang.Float getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this PickListItem.
     * 
     * @param quantity
     */
    public void setQuantity(java.lang.Float quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the unitAction value for this PickListItem.
     * 
     * @return unitAction
     */
    public com.cannontech.multispeak.deploy.service.UnitActn getUnitAction() {
        return unitAction;
    }


    /**
     * Sets the unitAction value for this PickListItem.
     * 
     * @param unitAction
     */
    public void setUnitAction(com.cannontech.multispeak.deploy.service.UnitActn unitAction) {
        this.unitAction = unitAction;
    }


    /**
     * Gets the materialItemID value for this PickListItem.
     * 
     * @return materialItemID
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getMaterialItemID() {
        return materialItemID;
    }


    /**
     * Sets the materialItemID value for this PickListItem.
     * 
     * @param materialItemID
     */
    public void setMaterialItemID(com.cannontech.multispeak.deploy.service.ObjectRef materialItemID) {
        this.materialItemID = materialItemID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PickListItem)) return false;
        PickListItem other = (PickListItem) obj;
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
              java.util.Arrays.equals(this.extensionsList, other.getExtensionsList()))) &&
            ((this.stockNumber==null && other.getStockNumber()==null) || 
             (this.stockNumber!=null &&
              this.stockNumber.equals(other.getStockNumber()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.unitAction==null && other.getUnitAction()==null) || 
             (this.unitAction!=null &&
              this.unitAction.equals(other.getUnitAction()))) &&
            ((this.materialItemID==null && other.getMaterialItemID()==null) || 
             (this.materialItemID!=null &&
              this.materialItemID.equals(other.getMaterialItemID())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExtensionsList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExtensionsList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStockNumber() != null) {
            _hashCode += getStockNumber().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getUnitAction() != null) {
            _hashCode += getUnitAction().hashCode();
        }
        if (getMaterialItemID() != null) {
            _hashCode += getMaterialItemID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PickListItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickListItem"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stockNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stockNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialItemID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItemID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
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
