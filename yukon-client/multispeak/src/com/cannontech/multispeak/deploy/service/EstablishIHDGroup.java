/**
 * EstablishIHDGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EstablishIHDGroup  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.InHomeDisplayGroup IHDGroup;

    public EstablishIHDGroup() {
    }

    public EstablishIHDGroup(
           com.cannontech.multispeak.deploy.service.InHomeDisplayGroup IHDGroup) {
           this.IHDGroup = IHDGroup;
    }


    /**
     * Gets the IHDGroup value for this EstablishIHDGroup.
     * 
     * @return IHDGroup
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayGroup getIHDGroup() {
        return IHDGroup;
    }


    /**
     * Sets the IHDGroup value for this EstablishIHDGroup.
     * 
     * @param IHDGroup
     */
    public void setIHDGroup(com.cannontech.multispeak.deploy.service.InHomeDisplayGroup IHDGroup) {
        this.IHDGroup = IHDGroup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EstablishIHDGroup)) return false;
        EstablishIHDGroup other = (EstablishIHDGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.IHDGroup==null && other.getIHDGroup()==null) || 
             (this.IHDGroup!=null &&
              this.IHDGroup.equals(other.getIHDGroup())));
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
        if (getIHDGroup() != null) {
            _hashCode += getIHDGroup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EstablishIHDGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">EstablishIHDGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IHDGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IHDGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup"));
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
