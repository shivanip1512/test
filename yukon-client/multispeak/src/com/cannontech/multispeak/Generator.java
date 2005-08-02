/**
 * Generator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Generator  extends com.cannontech.multispeak.MspMotorGenerator  implements java.io.Serializable {
    private java.math.BigInteger model;
    private com.cannontech.multispeak.ComplexNum holdVoltsZ;
    private java.lang.String holdID;
    private java.lang.Float kwOut;
    private java.lang.Float kwMax;
    private java.lang.Float kvarLead;
    private java.lang.Float kvarLag;
    private java.lang.Float srcVolts;
    private com.cannontech.multispeak.LdCon connected;

    public Generator() {
    }

    public Generator(
           java.math.BigInteger model,
           com.cannontech.multispeak.ComplexNum holdVoltsZ,
           java.lang.String holdID,
           java.lang.Float kwOut,
           java.lang.Float kwMax,
           java.lang.Float kvarLead,
           java.lang.Float kvarLag,
           java.lang.Float srcVolts,
           com.cannontech.multispeak.LdCon connected) {
           this.model = model;
           this.holdVoltsZ = holdVoltsZ;
           this.holdID = holdID;
           this.kwOut = kwOut;
           this.kwMax = kwMax;
           this.kvarLead = kvarLead;
           this.kvarLag = kvarLag;
           this.srcVolts = srcVolts;
           this.connected = connected;
    }


    /**
     * Gets the model value for this Generator.
     * 
     * @return model
     */
    public java.math.BigInteger getModel() {
        return model;
    }


    /**
     * Sets the model value for this Generator.
     * 
     * @param model
     */
    public void setModel(java.math.BigInteger model) {
        this.model = model;
    }


    /**
     * Gets the holdVoltsZ value for this Generator.
     * 
     * @return holdVoltsZ
     */
    public com.cannontech.multispeak.ComplexNum getHoldVoltsZ() {
        return holdVoltsZ;
    }


    /**
     * Sets the holdVoltsZ value for this Generator.
     * 
     * @param holdVoltsZ
     */
    public void setHoldVoltsZ(com.cannontech.multispeak.ComplexNum holdVoltsZ) {
        this.holdVoltsZ = holdVoltsZ;
    }


    /**
     * Gets the holdID value for this Generator.
     * 
     * @return holdID
     */
    public java.lang.String getHoldID() {
        return holdID;
    }


    /**
     * Sets the holdID value for this Generator.
     * 
     * @param holdID
     */
    public void setHoldID(java.lang.String holdID) {
        this.holdID = holdID;
    }


    /**
     * Gets the kwOut value for this Generator.
     * 
     * @return kwOut
     */
    public java.lang.Float getKwOut() {
        return kwOut;
    }


    /**
     * Sets the kwOut value for this Generator.
     * 
     * @param kwOut
     */
    public void setKwOut(java.lang.Float kwOut) {
        this.kwOut = kwOut;
    }


    /**
     * Gets the kwMax value for this Generator.
     * 
     * @return kwMax
     */
    public java.lang.Float getKwMax() {
        return kwMax;
    }


    /**
     * Sets the kwMax value for this Generator.
     * 
     * @param kwMax
     */
    public void setKwMax(java.lang.Float kwMax) {
        this.kwMax = kwMax;
    }


    /**
     * Gets the kvarLead value for this Generator.
     * 
     * @return kvarLead
     */
    public java.lang.Float getKvarLead() {
        return kvarLead;
    }


    /**
     * Sets the kvarLead value for this Generator.
     * 
     * @param kvarLead
     */
    public void setKvarLead(java.lang.Float kvarLead) {
        this.kvarLead = kvarLead;
    }


    /**
     * Gets the kvarLag value for this Generator.
     * 
     * @return kvarLag
     */
    public java.lang.Float getKvarLag() {
        return kvarLag;
    }


    /**
     * Sets the kvarLag value for this Generator.
     * 
     * @param kvarLag
     */
    public void setKvarLag(java.lang.Float kvarLag) {
        this.kvarLag = kvarLag;
    }


    /**
     * Gets the srcVolts value for this Generator.
     * 
     * @return srcVolts
     */
    public java.lang.Float getSrcVolts() {
        return srcVolts;
    }


    /**
     * Sets the srcVolts value for this Generator.
     * 
     * @param srcVolts
     */
    public void setSrcVolts(java.lang.Float srcVolts) {
        this.srcVolts = srcVolts;
    }


    /**
     * Gets the connected value for this Generator.
     * 
     * @return connected
     */
    public com.cannontech.multispeak.LdCon getConnected() {
        return connected;
    }


    /**
     * Sets the connected value for this Generator.
     * 
     * @param connected
     */
    public void setConnected(com.cannontech.multispeak.LdCon connected) {
        this.connected = connected;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Generator)) return false;
        Generator other = (Generator) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.model==null && other.getModel()==null) || 
             (this.model!=null &&
              this.model.equals(other.getModel()))) &&
            ((this.holdVoltsZ==null && other.getHoldVoltsZ()==null) || 
             (this.holdVoltsZ!=null &&
              this.holdVoltsZ.equals(other.getHoldVoltsZ()))) &&
            ((this.holdID==null && other.getHoldID()==null) || 
             (this.holdID!=null &&
              this.holdID.equals(other.getHoldID()))) &&
            ((this.kwOut==null && other.getKwOut()==null) || 
             (this.kwOut!=null &&
              this.kwOut.equals(other.getKwOut()))) &&
            ((this.kwMax==null && other.getKwMax()==null) || 
             (this.kwMax!=null &&
              this.kwMax.equals(other.getKwMax()))) &&
            ((this.kvarLead==null && other.getKvarLead()==null) || 
             (this.kvarLead!=null &&
              this.kvarLead.equals(other.getKvarLead()))) &&
            ((this.kvarLag==null && other.getKvarLag()==null) || 
             (this.kvarLag!=null &&
              this.kvarLag.equals(other.getKvarLag()))) &&
            ((this.srcVolts==null && other.getSrcVolts()==null) || 
             (this.srcVolts!=null &&
              this.srcVolts.equals(other.getSrcVolts()))) &&
            ((this.connected==null && other.getConnected()==null) || 
             (this.connected!=null &&
              this.connected.equals(other.getConnected())));
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
        if (getModel() != null) {
            _hashCode += getModel().hashCode();
        }
        if (getHoldVoltsZ() != null) {
            _hashCode += getHoldVoltsZ().hashCode();
        }
        if (getHoldID() != null) {
            _hashCode += getHoldID().hashCode();
        }
        if (getKwOut() != null) {
            _hashCode += getKwOut().hashCode();
        }
        if (getKwMax() != null) {
            _hashCode += getKwMax().hashCode();
        }
        if (getKvarLead() != null) {
            _hashCode += getKvarLead().hashCode();
        }
        if (getKvarLag() != null) {
            _hashCode += getKvarLag().hashCode();
        }
        if (getSrcVolts() != null) {
            _hashCode += getSrcVolts().hashCode();
        }
        if (getConnected() != null) {
            _hashCode += getConnected().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Generator.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "generator"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("model");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "model"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holdVoltsZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "holdVoltsZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holdID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "holdID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kwOut");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kwOut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kwMax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kwMax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kvarLead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kvarLead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kvarLag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kvarLag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("srcVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "srcVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connected");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connected"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon"));
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
