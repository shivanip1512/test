/**
 * PayableItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PayableItem  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.Float pastDueAmount;

    private java.lang.Float currentAmount;

    private java.lang.String description;

    private java.lang.String chargeCode;

    private java.util.Calendar dueDate;

    private java.util.Calendar disconnectDate;

    private java.lang.Float lastPaymentAmount;

    private java.util.Calendar lastPaymentDate;

    public PayableItem() {
    }

    public PayableItem(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.Float pastDueAmount,
           java.lang.Float currentAmount,
           java.lang.String description,
           java.lang.String chargeCode,
           java.util.Calendar dueDate,
           java.util.Calendar disconnectDate,
           java.lang.Float lastPaymentAmount,
           java.util.Calendar lastPaymentDate) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.pastDueAmount = pastDueAmount;
        this.currentAmount = currentAmount;
        this.description = description;
        this.chargeCode = chargeCode;
        this.dueDate = dueDate;
        this.disconnectDate = disconnectDate;
        this.lastPaymentAmount = lastPaymentAmount;
        this.lastPaymentDate = lastPaymentDate;
    }


    /**
     * Gets the pastDueAmount value for this PayableItem.
     * 
     * @return pastDueAmount
     */
    public java.lang.Float getPastDueAmount() {
        return pastDueAmount;
    }


    /**
     * Sets the pastDueAmount value for this PayableItem.
     * 
     * @param pastDueAmount
     */
    public void setPastDueAmount(java.lang.Float pastDueAmount) {
        this.pastDueAmount = pastDueAmount;
    }


    /**
     * Gets the currentAmount value for this PayableItem.
     * 
     * @return currentAmount
     */
    public java.lang.Float getCurrentAmount() {
        return currentAmount;
    }


    /**
     * Sets the currentAmount value for this PayableItem.
     * 
     * @param currentAmount
     */
    public void setCurrentAmount(java.lang.Float currentAmount) {
        this.currentAmount = currentAmount;
    }


    /**
     * Gets the description value for this PayableItem.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this PayableItem.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the chargeCode value for this PayableItem.
     * 
     * @return chargeCode
     */
    public java.lang.String getChargeCode() {
        return chargeCode;
    }


    /**
     * Sets the chargeCode value for this PayableItem.
     * 
     * @param chargeCode
     */
    public void setChargeCode(java.lang.String chargeCode) {
        this.chargeCode = chargeCode;
    }


    /**
     * Gets the dueDate value for this PayableItem.
     * 
     * @return dueDate
     */
    public java.util.Calendar getDueDate() {
        return dueDate;
    }


    /**
     * Sets the dueDate value for this PayableItem.
     * 
     * @param dueDate
     */
    public void setDueDate(java.util.Calendar dueDate) {
        this.dueDate = dueDate;
    }


    /**
     * Gets the disconnectDate value for this PayableItem.
     * 
     * @return disconnectDate
     */
    public java.util.Calendar getDisconnectDate() {
        return disconnectDate;
    }


    /**
     * Sets the disconnectDate value for this PayableItem.
     * 
     * @param disconnectDate
     */
    public void setDisconnectDate(java.util.Calendar disconnectDate) {
        this.disconnectDate = disconnectDate;
    }


    /**
     * Gets the lastPaymentAmount value for this PayableItem.
     * 
     * @return lastPaymentAmount
     */
    public java.lang.Float getLastPaymentAmount() {
        return lastPaymentAmount;
    }


    /**
     * Sets the lastPaymentAmount value for this PayableItem.
     * 
     * @param lastPaymentAmount
     */
    public void setLastPaymentAmount(java.lang.Float lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }


    /**
     * Gets the lastPaymentDate value for this PayableItem.
     * 
     * @return lastPaymentDate
     */
    public java.util.Calendar getLastPaymentDate() {
        return lastPaymentDate;
    }


    /**
     * Sets the lastPaymentDate value for this PayableItem.
     * 
     * @param lastPaymentDate
     */
    public void setLastPaymentDate(java.util.Calendar lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayableItem)) return false;
        PayableItem other = (PayableItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.pastDueAmount==null && other.getPastDueAmount()==null) || 
             (this.pastDueAmount!=null &&
              this.pastDueAmount.equals(other.getPastDueAmount()))) &&
            ((this.currentAmount==null && other.getCurrentAmount()==null) || 
             (this.currentAmount!=null &&
              this.currentAmount.equals(other.getCurrentAmount()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.chargeCode==null && other.getChargeCode()==null) || 
             (this.chargeCode!=null &&
              this.chargeCode.equals(other.getChargeCode()))) &&
            ((this.dueDate==null && other.getDueDate()==null) || 
             (this.dueDate!=null &&
              this.dueDate.equals(other.getDueDate()))) &&
            ((this.disconnectDate==null && other.getDisconnectDate()==null) || 
             (this.disconnectDate!=null &&
              this.disconnectDate.equals(other.getDisconnectDate()))) &&
            ((this.lastPaymentAmount==null && other.getLastPaymentAmount()==null) || 
             (this.lastPaymentAmount!=null &&
              this.lastPaymentAmount.equals(other.getLastPaymentAmount()))) &&
            ((this.lastPaymentDate==null && other.getLastPaymentDate()==null) || 
             (this.lastPaymentDate!=null &&
              this.lastPaymentDate.equals(other.getLastPaymentDate())));
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
        if (getPastDueAmount() != null) {
            _hashCode += getPastDueAmount().hashCode();
        }
        if (getCurrentAmount() != null) {
            _hashCode += getCurrentAmount().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getChargeCode() != null) {
            _hashCode += getChargeCode().hashCode();
        }
        if (getDueDate() != null) {
            _hashCode += getDueDate().hashCode();
        }
        if (getDisconnectDate() != null) {
            _hashCode += getDisconnectDate().hashCode();
        }
        if (getLastPaymentAmount() != null) {
            _hashCode += getLastPaymentAmount().hashCode();
        }
        if (getLastPaymentDate() != null) {
            _hashCode += getLastPaymentDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayableItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pastDueAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pastDueAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "currentAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dueDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dueDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disconnectDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "disconnectDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastPaymentAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastPaymentAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastPaymentDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastPaymentDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
