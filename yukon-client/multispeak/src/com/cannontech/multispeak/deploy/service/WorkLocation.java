/**
 * WorkLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WorkLocation  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Address address;

    private com.cannontech.multispeak.deploy.service.Geometry geometry;

    private java.lang.String servLoc;

    private java.lang.String gridLocation;

    private java.lang.String locationComment;

    private com.cannontech.multispeak.deploy.service.ObjectRef[] locationReferences;

    public WorkLocation() {
    }

    public WorkLocation(
           com.cannontech.multispeak.deploy.service.Address address,
           com.cannontech.multispeak.deploy.service.Geometry geometry,
           java.lang.String servLoc,
           java.lang.String gridLocation,
           java.lang.String locationComment,
           com.cannontech.multispeak.deploy.service.ObjectRef[] locationReferences) {
           this.address = address;
           this.geometry = geometry;
           this.servLoc = servLoc;
           this.gridLocation = gridLocation;
           this.locationComment = locationComment;
           this.locationReferences = locationReferences;
    }


    /**
     * Gets the address value for this WorkLocation.
     * 
     * @return address
     */
    public com.cannontech.multispeak.deploy.service.Address getAddress() {
        return address;
    }


    /**
     * Sets the address value for this WorkLocation.
     * 
     * @param address
     */
    public void setAddress(com.cannontech.multispeak.deploy.service.Address address) {
        this.address = address;
    }


    /**
     * Gets the geometry value for this WorkLocation.
     * 
     * @return geometry
     */
    public com.cannontech.multispeak.deploy.service.Geometry getGeometry() {
        return geometry;
    }


    /**
     * Sets the geometry value for this WorkLocation.
     * 
     * @param geometry
     */
    public void setGeometry(com.cannontech.multispeak.deploy.service.Geometry geometry) {
        this.geometry = geometry;
    }


    /**
     * Gets the servLoc value for this WorkLocation.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this WorkLocation.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the gridLocation value for this WorkLocation.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this WorkLocation.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }


    /**
     * Gets the locationComment value for this WorkLocation.
     * 
     * @return locationComment
     */
    public java.lang.String getLocationComment() {
        return locationComment;
    }


    /**
     * Sets the locationComment value for this WorkLocation.
     * 
     * @param locationComment
     */
    public void setLocationComment(java.lang.String locationComment) {
        this.locationComment = locationComment;
    }


    /**
     * Gets the locationReferences value for this WorkLocation.
     * 
     * @return locationReferences
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef[] getLocationReferences() {
        return locationReferences;
    }


    /**
     * Sets the locationReferences value for this WorkLocation.
     * 
     * @param locationReferences
     */
    public void setLocationReferences(com.cannontech.multispeak.deploy.service.ObjectRef[] locationReferences) {
        this.locationReferences = locationReferences;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WorkLocation)) return false;
        WorkLocation other = (WorkLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.geometry==null && other.getGeometry()==null) || 
             (this.geometry!=null &&
              this.geometry.equals(other.getGeometry()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.gridLocation==null && other.getGridLocation()==null) || 
             (this.gridLocation!=null &&
              this.gridLocation.equals(other.getGridLocation()))) &&
            ((this.locationComment==null && other.getLocationComment()==null) || 
             (this.locationComment!=null &&
              this.locationComment.equals(other.getLocationComment()))) &&
            ((this.locationReferences==null && other.getLocationReferences()==null) || 
             (this.locationReferences!=null &&
              java.util.Arrays.equals(this.locationReferences, other.getLocationReferences())));
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
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getGeometry() != null) {
            _hashCode += getGeometry().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getGridLocation() != null) {
            _hashCode += getGridLocation().hashCode();
        }
        if (getLocationComment() != null) {
            _hashCode += getLocationComment().hashCode();
        }
        if (getLocationReferences() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLocationReferences());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLocationReferences(), i);
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
        new org.apache.axis.description.TypeDesc(WorkLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("geometry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "geometry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "geometry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("locationComment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationComment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locationReferences");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationReferences"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationReference"));
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
