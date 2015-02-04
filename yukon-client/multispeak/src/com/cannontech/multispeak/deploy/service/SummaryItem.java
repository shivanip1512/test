/**
 * SummaryItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SummaryItem  implements java.io.Serializable {
    private java.lang.String itemCode;

    private java.lang.String itemDescription;

    private com.cannontech.multispeak.deploy.service.SummaryItemItemQuantity itemQuantity;

    private com.cannontech.multispeak.deploy.service.SummaryItemItemAmount itemAmount;

    public SummaryItem() {
    }

    public SummaryItem(
           java.lang.String itemCode,
           java.lang.String itemDescription,
           com.cannontech.multispeak.deploy.service.SummaryItemItemQuantity itemQuantity,
           com.cannontech.multispeak.deploy.service.SummaryItemItemAmount itemAmount) {
           this.itemCode = itemCode;
           this.itemDescription = itemDescription;
           this.itemQuantity = itemQuantity;
           this.itemAmount = itemAmount;
    }


    /**
     * Gets the itemCode value for this SummaryItem.
     * 
     * @return itemCode
     */
    public java.lang.String getItemCode() {
        return itemCode;
    }


    /**
     * Sets the itemCode value for this SummaryItem.
     * 
     * @param itemCode
     */
    public void setItemCode(java.lang.String itemCode) {
        this.itemCode = itemCode;
    }


    /**
     * Gets the itemDescription value for this SummaryItem.
     * 
     * @return itemDescription
     */
    public java.lang.String getItemDescription() {
        return itemDescription;
    }


    /**
     * Sets the itemDescription value for this SummaryItem.
     * 
     * @param itemDescription
     */
    public void setItemDescription(java.lang.String itemDescription) {
        this.itemDescription = itemDescription;
    }


    /**
     * Gets the itemQuantity value for this SummaryItem.
     * 
     * @return itemQuantity
     */
    public com.cannontech.multispeak.deploy.service.SummaryItemItemQuantity getItemQuantity() {
        return itemQuantity;
    }


    /**
     * Sets the itemQuantity value for this SummaryItem.
     * 
     * @param itemQuantity
     */
    public void setItemQuantity(com.cannontech.multispeak.deploy.service.SummaryItemItemQuantity itemQuantity) {
        this.itemQuantity = itemQuantity;
    }


    /**
     * Gets the itemAmount value for this SummaryItem.
     * 
     * @return itemAmount
     */
    public com.cannontech.multispeak.deploy.service.SummaryItemItemAmount getItemAmount() {
        return itemAmount;
    }


    /**
     * Sets the itemAmount value for this SummaryItem.
     * 
     * @param itemAmount
     */
    public void setItemAmount(com.cannontech.multispeak.deploy.service.SummaryItemItemAmount itemAmount) {
        this.itemAmount = itemAmount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SummaryItem)) return false;
        SummaryItem other = (SummaryItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemCode==null && other.getItemCode()==null) || 
             (this.itemCode!=null &&
              this.itemCode.equals(other.getItemCode()))) &&
            ((this.itemDescription==null && other.getItemDescription()==null) || 
             (this.itemDescription!=null &&
              this.itemDescription.equals(other.getItemDescription()))) &&
            ((this.itemQuantity==null && other.getItemQuantity()==null) || 
             (this.itemQuantity!=null &&
              this.itemQuantity.equals(other.getItemQuantity()))) &&
            ((this.itemAmount==null && other.getItemAmount()==null) || 
             (this.itemAmount!=null &&
              this.itemAmount.equals(other.getItemAmount())));
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
        if (getItemCode() != null) {
            _hashCode += getItemCode().hashCode();
        }
        if (getItemDescription() != null) {
            _hashCode += getItemDescription().hashCode();
        }
        if (getItemQuantity() != null) {
            _hashCode += getItemQuantity().hashCode();
        }
        if (getItemAmount() != null) {
            _hashCode += getItemAmount().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SummaryItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summaryItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "itemCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "itemDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemQuantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "itemQuantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">summaryItem>itemQuantity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "itemAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">summaryItem>itemAmount"));
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
