/**
 * MaterialManagementAssembly.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MaterialManagementAssembly  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String unitCode;
    private java.lang.String unDescr;
    private java.lang.Float unMatCost;
    private com.cannontech.multispeak.UnType unType;
    private com.cannontech.multispeak.ArrayOfCPR CPRList;
    private com.cannontech.multispeak.ArrayOfMaterialComponent materialComponentList;
    private com.cannontech.multispeak.ArrayOfLaborComponent laborComponentList;

    public MaterialManagementAssembly() {
    }

    public MaterialManagementAssembly(
           java.lang.String unitCode,
           java.lang.String unDescr,
           java.lang.Float unMatCost,
           com.cannontech.multispeak.UnType unType,
           com.cannontech.multispeak.ArrayOfCPR CPRList,
           com.cannontech.multispeak.ArrayOfMaterialComponent materialComponentList,
           com.cannontech.multispeak.ArrayOfLaborComponent laborComponentList) {
           this.unitCode = unitCode;
           this.unDescr = unDescr;
           this.unMatCost = unMatCost;
           this.unType = unType;
           this.CPRList = CPRList;
           this.materialComponentList = materialComponentList;
           this.laborComponentList = laborComponentList;
    }


    /**
     * Gets the unitCode value for this MaterialManagementAssembly.
     * 
     * @return unitCode
     */
    public java.lang.String getUnitCode() {
        return unitCode;
    }


    /**
     * Sets the unitCode value for this MaterialManagementAssembly.
     * 
     * @param unitCode
     */
    public void setUnitCode(java.lang.String unitCode) {
        this.unitCode = unitCode;
    }


    /**
     * Gets the unDescr value for this MaterialManagementAssembly.
     * 
     * @return unDescr
     */
    public java.lang.String getUnDescr() {
        return unDescr;
    }


    /**
     * Sets the unDescr value for this MaterialManagementAssembly.
     * 
     * @param unDescr
     */
    public void setUnDescr(java.lang.String unDescr) {
        this.unDescr = unDescr;
    }


    /**
     * Gets the unMatCost value for this MaterialManagementAssembly.
     * 
     * @return unMatCost
     */
    public java.lang.Float getUnMatCost() {
        return unMatCost;
    }


    /**
     * Sets the unMatCost value for this MaterialManagementAssembly.
     * 
     * @param unMatCost
     */
    public void setUnMatCost(java.lang.Float unMatCost) {
        this.unMatCost = unMatCost;
    }


    /**
     * Gets the unType value for this MaterialManagementAssembly.
     * 
     * @return unType
     */
    public com.cannontech.multispeak.UnType getUnType() {
        return unType;
    }


    /**
     * Sets the unType value for this MaterialManagementAssembly.
     * 
     * @param unType
     */
    public void setUnType(com.cannontech.multispeak.UnType unType) {
        this.unType = unType;
    }


    /**
     * Gets the CPRList value for this MaterialManagementAssembly.
     * 
     * @return CPRList
     */
    public com.cannontech.multispeak.ArrayOfCPR getCPRList() {
        return CPRList;
    }


    /**
     * Sets the CPRList value for this MaterialManagementAssembly.
     * 
     * @param CPRList
     */
    public void setCPRList(com.cannontech.multispeak.ArrayOfCPR CPRList) {
        this.CPRList = CPRList;
    }


    /**
     * Gets the materialComponentList value for this MaterialManagementAssembly.
     * 
     * @return materialComponentList
     */
    public com.cannontech.multispeak.ArrayOfMaterialComponent getMaterialComponentList() {
        return materialComponentList;
    }


    /**
     * Sets the materialComponentList value for this MaterialManagementAssembly.
     * 
     * @param materialComponentList
     */
    public void setMaterialComponentList(com.cannontech.multispeak.ArrayOfMaterialComponent materialComponentList) {
        this.materialComponentList = materialComponentList;
    }


    /**
     * Gets the laborComponentList value for this MaterialManagementAssembly.
     * 
     * @return laborComponentList
     */
    public com.cannontech.multispeak.ArrayOfLaborComponent getLaborComponentList() {
        return laborComponentList;
    }


    /**
     * Sets the laborComponentList value for this MaterialManagementAssembly.
     * 
     * @param laborComponentList
     */
    public void setLaborComponentList(com.cannontech.multispeak.ArrayOfLaborComponent laborComponentList) {
        this.laborComponentList = laborComponentList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaterialManagementAssembly)) return false;
        MaterialManagementAssembly other = (MaterialManagementAssembly) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.unitCode==null && other.getUnitCode()==null) || 
             (this.unitCode!=null &&
              this.unitCode.equals(other.getUnitCode()))) &&
            ((this.unDescr==null && other.getUnDescr()==null) || 
             (this.unDescr!=null &&
              this.unDescr.equals(other.getUnDescr()))) &&
            ((this.unMatCost==null && other.getUnMatCost()==null) || 
             (this.unMatCost!=null &&
              this.unMatCost.equals(other.getUnMatCost()))) &&
            ((this.unType==null && other.getUnType()==null) || 
             (this.unType!=null &&
              this.unType.equals(other.getUnType()))) &&
            ((this.CPRList==null && other.getCPRList()==null) || 
             (this.CPRList!=null &&
              this.CPRList.equals(other.getCPRList()))) &&
            ((this.materialComponentList==null && other.getMaterialComponentList()==null) || 
             (this.materialComponentList!=null &&
              this.materialComponentList.equals(other.getMaterialComponentList()))) &&
            ((this.laborComponentList==null && other.getLaborComponentList()==null) || 
             (this.laborComponentList!=null &&
              this.laborComponentList.equals(other.getLaborComponentList())));
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
        if (getUnitCode() != null) {
            _hashCode += getUnitCode().hashCode();
        }
        if (getUnDescr() != null) {
            _hashCode += getUnDescr().hashCode();
        }
        if (getUnMatCost() != null) {
            _hashCode += getUnMatCost().hashCode();
        }
        if (getUnType() != null) {
            _hashCode += getUnType().hashCode();
        }
        if (getCPRList() != null) {
            _hashCode += getCPRList().hashCode();
        }
        if (getMaterialComponentList() != null) {
            _hashCode += getMaterialComponentList().hashCode();
        }
        if (getLaborComponentList() != null) {
            _hashCode += getLaborComponentList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaterialManagementAssembly.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialManagementAssembly"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unDescr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unDescr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unMatCost");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unMatCost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CPRList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPRList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCPR"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialComponentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMaterialComponent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborComponentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfLaborComponent"));
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
