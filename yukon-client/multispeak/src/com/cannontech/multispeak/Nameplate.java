/**
 * Nameplate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Nameplate  implements java.io.Serializable {
    private java.lang.Float kh;
    private java.lang.Float kr;
    private com.cannontech.multispeak.Frequency frequency;
    private com.cannontech.multispeak.NumberOfElement numberOfElements;
    private com.cannontech.multispeak.BaseType baseType;
    private java.lang.String accuracyClass;
    private com.cannontech.multispeak.ElementsVoltage elementsVoltage;
    private com.cannontech.multispeak.SupplyVoltage supplyVoltage;
    private java.lang.Float maxAmperage;
    private java.lang.Float testAmperage;
    private java.lang.Float regRatio;
    private java.lang.Long phases;
    private java.lang.Long wires;
    private java.lang.Long dials;
    private java.lang.String form;
    private java.lang.Long multiplier;
    private java.lang.Float demandMult;
    private com.cannontech.multispeak.TransformerRatio transformerRatio;
    private java.lang.String transponderID;

    public Nameplate() {
    }

    public Nameplate(
           java.lang.Float kh,
           java.lang.Float kr,
           com.cannontech.multispeak.Frequency frequency,
           com.cannontech.multispeak.NumberOfElement numberOfElements,
           com.cannontech.multispeak.BaseType baseType,
           java.lang.String accuracyClass,
           com.cannontech.multispeak.ElementsVoltage elementsVoltage,
           com.cannontech.multispeak.SupplyVoltage supplyVoltage,
           java.lang.Float maxAmperage,
           java.lang.Float testAmperage,
           java.lang.Float regRatio,
           java.lang.Long phases,
           java.lang.Long wires,
           java.lang.Long dials,
           java.lang.String form,
           java.lang.Long multiplier,
           java.lang.Float demandMult,
           com.cannontech.multispeak.TransformerRatio transformerRatio,
           java.lang.String transponderID) {
           this.kh = kh;
           this.kr = kr;
           this.frequency = frequency;
           this.numberOfElements = numberOfElements;
           this.baseType = baseType;
           this.accuracyClass = accuracyClass;
           this.elementsVoltage = elementsVoltage;
           this.supplyVoltage = supplyVoltage;
           this.maxAmperage = maxAmperage;
           this.testAmperage = testAmperage;
           this.regRatio = regRatio;
           this.phases = phases;
           this.wires = wires;
           this.dials = dials;
           this.form = form;
           this.multiplier = multiplier;
           this.demandMult = demandMult;
           this.transformerRatio = transformerRatio;
           this.transponderID = transponderID;
    }


    /**
     * Gets the kh value for this Nameplate.
     * 
     * @return kh
     */
    public java.lang.Float getKh() {
        return kh;
    }


    /**
     * Sets the kh value for this Nameplate.
     * 
     * @param kh
     */
    public void setKh(java.lang.Float kh) {
        this.kh = kh;
    }


    /**
     * Gets the kr value for this Nameplate.
     * 
     * @return kr
     */
    public java.lang.Float getKr() {
        return kr;
    }


    /**
     * Sets the kr value for this Nameplate.
     * 
     * @param kr
     */
    public void setKr(java.lang.Float kr) {
        this.kr = kr;
    }


    /**
     * Gets the frequency value for this Nameplate.
     * 
     * @return frequency
     */
    public com.cannontech.multispeak.Frequency getFrequency() {
        return frequency;
    }


    /**
     * Sets the frequency value for this Nameplate.
     * 
     * @param frequency
     */
    public void setFrequency(com.cannontech.multispeak.Frequency frequency) {
        this.frequency = frequency;
    }


    /**
     * Gets the numberOfElements value for this Nameplate.
     * 
     * @return numberOfElements
     */
    public com.cannontech.multispeak.NumberOfElement getNumberOfElements() {
        return numberOfElements;
    }


    /**
     * Sets the numberOfElements value for this Nameplate.
     * 
     * @param numberOfElements
     */
    public void setNumberOfElements(com.cannontech.multispeak.NumberOfElement numberOfElements) {
        this.numberOfElements = numberOfElements;
    }


    /**
     * Gets the baseType value for this Nameplate.
     * 
     * @return baseType
     */
    public com.cannontech.multispeak.BaseType getBaseType() {
        return baseType;
    }


    /**
     * Sets the baseType value for this Nameplate.
     * 
     * @param baseType
     */
    public void setBaseType(com.cannontech.multispeak.BaseType baseType) {
        this.baseType = baseType;
    }


    /**
     * Gets the accuracyClass value for this Nameplate.
     * 
     * @return accuracyClass
     */
    public java.lang.String getAccuracyClass() {
        return accuracyClass;
    }


    /**
     * Sets the accuracyClass value for this Nameplate.
     * 
     * @param accuracyClass
     */
    public void setAccuracyClass(java.lang.String accuracyClass) {
        this.accuracyClass = accuracyClass;
    }


    /**
     * Gets the elementsVoltage value for this Nameplate.
     * 
     * @return elementsVoltage
     */
    public com.cannontech.multispeak.ElementsVoltage getElementsVoltage() {
        return elementsVoltage;
    }


    /**
     * Sets the elementsVoltage value for this Nameplate.
     * 
     * @param elementsVoltage
     */
    public void setElementsVoltage(com.cannontech.multispeak.ElementsVoltage elementsVoltage) {
        this.elementsVoltage = elementsVoltage;
    }


    /**
     * Gets the supplyVoltage value for this Nameplate.
     * 
     * @return supplyVoltage
     */
    public com.cannontech.multispeak.SupplyVoltage getSupplyVoltage() {
        return supplyVoltage;
    }


    /**
     * Sets the supplyVoltage value for this Nameplate.
     * 
     * @param supplyVoltage
     */
    public void setSupplyVoltage(com.cannontech.multispeak.SupplyVoltage supplyVoltage) {
        this.supplyVoltage = supplyVoltage;
    }


    /**
     * Gets the maxAmperage value for this Nameplate.
     * 
     * @return maxAmperage
     */
    public java.lang.Float getMaxAmperage() {
        return maxAmperage;
    }


    /**
     * Sets the maxAmperage value for this Nameplate.
     * 
     * @param maxAmperage
     */
    public void setMaxAmperage(java.lang.Float maxAmperage) {
        this.maxAmperage = maxAmperage;
    }


    /**
     * Gets the testAmperage value for this Nameplate.
     * 
     * @return testAmperage
     */
    public java.lang.Float getTestAmperage() {
        return testAmperage;
    }


    /**
     * Sets the testAmperage value for this Nameplate.
     * 
     * @param testAmperage
     */
    public void setTestAmperage(java.lang.Float testAmperage) {
        this.testAmperage = testAmperage;
    }


    /**
     * Gets the regRatio value for this Nameplate.
     * 
     * @return regRatio
     */
    public java.lang.Float getRegRatio() {
        return regRatio;
    }


    /**
     * Sets the regRatio value for this Nameplate.
     * 
     * @param regRatio
     */
    public void setRegRatio(java.lang.Float regRatio) {
        this.regRatio = regRatio;
    }


    /**
     * Gets the phases value for this Nameplate.
     * 
     * @return phases
     */
    public java.lang.Long getPhases() {
        return phases;
    }


    /**
     * Sets the phases value for this Nameplate.
     * 
     * @param phases
     */
    public void setPhases(java.lang.Long phases) {
        this.phases = phases;
    }


    /**
     * Gets the wires value for this Nameplate.
     * 
     * @return wires
     */
    public java.lang.Long getWires() {
        return wires;
    }


    /**
     * Sets the wires value for this Nameplate.
     * 
     * @param wires
     */
    public void setWires(java.lang.Long wires) {
        this.wires = wires;
    }


    /**
     * Gets the dials value for this Nameplate.
     * 
     * @return dials
     */
    public java.lang.Long getDials() {
        return dials;
    }


    /**
     * Sets the dials value for this Nameplate.
     * 
     * @param dials
     */
    public void setDials(java.lang.Long dials) {
        this.dials = dials;
    }


    /**
     * Gets the form value for this Nameplate.
     * 
     * @return form
     */
    public java.lang.String getForm() {
        return form;
    }


    /**
     * Sets the form value for this Nameplate.
     * 
     * @param form
     */
    public void setForm(java.lang.String form) {
        this.form = form;
    }


    /**
     * Gets the multiplier value for this Nameplate.
     * 
     * @return multiplier
     */
    public java.lang.Long getMultiplier() {
        return multiplier;
    }


    /**
     * Sets the multiplier value for this Nameplate.
     * 
     * @param multiplier
     */
    public void setMultiplier(java.lang.Long multiplier) {
        this.multiplier = multiplier;
    }


    /**
     * Gets the demandMult value for this Nameplate.
     * 
     * @return demandMult
     */
    public java.lang.Float getDemandMult() {
        return demandMult;
    }


    /**
     * Sets the demandMult value for this Nameplate.
     * 
     * @param demandMult
     */
    public void setDemandMult(java.lang.Float demandMult) {
        this.demandMult = demandMult;
    }


    /**
     * Gets the transformerRatio value for this Nameplate.
     * 
     * @return transformerRatio
     */
    public com.cannontech.multispeak.TransformerRatio getTransformerRatio() {
        return transformerRatio;
    }


    /**
     * Sets the transformerRatio value for this Nameplate.
     * 
     * @param transformerRatio
     */
    public void setTransformerRatio(com.cannontech.multispeak.TransformerRatio transformerRatio) {
        this.transformerRatio = transformerRatio;
    }


    /**
     * Gets the transponderID value for this Nameplate.
     * 
     * @return transponderID
     */
    public java.lang.String getTransponderID() {
        return transponderID;
    }


    /**
     * Sets the transponderID value for this Nameplate.
     * 
     * @param transponderID
     */
    public void setTransponderID(java.lang.String transponderID) {
        this.transponderID = transponderID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Nameplate)) return false;
        Nameplate other = (Nameplate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.kh==null && other.getKh()==null) || 
             (this.kh!=null &&
              this.kh.equals(other.getKh()))) &&
            ((this.kr==null && other.getKr()==null) || 
             (this.kr!=null &&
              this.kr.equals(other.getKr()))) &&
            ((this.frequency==null && other.getFrequency()==null) || 
             (this.frequency!=null &&
              this.frequency.equals(other.getFrequency()))) &&
            ((this.numberOfElements==null && other.getNumberOfElements()==null) || 
             (this.numberOfElements!=null &&
              this.numberOfElements.equals(other.getNumberOfElements()))) &&
            ((this.baseType==null && other.getBaseType()==null) || 
             (this.baseType!=null &&
              this.baseType.equals(other.getBaseType()))) &&
            ((this.accuracyClass==null && other.getAccuracyClass()==null) || 
             (this.accuracyClass!=null &&
              this.accuracyClass.equals(other.getAccuracyClass()))) &&
            ((this.elementsVoltage==null && other.getElementsVoltage()==null) || 
             (this.elementsVoltage!=null &&
              this.elementsVoltage.equals(other.getElementsVoltage()))) &&
            ((this.supplyVoltage==null && other.getSupplyVoltage()==null) || 
             (this.supplyVoltage!=null &&
              this.supplyVoltage.equals(other.getSupplyVoltage()))) &&
            ((this.maxAmperage==null && other.getMaxAmperage()==null) || 
             (this.maxAmperage!=null &&
              this.maxAmperage.equals(other.getMaxAmperage()))) &&
            ((this.testAmperage==null && other.getTestAmperage()==null) || 
             (this.testAmperage!=null &&
              this.testAmperage.equals(other.getTestAmperage()))) &&
            ((this.regRatio==null && other.getRegRatio()==null) || 
             (this.regRatio!=null &&
              this.regRatio.equals(other.getRegRatio()))) &&
            ((this.phases==null && other.getPhases()==null) || 
             (this.phases!=null &&
              this.phases.equals(other.getPhases()))) &&
            ((this.wires==null && other.getWires()==null) || 
             (this.wires!=null &&
              this.wires.equals(other.getWires()))) &&
            ((this.dials==null && other.getDials()==null) || 
             (this.dials!=null &&
              this.dials.equals(other.getDials()))) &&
            ((this.form==null && other.getForm()==null) || 
             (this.form!=null &&
              this.form.equals(other.getForm()))) &&
            ((this.multiplier==null && other.getMultiplier()==null) || 
             (this.multiplier!=null &&
              this.multiplier.equals(other.getMultiplier()))) &&
            ((this.demandMult==null && other.getDemandMult()==null) || 
             (this.demandMult!=null &&
              this.demandMult.equals(other.getDemandMult()))) &&
            ((this.transformerRatio==null && other.getTransformerRatio()==null) || 
             (this.transformerRatio!=null &&
              this.transformerRatio.equals(other.getTransformerRatio()))) &&
            ((this.transponderID==null && other.getTransponderID()==null) || 
             (this.transponderID!=null &&
              this.transponderID.equals(other.getTransponderID())));
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
        if (getKh() != null) {
            _hashCode += getKh().hashCode();
        }
        if (getKr() != null) {
            _hashCode += getKr().hashCode();
        }
        if (getFrequency() != null) {
            _hashCode += getFrequency().hashCode();
        }
        if (getNumberOfElements() != null) {
            _hashCode += getNumberOfElements().hashCode();
        }
        if (getBaseType() != null) {
            _hashCode += getBaseType().hashCode();
        }
        if (getAccuracyClass() != null) {
            _hashCode += getAccuracyClass().hashCode();
        }
        if (getElementsVoltage() != null) {
            _hashCode += getElementsVoltage().hashCode();
        }
        if (getSupplyVoltage() != null) {
            _hashCode += getSupplyVoltage().hashCode();
        }
        if (getMaxAmperage() != null) {
            _hashCode += getMaxAmperage().hashCode();
        }
        if (getTestAmperage() != null) {
            _hashCode += getTestAmperage().hashCode();
        }
        if (getRegRatio() != null) {
            _hashCode += getRegRatio().hashCode();
        }
        if (getPhases() != null) {
            _hashCode += getPhases().hashCode();
        }
        if (getWires() != null) {
            _hashCode += getWires().hashCode();
        }
        if (getDials() != null) {
            _hashCode += getDials().hashCode();
        }
        if (getForm() != null) {
            _hashCode += getForm().hashCode();
        }
        if (getMultiplier() != null) {
            _hashCode += getMultiplier().hashCode();
        }
        if (getDemandMult() != null) {
            _hashCode += getDemandMult().hashCode();
        }
        if (getTransformerRatio() != null) {
            _hashCode += getTransformerRatio().hashCode();
        }
        if (getTransponderID() != null) {
            _hashCode += getTransponderID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Nameplate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("frequency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "frequency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "frequency"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfElements");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfElements"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfElement"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accuracyClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accuracyClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementsVoltage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementsVoltage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementsVoltage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supplyVoltage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supplyVoltage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supplyVoltage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxAmperage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxAmperage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testAmperage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testAmperage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regRatio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regRatio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phases");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wires");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wires"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dials");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dials"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("form");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "form"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiplier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "multiplier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("demandMult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "demandMult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformerRatio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerRatio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerRatio"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transponderID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderID"));
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
