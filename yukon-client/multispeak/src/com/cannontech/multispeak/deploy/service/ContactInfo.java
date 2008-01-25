/**
 * ContactInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ContactInfo  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PhoneNumber[] phoneList;

    private com.cannontech.multispeak.deploy.service.EMailAddress[] eMailList;

    public ContactInfo() {
    }

    public ContactInfo(
           com.cannontech.multispeak.deploy.service.PhoneNumber[] phoneList,
           com.cannontech.multispeak.deploy.service.EMailAddress[] eMailList) {
           this.phoneList = phoneList;
           this.eMailList = eMailList;
    }


    /**
     * Gets the phoneList value for this ContactInfo.
     * 
     * @return phoneList
     */
    public com.cannontech.multispeak.deploy.service.PhoneNumber[] getPhoneList() {
        return phoneList;
    }


    /**
     * Sets the phoneList value for this ContactInfo.
     * 
     * @param phoneList
     */
    public void setPhoneList(com.cannontech.multispeak.deploy.service.PhoneNumber[] phoneList) {
        this.phoneList = phoneList;
    }


    /**
     * Gets the eMailList value for this ContactInfo.
     * 
     * @return eMailList
     */
    public com.cannontech.multispeak.deploy.service.EMailAddress[] getEMailList() {
        return eMailList;
    }


    /**
     * Sets the eMailList value for this ContactInfo.
     * 
     * @param eMailList
     */
    public void setEMailList(com.cannontech.multispeak.deploy.service.EMailAddress[] eMailList) {
        this.eMailList = eMailList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContactInfo)) return false;
        ContactInfo other = (ContactInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.phoneList==null && other.getPhoneList()==null) || 
             (this.phoneList!=null &&
              java.util.Arrays.equals(this.phoneList, other.getPhoneList()))) &&
            ((this.eMailList==null && other.getEMailList()==null) || 
             (this.eMailList!=null &&
              java.util.Arrays.equals(this.eMailList, other.getEMailList())));
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
        if (getPhoneList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPhoneList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPhoneList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEMailList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEMailList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEMailList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ContactInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMailList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress"));
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
