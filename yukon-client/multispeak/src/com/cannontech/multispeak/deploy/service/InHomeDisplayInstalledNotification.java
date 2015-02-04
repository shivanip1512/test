/**
 * InHomeDisplayInstalledNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InHomeDisplayInstalledNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs;

    public InHomeDisplayInstalledNotification() {
    }

    public InHomeDisplayInstalledNotification(
           com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) {
           this.addedIHDs = addedIHDs;
    }


    /**
     * Gets the addedIHDs value for this InHomeDisplayInstalledNotification.
     * 
     * @return addedIHDs
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplay[] getAddedIHDs() {
        return addedIHDs;
    }


    /**
     * Sets the addedIHDs value for this InHomeDisplayInstalledNotification.
     * 
     * @param addedIHDs
     */
    public void setAddedIHDs(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) {
        this.addedIHDs = addedIHDs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InHomeDisplayInstalledNotification)) return false;
        InHomeDisplayInstalledNotification other = (InHomeDisplayInstalledNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addedIHDs==null && other.getAddedIHDs()==null) || 
             (this.addedIHDs!=null &&
              java.util.Arrays.equals(this.addedIHDs, other.getAddedIHDs())));
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
        if (getAddedIHDs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAddedIHDs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAddedIHDs(), i);
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
        new org.apache.axis.description.TypeDesc(InHomeDisplayInstalledNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InHomeDisplayInstalledNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addedIHDs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "addedIHDs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplay"));
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
