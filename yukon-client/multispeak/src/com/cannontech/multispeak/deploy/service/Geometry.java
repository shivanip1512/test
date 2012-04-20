/**
 * Geometry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Geometry  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.LineStringType[] GMLLines;

    private com.cannontech.multispeak.deploy.service.PolygonType[] GMLPolygons;

    private com.cannontech.multispeak.deploy.service.PointType GMLLocation;

    public Geometry() {
    }

    public Geometry(
           com.cannontech.multispeak.deploy.service.LineStringType[] GMLLines,
           com.cannontech.multispeak.deploy.service.PolygonType[] GMLPolygons,
           com.cannontech.multispeak.deploy.service.PointType GMLLocation) {
           this.GMLLines = GMLLines;
           this.GMLPolygons = GMLPolygons;
           this.GMLLocation = GMLLocation;
    }


    /**
     * Gets the GMLLines value for this Geometry.
     * 
     * @return GMLLines
     */
    public com.cannontech.multispeak.deploy.service.LineStringType[] getGMLLines() {
        return GMLLines;
    }


    /**
     * Sets the GMLLines value for this Geometry.
     * 
     * @param GMLLines
     */
    public void setGMLLines(com.cannontech.multispeak.deploy.service.LineStringType[] GMLLines) {
        this.GMLLines = GMLLines;
    }


    /**
     * Gets the GMLPolygons value for this Geometry.
     * 
     * @return GMLPolygons
     */
    public com.cannontech.multispeak.deploy.service.PolygonType[] getGMLPolygons() {
        return GMLPolygons;
    }


    /**
     * Sets the GMLPolygons value for this Geometry.
     * 
     * @param GMLPolygons
     */
    public void setGMLPolygons(com.cannontech.multispeak.deploy.service.PolygonType[] GMLPolygons) {
        this.GMLPolygons = GMLPolygons;
    }


    /**
     * Gets the GMLLocation value for this Geometry.
     * 
     * @return GMLLocation
     */
    public com.cannontech.multispeak.deploy.service.PointType getGMLLocation() {
        return GMLLocation;
    }


    /**
     * Sets the GMLLocation value for this Geometry.
     * 
     * @param GMLLocation
     */
    public void setGMLLocation(com.cannontech.multispeak.deploy.service.PointType GMLLocation) {
        this.GMLLocation = GMLLocation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Geometry)) return false;
        Geometry other = (Geometry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.GMLLines==null && other.getGMLLines()==null) || 
             (this.GMLLines!=null &&
              java.util.Arrays.equals(this.GMLLines, other.getGMLLines()))) &&
            ((this.GMLPolygons==null && other.getGMLPolygons()==null) || 
             (this.GMLPolygons!=null &&
              java.util.Arrays.equals(this.GMLPolygons, other.getGMLPolygons()))) &&
            ((this.GMLLocation==null && other.getGMLLocation()==null) || 
             (this.GMLLocation!=null &&
              this.GMLLocation.equals(other.getGMLLocation())));
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
        if (getGMLLines() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGMLLines());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGMLLines(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGMLPolygons() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGMLPolygons());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGMLPolygons(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGMLLocation() != null) {
            _hashCode += getGMLLocation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Geometry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "geometry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GMLLines");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMLLines"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LineStringType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexLine"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GMLPolygons");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMLPolygons"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PolygonType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "Polygon"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GMLLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMLLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
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
