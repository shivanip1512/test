/**
 * LineStringType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LineStringType  extends com.cannontech.multispeak.AbstractGeometryType  implements java.io.Serializable {
    private com.cannontech.multispeak.CoordType[] coord;

    public LineStringType() {
    }

    public LineStringType(
           com.cannontech.multispeak.CoordType[] coord) {
           this.coord = coord;
    }


    /**
     * Gets the coord value for this LineStringType.
     * 
     * @return coord
     */
    public com.cannontech.multispeak.CoordType[] getCoord() {
        return coord;
    }


    /**
     * Sets the coord value for this LineStringType.
     * 
     * @param coord
     */
    public void setCoord(com.cannontech.multispeak.CoordType[] coord) {
        this.coord = coord;
    }

    public com.cannontech.multispeak.CoordType getCoord(int i) {
        return this.coord[i];
    }

    public void setCoord(int i, com.cannontech.multispeak.CoordType _value) {
        this.coord[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LineStringType)) return false;
        LineStringType other = (LineStringType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.coord==null && other.getCoord()==null) || 
             (this.coord!=null &&
              java.util.Arrays.equals(this.coord, other.getCoord())));
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
        if (getCoord() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCoord());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCoord(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LineStringType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LineStringType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("coord");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "coord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CoordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
