/**
 * Timesheet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Timesheet  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.TimePeriod timePeriod;
    private com.cannontech.multispeak.ArrayOfEmployeeTimeRecord employeeTimeRecordList;
    private com.cannontech.multispeak.ArrayOfJobWorked jobWorkedList;

    public Timesheet() {
    }

    public Timesheet(
           com.cannontech.multispeak.TimePeriod timePeriod,
           com.cannontech.multispeak.ArrayOfEmployeeTimeRecord employeeTimeRecordList,
           com.cannontech.multispeak.ArrayOfJobWorked jobWorkedList) {
           this.timePeriod = timePeriod;
           this.employeeTimeRecordList = employeeTimeRecordList;
           this.jobWorkedList = jobWorkedList;
    }


    /**
     * Gets the timePeriod value for this Timesheet.
     * 
     * @return timePeriod
     */
    public com.cannontech.multispeak.TimePeriod getTimePeriod() {
        return timePeriod;
    }


    /**
     * Sets the timePeriod value for this Timesheet.
     * 
     * @param timePeriod
     */
    public void setTimePeriod(com.cannontech.multispeak.TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }


    /**
     * Gets the employeeTimeRecordList value for this Timesheet.
     * 
     * @return employeeTimeRecordList
     */
    public com.cannontech.multispeak.ArrayOfEmployeeTimeRecord getEmployeeTimeRecordList() {
        return employeeTimeRecordList;
    }


    /**
     * Sets the employeeTimeRecordList value for this Timesheet.
     * 
     * @param employeeTimeRecordList
     */
    public void setEmployeeTimeRecordList(com.cannontech.multispeak.ArrayOfEmployeeTimeRecord employeeTimeRecordList) {
        this.employeeTimeRecordList = employeeTimeRecordList;
    }


    /**
     * Gets the jobWorkedList value for this Timesheet.
     * 
     * @return jobWorkedList
     */
    public com.cannontech.multispeak.ArrayOfJobWorked getJobWorkedList() {
        return jobWorkedList;
    }


    /**
     * Sets the jobWorkedList value for this Timesheet.
     * 
     * @param jobWorkedList
     */
    public void setJobWorkedList(com.cannontech.multispeak.ArrayOfJobWorked jobWorkedList) {
        this.jobWorkedList = jobWorkedList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Timesheet)) return false;
        Timesheet other = (Timesheet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.timePeriod==null && other.getTimePeriod()==null) || 
             (this.timePeriod!=null &&
              this.timePeriod.equals(other.getTimePeriod()))) &&
            ((this.employeeTimeRecordList==null && other.getEmployeeTimeRecordList()==null) || 
             (this.employeeTimeRecordList!=null &&
              this.employeeTimeRecordList.equals(other.getEmployeeTimeRecordList()))) &&
            ((this.jobWorkedList==null && other.getJobWorkedList()==null) || 
             (this.jobWorkedList!=null &&
              this.jobWorkedList.equals(other.getJobWorkedList())));
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
        if (getTimePeriod() != null) {
            _hashCode += getTimePeriod().hashCode();
        }
        if (getEmployeeTimeRecordList() != null) {
            _hashCode += getEmployeeTimeRecordList().hashCode();
        }
        if (getJobWorkedList() != null) {
            _hashCode += getJobWorkedList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Timesheet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timesheet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timePeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeTimeRecordList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecordList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployeeTimeRecord"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobWorkedList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorkedList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJobWorked"));
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
