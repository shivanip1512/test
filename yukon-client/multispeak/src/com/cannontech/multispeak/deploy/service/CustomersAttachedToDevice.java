/**
 * CustomersAttachedToDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CustomersAttachedToDevice  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String deviceID;

    private java.lang.String deviceDescription;

    private com.cannontech.multispeak.deploy.service.AffectedMeter[] affectedMeters;

    public CustomersAttachedToDevice() {
    }

    public CustomersAttachedToDevice(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String deviceID,
           java.lang.String deviceDescription,
           com.cannontech.multispeak.deploy.service.AffectedMeter[] affectedMeters) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.deviceID = deviceID;
        this.deviceDescription = deviceDescription;
        this.affectedMeters = affectedMeters;
    }


    /**
     * Gets the deviceID value for this CustomersAttachedToDevice.
     * 
     * @return deviceID
     */
    public java.lang.String getDeviceID() {
        return deviceID;
    }


    /**
     * Sets the deviceID value for this CustomersAttachedToDevice.
     * 
     * @param deviceID
     */
    public void setDeviceID(java.lang.String deviceID) {
        this.deviceID = deviceID;
    }


    /**
     * Gets the deviceDescription value for this CustomersAttachedToDevice.
     * 
     * @return deviceDescription
     */
    public java.lang.String getDeviceDescription() {
        return deviceDescription;
    }


    /**
     * Sets the deviceDescription value for this CustomersAttachedToDevice.
     * 
     * @param deviceDescription
     */
    public void setDeviceDescription(java.lang.String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }


    /**
     * Gets the affectedMeters value for this CustomersAttachedToDevice.
     * 
     * @return affectedMeters
     */
    public com.cannontech.multispeak.deploy.service.AffectedMeter[] getAffectedMeters() {
        return affectedMeters;
    }


    /**
     * Sets the affectedMeters value for this CustomersAttachedToDevice.
     * 
     * @param affectedMeters
     */
    public void setAffectedMeters(com.cannontech.multispeak.deploy.service.AffectedMeter[] affectedMeters) {
        this.affectedMeters = affectedMeters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomersAttachedToDevice)) return false;
        CustomersAttachedToDevice other = (CustomersAttachedToDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.deviceID==null && other.getDeviceID()==null) || 
             (this.deviceID!=null &&
              this.deviceID.equals(other.getDeviceID()))) &&
            ((this.deviceDescription==null && other.getDeviceDescription()==null) || 
             (this.deviceDescription!=null &&
              this.deviceDescription.equals(other.getDeviceDescription()))) &&
            ((this.affectedMeters==null && other.getAffectedMeters()==null) || 
             (this.affectedMeters!=null &&
              java.util.Arrays.equals(this.affectedMeters, other.getAffectedMeters())));
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
        if (getDeviceID() != null) {
            _hashCode += getDeviceID().hashCode();
        }
        if (getDeviceDescription() != null) {
            _hashCode += getDeviceDescription().hashCode();
        }
        if (getAffectedMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAffectedMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAffectedMeters(), i);
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
        new org.apache.axis.description.TypeDesc(CustomersAttachedToDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customersAttachedToDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceID"));
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
        elemField.setFieldName("affectedMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "affectedMeter"));
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
