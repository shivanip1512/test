/**
 * ScadaPoint.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ScadaPoint  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private java.lang.String description;

    private com.cannontech.multispeak.deploy.service.ScadaPointType scadaPointType;

    private java.lang.String GISFeatureID;

    private java.lang.String GISFeatureType;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public ScadaPoint() {
    }

    public ScadaPoint(
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
           java.lang.String description,
           com.cannontech.multispeak.deploy.service.ScadaPointType scadaPointType,
           java.lang.String GISFeatureID,
           java.lang.String GISFeatureType,
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
        this.description = description;
        this.scadaPointType = scadaPointType;
        this.GISFeatureID = GISFeatureID;
        this.GISFeatureType = GISFeatureType;
        this.GPS = GPS;
    }


    /**
     * Gets the description value for this ScadaPoint.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this ScadaPoint.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the scadaPointType value for this ScadaPoint.
     * 
     * @return scadaPointType
     */
    public com.cannontech.multispeak.deploy.service.ScadaPointType getScadaPointType() {
        return scadaPointType;
    }


    /**
     * Sets the scadaPointType value for this ScadaPoint.
     * 
     * @param scadaPointType
     */
    public void setScadaPointType(com.cannontech.multispeak.deploy.service.ScadaPointType scadaPointType) {
        this.scadaPointType = scadaPointType;
    }


    /**
     * Gets the GISFeatureID value for this ScadaPoint.
     * 
     * @return GISFeatureID
     */
    public java.lang.String getGISFeatureID() {
        return GISFeatureID;
    }


    /**
     * Sets the GISFeatureID value for this ScadaPoint.
     * 
     * @param GISFeatureID
     */
    public void setGISFeatureID(java.lang.String GISFeatureID) {
        this.GISFeatureID = GISFeatureID;
    }


    /**
     * Gets the GISFeatureType value for this ScadaPoint.
     * 
     * @return GISFeatureType
     */
    public java.lang.String getGISFeatureType() {
        return GISFeatureType;
    }


    /**
     * Sets the GISFeatureType value for this ScadaPoint.
     * 
     * @param GISFeatureType
     */
    public void setGISFeatureType(java.lang.String GISFeatureType) {
        this.GISFeatureType = GISFeatureType;
    }


    /**
     * Gets the GPS value for this ScadaPoint.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this ScadaPoint.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScadaPoint)) return false;
        ScadaPoint other = (ScadaPoint) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.scadaPointType==null && other.getScadaPointType()==null) || 
             (this.scadaPointType!=null &&
              this.scadaPointType.equals(other.getScadaPointType()))) &&
            ((this.GISFeatureID==null && other.getGISFeatureID()==null) || 
             (this.GISFeatureID!=null &&
              this.GISFeatureID.equals(other.getGISFeatureID()))) &&
            ((this.GISFeatureType==null && other.getGISFeatureType()==null) || 
             (this.GISFeatureType!=null &&
              this.GISFeatureType.equals(other.getGISFeatureType()))) &&
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getScadaPointType() != null) {
            _hashCode += getScadaPointType().hashCode();
        }
        if (getGISFeatureID() != null) {
            _hashCode += getGISFeatureID().hashCode();
        }
        if (getGISFeatureType() != null) {
            _hashCode += getGISFeatureType().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScadaPoint.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPoint"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaPointType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GISFeatureID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GISFeatureID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GISFeatureType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GISFeatureType"));
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
