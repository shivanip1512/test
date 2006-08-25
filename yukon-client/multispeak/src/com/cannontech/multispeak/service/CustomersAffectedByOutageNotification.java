/**
 * CustomersAffectedByOutageNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CustomersAffectedByOutageNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage newOutages;

    public CustomersAffectedByOutageNotification() {
    }

    public CustomersAffectedByOutageNotification(
           com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage newOutages) {
           this.newOutages = newOutages;
    }


    /**
     * Gets the newOutages value for this CustomersAffectedByOutageNotification.
     * 
     * @return newOutages
     */
    public com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage getNewOutages() {
        return newOutages;
    }


    /**
     * Sets the newOutages value for this CustomersAffectedByOutageNotification.
     * 
     * @param newOutages
     */
    public void setNewOutages(com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage newOutages) {
        this.newOutages = newOutages;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomersAffectedByOutageNotification)) return false;
        CustomersAffectedByOutageNotification other = (CustomersAffectedByOutageNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.newOutages==null && other.getNewOutages()==null) || 
             (this.newOutages!=null &&
              this.newOutages.equals(other.getNewOutages())));
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
        if (getNewOutages() != null) {
            _hashCode += getNewOutages().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CustomersAffectedByOutageNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CustomersAffectedByOutageNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newOutages");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newOutages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCustomersAffectedByOutage"));
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
