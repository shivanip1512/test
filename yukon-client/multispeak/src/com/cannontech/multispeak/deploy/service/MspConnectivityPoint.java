/**
 * MspConnectivityPoint.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public abstract class MspConnectivityPoint  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol;

    private com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList;

    private com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID;

    private java.lang.String sectionID;

    private com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID;

    private com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID;

    public MspConnectivityPoint() {
    }

    public MspConnectivityPoint(
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
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID) {
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
        this.graphicSymbol = graphicSymbol;
        this.annotationList = annotationList;
        this.fromNodeID = fromNodeID;
        this.sectionID = sectionID;
        this.toNodeID = toNodeID;
        this.parentSectionID = parentSectionID;
    }


    /**
     * Gets the graphicSymbol value for this MspConnectivityPoint.
     * 
     * @return graphicSymbol
     */
    public com.cannontech.multispeak.deploy.service.GraphicSymbol[] getGraphicSymbol() {
        return graphicSymbol;
    }


    /**
     * Sets the graphicSymbol value for this MspConnectivityPoint.
     * 
     * @param graphicSymbol
     */
    public void setGraphicSymbol(com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol) {
        this.graphicSymbol = graphicSymbol;
    }

    public com.cannontech.multispeak.deploy.service.GraphicSymbol getGraphicSymbol(int i) {
        return this.graphicSymbol[i];
    }

    public void setGraphicSymbol(int i, com.cannontech.multispeak.deploy.service.GraphicSymbol _value) {
        this.graphicSymbol[i] = _value;
    }


    /**
     * Gets the annotationList value for this MspConnectivityPoint.
     * 
     * @return annotationList
     */
    public com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] getAnnotationList() {
        return annotationList;
    }


    /**
     * Sets the annotationList value for this MspConnectivityPoint.
     * 
     * @param annotationList
     */
    public void setAnnotationList(com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList) {
        this.annotationList = annotationList;
    }

    public com.cannontech.multispeak.deploy.service.GenericAnnotationFeature getAnnotationList(int i) {
        return this.annotationList[i];
    }

    public void setAnnotationList(int i, com.cannontech.multispeak.deploy.service.GenericAnnotationFeature _value) {
        this.annotationList[i] = _value;
    }


    /**
     * Gets the fromNodeID value for this MspConnectivityPoint.
     * 
     * @return fromNodeID
     */
    public com.cannontech.multispeak.deploy.service.NodeIdentifier getFromNodeID() {
        return fromNodeID;
    }


    /**
     * Sets the fromNodeID value for this MspConnectivityPoint.
     * 
     * @param fromNodeID
     */
    public void setFromNodeID(com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID) {
        this.fromNodeID = fromNodeID;
    }


    /**
     * Gets the sectionID value for this MspConnectivityPoint.
     * 
     * @return sectionID
     */
    public java.lang.String getSectionID() {
        return sectionID;
    }


    /**
     * Sets the sectionID value for this MspConnectivityPoint.
     * 
     * @param sectionID
     */
    public void setSectionID(java.lang.String sectionID) {
        this.sectionID = sectionID;
    }


    /**
     * Gets the toNodeID value for this MspConnectivityPoint.
     * 
     * @return toNodeID
     */
    public com.cannontech.multispeak.deploy.service.NodeIdentifier getToNodeID() {
        return toNodeID;
    }


    /**
     * Sets the toNodeID value for this MspConnectivityPoint.
     * 
     * @param toNodeID
     */
    public void setToNodeID(com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID) {
        this.toNodeID = toNodeID;
    }


    /**
     * Gets the parentSectionID value for this MspConnectivityPoint.
     * 
     * @return parentSectionID
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getParentSectionID() {
        return parentSectionID;
    }


    /**
     * Sets the parentSectionID value for this MspConnectivityPoint.
     * 
     * @param parentSectionID
     */
    public void setParentSectionID(com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID) {
        this.parentSectionID = parentSectionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspConnectivityPoint)) return false;
        MspConnectivityPoint other = (MspConnectivityPoint) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.graphicSymbol==null && other.getGraphicSymbol()==null) || 
             (this.graphicSymbol!=null &&
              java.util.Arrays.equals(this.graphicSymbol, other.getGraphicSymbol()))) &&
            ((this.annotationList==null && other.getAnnotationList()==null) || 
             (this.annotationList!=null &&
              java.util.Arrays.equals(this.annotationList, other.getAnnotationList()))) &&
            ((this.fromNodeID==null && other.getFromNodeID()==null) || 
             (this.fromNodeID!=null &&
              this.fromNodeID.equals(other.getFromNodeID()))) &&
            ((this.sectionID==null && other.getSectionID()==null) || 
             (this.sectionID!=null &&
              this.sectionID.equals(other.getSectionID()))) &&
            ((this.toNodeID==null && other.getToNodeID()==null) || 
             (this.toNodeID!=null &&
              this.toNodeID.equals(other.getToNodeID()))) &&
            ((this.parentSectionID==null && other.getParentSectionID()==null) || 
             (this.parentSectionID!=null &&
              this.parentSectionID.equals(other.getParentSectionID())));
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
        if (getGraphicSymbol() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGraphicSymbol());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGraphicSymbol(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        if (getFromNodeID() != null) {
            _hashCode += getFromNodeID().hashCode();
        }
        if (getSectionID() != null) {
            _hashCode += getSectionID().hashCode();
        }
        if (getToNodeID() != null) {
            _hashCode += getToNodeID().hashCode();
        }
        if (getParentSectionID() != null) {
            _hashCode += getParentSectionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspConnectivityPoint.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspConnectivityPoint"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("graphicSymbol");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
