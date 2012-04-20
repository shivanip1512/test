/**
 * SecondaryJunctionBox.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SecondaryJunctionBox  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private java.lang.String junctionBoxType;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public SecondaryJunctionBox() {
    }

    public SecondaryJunctionBox(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           java.lang.String junctionBoxType,
           com.cannontech.multispeak.deploy.service.GPS GPS) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.junctionBoxType = junctionBoxType;
        this.GPS = GPS;
    }


    /**
     * Gets the junctionBoxType value for this SecondaryJunctionBox.
     * 
     * @return junctionBoxType
     */
    public java.lang.String getJunctionBoxType() {
        return junctionBoxType;
    }


    /**
     * Sets the junctionBoxType value for this SecondaryJunctionBox.
     * 
     * @param junctionBoxType
     */
    public void setJunctionBoxType(java.lang.String junctionBoxType) {
        this.junctionBoxType = junctionBoxType;
    }


    /**
     * Gets the GPS value for this SecondaryJunctionBox.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this SecondaryJunctionBox.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SecondaryJunctionBox)) return false;
        SecondaryJunctionBox other = (SecondaryJunctionBox) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.junctionBoxType==null && other.getJunctionBoxType()==null) || 
             (this.junctionBoxType!=null &&
              this.junctionBoxType.equals(other.getJunctionBoxType()))) &&
            ((this.GPS==null && other.getGPS()==null) || 
             (this.GPS!=null &&
              this.GPS.equals(other.getGPS())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getJunctionBoxType() != null) {
            _hashCode += getJunctionBoxType().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SecondaryJunctionBox.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryJunctionBox"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("junctionBoxType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "junctionBoxType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GPS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
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
