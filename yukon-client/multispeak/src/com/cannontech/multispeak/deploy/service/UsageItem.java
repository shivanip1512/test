/**
 * UsageItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class UsageItem  implements java.io.Serializable {
    private java.lang.String usageType;

    private java.lang.Float multiplier;

    private java.lang.Float previousReading;

    private java.util.Calendar previousReadDate;

    private java.lang.Float presentReading;

    private java.util.Calendar presentReadingDate;

    public UsageItem() {
    }

    public UsageItem(
           java.lang.String usageType,
           java.lang.Float multiplier,
           java.lang.Float previousReading,
           java.util.Calendar previousReadDate,
           java.lang.Float presentReading,
           java.util.Calendar presentReadingDate) {
           this.usageType = usageType;
           this.multiplier = multiplier;
           this.previousReading = previousReading;
           this.previousReadDate = previousReadDate;
           this.presentReading = presentReading;
           this.presentReadingDate = presentReadingDate;
    }


    /**
     * Gets the usageType value for this UsageItem.
     * 
     * @return usageType
     */
    public java.lang.String getUsageType() {
        return usageType;
    }


    /**
     * Sets the usageType value for this UsageItem.
     * 
     * @param usageType
     */
    public void setUsageType(java.lang.String usageType) {
        this.usageType = usageType;
    }


    /**
     * Gets the multiplier value for this UsageItem.
     * 
     * @return multiplier
     */
    public java.lang.Float getMultiplier() {
        return multiplier;
    }


    /**
     * Sets the multiplier value for this UsageItem.
     * 
     * @param multiplier
     */
    public void setMultiplier(java.lang.Float multiplier) {
        this.multiplier = multiplier;
    }


    /**
     * Gets the previousReading value for this UsageItem.
     * 
     * @return previousReading
     */
    public java.lang.Float getPreviousReading() {
        return previousReading;
    }


    /**
     * Sets the previousReading value for this UsageItem.
     * 
     * @param previousReading
     */
    public void setPreviousReading(java.lang.Float previousReading) {
        this.previousReading = previousReading;
    }


    /**
     * Gets the previousReadDate value for this UsageItem.
     * 
     * @return previousReadDate
     */
    public java.util.Calendar getPreviousReadDate() {
        return previousReadDate;
    }


    /**
     * Sets the previousReadDate value for this UsageItem.
     * 
     * @param previousReadDate
     */
    public void setPreviousReadDate(java.util.Calendar previousReadDate) {
        this.previousReadDate = previousReadDate;
    }


    /**
     * Gets the presentReading value for this UsageItem.
     * 
     * @return presentReading
     */
    public java.lang.Float getPresentReading() {
        return presentReading;
    }


    /**
     * Sets the presentReading value for this UsageItem.
     * 
     * @param presentReading
     */
    public void setPresentReading(java.lang.Float presentReading) {
        this.presentReading = presentReading;
    }


    /**
     * Gets the presentReadingDate value for this UsageItem.
     * 
     * @return presentReadingDate
     */
    public java.util.Calendar getPresentReadingDate() {
        return presentReadingDate;
    }


    /**
     * Sets the presentReadingDate value for this UsageItem.
     * 
     * @param presentReadingDate
     */
    public void setPresentReadingDate(java.util.Calendar presentReadingDate) {
        this.presentReadingDate = presentReadingDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UsageItem)) return false;
        UsageItem other = (UsageItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.usageType==null && other.getUsageType()==null) || 
             (this.usageType!=null &&
              this.usageType.equals(other.getUsageType()))) &&
            ((this.multiplier==null && other.getMultiplier()==null) || 
             (this.multiplier!=null &&
              this.multiplier.equals(other.getMultiplier()))) &&
            ((this.previousReading==null && other.getPreviousReading()==null) || 
             (this.previousReading!=null &&
              this.previousReading.equals(other.getPreviousReading()))) &&
            ((this.previousReadDate==null && other.getPreviousReadDate()==null) || 
             (this.previousReadDate!=null &&
              this.previousReadDate.equals(other.getPreviousReadDate()))) &&
            ((this.presentReading==null && other.getPresentReading()==null) || 
             (this.presentReading!=null &&
              this.presentReading.equals(other.getPresentReading()))) &&
            ((this.presentReadingDate==null && other.getPresentReadingDate()==null) || 
             (this.presentReadingDate!=null &&
              this.presentReadingDate.equals(other.getPresentReadingDate())));
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
        if (getUsageType() != null) {
            _hashCode += getUsageType().hashCode();
        }
        if (getMultiplier() != null) {
            _hashCode += getMultiplier().hashCode();
        }
        if (getPreviousReading() != null) {
            _hashCode += getPreviousReading().hashCode();
        }
        if (getPreviousReadDate() != null) {
            _hashCode += getPreviousReadDate().hashCode();
        }
        if (getPresentReading() != null) {
            _hashCode += getPresentReading().hashCode();
        }
        if (getPresentReadingDate() != null) {
            _hashCode += getPresentReadingDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UsageItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usageType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiplier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "multiplier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("previousReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("previousReadDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousReadDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("presentReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "presentReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("presentReadingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "presentReadingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
