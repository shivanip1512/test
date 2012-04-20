/**
 * AssessmentLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AssessmentLocation  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Address address;

    private java.lang.String locationOffset;

    private com.cannontech.multispeak.deploy.service.Assessment[] assessmentList;

    public AssessmentLocation() {
    }

    public AssessmentLocation(
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
           com.cannontech.multispeak.deploy.service.Address address,
           java.lang.String locationOffset,
           com.cannontech.multispeak.deploy.service.Assessment[] assessmentList) {
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
        this.address = address;
        this.locationOffset = locationOffset;
        this.assessmentList = assessmentList;
    }


    /**
     * Gets the address value for this AssessmentLocation.
     * 
     * @return address
     */
    public com.cannontech.multispeak.deploy.service.Address getAddress() {
        return address;
    }


    /**
     * Sets the address value for this AssessmentLocation.
     * 
     * @param address
     */
    public void setAddress(com.cannontech.multispeak.deploy.service.Address address) {
        this.address = address;
    }


    /**
     * Gets the locationOffset value for this AssessmentLocation.
     * 
     * @return locationOffset
     */
    public java.lang.String getLocationOffset() {
        return locationOffset;
    }


    /**
     * Sets the locationOffset value for this AssessmentLocation.
     * 
     * @param locationOffset
     */
    public void setLocationOffset(java.lang.String locationOffset) {
        this.locationOffset = locationOffset;
    }


    /**
     * Gets the assessmentList value for this AssessmentLocation.
     * 
     * @return assessmentList
     */
    public com.cannontech.multispeak.deploy.service.Assessment[] getAssessmentList() {
        return assessmentList;
    }


    /**
     * Sets the assessmentList value for this AssessmentLocation.
     * 
     * @param assessmentList
     */
    public void setAssessmentList(com.cannontech.multispeak.deploy.service.Assessment[] assessmentList) {
        this.assessmentList = assessmentList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AssessmentLocation)) return false;
        AssessmentLocation other = (AssessmentLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.locationOffset==null && other.getLocationOffset()==null) || 
             (this.locationOffset!=null &&
              this.locationOffset.equals(other.getLocationOffset()))) &&
            ((this.assessmentList==null && other.getAssessmentList()==null) || 
             (this.assessmentList!=null &&
              java.util.Arrays.equals(this.assessmentList, other.getAssessmentList())));
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
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getLocationOffset() != null) {
            _hashCode += getLocationOffset().hashCode();
        }
        if (getAssessmentList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAssessmentList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssessmentList(), i);
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
        new org.apache.axis.description.TypeDesc(AssessmentLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locationOffset");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationOffset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assessmentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessmentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment"));
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
