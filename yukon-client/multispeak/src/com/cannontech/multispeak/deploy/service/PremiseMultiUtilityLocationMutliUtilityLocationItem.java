/**
 * PremiseMultiUtilityLocationMutliUtilityLocationItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PremiseMultiUtilityLocationMutliUtilityLocationItem  implements java.io.Serializable {
    private java.lang.Object serviceType;

    private java.lang.Object serviceID;

    private java.lang.Object accountNumber;

    private java.lang.Object customerID;

    public PremiseMultiUtilityLocationMutliUtilityLocationItem() {
    }

    public PremiseMultiUtilityLocationMutliUtilityLocationItem(
           java.lang.Object serviceType,
           java.lang.Object serviceID,
           java.lang.Object accountNumber,
           java.lang.Object customerID) {
           this.serviceType = serviceType;
           this.serviceID = serviceID;
           this.accountNumber = accountNumber;
           this.customerID = customerID;
    }


    /**
     * Gets the serviceType value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @return serviceType
     */
    public java.lang.Object getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.Object serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the serviceID value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @return serviceID
     */
    public java.lang.Object getServiceID() {
        return serviceID;
    }


    /**
     * Sets the serviceID value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @param serviceID
     */
    public void setServiceID(java.lang.Object serviceID) {
        this.serviceID = serviceID;
    }


    /**
     * Gets the accountNumber value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @return accountNumber
     */
    public java.lang.Object getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.Object accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the customerID value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @return customerID
     */
    public java.lang.Object getCustomerID() {
        return customerID;
    }


    /**
     * Sets the customerID value for this PremiseMultiUtilityLocationMutliUtilityLocationItem.
     * 
     * @param customerID
     */
    public void setCustomerID(java.lang.Object customerID) {
        this.customerID = customerID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PremiseMultiUtilityLocationMutliUtilityLocationItem)) return false;
        PremiseMultiUtilityLocationMutliUtilityLocationItem other = (PremiseMultiUtilityLocationMutliUtilityLocationItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.serviceID==null && other.getServiceID()==null) || 
             (this.serviceID!=null &&
              this.serviceID.equals(other.getServiceID()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.customerID==null && other.getCustomerID()==null) || 
             (this.customerID!=null &&
              this.customerID.equals(other.getCustomerID())));
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
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getServiceID() != null) {
            _hashCode += getServiceID().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getCustomerID() != null) {
            _hashCode += getCustomerID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PremiseMultiUtilityLocationMutliUtilityLocationItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>premise>multiUtilityLocation>mutliUtilityLocationItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
