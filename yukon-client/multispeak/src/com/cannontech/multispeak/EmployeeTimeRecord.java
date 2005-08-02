/**
 * EmployeeTimeRecord.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class EmployeeTimeRecord  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String employeeID;
    private java.lang.String employeeName;
    private java.lang.String hoursWorked;

    public EmployeeTimeRecord() {
    }

    public EmployeeTimeRecord(
           java.lang.String employeeID,
           java.lang.String employeeName,
           java.lang.String hoursWorked) {
           this.employeeID = employeeID;
           this.employeeName = employeeName;
           this.hoursWorked = hoursWorked;
    }


    /**
     * Gets the employeeID value for this EmployeeTimeRecord.
     * 
     * @return employeeID
     */
    public java.lang.String getEmployeeID() {
        return employeeID;
    }


    /**
     * Sets the employeeID value for this EmployeeTimeRecord.
     * 
     * @param employeeID
     */
    public void setEmployeeID(java.lang.String employeeID) {
        this.employeeID = employeeID;
    }


    /**
     * Gets the employeeName value for this EmployeeTimeRecord.
     * 
     * @return employeeName
     */
    public java.lang.String getEmployeeName() {
        return employeeName;
    }


    /**
     * Sets the employeeName value for this EmployeeTimeRecord.
     * 
     * @param employeeName
     */
    public void setEmployeeName(java.lang.String employeeName) {
        this.employeeName = employeeName;
    }


    /**
     * Gets the hoursWorked value for this EmployeeTimeRecord.
     * 
     * @return hoursWorked
     */
    public java.lang.String getHoursWorked() {
        return hoursWorked;
    }


    /**
     * Sets the hoursWorked value for this EmployeeTimeRecord.
     * 
     * @param hoursWorked
     */
    public void setHoursWorked(java.lang.String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EmployeeTimeRecord)) return false;
        EmployeeTimeRecord other = (EmployeeTimeRecord) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.employeeID==null && other.getEmployeeID()==null) || 
             (this.employeeID!=null &&
              this.employeeID.equals(other.getEmployeeID()))) &&
            ((this.employeeName==null && other.getEmployeeName()==null) || 
             (this.employeeName!=null &&
              this.employeeName.equals(other.getEmployeeName()))) &&
            ((this.hoursWorked==null && other.getHoursWorked()==null) || 
             (this.hoursWorked!=null &&
              this.hoursWorked.equals(other.getHoursWorked())));
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
        if (getEmployeeID() != null) {
            _hashCode += getEmployeeID().hashCode();
        }
        if (getEmployeeName() != null) {
            _hashCode += getEmployeeName().hashCode();
        }
        if (getHoursWorked() != null) {
            _hashCode += getHoursWorked().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EmployeeTimeRecord.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hoursWorked");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hoursWorked"));
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
