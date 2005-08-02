/**
 * AbstractGeometryType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class AbstractGeometryType  implements java.io.Serializable {
    private org.apache.axis.types.Id gid;  // attribute
    private org.apache.axis.types.URI srsName;  // attribute

    public AbstractGeometryType() {
    }

    public AbstractGeometryType(
           org.apache.axis.types.Id gid,
           org.apache.axis.types.URI srsName) {
           this.gid = gid;
           this.srsName = srsName;
    }


    /**
     * Gets the gid value for this AbstractGeometryType.
     * 
     * @return gid
     */
    public org.apache.axis.types.Id getGid() {
        return gid;
    }


    /**
     * Sets the gid value for this AbstractGeometryType.
     * 
     * @param gid
     */
    public void setGid(org.apache.axis.types.Id gid) {
        this.gid = gid;
    }


    /**
     * Gets the srsName value for this AbstractGeometryType.
     * 
     * @return srsName
     */
    public org.apache.axis.types.URI getSrsName() {
        return srsName;
    }


    /**
     * Sets the srsName value for this AbstractGeometryType.
     * 
     * @param srsName
     */
    public void setSrsName(org.apache.axis.types.URI srsName) {
        this.srsName = srsName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AbstractGeometryType)) return false;
        AbstractGeometryType other = (AbstractGeometryType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.gid==null && other.getGid()==null) || 
             (this.gid!=null &&
              this.gid.equals(other.getGid()))) &&
            ((this.srsName==null && other.getSrsName()==null) || 
             (this.srsName!=null &&
              this.srsName.equals(other.getSrsName())));
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
        if (getGid() != null) {
            _hashCode += getGid().hashCode();
        }
        if (getSrsName() != null) {
            _hashCode += getSrsName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AbstractGeometryType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AbstractGeometryType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("gid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "gid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "ID"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("srsName");
        attrField.setXmlName(new javax.xml.namespace.QName("", "srsName"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(attrField);
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
