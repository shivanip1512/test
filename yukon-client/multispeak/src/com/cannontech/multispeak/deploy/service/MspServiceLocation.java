/**
 * MspServiceLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public abstract class MspServiceLocation  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol;

    private com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList;

    private java.lang.String custID;

    private java.lang.String accountNumber;

    private java.lang.String facilityName;

    private java.lang.String siteID;

    private com.cannontech.multispeak.deploy.service.Address serviceAddress;

    private java.lang.String revenueClass;

    private java.lang.String servStatus;

    private java.lang.String billingCycle;

    private java.lang.String route;

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

    private java.util.Calendar connectDate;

    private java.util.Calendar disconnectDate;

    private com.cannontech.multispeak.deploy.service.MspNetwork network;

    private java.lang.String SIC;

    private java.lang.String woNumber;

    private java.lang.String soNumber;

    public MspServiceLocation() {
    }

    public MspServiceLocation(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           java.lang.String custID,
           java.lang.String accountNumber,
           java.lang.String facilityName,
           java.lang.String siteID,
           com.cannontech.multispeak.deploy.service.Address serviceAddress,
           java.lang.String revenueClass,
           java.lang.String servStatus,
           java.lang.String billingCycle,
           java.lang.String route,
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
           java.util.Calendar connectDate,
           java.util.Calendar disconnectDate,
           com.cannontech.multispeak.deploy.service.MspNetwork network,
           java.lang.String SIC,
           java.lang.String woNumber,
           java.lang.String soNumber) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.graphicSymbol = graphicSymbol;
        this.annotationList = annotationList;
        this.custID = custID;
        this.accountNumber = accountNumber;
        this.facilityName = facilityName;
        this.siteID = siteID;
        this.serviceAddress = serviceAddress;
        this.revenueClass = revenueClass;
        this.servStatus = servStatus;
        this.billingCycle = billingCycle;
        this.route = route;
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
        this.connectDate = connectDate;
        this.disconnectDate = disconnectDate;
        this.network = network;
        this.SIC = SIC;
        this.woNumber = woNumber;
        this.soNumber = soNumber;
    }


    /**
     * Gets the graphicSymbol value for this MspServiceLocation.
     * 
     * @return graphicSymbol
     */
    public com.cannontech.multispeak.deploy.service.GraphicSymbol[] getGraphicSymbol() {
        return graphicSymbol;
    }


    /**
     * Sets the graphicSymbol value for this MspServiceLocation.
     * 
     * @param graphicSymbol
     */
    public void setGraphicSymbol(com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol) {
        this.graphicSymbol = graphicSymbol;
    }

    public com.cannontech.multispeak.deploy.service.GraphicSymbol getGraphicSymbol(int i) {
        return this.graphicSymbol[i];
    }

    public void setGraphicSymbol(int i, com.cannontech.multispeak.deploy.service.GraphicSymbol _value) {
        this.graphicSymbol[i] = _value;
    }


    /**
     * Gets the annotationList value for this MspServiceLocation.
     * 
     * @return annotationList
     */
    public com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] getAnnotationList() {
        return annotationList;
    }


    /**
     * Sets the annotationList value for this MspServiceLocation.
     * 
     * @param annotationList
     */
    public void setAnnotationList(com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList) {
        this.annotationList = annotationList;
    }

    public com.cannontech.multispeak.deploy.service.GenericAnnotationFeature getAnnotationList(int i) {
        return this.annotationList[i];
    }

    public void setAnnotationList(int i, com.cannontech.multispeak.deploy.service.GenericAnnotationFeature _value) {
        this.annotationList[i] = _value;
    }


    /**
     * Gets the custID value for this MspServiceLocation.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this MspServiceLocation.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the accountNumber value for this MspServiceLocation.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this MspServiceLocation.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the facilityName value for this MspServiceLocation.
     * 
     * @return facilityName
     */
    public java.lang.String getFacilityName() {
        return facilityName;
    }


    /**
     * Sets the facilityName value for this MspServiceLocation.
     * 
     * @param facilityName
     */
    public void setFacilityName(java.lang.String facilityName) {
        this.facilityName = facilityName;
    }


    /**
     * Gets the siteID value for this MspServiceLocation.
     * 
     * @return siteID
     */
    public java.lang.String getSiteID() {
        return siteID;
    }


    /**
     * Sets the siteID value for this MspServiceLocation.
     * 
     * @param siteID
     */
    public void setSiteID(java.lang.String siteID) {
        this.siteID = siteID;
    }


    /**
     * Gets the serviceAddress value for this MspServiceLocation.
     * 
     * @return serviceAddress
     */
    public com.cannontech.multispeak.deploy.service.Address getServiceAddress() {
        return serviceAddress;
    }


    /**
     * Sets the serviceAddress value for this MspServiceLocation.
     * 
     * @param serviceAddress
     */
    public void setServiceAddress(com.cannontech.multispeak.deploy.service.Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }


    /**
     * Gets the revenueClass value for this MspServiceLocation.
     * 
     * @return revenueClass
     */
    public java.lang.String getRevenueClass() {
        return revenueClass;
    }


    /**
     * Sets the revenueClass value for this MspServiceLocation.
     * 
     * @param revenueClass
     */
    public void setRevenueClass(java.lang.String revenueClass) {
        this.revenueClass = revenueClass;
    }


    /**
     * Gets the servStatus value for this MspServiceLocation.
     * 
     * @return servStatus
     */
    public java.lang.String getServStatus() {
        return servStatus;
    }


    /**
     * Sets the servStatus value for this MspServiceLocation.
     * 
     * @param servStatus
     */
    public void setServStatus(java.lang.String servStatus) {
        this.servStatus = servStatus;
    }


    /**
     * Gets the billingCycle value for this MspServiceLocation.
     * 
     * @return billingCycle
     */
    public java.lang.String getBillingCycle() {
        return billingCycle;
    }


    /**
     * Sets the billingCycle value for this MspServiceLocation.
     * 
     * @param billingCycle
     */
    public void setBillingCycle(java.lang.String billingCycle) {
        this.billingCycle = billingCycle;
    }


    /**
     * Gets the route value for this MspServiceLocation.
     * 
     * @return route
     */
    public java.lang.String getRoute() {
        return route;
    }


    /**
     * Sets the route value for this MspServiceLocation.
     * 
     * @param route
     */
    public void setRoute(java.lang.String route) {
        this.route = route;
    }


    /**
     * Gets the budgBill value for this MspServiceLocation.
     * 
     * @return budgBill
     */
    public java.lang.String getBudgBill() {
        return budgBill;
    }


    /**
     * Sets the budgBill value for this MspServiceLocation.
     * 
     * @param budgBill
     */
    public void setBudgBill(java.lang.String budgBill) {
        this.budgBill = budgBill;
    }


    /**
     * Gets the acRecvBal value for this MspServiceLocation.
     * 
     * @return acRecvBal
     */
    public java.lang.Float getAcRecvBal() {
        return acRecvBal;
    }


    /**
     * Sets the acRecvBal value for this MspServiceLocation.
     * 
     * @param acRecvBal
     */
    public void setAcRecvBal(java.lang.Float acRecvBal) {
        this.acRecvBal = acRecvBal;
    }


    /**
     * Gets the acRecvCur value for this MspServiceLocation.
     * 
     * @return acRecvCur
     */
    public java.lang.Float getAcRecvCur() {
        return acRecvCur;
    }


    /**
     * Sets the acRecvCur value for this MspServiceLocation.
     * 
     * @param acRecvCur
     */
    public void setAcRecvCur(java.lang.Float acRecvCur) {
        this.acRecvCur = acRecvCur;
    }


    /**
     * Gets the acRecv30 value for this MspServiceLocation.
     * 
     * @return acRecv30
     */
    public java.lang.Float getAcRecv30() {
        return acRecv30;
    }


    /**
     * Sets the acRecv30 value for this MspServiceLocation.
     * 
     * @param acRecv30
     */
    public void setAcRecv30(java.lang.Float acRecv30) {
        this.acRecv30 = acRecv30;
    }


    /**
     * Gets the acRecv60 value for this MspServiceLocation.
     * 
     * @return acRecv60
     */
    public java.lang.Float getAcRecv60() {
        return acRecv60;
    }


    /**
     * Sets the acRecv60 value for this MspServiceLocation.
     * 
     * @param acRecv60
     */
    public void setAcRecv60(java.lang.Float acRecv60) {
        this.acRecv60 = acRecv60;
    }


    /**
     * Gets the acRecv90 value for this MspServiceLocation.
     * 
     * @return acRecv90
     */
    public java.lang.Float getAcRecv90() {
        return acRecv90;
    }


    /**
     * Sets the acRecv90 value for this MspServiceLocation.
     * 
     * @param acRecv90
     */
    public void setAcRecv90(java.lang.Float acRecv90) {
        this.acRecv90 = acRecv90;
    }


    /**
     * Gets the paymentDueDate value for this MspServiceLocation.
     * 
     * @return paymentDueDate
     */
    public java.util.Calendar getPaymentDueDate() {
        return paymentDueDate;
    }


    /**
     * Sets the paymentDueDate value for this MspServiceLocation.
     * 
     * @param paymentDueDate
     */
    public void setPaymentDueDate(java.util.Calendar paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }


    /**
     * Gets the lastPaymentDate value for this MspServiceLocation.
     * 
     * @return lastPaymentDate
     */
    public java.util.Calendar getLastPaymentDate() {
        return lastPaymentDate;
    }


    /**
     * Sets the lastPaymentDate value for this MspServiceLocation.
     * 
     * @param lastPaymentDate
     */
    public void setLastPaymentDate(java.util.Calendar lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }


    /**
     * Gets the lastPaymentAmount value for this MspServiceLocation.
     * 
     * @return lastPaymentAmount
     */
    public java.lang.Float getLastPaymentAmount() {
        return lastPaymentAmount;
    }


    /**
     * Sets the lastPaymentAmount value for this MspServiceLocation.
     * 
     * @param lastPaymentAmount
     */
    public void setLastPaymentAmount(java.lang.Float lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }


    /**
     * Gets the billDate value for this MspServiceLocation.
     * 
     * @return billDate
     */
    public java.util.Calendar getBillDate() {
        return billDate;
    }


    /**
     * Sets the billDate value for this MspServiceLocation.
     * 
     * @param billDate
     */
    public void setBillDate(java.util.Calendar billDate) {
        this.billDate = billDate;
    }


    /**
     * Gets the shutOffDate value for this MspServiceLocation.
     * 
     * @return shutOffDate
     */
    public java.util.Calendar getShutOffDate() {
        return shutOffDate;
    }


    /**
     * Sets the shutOffDate value for this MspServiceLocation.
     * 
     * @param shutOffDate
     */
    public void setShutOffDate(java.util.Calendar shutOffDate) {
        this.shutOffDate = shutOffDate;
    }


    /**
     * Gets the connectDate value for this MspServiceLocation.
     * 
     * @return connectDate
     */
    public java.util.Calendar getConnectDate() {
        return connectDate;
    }


    /**
     * Sets the connectDate value for this MspServiceLocation.
     * 
     * @param connectDate
     */
    public void setConnectDate(java.util.Calendar connectDate) {
        this.connectDate = connectDate;
    }


    /**
     * Gets the disconnectDate value for this MspServiceLocation.
     * 
     * @return disconnectDate
     */
    public java.util.Calendar getDisconnectDate() {
        return disconnectDate;
    }


    /**
     * Sets the disconnectDate value for this MspServiceLocation.
     * 
     * @param disconnectDate
     */
    public void setDisconnectDate(java.util.Calendar disconnectDate) {
        this.disconnectDate = disconnectDate;
    }


    /**
     * Gets the network value for this MspServiceLocation.
     * 
     * @return network
     */
    public com.cannontech.multispeak.deploy.service.MspNetwork getNetwork() {
        return network;
    }


    /**
     * Sets the network value for this MspServiceLocation.
     * 
     * @param network
     */
    public void setNetwork(com.cannontech.multispeak.deploy.service.MspNetwork network) {
        this.network = network;
    }


    /**
     * Gets the SIC value for this MspServiceLocation.
     * 
     * @return SIC
     */
    public java.lang.String getSIC() {
        return SIC;
    }


    /**
     * Sets the SIC value for this MspServiceLocation.
     * 
     * @param SIC
     */
    public void setSIC(java.lang.String SIC) {
        this.SIC = SIC;
    }


    /**
     * Gets the woNumber value for this MspServiceLocation.
     * 
     * @return woNumber
     */
    public java.lang.String getWoNumber() {
        return woNumber;
    }


    /**
     * Sets the woNumber value for this MspServiceLocation.
     * 
     * @param woNumber
     */
    public void setWoNumber(java.lang.String woNumber) {
        this.woNumber = woNumber;
    }


    /**
     * Gets the soNumber value for this MspServiceLocation.
     * 
     * @return soNumber
     */
    public java.lang.String getSoNumber() {
        return soNumber;
    }


    /**
     * Sets the soNumber value for this MspServiceLocation.
     * 
     * @param soNumber
     */
    public void setSoNumber(java.lang.String soNumber) {
        this.soNumber = soNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspServiceLocation)) return false;
        MspServiceLocation other = (MspServiceLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.graphicSymbol==null && other.getGraphicSymbol()==null) || 
             (this.graphicSymbol!=null &&
              java.util.Arrays.equals(this.graphicSymbol, other.getGraphicSymbol()))) &&
            ((this.annotationList==null && other.getAnnotationList()==null) || 
             (this.annotationList!=null &&
              java.util.Arrays.equals(this.annotationList, other.getAnnotationList()))) &&
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
            ((this.serviceAddress==null && other.getServiceAddress()==null) || 
             (this.serviceAddress!=null &&
              this.serviceAddress.equals(other.getServiceAddress()))) &&
            ((this.revenueClass==null && other.getRevenueClass()==null) || 
             (this.revenueClass!=null &&
              this.revenueClass.equals(other.getRevenueClass()))) &&
            ((this.servStatus==null && other.getServStatus()==null) || 
             (this.servStatus!=null &&
              this.servStatus.equals(other.getServStatus()))) &&
            ((this.billingCycle==null && other.getBillingCycle()==null) || 
             (this.billingCycle!=null &&
              this.billingCycle.equals(other.getBillingCycle()))) &&
            ((this.route==null && other.getRoute()==null) || 
             (this.route!=null &&
              this.route.equals(other.getRoute()))) &&
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
            ((this.woNumber==null && other.getWoNumber()==null) || 
             (this.woNumber!=null &&
              this.woNumber.equals(other.getWoNumber()))) &&
            ((this.soNumber==null && other.getSoNumber()==null) || 
             (this.soNumber!=null &&
              this.soNumber.equals(other.getSoNumber())));
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
        if (getGraphicSymbol() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGraphicSymbol());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGraphicSymbol(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAnnotationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnnotationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnnotationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
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
        if (getServiceAddress() != null) {
            _hashCode += getServiceAddress().hashCode();
        }
        if (getRevenueClass() != null) {
            _hashCode += getRevenueClass().hashCode();
        }
        if (getServStatus() != null) {
            _hashCode += getServStatus().hashCode();
        }
        if (getBillingCycle() != null) {
            _hashCode += getBillingCycle().hashCode();
        }
        if (getRoute() != null) {
            _hashCode += getRoute().hashCode();
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
        if (getWoNumber() != null) {
            _hashCode += getWoNumber().hashCode();
        }
        if (getSoNumber() != null) {
            _hashCode += getSoNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspServiceLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspServiceLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("graphicSymbol");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("annotationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "annotationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("serviceAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspNetwork"));
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
        elemField.setFieldName("woNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "woNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("soNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "soNumber"));
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
