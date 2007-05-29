/**
 * MeterBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MeterBase  extends com.cannontech.multispeak.service.MspObject  implements java.io.Serializable {
    private java.lang.String meterID;
    private java.lang.String servLoc;
    private java.lang.String premiseID;
    private java.lang.String form;
    private java.lang.String _class;
    private com.cannontech.multispeak.service.BaseType baseType;
    private com.cannontech.multispeak.service.InstrumentTransformers instrumentTransformers;

    public MeterBase() {
    }

    public MeterBase(
           java.lang.String meterID,
           java.lang.String servLoc,
           java.lang.String premiseID,
           java.lang.String form,
           java.lang.String _class,
           com.cannontech.multispeak.service.BaseType baseType,
           com.cannontech.multispeak.service.InstrumentTransformers instrumentTransformers) {
           this.meterID = meterID;
           this.servLoc = servLoc;
           this.premiseID = premiseID;
           this.form = form;
           this._class = _class;
           this.baseType = baseType;
           this.instrumentTransformers = instrumentTransformers;
    }


    /**
     * Gets the meterID value for this MeterBase.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this MeterBase.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the servLoc value for this MeterBase.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this MeterBase.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the premiseID value for this MeterBase.
     * 
     * @return premiseID
     */
    public java.lang.String getPremiseID() {
        return premiseID;
    }


    /**
     * Sets the premiseID value for this MeterBase.
     * 
     * @param premiseID
     */
    public void setPremiseID(java.lang.String premiseID) {
        this.premiseID = premiseID;
    }


    /**
     * Gets the form value for this MeterBase.
     * 
     * @return form
     */
    public java.lang.String getForm() {
        return form;
    }


    /**
     * Sets the form value for this MeterBase.
     * 
     * @param form
     */
    public void setForm(java.lang.String form) {
        this.form = form;
    }


    /**
     * Gets the _class value for this MeterBase.
     * 
     * @return _class
     */
    public java.lang.String get_class() {
        return _class;
    }


    /**
     * Sets the _class value for this MeterBase.
     * 
     * @param _class
     */
    public void set_class(java.lang.String _class) {
        this._class = _class;
    }


    /**
     * Gets the baseType value for this MeterBase.
     * 
     * @return baseType
     */
    public com.cannontech.multispeak.service.BaseType getBaseType() {
        return baseType;
    }


    /**
     * Sets the baseType value for this MeterBase.
     * 
     * @param baseType
     */
    public void setBaseType(com.cannontech.multispeak.service.BaseType baseType) {
        this.baseType = baseType;
    }


    /**
     * Gets the instrumentTransformers value for this MeterBase.
     * 
     * @return instrumentTransformers
     */
    public com.cannontech.multispeak.service.InstrumentTransformers getInstrumentTransformers() {
        return instrumentTransformers;
    }


    /**
     * Sets the instrumentTransformers value for this MeterBase.
     * 
     * @param instrumentTransformers
     */
    public void setInstrumentTransformers(com.cannontech.multispeak.service.InstrumentTransformers instrumentTransformers) {
        this.instrumentTransformers = instrumentTransformers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterBase)) return false;
        MeterBase other = (MeterBase) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.premiseID==null && other.getPremiseID()==null) || 
             (this.premiseID!=null &&
              this.premiseID.equals(other.getPremiseID()))) &&
            ((this.form==null && other.getForm()==null) || 
             (this.form!=null &&
              this.form.equals(other.getForm()))) &&
            ((this._class==null && other.get_class()==null) || 
             (this._class!=null &&
              this._class.equals(other.get_class()))) &&
            ((this.baseType==null && other.getBaseType()==null) || 
             (this.baseType!=null &&
              this.baseType.equals(other.getBaseType()))) &&
            ((this.instrumentTransformers==null && other.getInstrumentTransformers()==null) || 
             (this.instrumentTransformers!=null &&
              this.instrumentTransformers.equals(other.getInstrumentTransformers())));
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
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getPremiseID() != null) {
            _hashCode += getPremiseID().hashCode();
        }
        if (getForm() != null) {
            _hashCode += getForm().hashCode();
        }
        if (get_class() != null) {
            _hashCode += get_class().hashCode();
        }
        if (getBaseType() != null) {
            _hashCode += getBaseType().hashCode();
        }
        if (getInstrumentTransformers() != null) {
            _hashCode += getInstrumentTransformers().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterBase.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterBase"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("premiseID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("_class");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("instrumentTransformers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instrumentTransformers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instrumentTransformers"));
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
