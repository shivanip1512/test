/**
 * FeederObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class FeederObject  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String substationName;
    private java.lang.String feederName;
    private java.lang.String feederNo;
    private java.lang.String facilityID;
    private java.lang.String eaLoc;
    private java.lang.String feederColor;
    private java.lang.String bus;

    public FeederObject() {
    }

    public FeederObject(
           java.lang.String substationName,
           java.lang.String feederName,
           java.lang.String feederNo,
           java.lang.String facilityID,
           java.lang.String eaLoc,
           java.lang.String feederColor,
           java.lang.String bus) {
           this.substationName = substationName;
           this.feederName = feederName;
           this.feederNo = feederNo;
           this.facilityID = facilityID;
           this.eaLoc = eaLoc;
           this.feederColor = feederColor;
           this.bus = bus;
    }


    /**
     * Gets the substationName value for this FeederObject.
     * 
     * @return substationName
     */
    public java.lang.String getSubstationName() {
        return substationName;
    }


    /**
     * Sets the substationName value for this FeederObject.
     * 
     * @param substationName
     */
    public void setSubstationName(java.lang.String substationName) {
        this.substationName = substationName;
    }


    /**
     * Gets the feederName value for this FeederObject.
     * 
     * @return feederName
     */
    public java.lang.String getFeederName() {
        return feederName;
    }


    /**
     * Sets the feederName value for this FeederObject.
     * 
     * @param feederName
     */
    public void setFeederName(java.lang.String feederName) {
        this.feederName = feederName;
    }


    /**
     * Gets the feederNo value for this FeederObject.
     * 
     * @return feederNo
     */
    public java.lang.String getFeederNo() {
        return feederNo;
    }


    /**
     * Sets the feederNo value for this FeederObject.
     * 
     * @param feederNo
     */
    public void setFeederNo(java.lang.String feederNo) {
        this.feederNo = feederNo;
    }


    /**
     * Gets the facilityID value for this FeederObject.
     * 
     * @return facilityID
     */
    public java.lang.String getFacilityID() {
        return facilityID;
    }


    /**
     * Sets the facilityID value for this FeederObject.
     * 
     * @param facilityID
     */
    public void setFacilityID(java.lang.String facilityID) {
        this.facilityID = facilityID;
    }


    /**
     * Gets the eaLoc value for this FeederObject.
     * 
     * @return eaLoc
     */
    public java.lang.String getEaLoc() {
        return eaLoc;
    }


    /**
     * Sets the eaLoc value for this FeederObject.
     * 
     * @param eaLoc
     */
    public void setEaLoc(java.lang.String eaLoc) {
        this.eaLoc = eaLoc;
    }


    /**
     * Gets the feederColor value for this FeederObject.
     * 
     * @return feederColor
     */
    public java.lang.String getFeederColor() {
        return feederColor;
    }


    /**
     * Sets the feederColor value for this FeederObject.
     * 
     * @param feederColor
     */
    public void setFeederColor(java.lang.String feederColor) {
        this.feederColor = feederColor;
    }


    /**
     * Gets the bus value for this FeederObject.
     * 
     * @return bus
     */
    public java.lang.String getBus() {
        return bus;
    }


    /**
     * Sets the bus value for this FeederObject.
     * 
     * @param bus
     */
    public void setBus(java.lang.String bus) {
        this.bus = bus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FeederObject)) return false;
        FeederObject other = (FeederObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.substationName==null && other.getSubstationName()==null) || 
             (this.substationName!=null &&
              this.substationName.equals(other.getSubstationName()))) &&
            ((this.feederName==null && other.getFeederName()==null) || 
             (this.feederName!=null &&
              this.feederName.equals(other.getFeederName()))) &&
            ((this.feederNo==null && other.getFeederNo()==null) || 
             (this.feederNo!=null &&
              this.feederNo.equals(other.getFeederNo()))) &&
            ((this.facilityID==null && other.getFacilityID()==null) || 
             (this.facilityID!=null &&
              this.facilityID.equals(other.getFacilityID()))) &&
            ((this.eaLoc==null && other.getEaLoc()==null) || 
             (this.eaLoc!=null &&
              this.eaLoc.equals(other.getEaLoc()))) &&
            ((this.feederColor==null && other.getFeederColor()==null) || 
             (this.feederColor!=null &&
              this.feederColor.equals(other.getFeederColor()))) &&
            ((this.bus==null && other.getBus()==null) || 
             (this.bus!=null &&
              this.bus.equals(other.getBus())));
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
        if (getSubstationName() != null) {
            _hashCode += getSubstationName().hashCode();
        }
        if (getFeederName() != null) {
            _hashCode += getFeederName().hashCode();
        }
        if (getFeederNo() != null) {
            _hashCode += getFeederNo().hashCode();
        }
        if (getFacilityID() != null) {
            _hashCode += getFacilityID().hashCode();
        }
        if (getEaLoc() != null) {
            _hashCode += getEaLoc().hashCode();
        }
        if (getFeederColor() != null) {
            _hashCode += getFeederColor().hashCode();
        }
        if (getBus() != null) {
            _hashCode += getBus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FeederObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("facilityID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederColor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederColor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bus"));
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
