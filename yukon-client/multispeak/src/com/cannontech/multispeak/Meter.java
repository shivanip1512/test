/**
 * Meter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Meter  extends com.cannontech.multispeak.MspDevice  implements java.io.Serializable {
    private java.lang.String meterNo;
    private java.lang.String serialNumber;
    private java.lang.String meterType;
    private java.lang.String manufacturer;
    private com.cannontech.multispeak.ArrayOfString1 sealNumberList;
    private java.lang.String AMRType;
    private com.cannontech.multispeak.Nameplate nameplate;
    private com.cannontech.multispeak.UtilityInfo utilityInfo;

    public Meter() {
    }

    public Meter(
           java.lang.String meterNo,
           java.lang.String serialNumber,
           java.lang.String meterType,
           java.lang.String manufacturer,
           com.cannontech.multispeak.ArrayOfString1 sealNumberList,
           java.lang.String AMRType,
           com.cannontech.multispeak.Nameplate nameplate,
           com.cannontech.multispeak.UtilityInfo utilityInfo) {
           this.meterNo = meterNo;
           this.serialNumber = serialNumber;
           this.meterType = meterType;
           this.manufacturer = manufacturer;
           this.sealNumberList = sealNumberList;
           this.AMRType = AMRType;
           this.nameplate = nameplate;
           this.utilityInfo = utilityInfo;
    }


    /**
     * Gets the meterNo value for this Meter.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this Meter.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the serialNumber value for this Meter.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this Meter.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the meterType value for this Meter.
     * 
     * @return meterType
     */
    public java.lang.String getMeterType() {
        return meterType;
    }


    /**
     * Sets the meterType value for this Meter.
     * 
     * @param meterType
     */
    public void setMeterType(java.lang.String meterType) {
        this.meterType = meterType;
    }


    /**
     * Gets the manufacturer value for this Meter.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this Meter.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the sealNumberList value for this Meter.
     * 
     * @return sealNumberList
     */
    public com.cannontech.multispeak.ArrayOfString1 getSealNumberList() {
        return sealNumberList;
    }


    /**
     * Sets the sealNumberList value for this Meter.
     * 
     * @param sealNumberList
     */
    public void setSealNumberList(com.cannontech.multispeak.ArrayOfString1 sealNumberList) {
        this.sealNumberList = sealNumberList;
    }


    /**
     * Gets the AMRType value for this Meter.
     * 
     * @return AMRType
     */
    public java.lang.String getAMRType() {
        return AMRType;
    }


    /**
     * Sets the AMRType value for this Meter.
     * 
     * @param AMRType
     */
    public void setAMRType(java.lang.String AMRType) {
        this.AMRType = AMRType;
    }


    /**
     * Gets the nameplate value for this Meter.
     * 
     * @return nameplate
     */
    public com.cannontech.multispeak.Nameplate getNameplate() {
        return nameplate;
    }


    /**
     * Sets the nameplate value for this Meter.
     * 
     * @param nameplate
     */
    public void setNameplate(com.cannontech.multispeak.Nameplate nameplate) {
        this.nameplate = nameplate;
    }


    /**
     * Gets the utilityInfo value for this Meter.
     * 
     * @return utilityInfo
     */
    public com.cannontech.multispeak.UtilityInfo getUtilityInfo() {
        return utilityInfo;
    }


    /**
     * Sets the utilityInfo value for this Meter.
     * 
     * @param utilityInfo
     */
    public void setUtilityInfo(com.cannontech.multispeak.UtilityInfo utilityInfo) {
        this.utilityInfo = utilityInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Meter)) return false;
        Meter other = (Meter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.meterType==null && other.getMeterType()==null) || 
             (this.meterType!=null &&
              this.meterType.equals(other.getMeterType()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.sealNumberList==null && other.getSealNumberList()==null) || 
             (this.sealNumberList!=null &&
              this.sealNumberList.equals(other.getSealNumberList()))) &&
            ((this.AMRType==null && other.getAMRType()==null) || 
             (this.AMRType!=null &&
              this.AMRType.equals(other.getAMRType()))) &&
            ((this.nameplate==null && other.getNameplate()==null) || 
             (this.nameplate!=null &&
              this.nameplate.equals(other.getNameplate()))) &&
            ((this.utilityInfo==null && other.getUtilityInfo()==null) || 
             (this.utilityInfo!=null &&
              this.utilityInfo.equals(other.getUtilityInfo())));
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
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getMeterType() != null) {
            _hashCode += getMeterType().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getSealNumberList() != null) {
            _hashCode += getSealNumberList().hashCode();
        }
        if (getAMRType() != null) {
            _hashCode += getAMRType().hashCode();
        }
        if (getNameplate() != null) {
            _hashCode += getNameplate().hashCode();
        }
        if (getUtilityInfo() != null) {
            _hashCode += getUtilityInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Meter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("sealNumberList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sealNumberList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString1"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AMRType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AMRType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utilityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo"));
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
