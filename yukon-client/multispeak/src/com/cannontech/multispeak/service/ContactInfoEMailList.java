/**
 * ContactInfoEMailList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ContactInfoEMailList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.EMailAddress[] eMailAddress;

    public ContactInfoEMailList() {
    }

    public ContactInfoEMailList(
           com.cannontech.multispeak.service.EMailAddress[] eMailAddress) {
           this.eMailAddress = eMailAddress;
    }


    /**
     * Gets the eMailAddress value for this ContactInfoEMailList.
     * 
     * @return eMailAddress
     */
    public com.cannontech.multispeak.service.EMailAddress[] getEMailAddress() {
        return eMailAddress;
    }


    /**
     * Sets the eMailAddress value for this ContactInfoEMailList.
     * 
     * @param eMailAddress
     */
    public void setEMailAddress(com.cannontech.multispeak.service.EMailAddress[] eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    public com.cannontech.multispeak.service.EMailAddress getEMailAddress(int i) {
        return this.eMailAddress[i];
    }

    public void setEMailAddress(int i, com.cannontech.multispeak.service.EMailAddress _value) {
        this.eMailAddress[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContactInfoEMailList)) return false;
        ContactInfoEMailList other = (ContactInfoEMailList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eMailAddress==null && other.getEMailAddress()==null) || 
             (this.eMailAddress!=null &&
              java.util.Arrays.equals(this.eMailAddress, other.getEMailAddress())));
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
        if (getEMailAddress() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEMailAddress());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEMailAddress(), i);
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
        new org.apache.axis.description.TypeDesc(ContactInfoEMailList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">contactInfo>eMailList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress"));
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
