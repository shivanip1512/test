/**
 * MspElectricPoint.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public abstract class MspElectricPoint  extends com.cannontech.multispeak.deploy.service.MspConnectivityPoint  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCode;

    private com.cannontech.multispeak.deploy.service.MspLoadGroup load;

    public MspElectricPoint() {
    }

    public MspElectricPoint(
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
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
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
            facilityID,
            graphicSymbol,
            annotationList,
            fromNodeID,
            parentSectionID,
            sectionID,
            toNodeID);
        this.phaseCode = phaseCode;
        this.load = load;
    }


    /**
     * Gets the phaseCode value for this MspElectricPoint.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this MspElectricPoint.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the load value for this MspElectricPoint.
     * 
     * @return load
     */
    public com.cannontech.multispeak.deploy.service.MspLoadGroup getLoad() {
        return load;
    }


    /**
     * Sets the load value for this MspElectricPoint.
     * 
     * @param load
     */
    public void setLoad(com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
        this.load = load;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspElectricPoint)) return false;
        MspElectricPoint other = (MspElectricPoint) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode()))) &&
            ((this.load==null && other.getLoad()==null) || 
             (this.load!=null &&
              this.load.equals(other.getLoad())));
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
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        if (getLoad() != null) {
            _hashCode += getLoad().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspElectricPoint.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricPoint"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("load");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "load"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLoadGroup"));
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
