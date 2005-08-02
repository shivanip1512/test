/**
 * TOUReading.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class TOUReading  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.math.BigInteger ratePeriod;
    private java.math.BigInteger kWh;
    private java.lang.Float kW;
    private java.util.Calendar kWDateTime;

    public TOUReading() {
    }

    public TOUReading(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.math.BigInteger ratePeriod,
           java.math.BigInteger kWh,
           java.lang.Float kW,
           java.util.Calendar kWDateTime) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.ratePeriod = ratePeriod;
           this.kWh = kWh;
           this.kW = kW;
           this.kWDateTime = kWDateTime;
    }


    /**
     * Gets the extensions value for this TOUReading.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this TOUReading.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this TOUReading.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this TOUReading.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the ratePeriod value for this TOUReading.
     * 
     * @return ratePeriod
     */
    public java.math.BigInteger getRatePeriod() {
        return ratePeriod;
    }


    /**
     * Sets the ratePeriod value for this TOUReading.
     * 
     * @param ratePeriod
     */
    public void setRatePeriod(java.math.BigInteger ratePeriod) {
        this.ratePeriod = ratePeriod;
    }


    /**
     * Gets the kWh value for this TOUReading.
     * 
     * @return kWh
     */
    public java.math.BigInteger getKWh() {
        return kWh;
    }


    /**
     * Sets the kWh value for this TOUReading.
     * 
     * @param kWh
     */
    public void setKWh(java.math.BigInteger kWh) {
        this.kWh = kWh;
    }


    /**
     * Gets the kW value for this TOUReading.
     * 
     * @return kW
     */
    public java.lang.Float getKW() {
        return kW;
    }


    /**
     * Sets the kW value for this TOUReading.
     * 
     * @param kW
     */
    public void setKW(java.lang.Float kW) {
        this.kW = kW;
    }


    /**
     * Gets the kWDateTime value for this TOUReading.
     * 
     * @return kWDateTime
     */
    public java.util.Calendar getKWDateTime() {
        return kWDateTime;
    }


    /**
     * Sets the kWDateTime value for this TOUReading.
     * 
     * @param kWDateTime
     */
    public void setKWDateTime(java.util.Calendar kWDateTime) {
        this.kWDateTime = kWDateTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TOUReading)) return false;
        TOUReading other = (TOUReading) obj;
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
            ((this.ratePeriod==null && other.getRatePeriod()==null) || 
             (this.ratePeriod!=null &&
              this.ratePeriod.equals(other.getRatePeriod()))) &&
            ((this.kWh==null && other.getKWh()==null) || 
             (this.kWh!=null &&
              this.kWh.equals(other.getKWh()))) &&
            ((this.kW==null && other.getKW()==null) || 
             (this.kW!=null &&
              this.kW.equals(other.getKW()))) &&
            ((this.kWDateTime==null && other.getKWDateTime()==null) || 
             (this.kWDateTime!=null &&
              this.kWDateTime.equals(other.getKWDateTime())));
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
        if (getRatePeriod() != null) {
            _hashCode += getRatePeriod().hashCode();
        }
        if (getKWh() != null) {
            _hashCode += getKWh().hashCode();
        }
        if (getKW() != null) {
            _hashCode += getKW().hashCode();
        }
        if (getKWDateTime() != null) {
            _hashCode += getKWDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TOUReading.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading"));
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
        elemField.setFieldName("ratePeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ratePeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KW");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kW"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWDateTime"));
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
