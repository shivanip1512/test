/**
 * SecurityLight.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SecurityLight  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private java.lang.String lightType;

    private java.lang.Long wattage;

    private java.lang.Float brightness;

    private java.lang.String sequence;

    private java.lang.String status;

    private java.lang.String actionTaken;

    private java.lang.String transformerBankID;

    private java.lang.String description;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public SecurityLight() {
    }

    public SecurityLight(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           java.lang.String lightType,
           java.lang.Long wattage,
           java.lang.Float brightness,
           java.lang.String sequence,
           java.lang.String status,
           java.lang.String actionTaken,
           java.lang.String transformerBankID,
           java.lang.String description,
           com.cannontech.multispeak.deploy.service.GPS GPS) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.lightType = lightType;
        this.wattage = wattage;
        this.brightness = brightness;
        this.sequence = sequence;
        this.status = status;
        this.actionTaken = actionTaken;
        this.transformerBankID = transformerBankID;
        this.description = description;
        this.GPS = GPS;
    }


    /**
     * Gets the lightType value for this SecurityLight.
     * 
     * @return lightType
     */
    public java.lang.String getLightType() {
        return lightType;
    }


    /**
     * Sets the lightType value for this SecurityLight.
     * 
     * @param lightType
     */
    public void setLightType(java.lang.String lightType) {
        this.lightType = lightType;
    }


    /**
     * Gets the wattage value for this SecurityLight.
     * 
     * @return wattage
     */
    public java.lang.Long getWattage() {
        return wattage;
    }


    /**
     * Sets the wattage value for this SecurityLight.
     * 
     * @param wattage
     */
    public void setWattage(java.lang.Long wattage) {
        this.wattage = wattage;
    }


    /**
     * Gets the brightness value for this SecurityLight.
     * 
     * @return brightness
     */
    public java.lang.Float getBrightness() {
        return brightness;
    }


    /**
     * Sets the brightness value for this SecurityLight.
     * 
     * @param brightness
     */
    public void setBrightness(java.lang.Float brightness) {
        this.brightness = brightness;
    }


    /**
     * Gets the sequence value for this SecurityLight.
     * 
     * @return sequence
     */
    public java.lang.String getSequence() {
        return sequence;
    }


    /**
     * Sets the sequence value for this SecurityLight.
     * 
     * @param sequence
     */
    public void setSequence(java.lang.String sequence) {
        this.sequence = sequence;
    }


    /**
     * Gets the status value for this SecurityLight.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this SecurityLight.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the actionTaken value for this SecurityLight.
     * 
     * @return actionTaken
     */
    public java.lang.String getActionTaken() {
        return actionTaken;
    }


    /**
     * Sets the actionTaken value for this SecurityLight.
     * 
     * @param actionTaken
     */
    public void setActionTaken(java.lang.String actionTaken) {
        this.actionTaken = actionTaken;
    }


    /**
     * Gets the transformerBankID value for this SecurityLight.
     * 
     * @return transformerBankID
     */
    public java.lang.String getTransformerBankID() {
        return transformerBankID;
    }


    /**
     * Sets the transformerBankID value for this SecurityLight.
     * 
     * @param transformerBankID
     */
    public void setTransformerBankID(java.lang.String transformerBankID) {
        this.transformerBankID = transformerBankID;
    }


    /**
     * Gets the description value for this SecurityLight.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this SecurityLight.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the GPS value for this SecurityLight.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this SecurityLight.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SecurityLight)) return false;
        SecurityLight other = (SecurityLight) obj;
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
            ((this.sequence==null && other.getSequence()==null) || 
             (this.sequence!=null &&
              this.sequence.equals(other.getSequence()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.actionTaken==null && other.getActionTaken()==null) || 
             (this.actionTaken!=null &&
              this.actionTaken.equals(other.getActionTaken()))) &&
            ((this.transformerBankID==null && other.getTransformerBankID()==null) || 
             (this.transformerBankID!=null &&
              this.transformerBankID.equals(other.getTransformerBankID()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.GPS==null && other.getGPS()==null) || 
             (this.GPS!=null &&
              this.GPS.equals(other.getGPS())));
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
        if (getSequence() != null) {
            _hashCode += getSequence().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getActionTaken() != null) {
            _hashCode += getActionTaken().hashCode();
        }
        if (getTransformerBankID() != null) {
            _hashCode += getTransformerBankID().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SecurityLight.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "securityLight"));
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
        elemField.setFieldName("sequence");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sequence"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionTaken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GPS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
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
