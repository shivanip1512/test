/**
 * MaterialItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MaterialItem  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String stockNumber;

    private java.lang.String stockDescr;

    private java.lang.String item;

    private java.lang.Float avgCost;

    private java.lang.Float newCost;

    private java.lang.Float laborFactor;

    private java.lang.String materialType;

    private com.cannontech.multispeak.deploy.service.MaterialUnits materialUnits;

    private com.cannontech.multispeak.deploy.service.WarehouseLocation[] warehouseLocationList;

    private java.util.Calendar effectiveDate;

    private java.lang.String materialClass;

    public MaterialItem() {
    }

    public MaterialItem(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String stockNumber,
           java.lang.String stockDescr,
           java.lang.String item,
           java.lang.Float avgCost,
           java.lang.Float newCost,
           java.lang.Float laborFactor,
           java.lang.String materialType,
           com.cannontech.multispeak.deploy.service.MaterialUnits materialUnits,
           com.cannontech.multispeak.deploy.service.WarehouseLocation[] warehouseLocationList,
           java.util.Calendar effectiveDate,
           java.lang.String materialClass) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.stockNumber = stockNumber;
        this.stockDescr = stockDescr;
        this.item = item;
        this.avgCost = avgCost;
        this.newCost = newCost;
        this.laborFactor = laborFactor;
        this.materialType = materialType;
        this.materialUnits = materialUnits;
        this.warehouseLocationList = warehouseLocationList;
        this.effectiveDate = effectiveDate;
        this.materialClass = materialClass;
    }


    /**
     * Gets the stockNumber value for this MaterialItem.
     * 
     * @return stockNumber
     */
    public java.lang.String getStockNumber() {
        return stockNumber;
    }


    /**
     * Sets the stockNumber value for this MaterialItem.
     * 
     * @param stockNumber
     */
    public void setStockNumber(java.lang.String stockNumber) {
        this.stockNumber = stockNumber;
    }


    /**
     * Gets the stockDescr value for this MaterialItem.
     * 
     * @return stockDescr
     */
    public java.lang.String getStockDescr() {
        return stockDescr;
    }


    /**
     * Sets the stockDescr value for this MaterialItem.
     * 
     * @param stockDescr
     */
    public void setStockDescr(java.lang.String stockDescr) {
        this.stockDescr = stockDescr;
    }


    /**
     * Gets the item value for this MaterialItem.
     * 
     * @return item
     */
    public java.lang.String getItem() {
        return item;
    }


    /**
     * Sets the item value for this MaterialItem.
     * 
     * @param item
     */
    public void setItem(java.lang.String item) {
        this.item = item;
    }


    /**
     * Gets the avgCost value for this MaterialItem.
     * 
     * @return avgCost
     */
    public java.lang.Float getAvgCost() {
        return avgCost;
    }


    /**
     * Sets the avgCost value for this MaterialItem.
     * 
     * @param avgCost
     */
    public void setAvgCost(java.lang.Float avgCost) {
        this.avgCost = avgCost;
    }


    /**
     * Gets the newCost value for this MaterialItem.
     * 
     * @return newCost
     */
    public java.lang.Float getNewCost() {
        return newCost;
    }


    /**
     * Sets the newCost value for this MaterialItem.
     * 
     * @param newCost
     */
    public void setNewCost(java.lang.Float newCost) {
        this.newCost = newCost;
    }


    /**
     * Gets the laborFactor value for this MaterialItem.
     * 
     * @return laborFactor
     */
    public java.lang.Float getLaborFactor() {
        return laborFactor;
    }


    /**
     * Sets the laborFactor value for this MaterialItem.
     * 
     * @param laborFactor
     */
    public void setLaborFactor(java.lang.Float laborFactor) {
        this.laborFactor = laborFactor;
    }


    /**
     * Gets the materialType value for this MaterialItem.
     * 
     * @return materialType
     */
    public java.lang.String getMaterialType() {
        return materialType;
    }


    /**
     * Sets the materialType value for this MaterialItem.
     * 
     * @param materialType
     */
    public void setMaterialType(java.lang.String materialType) {
        this.materialType = materialType;
    }


    /**
     * Gets the materialUnits value for this MaterialItem.
     * 
     * @return materialUnits
     */
    public com.cannontech.multispeak.deploy.service.MaterialUnits getMaterialUnits() {
        return materialUnits;
    }


    /**
     * Sets the materialUnits value for this MaterialItem.
     * 
     * @param materialUnits
     */
    public void setMaterialUnits(com.cannontech.multispeak.deploy.service.MaterialUnits materialUnits) {
        this.materialUnits = materialUnits;
    }


    /**
     * Gets the warehouseLocationList value for this MaterialItem.
     * 
     * @return warehouseLocationList
     */
    public com.cannontech.multispeak.deploy.service.WarehouseLocation[] getWarehouseLocationList() {
        return warehouseLocationList;
    }


    /**
     * Sets the warehouseLocationList value for this MaterialItem.
     * 
     * @param warehouseLocationList
     */
    public void setWarehouseLocationList(com.cannontech.multispeak.deploy.service.WarehouseLocation[] warehouseLocationList) {
        this.warehouseLocationList = warehouseLocationList;
    }


    /**
     * Gets the effectiveDate value for this MaterialItem.
     * 
     * @return effectiveDate
     */
    public java.util.Calendar getEffectiveDate() {
        return effectiveDate;
    }


    /**
     * Sets the effectiveDate value for this MaterialItem.
     * 
     * @param effectiveDate
     */
    public void setEffectiveDate(java.util.Calendar effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    /**
     * Gets the materialClass value for this MaterialItem.
     * 
     * @return materialClass
     */
    public java.lang.String getMaterialClass() {
        return materialClass;
    }


    /**
     * Sets the materialClass value for this MaterialItem.
     * 
     * @param materialClass
     */
    public void setMaterialClass(java.lang.String materialClass) {
        this.materialClass = materialClass;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaterialItem)) return false;
        MaterialItem other = (MaterialItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.stockNumber==null && other.getStockNumber()==null) || 
             (this.stockNumber!=null &&
              this.stockNumber.equals(other.getStockNumber()))) &&
            ((this.stockDescr==null && other.getStockDescr()==null) || 
             (this.stockDescr!=null &&
              this.stockDescr.equals(other.getStockDescr()))) &&
            ((this.item==null && other.getItem()==null) || 
             (this.item!=null &&
              this.item.equals(other.getItem()))) &&
            ((this.avgCost==null && other.getAvgCost()==null) || 
             (this.avgCost!=null &&
              this.avgCost.equals(other.getAvgCost()))) &&
            ((this.newCost==null && other.getNewCost()==null) || 
             (this.newCost!=null &&
              this.newCost.equals(other.getNewCost()))) &&
            ((this.laborFactor==null && other.getLaborFactor()==null) || 
             (this.laborFactor!=null &&
              this.laborFactor.equals(other.getLaborFactor()))) &&
            ((this.materialType==null && other.getMaterialType()==null) || 
             (this.materialType!=null &&
              this.materialType.equals(other.getMaterialType()))) &&
            ((this.materialUnits==null && other.getMaterialUnits()==null) || 
             (this.materialUnits!=null &&
              this.materialUnits.equals(other.getMaterialUnits()))) &&
            ((this.warehouseLocationList==null && other.getWarehouseLocationList()==null) || 
             (this.warehouseLocationList!=null &&
              java.util.Arrays.equals(this.warehouseLocationList, other.getWarehouseLocationList()))) &&
            ((this.effectiveDate==null && other.getEffectiveDate()==null) || 
             (this.effectiveDate!=null &&
              this.effectiveDate.equals(other.getEffectiveDate()))) &&
            ((this.materialClass==null && other.getMaterialClass()==null) || 
             (this.materialClass!=null &&
              this.materialClass.equals(other.getMaterialClass())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getStockNumber() != null) {
            _hashCode += getStockNumber().hashCode();
        }
        if (getStockDescr() != null) {
            _hashCode += getStockDescr().hashCode();
        }
        if (getItem() != null) {
            _hashCode += getItem().hashCode();
        }
        if (getAvgCost() != null) {
            _hashCode += getAvgCost().hashCode();
        }
        if (getNewCost() != null) {
            _hashCode += getNewCost().hashCode();
        }
        if (getLaborFactor() != null) {
            _hashCode += getLaborFactor().hashCode();
        }
        if (getMaterialType() != null) {
            _hashCode += getMaterialType().hashCode();
        }
        if (getMaterialUnits() != null) {
            _hashCode += getMaterialUnits().hashCode();
        }
        if (getWarehouseLocationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWarehouseLocationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWarehouseLocationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEffectiveDate() != null) {
            _hashCode += getEffectiveDate().hashCode();
        }
        if (getMaterialClass() != null) {
            _hashCode += getMaterialClass().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaterialItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stockNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stockNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stockDescr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stockDescr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "item"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avgCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "avgCost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newCost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborFactor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborFactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialUnits");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialUnits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialUnits"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warehouseLocationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effectiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "effectiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialClass"));
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
