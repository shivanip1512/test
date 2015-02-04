/**
 * GetIHDGroupNamesByInHomeDisplayID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetIHDGroupNamesByInHomeDisplayID  implements java.io.Serializable {
    private java.lang.String inHomeDisplayID;

    public GetIHDGroupNamesByInHomeDisplayID() {
    }

    public GetIHDGroupNamesByInHomeDisplayID(
           java.lang.String inHomeDisplayID) {
           this.inHomeDisplayID = inHomeDisplayID;
    }


    /**
     * Gets the inHomeDisplayID value for this GetIHDGroupNamesByInHomeDisplayID.
     * 
     * @return inHomeDisplayID
     */
    public java.lang.String getInHomeDisplayID() {
        return inHomeDisplayID;
    }


    /**
     * Sets the inHomeDisplayID value for this GetIHDGroupNamesByInHomeDisplayID.
     * 
     * @param inHomeDisplayID
     */
    public void setInHomeDisplayID(java.lang.String inHomeDisplayID) {
        this.inHomeDisplayID = inHomeDisplayID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetIHDGroupNamesByInHomeDisplayID)) return false;
        GetIHDGroupNamesByInHomeDisplayID other = (GetIHDGroupNamesByInHomeDisplayID) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.inHomeDisplayID==null && other.getInHomeDisplayID()==null) || 
             (this.inHomeDisplayID!=null &&
              this.inHomeDisplayID.equals(other.getInHomeDisplayID())));
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
        if (getInHomeDisplayID() != null) {
            _hashCode += getInHomeDisplayID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetIHDGroupNamesByInHomeDisplayID.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetIHDGroupNamesByInHomeDisplayID"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID"));
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
