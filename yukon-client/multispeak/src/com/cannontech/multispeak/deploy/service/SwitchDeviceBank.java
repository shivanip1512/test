/**
 * SwitchDeviceBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SwitchDeviceBank  extends com.cannontech.multispeak.deploy.service.MspSwitchingBank  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MspSwitchDeviceList mspSwitchDeviceList;

    public SwitchDeviceBank() {
    }

    public SwitchDeviceBank(
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
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load,
           java.lang.Boolean isGanged,
           com.cannontech.multispeak.deploy.service.ObjectRef partner,
           java.lang.Long ldPoint,
           com.cannontech.multispeak.deploy.service.MspSwitchDeviceList mspSwitchDeviceList) {
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
            toNodeID,
            fromNodeID,
            parentSectionID,
            sectionID,
            phaseCode,
            load,
            isGanged,
            partner,
            ldPoint);
        this.mspSwitchDeviceList = mspSwitchDeviceList;
    }


    /**
     * Gets the mspSwitchDeviceList value for this SwitchDeviceBank.
     * 
     * @return mspSwitchDeviceList
     */
    public com.cannontech.multispeak.deploy.service.MspSwitchDeviceList getMspSwitchDeviceList() {
        return mspSwitchDeviceList;
    }


    /**
     * Sets the mspSwitchDeviceList value for this SwitchDeviceBank.
     * 
     * @param mspSwitchDeviceList
     */
    public void setMspSwitchDeviceList(com.cannontech.multispeak.deploy.service.MspSwitchDeviceList mspSwitchDeviceList) {
        this.mspSwitchDeviceList = mspSwitchDeviceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SwitchDeviceBank)) return false;
        SwitchDeviceBank other = (SwitchDeviceBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.mspSwitchDeviceList==null && other.getMspSwitchDeviceList()==null) || 
             (this.mspSwitchDeviceList!=null &&
              this.mspSwitchDeviceList.equals(other.getMspSwitchDeviceList())));
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
        if (getMspSwitchDeviceList() != null) {
            _hashCode += getMspSwitchDeviceList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SwitchDeviceBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspSwitchDeviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList"));
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
