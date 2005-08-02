/**
 * Assembly.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Assembly  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.lang.String unitCode;
    private com.cannontech.multispeak.UnitActn unitActn;
    private com.cannontech.multispeak.SpanTyp spanTyp;
    private java.lang.Float unitLength;
    private java.lang.String poleVar;
    private java.lang.String wireVar;
    private java.lang.String neutVar;
    private java.lang.String miscVar;
    private java.lang.Float quantity;
    private java.lang.Long poleHeight;
    private com.cannontech.multispeak.PoleClass poleClass;
    private com.cannontech.multispeak.PoleType poleType;

    public Assembly() {
    }

    public Assembly(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.lang.String unitCode,
           com.cannontech.multispeak.UnitActn unitActn,
           com.cannontech.multispeak.SpanTyp spanTyp,
           java.lang.Float unitLength,
           java.lang.String poleVar,
           java.lang.String wireVar,
           java.lang.String neutVar,
           java.lang.String miscVar,
           java.lang.Float quantity,
           java.lang.Long poleHeight,
           com.cannontech.multispeak.PoleClass poleClass,
           com.cannontech.multispeak.PoleType poleType) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.unitCode = unitCode;
           this.unitActn = unitActn;
           this.spanTyp = spanTyp;
           this.unitLength = unitLength;
           this.poleVar = poleVar;
           this.wireVar = wireVar;
           this.neutVar = neutVar;
           this.miscVar = miscVar;
           this.quantity = quantity;
           this.poleHeight = poleHeight;
           this.poleClass = poleClass;
           this.poleType = poleType;
    }


    /**
     * Gets the extensions value for this Assembly.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this Assembly.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this Assembly.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this Assembly.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the unitCode value for this Assembly.
     * 
     * @return unitCode
     */
    public java.lang.String getUnitCode() {
        return unitCode;
    }


    /**
     * Sets the unitCode value for this Assembly.
     * 
     * @param unitCode
     */
    public void setUnitCode(java.lang.String unitCode) {
        this.unitCode = unitCode;
    }


    /**
     * Gets the unitActn value for this Assembly.
     * 
     * @return unitActn
     */
    public com.cannontech.multispeak.UnitActn getUnitActn() {
        return unitActn;
    }


    /**
     * Sets the unitActn value for this Assembly.
     * 
     * @param unitActn
     */
    public void setUnitActn(com.cannontech.multispeak.UnitActn unitActn) {
        this.unitActn = unitActn;
    }


    /**
     * Gets the spanTyp value for this Assembly.
     * 
     * @return spanTyp
     */
    public com.cannontech.multispeak.SpanTyp getSpanTyp() {
        return spanTyp;
    }


    /**
     * Sets the spanTyp value for this Assembly.
     * 
     * @param spanTyp
     */
    public void setSpanTyp(com.cannontech.multispeak.SpanTyp spanTyp) {
        this.spanTyp = spanTyp;
    }


    /**
     * Gets the unitLength value for this Assembly.
     * 
     * @return unitLength
     */
    public java.lang.Float getUnitLength() {
        return unitLength;
    }


    /**
     * Sets the unitLength value for this Assembly.
     * 
     * @param unitLength
     */
    public void setUnitLength(java.lang.Float unitLength) {
        this.unitLength = unitLength;
    }


    /**
     * Gets the poleVar value for this Assembly.
     * 
     * @return poleVar
     */
    public java.lang.String getPoleVar() {
        return poleVar;
    }


    /**
     * Sets the poleVar value for this Assembly.
     * 
     * @param poleVar
     */
    public void setPoleVar(java.lang.String poleVar) {
        this.poleVar = poleVar;
    }


    /**
     * Gets the wireVar value for this Assembly.
     * 
     * @return wireVar
     */
    public java.lang.String getWireVar() {
        return wireVar;
    }


    /**
     * Sets the wireVar value for this Assembly.
     * 
     * @param wireVar
     */
    public void setWireVar(java.lang.String wireVar) {
        this.wireVar = wireVar;
    }


    /**
     * Gets the neutVar value for this Assembly.
     * 
     * @return neutVar
     */
    public java.lang.String getNeutVar() {
        return neutVar;
    }


    /**
     * Sets the neutVar value for this Assembly.
     * 
     * @param neutVar
     */
    public void setNeutVar(java.lang.String neutVar) {
        this.neutVar = neutVar;
    }


    /**
     * Gets the miscVar value for this Assembly.
     * 
     * @return miscVar
     */
    public java.lang.String getMiscVar() {
        return miscVar;
    }


    /**
     * Sets the miscVar value for this Assembly.
     * 
     * @param miscVar
     */
    public void setMiscVar(java.lang.String miscVar) {
        this.miscVar = miscVar;
    }


    /**
     * Gets the quantity value for this Assembly.
     * 
     * @return quantity
     */
    public java.lang.Float getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this Assembly.
     * 
     * @param quantity
     */
    public void setQuantity(java.lang.Float quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the poleHeight value for this Assembly.
     * 
     * @return poleHeight
     */
    public java.lang.Long getPoleHeight() {
        return poleHeight;
    }


    /**
     * Sets the poleHeight value for this Assembly.
     * 
     * @param poleHeight
     */
    public void setPoleHeight(java.lang.Long poleHeight) {
        this.poleHeight = poleHeight;
    }


    /**
     * Gets the poleClass value for this Assembly.
     * 
     * @return poleClass
     */
    public com.cannontech.multispeak.PoleClass getPoleClass() {
        return poleClass;
    }


    /**
     * Sets the poleClass value for this Assembly.
     * 
     * @param poleClass
     */
    public void setPoleClass(com.cannontech.multispeak.PoleClass poleClass) {
        this.poleClass = poleClass;
    }


    /**
     * Gets the poleType value for this Assembly.
     * 
     * @return poleType
     */
    public com.cannontech.multispeak.PoleType getPoleType() {
        return poleType;
    }


    /**
     * Sets the poleType value for this Assembly.
     * 
     * @param poleType
     */
    public void setPoleType(com.cannontech.multispeak.PoleType poleType) {
        this.poleType = poleType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Assembly)) return false;
        Assembly other = (Assembly) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.extensionsList==null && other.getExtensionsList()==null) || 
             (this.extensionsList!=null &&
              this.extensionsList.equals(other.getExtensionsList()))) &&
            ((this.unitCode==null && other.getUnitCode()==null) || 
             (this.unitCode!=null &&
              this.unitCode.equals(other.getUnitCode()))) &&
            ((this.unitActn==null && other.getUnitActn()==null) || 
             (this.unitActn!=null &&
              this.unitActn.equals(other.getUnitActn()))) &&
            ((this.spanTyp==null && other.getSpanTyp()==null) || 
             (this.spanTyp!=null &&
              this.spanTyp.equals(other.getSpanTyp()))) &&
            ((this.unitLength==null && other.getUnitLength()==null) || 
             (this.unitLength!=null &&
              this.unitLength.equals(other.getUnitLength()))) &&
            ((this.poleVar==null && other.getPoleVar()==null) || 
             (this.poleVar!=null &&
              this.poleVar.equals(other.getPoleVar()))) &&
            ((this.wireVar==null && other.getWireVar()==null) || 
             (this.wireVar!=null &&
              this.wireVar.equals(other.getWireVar()))) &&
            ((this.neutVar==null && other.getNeutVar()==null) || 
             (this.neutVar!=null &&
              this.neutVar.equals(other.getNeutVar()))) &&
            ((this.miscVar==null && other.getMiscVar()==null) || 
             (this.miscVar!=null &&
              this.miscVar.equals(other.getMiscVar()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.poleHeight==null && other.getPoleHeight()==null) || 
             (this.poleHeight!=null &&
              this.poleHeight.equals(other.getPoleHeight()))) &&
            ((this.poleClass==null && other.getPoleClass()==null) || 
             (this.poleClass!=null &&
              this.poleClass.equals(other.getPoleClass()))) &&
            ((this.poleType==null && other.getPoleType()==null) || 
             (this.poleType!=null &&
              this.poleType.equals(other.getPoleType())));
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
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getExtensionsList() != null) {
            _hashCode += getExtensionsList().hashCode();
        }
        if (getUnitCode() != null) {
            _hashCode += getUnitCode().hashCode();
        }
        if (getUnitActn() != null) {
            _hashCode += getUnitActn().hashCode();
        }
        if (getSpanTyp() != null) {
            _hashCode += getSpanTyp().hashCode();
        }
        if (getUnitLength() != null) {
            _hashCode += getUnitLength().hashCode();
        }
        if (getPoleVar() != null) {
            _hashCode += getPoleVar().hashCode();
        }
        if (getWireVar() != null) {
            _hashCode += getWireVar().hashCode();
        }
        if (getNeutVar() != null) {
            _hashCode += getNeutVar().hashCode();
        }
        if (getMiscVar() != null) {
            _hashCode += getMiscVar().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getPoleHeight() != null) {
            _hashCode += getPoleHeight().hashCode();
        }
        if (getPoleClass() != null) {
            _hashCode += getPoleClass().hashCode();
        }
        if (getPoleType() != null) {
            _hashCode += getPoleType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Assembly.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitActn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("spanTyp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanTyp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spanTyp"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitLength");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitLength"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleVar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleVar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wireVar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wireVar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("neutVar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "neutVar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("miscVar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "miscVar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType"));
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
