/**
 * PpmStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PpmStatus  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterStatus meterStatus;

    private com.cannontech.multispeak.deploy.service.ErrorObject errorObject;

    private com.cannontech.multispeak.deploy.service.RemainingBalance remainingBalance;

    public PpmStatus() {
    }

    public PpmStatus(
           com.cannontech.multispeak.deploy.service.MeterStatus meterStatus,
           com.cannontech.multispeak.deploy.service.ErrorObject errorObject,
           com.cannontech.multispeak.deploy.service.RemainingBalance remainingBalance) {
           this.meterStatus = meterStatus;
           this.errorObject = errorObject;
           this.remainingBalance = remainingBalance;
    }


    /**
     * Gets the meterStatus value for this PpmStatus.
     * 
     * @return meterStatus
     */
    public com.cannontech.multispeak.deploy.service.MeterStatus getMeterStatus() {
        return meterStatus;
    }


    /**
     * Sets the meterStatus value for this PpmStatus.
     * 
     * @param meterStatus
     */
    public void setMeterStatus(com.cannontech.multispeak.deploy.service.MeterStatus meterStatus) {
        this.meterStatus = meterStatus;
    }


    /**
     * Gets the errorObject value for this PpmStatus.
     * 
     * @return errorObject
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject getErrorObject() {
        return errorObject;
    }


    /**
     * Sets the errorObject value for this PpmStatus.
     * 
     * @param errorObject
     */
    public void setErrorObject(com.cannontech.multispeak.deploy.service.ErrorObject errorObject) {
        this.errorObject = errorObject;
    }


    /**
     * Gets the remainingBalance value for this PpmStatus.
     * 
     * @return remainingBalance
     */
    public com.cannontech.multispeak.deploy.service.RemainingBalance getRemainingBalance() {
        return remainingBalance;
    }


    /**
     * Sets the remainingBalance value for this PpmStatus.
     * 
     * @param remainingBalance
     */
    public void setRemainingBalance(com.cannontech.multispeak.deploy.service.RemainingBalance remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PpmStatus)) return false;
        PpmStatus other = (PpmStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterStatus==null && other.getMeterStatus()==null) || 
             (this.meterStatus!=null &&
              this.meterStatus.equals(other.getMeterStatus()))) &&
            ((this.errorObject==null && other.getErrorObject()==null) || 
             (this.errorObject!=null &&
              this.errorObject.equals(other.getErrorObject()))) &&
            ((this.remainingBalance==null && other.getRemainingBalance()==null) || 
             (this.remainingBalance!=null &&
              this.remainingBalance.equals(other.getRemainingBalance())));
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
        if (getMeterStatus() != null) {
            _hashCode += getMeterStatus().hashCode();
        }
        if (getErrorObject() != null) {
            _hashCode += getErrorObject().hashCode();
        }
        if (getRemainingBalance() != null) {
            _hashCode += getRemainingBalance().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PpmStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remainingBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "remainingBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "remainingBalance"));
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
