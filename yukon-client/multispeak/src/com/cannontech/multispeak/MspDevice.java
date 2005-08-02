/**
 * MspDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspDevice  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String deviceClass;
    private java.util.Calendar inServiceDate;
    private java.util.Calendar outServiceDate;
    private java.lang.String facilityID;

    public MspDevice() {
    }

    public MspDevice(
           java.lang.String deviceClass,
           java.util.Calendar inServiceDate,
           java.util.Calendar outServiceDate,
           java.lang.String facilityID) {
           this.deviceClass = deviceClass;
           this.inServiceDate = inServiceDate;
           this.outServiceDate = outServiceDate;
           this.facilityID = facilityID;
    }


    /**
     * Gets the deviceClass value for this MspDevice.
     * 
     * @return deviceClass
     */
    public java.lang.String getDeviceClass() {
        return deviceClass;
    }


    /**
     * Sets the deviceClass value for this MspDevice.
     * 
     * @param deviceClass
     */
    public void setDeviceClass(java.lang.String deviceClass) {
        this.deviceClass = deviceClass;
    }


    /**
     * Gets the inServiceDate value for this MspDevice.
     * 
     * @return inServiceDate
     */
    public java.util.Calendar getInServiceDate() {
        return inServiceDate;
    }


    /**
     * Sets the inServiceDate value for this MspDevice.
     * 
     * @param inServiceDate
     */
    public void setInServiceDate(java.util.Calendar inServiceDate) {
        this.inServiceDate = inServiceDate;
    }


    /**
     * Gets the outServiceDate value for this MspDevice.
     * 
     * @return outServiceDate
     */
    public java.util.Calendar getOutServiceDate() {
        return outServiceDate;
    }


    /**
     * Sets the outServiceDate value for this MspDevice.
     * 
     * @param outServiceDate
     */
    public void setOutServiceDate(java.util.Calendar outServiceDate) {
        this.outServiceDate = outServiceDate;
    }


    /**
     * Gets the facilityID value for this MspDevice.
     * 
     * @return facilityID
     */
    public java.lang.String getFacilityID() {
        return facilityID;
    }


    /**
     * Sets the facilityID value for this MspDevice.
     * 
     * @param facilityID
     */
    public void setFacilityID(java.lang.String facilityID) {
        this.facilityID = facilityID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspDevice)) return false;
        MspDevice other = (MspDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.deviceClass==null && other.getDeviceClass()==null) || 
             (this.deviceClass!=null &&
              this.deviceClass.equals(other.getDeviceClass()))) &&
            ((this.inServiceDate==null && other.getInServiceDate()==null) || 
             (this.inServiceDate!=null &&
              this.inServiceDate.equals(other.getInServiceDate()))) &&
            ((this.outServiceDate==null && other.getOutServiceDate()==null) || 
             (this.outServiceDate!=null &&
              this.outServiceDate.equals(other.getOutServiceDate()))) &&
            ((this.facilityID==null && other.getFacilityID()==null) || 
             (this.facilityID!=null &&
              this.facilityID.equals(other.getFacilityID())));
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
        if (getDeviceClass() != null) {
            _hashCode += getDeviceClass().hashCode();
        }
        if (getInServiceDate() != null) {
            _hashCode += getInServiceDate().hashCode();
        }
        if (getOutServiceDate() != null) {
            _hashCode += getOutServiceDate().hashCode();
        }
        if (getFacilityID() != null) {
            _hashCode += getFacilityID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inServiceDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inServiceDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outServiceDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outServiceDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facilityID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"));
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
