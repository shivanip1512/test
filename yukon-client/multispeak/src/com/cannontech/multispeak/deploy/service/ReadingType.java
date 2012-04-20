/**
 * ReadingType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingType  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ReadingTypeID readingTypeID;

    private java.lang.String measurementType;

    private java.lang.String measTypeCategory;

    private com.cannontech.multispeak.deploy.service.Uom units;

    private java.lang.Boolean forwardChronology;

    private java.lang.String defaultValueDataType;

    private java.lang.String defaultQuality;

    private java.lang.String dynamicConfiguration;

    private java.math.BigInteger channelNumber;

    public ReadingType() {
    }

    public ReadingType(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.ReadingTypeID readingTypeID,
           java.lang.String measurementType,
           java.lang.String measTypeCategory,
           com.cannontech.multispeak.deploy.service.Uom units,
           java.lang.Boolean forwardChronology,
           java.lang.String defaultValueDataType,
           java.lang.String defaultQuality,
           java.lang.String dynamicConfiguration,
           java.math.BigInteger channelNumber) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.readingTypeID = readingTypeID;
        this.measurementType = measurementType;
        this.measTypeCategory = measTypeCategory;
        this.units = units;
        this.forwardChronology = forwardChronology;
        this.defaultValueDataType = defaultValueDataType;
        this.defaultQuality = defaultQuality;
        this.dynamicConfiguration = dynamicConfiguration;
        this.channelNumber = channelNumber;
    }


    /**
     * Gets the readingTypeID value for this ReadingType.
     * 
     * @return readingTypeID
     */
    public com.cannontech.multispeak.deploy.service.ReadingTypeID getReadingTypeID() {
        return readingTypeID;
    }


    /**
     * Sets the readingTypeID value for this ReadingType.
     * 
     * @param readingTypeID
     */
    public void setReadingTypeID(com.cannontech.multispeak.deploy.service.ReadingTypeID readingTypeID) {
        this.readingTypeID = readingTypeID;
    }


    /**
     * Gets the measurementType value for this ReadingType.
     * 
     * @return measurementType
     */
    public java.lang.String getMeasurementType() {
        return measurementType;
    }


    /**
     * Sets the measurementType value for this ReadingType.
     * 
     * @param measurementType
     */
    public void setMeasurementType(java.lang.String measurementType) {
        this.measurementType = measurementType;
    }


    /**
     * Gets the measTypeCategory value for this ReadingType.
     * 
     * @return measTypeCategory
     */
    public java.lang.String getMeasTypeCategory() {
        return measTypeCategory;
    }


    /**
     * Sets the measTypeCategory value for this ReadingType.
     * 
     * @param measTypeCategory
     */
    public void setMeasTypeCategory(java.lang.String measTypeCategory) {
        this.measTypeCategory = measTypeCategory;
    }


    /**
     * Gets the units value for this ReadingType.
     * 
     * @return units
     */
    public com.cannontech.multispeak.deploy.service.Uom getUnits() {
        return units;
    }


    /**
     * Sets the units value for this ReadingType.
     * 
     * @param units
     */
    public void setUnits(com.cannontech.multispeak.deploy.service.Uom units) {
        this.units = units;
    }


    /**
     * Gets the forwardChronology value for this ReadingType.
     * 
     * @return forwardChronology
     */
    public java.lang.Boolean getForwardChronology() {
        return forwardChronology;
    }


    /**
     * Sets the forwardChronology value for this ReadingType.
     * 
     * @param forwardChronology
     */
    public void setForwardChronology(java.lang.Boolean forwardChronology) {
        this.forwardChronology = forwardChronology;
    }


    /**
     * Gets the defaultValueDataType value for this ReadingType.
     * 
     * @return defaultValueDataType
     */
    public java.lang.String getDefaultValueDataType() {
        return defaultValueDataType;
    }


    /**
     * Sets the defaultValueDataType value for this ReadingType.
     * 
     * @param defaultValueDataType
     */
    public void setDefaultValueDataType(java.lang.String defaultValueDataType) {
        this.defaultValueDataType = defaultValueDataType;
    }


    /**
     * Gets the defaultQuality value for this ReadingType.
     * 
     * @return defaultQuality
     */
    public java.lang.String getDefaultQuality() {
        return defaultQuality;
    }


    /**
     * Sets the defaultQuality value for this ReadingType.
     * 
     * @param defaultQuality
     */
    public void setDefaultQuality(java.lang.String defaultQuality) {
        this.defaultQuality = defaultQuality;
    }


    /**
     * Gets the dynamicConfiguration value for this ReadingType.
     * 
     * @return dynamicConfiguration
     */
    public java.lang.String getDynamicConfiguration() {
        return dynamicConfiguration;
    }


    /**
     * Sets the dynamicConfiguration value for this ReadingType.
     * 
     * @param dynamicConfiguration
     */
    public void setDynamicConfiguration(java.lang.String dynamicConfiguration) {
        this.dynamicConfiguration = dynamicConfiguration;
    }


    /**
     * Gets the channelNumber value for this ReadingType.
     * 
     * @return channelNumber
     */
    public java.math.BigInteger getChannelNumber() {
        return channelNumber;
    }


    /**
     * Sets the channelNumber value for this ReadingType.
     * 
     * @param channelNumber
     */
    public void setChannelNumber(java.math.BigInteger channelNumber) {
        this.channelNumber = channelNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingType)) return false;
        ReadingType other = (ReadingType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.readingTypeID==null && other.getReadingTypeID()==null) || 
             (this.readingTypeID!=null &&
              this.readingTypeID.equals(other.getReadingTypeID()))) &&
            ((this.measurementType==null && other.getMeasurementType()==null) || 
             (this.measurementType!=null &&
              this.measurementType.equals(other.getMeasurementType()))) &&
            ((this.measTypeCategory==null && other.getMeasTypeCategory()==null) || 
             (this.measTypeCategory!=null &&
              this.measTypeCategory.equals(other.getMeasTypeCategory()))) &&
            ((this.units==null && other.getUnits()==null) || 
             (this.units!=null &&
              this.units.equals(other.getUnits()))) &&
            ((this.forwardChronology==null && other.getForwardChronology()==null) || 
             (this.forwardChronology!=null &&
              this.forwardChronology.equals(other.getForwardChronology()))) &&
            ((this.defaultValueDataType==null && other.getDefaultValueDataType()==null) || 
             (this.defaultValueDataType!=null &&
              this.defaultValueDataType.equals(other.getDefaultValueDataType()))) &&
            ((this.defaultQuality==null && other.getDefaultQuality()==null) || 
             (this.defaultQuality!=null &&
              this.defaultQuality.equals(other.getDefaultQuality()))) &&
            ((this.dynamicConfiguration==null && other.getDynamicConfiguration()==null) || 
             (this.dynamicConfiguration!=null &&
              this.dynamicConfiguration.equals(other.getDynamicConfiguration()))) &&
            ((this.channelNumber==null && other.getChannelNumber()==null) || 
             (this.channelNumber!=null &&
              this.channelNumber.equals(other.getChannelNumber())));
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
        if (getReadingTypeID() != null) {
            _hashCode += getReadingTypeID().hashCode();
        }
        if (getMeasurementType() != null) {
            _hashCode += getMeasurementType().hashCode();
        }
        if (getMeasTypeCategory() != null) {
            _hashCode += getMeasTypeCategory().hashCode();
        }
        if (getUnits() != null) {
            _hashCode += getUnits().hashCode();
        }
        if (getForwardChronology() != null) {
            _hashCode += getForwardChronology().hashCode();
        }
        if (getDefaultValueDataType() != null) {
            _hashCode += getDefaultValueDataType().hashCode();
        }
        if (getDefaultQuality() != null) {
            _hashCode += getDefaultQuality().hashCode();
        }
        if (getDynamicConfiguration() != null) {
            _hashCode += getDynamicConfiguration().hashCode();
        }
        if (getChannelNumber() != null) {
            _hashCode += getChannelNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadingType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingTypeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeID"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measTypeCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measTypeCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("units");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "units"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardChronology");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "forwardChronology"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultValueDataType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "defaultValueDataType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultQuality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "defaultQuality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dynamicConfiguration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dynamicConfiguration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channelNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
