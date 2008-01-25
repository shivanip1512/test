/**
 * BillingDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class BillingDetail  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String accountNumber;

    private java.lang.String serviceType;

    private java.lang.String servLoc;

    private java.lang.String meterNo;

    private java.lang.String ppmLocationID;

    private java.lang.String revenueMonth;

    private java.lang.String revenueYear;

    private java.util.Calendar cisBillDate;

    private java.lang.String billingCycle;

    private java.lang.Float priorBalance;

    private java.lang.Float payment;

    private java.lang.Float creditCharge;

    private java.lang.Float debitCharge;

    private com.cannontech.multispeak.deploy.service.ChargeItem[] chargeItemList;

    private java.lang.Float CISBalanceAfterBilling;

    public BillingDetail() {
    }

    public BillingDetail(
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
           java.lang.String meterNo,
           java.lang.String ppmLocationID,
           java.lang.String revenueMonth,
           java.lang.String revenueYear,
           java.util.Calendar cisBillDate,
           java.lang.String billingCycle,
           java.lang.Float priorBalance,
           java.lang.Float payment,
           java.lang.Float creditCharge,
           java.lang.Float debitCharge,
           com.cannontech.multispeak.deploy.service.ChargeItem[] chargeItemList,
           java.lang.Float CISBalanceAfterBilling) {
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
        this.meterNo = meterNo;
        this.ppmLocationID = ppmLocationID;
        this.revenueMonth = revenueMonth;
        this.revenueYear = revenueYear;
        this.cisBillDate = cisBillDate;
        this.billingCycle = billingCycle;
        this.priorBalance = priorBalance;
        this.payment = payment;
        this.creditCharge = creditCharge;
        this.debitCharge = debitCharge;
        this.chargeItemList = chargeItemList;
        this.CISBalanceAfterBilling = CISBalanceAfterBilling;
    }


    /**
     * Gets the accountNumber value for this BillingDetail.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this BillingDetail.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the serviceType value for this BillingDetail.
     * 
     * @return serviceType
     */
    public java.lang.String getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this BillingDetail.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.String serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the servLoc value for this BillingDetail.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this BillingDetail.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the meterNo value for this BillingDetail.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this BillingDetail.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the ppmLocationID value for this BillingDetail.
     * 
     * @return ppmLocationID
     */
    public java.lang.String getPpmLocationID() {
        return ppmLocationID;
    }


    /**
     * Sets the ppmLocationID value for this BillingDetail.
     * 
     * @param ppmLocationID
     */
    public void setPpmLocationID(java.lang.String ppmLocationID) {
        this.ppmLocationID = ppmLocationID;
    }


    /**
     * Gets the revenueMonth value for this BillingDetail.
     * 
     * @return revenueMonth
     */
    public java.lang.String getRevenueMonth() {
        return revenueMonth;
    }


    /**
     * Sets the revenueMonth value for this BillingDetail.
     * 
     * @param revenueMonth
     */
    public void setRevenueMonth(java.lang.String revenueMonth) {
        this.revenueMonth = revenueMonth;
    }


    /**
     * Gets the revenueYear value for this BillingDetail.
     * 
     * @return revenueYear
     */
    public java.lang.String getRevenueYear() {
        return revenueYear;
    }


    /**
     * Sets the revenueYear value for this BillingDetail.
     * 
     * @param revenueYear
     */
    public void setRevenueYear(java.lang.String revenueYear) {
        this.revenueYear = revenueYear;
    }


    /**
     * Gets the cisBillDate value for this BillingDetail.
     * 
     * @return cisBillDate
     */
    public java.util.Calendar getCisBillDate() {
        return cisBillDate;
    }


    /**
     * Sets the cisBillDate value for this BillingDetail.
     * 
     * @param cisBillDate
     */
    public void setCisBillDate(java.util.Calendar cisBillDate) {
        this.cisBillDate = cisBillDate;
    }


    /**
     * Gets the billingCycle value for this BillingDetail.
     * 
     * @return billingCycle
     */
    public java.lang.String getBillingCycle() {
        return billingCycle;
    }


    /**
     * Sets the billingCycle value for this BillingDetail.
     * 
     * @param billingCycle
     */
    public void setBillingCycle(java.lang.String billingCycle) {
        this.billingCycle = billingCycle;
    }


    /**
     * Gets the priorBalance value for this BillingDetail.
     * 
     * @return priorBalance
     */
    public java.lang.Float getPriorBalance() {
        return priorBalance;
    }


    /**
     * Sets the priorBalance value for this BillingDetail.
     * 
     * @param priorBalance
     */
    public void setPriorBalance(java.lang.Float priorBalance) {
        this.priorBalance = priorBalance;
    }


    /**
     * Gets the payment value for this BillingDetail.
     * 
     * @return payment
     */
    public java.lang.Float getPayment() {
        return payment;
    }


    /**
     * Sets the payment value for this BillingDetail.
     * 
     * @param payment
     */
    public void setPayment(java.lang.Float payment) {
        this.payment = payment;
    }


    /**
     * Gets the creditCharge value for this BillingDetail.
     * 
     * @return creditCharge
     */
    public java.lang.Float getCreditCharge() {
        return creditCharge;
    }


    /**
     * Sets the creditCharge value for this BillingDetail.
     * 
     * @param creditCharge
     */
    public void setCreditCharge(java.lang.Float creditCharge) {
        this.creditCharge = creditCharge;
    }


    /**
     * Gets the debitCharge value for this BillingDetail.
     * 
     * @return debitCharge
     */
    public java.lang.Float getDebitCharge() {
        return debitCharge;
    }


    /**
     * Sets the debitCharge value for this BillingDetail.
     * 
     * @param debitCharge
     */
    public void setDebitCharge(java.lang.Float debitCharge) {
        this.debitCharge = debitCharge;
    }


    /**
     * Gets the chargeItemList value for this BillingDetail.
     * 
     * @return chargeItemList
     */
    public com.cannontech.multispeak.deploy.service.ChargeItem[] getChargeItemList() {
        return chargeItemList;
    }


    /**
     * Sets the chargeItemList value for this BillingDetail.
     * 
     * @param chargeItemList
     */
    public void setChargeItemList(com.cannontech.multispeak.deploy.service.ChargeItem[] chargeItemList) {
        this.chargeItemList = chargeItemList;
    }


    /**
     * Gets the CISBalanceAfterBilling value for this BillingDetail.
     * 
     * @return CISBalanceAfterBilling
     */
    public java.lang.Float getCISBalanceAfterBilling() {
        return CISBalanceAfterBilling;
    }


    /**
     * Sets the CISBalanceAfterBilling value for this BillingDetail.
     * 
     * @param CISBalanceAfterBilling
     */
    public void setCISBalanceAfterBilling(java.lang.Float CISBalanceAfterBilling) {
        this.CISBalanceAfterBilling = CISBalanceAfterBilling;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BillingDetail)) return false;
        BillingDetail other = (BillingDetail) obj;
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
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.ppmLocationID==null && other.getPpmLocationID()==null) || 
             (this.ppmLocationID!=null &&
              this.ppmLocationID.equals(other.getPpmLocationID()))) &&
            ((this.revenueMonth==null && other.getRevenueMonth()==null) || 
             (this.revenueMonth!=null &&
              this.revenueMonth.equals(other.getRevenueMonth()))) &&
            ((this.revenueYear==null && other.getRevenueYear()==null) || 
             (this.revenueYear!=null &&
              this.revenueYear.equals(other.getRevenueYear()))) &&
            ((this.cisBillDate==null && other.getCisBillDate()==null) || 
             (this.cisBillDate!=null &&
              this.cisBillDate.equals(other.getCisBillDate()))) &&
            ((this.billingCycle==null && other.getBillingCycle()==null) || 
             (this.billingCycle!=null &&
              this.billingCycle.equals(other.getBillingCycle()))) &&
            ((this.priorBalance==null && other.getPriorBalance()==null) || 
             (this.priorBalance!=null &&
              this.priorBalance.equals(other.getPriorBalance()))) &&
            ((this.payment==null && other.getPayment()==null) || 
             (this.payment!=null &&
              this.payment.equals(other.getPayment()))) &&
            ((this.creditCharge==null && other.getCreditCharge()==null) || 
             (this.creditCharge!=null &&
              this.creditCharge.equals(other.getCreditCharge()))) &&
            ((this.debitCharge==null && other.getDebitCharge()==null) || 
             (this.debitCharge!=null &&
              this.debitCharge.equals(other.getDebitCharge()))) &&
            ((this.chargeItemList==null && other.getChargeItemList()==null) || 
             (this.chargeItemList!=null &&
              java.util.Arrays.equals(this.chargeItemList, other.getChargeItemList()))) &&
            ((this.CISBalanceAfterBilling==null && other.getCISBalanceAfterBilling()==null) || 
             (this.CISBalanceAfterBilling!=null &&
              this.CISBalanceAfterBilling.equals(other.getCISBalanceAfterBilling())));
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
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getPpmLocationID() != null) {
            _hashCode += getPpmLocationID().hashCode();
        }
        if (getRevenueMonth() != null) {
            _hashCode += getRevenueMonth().hashCode();
        }
        if (getRevenueYear() != null) {
            _hashCode += getRevenueYear().hashCode();
        }
        if (getCisBillDate() != null) {
            _hashCode += getCisBillDate().hashCode();
        }
        if (getBillingCycle() != null) {
            _hashCode += getBillingCycle().hashCode();
        }
        if (getPriorBalance() != null) {
            _hashCode += getPriorBalance().hashCode();
        }
        if (getPayment() != null) {
            _hashCode += getPayment().hashCode();
        }
        if (getCreditCharge() != null) {
            _hashCode += getCreditCharge().hashCode();
        }
        if (getDebitCharge() != null) {
            _hashCode += getDebitCharge().hashCode();
        }
        if (getChargeItemList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChargeItemList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChargeItemList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCISBalanceAfterBilling() != null) {
            _hashCode += getCISBalanceAfterBilling().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BillingDetail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDetail"));
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
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
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
        elemField.setFieldName("revenueMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revenueMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revenueYear");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revenueYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cisBillDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cisBillDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingCycle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingCycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditCharge");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCharge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("debitCharge");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "debitCharge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeItemList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItemList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeItem"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CISBalanceAfterBilling");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CISBalanceAfterBilling"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
