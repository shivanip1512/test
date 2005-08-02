/**
 * Regulator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Regulator  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String eaEquipment;
    private java.lang.Float vOut;
    private java.lang.Float ldcR;
    private java.lang.Float ldcX;
    private java.lang.Float fhHi;
    private java.lang.Float fhLo;
    private com.cannontech.multispeak.MspPhase phase;
    private java.lang.Float kva;

    public Regulator() {
    }

    public Regulator(
           java.lang.String eaEquipment,
           java.lang.Float vOut,
           java.lang.Float ldcR,
           java.lang.Float ldcX,
           java.lang.Float fhHi,
           java.lang.Float fhLo,
           com.cannontech.multispeak.MspPhase phase,
           java.lang.Float kva) {
           this.eaEquipment = eaEquipment;
           this.vOut = vOut;
           this.ldcR = ldcR;
           this.ldcX = ldcX;
           this.fhHi = fhHi;
           this.fhLo = fhLo;
           this.phase = phase;
           this.kva = kva;
    }


    /**
     * Gets the eaEquipment value for this Regulator.
     * 
     * @return eaEquipment
     */
    public java.lang.String getEaEquipment() {
        return eaEquipment;
    }


    /**
     * Sets the eaEquipment value for this Regulator.
     * 
     * @param eaEquipment
     */
    public void setEaEquipment(java.lang.String eaEquipment) {
        this.eaEquipment = eaEquipment;
    }


    /**
     * Gets the vOut value for this Regulator.
     * 
     * @return vOut
     */
    public java.lang.Float getVOut() {
        return vOut;
    }


    /**
     * Sets the vOut value for this Regulator.
     * 
     * @param vOut
     */
    public void setVOut(java.lang.Float vOut) {
        this.vOut = vOut;
    }


    /**
     * Gets the ldcR value for this Regulator.
     * 
     * @return ldcR
     */
    public java.lang.Float getLdcR() {
        return ldcR;
    }


    /**
     * Sets the ldcR value for this Regulator.
     * 
     * @param ldcR
     */
    public void setLdcR(java.lang.Float ldcR) {
        this.ldcR = ldcR;
    }


    /**
     * Gets the ldcX value for this Regulator.
     * 
     * @return ldcX
     */
    public java.lang.Float getLdcX() {
        return ldcX;
    }


    /**
     * Sets the ldcX value for this Regulator.
     * 
     * @param ldcX
     */
    public void setLdcX(java.lang.Float ldcX) {
        this.ldcX = ldcX;
    }


    /**
     * Gets the fhHi value for this Regulator.
     * 
     * @return fhHi
     */
    public java.lang.Float getFhHi() {
        return fhHi;
    }


    /**
     * Sets the fhHi value for this Regulator.
     * 
     * @param fhHi
     */
    public void setFhHi(java.lang.Float fhHi) {
        this.fhHi = fhHi;
    }


    /**
     * Gets the fhLo value for this Regulator.
     * 
     * @return fhLo
     */
    public java.lang.Float getFhLo() {
        return fhLo;
    }


    /**
     * Sets the fhLo value for this Regulator.
     * 
     * @param fhLo
     */
    public void setFhLo(java.lang.Float fhLo) {
        this.fhLo = fhLo;
    }


    /**
     * Gets the phase value for this Regulator.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.MspPhase getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this Regulator.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.MspPhase phase) {
        this.phase = phase;
    }


    /**
     * Gets the kva value for this Regulator.
     * 
     * @return kva
     */
    public java.lang.Float getKva() {
        return kva;
    }


    /**
     * Sets the kva value for this Regulator.
     * 
     * @param kva
     */
    public void setKva(java.lang.Float kva) {
        this.kva = kva;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Regulator)) return false;
        Regulator other = (Regulator) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.eaEquipment==null && other.getEaEquipment()==null) || 
             (this.eaEquipment!=null &&
              this.eaEquipment.equals(other.getEaEquipment()))) &&
            ((this.vOut==null && other.getVOut()==null) || 
             (this.vOut!=null &&
              this.vOut.equals(other.getVOut()))) &&
            ((this.ldcR==null && other.getLdcR()==null) || 
             (this.ldcR!=null &&
              this.ldcR.equals(other.getLdcR()))) &&
            ((this.ldcX==null && other.getLdcX()==null) || 
             (this.ldcX!=null &&
              this.ldcX.equals(other.getLdcX()))) &&
            ((this.fhHi==null && other.getFhHi()==null) || 
             (this.fhHi!=null &&
              this.fhHi.equals(other.getFhHi()))) &&
            ((this.fhLo==null && other.getFhLo()==null) || 
             (this.fhLo!=null &&
              this.fhLo.equals(other.getFhLo()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.kva==null && other.getKva()==null) || 
             (this.kva!=null &&
              this.kva.equals(other.getKva())));
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
        if (getEaEquipment() != null) {
            _hashCode += getEaEquipment().hashCode();
        }
        if (getVOut() != null) {
            _hashCode += getVOut().hashCode();
        }
        if (getLdcR() != null) {
            _hashCode += getLdcR().hashCode();
        }
        if (getLdcX() != null) {
            _hashCode += getLdcX().hashCode();
        }
        if (getFhHi() != null) {
            _hashCode += getFhHi().hashCode();
        }
        if (getFhLo() != null) {
            _hashCode += getFhLo().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getKva() != null) {
            _hashCode += getKva().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Regulator.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulator"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaEquipment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaEquipment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VOut");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vOut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldcR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldcR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldcX");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldcX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fhHi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fhHi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fhLo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fhLo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kva");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kva"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
