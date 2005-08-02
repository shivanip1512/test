/**
 * OhPrimaryLine.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OhPrimaryLine  extends com.cannontech.multispeak.MspElectricLine  implements java.io.Serializable {
    private java.lang.Float operVolt;
    private org.apache.axis.types.NonNegativeInteger condPerPhase;
    private java.lang.String lengthSrc;

    public OhPrimaryLine() {
    }

    public OhPrimaryLine(
           java.lang.Float operVolt,
           org.apache.axis.types.NonNegativeInteger condPerPhase,
           java.lang.String lengthSrc) {
           this.operVolt = operVolt;
           this.condPerPhase = condPerPhase;
           this.lengthSrc = lengthSrc;
    }


    /**
     * Gets the operVolt value for this OhPrimaryLine.
     * 
     * @return operVolt
     */
    public java.lang.Float getOperVolt() {
        return operVolt;
    }


    /**
     * Sets the operVolt value for this OhPrimaryLine.
     * 
     * @param operVolt
     */
    public void setOperVolt(java.lang.Float operVolt) {
        this.operVolt = operVolt;
    }


    /**
     * Gets the condPerPhase value for this OhPrimaryLine.
     * 
     * @return condPerPhase
     */
    public org.apache.axis.types.NonNegativeInteger getCondPerPhase() {
        return condPerPhase;
    }


    /**
     * Sets the condPerPhase value for this OhPrimaryLine.
     * 
     * @param condPerPhase
     */
    public void setCondPerPhase(org.apache.axis.types.NonNegativeInteger condPerPhase) {
        this.condPerPhase = condPerPhase;
    }


    /**
     * Gets the lengthSrc value for this OhPrimaryLine.
     * 
     * @return lengthSrc
     */
    public java.lang.String getLengthSrc() {
        return lengthSrc;
    }


    /**
     * Sets the lengthSrc value for this OhPrimaryLine.
     * 
     * @param lengthSrc
     */
    public void setLengthSrc(java.lang.String lengthSrc) {
        this.lengthSrc = lengthSrc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OhPrimaryLine)) return false;
        OhPrimaryLine other = (OhPrimaryLine) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.operVolt==null && other.getOperVolt()==null) || 
             (this.operVolt!=null &&
              this.operVolt.equals(other.getOperVolt()))) &&
            ((this.condPerPhase==null && other.getCondPerPhase()==null) || 
             (this.condPerPhase!=null &&
              this.condPerPhase.equals(other.getCondPerPhase()))) &&
            ((this.lengthSrc==null && other.getLengthSrc()==null) || 
             (this.lengthSrc!=null &&
              this.lengthSrc.equals(other.getLengthSrc())));
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
        if (getOperVolt() != null) {
            _hashCode += getOperVolt().hashCode();
        }
        if (getCondPerPhase() != null) {
            _hashCode += getCondPerPhase().hashCode();
        }
        if (getLengthSrc() != null) {
            _hashCode += getLengthSrc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OhPrimaryLine.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohPrimaryLine"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "operVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("condPerPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "condPerPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lengthSrc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthSrc"));
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
