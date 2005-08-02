/**
 * StreetLight.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class StreetLight  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.String lightType;
    private java.lang.Long wattage;
    private java.lang.Float brightness;
    private java.lang.String transformerBankID;

    public StreetLight() {
    }

    public StreetLight(
           java.lang.String lightType,
           java.lang.Long wattage,
           java.lang.Float brightness,
           java.lang.String transformerBankID) {
           this.lightType = lightType;
           this.wattage = wattage;
           this.brightness = brightness;
           this.transformerBankID = transformerBankID;
    }


    /**
     * Gets the lightType value for this StreetLight.
     * 
     * @return lightType
     */
    public java.lang.String getLightType() {
        return lightType;
    }


    /**
     * Sets the lightType value for this StreetLight.
     * 
     * @param lightType
     */
    public void setLightType(java.lang.String lightType) {
        this.lightType = lightType;
    }


    /**
     * Gets the wattage value for this StreetLight.
     * 
     * @return wattage
     */
    public java.lang.Long getWattage() {
        return wattage;
    }


    /**
     * Sets the wattage value for this StreetLight.
     * 
     * @param wattage
     */
    public void setWattage(java.lang.Long wattage) {
        this.wattage = wattage;
    }


    /**
     * Gets the brightness value for this StreetLight.
     * 
     * @return brightness
     */
    public java.lang.Float getBrightness() {
        return brightness;
    }


    /**
     * Sets the brightness value for this StreetLight.
     * 
     * @param brightness
     */
    public void setBrightness(java.lang.Float brightness) {
        this.brightness = brightness;
    }


    /**
     * Gets the transformerBankID value for this StreetLight.
     * 
     * @return transformerBankID
     */
    public java.lang.String getTransformerBankID() {
        return transformerBankID;
    }


    /**
     * Sets the transformerBankID value for this StreetLight.
     * 
     * @param transformerBankID
     */
    public void setTransformerBankID(java.lang.String transformerBankID) {
        this.transformerBankID = transformerBankID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StreetLight)) return false;
        StreetLight other = (StreetLight) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.lightType==null && other.getLightType()==null) || 
             (this.lightType!=null &&
              this.lightType.equals(other.getLightType()))) &&
            ((this.wattage==null && other.getWattage()==null) || 
             (this.wattage!=null &&
              this.wattage.equals(other.getWattage()))) &&
            ((this.brightness==null && other.getBrightness()==null) || 
             (this.brightness!=null &&
              this.brightness.equals(other.getBrightness()))) &&
            ((this.transformerBankID==null && other.getTransformerBankID()==null) || 
             (this.transformerBankID!=null &&
              this.transformerBankID.equals(other.getTransformerBankID())));
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
        if (getLightType() != null) {
            _hashCode += getLightType().hashCode();
        }
        if (getWattage() != null) {
            _hashCode += getWattage().hashCode();
        }
        if (getBrightness() != null) {
            _hashCode += getBrightness().hashCode();
        }
        if (getTransformerBankID() != null) {
            _hashCode += getTransformerBankID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StreetLight.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lightType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lightType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wattage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wattage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("brightness");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "brightness"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformerBankID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBankID"));
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
