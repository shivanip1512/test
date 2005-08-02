/**
 * ServiceLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ServiceLocation  extends com.cannontech.multispeak.MspElectricPoint  implements java.io.Serializable {
    private java.lang.String custID;
    private java.lang.String accountNumber;
    private java.lang.String facilityName;
    private java.lang.String siteID;
    private java.lang.String servAddr1;
    private java.lang.String servAddr2;
    private java.lang.String servCity;
    private java.lang.String servState;
    private java.lang.String servZip;
    private java.lang.String servType;
    private java.lang.String revenueClass;
    private java.lang.String servStatus;
    private com.cannontech.multispeak.PowerStatus outageStatus;
    private java.lang.String billingCycle;
    private java.lang.String route;
    private java.lang.String specialNeeds;
    private java.lang.String loadMgmt;
    private java.lang.String budgBill;
    private java.lang.Float acRecvBal;
    private java.lang.Float acRecvCur;
    private java.lang.Float acRecv30;
    private java.lang.Float acRecv60;
    private java.lang.Float acRecv90;
    private java.util.Calendar paymentDueDate;
    private java.util.Calendar lastPaymentDate;
    private java.lang.Float lastPaymentAmount;
    private java.util.Calendar billDate;
    private java.util.Calendar shutOffDate;
    private java.lang.String connection;
    private java.util.Calendar connectDate;
    private java.util.Calendar disconnectDate;
    private com.cannontech.multispeak.Network network;
    private java.lang.String SIC;
    private java.lang.Boolean isCogenerationSite;

    public ServiceLocation() {
    }

    public ServiceLocation(
           java.lang.String custID,
           java.lang.String accountNumber,
           java.lang.String facilityName,
           java.lang.String siteID,
           java.lang.String servAddr1,
           java.lang.String servAddr2,
           java.lang.String servCity,
           java.lang.String servState,
           java.lang.String servZip,
           java.lang.String servType,
           java.lang.String revenueClass,
           java.lang.String servStatus,
           com.cannontech.multispeak.PowerStatus outageStatus,
           java.lang.String billingCycle,
           java.lang.String route,
           java.lang.String specialNeeds,
           java.lang.String loadMgmt,
           java.lang.String budgBill,
           java.lang.Float acRecvBal,
           java.lang.Float acRecvCur,
           java.lang.Float acRecv30,
           java.lang.Float acRecv60,
           java.lang.Float acRecv90,
           java.util.Calendar paymentDueDate,
           java.util.Calendar lastPaymentDate,
           java.lang.Float lastPaymentAmount,
           java.util.Calendar billDate,
           java.util.Calendar shutOffDate,
           java.lang.String connection,
           java.util.Calendar connectDate,
           java.util.Calendar disconnectDate,
           com.cannontech.multispeak.Network network,
           java.lang.String SIC,
           java.lang.Boolean isCogenerationSite) {
           this.custID = custID;
           this.accountNumber = accountNumber;
           this.facilityName = facilityName;
           this.siteID = siteID;
           this.servAddr1 = servAddr1;
           this.servAddr2 = servAddr2;
           this.servCity = servCity;
           this.servState = servState;
           this.servZip = servZip;
           this.servType = servType;
           this.revenueClass = revenueClass;
           this.servStatus = servStatus;
           this.outageStatus = outageStatus;
           this.billingCycle = billingCycle;
           this.route = route;
           this.specialNeeds = specialNeeds;
           this.loadMgmt = loadMgmt;
           this.budgBill = budgBill;
           this.acRecvBal = acRecvBal;
           this.acRecvCur = acRecvCur;
           this.acRecv30 = acRecv30;
           this.acRecv60 = acRecv60;
           this.acRecv90 = acRecv90;
           this.paymentDueDate = paymentDueDate;
           this.lastPaymentDate = lastPaymentDate;
           this.lastPaymentAmount = lastPaymentAmount;
           this.billDate = billDate;
           this.shutOffDate = shutOffDate;
           this.connection = connection;
           this.connectDate = connectDate;
           this.disconnectDate = disconnectDate;
           this.network = network;
           this.SIC = SIC;
           this.isCogenerationSite = isCogenerationSite;
    }


    /**
     * Gets the custID value for this ServiceLocation.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this ServiceLocation.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the accountNumber value for this ServiceLocation.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this ServiceLocation.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the facilityName value for this ServiceLocation.
     * 
     * @return facilityName
     */
    public java.lang.String getFacilityName() {
        return facilityName;
    }


    /**
     * Sets the facilityName value for this ServiceLocation.
     * 
     * @param facilityName
     */
    public void setFacilityName(java.lang.String facilityName) {
        this.facilityName = facilityName;
    }


    /**
     * Gets the siteID value for this ServiceLocation.
     * 
     * @return siteID
     */
    public java.lang.String getSiteID() {
        return siteID;
    }


    /**
     * Sets the siteID value for this ServiceLocation.
     * 
     * @param siteID
     */
    public void setSiteID(java.lang.String siteID) {
        this.siteID = siteID;
    }


    /**
     * Gets the servAddr1 value for this ServiceLocation.
     * 
     * @return servAddr1
     */
    public java.lang.String getServAddr1() {
        return servAddr1;
    }


    /**
     * Sets the servAddr1 value for this ServiceLocation.
     * 
     * @param servAddr1
     */
    public void setServAddr1(java.lang.String servAddr1) {
        this.servAddr1 = servAddr1;
    }


    /**
     * Gets the servAddr2 value for this ServiceLocation.
     * 
     * @return servAddr2
     */
    public java.lang.String getServAddr2() {
        return servAddr2;
    }


    /**
     * Sets the servAddr2 value for this ServiceLocation.
     * 
     * @param servAddr2
     */
    public void setServAddr2(java.lang.String servAddr2) {
        this.servAddr2 = servAddr2;
    }


    /**
     * Gets the servCity value for this ServiceLocation.
     * 
     * @return servCity
     */
    public java.lang.String getServCity() {
        return servCity;
    }


    /**
     * Sets the servCity value for this ServiceLocation.
     * 
     * @param servCity
     */
    public void setServCity(java.lang.String servCity) {
        this.servCity = servCity;
    }


    /**
     * Gets the servState value for this ServiceLocation.
     * 
     * @return servState
     */
    public java.lang.String getServState() {
        return servState;
    }


    /**
     * Sets the servState value for this ServiceLocation.
     * 
     * @param servState
     */
    public void setServState(java.lang.String servState) {
        this.servState = servState;
    }


    /**
     * Gets the servZip value for this ServiceLocation.
     * 
     * @return servZip
     */
    public java.lang.String getServZip() {
        return servZip;
    }


    /**
     * Sets the servZip value for this ServiceLocation.
     * 
     * @param servZip
     */
    public void setServZip(java.lang.String servZip) {
        this.servZip = servZip;
    }


    /**
     * Gets the servType value for this ServiceLocation.
     * 
     * @return servType
     */
    public java.lang.String getServType() {
        return servType;
    }


    /**
     * Sets the servType value for this ServiceLocation.
     * 
     * @param servType
     */
    public void setServType(java.lang.String servType) {
        this.servType = servType;
    }


    /**
     * Gets the revenueClass value for this ServiceLocation.
     * 
     * @return revenueClass
     */
    public java.lang.String getRevenueClass() {
        return revenueClass;
    }


    /**
     * Sets the revenueClass value for this ServiceLocation.
     * 
     * @param revenueClass
     */
    public void setRevenueClass(java.lang.String revenueClass) {
        this.revenueClass = revenueClass;
    }


    /**
     * Gets the servStatus value for this ServiceLocation.
     * 
     * @return servStatus
     */
    public java.lang.String getServStatus() {
        return servStatus;
    }


    /**
     * Sets the servStatus value for this ServiceLocation.
     * 
     * @param servStatus
     */
    public void setServStatus(java.lang.String servStatus) {
        this.servStatus = servStatus;
    }


    /**
     * Gets the outageStatus value for this ServiceLocation.
     * 
     * @return outageStatus
     */
    public com.cannontech.multispeak.PowerStatus getOutageStatus() {
        return outageStatus;
    }


    /**
     * Sets the outageStatus value for this ServiceLocation.
     * 
     * @param outageStatus
     */
    public void setOutageStatus(com.cannontech.multispeak.PowerStatus outageStatus) {
        this.outageStatus = outageStatus;
    }


    /**
     * Gets the billingCycle value for this ServiceLocation.
     * 
     * @return billingCycle
     */
    public java.lang.String getBillingCycle() {
        return billingCycle;
    }


    /**
     * Sets the billingCycle value for this ServiceLocation.
     * 
     * @param billingCycle
     */
    public void setBillingCycle(java.lang.String billingCycle) {
        this.billingCycle = billingCycle;
    }


    /**
     * Gets the route value for this ServiceLocation.
     * 
     * @return route
     */
    public java.lang.String getRoute() {
        return route;
    }


    /**
     * Sets the route value for this ServiceLocation.
     * 
     * @param route
     */
    public void setRoute(java.lang.String route) {
        this.route = route;
    }


    /**
     * Gets the specialNeeds value for this ServiceLocation.
     * 
     * @return specialNeeds
     */
    public java.lang.String getSpecialNeeds() {
        return specialNeeds;
    }


    /**
     * Sets the specialNeeds value for this ServiceLocation.
     * 
     * @param specialNeeds
     */
    public void setSpecialNeeds(java.lang.String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }


    /**
     * Gets the loadMgmt value for this ServiceLocation.
     * 
     * @return loadMgmt
     */
    public java.lang.String getLoadMgmt() {
        return loadMgmt;
    }


    /**
     * Sets the loadMgmt value for this ServiceLocation.
     * 
     * @param loadMgmt
     */
    public void setLoadMgmt(java.lang.String loadMgmt) {
        this.loadMgmt = loadMgmt;
    }


    /**
     * Gets the budgBill value for this ServiceLocation.
     * 
     * @return budgBill
     */
    public java.lang.String getBudgBill() {
        return budgBill;
    }


    /**
     * Sets the budgBill value for this ServiceLocation.
     * 
     * @param budgBill
     */
    public void setBudgBill(java.lang.String budgBill) {
        this.budgBill = budgBill;
    }


    /**
     * Gets the acRecvBal value for this ServiceLocation.
     * 
     * @return acRecvBal
     */
    public java.lang.Float getAcRecvBal() {
        return acRecvBal;
    }


    /**
     * Sets the acRecvBal value for this ServiceLocation.
     * 
     * @param acRecvBal
     */
    public void setAcRecvBal(java.lang.Float acRecvBal) {
        this.acRecvBal = acRecvBal;
    }


    /**
     * Gets the acRecvCur value for this ServiceLocation.
     * 
     * @return acRecvCur
     */
    public java.lang.Float getAcRecvCur() {
        return acRecvCur;
    }


    /**
     * Sets the acRecvCur value for this ServiceLocation.
     * 
     * @param acRecvCur
     */
    public void setAcRecvCur(java.lang.Float acRecvCur) {
        this.acRecvCur = acRecvCur;
    }


    /**
     * Gets the acRecv30 value for this ServiceLocation.
     * 
     * @return acRecv30
     */
    public java.lang.Float getAcRecv30() {
        return acRecv30;
    }


    /**
     * Sets the acRecv30 value for this ServiceLocation.
     * 
     * @param acRecv30
     */
    public void setAcRecv30(java.lang.Float acRecv30) {
        this.acRecv30 = acRecv30;
    }


    /**
     * Gets the acRecv60 value for this ServiceLocation.
     * 
     * @return acRecv60
     */
    public java.lang.Float getAcRecv60() {
        return acRecv60;
    }


    /**
     * Sets the acRecv60 value for this ServiceLocation.
     * 
     * @param acRecv60
     */
    public void setAcRecv60(java.lang.Float acRecv60) {
        this.acRecv60 = acRecv60;
    }


    /**
     * Gets the acRecv90 value for this ServiceLocation.
     * 
     * @return acRecv90
     */
    public java.lang.Float getAcRecv90() {
        return acRecv90;
    }


    /**
     * Sets the acRecv90 value for this ServiceLocation.
     * 
     * @param acRecv90
     */
    public void setAcRecv90(java.lang.Float acRecv90) {
        this.acRecv90 = acRecv90;
    }


    /**
     * Gets the paymentDueDate value for this ServiceLocation.
     * 
     * @return paymentDueDate
     */
    public java.util.Calendar getPaymentDueDate() {
        return paymentDueDate;
    }


    /**
     * Sets the paymentDueDate value for this ServiceLocation.
     * 
     * @param paymentDueDate
     */
    public void setPaymentDueDate(java.util.Calendar paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }


    /**
     * Gets the lastPaymentDate value for this ServiceLocation.
     * 
     * @return lastPaymentDate
     */
    public java.util.Calendar getLastPaymentDate() {
        return lastPaymentDate;
    }


    /**
     * Sets the lastPaymentDate value for this ServiceLocation.
     * 
     * @param lastPaymentDate
     */
    public void setLastPaymentDate(java.util.Calendar lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }


    /**
     * Gets the lastPaymentAmount value for this ServiceLocation.
     * 
     * @return lastPaymentAmount
     */
    public java.lang.Float getLastPaymentAmount() {
        return lastPaymentAmount;
    }


    /**
     * Sets the lastPaymentAmount value for this ServiceLocation.
     * 
     * @param lastPaymentAmount
     */
    public void setLastPaymentAmount(java.lang.Float lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }


    /**
     * Gets the billDate value for this ServiceLocation.
     * 
     * @return billDate
     */
    public java.util.Calendar getBillDate() {
        return billDate;
    }


    /**
     * Sets the billDate value for this ServiceLocation.
     * 
     * @param billDate
     */
    public void setBillDate(java.util.Calendar billDate) {
        this.billDate = billDate;
    }


    /**
     * Gets the shutOffDate value for this ServiceLocation.
     * 
     * @return shutOffDate
     */
    public java.util.Calendar getShutOffDate() {
        return shutOffDate;
    }


    /**
     * Sets the shutOffDate value for this ServiceLocation.
     * 
     * @param shutOffDate
     */
    public void setShutOffDate(java.util.Calendar shutOffDate) {
        this.shutOffDate = shutOffDate;
    }


    /**
     * Gets the connection value for this ServiceLocation.
     * 
     * @return connection
     */
    public java.lang.String getConnection() {
        return connection;
    }


    /**
     * Sets the connection value for this ServiceLocation.
     * 
     * @param connection
     */
    public void setConnection(java.lang.String connection) {
        this.connection = connection;
    }


    /**
     * Gets the connectDate value for this ServiceLocation.
     * 
     * @return connectDate
     */
    public java.util.Calendar getConnectDate() {
        return connectDate;
    }


    /**
     * Sets the connectDate value for this ServiceLocation.
     * 
     * @param connectDate
     */
    public void setConnectDate(java.util.Calendar connectDate) {
        this.connectDate = connectDate;
    }


    /**
     * Gets the disconnectDate value for this ServiceLocation.
     * 
     * @return disconnectDate
     */
    public java.util.Calendar getDisconnectDate() {
        return disconnectDate;
    }


    /**
     * Sets the disconnectDate value for this ServiceLocation.
     * 
     * @param disconnectDate
     */
    public void setDisconnectDate(java.util.Calendar disconnectDate) {
        this.disconnectDate = disconnectDate;
    }


    /**
     * Gets the network value for this ServiceLocation.
     * 
     * @return network
     */
    public com.cannontech.multispeak.Network getNetwork() {
        return network;
    }


    /**
     * Sets the network value for this ServiceLocation.
     * 
     * @param network
     */
    public void setNetwork(com.cannontech.multispeak.Network network) {
        this.network = network;
    }


    /**
     * Gets the SIC value for this ServiceLocation.
     * 
     * @return SIC
     */
    public java.lang.String getSIC() {
        return SIC;
    }


    /**
     * Sets the SIC value for this ServiceLocation.
     * 
     * @param SIC
     */
    public void setSIC(java.lang.String SIC) {
        this.SIC = SIC;
    }


    /**
     * Gets the isCogenerationSite value for this ServiceLocation.
     * 
     * @return isCogenerationSite
     */
    public java.lang.Boolean getIsCogenerationSite() {
        return isCogenerationSite;
    }


    /**
     * Sets the isCogenerationSite value for this ServiceLocation.
     * 
     * @param isCogenerationSite
     */
    public void setIsCogenerationSite(java.lang.Boolean isCogenerationSite) {
        this.isCogenerationSite = isCogenerationSite;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceLocation)) return false;
        ServiceLocation other = (ServiceLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.facilityName==null && other.getFacilityName()==null) || 
             (this.facilityName!=null &&
              this.facilityName.equals(other.getFacilityName()))) &&
            ((this.siteID==null && other.getSiteID()==null) || 
             (this.siteID!=null &&
              this.siteID.equals(other.getSiteID()))) &&
            ((this.servAddr1==null && other.getServAddr1()==null) || 
             (this.servAddr1!=null &&
              this.servAddr1.equals(other.getServAddr1()))) &&
            ((this.servAddr2==null && other.getServAddr2()==null) || 
             (this.servAddr2!=null &&
              this.servAddr2.equals(other.getServAddr2()))) &&
            ((this.servCity==null && other.getServCity()==null) || 
             (this.servCity!=null &&
              this.servCity.equals(other.getServCity()))) &&
            ((this.servState==null && other.getServState()==null) || 
             (this.servState!=null &&
              this.servState.equals(other.getServState()))) &&
            ((this.servZip==null && other.getServZip()==null) || 
             (this.servZip!=null &&
              this.servZip.equals(other.getServZip()))) &&
            ((this.servType==null && other.getServType()==null) || 
             (this.servType!=null &&
              this.servType.equals(other.getServType()))) &&
            ((this.revenueClass==null && other.getRevenueClass()==null) || 
             (this.revenueClass!=null &&
              this.revenueClass.equals(other.getRevenueClass()))) &&
            ((this.servStatus==null && other.getServStatus()==null) || 
             (this.servStatus!=null &&
              this.servStatus.equals(other.getServStatus()))) &&
            ((this.outageStatus==null && other.getOutageStatus()==null) || 
             (this.outageStatus!=null &&
              this.outageStatus.equals(other.getOutageStatus()))) &&
            ((this.billingCycle==null && other.getBillingCycle()==null) || 
             (this.billingCycle!=null &&
              this.billingCycle.equals(other.getBillingCycle()))) &&
            ((this.route==null && other.getRoute()==null) || 
             (this.route!=null &&
              this.route.equals(other.getRoute()))) &&
            ((this.specialNeeds==null && other.getSpecialNeeds()==null) || 
             (this.specialNeeds!=null &&
              this.specialNeeds.equals(other.getSpecialNeeds()))) &&
            ((this.loadMgmt==null && other.getLoadMgmt()==null) || 
             (this.loadMgmt!=null &&
              this.loadMgmt.equals(other.getLoadMgmt()))) &&
            ((this.budgBill==null && other.getBudgBill()==null) || 
             (this.budgBill!=null &&
              this.budgBill.equals(other.getBudgBill()))) &&
            ((this.acRecvBal==null && other.getAcRecvBal()==null) || 
             (this.acRecvBal!=null &&
              this.acRecvBal.equals(other.getAcRecvBal()))) &&
            ((this.acRecvCur==null && other.getAcRecvCur()==null) || 
             (this.acRecvCur!=null &&
              this.acRecvCur.equals(other.getAcRecvCur()))) &&
            ((this.acRecv30==null && other.getAcRecv30()==null) || 
             (this.acRecv30!=null &&
              this.acRecv30.equals(other.getAcRecv30()))) &&
            ((this.acRecv60==null && other.getAcRecv60()==null) || 
             (this.acRecv60!=null &&
              this.acRecv60.equals(other.getAcRecv60()))) &&
            ((this.acRecv90==null && other.getAcRecv90()==null) || 
             (this.acRecv90!=null &&
              this.acRecv90.equals(other.getAcRecv90()))) &&
            ((this.paymentDueDate==null && other.getPaymentDueDate()==null) || 
             (this.paymentDueDate!=null &&
              this.paymentDueDate.equals(other.getPaymentDueDate()))) &&
            ((this.lastPaymentDate==null && other.getLastPaymentDate()==null) || 
             (this.lastPaymentDate!=null &&
              this.lastPaymentDate.equals(other.getLastPaymentDate()))) &&
            ((this.lastPaymentAmount==null && other.getLastPaymentAmount()==null) || 
             (this.lastPaymentAmount!=null &&
              this.lastPaymentAmount.equals(other.getLastPaymentAmount()))) &&
            ((this.billDate==null && other.getBillDate()==null) || 
             (this.billDate!=null &&
              this.billDate.equals(other.getBillDate()))) &&
            ((this.shutOffDate==null && other.getShutOffDate()==null) || 
             (this.shutOffDate!=null &&
              this.shutOffDate.equals(other.getShutOffDate()))) &&
            ((this.connection==null && other.getConnection()==null) || 
             (this.connection!=null &&
              this.connection.equals(other.getConnection()))) &&
            ((this.connectDate==null && other.getConnectDate()==null) || 
             (this.connectDate!=null &&
              this.connectDate.equals(other.getConnectDate()))) &&
            ((this.disconnectDate==null && other.getDisconnectDate()==null) || 
             (this.disconnectDate!=null &&
              this.disconnectDate.equals(other.getDisconnectDate()))) &&
            ((this.network==null && other.getNetwork()==null) || 
             (this.network!=null &&
              this.network.equals(other.getNetwork()))) &&
            ((this.SIC==null && other.getSIC()==null) || 
             (this.SIC!=null &&
              this.SIC.equals(other.getSIC()))) &&
            ((this.isCogenerationSite==null && other.getIsCogenerationSite()==null) || 
             (this.isCogenerationSite!=null &&
              this.isCogenerationSite.equals(other.getIsCogenerationSite())));
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
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getFacilityName() != null) {
            _hashCode += getFacilityName().hashCode();
        }
        if (getSiteID() != null) {
            _hashCode += getSiteID().hashCode();
        }
        if (getServAddr1() != null) {
            _hashCode += getServAddr1().hashCode();
        }
        if (getServAddr2() != null) {
            _hashCode += getServAddr2().hashCode();
        }
        if (getServCity() != null) {
            _hashCode += getServCity().hashCode();
        }
        if (getServState() != null) {
            _hashCode += getServState().hashCode();
        }
        if (getServZip() != null) {
            _hashCode += getServZip().hashCode();
        }
        if (getServType() != null) {
            _hashCode += getServType().hashCode();
        }
        if (getRevenueClass() != null) {
            _hashCode += getRevenueClass().hashCode();
        }
        if (getServStatus() != null) {
            _hashCode += getServStatus().hashCode();
        }
        if (getOutageStatus() != null) {
            _hashCode += getOutageStatus().hashCode();
        }
        if (getBillingCycle() != null) {
            _hashCode += getBillingCycle().hashCode();
        }
        if (getRoute() != null) {
            _hashCode += getRoute().hashCode();
        }
        if (getSpecialNeeds() != null) {
            _hashCode += getSpecialNeeds().hashCode();
        }
        if (getLoadMgmt() != null) {
            _hashCode += getLoadMgmt().hashCode();
        }
        if (getBudgBill() != null) {
            _hashCode += getBudgBill().hashCode();
        }
        if (getAcRecvBal() != null) {
            _hashCode += getAcRecvBal().hashCode();
        }
        if (getAcRecvCur() != null) {
            _hashCode += getAcRecvCur().hashCode();
        }
        if (getAcRecv30() != null) {
            _hashCode += getAcRecv30().hashCode();
        }
        if (getAcRecv60() != null) {
            _hashCode += getAcRecv60().hashCode();
        }
        if (getAcRecv90() != null) {
            _hashCode += getAcRecv90().hashCode();
        }
        if (getPaymentDueDate() != null) {
            _hashCode += getPaymentDueDate().hashCode();
        }
        if (getLastPaymentDate() != null) {
            _hashCode += getLastPaymentDate().hashCode();
        }
        if (getLastPaymentAmount() != null) {
            _hashCode += getLastPaymentAmount().hashCode();
        }
        if (getBillDate() != null) {
            _hashCode += getBillDate().hashCode();
        }
        if (getShutOffDate() != null) {
            _hashCode += getShutOffDate().hashCode();
        }
        if (getConnection() != null) {
            _hashCode += getConnection().hashCode();
        }
        if (getConnectDate() != null) {
            _hashCode += getConnectDate().hashCode();
        }
        if (getDisconnectDate() != null) {
            _hashCode += getDisconnectDate().hashCode();
        }
        if (getNetwork() != null) {
            _hashCode += getNetwork().hashCode();
        }
        if (getSIC() != null) {
            _hashCode += getSIC().hashCode();
        }
        if (getIsCogenerationSite() != null) {
            _hashCode += getIsCogenerationSite().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facilityName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "siteID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servAddr1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servAddr1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servAddr2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servAddr2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servZip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servZip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revenueClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revenueClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerStatus"));
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
        elemField.setFieldName("route");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "route"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialNeeds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "specialNeeds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadMgmt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadMgmt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budgBill");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "budgBill"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acRecvBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acRecvBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acRecvCur");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acRecvCur"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acRecv30");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acRecv30"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acRecv60");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acRecv60"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acRecv90");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acRecv90"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentDueDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentDueDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastPaymentAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastPaymentAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shutOffDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shutOffDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connection");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("disconnectDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "disconnectDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("network");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "network"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "network"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SIC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "SIC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isCogenerationSite");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isCogenerationSite"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
