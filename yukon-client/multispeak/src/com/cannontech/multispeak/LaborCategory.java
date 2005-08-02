/**
 * LaborCategory.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LaborCategory  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String description;
    private java.lang.Float installCost;
    private java.lang.Float retireCost;
    private java.lang.Float salvageCost;

    public LaborCategory() {
    }

    public LaborCategory(
           java.lang.String description,
           java.lang.Float installCost,
           java.lang.Float retireCost,
           java.lang.Float salvageCost) {
           this.description = description;
           this.installCost = installCost;
           this.retireCost = retireCost;
           this.salvageCost = salvageCost;
    }


    /**
     * Gets the description value for this LaborCategory.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this LaborCategory.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the installCost value for this LaborCategory.
     * 
     * @return installCost
     */
    public java.lang.Float getInstallCost() {
        return installCost;
    }


    /**
     * Sets the installCost value for this LaborCategory.
     * 
     * @param installCost
     */
    public void setInstallCost(java.lang.Float installCost) {
        this.installCost = installCost;
    }


    /**
     * Gets the retireCost value for this LaborCategory.
     * 
     * @return retireCost
     */
    public java.lang.Float getRetireCost() {
        return retireCost;
    }


    /**
     * Sets the retireCost value for this LaborCategory.
     * 
     * @param retireCost
     */
    public void setRetireCost(java.lang.Float retireCost) {
        this.retireCost = retireCost;
    }


    /**
     * Gets the salvageCost value for this LaborCategory.
     * 
     * @return salvageCost
     */
    public java.lang.Float getSalvageCost() {
        return salvageCost;
    }


    /**
     * Sets the salvageCost value for this LaborCategory.
     * 
     * @param salvageCost
     */
    public void setSalvageCost(java.lang.Float salvageCost) {
        this.salvageCost = salvageCost;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LaborCategory)) return false;
        LaborCategory other = (LaborCategory) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.installCost==null && other.getInstallCost()==null) || 
             (this.installCost!=null &&
              this.installCost.equals(other.getInstallCost()))) &&
            ((this.retireCost==null && other.getRetireCost()==null) || 
             (this.retireCost!=null &&
              this.retireCost.equals(other.getRetireCost()))) &&
            ((this.salvageCost==null && other.getSalvageCost()==null) || 
             (this.salvageCost!=null &&
              this.salvageCost.equals(other.getSalvageCost())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getInstallCost() != null) {
            _hashCode += getInstallCost().hashCode();
        }
        if (getRetireCost() != null) {
            _hashCode += getRetireCost().hashCode();
        }
        if (getSalvageCost() != null) {
            _hashCode += getSalvageCost().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LaborCategory.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategory"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("installCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "installCost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retireCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "retireCost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salvageCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "salvageCost"));
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
