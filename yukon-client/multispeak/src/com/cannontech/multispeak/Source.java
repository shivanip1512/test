/**
 * Source.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Source  implements java.io.Serializable {
    private org.apache.axis.types.UnsignedInt sourceID;
    private com.cannontech.multispeak.QuantityType quantityType;
    private com.cannontech.multispeak.Uom uom;
    private com.cannontech.multispeak.Accountability accountability;
    private com.cannontech.multispeak.PhaseAssociation phase;
    private java.lang.String harmonic;
    private com.cannontech.multispeak.TransformerRatio transformerRatio;
    private com.cannontech.multispeak.DisplayFormat displayFormat;
    private java.lang.Boolean multiplierApplied;

    public Source() {
    }

    public Source(
           org.apache.axis.types.UnsignedInt sourceID,
           com.cannontech.multispeak.QuantityType quantityType,
           com.cannontech.multispeak.Uom uom,
           com.cannontech.multispeak.Accountability accountability,
           com.cannontech.multispeak.PhaseAssociation phase,
           java.lang.String harmonic,
           com.cannontech.multispeak.TransformerRatio transformerRatio,
           com.cannontech.multispeak.DisplayFormat displayFormat,
           java.lang.Boolean multiplierApplied) {
           this.sourceID = sourceID;
           this.quantityType = quantityType;
           this.uom = uom;
           this.accountability = accountability;
           this.phase = phase;
           this.harmonic = harmonic;
           this.transformerRatio = transformerRatio;
           this.displayFormat = displayFormat;
           this.multiplierApplied = multiplierApplied;
    }


    /**
     * Gets the sourceID value for this Source.
     * 
     * @return sourceID
     */
    public org.apache.axis.types.UnsignedInt getSourceID() {
        return sourceID;
    }


    /**
     * Sets the sourceID value for this Source.
     * 
     * @param sourceID
     */
    public void setSourceID(org.apache.axis.types.UnsignedInt sourceID) {
        this.sourceID = sourceID;
    }


    /**
     * Gets the quantityType value for this Source.
     * 
     * @return quantityType
     */
    public com.cannontech.multispeak.QuantityType getQuantityType() {
        return quantityType;
    }


    /**
     * Sets the quantityType value for this Source.
     * 
     * @param quantityType
     */
    public void setQuantityType(com.cannontech.multispeak.QuantityType quantityType) {
        this.quantityType = quantityType;
    }


    /**
     * Gets the uom value for this Source.
     * 
     * @return uom
     */
    public com.cannontech.multispeak.Uom getUom() {
        return uom;
    }


    /**
     * Sets the uom value for this Source.
     * 
     * @param uom
     */
    public void setUom(com.cannontech.multispeak.Uom uom) {
        this.uom = uom;
    }


    /**
     * Gets the accountability value for this Source.
     * 
     * @return accountability
     */
    public com.cannontech.multispeak.Accountability getAccountability() {
        return accountability;
    }


    /**
     * Sets the accountability value for this Source.
     * 
     * @param accountability
     */
    public void setAccountability(com.cannontech.multispeak.Accountability accountability) {
        this.accountability = accountability;
    }


    /**
     * Gets the phase value for this Source.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseAssociation getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this Source.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseAssociation phase) {
        this.phase = phase;
    }


    /**
     * Gets the harmonic value for this Source.
     * 
     * @return harmonic
     */
    public java.lang.String getHarmonic() {
        return harmonic;
    }


    /**
     * Sets the harmonic value for this Source.
     * 
     * @param harmonic
     */
    public void setHarmonic(java.lang.String harmonic) {
        this.harmonic = harmonic;
    }


    /**
     * Gets the transformerRatio value for this Source.
     * 
     * @return transformerRatio
     */
    public com.cannontech.multispeak.TransformerRatio getTransformerRatio() {
        return transformerRatio;
    }


    /**
     * Sets the transformerRatio value for this Source.
     * 
     * @param transformerRatio
     */
    public void setTransformerRatio(com.cannontech.multispeak.TransformerRatio transformerRatio) {
        this.transformerRatio = transformerRatio;
    }


    /**
     * Gets the displayFormat value for this Source.
     * 
     * @return displayFormat
     */
    public com.cannontech.multispeak.DisplayFormat getDisplayFormat() {
        return displayFormat;
    }


    /**
     * Sets the displayFormat value for this Source.
     * 
     * @param displayFormat
     */
    public void setDisplayFormat(com.cannontech.multispeak.DisplayFormat displayFormat) {
        this.displayFormat = displayFormat;
    }


    /**
     * Gets the multiplierApplied value for this Source.
     * 
     * @return multiplierApplied
     */
    public java.lang.Boolean getMultiplierApplied() {
        return multiplierApplied;
    }


    /**
     * Sets the multiplierApplied value for this Source.
     * 
     * @param multiplierApplied
     */
    public void setMultiplierApplied(java.lang.Boolean multiplierApplied) {
        this.multiplierApplied = multiplierApplied;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Source)) return false;
        Source other = (Source) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sourceID==null && other.getSourceID()==null) || 
             (this.sourceID!=null &&
              this.sourceID.equals(other.getSourceID()))) &&
            ((this.quantityType==null && other.getQuantityType()==null) || 
             (this.quantityType!=null &&
              this.quantityType.equals(other.getQuantityType()))) &&
            ((this.uom==null && other.getUom()==null) || 
             (this.uom!=null &&
              this.uom.equals(other.getUom()))) &&
            ((this.accountability==null && other.getAccountability()==null) || 
             (this.accountability!=null &&
              this.accountability.equals(other.getAccountability()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.harmonic==null && other.getHarmonic()==null) || 
             (this.harmonic!=null &&
              this.harmonic.equals(other.getHarmonic()))) &&
            ((this.transformerRatio==null && other.getTransformerRatio()==null) || 
             (this.transformerRatio!=null &&
              this.transformerRatio.equals(other.getTransformerRatio()))) &&
            ((this.displayFormat==null && other.getDisplayFormat()==null) || 
             (this.displayFormat!=null &&
              this.displayFormat.equals(other.getDisplayFormat()))) &&
            ((this.multiplierApplied==null && other.getMultiplierApplied()==null) || 
             (this.multiplierApplied!=null &&
              this.multiplierApplied.equals(other.getMultiplierApplied())));
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
        if (getSourceID() != null) {
            _hashCode += getSourceID().hashCode();
        }
        if (getQuantityType() != null) {
            _hashCode += getQuantityType().hashCode();
        }
        if (getUom() != null) {
            _hashCode += getUom().hashCode();
        }
        if (getAccountability() != null) {
            _hashCode += getAccountability().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getHarmonic() != null) {
            _hashCode += getHarmonic().hashCode();
        }
        if (getTransformerRatio() != null) {
            _hashCode += getTransformerRatio().hashCode();
        }
        if (getDisplayFormat() != null) {
            _hashCode += getDisplayFormat().hashCode();
        }
        if (getMultiplierApplied() != null) {
            _hashCode += getMultiplierApplied().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Source.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sourceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantityType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantityType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountability");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseAssociation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("harmonic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "harmonic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("displayFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayFormat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayFormat"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiplierApplied");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "multiplierApplied"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
