/**
 * InHomeDisplayBillingMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InHomeDisplayBillingMessage  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String inHomeDisplayID;

    private java.lang.Float currentBalance;

    private java.lang.Float currentRate;

    private java.util.Calendar dateTime;

    private java.math.BigInteger priorityOrder;

    private java.lang.Float averageDailyUsage;

    private java.lang.Float usedYesterday;

    private java.lang.Float usedThisMonth;

    private java.lang.Float usedLastMonth;

    private java.lang.Float usedFourWeeksAgo;

    private java.lang.Float usedThisMonthLastYear;

    private com.cannontech.multispeak.deploy.service.UsageOtherPeriod[] usageOtherPeriodList;

    private java.math.BigInteger daysRemaining;

    public InHomeDisplayBillingMessage() {
    }

    public InHomeDisplayBillingMessage(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String inHomeDisplayID,
           java.lang.Float currentBalance,
           java.lang.Float currentRate,
           java.util.Calendar dateTime,
           java.math.BigInteger priorityOrder,
           java.lang.Float averageDailyUsage,
           java.lang.Float usedYesterday,
           java.lang.Float usedThisMonth,
           java.lang.Float usedLastMonth,
           java.lang.Float usedFourWeeksAgo,
           java.lang.Float usedThisMonthLastYear,
           com.cannontech.multispeak.deploy.service.UsageOtherPeriod[] usageOtherPeriodList,
           java.math.BigInteger daysRemaining) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.inHomeDisplayID = inHomeDisplayID;
        this.currentBalance = currentBalance;
        this.currentRate = currentRate;
        this.dateTime = dateTime;
        this.priorityOrder = priorityOrder;
        this.averageDailyUsage = averageDailyUsage;
        this.usedYesterday = usedYesterday;
        this.usedThisMonth = usedThisMonth;
        this.usedLastMonth = usedLastMonth;
        this.usedFourWeeksAgo = usedFourWeeksAgo;
        this.usedThisMonthLastYear = usedThisMonthLastYear;
        this.usageOtherPeriodList = usageOtherPeriodList;
        this.daysRemaining = daysRemaining;
    }


    /**
     * Gets the inHomeDisplayID value for this InHomeDisplayBillingMessage.
     * 
     * @return inHomeDisplayID
     */
    public java.lang.String getInHomeDisplayID() {
        return inHomeDisplayID;
    }


    /**
     * Sets the inHomeDisplayID value for this InHomeDisplayBillingMessage.
     * 
     * @param inHomeDisplayID
     */
    public void setInHomeDisplayID(java.lang.String inHomeDisplayID) {
        this.inHomeDisplayID = inHomeDisplayID;
    }


    /**
     * Gets the currentBalance value for this InHomeDisplayBillingMessage.
     * 
     * @return currentBalance
     */
    public java.lang.Float getCurrentBalance() {
        return currentBalance;
    }


    /**
     * Sets the currentBalance value for this InHomeDisplayBillingMessage.
     * 
     * @param currentBalance
     */
    public void setCurrentBalance(java.lang.Float currentBalance) {
        this.currentBalance = currentBalance;
    }


    /**
     * Gets the currentRate value for this InHomeDisplayBillingMessage.
     * 
     * @return currentRate
     */
    public java.lang.Float getCurrentRate() {
        return currentRate;
    }


    /**
     * Sets the currentRate value for this InHomeDisplayBillingMessage.
     * 
     * @param currentRate
     */
    public void setCurrentRate(java.lang.Float currentRate) {
        this.currentRate = currentRate;
    }


    /**
     * Gets the dateTime value for this InHomeDisplayBillingMessage.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this InHomeDisplayBillingMessage.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the priorityOrder value for this InHomeDisplayBillingMessage.
     * 
     * @return priorityOrder
     */
    public java.math.BigInteger getPriorityOrder() {
        return priorityOrder;
    }


    /**
     * Sets the priorityOrder value for this InHomeDisplayBillingMessage.
     * 
     * @param priorityOrder
     */
    public void setPriorityOrder(java.math.BigInteger priorityOrder) {
        this.priorityOrder = priorityOrder;
    }


    /**
     * Gets the averageDailyUsage value for this InHomeDisplayBillingMessage.
     * 
     * @return averageDailyUsage
     */
    public java.lang.Float getAverageDailyUsage() {
        return averageDailyUsage;
    }


    /**
     * Sets the averageDailyUsage value for this InHomeDisplayBillingMessage.
     * 
     * @param averageDailyUsage
     */
    public void setAverageDailyUsage(java.lang.Float averageDailyUsage) {
        this.averageDailyUsage = averageDailyUsage;
    }


    /**
     * Gets the usedYesterday value for this InHomeDisplayBillingMessage.
     * 
     * @return usedYesterday
     */
    public java.lang.Float getUsedYesterday() {
        return usedYesterday;
    }


    /**
     * Sets the usedYesterday value for this InHomeDisplayBillingMessage.
     * 
     * @param usedYesterday
     */
    public void setUsedYesterday(java.lang.Float usedYesterday) {
        this.usedYesterday = usedYesterday;
    }


    /**
     * Gets the usedThisMonth value for this InHomeDisplayBillingMessage.
     * 
     * @return usedThisMonth
     */
    public java.lang.Float getUsedThisMonth() {
        return usedThisMonth;
    }


    /**
     * Sets the usedThisMonth value for this InHomeDisplayBillingMessage.
     * 
     * @param usedThisMonth
     */
    public void setUsedThisMonth(java.lang.Float usedThisMonth) {
        this.usedThisMonth = usedThisMonth;
    }


    /**
     * Gets the usedLastMonth value for this InHomeDisplayBillingMessage.
     * 
     * @return usedLastMonth
     */
    public java.lang.Float getUsedLastMonth() {
        return usedLastMonth;
    }


    /**
     * Sets the usedLastMonth value for this InHomeDisplayBillingMessage.
     * 
     * @param usedLastMonth
     */
    public void setUsedLastMonth(java.lang.Float usedLastMonth) {
        this.usedLastMonth = usedLastMonth;
    }


    /**
     * Gets the usedFourWeeksAgo value for this InHomeDisplayBillingMessage.
     * 
     * @return usedFourWeeksAgo
     */
    public java.lang.Float getUsedFourWeeksAgo() {
        return usedFourWeeksAgo;
    }


    /**
     * Sets the usedFourWeeksAgo value for this InHomeDisplayBillingMessage.
     * 
     * @param usedFourWeeksAgo
     */
    public void setUsedFourWeeksAgo(java.lang.Float usedFourWeeksAgo) {
        this.usedFourWeeksAgo = usedFourWeeksAgo;
    }


    /**
     * Gets the usedThisMonthLastYear value for this InHomeDisplayBillingMessage.
     * 
     * @return usedThisMonthLastYear
     */
    public java.lang.Float getUsedThisMonthLastYear() {
        return usedThisMonthLastYear;
    }


    /**
     * Sets the usedThisMonthLastYear value for this InHomeDisplayBillingMessage.
     * 
     * @param usedThisMonthLastYear
     */
    public void setUsedThisMonthLastYear(java.lang.Float usedThisMonthLastYear) {
        this.usedThisMonthLastYear = usedThisMonthLastYear;
    }


    /**
     * Gets the usageOtherPeriodList value for this InHomeDisplayBillingMessage.
     * 
     * @return usageOtherPeriodList
     */
    public com.cannontech.multispeak.deploy.service.UsageOtherPeriod[] getUsageOtherPeriodList() {
        return usageOtherPeriodList;
    }


    /**
     * Sets the usageOtherPeriodList value for this InHomeDisplayBillingMessage.
     * 
     * @param usageOtherPeriodList
     */
    public void setUsageOtherPeriodList(com.cannontech.multispeak.deploy.service.UsageOtherPeriod[] usageOtherPeriodList) {
        this.usageOtherPeriodList = usageOtherPeriodList;
    }


    /**
     * Gets the daysRemaining value for this InHomeDisplayBillingMessage.
     * 
     * @return daysRemaining
     */
    public java.math.BigInteger getDaysRemaining() {
        return daysRemaining;
    }


    /**
     * Sets the daysRemaining value for this InHomeDisplayBillingMessage.
     * 
     * @param daysRemaining
     */
    public void setDaysRemaining(java.math.BigInteger daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InHomeDisplayBillingMessage)) return false;
        InHomeDisplayBillingMessage other = (InHomeDisplayBillingMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.inHomeDisplayID==null && other.getInHomeDisplayID()==null) || 
             (this.inHomeDisplayID!=null &&
              this.inHomeDisplayID.equals(other.getInHomeDisplayID()))) &&
            ((this.currentBalance==null && other.getCurrentBalance()==null) || 
             (this.currentBalance!=null &&
              this.currentBalance.equals(other.getCurrentBalance()))) &&
            ((this.currentRate==null && other.getCurrentRate()==null) || 
             (this.currentRate!=null &&
              this.currentRate.equals(other.getCurrentRate()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.priorityOrder==null && other.getPriorityOrder()==null) || 
             (this.priorityOrder!=null &&
              this.priorityOrder.equals(other.getPriorityOrder()))) &&
            ((this.averageDailyUsage==null && other.getAverageDailyUsage()==null) || 
             (this.averageDailyUsage!=null &&
              this.averageDailyUsage.equals(other.getAverageDailyUsage()))) &&
            ((this.usedYesterday==null && other.getUsedYesterday()==null) || 
             (this.usedYesterday!=null &&
              this.usedYesterday.equals(other.getUsedYesterday()))) &&
            ((this.usedThisMonth==null && other.getUsedThisMonth()==null) || 
             (this.usedThisMonth!=null &&
              this.usedThisMonth.equals(other.getUsedThisMonth()))) &&
            ((this.usedLastMonth==null && other.getUsedLastMonth()==null) || 
             (this.usedLastMonth!=null &&
              this.usedLastMonth.equals(other.getUsedLastMonth()))) &&
            ((this.usedFourWeeksAgo==null && other.getUsedFourWeeksAgo()==null) || 
             (this.usedFourWeeksAgo!=null &&
              this.usedFourWeeksAgo.equals(other.getUsedFourWeeksAgo()))) &&
            ((this.usedThisMonthLastYear==null && other.getUsedThisMonthLastYear()==null) || 
             (this.usedThisMonthLastYear!=null &&
              this.usedThisMonthLastYear.equals(other.getUsedThisMonthLastYear()))) &&
            ((this.usageOtherPeriodList==null && other.getUsageOtherPeriodList()==null) || 
             (this.usageOtherPeriodList!=null &&
              java.util.Arrays.equals(this.usageOtherPeriodList, other.getUsageOtherPeriodList()))) &&
            ((this.daysRemaining==null && other.getDaysRemaining()==null) || 
             (this.daysRemaining!=null &&
              this.daysRemaining.equals(other.getDaysRemaining())));
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
        if (getInHomeDisplayID() != null) {
            _hashCode += getInHomeDisplayID().hashCode();
        }
        if (getCurrentBalance() != null) {
            _hashCode += getCurrentBalance().hashCode();
        }
        if (getCurrentRate() != null) {
            _hashCode += getCurrentRate().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getPriorityOrder() != null) {
            _hashCode += getPriorityOrder().hashCode();
        }
        if (getAverageDailyUsage() != null) {
            _hashCode += getAverageDailyUsage().hashCode();
        }
        if (getUsedYesterday() != null) {
            _hashCode += getUsedYesterday().hashCode();
        }
        if (getUsedThisMonth() != null) {
            _hashCode += getUsedThisMonth().hashCode();
        }
        if (getUsedLastMonth() != null) {
            _hashCode += getUsedLastMonth().hashCode();
        }
        if (getUsedFourWeeksAgo() != null) {
            _hashCode += getUsedFourWeeksAgo().hashCode();
        }
        if (getUsedThisMonthLastYear() != null) {
            _hashCode += getUsedThisMonthLastYear().hashCode();
        }
        if (getUsageOtherPeriodList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUsageOtherPeriodList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUsageOtherPeriodList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDaysRemaining() != null) {
            _hashCode += getDaysRemaining().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InHomeDisplayBillingMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayBillingMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "currentBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "currentRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("averageDailyUsage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "averageDailyUsage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedYesterday");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usedYesterday"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedThisMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usedThisMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedLastMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usedLastMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedFourWeeksAgo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usedFourWeeksAgo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedThisMonthLastYear");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usedThisMonthLastYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usageOtherPeriodList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriodList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriod"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageOtherPeriod"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("daysRemaining");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "daysRemaining"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
