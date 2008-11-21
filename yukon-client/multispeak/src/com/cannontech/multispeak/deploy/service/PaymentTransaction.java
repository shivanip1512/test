/**
 * PaymentTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PaymentTransaction  implements java.io.Serializable {
    private java.lang.String accountNumber;

    private java.lang.String typeService;

    private com.cannontech.multispeak.deploy.service.CreditCardPayment[] creditCardPayment;

    private com.cannontech.multispeak.deploy.service.ECheckPayment[] eCheckPayment;

    private com.cannontech.multispeak.deploy.service.AchPayment[] achPayment;

    private com.cannontech.multispeak.deploy.service.CashPayment[] cashPayment;

    public PaymentTransaction() {
    }

    public PaymentTransaction(
           java.lang.String accountNumber,
           java.lang.String typeService,
           com.cannontech.multispeak.deploy.service.CreditCardPayment[] creditCardPayment,
           com.cannontech.multispeak.deploy.service.ECheckPayment[] eCheckPayment,
           com.cannontech.multispeak.deploy.service.AchPayment[] achPayment,
           com.cannontech.multispeak.deploy.service.CashPayment[] cashPayment) {
           this.accountNumber = accountNumber;
           this.typeService = typeService;
           this.creditCardPayment = creditCardPayment;
           this.eCheckPayment = eCheckPayment;
           this.achPayment = achPayment;
           this.cashPayment = cashPayment;
    }


    /**
     * Gets the accountNumber value for this PaymentTransaction.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this PaymentTransaction.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the typeService value for this PaymentTransaction.
     * 
     * @return typeService
     */
    public java.lang.String getTypeService() {
        return typeService;
    }


    /**
     * Sets the typeService value for this PaymentTransaction.
     * 
     * @param typeService
     */
    public void setTypeService(java.lang.String typeService) {
        this.typeService = typeService;
    }


    /**
     * Gets the creditCardPayment value for this PaymentTransaction.
     * 
     * @return creditCardPayment
     */
    public com.cannontech.multispeak.deploy.service.CreditCardPayment[] getCreditCardPayment() {
        return creditCardPayment;
    }


    /**
     * Sets the creditCardPayment value for this PaymentTransaction.
     * 
     * @param creditCardPayment
     */
    public void setCreditCardPayment(com.cannontech.multispeak.deploy.service.CreditCardPayment[] creditCardPayment) {
        this.creditCardPayment = creditCardPayment;
    }

    public com.cannontech.multispeak.deploy.service.CreditCardPayment getCreditCardPayment(int i) {
        return this.creditCardPayment[i];
    }

    public void setCreditCardPayment(int i, com.cannontech.multispeak.deploy.service.CreditCardPayment _value) {
        this.creditCardPayment[i] = _value;
    }


    /**
     * Gets the eCheckPayment value for this PaymentTransaction.
     * 
     * @return eCheckPayment
     */
    public com.cannontech.multispeak.deploy.service.ECheckPayment[] getECheckPayment() {
        return eCheckPayment;
    }


    /**
     * Sets the eCheckPayment value for this PaymentTransaction.
     * 
     * @param eCheckPayment
     */
    public void setECheckPayment(com.cannontech.multispeak.deploy.service.ECheckPayment[] eCheckPayment) {
        this.eCheckPayment = eCheckPayment;
    }

    public com.cannontech.multispeak.deploy.service.ECheckPayment getECheckPayment(int i) {
        return this.eCheckPayment[i];
    }

    public void setECheckPayment(int i, com.cannontech.multispeak.deploy.service.ECheckPayment _value) {
        this.eCheckPayment[i] = _value;
    }


    /**
     * Gets the achPayment value for this PaymentTransaction.
     * 
     * @return achPayment
     */
    public com.cannontech.multispeak.deploy.service.AchPayment[] getAchPayment() {
        return achPayment;
    }


    /**
     * Sets the achPayment value for this PaymentTransaction.
     * 
     * @param achPayment
     */
    public void setAchPayment(com.cannontech.multispeak.deploy.service.AchPayment[] achPayment) {
        this.achPayment = achPayment;
    }

    public com.cannontech.multispeak.deploy.service.AchPayment getAchPayment(int i) {
        return this.achPayment[i];
    }

    public void setAchPayment(int i, com.cannontech.multispeak.deploy.service.AchPayment _value) {
        this.achPayment[i] = _value;
    }


    /**
     * Gets the cashPayment value for this PaymentTransaction.
     * 
     * @return cashPayment
     */
    public com.cannontech.multispeak.deploy.service.CashPayment[] getCashPayment() {
        return cashPayment;
    }


    /**
     * Sets the cashPayment value for this PaymentTransaction.
     * 
     * @param cashPayment
     */
    public void setCashPayment(com.cannontech.multispeak.deploy.service.CashPayment[] cashPayment) {
        this.cashPayment = cashPayment;
    }

    public com.cannontech.multispeak.deploy.service.CashPayment getCashPayment(int i) {
        return this.cashPayment[i];
    }

    public void setCashPayment(int i, com.cannontech.multispeak.deploy.service.CashPayment _value) {
        this.cashPayment[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentTransaction)) return false;
        PaymentTransaction other = (PaymentTransaction) obj;
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
            ((this.creditCardPayment==null && other.getCreditCardPayment()==null) || 
             (this.creditCardPayment!=null &&
              java.util.Arrays.equals(this.creditCardPayment, other.getCreditCardPayment()))) &&
            ((this.eCheckPayment==null && other.getECheckPayment()==null) || 
             (this.eCheckPayment!=null &&
              java.util.Arrays.equals(this.eCheckPayment, other.getECheckPayment()))) &&
            ((this.achPayment==null && other.getAchPayment()==null) || 
             (this.achPayment!=null &&
              java.util.Arrays.equals(this.achPayment, other.getAchPayment()))) &&
            ((this.cashPayment==null && other.getCashPayment()==null) || 
             (this.cashPayment!=null &&
              java.util.Arrays.equals(this.cashPayment, other.getCashPayment())));
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
        if (getCreditCardPayment() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCreditCardPayment());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCreditCardPayment(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getECheckPayment() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getECheckPayment());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getECheckPayment(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAchPayment() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAchPayment());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAchPayment(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCashPayment() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCashPayment());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCashPayment(), i);
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
        new org.apache.axis.description.TypeDesc(PaymentTransaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentTransaction"));
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
        elemField.setFieldName("creditCardPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardPayment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ECheckPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eCheckPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eCheckPayment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("achPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "achPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "achPayment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cashPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cashPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cashPayment"));
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
