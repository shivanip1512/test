/**
 * MspElectricLine.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public abstract class MspElectricLine  extends com.cannontech.multispeak.deploy.service.MspConnectivityLine  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCode;

    private com.cannontech.multispeak.deploy.service.Conductor[] conductorList;

    private java.lang.String condN;

    private java.lang.Float condLength;

    private java.lang.String constr;

    private com.cannontech.multispeak.deploy.service.MspLoadGroup load;

    public MspElectricLine() {
    }

    public MspElectricLine(
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
           com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
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
            toNodeID);
        this.phaseCode = phaseCode;
        this.conductorList = conductorList;
        this.condN = condN;
        this.condLength = condLength;
        this.constr = constr;
        this.load = load;
    }


    /**
     * Gets the phaseCode value for this MspElectricLine.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this MspElectricLine.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the conductorList value for this MspElectricLine.
     * 
     * @return conductorList
     */
    public com.cannontech.multispeak.deploy.service.Conductor[] getConductorList() {
        return conductorList;
    }


    /**
     * Sets the conductorList value for this MspElectricLine.
     * 
     * @param conductorList
     */
    public void setConductorList(com.cannontech.multispeak.deploy.service.Conductor[] conductorList) {
        this.conductorList = conductorList;
    }


    /**
     * Gets the condN value for this MspElectricLine.
     * 
     * @return condN
     */
    public java.lang.String getCondN() {
        return condN;
    }


    /**
     * Sets the condN value for this MspElectricLine.
     * 
     * @param condN
     */
    public void setCondN(java.lang.String condN) {
        this.condN = condN;
    }


    /**
     * Gets the condLength value for this MspElectricLine.
     * 
     * @return condLength
     */
    public java.lang.Float getCondLength() {
        return condLength;
    }


    /**
     * Sets the condLength value for this MspElectricLine.
     * 
     * @param condLength
     */
    public void setCondLength(java.lang.Float condLength) {
        this.condLength = condLength;
    }


    /**
     * Gets the constr value for this MspElectricLine.
     * 
     * @return constr
     */
    public java.lang.String getConstr() {
        return constr;
    }


    /**
     * Sets the constr value for this MspElectricLine.
     * 
     * @param constr
     */
    public void setConstr(java.lang.String constr) {
        this.constr = constr;
    }


    /**
     * Gets the load value for this MspElectricLine.
     * 
     * @return load
     */
    public com.cannontech.multispeak.deploy.service.MspLoadGroup getLoad() {
        return load;
    }


    /**
     * Sets the load value for this MspElectricLine.
     * 
     * @param load
     */
    public void setLoad(com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
        this.load = load;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspElectricLine)) return false;
        MspElectricLine other = (MspElectricLine) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode()))) &&
            ((this.conductorList==null && other.getConductorList()==null) || 
             (this.conductorList!=null &&
              java.util.Arrays.equals(this.conductorList, other.getConductorList()))) &&
            ((this.condN==null && other.getCondN()==null) || 
             (this.condN!=null &&
              this.condN.equals(other.getCondN()))) &&
            ((this.condLength==null && other.getCondLength()==null) || 
             (this.condLength!=null &&
              this.condLength.equals(other.getCondLength()))) &&
            ((this.constr==null && other.getConstr()==null) || 
             (this.constr!=null &&
              this.constr.equals(other.getConstr()))) &&
            ((this.load==null && other.getLoad()==null) || 
             (this.load!=null &&
              this.load.equals(other.getLoad())));
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
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        if (getConductorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConductorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConductorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCondN() != null) {
            _hashCode += getCondN().hashCode();
        }
        if (getCondLength() != null) {
            _hashCode += getCondLength().hashCode();
        }
        if (getConstr() != null) {
            _hashCode += getConstr().hashCode();
        }
        if (getLoad() != null) {
            _hashCode += getLoad().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspElectricLine.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspElectricLine"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conductorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("condN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "condN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("condLength");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "condLength"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("constr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("load");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "load"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLoadGroup"));
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
