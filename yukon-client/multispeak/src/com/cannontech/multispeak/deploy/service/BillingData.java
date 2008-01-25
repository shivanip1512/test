/**
 * BillingData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class BillingData  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String accountNumber;

    private java.lang.String serviceType;

    private java.lang.String servLoc;

    private java.lang.String ppmLocationID;

    private java.lang.String meterNo;

    private java.lang.Float CISBalanceAfterBilling;

    private java.util.Calendar billingDate;

    private java.util.Calendar billFromDate;

    private java.util.Calendar billToDate;

    public BillingData() {
    }

    public BillingData(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String accountNumber,
           java.lang.String serviceType,
           java.lang.String servLoc,
           java.lang.String ppmLocationID,
           java.lang.String meterNo,
           java.lang.Float CISBalanceAfterBilling,
           java.util.Calendar billingDate,
           java.util.Calendar billFromDate,
           java.util.Calendar billToDate) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.accountNumber = accountNumber;
        this.serviceType = serviceType;
        this.servLoc = servLoc;
        this.ppmLocationID = ppmLocationID;
        this.meterNo = meterNo;
        this.CISBalanceAfterBilling = CISBalanceAfterBilling;
        this.billingDate = billingDate;
        this.billFromDate = billFromDate;
        this.billToDate = billToDate;
    }


    /**
     * Gets the accountNumber value for this BillingData.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this BillingData.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the serviceType value for this BillingData.
     * 
     * @return serviceType
     */
    public java.lang.String getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this BillingData.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.String serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the servLoc value for this BillingData.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this BillingData.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the ppmLocationID value for this BillingData.
     * 
     * @return ppmLocationID
     */
    public java.lang.String getPpmLocationID() {
        return ppmLocationID;
    }


    /**
     * Sets the ppmLocationID value for this BillingData.
     * 
     * @param ppmLocationID
     */
    public void setPpmLocationID(java.lang.String ppmLocationID) {
        this.ppmLocationID = ppmLocationID;
    }


    /**
     * Gets the meterNo value for this BillingData.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this BillingData.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the CISBalanceAfterBilling value for this BillingData.
     * 
     * @return CISBalanceAfterBilling
     */
    public java.lang.Float getCISBalanceAfterBilling() {
        return CISBalanceAfterBilling;
    }


    /**
     * Sets the CISBalanceAfterBilling value for this BillingData.
     * 
     * @param CISBalanceAfterBilling
     */
    public void setCISBalanceAfterBilling(java.lang.Float CISBalanceAfterBilling) {
        this.CISBalanceAfterBilling = CISBalanceAfterBilling;
    }


    /**
     * Gets the billingDate value for this BillingData.
     * 
     * @return billingDate
     */
    public java.util.Calendar getBillingDate() {
        return billingDate;
    }


    /**
     * Sets the billingDate value for this BillingData.
     * 
     * @param billingDate
     */
    public void setBillingDate(java.util.Calendar billingDate) {
        this.billingDate = billingDate;
    }


    /**
     * Gets the billFromDate value for this BillingData.
     * 
     * @return billFromDate
     */
    public java.util.Calendar getBillFromDate() {
        return billFromDate;
    }


    /**
     * Sets the billFromDate value for this BillingData.
     * 
     * @param billFromDate
     */
    public void setBillFromDate(java.util.Calendar billFromDate) {
        this.billFromDate = billFromDate;
    }


    /**
     * Gets the billToDate value for this BillingData.
     * 
     * @return billToDate
     */
    public java.util.Calendar getBillToDate() {
        return billToDate;
    }


    /**
     * Sets the billToDate value for this BillingData.
     * 
     * @param billToDate
     */
    public void setBillToDate(java.util.Calendar billToDate) {
        this.billToDate = billToDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BillingData)) return false;
        BillingData other = (BillingData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.ppmLocationID==null && other.getPpmLocationID()==null) || 
             (this.ppmLocationID!=null &&
              this.ppmLocationID.equals(other.getPpmLocationID()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.CISBalanceAfterBilling==null && other.getCISBalanceAfterBilling()==null) || 
             (this.CISBalanceAfterBilling!=null &&
              this.CISBalanceAfterBilling.equals(other.getCISBalanceAfterBilling()))) &&
            ((this.billingDate==null && other.getBillingDate()==null) || 
             (this.billingDate!=null &&
              this.billingDate.equals(other.getBillingDate()))) &&
            ((this.billFromDate==null && other.getBillFromDate()==null) || 
             (this.billFromDate!=null &&
              this.billFromDate.equals(other.getBillFromDate()))) &&
            ((this.billToDate==null && other.getBillToDate()==null) || 
             (this.billToDate!=null &&
              this.billToDate.equals(other.getBillToDate())));
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
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getPpmLocationID() != null) {
            _hashCode += getPpmLocationID().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getCISBalanceAfterBilling() != null) {
            _hashCode += getCISBalanceAfterBilling().hashCode();
        }
        if (getBillingDate() != null) {
            _hashCode += getBillingDate().hashCode();
        }
        if (getBillFromDate() != null) {
            _hashCode += getBillFromDate().hashCode();
        }
        if (getBillToDate() != null) {
            _hashCode += getBillToDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BillingData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ppmLocationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CISBalanceAfterBilling");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CISBalanceAfterBilling"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billFromDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billFromDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billToDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billToDate"));
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
