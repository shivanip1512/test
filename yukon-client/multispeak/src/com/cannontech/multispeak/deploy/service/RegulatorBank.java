/**
 * RegulatorBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RegulatorBank  extends com.cannontech.multispeak.deploy.service.MspBankObject  implements java.io.Serializable {
    private java.lang.String regType;

    private com.cannontech.multispeak.deploy.service.MspPhase ctrlPhase;

    private java.lang.String wdgType;

    private com.cannontech.multispeak.deploy.service.Regulator[] regulatorList;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public RegulatorBank() {
    }

    public RegulatorBank(
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
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load,
           java.lang.String regType,
           com.cannontech.multispeak.deploy.service.MspPhase ctrlPhase,
           java.lang.String wdgType,
           com.cannontech.multispeak.deploy.service.Regulator[] regulatorList,
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
            facilityID,
            graphicSymbol,
            annotationList,
            fromNodeID,
            sectionID,
            toNodeID,
            parentSectionID,
            phaseCode,
            load);
        this.regType = regType;
        this.ctrlPhase = ctrlPhase;
        this.wdgType = wdgType;
        this.regulatorList = regulatorList;
        this.GPS = GPS;
    }


    /**
     * Gets the regType value for this RegulatorBank.
     * 
     * @return regType
     */
    public java.lang.String getRegType() {
        return regType;
    }


    /**
     * Sets the regType value for this RegulatorBank.
     * 
     * @param regType
     */
    public void setRegType(java.lang.String regType) {
        this.regType = regType;
    }


    /**
     * Gets the ctrlPhase value for this RegulatorBank.
     * 
     * @return ctrlPhase
     */
    public com.cannontech.multispeak.deploy.service.MspPhase getCtrlPhase() {
        return ctrlPhase;
    }


    /**
     * Sets the ctrlPhase value for this RegulatorBank.
     * 
     * @param ctrlPhase
     */
    public void setCtrlPhase(com.cannontech.multispeak.deploy.service.MspPhase ctrlPhase) {
        this.ctrlPhase = ctrlPhase;
    }


    /**
     * Gets the wdgType value for this RegulatorBank.
     * 
     * @return wdgType
     */
    public java.lang.String getWdgType() {
        return wdgType;
    }


    /**
     * Sets the wdgType value for this RegulatorBank.
     * 
     * @param wdgType
     */
    public void setWdgType(java.lang.String wdgType) {
        this.wdgType = wdgType;
    }


    /**
     * Gets the regulatorList value for this RegulatorBank.
     * 
     * @return regulatorList
     */
    public com.cannontech.multispeak.deploy.service.Regulator[] getRegulatorList() {
        return regulatorList;
    }


    /**
     * Sets the regulatorList value for this RegulatorBank.
     * 
     * @param regulatorList
     */
    public void setRegulatorList(com.cannontech.multispeak.deploy.service.Regulator[] regulatorList) {
        this.regulatorList = regulatorList;
    }


    /**
     * Gets the GPS value for this RegulatorBank.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this RegulatorBank.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegulatorBank)) return false;
        RegulatorBank other = (RegulatorBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.regType==null && other.getRegType()==null) || 
             (this.regType!=null &&
              this.regType.equals(other.getRegType()))) &&
            ((this.ctrlPhase==null && other.getCtrlPhase()==null) || 
             (this.ctrlPhase!=null &&
              this.ctrlPhase.equals(other.getCtrlPhase()))) &&
            ((this.wdgType==null && other.getWdgType()==null) || 
             (this.wdgType!=null &&
              this.wdgType.equals(other.getWdgType()))) &&
            ((this.regulatorList==null && other.getRegulatorList()==null) || 
             (this.regulatorList!=null &&
              java.util.Arrays.equals(this.regulatorList, other.getRegulatorList()))) &&
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
        if (getRegType() != null) {
            _hashCode += getRegType().hashCode();
        }
        if (getCtrlPhase() != null) {
            _hashCode += getCtrlPhase().hashCode();
        }
        if (getWdgType() != null) {
            _hashCode += getWdgType().hashCode();
        }
        if (getRegulatorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegulatorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegulatorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegulatorBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ctrlPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ctrlPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wdgType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regulatorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
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
