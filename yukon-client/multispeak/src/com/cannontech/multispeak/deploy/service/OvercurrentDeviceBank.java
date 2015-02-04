/**
 * OvercurrentDeviceBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OvercurrentDeviceBank  extends com.cannontech.multispeak.deploy.service.MspSwitchingBank  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MspOverCurrentDeviceList mspOverCurrentDeviceList;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public OvercurrentDeviceBank() {
    }

    public OvercurrentDeviceBank(
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
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load,
           java.lang.Boolean isGanged,
           com.cannontech.multispeak.deploy.service.ObjectRef partner,
           java.lang.Long ldPoint,
           com.cannontech.multispeak.deploy.service.MspOverCurrentDeviceList mspOverCurrentDeviceList,
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
            facilityID,
            graphicSymbol,
            annotationList,
            fromNodeID,
            sectionID,
            toNodeID,
            parentSectionID,
            phaseCode,
            load,
            isGanged,
            partner,
            ldPoint);
        this.mspOverCurrentDeviceList = mspOverCurrentDeviceList;
        this.GPS = GPS;
    }


    /**
     * Gets the mspOverCurrentDeviceList value for this OvercurrentDeviceBank.
     * 
     * @return mspOverCurrentDeviceList
     */
    public com.cannontech.multispeak.deploy.service.MspOverCurrentDeviceList getMspOverCurrentDeviceList() {
        return mspOverCurrentDeviceList;
    }


    /**
     * Sets the mspOverCurrentDeviceList value for this OvercurrentDeviceBank.
     * 
     * @param mspOverCurrentDeviceList
     */
    public void setMspOverCurrentDeviceList(com.cannontech.multispeak.deploy.service.MspOverCurrentDeviceList mspOverCurrentDeviceList) {
        this.mspOverCurrentDeviceList = mspOverCurrentDeviceList;
    }


    /**
     * Gets the GPS value for this OvercurrentDeviceBank.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this OvercurrentDeviceBank.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OvercurrentDeviceBank)) return false;
        OvercurrentDeviceBank other = (OvercurrentDeviceBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.mspOverCurrentDeviceList==null && other.getMspOverCurrentDeviceList()==null) || 
             (this.mspOverCurrentDeviceList!=null &&
              this.mspOverCurrentDeviceList.equals(other.getMspOverCurrentDeviceList()))) &&
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
        if (getMspOverCurrentDeviceList() != null) {
            _hashCode += getMspOverCurrentDeviceList().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OvercurrentDeviceBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspOverCurrentDeviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList"));
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
