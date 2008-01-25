/**
 * AdjustmentItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AdjustmentItem  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.AdjustmentItemBalanceType balanceType;

    private float amount;

    private java.lang.String adjustmentItemID;

    public AdjustmentItem() {
    }

    public AdjustmentItem(
           com.cannontech.multispeak.deploy.service.AdjustmentItemBalanceType balanceType,
           float amount,
           java.lang.String adjustmentItemID) {
           this.balanceType = balanceType;
           this.amount = amount;
           this.adjustmentItemID = adjustmentItemID;
    }


    /**
     * Gets the balanceType value for this AdjustmentItem.
     * 
     * @return balanceType
     */
    public com.cannontech.multispeak.deploy.service.AdjustmentItemBalanceType getBalanceType() {
        return balanceType;
    }


    /**
     * Sets the balanceType value for this AdjustmentItem.
     * 
     * @param balanceType
     */
    public void setBalanceType(com.cannontech.multispeak.deploy.service.AdjustmentItemBalanceType balanceType) {
        this.balanceType = balanceType;
    }


    /**
     * Gets the amount value for this AdjustmentItem.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this AdjustmentItem.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the adjustmentItemID value for this AdjustmentItem.
     * 
     * @return adjustmentItemID
     */
    public java.lang.String getAdjustmentItemID() {
        return adjustmentItemID;
    }


    /**
     * Sets the adjustmentItemID value for this AdjustmentItem.
     * 
     * @param adjustmentItemID
     */
    public void setAdjustmentItemID(java.lang.String adjustmentItemID) {
        this.adjustmentItemID = adjustmentItemID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AdjustmentItem)) return false;
        AdjustmentItem other = (AdjustmentItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.balanceType==null && other.getBalanceType()==null) || 
             (this.balanceType!=null &&
              this.balanceType.equals(other.getBalanceType()))) &&
            this.amount == other.getAmount() &&
            ((this.adjustmentItemID==null && other.getAdjustmentItemID()==null) || 
             (this.adjustmentItemID!=null &&
              this.adjustmentItemID.equals(other.getAdjustmentItemID())));
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
        if (getBalanceType() != null) {
            _hashCode += getBalanceType().hashCode();
        }
        _hashCode += new Float(getAmount()).hashCode();
        if (getAdjustmentItemID() != null) {
            _hashCode += getAdjustmentItemID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AdjustmentItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "balanceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">adjustmentItem>balanceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adjustmentItemID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItemID"));
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
