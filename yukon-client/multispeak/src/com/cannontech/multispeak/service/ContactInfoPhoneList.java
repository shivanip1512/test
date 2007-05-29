/**
 * ContactInfoPhoneList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ContactInfoPhoneList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.PhoneNumber[] phoneNumber;

    public ContactInfoPhoneList() {
    }

    public ContactInfoPhoneList(
           com.cannontech.multispeak.service.PhoneNumber[] phoneNumber) {
           this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the phoneNumber value for this ContactInfoPhoneList.
     * 
     * @return phoneNumber
     */
    public com.cannontech.multispeak.service.PhoneNumber[] getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this ContactInfoPhoneList.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(com.cannontech.multispeak.service.PhoneNumber[] phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public com.cannontech.multispeak.service.PhoneNumber getPhoneNumber(int i) {
        return this.phoneNumber[i];
    }

    public void setPhoneNumber(int i, com.cannontech.multispeak.service.PhoneNumber _value) {
        this.phoneNumber[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContactInfoPhoneList)) return false;
        ContactInfoPhoneList other = (ContactInfoPhoneList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.phoneNumber==null && other.getPhoneNumber()==null) || 
             (this.phoneNumber!=null &&
              java.util.Arrays.equals(this.phoneNumber, other.getPhoneNumber())));
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
        if (getPhoneNumber() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPhoneNumber());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPhoneNumber(), i);
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
        new org.apache.axis.description.TypeDesc(ContactInfoPhoneList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>phoneList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phoneNumber"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
