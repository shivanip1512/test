/**
 * Riser.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Riser  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.String constType;
    private java.lang.Long riserHeight;
    private java.lang.String material;

    public Riser() {
    }

    public Riser(
           java.lang.String constType,
           java.lang.Long riserHeight,
           java.lang.String material) {
           this.constType = constType;
           this.riserHeight = riserHeight;
           this.material = material;
    }


    /**
     * Gets the constType value for this Riser.
     * 
     * @return constType
     */
    public java.lang.String getConstType() {
        return constType;
    }


    /**
     * Sets the constType value for this Riser.
     * 
     * @param constType
     */
    public void setConstType(java.lang.String constType) {
        this.constType = constType;
    }


    /**
     * Gets the riserHeight value for this Riser.
     * 
     * @return riserHeight
     */
    public java.lang.Long getRiserHeight() {
        return riserHeight;
    }


    /**
     * Sets the riserHeight value for this Riser.
     * 
     * @param riserHeight
     */
    public void setRiserHeight(java.lang.Long riserHeight) {
        this.riserHeight = riserHeight;
    }


    /**
     * Gets the material value for this Riser.
     * 
     * @return material
     */
    public java.lang.String getMaterial() {
        return material;
    }


    /**
     * Sets the material value for this Riser.
     * 
     * @param material
     */
    public void setMaterial(java.lang.String material) {
        this.material = material;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Riser)) return false;
        Riser other = (Riser) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.constType==null && other.getConstType()==null) || 
             (this.constType!=null &&
              this.constType.equals(other.getConstType()))) &&
            ((this.riserHeight==null && other.getRiserHeight()==null) || 
             (this.riserHeight!=null &&
              this.riserHeight.equals(other.getRiserHeight()))) &&
            ((this.material==null && other.getMaterial()==null) || 
             (this.material!=null &&
              this.material.equals(other.getMaterial())));
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
        if (getConstType() != null) {
            _hashCode += getConstType().hashCode();
        }
        if (getRiserHeight() != null) {
            _hashCode += getRiserHeight().hashCode();
        }
        if (getMaterial() != null) {
            _hashCode += getMaterial().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Riser.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riser"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("constType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riserHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riserHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("material");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "material"));
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
