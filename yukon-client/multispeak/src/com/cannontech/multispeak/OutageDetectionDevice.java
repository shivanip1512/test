/**
 * OutageDetectionDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OutageDetectionDevice  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.String serialNumber;
    private com.cannontech.multispeak.OutageDetectDeviceType outageDetectDeviceType;
    private java.lang.String manufacturer;
    private java.lang.Long phases;
    private com.cannontech.multispeak.PhaseCd phaseCd;
    private java.lang.String meterNo;
    private com.cannontech.multispeak.OutageDetectDeviceStatus outageDetectDeviceStatus;

    public OutageDetectionDevice() {
    }

    public OutageDetectionDevice(
           java.lang.String serialNumber,
           com.cannontech.multispeak.OutageDetectDeviceType outageDetectDeviceType,
           java.lang.String manufacturer,
           java.lang.Long phases,
           com.cannontech.multispeak.PhaseCd phaseCd,
           java.lang.String meterNo,
           com.cannontech.multispeak.OutageDetectDeviceStatus outageDetectDeviceStatus) {
           this.serialNumber = serialNumber;
           this.outageDetectDeviceType = outageDetectDeviceType;
           this.manufacturer = manufacturer;
           this.phases = phases;
           this.phaseCd = phaseCd;
           this.meterNo = meterNo;
           this.outageDetectDeviceStatus = outageDetectDeviceStatus;
    }


    /**
     * Gets the serialNumber value for this OutageDetectionDevice.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this OutageDetectionDevice.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the outageDetectDeviceType value for this OutageDetectionDevice.
     * 
     * @return outageDetectDeviceType
     */
    public com.cannontech.multispeak.OutageDetectDeviceType getOutageDetectDeviceType() {
        return outageDetectDeviceType;
    }


    /**
     * Sets the outageDetectDeviceType value for this OutageDetectionDevice.
     * 
     * @param outageDetectDeviceType
     */
    public void setOutageDetectDeviceType(com.cannontech.multispeak.OutageDetectDeviceType outageDetectDeviceType) {
        this.outageDetectDeviceType = outageDetectDeviceType;
    }


    /**
     * Gets the manufacturer value for this OutageDetectionDevice.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this OutageDetectionDevice.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the phases value for this OutageDetectionDevice.
     * 
     * @return phases
     */
    public java.lang.Long getPhases() {
        return phases;
    }


    /**
     * Sets the phases value for this OutageDetectionDevice.
     * 
     * @param phases
     */
    public void setPhases(java.lang.Long phases) {
        this.phases = phases;
    }


    /**
     * Gets the phaseCd value for this OutageDetectionDevice.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this OutageDetectionDevice.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the meterNo value for this OutageDetectionDevice.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this OutageDetectionDevice.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the outageDetectDeviceStatus value for this OutageDetectionDevice.
     * 
     * @return outageDetectDeviceStatus
     */
    public com.cannontech.multispeak.OutageDetectDeviceStatus getOutageDetectDeviceStatus() {
        return outageDetectDeviceStatus;
    }


    /**
     * Sets the outageDetectDeviceStatus value for this OutageDetectionDevice.
     * 
     * @param outageDetectDeviceStatus
     */
    public void setOutageDetectDeviceStatus(com.cannontech.multispeak.OutageDetectDeviceStatus outageDetectDeviceStatus) {
        this.outageDetectDeviceStatus = outageDetectDeviceStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageDetectionDevice)) return false;
        OutageDetectionDevice other = (OutageDetectionDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.outageDetectDeviceType==null && other.getOutageDetectDeviceType()==null) || 
             (this.outageDetectDeviceType!=null &&
              this.outageDetectDeviceType.equals(other.getOutageDetectDeviceType()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.phases==null && other.getPhases()==null) || 
             (this.phases!=null &&
              this.phases.equals(other.getPhases()))) &&
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.outageDetectDeviceStatus==null && other.getOutageDetectDeviceStatus()==null) || 
             (this.outageDetectDeviceStatus!=null &&
              this.outageDetectDeviceStatus.equals(other.getOutageDetectDeviceStatus())));
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
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getOutageDetectDeviceType() != null) {
            _hashCode += getOutageDetectDeviceType().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getPhases() != null) {
            _hashCode += getPhases().hashCode();
        }
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getOutageDetectDeviceStatus() != null) {
            _hashCode += getOutageDetectDeviceStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageDetectionDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectDeviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manufacturer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phases");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectDeviceStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceStatus"));
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
