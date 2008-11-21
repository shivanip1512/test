/**
 * PaymentExtension.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PaymentExtension  implements java.io.Serializable {
    private java.lang.String accountNumber;

    private java.lang.String typeService;

    private java.lang.String payableItemID;

    private java.lang.Float utilityFee;

    private java.util.Date extendedTo;

    private com.cannontech.multispeak.deploy.service.ResponseCode responseCode;

    public PaymentExtension() {
    }

    public PaymentExtension(
           java.lang.String accountNumber,
           java.lang.String typeService,
           java.lang.String payableItemID,
           java.lang.Float utilityFee,
           java.util.Date extendedTo,
           com.cannontech.multispeak.deploy.service.ResponseCode responseCode) {
           this.accountNumber = accountNumber;
           this.typeService = typeService;
           this.payableItemID = payableItemID;
           this.utilityFee = utilityFee;
           this.extendedTo = extendedTo;
           this.responseCode = responseCode;
    }


    /**
     * Gets the accountNumber value for this PaymentExtension.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this PaymentExtension.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the typeService value for this PaymentExtension.
     * 
     * @return typeService
     */
    public java.lang.String getTypeService() {
        return typeService;
    }


    /**
     * Sets the typeService value for this PaymentExtension.
     * 
     * @param typeService
     */
    public void setTypeService(java.lang.String typeService) {
        this.typeService = typeService;
    }


    /**
     * Gets the payableItemID value for this PaymentExtension.
     * 
     * @return payableItemID
     */
    public java.lang.String getPayableItemID() {
        return payableItemID;
    }


    /**
     * Sets the payableItemID value for this PaymentExtension.
     * 
     * @param payableItemID
     */
    public void setPayableItemID(java.lang.String payableItemID) {
        this.payableItemID = payableItemID;
    }


    /**
     * Gets the utilityFee value for this PaymentExtension.
     * 
     * @return utilityFee
     */
    public java.lang.Float getUtilityFee() {
        return utilityFee;
    }


    /**
     * Sets the utilityFee value for this PaymentExtension.
     * 
     * @param utilityFee
     */
    public void setUtilityFee(java.lang.Float utilityFee) {
        this.utilityFee = utilityFee;
    }


    /**
     * Gets the extendedTo value for this PaymentExtension.
     * 
     * @return extendedTo
     */
    public java.util.Date getExtendedTo() {
        return extendedTo;
    }


    /**
     * Sets the extendedTo value for this PaymentExtension.
     * 
     * @param extendedTo
     */
    public void setExtendedTo(java.util.Date extendedTo) {
        this.extendedTo = extendedTo;
    }


    /**
     * Gets the responseCode value for this PaymentExtension.
     * 
     * @return responseCode
     */
    public com.cannontech.multispeak.deploy.service.ResponseCode getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this PaymentExtension.
     * 
     * @param responseCode
     */
    public void setResponseCode(com.cannontech.multispeak.deploy.service.ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentExtension)) return false;
        PaymentExtension other = (PaymentExtension) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.typeService==null && other.getTypeService()==null) || 
             (this.typeService!=null &&
              this.typeService.equals(other.getTypeService()))) &&
            ((this.payableItemID==null && other.getPayableItemID()==null) || 
             (this.payableItemID!=null &&
              this.payableItemID.equals(other.getPayableItemID()))) &&
            ((this.utilityFee==null && other.getUtilityFee()==null) || 
             (this.utilityFee!=null &&
              this.utilityFee.equals(other.getUtilityFee()))) &&
            ((this.extendedTo==null && other.getExtendedTo()==null) || 
             (this.extendedTo!=null &&
              this.extendedTo.equals(other.getExtendedTo()))) &&
            ((this.responseCode==null && other.getResponseCode()==null) || 
             (this.responseCode!=null &&
              this.responseCode.equals(other.getResponseCode())));
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
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getTypeService() != null) {
            _hashCode += getTypeService().hashCode();
        }
        if (getPayableItemID() != null) {
            _hashCode += getPayableItemID().hashCode();
        }
        if (getUtilityFee() != null) {
            _hashCode += getUtilityFee().hashCode();
        }
        if (getExtendedTo() != null) {
            _hashCode += getExtendedTo().hashCode();
        }
        if (getResponseCode() != null) {
            _hashCode += getResponseCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaymentExtension.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentExtension"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeService");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "typeService"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payableItemID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utilityFee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityFee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extendedTo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extendedTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode"));
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
