/**
 * MspPointObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspPointObject  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.PointType mapLocation;
    private java.lang.String gridLocation;
    private java.lang.Float rotation;
    private java.lang.String facilityID;

    public MspPointObject() {
    }

    public MspPointObject(
           com.cannontech.multispeak.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID) {
           this.mapLocation = mapLocation;
           this.gridLocation = gridLocation;
           this.rotation = rotation;
           this.facilityID = facilityID;
    }


    /**
     * Gets the mapLocation value for this MspPointObject.
     * 
     * @return mapLocation
     */
    public com.cannontech.multispeak.PointType getMapLocation() {
        return mapLocation;
    }


    /**
     * Sets the mapLocation value for this MspPointObject.
     * 
     * @param mapLocation
     */
    public void setMapLocation(com.cannontech.multispeak.PointType mapLocation) {
        this.mapLocation = mapLocation;
    }


    /**
     * Gets the gridLocation value for this MspPointObject.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this MspPointObject.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }


    /**
     * Gets the rotation value for this MspPointObject.
     * 
     * @return rotation
     */
    public java.lang.Float getRotation() {
        return rotation;
    }


    /**
     * Sets the rotation value for this MspPointObject.
     * 
     * @param rotation
     */
    public void setRotation(java.lang.Float rotation) {
        this.rotation = rotation;
    }


    /**
     * Gets the facilityID value for this MspPointObject.
     * 
     * @return facilityID
     */
    public java.lang.String getFacilityID() {
        return facilityID;
    }


    /**
     * Sets the facilityID value for this MspPointObject.
     * 
     * @param facilityID
     */
    public void setFacilityID(java.lang.String facilityID) {
        this.facilityID = facilityID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspPointObject)) return false;
        MspPointObject other = (MspPointObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.mapLocation==null && other.getMapLocation()==null) || 
             (this.mapLocation!=null &&
              this.mapLocation.equals(other.getMapLocation()))) &&
            ((this.gridLocation==null && other.getGridLocation()==null) || 
             (this.gridLocation!=null &&
              this.gridLocation.equals(other.getGridLocation()))) &&
            ((this.rotation==null && other.getRotation()==null) || 
             (this.rotation!=null &&
              this.rotation.equals(other.getRotation()))) &&
            ((this.facilityID==null && other.getFacilityID()==null) || 
             (this.facilityID!=null &&
              this.facilityID.equals(other.getFacilityID())));
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
        if (getMapLocation() != null) {
            _hashCode += getMapLocation().hashCode();
        }
        if (getGridLocation() != null) {
            _hashCode += getGridLocation().hashCode();
        }
        if (getRotation() != null) {
            _hashCode += getRotation().hashCode();
        }
        if (getFacilityID() != null) {
            _hashCode += getFacilityID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspPointObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPointObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mapLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gridLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rotation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "rotation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facilityID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"));
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
