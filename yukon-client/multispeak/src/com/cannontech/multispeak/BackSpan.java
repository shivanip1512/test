/**
 * BackSpan.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class BackSpan  implements java.io.Serializable {
    private java.lang.Float length;
    private com.cannontech.multispeak.LengthUnit lengthUnit;
    private java.lang.String stationID;
    private com.cannontech.multispeak.UnitActn unitAction;

    public BackSpan() {
    }

    public BackSpan(
           java.lang.Float length,
           com.cannontech.multispeak.LengthUnit lengthUnit,
           java.lang.String stationID,
           com.cannontech.multispeak.UnitActn unitAction) {
           this.length = length;
           this.lengthUnit = lengthUnit;
           this.stationID = stationID;
           this.unitAction = unitAction;
    }


    /**
     * Gets the length value for this BackSpan.
     * 
     * @return length
     */
    public java.lang.Float getLength() {
        return length;
    }


    /**
     * Sets the length value for this BackSpan.
     * 
     * @param length
     */
    public void setLength(java.lang.Float length) {
        this.length = length;
    }


    /**
     * Gets the lengthUnit value for this BackSpan.
     * 
     * @return lengthUnit
     */
    public com.cannontech.multispeak.LengthUnit getLengthUnit() {
        return lengthUnit;
    }


    /**
     * Sets the lengthUnit value for this BackSpan.
     * 
     * @param lengthUnit
     */
    public void setLengthUnit(com.cannontech.multispeak.LengthUnit lengthUnit) {
        this.lengthUnit = lengthUnit;
    }


    /**
     * Gets the stationID value for this BackSpan.
     * 
     * @return stationID
     */
    public java.lang.String getStationID() {
        return stationID;
    }


    /**
     * Sets the stationID value for this BackSpan.
     * 
     * @param stationID
     */
    public void setStationID(java.lang.String stationID) {
        this.stationID = stationID;
    }


    /**
     * Gets the unitAction value for this BackSpan.
     * 
     * @return unitAction
     */
    public com.cannontech.multispeak.UnitActn getUnitAction() {
        return unitAction;
    }


    /**
     * Sets the unitAction value for this BackSpan.
     * 
     * @param unitAction
     */
    public void setUnitAction(com.cannontech.multispeak.UnitActn unitAction) {
        this.unitAction = unitAction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BackSpan)) return false;
        BackSpan other = (BackSpan) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.length==null && other.getLength()==null) || 
             (this.length!=null &&
              this.length.equals(other.getLength()))) &&
            ((this.lengthUnit==null && other.getLengthUnit()==null) || 
             (this.lengthUnit!=null &&
              this.lengthUnit.equals(other.getLengthUnit()))) &&
            ((this.stationID==null && other.getStationID()==null) || 
             (this.stationID!=null &&
              this.stationID.equals(other.getStationID()))) &&
            ((this.unitAction==null && other.getUnitAction()==null) || 
             (this.unitAction!=null &&
              this.unitAction.equals(other.getUnitAction())));
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
        if (getLength() != null) {
            _hashCode += getLength().hashCode();
        }
        if (getLengthUnit() != null) {
            _hashCode += getLengthUnit().hashCode();
        }
        if (getStationID() != null) {
            _hashCode += getStationID().hashCode();
        }
        if (getUnitAction() != null) {
            _hashCode += getUnitAction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BackSpan.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpan"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("length");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "length"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lengthUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn"));
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
