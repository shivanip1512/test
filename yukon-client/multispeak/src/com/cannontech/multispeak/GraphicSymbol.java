/**
 * GraphicSymbol.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GraphicSymbol  implements java.io.Serializable {
    private java.lang.Float rotation;
    private java.lang.Float scale;
    private com.cannontech.multispeak.PointType mapLocation;
    private java.lang.String featureType;
    private java.lang.String featureSubtype;

    public GraphicSymbol() {
    }

    public GraphicSymbol(
           java.lang.Float rotation,
           java.lang.Float scale,
           com.cannontech.multispeak.PointType mapLocation,
           java.lang.String featureType,
           java.lang.String featureSubtype) {
           this.rotation = rotation;
           this.scale = scale;
           this.mapLocation = mapLocation;
           this.featureType = featureType;
           this.featureSubtype = featureSubtype;
    }


    /**
     * Gets the rotation value for this GraphicSymbol.
     * 
     * @return rotation
     */
    public java.lang.Float getRotation() {
        return rotation;
    }


    /**
     * Sets the rotation value for this GraphicSymbol.
     * 
     * @param rotation
     */
    public void setRotation(java.lang.Float rotation) {
        this.rotation = rotation;
    }


    /**
     * Gets the scale value for this GraphicSymbol.
     * 
     * @return scale
     */
    public java.lang.Float getScale() {
        return scale;
    }


    /**
     * Sets the scale value for this GraphicSymbol.
     * 
     * @param scale
     */
    public void setScale(java.lang.Float scale) {
        this.scale = scale;
    }


    /**
     * Gets the mapLocation value for this GraphicSymbol.
     * 
     * @return mapLocation
     */
    public com.cannontech.multispeak.PointType getMapLocation() {
        return mapLocation;
    }


    /**
     * Sets the mapLocation value for this GraphicSymbol.
     * 
     * @param mapLocation
     */
    public void setMapLocation(com.cannontech.multispeak.PointType mapLocation) {
        this.mapLocation = mapLocation;
    }


    /**
     * Gets the featureType value for this GraphicSymbol.
     * 
     * @return featureType
     */
    public java.lang.String getFeatureType() {
        return featureType;
    }


    /**
     * Sets the featureType value for this GraphicSymbol.
     * 
     * @param featureType
     */
    public void setFeatureType(java.lang.String featureType) {
        this.featureType = featureType;
    }


    /**
     * Gets the featureSubtype value for this GraphicSymbol.
     * 
     * @return featureSubtype
     */
    public java.lang.String getFeatureSubtype() {
        return featureSubtype;
    }


    /**
     * Sets the featureSubtype value for this GraphicSymbol.
     * 
     * @param featureSubtype
     */
    public void setFeatureSubtype(java.lang.String featureSubtype) {
        this.featureSubtype = featureSubtype;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GraphicSymbol)) return false;
        GraphicSymbol other = (GraphicSymbol) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rotation==null && other.getRotation()==null) || 
             (this.rotation!=null &&
              this.rotation.equals(other.getRotation()))) &&
            ((this.scale==null && other.getScale()==null) || 
             (this.scale!=null &&
              this.scale.equals(other.getScale()))) &&
            ((this.mapLocation==null && other.getMapLocation()==null) || 
             (this.mapLocation!=null &&
              this.mapLocation.equals(other.getMapLocation()))) &&
            ((this.featureType==null && other.getFeatureType()==null) || 
             (this.featureType!=null &&
              this.featureType.equals(other.getFeatureType()))) &&
            ((this.featureSubtype==null && other.getFeatureSubtype()==null) || 
             (this.featureSubtype!=null &&
              this.featureSubtype.equals(other.getFeatureSubtype())));
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
        if (getRotation() != null) {
            _hashCode += getRotation().hashCode();
        }
        if (getScale() != null) {
            _hashCode += getScale().hashCode();
        }
        if (getMapLocation() != null) {
            _hashCode += getMapLocation().hashCode();
        }
        if (getFeatureType() != null) {
            _hashCode += getFeatureType().hashCode();
        }
        if (getFeatureSubtype() != null) {
            _hashCode += getFeatureSubtype().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GraphicSymbol.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rotation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "rotation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scale");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mapLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "featureType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureSubtype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "featureSubtype"));
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
