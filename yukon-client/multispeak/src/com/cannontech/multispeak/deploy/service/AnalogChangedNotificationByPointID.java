/**
 * AnalogChangedNotificationByPointID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AnalogChangedNotificationByPointID  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs;

    private java.lang.String transactionID;

    public AnalogChangedNotificationByPointID() {
    }

    public AnalogChangedNotificationByPointID(
           com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs,
           java.lang.String transactionID) {
           this.scadaAnalogs = scadaAnalogs;
           this.transactionID = transactionID;
    }


    /**
     * Gets the scadaAnalogs value for this AnalogChangedNotificationByPointID.
     * 
     * @return scadaAnalogs
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog[] getScadaAnalogs() {
        return scadaAnalogs;
    }


    /**
     * Sets the scadaAnalogs value for this AnalogChangedNotificationByPointID.
     * 
     * @param scadaAnalogs
     */
    public void setScadaAnalogs(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) {
        this.scadaAnalogs = scadaAnalogs;
    }


    /**
     * Gets the transactionID value for this AnalogChangedNotificationByPointID.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this AnalogChangedNotificationByPointID.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnalogChangedNotificationByPointID)) return false;
        AnalogChangedNotificationByPointID other = (AnalogChangedNotificationByPointID) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.scadaAnalogs==null && other.getScadaAnalogs()==null) || 
             (this.scadaAnalogs!=null &&
              java.util.Arrays.equals(this.scadaAnalogs, other.getScadaAnalogs()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID())));
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
        if (getScadaAnalogs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getScadaAnalogs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getScadaAnalogs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AnalogChangedNotificationByPointID.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">AnalogChangedNotificationByPointID"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaAnalogs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalogs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"));
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
