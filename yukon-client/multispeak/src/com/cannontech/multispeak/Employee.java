/**
 * Employee.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Employee  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String employeeName;
    private java.lang.String contactInfo;
    private java.lang.Boolean isEmployee;
    private java.lang.String skillType;

    public Employee() {
    }

    public Employee(
           java.lang.String employeeName,
           java.lang.String contactInfo,
           java.lang.Boolean isEmployee,
           java.lang.String skillType) {
           this.employeeName = employeeName;
           this.contactInfo = contactInfo;
           this.isEmployee = isEmployee;
           this.skillType = skillType;
    }


    /**
     * Gets the employeeName value for this Employee.
     * 
     * @return employeeName
     */
    public java.lang.String getEmployeeName() {
        return employeeName;
    }


    /**
     * Sets the employeeName value for this Employee.
     * 
     * @param employeeName
     */
    public void setEmployeeName(java.lang.String employeeName) {
        this.employeeName = employeeName;
    }


    /**
     * Gets the contactInfo value for this Employee.
     * 
     * @return contactInfo
     */
    public java.lang.String getContactInfo() {
        return contactInfo;
    }


    /**
     * Sets the contactInfo value for this Employee.
     * 
     * @param contactInfo
     */
    public void setContactInfo(java.lang.String contactInfo) {
        this.contactInfo = contactInfo;
    }


    /**
     * Gets the isEmployee value for this Employee.
     * 
     * @return isEmployee
     */
    public java.lang.Boolean getIsEmployee() {
        return isEmployee;
    }


    /**
     * Sets the isEmployee value for this Employee.
     * 
     * @param isEmployee
     */
    public void setIsEmployee(java.lang.Boolean isEmployee) {
        this.isEmployee = isEmployee;
    }


    /**
     * Gets the skillType value for this Employee.
     * 
     * @return skillType
     */
    public java.lang.String getSkillType() {
        return skillType;
    }


    /**
     * Sets the skillType value for this Employee.
     * 
     * @param skillType
     */
    public void setSkillType(java.lang.String skillType) {
        this.skillType = skillType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Employee)) return false;
        Employee other = (Employee) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.employeeName==null && other.getEmployeeName()==null) || 
             (this.employeeName!=null &&
              this.employeeName.equals(other.getEmployeeName()))) &&
            ((this.contactInfo==null && other.getContactInfo()==null) || 
             (this.contactInfo!=null &&
              this.contactInfo.equals(other.getContactInfo()))) &&
            ((this.isEmployee==null && other.getIsEmployee()==null) || 
             (this.isEmployee!=null &&
              this.isEmployee.equals(other.getIsEmployee()))) &&
            ((this.skillType==null && other.getSkillType()==null) || 
             (this.skillType!=null &&
              this.skillType.equals(other.getSkillType())));
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
        if (getEmployeeName() != null) {
            _hashCode += getEmployeeName().hashCode();
        }
        if (getContactInfo() != null) {
            _hashCode += getContactInfo().hashCode();
        }
        if (getIsEmployee() != null) {
            _hashCode += getIsEmployee().hashCode();
        }
        if (getSkillType() != null) {
            _hashCode += getSkillType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Employee.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isEmployee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isEmployee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("skillType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "skillType"));
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
