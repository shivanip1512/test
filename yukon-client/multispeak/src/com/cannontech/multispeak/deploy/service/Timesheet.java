/**
 * Timesheet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Timesheet  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.TimePeriod timePeriod;

    private com.cannontech.multispeak.deploy.service.EmployeeTimeRecord[] employeeTimeRecordList;

    private com.cannontech.multispeak.deploy.service.JobWorked[] jobWorkedList;

    public Timesheet() {
    }

    public Timesheet(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.TimePeriod timePeriod,
           com.cannontech.multispeak.deploy.service.EmployeeTimeRecord[] employeeTimeRecordList,
           com.cannontech.multispeak.deploy.service.JobWorked[] jobWorkedList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.timePeriod = timePeriod;
        this.employeeTimeRecordList = employeeTimeRecordList;
        this.jobWorkedList = jobWorkedList;
    }


    /**
     * Gets the timePeriod value for this Timesheet.
     * 
     * @return timePeriod
     */
    public com.cannontech.multispeak.deploy.service.TimePeriod getTimePeriod() {
        return timePeriod;
    }


    /**
     * Sets the timePeriod value for this Timesheet.
     * 
     * @param timePeriod
     */
    public void setTimePeriod(com.cannontech.multispeak.deploy.service.TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }


    /**
     * Gets the employeeTimeRecordList value for this Timesheet.
     * 
     * @return employeeTimeRecordList
     */
    public com.cannontech.multispeak.deploy.service.EmployeeTimeRecord[] getEmployeeTimeRecordList() {
        return employeeTimeRecordList;
    }


    /**
     * Sets the employeeTimeRecordList value for this Timesheet.
     * 
     * @param employeeTimeRecordList
     */
    public void setEmployeeTimeRecordList(com.cannontech.multispeak.deploy.service.EmployeeTimeRecord[] employeeTimeRecordList) {
        this.employeeTimeRecordList = employeeTimeRecordList;
    }


    /**
     * Gets the jobWorkedList value for this Timesheet.
     * 
     * @return jobWorkedList
     */
    public com.cannontech.multispeak.deploy.service.JobWorked[] getJobWorkedList() {
        return jobWorkedList;
    }


    /**
     * Sets the jobWorkedList value for this Timesheet.
     * 
     * @param jobWorkedList
     */
    public void setJobWorkedList(com.cannontech.multispeak.deploy.service.JobWorked[] jobWorkedList) {
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
              java.util.Arrays.equals(this.employeeTimeRecordList, other.getEmployeeTimeRecordList()))) &&
            ((this.jobWorkedList==null && other.getJobWorkedList()==null) || 
             (this.jobWorkedList!=null &&
              java.util.Arrays.equals(this.jobWorkedList, other.getJobWorkedList())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEmployeeTimeRecordList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEmployeeTimeRecordList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getJobWorkedList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getJobWorkedList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getJobWorkedList(), i);
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeTimeRecord"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobWorkedList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorkedList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobWorked"));
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
