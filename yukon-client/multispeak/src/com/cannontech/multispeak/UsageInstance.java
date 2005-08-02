/**
 * UsageInstance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class UsageInstance  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.util.Calendar dateTime;
    private java.math.BigInteger monthNumber;
    private java.lang.Float value;
    private com.cannontech.multispeak.Uom uom;
    private java.math.BigInteger useDays;
    private com.cannontech.multispeak.QualityDescription quality;
    private com.cannontech.multispeak.UnitPrefix unitPrefix;
    private java.lang.String rate;

    public UsageInstance() {
    }

    public UsageInstance(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.util.Calendar dateTime,
           java.math.BigInteger monthNumber,
           java.lang.Float value,
           com.cannontech.multispeak.Uom uom,
           java.math.BigInteger useDays,
           com.cannontech.multispeak.QualityDescription quality,
           com.cannontech.multispeak.UnitPrefix unitPrefix,
           java.lang.String rate) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.dateTime = dateTime;
           this.monthNumber = monthNumber;
           this.value = value;
           this.uom = uom;
           this.useDays = useDays;
           this.quality = quality;
           this.unitPrefix = unitPrefix;
           this.rate = rate;
    }


    /**
     * Gets the extensions value for this UsageInstance.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this UsageInstance.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this UsageInstance.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this UsageInstance.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the dateTime value for this UsageInstance.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this UsageInstance.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the monthNumber value for this UsageInstance.
     * 
     * @return monthNumber
     */
    public java.math.BigInteger getMonthNumber() {
        return monthNumber;
    }


    /**
     * Sets the monthNumber value for this UsageInstance.
     * 
     * @param monthNumber
     */
    public void setMonthNumber(java.math.BigInteger monthNumber) {
        this.monthNumber = monthNumber;
    }


    /**
     * Gets the value value for this UsageInstance.
     * 
     * @return value
     */
    public java.lang.Float getValue() {
        return value;
    }


    /**
     * Sets the value value for this UsageInstance.
     * 
     * @param value
     */
    public void setValue(java.lang.Float value) {
        this.value = value;
    }


    /**
     * Gets the uom value for this UsageInstance.
     * 
     * @return uom
     */
    public com.cannontech.multispeak.Uom getUom() {
        return uom;
    }


    /**
     * Sets the uom value for this UsageInstance.
     * 
     * @param uom
     */
    public void setUom(com.cannontech.multispeak.Uom uom) {
        this.uom = uom;
    }


    /**
     * Gets the useDays value for this UsageInstance.
     * 
     * @return useDays
     */
    public java.math.BigInteger getUseDays() {
        return useDays;
    }


    /**
     * Sets the useDays value for this UsageInstance.
     * 
     * @param useDays
     */
    public void setUseDays(java.math.BigInteger useDays) {
        this.useDays = useDays;
    }


    /**
     * Gets the quality value for this UsageInstance.
     * 
     * @return quality
     */
    public com.cannontech.multispeak.QualityDescription getQuality() {
        return quality;
    }


    /**
     * Sets the quality value for this UsageInstance.
     * 
     * @param quality
     */
    public void setQuality(com.cannontech.multispeak.QualityDescription quality) {
        this.quality = quality;
    }


    /**
     * Gets the unitPrefix value for this UsageInstance.
     * 
     * @return unitPrefix
     */
    public com.cannontech.multispeak.UnitPrefix getUnitPrefix() {
        return unitPrefix;
    }


    /**
     * Sets the unitPrefix value for this UsageInstance.
     * 
     * @param unitPrefix
     */
    public void setUnitPrefix(com.cannontech.multispeak.UnitPrefix unitPrefix) {
        this.unitPrefix = unitPrefix;
    }


    /**
     * Gets the rate value for this UsageInstance.
     * 
     * @return rate
     */
    public java.lang.String getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this UsageInstance.
     * 
     * @param rate
     */
    public void setRate(java.lang.String rate) {
        this.rate = rate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UsageInstance)) return false;
        UsageInstance other = (UsageInstance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.extensionsList==null && other.getExtensionsList()==null) || 
             (this.extensionsList!=null &&
              this.extensionsList.equals(other.getExtensionsList()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.monthNumber==null && other.getMonthNumber()==null) || 
             (this.monthNumber!=null &&
              this.monthNumber.equals(other.getMonthNumber()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.uom==null && other.getUom()==null) || 
             (this.uom!=null &&
              this.uom.equals(other.getUom()))) &&
            ((this.useDays==null && other.getUseDays()==null) || 
             (this.useDays!=null &&
              this.useDays.equals(other.getUseDays()))) &&
            ((this.quality==null && other.getQuality()==null) || 
             (this.quality!=null &&
              this.quality.equals(other.getQuality()))) &&
            ((this.unitPrefix==null && other.getUnitPrefix()==null) || 
             (this.unitPrefix!=null &&
              this.unitPrefix.equals(other.getUnitPrefix()))) &&
            ((this.rate==null && other.getRate()==null) || 
             (this.rate!=null &&
              this.rate.equals(other.getRate())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getExtensionsList() != null) {
            _hashCode += getExtensionsList().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getMonthNumber() != null) {
            _hashCode += getMonthNumber().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getUom() != null) {
            _hashCode += getUom().hashCode();
        }
        if (getUseDays() != null) {
            _hashCode += getUseDays().hashCode();
        }
        if (getQuality() != null) {
            _hashCode += getQuality().hashCode();
        }
        if (getUnitPrefix() != null) {
            _hashCode += getUnitPrefix().hashCode();
        }
        if (getRate() != null) {
            _hashCode += getRate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UsageInstance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monthNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "monthNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("useDays");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "useDays"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitPrefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "rate"));
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
