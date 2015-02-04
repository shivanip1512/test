/**
 * ChargeableDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ChargeableDevice  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String chargeableDeviceType;

    private java.lang.String deviceDescription;

    private java.math.BigInteger quantity;

    private com.cannontech.multispeak.deploy.service.ChargeableDeviceActionFlag actionFlag;

    public ChargeableDevice() {
    }

    public ChargeableDevice(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String chargeableDeviceType,
           java.lang.String deviceDescription,
           java.math.BigInteger quantity,
           com.cannontech.multispeak.deploy.service.ChargeableDeviceActionFlag actionFlag) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.chargeableDeviceType = chargeableDeviceType;
        this.deviceDescription = deviceDescription;
        this.quantity = quantity;
        this.actionFlag = actionFlag;
    }


    /**
     * Gets the chargeableDeviceType value for this ChargeableDevice.
     * 
     * @return chargeableDeviceType
     */
    public java.lang.String getChargeableDeviceType() {
        return chargeableDeviceType;
    }


    /**
     * Sets the chargeableDeviceType value for this ChargeableDevice.
     * 
     * @param chargeableDeviceType
     */
    public void setChargeableDeviceType(java.lang.String chargeableDeviceType) {
        this.chargeableDeviceType = chargeableDeviceType;
    }


    /**
     * Gets the deviceDescription value for this ChargeableDevice.
     * 
     * @return deviceDescription
     */
    public java.lang.String getDeviceDescription() {
        return deviceDescription;
    }


    /**
     * Sets the deviceDescription value for this ChargeableDevice.
     * 
     * @param deviceDescription
     */
    public void setDeviceDescription(java.lang.String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }


    /**
     * Gets the quantity value for this ChargeableDevice.
     * 
     * @return quantity
     */
    public java.math.BigInteger getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this ChargeableDevice.
     * 
     * @param quantity
     */
    public void setQuantity(java.math.BigInteger quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the actionFlag value for this ChargeableDevice.
     * 
     * @return actionFlag
     */
    public com.cannontech.multispeak.deploy.service.ChargeableDeviceActionFlag getActionFlag() {
        return actionFlag;
    }


    /**
     * Sets the actionFlag value for this ChargeableDevice.
     * 
     * @param actionFlag
     */
    public void setActionFlag(com.cannontech.multispeak.deploy.service.ChargeableDeviceActionFlag actionFlag) {
        this.actionFlag = actionFlag;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChargeableDevice)) return false;
        ChargeableDevice other = (ChargeableDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.chargeableDeviceType==null && other.getChargeableDeviceType()==null) || 
             (this.chargeableDeviceType!=null &&
              this.chargeableDeviceType.equals(other.getChargeableDeviceType()))) &&
            ((this.deviceDescription==null && other.getDeviceDescription()==null) || 
             (this.deviceDescription!=null &&
              this.deviceDescription.equals(other.getDeviceDescription()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.actionFlag==null && other.getActionFlag()==null) || 
             (this.actionFlag!=null &&
              this.actionFlag.equals(other.getActionFlag())));
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
        if (getChargeableDeviceType() != null) {
            _hashCode += getChargeableDeviceType().hashCode();
        }
        if (getDeviceDescription() != null) {
            _hashCode += getDeviceDescription().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getActionFlag() != null) {
            _hashCode += getActionFlag().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChargeableDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeableDeviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "chargeableDeviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionFlag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionFlag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">chargeableDevice>actionFlag"));
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
