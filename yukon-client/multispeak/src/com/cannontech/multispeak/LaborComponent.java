/**
 * LaborComponent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LaborComponent  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.lang.String laborCategoryID;
    private java.lang.Float constMH;
    private java.lang.Float retireMH;
    private java.lang.Float salvageMH;
    private java.lang.Float hotConstMH;
    private java.lang.Float hotRetireMH;
    private java.lang.Float hotSalvageMH;

    public LaborComponent() {
    }

    public LaborComponent(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.lang.String laborCategoryID,
           java.lang.Float constMH,
           java.lang.Float retireMH,
           java.lang.Float salvageMH,
           java.lang.Float hotConstMH,
           java.lang.Float hotRetireMH,
           java.lang.Float hotSalvageMH) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.laborCategoryID = laborCategoryID;
           this.constMH = constMH;
           this.retireMH = retireMH;
           this.salvageMH = salvageMH;
           this.hotConstMH = hotConstMH;
           this.hotRetireMH = hotRetireMH;
           this.hotSalvageMH = hotSalvageMH;
    }


    /**
     * Gets the extensions value for this LaborComponent.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this LaborComponent.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this LaborComponent.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this LaborComponent.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the laborCategoryID value for this LaborComponent.
     * 
     * @return laborCategoryID
     */
    public java.lang.String getLaborCategoryID() {
        return laborCategoryID;
    }


    /**
     * Sets the laborCategoryID value for this LaborComponent.
     * 
     * @param laborCategoryID
     */
    public void setLaborCategoryID(java.lang.String laborCategoryID) {
        this.laborCategoryID = laborCategoryID;
    }


    /**
     * Gets the constMH value for this LaborComponent.
     * 
     * @return constMH
     */
    public java.lang.Float getConstMH() {
        return constMH;
    }


    /**
     * Sets the constMH value for this LaborComponent.
     * 
     * @param constMH
     */
    public void setConstMH(java.lang.Float constMH) {
        this.constMH = constMH;
    }


    /**
     * Gets the retireMH value for this LaborComponent.
     * 
     * @return retireMH
     */
    public java.lang.Float getRetireMH() {
        return retireMH;
    }


    /**
     * Sets the retireMH value for this LaborComponent.
     * 
     * @param retireMH
     */
    public void setRetireMH(java.lang.Float retireMH) {
        this.retireMH = retireMH;
    }


    /**
     * Gets the salvageMH value for this LaborComponent.
     * 
     * @return salvageMH
     */
    public java.lang.Float getSalvageMH() {
        return salvageMH;
    }


    /**
     * Sets the salvageMH value for this LaborComponent.
     * 
     * @param salvageMH
     */
    public void setSalvageMH(java.lang.Float salvageMH) {
        this.salvageMH = salvageMH;
    }


    /**
     * Gets the hotConstMH value for this LaborComponent.
     * 
     * @return hotConstMH
     */
    public java.lang.Float getHotConstMH() {
        return hotConstMH;
    }


    /**
     * Sets the hotConstMH value for this LaborComponent.
     * 
     * @param hotConstMH
     */
    public void setHotConstMH(java.lang.Float hotConstMH) {
        this.hotConstMH = hotConstMH;
    }


    /**
     * Gets the hotRetireMH value for this LaborComponent.
     * 
     * @return hotRetireMH
     */
    public java.lang.Float getHotRetireMH() {
        return hotRetireMH;
    }


    /**
     * Sets the hotRetireMH value for this LaborComponent.
     * 
     * @param hotRetireMH
     */
    public void setHotRetireMH(java.lang.Float hotRetireMH) {
        this.hotRetireMH = hotRetireMH;
    }


    /**
     * Gets the hotSalvageMH value for this LaborComponent.
     * 
     * @return hotSalvageMH
     */
    public java.lang.Float getHotSalvageMH() {
        return hotSalvageMH;
    }


    /**
     * Sets the hotSalvageMH value for this LaborComponent.
     * 
     * @param hotSalvageMH
     */
    public void setHotSalvageMH(java.lang.Float hotSalvageMH) {
        this.hotSalvageMH = hotSalvageMH;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LaborComponent)) return false;
        LaborComponent other = (LaborComponent) obj;
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
            ((this.laborCategoryID==null && other.getLaborCategoryID()==null) || 
             (this.laborCategoryID!=null &&
              this.laborCategoryID.equals(other.getLaborCategoryID()))) &&
            ((this.constMH==null && other.getConstMH()==null) || 
             (this.constMH!=null &&
              this.constMH.equals(other.getConstMH()))) &&
            ((this.retireMH==null && other.getRetireMH()==null) || 
             (this.retireMH!=null &&
              this.retireMH.equals(other.getRetireMH()))) &&
            ((this.salvageMH==null && other.getSalvageMH()==null) || 
             (this.salvageMH!=null &&
              this.salvageMH.equals(other.getSalvageMH()))) &&
            ((this.hotConstMH==null && other.getHotConstMH()==null) || 
             (this.hotConstMH!=null &&
              this.hotConstMH.equals(other.getHotConstMH()))) &&
            ((this.hotRetireMH==null && other.getHotRetireMH()==null) || 
             (this.hotRetireMH!=null &&
              this.hotRetireMH.equals(other.getHotRetireMH()))) &&
            ((this.hotSalvageMH==null && other.getHotSalvageMH()==null) || 
             (this.hotSalvageMH!=null &&
              this.hotSalvageMH.equals(other.getHotSalvageMH())));
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
        if (getLaborCategoryID() != null) {
            _hashCode += getLaborCategoryID().hashCode();
        }
        if (getConstMH() != null) {
            _hashCode += getConstMH().hashCode();
        }
        if (getRetireMH() != null) {
            _hashCode += getRetireMH().hashCode();
        }
        if (getSalvageMH() != null) {
            _hashCode += getSalvageMH().hashCode();
        }
        if (getHotConstMH() != null) {
            _hashCode += getHotConstMH().hashCode();
        }
        if (getHotRetireMH() != null) {
            _hashCode += getHotRetireMH().hashCode();
        }
        if (getHotSalvageMH() != null) {
            _hashCode += getHotSalvageMH().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LaborComponent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent"));
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
        elemField.setFieldName("laborCategoryID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategoryID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("constMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retireMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "retireMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salvageMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "salvageMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotConstMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hotConstMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotRetireMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hotRetireMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotSalvageMH");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hotSalvageMH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
