/**
 * MaterialManagementAssembly.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MaterialManagementAssembly  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String unitCode;

    private java.lang.String unDescr;

    private java.lang.Float unMatCost;

    private com.cannontech.multispeak.deploy.service.UnType unType;

    private com.cannontech.multispeak.deploy.service.CPR[] CPRList;

    private com.cannontech.multispeak.deploy.service.MaterialComponent[] materialComponentList;

    private com.cannontech.multispeak.deploy.service.LaborComponent[] laborComponentList;

    public MaterialManagementAssembly() {
    }

    public MaterialManagementAssembly(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String unitCode,
           java.lang.String unDescr,
           java.lang.Float unMatCost,
           com.cannontech.multispeak.deploy.service.UnType unType,
           com.cannontech.multispeak.deploy.service.CPR[] CPRList,
           com.cannontech.multispeak.deploy.service.MaterialComponent[] materialComponentList,
           com.cannontech.multispeak.deploy.service.LaborComponent[] laborComponentList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
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
    public com.cannontech.multispeak.deploy.service.UnType getUnType() {
        return unType;
    }


    /**
     * Sets the unType value for this MaterialManagementAssembly.
     * 
     * @param unType
     */
    public void setUnType(com.cannontech.multispeak.deploy.service.UnType unType) {
        this.unType = unType;
    }


    /**
     * Gets the CPRList value for this MaterialManagementAssembly.
     * 
     * @return CPRList
     */
    public com.cannontech.multispeak.deploy.service.CPR[] getCPRList() {
        return CPRList;
    }


    /**
     * Sets the CPRList value for this MaterialManagementAssembly.
     * 
     * @param CPRList
     */
    public void setCPRList(com.cannontech.multispeak.deploy.service.CPR[] CPRList) {
        this.CPRList = CPRList;
    }


    /**
     * Gets the materialComponentList value for this MaterialManagementAssembly.
     * 
     * @return materialComponentList
     */
    public com.cannontech.multispeak.deploy.service.MaterialComponent[] getMaterialComponentList() {
        return materialComponentList;
    }


    /**
     * Sets the materialComponentList value for this MaterialManagementAssembly.
     * 
     * @param materialComponentList
     */
    public void setMaterialComponentList(com.cannontech.multispeak.deploy.service.MaterialComponent[] materialComponentList) {
        this.materialComponentList = materialComponentList;
    }


    /**
     * Gets the laborComponentList value for this MaterialManagementAssembly.
     * 
     * @return laborComponentList
     */
    public com.cannontech.multispeak.deploy.service.LaborComponent[] getLaborComponentList() {
        return laborComponentList;
    }


    /**
     * Sets the laborComponentList value for this MaterialManagementAssembly.
     * 
     * @param laborComponentList
     */
    public void setLaborComponentList(com.cannontech.multispeak.deploy.service.LaborComponent[] laborComponentList) {
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
              java.util.Arrays.equals(this.CPRList, other.getCPRList()))) &&
            ((this.materialComponentList==null && other.getMaterialComponentList()==null) || 
             (this.materialComponentList!=null &&
              java.util.Arrays.equals(this.materialComponentList, other.getMaterialComponentList()))) &&
            ((this.laborComponentList==null && other.getLaborComponentList()==null) || 
             (this.laborComponentList!=null &&
              java.util.Arrays.equals(this.laborComponentList, other.getLaborComponentList())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCPRList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCPRList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMaterialComponentList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMaterialComponentList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMaterialComponentList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLaborComponentList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLaborComponentList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLaborComponentList(), i);
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialComponentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialComponent"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborComponentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborComponent"));
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
