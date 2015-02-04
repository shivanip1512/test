/**
 * AchPayment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AchPayment  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CheckInfo checkInfo;

    private com.cannontech.multispeak.deploy.service.Tender tender;

    public AchPayment() {
    }

    public AchPayment(
           com.cannontech.multispeak.deploy.service.CheckInfo checkInfo,
           com.cannontech.multispeak.deploy.service.Tender tender) {
           this.checkInfo = checkInfo;
           this.tender = tender;
    }


    /**
     * Gets the checkInfo value for this AchPayment.
     * 
     * @return checkInfo
     */
    public com.cannontech.multispeak.deploy.service.CheckInfo getCheckInfo() {
        return checkInfo;
    }


    /**
     * Sets the checkInfo value for this AchPayment.
     * 
     * @param checkInfo
     */
    public void setCheckInfo(com.cannontech.multispeak.deploy.service.CheckInfo checkInfo) {
        this.checkInfo = checkInfo;
    }


    /**
     * Gets the tender value for this AchPayment.
     * 
     * @return tender
     */
    public com.cannontech.multispeak.deploy.service.Tender getTender() {
        return tender;
    }


    /**
     * Sets the tender value for this AchPayment.
     * 
     * @param tender
     */
    public void setTender(com.cannontech.multispeak.deploy.service.Tender tender) {
        this.tender = tender;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AchPayment)) return false;
        AchPayment other = (AchPayment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.checkInfo==null && other.getCheckInfo()==null) || 
             (this.checkInfo!=null &&
              this.checkInfo.equals(other.getCheckInfo()))) &&
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
        if (getCheckInfo() != null) {
            _hashCode += getCheckInfo().hashCode();
        }
        if (getTender() != null) {
            _hashCode += getTender().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AchPayment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "achPayment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "checkInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "checkInfo"));
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
