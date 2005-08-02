/**
 * TransformerBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class TransformerBank  extends com.cannontech.multispeak.MspBankObject  implements java.io.Serializable {
    private com.cannontech.multispeak.WdgCode wdgCode;
    private java.lang.String transDescr;
    private com.cannontech.multispeak.Mounting mounting;
    private java.lang.Float vInput;
    private java.lang.Float vOut;
    private com.cannontech.multispeak.Config sourceSideConfig;
    private java.lang.Float tertVolts;
    private com.cannontech.multispeak.ObjectRef tertChild;
    private java.lang.Float vOutNom;
    private java.lang.Float vOutNomTertiary;
    private java.lang.String secProt;
    private java.lang.Float secVolt;
    private java.lang.Float ratedVolt;
    private java.lang.String priProt;
    private com.cannontech.multispeak.ArrayOfTransformer transformerList;

    public TransformerBank() {
    }

    public TransformerBank(
           com.cannontech.multispeak.WdgCode wdgCode,
           java.lang.String transDescr,
           com.cannontech.multispeak.Mounting mounting,
           java.lang.Float vInput,
           java.lang.Float vOut,
           com.cannontech.multispeak.Config sourceSideConfig,
           java.lang.Float tertVolts,
           com.cannontech.multispeak.ObjectRef tertChild,
           java.lang.Float vOutNom,
           java.lang.Float vOutNomTertiary,
           java.lang.String secProt,
           java.lang.Float secVolt,
           java.lang.Float ratedVolt,
           java.lang.String priProt,
           com.cannontech.multispeak.ArrayOfTransformer transformerList) {
           this.wdgCode = wdgCode;
           this.transDescr = transDescr;
           this.mounting = mounting;
           this.vInput = vInput;
           this.vOut = vOut;
           this.sourceSideConfig = sourceSideConfig;
           this.tertVolts = tertVolts;
           this.tertChild = tertChild;
           this.vOutNom = vOutNom;
           this.vOutNomTertiary = vOutNomTertiary;
           this.secProt = secProt;
           this.secVolt = secVolt;
           this.ratedVolt = ratedVolt;
           this.priProt = priProt;
           this.transformerList = transformerList;
    }


    /**
     * Gets the wdgCode value for this TransformerBank.
     * 
     * @return wdgCode
     */
    public com.cannontech.multispeak.WdgCode getWdgCode() {
        return wdgCode;
    }


    /**
     * Sets the wdgCode value for this TransformerBank.
     * 
     * @param wdgCode
     */
    public void setWdgCode(com.cannontech.multispeak.WdgCode wdgCode) {
        this.wdgCode = wdgCode;
    }


    /**
     * Gets the transDescr value for this TransformerBank.
     * 
     * @return transDescr
     */
    public java.lang.String getTransDescr() {
        return transDescr;
    }


    /**
     * Sets the transDescr value for this TransformerBank.
     * 
     * @param transDescr
     */
    public void setTransDescr(java.lang.String transDescr) {
        this.transDescr = transDescr;
    }


    /**
     * Gets the mounting value for this TransformerBank.
     * 
     * @return mounting
     */
    public com.cannontech.multispeak.Mounting getMounting() {
        return mounting;
    }


    /**
     * Sets the mounting value for this TransformerBank.
     * 
     * @param mounting
     */
    public void setMounting(com.cannontech.multispeak.Mounting mounting) {
        this.mounting = mounting;
    }


    /**
     * Gets the vInput value for this TransformerBank.
     * 
     * @return vInput
     */
    public java.lang.Float getVInput() {
        return vInput;
    }


    /**
     * Sets the vInput value for this TransformerBank.
     * 
     * @param vInput
     */
    public void setVInput(java.lang.Float vInput) {
        this.vInput = vInput;
    }


    /**
     * Gets the vOut value for this TransformerBank.
     * 
     * @return vOut
     */
    public java.lang.Float getVOut() {
        return vOut;
    }


    /**
     * Sets the vOut value for this TransformerBank.
     * 
     * @param vOut
     */
    public void setVOut(java.lang.Float vOut) {
        this.vOut = vOut;
    }


    /**
     * Gets the sourceSideConfig value for this TransformerBank.
     * 
     * @return sourceSideConfig
     */
    public com.cannontech.multispeak.Config getSourceSideConfig() {
        return sourceSideConfig;
    }


    /**
     * Sets the sourceSideConfig value for this TransformerBank.
     * 
     * @param sourceSideConfig
     */
    public void setSourceSideConfig(com.cannontech.multispeak.Config sourceSideConfig) {
        this.sourceSideConfig = sourceSideConfig;
    }


    /**
     * Gets the tertVolts value for this TransformerBank.
     * 
     * @return tertVolts
     */
    public java.lang.Float getTertVolts() {
        return tertVolts;
    }


    /**
     * Sets the tertVolts value for this TransformerBank.
     * 
     * @param tertVolts
     */
    public void setTertVolts(java.lang.Float tertVolts) {
        this.tertVolts = tertVolts;
    }


    /**
     * Gets the tertChild value for this TransformerBank.
     * 
     * @return tertChild
     */
    public com.cannontech.multispeak.ObjectRef getTertChild() {
        return tertChild;
    }


    /**
     * Sets the tertChild value for this TransformerBank.
     * 
     * @param tertChild
     */
    public void setTertChild(com.cannontech.multispeak.ObjectRef tertChild) {
        this.tertChild = tertChild;
    }


    /**
     * Gets the vOutNom value for this TransformerBank.
     * 
     * @return vOutNom
     */
    public java.lang.Float getVOutNom() {
        return vOutNom;
    }


    /**
     * Sets the vOutNom value for this TransformerBank.
     * 
     * @param vOutNom
     */
    public void setVOutNom(java.lang.Float vOutNom) {
        this.vOutNom = vOutNom;
    }


    /**
     * Gets the vOutNomTertiary value for this TransformerBank.
     * 
     * @return vOutNomTertiary
     */
    public java.lang.Float getVOutNomTertiary() {
        return vOutNomTertiary;
    }


    /**
     * Sets the vOutNomTertiary value for this TransformerBank.
     * 
     * @param vOutNomTertiary
     */
    public void setVOutNomTertiary(java.lang.Float vOutNomTertiary) {
        this.vOutNomTertiary = vOutNomTertiary;
    }


    /**
     * Gets the secProt value for this TransformerBank.
     * 
     * @return secProt
     */
    public java.lang.String getSecProt() {
        return secProt;
    }


    /**
     * Sets the secProt value for this TransformerBank.
     * 
     * @param secProt
     */
    public void setSecProt(java.lang.String secProt) {
        this.secProt = secProt;
    }


    /**
     * Gets the secVolt value for this TransformerBank.
     * 
     * @return secVolt
     */
    public java.lang.Float getSecVolt() {
        return secVolt;
    }


    /**
     * Sets the secVolt value for this TransformerBank.
     * 
     * @param secVolt
     */
    public void setSecVolt(java.lang.Float secVolt) {
        this.secVolt = secVolt;
    }


    /**
     * Gets the ratedVolt value for this TransformerBank.
     * 
     * @return ratedVolt
     */
    public java.lang.Float getRatedVolt() {
        return ratedVolt;
    }


    /**
     * Sets the ratedVolt value for this TransformerBank.
     * 
     * @param ratedVolt
     */
    public void setRatedVolt(java.lang.Float ratedVolt) {
        this.ratedVolt = ratedVolt;
    }


    /**
     * Gets the priProt value for this TransformerBank.
     * 
     * @return priProt
     */
    public java.lang.String getPriProt() {
        return priProt;
    }


    /**
     * Sets the priProt value for this TransformerBank.
     * 
     * @param priProt
     */
    public void setPriProt(java.lang.String priProt) {
        this.priProt = priProt;
    }


    /**
     * Gets the transformerList value for this TransformerBank.
     * 
     * @return transformerList
     */
    public com.cannontech.multispeak.ArrayOfTransformer getTransformerList() {
        return transformerList;
    }


    /**
     * Sets the transformerList value for this TransformerBank.
     * 
     * @param transformerList
     */
    public void setTransformerList(com.cannontech.multispeak.ArrayOfTransformer transformerList) {
        this.transformerList = transformerList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransformerBank)) return false;
        TransformerBank other = (TransformerBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.wdgCode==null && other.getWdgCode()==null) || 
             (this.wdgCode!=null &&
              this.wdgCode.equals(other.getWdgCode()))) &&
            ((this.transDescr==null && other.getTransDescr()==null) || 
             (this.transDescr!=null &&
              this.transDescr.equals(other.getTransDescr()))) &&
            ((this.mounting==null && other.getMounting()==null) || 
             (this.mounting!=null &&
              this.mounting.equals(other.getMounting()))) &&
            ((this.vInput==null && other.getVInput()==null) || 
             (this.vInput!=null &&
              this.vInput.equals(other.getVInput()))) &&
            ((this.vOut==null && other.getVOut()==null) || 
             (this.vOut!=null &&
              this.vOut.equals(other.getVOut()))) &&
            ((this.sourceSideConfig==null && other.getSourceSideConfig()==null) || 
             (this.sourceSideConfig!=null &&
              this.sourceSideConfig.equals(other.getSourceSideConfig()))) &&
            ((this.tertVolts==null && other.getTertVolts()==null) || 
             (this.tertVolts!=null &&
              this.tertVolts.equals(other.getTertVolts()))) &&
            ((this.tertChild==null && other.getTertChild()==null) || 
             (this.tertChild!=null &&
              this.tertChild.equals(other.getTertChild()))) &&
            ((this.vOutNom==null && other.getVOutNom()==null) || 
             (this.vOutNom!=null &&
              this.vOutNom.equals(other.getVOutNom()))) &&
            ((this.vOutNomTertiary==null && other.getVOutNomTertiary()==null) || 
             (this.vOutNomTertiary!=null &&
              this.vOutNomTertiary.equals(other.getVOutNomTertiary()))) &&
            ((this.secProt==null && other.getSecProt()==null) || 
             (this.secProt!=null &&
              this.secProt.equals(other.getSecProt()))) &&
            ((this.secVolt==null && other.getSecVolt()==null) || 
             (this.secVolt!=null &&
              this.secVolt.equals(other.getSecVolt()))) &&
            ((this.ratedVolt==null && other.getRatedVolt()==null) || 
             (this.ratedVolt!=null &&
              this.ratedVolt.equals(other.getRatedVolt()))) &&
            ((this.priProt==null && other.getPriProt()==null) || 
             (this.priProt!=null &&
              this.priProt.equals(other.getPriProt()))) &&
            ((this.transformerList==null && other.getTransformerList()==null) || 
             (this.transformerList!=null &&
              this.transformerList.equals(other.getTransformerList())));
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
        if (getWdgCode() != null) {
            _hashCode += getWdgCode().hashCode();
        }
        if (getTransDescr() != null) {
            _hashCode += getTransDescr().hashCode();
        }
        if (getMounting() != null) {
            _hashCode += getMounting().hashCode();
        }
        if (getVInput() != null) {
            _hashCode += getVInput().hashCode();
        }
        if (getVOut() != null) {
            _hashCode += getVOut().hashCode();
        }
        if (getSourceSideConfig() != null) {
            _hashCode += getSourceSideConfig().hashCode();
        }
        if (getTertVolts() != null) {
            _hashCode += getTertVolts().hashCode();
        }
        if (getTertChild() != null) {
            _hashCode += getTertChild().hashCode();
        }
        if (getVOutNom() != null) {
            _hashCode += getVOutNom().hashCode();
        }
        if (getVOutNomTertiary() != null) {
            _hashCode += getVOutNomTertiary().hashCode();
        }
        if (getSecProt() != null) {
            _hashCode += getSecProt().hashCode();
        }
        if (getSecVolt() != null) {
            _hashCode += getSecVolt().hashCode();
        }
        if (getRatedVolt() != null) {
            _hashCode += getRatedVolt().hashCode();
        }
        if (getPriProt() != null) {
            _hashCode += getPriProt().hashCode();
        }
        if (getTransformerList() != null) {
            _hashCode += getTransformerList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransformerBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wdgCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transDescr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transDescr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mounting");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VInput");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vInput"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
        elemField.setFieldName("sourceSideConfig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sourceSideConfig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "config"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tertVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tertVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tertChild");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tertChild"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VOutNom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vOutNom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VOutNomTertiary");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vOutNomTertiary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secProt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secProt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratedVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ratedVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priProt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priProt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformerList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTransformer"));
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
