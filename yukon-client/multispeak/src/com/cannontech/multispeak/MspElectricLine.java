/**
 * MspElectricLine.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspElectricLine  extends com.cannontech.multispeak.MspConnectivityLine  implements java.io.Serializable {
    private com.cannontech.multispeak.PhaseCd phaseCode;
    private com.cannontech.multispeak.ArrayOfConductor conductorList;
    private java.lang.String condN;
    private java.lang.Float condLength;
    private java.lang.String constr;
    private com.cannontech.multispeak.MspLoadGroup load;

    public MspElectricLine() {
    }

    public MspElectricLine(
           com.cannontech.multispeak.PhaseCd phaseCode,
           com.cannontech.multispeak.ArrayOfConductor conductorList,
           java.lang.String condN,
           java.lang.Float condLength,
           java.lang.String constr,
           com.cannontech.multispeak.MspLoadGroup load) {
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
    public com.cannontech.multispeak.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this MspElectricLine.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the conductorList value for this MspElectricLine.
     * 
     * @return conductorList
     */
    public com.cannontech.multispeak.ArrayOfConductor getConductorList() {
        return conductorList;
    }


    /**
     * Sets the conductorList value for this MspElectricLine.
     * 
     * @param conductorList
     */
    public void setConductorList(com.cannontech.multispeak.ArrayOfConductor conductorList) {
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
    public com.cannontech.multispeak.MspLoadGroup getLoad() {
        return load;
    }


    /**
     * Sets the load value for this MspElectricLine.
     * 
     * @param load
     */
    public void setLoad(com.cannontech.multispeak.MspLoadGroup load) {
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
              this.conductorList.equals(other.getConductorList()))) &&
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
            _hashCode += getConductorList().hashCode();
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConductor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
