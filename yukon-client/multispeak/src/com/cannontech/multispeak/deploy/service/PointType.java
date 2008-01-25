/**
 * PointType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PointType  extends com.cannontech.multispeak.deploy.service.AbstractGeometryType  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CoordType coord;

    private com.cannontech.multispeak.deploy.service.CoordinatesType coordinates;

    public PointType() {
    }

    public PointType(
           com.cannontech.multispeak.deploy.service.CoordType coord,
           com.cannontech.multispeak.deploy.service.CoordinatesType coordinates) {
        this.coord = coord;
        this.coordinates = coordinates;
    }


    /**
     * Gets the coord value for this PointType.
     * 
     * @return coord
     */
    public com.cannontech.multispeak.deploy.service.CoordType getCoord() {
        return coord;
    }


    /**
     * Sets the coord value for this PointType.
     * 
     * @param coord
     */
    public void setCoord(com.cannontech.multispeak.deploy.service.CoordType coord) {
        this.coord = coord;
    }


    /**
     * Gets the coordinates value for this PointType.
     * 
     * @return coordinates
     */
    public com.cannontech.multispeak.deploy.service.CoordinatesType getCoordinates() {
        return coordinates;
    }


    /**
     * Sets the coordinates value for this PointType.
     * 
     * @param coordinates
     */
    public void setCoordinates(com.cannontech.multispeak.deploy.service.CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PointType)) return false;
        PointType other = (PointType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.coord==null && other.getCoord()==null) || 
             (this.coord!=null &&
              this.coord.equals(other.getCoord()))) &&
            ((this.coordinates==null && other.getCoordinates()==null) || 
             (this.coordinates!=null &&
              this.coordinates.equals(other.getCoordinates())));
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
        if (getCoord() != null) {
            _hashCode += getCoord().hashCode();
        }
        if (getCoordinates() != null) {
            _hashCode += getCoordinates().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PointType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("coord");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("coordinates");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coordinates"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordinatesType"));
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
