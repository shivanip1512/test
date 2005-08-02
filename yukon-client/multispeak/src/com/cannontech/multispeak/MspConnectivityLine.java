/**
 * MspConnectivityLine.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspConnectivityLine  extends com.cannontech.multispeak.MspLineObject  implements java.io.Serializable {
    private java.lang.String gridLocation;
    private com.cannontech.multispeak.GenericAnnotationFeature[] annotationList;
    private com.cannontech.multispeak.NodeIdentifier toNodeID;
    private com.cannontech.multispeak.ObjectRef parentSectionID;
    private com.cannontech.multispeak.NodeIdentifier fromNodeID;
    private java.lang.String sectionID;

    public MspConnectivityLine() {
    }

    public MspConnectivityLine(
           java.lang.String gridLocation,
           com.cannontech.multispeak.GenericAnnotationFeature[] annotationList,
           com.cannontech.multispeak.NodeIdentifier toNodeID,
           com.cannontech.multispeak.ObjectRef parentSectionID,
           com.cannontech.multispeak.NodeIdentifier fromNodeID,
           java.lang.String sectionID) {
           this.gridLocation = gridLocation;
           this.annotationList = annotationList;
           this.toNodeID = toNodeID;
           this.parentSectionID = parentSectionID;
           this.fromNodeID = fromNodeID;
           this.sectionID = sectionID;
    }


    /**
     * Gets the gridLocation value for this MspConnectivityLine.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this MspConnectivityLine.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }


    /**
     * Gets the annotationList value for this MspConnectivityLine.
     * 
     * @return annotationList
     */
    public com.cannontech.multispeak.GenericAnnotationFeature[] getAnnotationList() {
        return annotationList;
    }


    /**
     * Sets the annotationList value for this MspConnectivityLine.
     * 
     * @param annotationList
     */
    public void setAnnotationList(com.cannontech.multispeak.GenericAnnotationFeature[] annotationList) {
        this.annotationList = annotationList;
    }

    public com.cannontech.multispeak.GenericAnnotationFeature getAnnotationList(int i) {
        return this.annotationList[i];
    }

    public void setAnnotationList(int i, com.cannontech.multispeak.GenericAnnotationFeature _value) {
        this.annotationList[i] = _value;
    }


    /**
     * Gets the toNodeID value for this MspConnectivityLine.
     * 
     * @return toNodeID
     */
    public com.cannontech.multispeak.NodeIdentifier getToNodeID() {
        return toNodeID;
    }


    /**
     * Sets the toNodeID value for this MspConnectivityLine.
     * 
     * @param toNodeID
     */
    public void setToNodeID(com.cannontech.multispeak.NodeIdentifier toNodeID) {
        this.toNodeID = toNodeID;
    }


    /**
     * Gets the parentSectionID value for this MspConnectivityLine.
     * 
     * @return parentSectionID
     */
    public com.cannontech.multispeak.ObjectRef getParentSectionID() {
        return parentSectionID;
    }


    /**
     * Sets the parentSectionID value for this MspConnectivityLine.
     * 
     * @param parentSectionID
     */
    public void setParentSectionID(com.cannontech.multispeak.ObjectRef parentSectionID) {
        this.parentSectionID = parentSectionID;
    }


    /**
     * Gets the fromNodeID value for this MspConnectivityLine.
     * 
     * @return fromNodeID
     */
    public com.cannontech.multispeak.NodeIdentifier getFromNodeID() {
        return fromNodeID;
    }


    /**
     * Sets the fromNodeID value for this MspConnectivityLine.
     * 
     * @param fromNodeID
     */
    public void setFromNodeID(com.cannontech.multispeak.NodeIdentifier fromNodeID) {
        this.fromNodeID = fromNodeID;
    }


    /**
     * Gets the sectionID value for this MspConnectivityLine.
     * 
     * @return sectionID
     */
    public java.lang.String getSectionID() {
        return sectionID;
    }


    /**
     * Sets the sectionID value for this MspConnectivityLine.
     * 
     * @param sectionID
     */
    public void setSectionID(java.lang.String sectionID) {
        this.sectionID = sectionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspConnectivityLine)) return false;
        MspConnectivityLine other = (MspConnectivityLine) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.gridLocation==null && other.getGridLocation()==null) || 
             (this.gridLocation!=null &&
              this.gridLocation.equals(other.getGridLocation()))) &&
            ((this.annotationList==null && other.getAnnotationList()==null) || 
             (this.annotationList!=null &&
              java.util.Arrays.equals(this.annotationList, other.getAnnotationList()))) &&
            ((this.toNodeID==null && other.getToNodeID()==null) || 
             (this.toNodeID!=null &&
              this.toNodeID.equals(other.getToNodeID()))) &&
            ((this.parentSectionID==null && other.getParentSectionID()==null) || 
             (this.parentSectionID!=null &&
              this.parentSectionID.equals(other.getParentSectionID()))) &&
            ((this.fromNodeID==null && other.getFromNodeID()==null) || 
             (this.fromNodeID!=null &&
              this.fromNodeID.equals(other.getFromNodeID()))) &&
            ((this.sectionID==null && other.getSectionID()==null) || 
             (this.sectionID!=null &&
              this.sectionID.equals(other.getSectionID())));
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
        if (getGridLocation() != null) {
            _hashCode += getGridLocation().hashCode();
        }
        if (getAnnotationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnnotationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnnotationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getToNodeID() != null) {
            _hashCode += getToNodeID().hashCode();
        }
        if (getParentSectionID() != null) {
            _hashCode += getParentSectionID().hashCode();
        }
        if (getFromNodeID() != null) {
            _hashCode += getFromNodeID().hashCode();
        }
        if (getSectionID() != null) {
            _hashCode += getSectionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspConnectivityLine.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityLine"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gridLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("annotationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "annotationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("toNodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "toNodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentSectionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentSectionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fromNodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fromNodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionID"));
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
