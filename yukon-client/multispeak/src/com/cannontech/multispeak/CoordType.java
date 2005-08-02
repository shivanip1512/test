/**
 * CoordType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CoordType  implements java.io.Serializable {
    private java.math.BigDecimal x;
    private java.math.BigDecimal y;
    private java.math.BigDecimal z;

    public CoordType() {
    }

    public CoordType(
           java.math.BigDecimal x,
           java.math.BigDecimal y,
           java.math.BigDecimal z) {
           this.x = x;
           this.y = y;
           this.z = z;
    }


    /**
     * Gets the x value for this CoordType.
     * 
     * @return x
     */
    public java.math.BigDecimal getX() {
        return x;
    }


    /**
     * Sets the x value for this CoordType.
     * 
     * @param x
     */
    public void setX(java.math.BigDecimal x) {
        this.x = x;
    }


    /**
     * Gets the y value for this CoordType.
     * 
     * @return y
     */
    public java.math.BigDecimal getY() {
        return y;
    }


    /**
     * Sets the y value for this CoordType.
     * 
     * @param y
     */
    public void setY(java.math.BigDecimal y) {
        this.y = y;
    }


    /**
     * Gets the z value for this CoordType.
     * 
     * @return z
     */
    public java.math.BigDecimal getZ() {
        return z;
    }


    /**
     * Sets the z value for this CoordType.
     * 
     * @param z
     */
    public void setZ(java.math.BigDecimal z) {
        this.z = z;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CoordType)) return false;
        CoordType other = (CoordType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.x==null && other.getX()==null) || 
             (this.x!=null &&
              this.x.equals(other.getX()))) &&
            ((this.y==null && other.getY()==null) || 
             (this.y!=null &&
              this.y.equals(other.getY()))) &&
            ((this.z==null && other.getZ()==null) || 
             (this.z!=null &&
              this.z.equals(other.getZ())));
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
        if (getX() != null) {
            _hashCode += getX().hashCode();
        }
        if (getY() != null) {
            _hashCode += getY().hashCode();
        }
        if (getZ() != null) {
            _hashCode += getZ().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CoordType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("x");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "X"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("y");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "Y"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("z");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "Z"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
