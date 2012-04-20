/**
 * UgSecondaryLine.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class UgSecondaryLine  extends com.cannontech.multispeak.deploy.service.MspElectricLine  implements java.io.Serializable {
    private java.lang.Float operVolt;

    private com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType uGSecType;

    private java.lang.Boolean isInConduit;

    private java.lang.String manufacturer;

    private org.apache.axis.types.NonNegativeInteger condPerPhase;

    private java.lang.String lengthSrc;

    public UgSecondaryLine() {
    }

    public UgSecondaryLine(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.LineStringType complexLine,
           java.lang.String gridLocation,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.Conductor[] conductorList,
           java.lang.String condN,
           java.lang.Float condLength,
           java.lang.String constr,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load,
           java.lang.Float operVolt,
           com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType uGSecType,
           java.lang.Boolean isInConduit,
           java.lang.String manufacturer,
           org.apache.axis.types.NonNegativeInteger condPerPhase,
           java.lang.String lengthSrc) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            complexLine,
            gridLocation,
            annotationList,
            fromNodeID,
            parentSectionID,
            sectionID,
            toNodeID,
            phaseCode,
            conductorList,
            condN,
            condLength,
            constr,
            load);
        this.operVolt = operVolt;
        this.uGSecType = uGSecType;
        this.isInConduit = isInConduit;
        this.manufacturer = manufacturer;
        this.condPerPhase = condPerPhase;
        this.lengthSrc = lengthSrc;
    }


    /**
     * Gets the operVolt value for this UgSecondaryLine.
     * 
     * @return operVolt
     */
    public java.lang.Float getOperVolt() {
        return operVolt;
    }


    /**
     * Sets the operVolt value for this UgSecondaryLine.
     * 
     * @param operVolt
     */
    public void setOperVolt(java.lang.Float operVolt) {
        this.operVolt = operVolt;
    }


    /**
     * Gets the uGSecType value for this UgSecondaryLine.
     * 
     * @return uGSecType
     */
    public com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType getUGSecType() {
        return uGSecType;
    }


    /**
     * Sets the uGSecType value for this UgSecondaryLine.
     * 
     * @param uGSecType
     */
    public void setUGSecType(com.cannontech.multispeak.deploy.service.UgSecondaryLineUGSecType uGSecType) {
        this.uGSecType = uGSecType;
    }


    /**
     * Gets the isInConduit value for this UgSecondaryLine.
     * 
     * @return isInConduit
     */
    public java.lang.Boolean getIsInConduit() {
        return isInConduit;
    }


    /**
     * Sets the isInConduit value for this UgSecondaryLine.
     * 
     * @param isInConduit
     */
    public void setIsInConduit(java.lang.Boolean isInConduit) {
        this.isInConduit = isInConduit;
    }


    /**
     * Gets the manufacturer value for this UgSecondaryLine.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this UgSecondaryLine.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the condPerPhase value for this UgSecondaryLine.
     * 
     * @return condPerPhase
     */
    public org.apache.axis.types.NonNegativeInteger getCondPerPhase() {
        return condPerPhase;
    }


    /**
     * Sets the condPerPhase value for this UgSecondaryLine.
     * 
     * @param condPerPhase
     */
    public void setCondPerPhase(org.apache.axis.types.NonNegativeInteger condPerPhase) {
        this.condPerPhase = condPerPhase;
    }


    /**
     * Gets the lengthSrc value for this UgSecondaryLine.
     * 
     * @return lengthSrc
     */
    public java.lang.String getLengthSrc() {
        return lengthSrc;
    }


    /**
     * Sets the lengthSrc value for this UgSecondaryLine.
     * 
     * @param lengthSrc
     */
    public void setLengthSrc(java.lang.String lengthSrc) {
        this.lengthSrc = lengthSrc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UgSecondaryLine)) return false;
        UgSecondaryLine other = (UgSecondaryLine) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.operVolt==null && other.getOperVolt()==null) || 
             (this.operVolt!=null &&
              this.operVolt.equals(other.getOperVolt()))) &&
            ((this.uGSecType==null && other.getUGSecType()==null) || 
             (this.uGSecType!=null &&
              this.uGSecType.equals(other.getUGSecType()))) &&
            ((this.isInConduit==null && other.getIsInConduit()==null) || 
             (this.isInConduit!=null &&
              this.isInConduit.equals(other.getIsInConduit()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.condPerPhase==null && other.getCondPerPhase()==null) || 
             (this.condPerPhase!=null &&
              this.condPerPhase.equals(other.getCondPerPhase()))) &&
            ((this.lengthSrc==null && other.getLengthSrc()==null) || 
             (this.lengthSrc!=null &&
              this.lengthSrc.equals(other.getLengthSrc())));
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
        if (getOperVolt() != null) {
            _hashCode += getOperVolt().hashCode();
        }
        if (getUGSecType() != null) {
            _hashCode += getUGSecType().hashCode();
        }
        if (getIsInConduit() != null) {
            _hashCode += getIsInConduit().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getCondPerPhase() != null) {
            _hashCode += getCondPerPhase().hashCode();
        }
        if (getLengthSrc() != null) {
            _hashCode += getLengthSrc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UgSecondaryLine.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLine"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "operVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("UGSecType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uGSecType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ugSecondaryLine>uGSecType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isInConduit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isInConduit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
        elemField.setFieldName("condPerPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "condPerPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lengthSrc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthSrc"));
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
