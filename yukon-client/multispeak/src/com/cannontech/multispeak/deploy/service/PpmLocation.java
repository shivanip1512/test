/**
 * PpmLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PpmLocation  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String serviceType;

    private java.lang.String rateCode;

    private java.lang.String taxDist;

    private java.lang.String fuelCostAdjCode;

    private com.cannontech.multispeak.deploy.service.FixedCharge[] fixedChargeCodeList;

    private java.lang.String servLoc;

    private java.lang.Float priorityBalance;

    private java.lang.Float sharedArrears;

    private com.cannontech.multispeak.deploy.service.MeterRead initialRead;

    private com.cannontech.multispeak.deploy.service.Customer customer;

    private java.lang.Float totalBalance;

    private java.math.BigInteger lastMeterRead;

    private java.util.Calendar lastReadingDate;

    private java.lang.Float depositAmount;

    private java.lang.Float depositPaid;

    private java.lang.Float depositInterest;

    private java.util.Calendar connectDate;

    private java.lang.String billingCycle;

    private java.util.Calendar meterSetDate;

    public PpmLocation() {
    }

    public PpmLocation(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String serviceType,
           java.lang.String rateCode,
           java.lang.String taxDist,
           java.lang.String fuelCostAdjCode,
           com.cannontech.multispeak.deploy.service.FixedCharge[] fixedChargeCodeList,
           java.lang.String servLoc,
           java.lang.Float priorityBalance,
           java.lang.Float sharedArrears,
           com.cannontech.multispeak.deploy.service.MeterRead initialRead,
           com.cannontech.multispeak.deploy.service.Customer customer,
           java.lang.Float totalBalance,
           java.math.BigInteger lastMeterRead,
           java.util.Calendar lastReadingDate,
           java.lang.Float depositAmount,
           java.lang.Float depositPaid,
           java.lang.Float depositInterest,
           java.util.Calendar connectDate,
           java.lang.String billingCycle,
           java.util.Calendar meterSetDate) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.serviceType = serviceType;
        this.rateCode = rateCode;
        this.taxDist = taxDist;
        this.fuelCostAdjCode = fuelCostAdjCode;
        this.fixedChargeCodeList = fixedChargeCodeList;
        this.servLoc = servLoc;
        this.priorityBalance = priorityBalance;
        this.sharedArrears = sharedArrears;
        this.initialRead = initialRead;
        this.customer = customer;
        this.totalBalance = totalBalance;
        this.lastMeterRead = lastMeterRead;
        this.lastReadingDate = lastReadingDate;
        this.depositAmount = depositAmount;
        this.depositPaid = depositPaid;
        this.depositInterest = depositInterest;
        this.connectDate = connectDate;
        this.billingCycle = billingCycle;
        this.meterSetDate = meterSetDate;
    }


    /**
     * Gets the serviceType value for this PpmLocation.
     * 
     * @return serviceType
     */
    public java.lang.String getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this PpmLocation.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.String serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the rateCode value for this PpmLocation.
     * 
     * @return rateCode
     */
    public java.lang.String getRateCode() {
        return rateCode;
    }


    /**
     * Sets the rateCode value for this PpmLocation.
     * 
     * @param rateCode
     */
    public void setRateCode(java.lang.String rateCode) {
        this.rateCode = rateCode;
    }


    /**
     * Gets the taxDist value for this PpmLocation.
     * 
     * @return taxDist
     */
    public java.lang.String getTaxDist() {
        return taxDist;
    }


    /**
     * Sets the taxDist value for this PpmLocation.
     * 
     * @param taxDist
     */
    public void setTaxDist(java.lang.String taxDist) {
        this.taxDist = taxDist;
    }


    /**
     * Gets the fuelCostAdjCode value for this PpmLocation.
     * 
     * @return fuelCostAdjCode
     */
    public java.lang.String getFuelCostAdjCode() {
        return fuelCostAdjCode;
    }


    /**
     * Sets the fuelCostAdjCode value for this PpmLocation.
     * 
     * @param fuelCostAdjCode
     */
    public void setFuelCostAdjCode(java.lang.String fuelCostAdjCode) {
        this.fuelCostAdjCode = fuelCostAdjCode;
    }


    /**
     * Gets the fixedChargeCodeList value for this PpmLocation.
     * 
     * @return fixedChargeCodeList
     */
    public com.cannontech.multispeak.deploy.service.FixedCharge[] getFixedChargeCodeList() {
        return fixedChargeCodeList;
    }


    /**
     * Sets the fixedChargeCodeList value for this PpmLocation.
     * 
     * @param fixedChargeCodeList
     */
    public void setFixedChargeCodeList(com.cannontech.multispeak.deploy.service.FixedCharge[] fixedChargeCodeList) {
        this.fixedChargeCodeList = fixedChargeCodeList;
    }


    /**
     * Gets the servLoc value for this PpmLocation.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this PpmLocation.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the priorityBalance value for this PpmLocation.
     * 
     * @return priorityBalance
     */
    public java.lang.Float getPriorityBalance() {
        return priorityBalance;
    }


    /**
     * Sets the priorityBalance value for this PpmLocation.
     * 
     * @param priorityBalance
     */
    public void setPriorityBalance(java.lang.Float priorityBalance) {
        this.priorityBalance = priorityBalance;
    }


    /**
     * Gets the sharedArrears value for this PpmLocation.
     * 
     * @return sharedArrears
     */
    public java.lang.Float getSharedArrears() {
        return sharedArrears;
    }


    /**
     * Sets the sharedArrears value for this PpmLocation.
     * 
     * @param sharedArrears
     */
    public void setSharedArrears(java.lang.Float sharedArrears) {
        this.sharedArrears = sharedArrears;
    }


    /**
     * Gets the initialRead value for this PpmLocation.
     * 
     * @return initialRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getInitialRead() {
        return initialRead;
    }


    /**
     * Sets the initialRead value for this PpmLocation.
     * 
     * @param initialRead
     */
    public void setInitialRead(com.cannontech.multispeak.deploy.service.MeterRead initialRead) {
        this.initialRead = initialRead;
    }


    /**
     * Gets the customer value for this PpmLocation.
     * 
     * @return customer
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomer() {
        return customer;
    }


    /**
     * Sets the customer value for this PpmLocation.
     * 
     * @param customer
     */
    public void setCustomer(com.cannontech.multispeak.deploy.service.Customer customer) {
        this.customer = customer;
    }


    /**
     * Gets the totalBalance value for this PpmLocation.
     * 
     * @return totalBalance
     */
    public java.lang.Float getTotalBalance() {
        return totalBalance;
    }


    /**
     * Sets the totalBalance value for this PpmLocation.
     * 
     * @param totalBalance
     */
    public void setTotalBalance(java.lang.Float totalBalance) {
        this.totalBalance = totalBalance;
    }


    /**
     * Gets the lastMeterRead value for this PpmLocation.
     * 
     * @return lastMeterRead
     */
    public java.math.BigInteger getLastMeterRead() {
        return lastMeterRead;
    }


    /**
     * Sets the lastMeterRead value for this PpmLocation.
     * 
     * @param lastMeterRead
     */
    public void setLastMeterRead(java.math.BigInteger lastMeterRead) {
        this.lastMeterRead = lastMeterRead;
    }


    /**
     * Gets the lastReadingDate value for this PpmLocation.
     * 
     * @return lastReadingDate
     */
    public java.util.Calendar getLastReadingDate() {
        return lastReadingDate;
    }


    /**
     * Sets the lastReadingDate value for this PpmLocation.
     * 
     * @param lastReadingDate
     */
    public void setLastReadingDate(java.util.Calendar lastReadingDate) {
        this.lastReadingDate = lastReadingDate;
    }


    /**
     * Gets the depositAmount value for this PpmLocation.
     * 
     * @return depositAmount
     */
    public java.lang.Float getDepositAmount() {
        return depositAmount;
    }


    /**
     * Sets the depositAmount value for this PpmLocation.
     * 
     * @param depositAmount
     */
    public void setDepositAmount(java.lang.Float depositAmount) {
        this.depositAmount = depositAmount;
    }


    /**
     * Gets the depositPaid value for this PpmLocation.
     * 
     * @return depositPaid
     */
    public java.lang.Float getDepositPaid() {
        return depositPaid;
    }


    /**
     * Sets the depositPaid value for this PpmLocation.
     * 
     * @param depositPaid
     */
    public void setDepositPaid(java.lang.Float depositPaid) {
        this.depositPaid = depositPaid;
    }


    /**
     * Gets the depositInterest value for this PpmLocation.
     * 
     * @return depositInterest
     */
    public java.lang.Float getDepositInterest() {
        return depositInterest;
    }


    /**
     * Sets the depositInterest value for this PpmLocation.
     * 
     * @param depositInterest
     */
    public void setDepositInterest(java.lang.Float depositInterest) {
        this.depositInterest = depositInterest;
    }


    /**
     * Gets the connectDate value for this PpmLocation.
     * 
     * @return connectDate
     */
    public java.util.Calendar getConnectDate() {
        return connectDate;
    }


    /**
     * Sets the connectDate value for this PpmLocation.
     * 
     * @param connectDate
     */
    public void setConnectDate(java.util.Calendar connectDate) {
        this.connectDate = connectDate;
    }


    /**
     * Gets the billingCycle value for this PpmLocation.
     * 
     * @return billingCycle
     */
    public java.lang.String getBillingCycle() {
        return billingCycle;
    }


    /**
     * Sets the billingCycle value for this PpmLocation.
     * 
     * @param billingCycle
     */
    public void setBillingCycle(java.lang.String billingCycle) {
        this.billingCycle = billingCycle;
    }


    /**
     * Gets the meterSetDate value for this PpmLocation.
     * 
     * @return meterSetDate
     */
    public java.util.Calendar getMeterSetDate() {
        return meterSetDate;
    }


    /**
     * Sets the meterSetDate value for this PpmLocation.
     * 
     * @param meterSetDate
     */
    public void setMeterSetDate(java.util.Calendar meterSetDate) {
        this.meterSetDate = meterSetDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PpmLocation)) return false;
        PpmLocation other = (PpmLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.rateCode==null && other.getRateCode()==null) || 
             (this.rateCode!=null &&
              this.rateCode.equals(other.getRateCode()))) &&
            ((this.taxDist==null && other.getTaxDist()==null) || 
             (this.taxDist!=null &&
              this.taxDist.equals(other.getTaxDist()))) &&
            ((this.fuelCostAdjCode==null && other.getFuelCostAdjCode()==null) || 
             (this.fuelCostAdjCode!=null &&
              this.fuelCostAdjCode.equals(other.getFuelCostAdjCode()))) &&
            ((this.fixedChargeCodeList==null && other.getFixedChargeCodeList()==null) || 
             (this.fixedChargeCodeList!=null &&
              java.util.Arrays.equals(this.fixedChargeCodeList, other.getFixedChargeCodeList()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.priorityBalance==null && other.getPriorityBalance()==null) || 
             (this.priorityBalance!=null &&
              this.priorityBalance.equals(other.getPriorityBalance()))) &&
            ((this.sharedArrears==null && other.getSharedArrears()==null) || 
             (this.sharedArrears!=null &&
              this.sharedArrears.equals(other.getSharedArrears()))) &&
            ((this.initialRead==null && other.getInitialRead()==null) || 
             (this.initialRead!=null &&
              this.initialRead.equals(other.getInitialRead()))) &&
            ((this.customer==null && other.getCustomer()==null) || 
             (this.customer!=null &&
              this.customer.equals(other.getCustomer()))) &&
            ((this.totalBalance==null && other.getTotalBalance()==null) || 
             (this.totalBalance!=null &&
              this.totalBalance.equals(other.getTotalBalance()))) &&
            ((this.lastMeterRead==null && other.getLastMeterRead()==null) || 
             (this.lastMeterRead!=null &&
              this.lastMeterRead.equals(other.getLastMeterRead()))) &&
            ((this.lastReadingDate==null && other.getLastReadingDate()==null) || 
             (this.lastReadingDate!=null &&
              this.lastReadingDate.equals(other.getLastReadingDate()))) &&
            ((this.depositAmount==null && other.getDepositAmount()==null) || 
             (this.depositAmount!=null &&
              this.depositAmount.equals(other.getDepositAmount()))) &&
            ((this.depositPaid==null && other.getDepositPaid()==null) || 
             (this.depositPaid!=null &&
              this.depositPaid.equals(other.getDepositPaid()))) &&
            ((this.depositInterest==null && other.getDepositInterest()==null) || 
             (this.depositInterest!=null &&
              this.depositInterest.equals(other.getDepositInterest()))) &&
            ((this.connectDate==null && other.getConnectDate()==null) || 
             (this.connectDate!=null &&
              this.connectDate.equals(other.getConnectDate()))) &&
            ((this.billingCycle==null && other.getBillingCycle()==null) || 
             (this.billingCycle!=null &&
              this.billingCycle.equals(other.getBillingCycle()))) &&
            ((this.meterSetDate==null && other.getMeterSetDate()==null) || 
             (this.meterSetDate!=null &&
              this.meterSetDate.equals(other.getMeterSetDate())));
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
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getRateCode() != null) {
            _hashCode += getRateCode().hashCode();
        }
        if (getTaxDist() != null) {
            _hashCode += getTaxDist().hashCode();
        }
        if (getFuelCostAdjCode() != null) {
            _hashCode += getFuelCostAdjCode().hashCode();
        }
        if (getFixedChargeCodeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFixedChargeCodeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFixedChargeCodeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getPriorityBalance() != null) {
            _hashCode += getPriorityBalance().hashCode();
        }
        if (getSharedArrears() != null) {
            _hashCode += getSharedArrears().hashCode();
        }
        if (getInitialRead() != null) {
            _hashCode += getInitialRead().hashCode();
        }
        if (getCustomer() != null) {
            _hashCode += getCustomer().hashCode();
        }
        if (getTotalBalance() != null) {
            _hashCode += getTotalBalance().hashCode();
        }
        if (getLastMeterRead() != null) {
            _hashCode += getLastMeterRead().hashCode();
        }
        if (getLastReadingDate() != null) {
            _hashCode += getLastReadingDate().hashCode();
        }
        if (getDepositAmount() != null) {
            _hashCode += getDepositAmount().hashCode();
        }
        if (getDepositPaid() != null) {
            _hashCode += getDepositPaid().hashCode();
        }
        if (getDepositInterest() != null) {
            _hashCode += getDepositInterest().hashCode();
        }
        if (getConnectDate() != null) {
            _hashCode += getConnectDate().hashCode();
        }
        if (getBillingCycle() != null) {
            _hashCode += getBillingCycle().hashCode();
        }
        if (getMeterSetDate() != null) {
            _hashCode += getMeterSetDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PpmLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rateCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "rateCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxDist");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "taxDist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fuelCostAdjCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuelCostAdjCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fixedChargeCodeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCodeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedCharge"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fixedChargeCode"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sharedArrears");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sharedArrears"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initialRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "initialRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "totalBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastMeterRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastMeterRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReadingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReadingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depositAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "depositAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depositPaid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "depositPaid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depositInterest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "depositInterest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDate"));
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
        elemField.setFieldName("meterSetDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterSetDate"));
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
