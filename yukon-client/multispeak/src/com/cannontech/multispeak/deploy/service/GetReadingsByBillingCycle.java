/**
 * GetReadingsByBillingCycle.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetReadingsByBillingCycle  implements java.io.Serializable {
    private java.lang.String billingCycle;

    private java.util.Calendar billingDate;

    private int kWhLookBack;

    private int kWLookBack;

    private int kWLookForward;

    private java.lang.String lastReceived;

    public GetReadingsByBillingCycle() {
    }

    public GetReadingsByBillingCycle(
           java.lang.String billingCycle,
           java.util.Calendar billingDate,
           int kWhLookBack,
           int kWLookBack,
           int kWLookForward,
           java.lang.String lastReceived) {
           this.billingCycle = billingCycle;
           this.billingDate = billingDate;
           this.kWhLookBack = kWhLookBack;
           this.kWLookBack = kWLookBack;
           this.kWLookForward = kWLookForward;
           this.lastReceived = lastReceived;
    }


    /**
     * Gets the billingCycle value for this GetReadingsByBillingCycle.
     * 
     * @return billingCycle
     */
    public java.lang.String getBillingCycle() {
        return billingCycle;
    }


    /**
     * Sets the billingCycle value for this GetReadingsByBillingCycle.
     * 
     * @param billingCycle
     */
    public void setBillingCycle(java.lang.String billingCycle) {
        this.billingCycle = billingCycle;
    }


    /**
     * Gets the billingDate value for this GetReadingsByBillingCycle.
     * 
     * @return billingDate
     */
    public java.util.Calendar getBillingDate() {
        return billingDate;
    }


    /**
     * Sets the billingDate value for this GetReadingsByBillingCycle.
     * 
     * @param billingDate
     */
    public void setBillingDate(java.util.Calendar billingDate) {
        this.billingDate = billingDate;
    }


    /**
     * Gets the kWhLookBack value for this GetReadingsByBillingCycle.
     * 
     * @return kWhLookBack
     */
    public int getKWhLookBack() {
        return kWhLookBack;
    }


    /**
     * Sets the kWhLookBack value for this GetReadingsByBillingCycle.
     * 
     * @param kWhLookBack
     */
    public void setKWhLookBack(int kWhLookBack) {
        this.kWhLookBack = kWhLookBack;
    }


    /**
     * Gets the kWLookBack value for this GetReadingsByBillingCycle.
     * 
     * @return kWLookBack
     */
    public int getKWLookBack() {
        return kWLookBack;
    }


    /**
     * Sets the kWLookBack value for this GetReadingsByBillingCycle.
     * 
     * @param kWLookBack
     */
    public void setKWLookBack(int kWLookBack) {
        this.kWLookBack = kWLookBack;
    }


    /**
     * Gets the kWLookForward value for this GetReadingsByBillingCycle.
     * 
     * @return kWLookForward
     */
    public int getKWLookForward() {
        return kWLookForward;
    }


    /**
     * Sets the kWLookForward value for this GetReadingsByBillingCycle.
     * 
     * @param kWLookForward
     */
    public void setKWLookForward(int kWLookForward) {
        this.kWLookForward = kWLookForward;
    }


    /**
     * Gets the lastReceived value for this GetReadingsByBillingCycle.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetReadingsByBillingCycle.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetReadingsByBillingCycle)) return false;
        GetReadingsByBillingCycle other = (GetReadingsByBillingCycle) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.billingCycle==null && other.getBillingCycle()==null) || 
             (this.billingCycle!=null &&
              this.billingCycle.equals(other.getBillingCycle()))) &&
            ((this.billingDate==null && other.getBillingDate()==null) || 
             (this.billingDate!=null &&
              this.billingDate.equals(other.getBillingDate()))) &&
            this.kWhLookBack == other.getKWhLookBack() &&
            this.kWLookBack == other.getKWLookBack() &&
            this.kWLookForward == other.getKWLookForward() &&
            ((this.lastReceived==null && other.getLastReceived()==null) || 
             (this.lastReceived!=null &&
              this.lastReceived.equals(other.getLastReceived())));
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
        if (getBillingCycle() != null) {
            _hashCode += getBillingCycle().hashCode();
        }
        if (getBillingDate() != null) {
            _hashCode += getBillingDate().hashCode();
        }
        _hashCode += getKWhLookBack();
        _hashCode += getKWLookBack();
        _hashCode += getKWLookForward();
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetReadingsByBillingCycle.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetReadingsByBillingCycle"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingCycle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingCycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWhLookBack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWhLookBack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWLookBack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookBack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWLookForward");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookForward"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReceived");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"));
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
