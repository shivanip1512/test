/**
 * CircuitElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CircuitElement  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String elementName;
    private java.lang.String parentName;
    private com.cannontech.multispeak.CircuitElementElementType elementType;
    private java.lang.String substationName;
    private java.lang.String feederName;
    private java.lang.String feederNo;
    private com.cannontech.multispeak.PhaseCd phase;

    public CircuitElement() {
    }

    public CircuitElement(
           java.lang.String elementName,
           java.lang.String parentName,
           com.cannontech.multispeak.CircuitElementElementType elementType,
           java.lang.String substationName,
           java.lang.String feederName,
           java.lang.String feederNo,
           com.cannontech.multispeak.PhaseCd phase) {
           this.elementName = elementName;
           this.parentName = parentName;
           this.elementType = elementType;
           this.substationName = substationName;
           this.feederName = feederName;
           this.feederNo = feederNo;
           this.phase = phase;
    }


    /**
     * Gets the elementName value for this CircuitElement.
     * 
     * @return elementName
     */
    public java.lang.String getElementName() {
        return elementName;
    }


    /**
     * Sets the elementName value for this CircuitElement.
     * 
     * @param elementName
     */
    public void setElementName(java.lang.String elementName) {
        this.elementName = elementName;
    }


    /**
     * Gets the parentName value for this CircuitElement.
     * 
     * @return parentName
     */
    public java.lang.String getParentName() {
        return parentName;
    }


    /**
     * Sets the parentName value for this CircuitElement.
     * 
     * @param parentName
     */
    public void setParentName(java.lang.String parentName) {
        this.parentName = parentName;
    }


    /**
     * Gets the elementType value for this CircuitElement.
     * 
     * @return elementType
     */
    public com.cannontech.multispeak.CircuitElementElementType getElementType() {
        return elementType;
    }


    /**
     * Sets the elementType value for this CircuitElement.
     * 
     * @param elementType
     */
    public void setElementType(com.cannontech.multispeak.CircuitElementElementType elementType) {
        this.elementType = elementType;
    }


    /**
     * Gets the substationName value for this CircuitElement.
     * 
     * @return substationName
     */
    public java.lang.String getSubstationName() {
        return substationName;
    }


    /**
     * Sets the substationName value for this CircuitElement.
     * 
     * @param substationName
     */
    public void setSubstationName(java.lang.String substationName) {
        this.substationName = substationName;
    }


    /**
     * Gets the feederName value for this CircuitElement.
     * 
     * @return feederName
     */
    public java.lang.String getFeederName() {
        return feederName;
    }


    /**
     * Sets the feederName value for this CircuitElement.
     * 
     * @param feederName
     */
    public void setFeederName(java.lang.String feederName) {
        this.feederName = feederName;
    }


    /**
     * Gets the feederNo value for this CircuitElement.
     * 
     * @return feederNo
     */
    public java.lang.String getFeederNo() {
        return feederNo;
    }


    /**
     * Sets the feederNo value for this CircuitElement.
     * 
     * @param feederNo
     */
    public void setFeederNo(java.lang.String feederNo) {
        this.feederNo = feederNo;
    }


    /**
     * Gets the phase value for this CircuitElement.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this CircuitElement.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CircuitElement)) return false;
        CircuitElement other = (CircuitElement) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.elementName==null && other.getElementName()==null) || 
             (this.elementName!=null &&
              this.elementName.equals(other.getElementName()))) &&
            ((this.parentName==null && other.getParentName()==null) || 
             (this.parentName!=null &&
              this.parentName.equals(other.getParentName()))) &&
            ((this.elementType==null && other.getElementType()==null) || 
             (this.elementType!=null &&
              this.elementType.equals(other.getElementType()))) &&
            ((this.substationName==null && other.getSubstationName()==null) || 
             (this.substationName!=null &&
              this.substationName.equals(other.getSubstationName()))) &&
            ((this.feederName==null && other.getFeederName()==null) || 
             (this.feederName!=null &&
              this.feederName.equals(other.getFeederName()))) &&
            ((this.feederNo==null && other.getFeederNo()==null) || 
             (this.feederNo!=null &&
              this.feederNo.equals(other.getFeederNo()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase())));
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
        if (getElementName() != null) {
            _hashCode += getElementName().hashCode();
        }
        if (getParentName() != null) {
            _hashCode += getParentName().hashCode();
        }
        if (getElementType() != null) {
            _hashCode += getElementType().hashCode();
        }
        if (getSubstationName() != null) {
            _hashCode += getSubstationName().hashCode();
        }
        if (getFeederName() != null) {
            _hashCode += getFeederName().hashCode();
        }
        if (getFeederNo() != null) {
            _hashCode += getFeederNo().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CircuitElement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementElementType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
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
