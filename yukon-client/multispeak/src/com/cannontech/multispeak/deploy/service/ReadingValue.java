/**
 * ReadingValue.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingValue  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Extensions extensions;

    private com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList;

    private java.lang.String units;

    private java.lang.String value;

    private java.lang.String readingType;

    private com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType readingValueType;

    private java.lang.String name;

    private java.util.Calendar dateTime;

    private java.lang.String fieldName;

    public ReadingValue() {
    }

    public ReadingValue(
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String units,
           java.lang.String value,
           java.lang.String readingType,
           com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType readingValueType,
           java.lang.String name,
           java.util.Calendar dateTime,
           java.lang.String fieldName) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.units = units;
           this.value = value;
           this.readingType = readingType;
           this.readingValueType = readingValueType;
           this.name = name;
           this.dateTime = dateTime;
           this.fieldName = fieldName;
    }


    /**
     * Gets the extensions value for this ReadingValue.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.deploy.service.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this ReadingValue.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.deploy.service.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this ReadingValue.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.deploy.service.ExtensionsItem[] getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this ReadingValue.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the units value for this ReadingValue.
     * 
     * @return units
     */
    public java.lang.String getUnits() {
        return units;
    }


    /**
     * Sets the units value for this ReadingValue.
     * 
     * @param units
     */
    public void setUnits(java.lang.String units) {
        this.units = units;
    }


    /**
     * Gets the value value for this ReadingValue.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this ReadingValue.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the readingType value for this ReadingValue.
     * 
     * @return readingType
     */
    public java.lang.String getReadingType() {
        return readingType;
    }


    /**
     * Sets the readingType value for this ReadingValue.
     * 
     * @param readingType
     */
    public void setReadingType(java.lang.String readingType) {
        this.readingType = readingType;
    }


    /**
     * Gets the readingValueType value for this ReadingValue.
     * 
     * @return readingValueType
     */
    public com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType getReadingValueType() {
        return readingValueType;
    }


    /**
     * Sets the readingValueType value for this ReadingValue.
     * 
     * @param readingValueType
     */
    public void setReadingValueType(com.cannontech.multispeak.deploy.service.ReadingValueReadingValueType readingValueType) {
        this.readingValueType = readingValueType;
    }


    /**
     * Gets the name value for this ReadingValue.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ReadingValue.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the dateTime value for this ReadingValue.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this ReadingValue.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the fieldName value for this ReadingValue.
     * 
     * @return fieldName
     */
    public java.lang.String getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this ReadingValue.
     * 
     * @param fieldName
     */
    public void setFieldName(java.lang.String fieldName) {
        this.fieldName = fieldName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingValue)) return false;
        ReadingValue other = (ReadingValue) obj;
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
              java.util.Arrays.equals(this.extensionsList, other.getExtensionsList()))) &&
            ((this.units==null && other.getUnits()==null) || 
             (this.units!=null &&
              this.units.equals(other.getUnits()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.readingType==null && other.getReadingType()==null) || 
             (this.readingType!=null &&
              this.readingType.equals(other.getReadingType()))) &&
            ((this.readingValueType==null && other.getReadingValueType()==null) || 
             (this.readingValueType!=null &&
              this.readingValueType.equals(other.getReadingValueType()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.fieldName==null && other.getFieldName()==null) || 
             (this.fieldName!=null &&
              this.fieldName.equals(other.getFieldName())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExtensionsList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExtensionsList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUnits() != null) {
            _hashCode += getUnits().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getReadingType() != null) {
            _hashCode += getReadingType().hashCode();
        }
        if (getReadingValueType() != null) {
            _hashCode += getReadingValueType().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getFieldName() != null) {
            _hashCode += getFieldName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadingValue.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("units");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "units"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingValueType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValueType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingValue>readingValueType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("fieldName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fieldName"));
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
