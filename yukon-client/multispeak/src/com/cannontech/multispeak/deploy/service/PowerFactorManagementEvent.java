/**
 * PowerFactorManagementEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PowerFactorManagementEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String switchID;

    private com.cannontech.multispeak.deploy.service.ControlEventType controlEventType;

    private java.lang.String affectedRelay;

    public PowerFactorManagementEvent() {
    }

    public PowerFactorManagementEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String switchID,
           com.cannontech.multispeak.deploy.service.ControlEventType controlEventType,
           java.lang.String affectedRelay) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.switchID = switchID;
        this.controlEventType = controlEventType;
        this.affectedRelay = affectedRelay;
    }


    /**
     * Gets the switchID value for this PowerFactorManagementEvent.
     * 
     * @return switchID
     */
    public java.lang.String getSwitchID() {
        return switchID;
    }


    /**
     * Sets the switchID value for this PowerFactorManagementEvent.
     * 
     * @param switchID
     */
    public void setSwitchID(java.lang.String switchID) {
        this.switchID = switchID;
    }


    /**
     * Gets the controlEventType value for this PowerFactorManagementEvent.
     * 
     * @return controlEventType
     */
    public com.cannontech.multispeak.deploy.service.ControlEventType getControlEventType() {
        return controlEventType;
    }


    /**
     * Sets the controlEventType value for this PowerFactorManagementEvent.
     * 
     * @param controlEventType
     */
    public void setControlEventType(com.cannontech.multispeak.deploy.service.ControlEventType controlEventType) {
        this.controlEventType = controlEventType;
    }


    /**
     * Gets the affectedRelay value for this PowerFactorManagementEvent.
     * 
     * @return affectedRelay
     */
    public java.lang.String getAffectedRelay() {
        return affectedRelay;
    }


    /**
     * Sets the affectedRelay value for this PowerFactorManagementEvent.
     * 
     * @param affectedRelay
     */
    public void setAffectedRelay(java.lang.String affectedRelay) {
        this.affectedRelay = affectedRelay;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PowerFactorManagementEvent)) return false;
        PowerFactorManagementEvent other = (PowerFactorManagementEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.switchID==null && other.getSwitchID()==null) || 
             (this.switchID!=null &&
              this.switchID.equals(other.getSwitchID()))) &&
            ((this.controlEventType==null && other.getControlEventType()==null) || 
             (this.controlEventType!=null &&
              this.controlEventType.equals(other.getControlEventType()))) &&
            ((this.affectedRelay==null && other.getAffectedRelay()==null) || 
             (this.affectedRelay!=null &&
              this.affectedRelay.equals(other.getAffectedRelay())));
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
        if (getSwitchID() != null) {
            _hashCode += getSwitchID().hashCode();
        }
        if (getControlEventType() != null) {
            _hashCode += getControlEventType().hashCode();
        }
        if (getAffectedRelay() != null) {
            _hashCode += getAffectedRelay().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PowerFactorManagementEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorManagementEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlEventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("affectedRelay");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedRelay"));
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
