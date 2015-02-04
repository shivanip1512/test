/**
 * RemainingBalance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RemainingBalance  implements java.io.Serializable {
    private float priorityBalance;

    private float sharedArrears;

    private float balance;

    private com.cannontech.multispeak.deploy.service.MeterRead finalRead;

    public RemainingBalance() {
    }

    public RemainingBalance(
           float priorityBalance,
           float sharedArrears,
           float balance,
           com.cannontech.multispeak.deploy.service.MeterRead finalRead) {
           this.priorityBalance = priorityBalance;
           this.sharedArrears = sharedArrears;
           this.balance = balance;
           this.finalRead = finalRead;
    }


    /**
     * Gets the priorityBalance value for this RemainingBalance.
     * 
     * @return priorityBalance
     */
    public float getPriorityBalance() {
        return priorityBalance;
    }


    /**
     * Sets the priorityBalance value for this RemainingBalance.
     * 
     * @param priorityBalance
     */
    public void setPriorityBalance(float priorityBalance) {
        this.priorityBalance = priorityBalance;
    }


    /**
     * Gets the sharedArrears value for this RemainingBalance.
     * 
     * @return sharedArrears
     */
    public float getSharedArrears() {
        return sharedArrears;
    }


    /**
     * Sets the sharedArrears value for this RemainingBalance.
     * 
     * @param sharedArrears
     */
    public void setSharedArrears(float sharedArrears) {
        this.sharedArrears = sharedArrears;
    }


    /**
     * Gets the balance value for this RemainingBalance.
     * 
     * @return balance
     */
    public float getBalance() {
        return balance;
    }


    /**
     * Sets the balance value for this RemainingBalance.
     * 
     * @param balance
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }


    /**
     * Gets the finalRead value for this RemainingBalance.
     * 
     * @return finalRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getFinalRead() {
        return finalRead;
    }


    /**
     * Sets the finalRead value for this RemainingBalance.
     * 
     * @param finalRead
     */
    public void setFinalRead(com.cannontech.multispeak.deploy.service.MeterRead finalRead) {
        this.finalRead = finalRead;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RemainingBalance)) return false;
        RemainingBalance other = (RemainingBalance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.priorityBalance == other.getPriorityBalance() &&
            this.sharedArrears == other.getSharedArrears() &&
            this.balance == other.getBalance() &&
            ((this.finalRead==null && other.getFinalRead()==null) || 
             (this.finalRead!=null &&
              this.finalRead.equals(other.getFinalRead())));
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
        _hashCode += new Float(getPriorityBalance()).hashCode();
        _hashCode += new Float(getSharedArrears()).hashCode();
        _hashCode += new Float(getBalance()).hashCode();
        if (getFinalRead() != null) {
            _hashCode += getFinalRead().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RemainingBalance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "remainingBalance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sharedArrears");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sharedArrears"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "balance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finalRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "finalRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
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
