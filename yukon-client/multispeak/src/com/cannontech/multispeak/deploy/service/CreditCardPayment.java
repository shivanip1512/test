/**
 * CreditCardPayment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CreditCardPayment  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CreditCardInfo creditCardInfo;

    private com.cannontech.multispeak.deploy.service.Tender tender;

    public CreditCardPayment() {
    }

    public CreditCardPayment(
           com.cannontech.multispeak.deploy.service.CreditCardInfo creditCardInfo,
           com.cannontech.multispeak.deploy.service.Tender tender) {
           this.creditCardInfo = creditCardInfo;
           this.tender = tender;
    }


    /**
     * Gets the creditCardInfo value for this CreditCardPayment.
     * 
     * @return creditCardInfo
     */
    public com.cannontech.multispeak.deploy.service.CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }


    /**
     * Sets the creditCardInfo value for this CreditCardPayment.
     * 
     * @param creditCardInfo
     */
    public void setCreditCardInfo(com.cannontech.multispeak.deploy.service.CreditCardInfo creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }


    /**
     * Gets the tender value for this CreditCardPayment.
     * 
     * @return tender
     */
    public com.cannontech.multispeak.deploy.service.Tender getTender() {
        return tender;
    }


    /**
     * Sets the tender value for this CreditCardPayment.
     * 
     * @param tender
     */
    public void setTender(com.cannontech.multispeak.deploy.service.Tender tender) {
        this.tender = tender;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreditCardPayment)) return false;
        CreditCardPayment other = (CreditCardPayment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.creditCardInfo==null && other.getCreditCardInfo()==null) || 
             (this.creditCardInfo!=null &&
              this.creditCardInfo.equals(other.getCreditCardInfo()))) &&
            ((this.tender==null && other.getTender()==null) || 
             (this.tender!=null &&
              this.tender.equals(other.getTender())));
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
        if (getCreditCardInfo() != null) {
            _hashCode += getCreditCardInfo().hashCode();
        }
        if (getTender() != null) {
            _hashCode += getTender().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreditCardPayment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardPayment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditCardInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "creditCardInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tender"));
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
