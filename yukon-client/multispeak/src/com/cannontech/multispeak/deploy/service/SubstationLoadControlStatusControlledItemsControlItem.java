/**
 * SubstationLoadControlStatusControlledItemsControlItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SubstationLoadControlStatusControlledItemsControlItem  implements java.io.Serializable {
    private java.math.BigInteger numberOfItems;

    private java.math.BigInteger numberOfControlledItems;

    private java.lang.String description;

    public SubstationLoadControlStatusControlledItemsControlItem() {
    }

    public SubstationLoadControlStatusControlledItemsControlItem(
           java.math.BigInteger numberOfItems,
           java.math.BigInteger numberOfControlledItems,
           java.lang.String description) {
           this.numberOfItems = numberOfItems;
           this.numberOfControlledItems = numberOfControlledItems;
           this.description = description;
    }


    /**
     * Gets the numberOfItems value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @return numberOfItems
     */
    public java.math.BigInteger getNumberOfItems() {
        return numberOfItems;
    }


    /**
     * Sets the numberOfItems value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @param numberOfItems
     */
    public void setNumberOfItems(java.math.BigInteger numberOfItems) {
        this.numberOfItems = numberOfItems;
    }


    /**
     * Gets the numberOfControlledItems value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @return numberOfControlledItems
     */
    public java.math.BigInteger getNumberOfControlledItems() {
        return numberOfControlledItems;
    }


    /**
     * Sets the numberOfControlledItems value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @param numberOfControlledItems
     */
    public void setNumberOfControlledItems(java.math.BigInteger numberOfControlledItems) {
        this.numberOfControlledItems = numberOfControlledItems;
    }


    /**
     * Gets the description value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this SubstationLoadControlStatusControlledItemsControlItem.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubstationLoadControlStatusControlledItemsControlItem)) return false;
        SubstationLoadControlStatusControlledItemsControlItem other = (SubstationLoadControlStatusControlledItemsControlItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.numberOfItems==null && other.getNumberOfItems()==null) || 
             (this.numberOfItems!=null &&
              this.numberOfItems.equals(other.getNumberOfItems()))) &&
            ((this.numberOfControlledItems==null && other.getNumberOfControlledItems()==null) || 
             (this.numberOfControlledItems!=null &&
              this.numberOfControlledItems.equals(other.getNumberOfControlledItems()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription())));
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
        if (getNumberOfItems() != null) {
            _hashCode += getNumberOfItems().hashCode();
        }
        if (getNumberOfControlledItems() != null) {
            _hashCode += getNumberOfControlledItems().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubstationLoadControlStatusControlledItemsControlItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>substationLoadControlStatus>controlledItems>controlItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfControlledItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfControlledItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
