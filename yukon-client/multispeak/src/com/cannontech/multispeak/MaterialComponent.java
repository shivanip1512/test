/**
 * MaterialComponent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MaterialComponent  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.lang.String stockNumber;
    private java.lang.Float itemCount;
    private java.lang.String poleVar;
    private java.lang.String wireVar;
    private java.lang.String neutVar;
    private java.lang.String miscVar;

    public MaterialComponent() {
    }

    public MaterialComponent(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.lang.String stockNumber,
           java.lang.Float itemCount,
           java.lang.String poleVar,
           java.lang.String wireVar,
           java.lang.String neutVar,
           java.lang.String miscVar) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.stockNumber = stockNumber;
           this.itemCount = itemCount;
           this.poleVar = poleVar;
           this.wireVar = wireVar;
           this.neutVar = neutVar;
           this.miscVar = miscVar;
    }


    /**
     * Gets the extensions value for this MaterialComponent.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this MaterialComponent.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this MaterialComponent.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this MaterialComponent.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the stockNumber value for this MaterialComponent.
     * 
     * @return stockNumber
     */
    public java.lang.String getStockNumber() {
        return stockNumber;
    }


    /**
     * Sets the stockNumber value for this MaterialComponent.
     * 
     * @param stockNumber
     */
    public void setStockNumber(java.lang.String stockNumber) {
        this.stockNumber = stockNumber;
    }


    /**
     * Gets the itemCount value for this MaterialComponent.
     * 
     * @return itemCount
     */
    public java.lang.Float getItemCount() {
        return itemCount;
    }


    /**
     * Sets the itemCount value for this MaterialComponent.
     * 
     * @param itemCount
     */
    public void setItemCount(java.lang.Float itemCount) {
        this.itemCount = itemCount;
    }


    /**
     * Gets the poleVar value for this MaterialComponent.
     * 
     * @return poleVar
     */
    public java.lang.String getPoleVar() {
        return poleVar;
    }


    /**
     * Sets the poleVar value for this MaterialComponent.
     * 
     * @param poleVar
     */
    public void setPoleVar(java.lang.String poleVar) {
        this.poleVar = poleVar;
    }


    /**
     * Gets the wireVar value for this MaterialComponent.
     * 
     * @return wireVar
     */
    public java.lang.String getWireVar() {
        return wireVar;
    }


    /**
     * Sets the wireVar value for this MaterialComponent.
     * 
     * @param wireVar
     */
    public void setWireVar(java.lang.String wireVar) {
        this.wireVar = wireVar;
    }


    /**
     * Gets the neutVar value for this MaterialComponent.
     * 
     * @return neutVar
     */
    public java.lang.String getNeutVar() {
        return neutVar;
    }


    /**
     * Sets the neutVar value for this MaterialComponent.
     * 
     * @param neutVar
     */
    public void setNeutVar(java.lang.String neutVar) {
        this.neutVar = neutVar;
    }


    /**
     * Gets the miscVar value for this MaterialComponent.
     * 
     * @return miscVar
     */
    public java.lang.String getMiscVar() {
        return miscVar;
    }


    /**
     * Sets the miscVar value for this MaterialComponent.
     * 
     * @param miscVar
     */
    public void setMiscVar(java.lang.String miscVar) {
        this.miscVar = miscVar;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaterialComponent)) return false;
        MaterialComponent other = (MaterialComponent) obj;
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
            ((this.stockNumber==null && other.getStockNumber()==null) || 
             (this.stockNumber!=null &&
              this.stockNumber.equals(other.getStockNumber()))) &&
            ((this.itemCount==null && other.getItemCount()==null) || 
             (this.itemCount!=null &&
              this.itemCount.equals(other.getItemCount()))) &&
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
              this.miscVar.equals(other.getMiscVar())));
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
        if (getStockNumber() != null) {
            _hashCode += getStockNumber().hashCode();
        }
        if (getItemCount() != null) {
            _hashCode += getItemCount().hashCode();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaterialComponent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent"));
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
        elemField.setFieldName("stockNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stockNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "itemCount"));
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
