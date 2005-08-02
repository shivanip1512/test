/**
 * TestInstance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class TestInstance  implements java.io.Serializable {
    private java.util.Calendar testDate;
    private java.lang.String pcbClass;
    private java.lang.String sampleNumber;
    private java.lang.String comment;

    public TestInstance() {
    }

    public TestInstance(
           java.util.Calendar testDate,
           java.lang.String pcbClass,
           java.lang.String sampleNumber,
           java.lang.String comment) {
           this.testDate = testDate;
           this.pcbClass = pcbClass;
           this.sampleNumber = sampleNumber;
           this.comment = comment;
    }


    /**
     * Gets the testDate value for this TestInstance.
     * 
     * @return testDate
     */
    public java.util.Calendar getTestDate() {
        return testDate;
    }


    /**
     * Sets the testDate value for this TestInstance.
     * 
     * @param testDate
     */
    public void setTestDate(java.util.Calendar testDate) {
        this.testDate = testDate;
    }


    /**
     * Gets the pcbClass value for this TestInstance.
     * 
     * @return pcbClass
     */
    public java.lang.String getPcbClass() {
        return pcbClass;
    }


    /**
     * Sets the pcbClass value for this TestInstance.
     * 
     * @param pcbClass
     */
    public void setPcbClass(java.lang.String pcbClass) {
        this.pcbClass = pcbClass;
    }


    /**
     * Gets the sampleNumber value for this TestInstance.
     * 
     * @return sampleNumber
     */
    public java.lang.String getSampleNumber() {
        return sampleNumber;
    }


    /**
     * Sets the sampleNumber value for this TestInstance.
     * 
     * @param sampleNumber
     */
    public void setSampleNumber(java.lang.String sampleNumber) {
        this.sampleNumber = sampleNumber;
    }


    /**
     * Gets the comment value for this TestInstance.
     * 
     * @return comment
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this TestInstance.
     * 
     * @param comment
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestInstance)) return false;
        TestInstance other = (TestInstance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.testDate==null && other.getTestDate()==null) || 
             (this.testDate!=null &&
              this.testDate.equals(other.getTestDate()))) &&
            ((this.pcbClass==null && other.getPcbClass()==null) || 
             (this.pcbClass!=null &&
              this.pcbClass.equals(other.getPcbClass()))) &&
            ((this.sampleNumber==null && other.getSampleNumber()==null) || 
             (this.sampleNumber!=null &&
              this.sampleNumber.equals(other.getSampleNumber()))) &&
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment())));
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
        if (getTestDate() != null) {
            _hashCode += getTestDate().hashCode();
        }
        if (getPcbClass() != null) {
            _hashCode += getPcbClass().hashCode();
        }
        if (getSampleNumber() != null) {
            _hashCode += getSampleNumber().hashCode();
        }
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestInstance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pcbClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sampleNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sampleNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "comment"));
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
