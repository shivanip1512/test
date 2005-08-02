/**
 * Registers.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Registers  implements java.io.Serializable {
    private com.cannontech.multispeak.Summation summation;
    private com.cannontech.multispeak.CoincidentalValue coincidentalValue;
    private com.cannontech.multispeak.CumDemand cumDemand;
    private com.cannontech.multispeak.FlowDemand demand;
    private org.apache.axis.types.UnsignedInt tier;  // attribute

    public Registers() {
    }

    public Registers(
           com.cannontech.multispeak.Summation summation,
           com.cannontech.multispeak.CoincidentalValue coincidentalValue,
           com.cannontech.multispeak.CumDemand cumDemand,
           com.cannontech.multispeak.FlowDemand demand,
           org.apache.axis.types.UnsignedInt tier) {
           this.summation = summation;
           this.coincidentalValue = coincidentalValue;
           this.cumDemand = cumDemand;
           this.demand = demand;
           this.tier = tier;
    }


    /**
     * Gets the summation value for this Registers.
     * 
     * @return summation
     */
    public com.cannontech.multispeak.Summation getSummation() {
        return summation;
    }


    /**
     * Sets the summation value for this Registers.
     * 
     * @param summation
     */
    public void setSummation(com.cannontech.multispeak.Summation summation) {
        this.summation = summation;
    }


    /**
     * Gets the coincidentalValue value for this Registers.
     * 
     * @return coincidentalValue
     */
    public com.cannontech.multispeak.CoincidentalValue getCoincidentalValue() {
        return coincidentalValue;
    }


    /**
     * Sets the coincidentalValue value for this Registers.
     * 
     * @param coincidentalValue
     */
    public void setCoincidentalValue(com.cannontech.multispeak.CoincidentalValue coincidentalValue) {
        this.coincidentalValue = coincidentalValue;
    }


    /**
     * Gets the cumDemand value for this Registers.
     * 
     * @return cumDemand
     */
    public com.cannontech.multispeak.CumDemand getCumDemand() {
        return cumDemand;
    }


    /**
     * Sets the cumDemand value for this Registers.
     * 
     * @param cumDemand
     */
    public void setCumDemand(com.cannontech.multispeak.CumDemand cumDemand) {
        this.cumDemand = cumDemand;
    }


    /**
     * Gets the demand value for this Registers.
     * 
     * @return demand
     */
    public com.cannontech.multispeak.FlowDemand getDemand() {
        return demand;
    }


    /**
     * Sets the demand value for this Registers.
     * 
     * @param demand
     */
    public void setDemand(com.cannontech.multispeak.FlowDemand demand) {
        this.demand = demand;
    }


    /**
     * Gets the tier value for this Registers.
     * 
     * @return tier
     */
    public org.apache.axis.types.UnsignedInt getTier() {
        return tier;
    }


    /**
     * Sets the tier value for this Registers.
     * 
     * @param tier
     */
    public void setTier(org.apache.axis.types.UnsignedInt tier) {
        this.tier = tier;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Registers)) return false;
        Registers other = (Registers) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.summation==null && other.getSummation()==null) || 
             (this.summation!=null &&
              this.summation.equals(other.getSummation()))) &&
            ((this.coincidentalValue==null && other.getCoincidentalValue()==null) || 
             (this.coincidentalValue!=null &&
              this.coincidentalValue.equals(other.getCoincidentalValue()))) &&
            ((this.cumDemand==null && other.getCumDemand()==null) || 
             (this.cumDemand!=null &&
              this.cumDemand.equals(other.getCumDemand()))) &&
            ((this.demand==null && other.getDemand()==null) || 
             (this.demand!=null &&
              this.demand.equals(other.getDemand()))) &&
            ((this.tier==null && other.getTier()==null) || 
             (this.tier!=null &&
              this.tier.equals(other.getTier())));
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
        if (getSummation() != null) {
            _hashCode += getSummation().hashCode();
        }
        if (getCoincidentalValue() != null) {
            _hashCode += getCoincidentalValue().hashCode();
        }
        if (getCumDemand() != null) {
            _hashCode += getCumDemand().hashCode();
        }
        if (getDemand() != null) {
            _hashCode += getDemand().hashCode();
        }
        if (getTier() != null) {
            _hashCode += getTier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Registers.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registers"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("tier");
        attrField.setXmlName(new javax.xml.namespace.QName("", "tier"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("summation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "summation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("coincidentalValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coincidentalValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coincidentalValue"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cumDemand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemand"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("demand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "demand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "flowDemand"));
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
