/**
 * MeterTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeterTest  implements java.io.Serializable {
    private java.util.Calendar testDate;

    private com.cannontech.multispeak.deploy.service.AsFound asFound;

    private com.cannontech.multispeak.deploy.service.AsLeft asLeft;

    private java.lang.String reason;

    private java.lang.String testersInitials;

    private java.lang.String testCompany;

    private java.lang.String comments;

    private java.lang.Float revolutions;

    public MeterTest() {
    }

    public MeterTest(
           java.util.Calendar testDate,
           com.cannontech.multispeak.deploy.service.AsFound asFound,
           com.cannontech.multispeak.deploy.service.AsLeft asLeft,
           java.lang.String reason,
           java.lang.String testersInitials,
           java.lang.String testCompany,
           java.lang.String comments,
           java.lang.Float revolutions) {
           this.testDate = testDate;
           this.asFound = asFound;
           this.asLeft = asLeft;
           this.reason = reason;
           this.testersInitials = testersInitials;
           this.testCompany = testCompany;
           this.comments = comments;
           this.revolutions = revolutions;
    }


    /**
     * Gets the testDate value for this MeterTest.
     * 
     * @return testDate
     */
    public java.util.Calendar getTestDate() {
        return testDate;
    }


    /**
     * Sets the testDate value for this MeterTest.
     * 
     * @param testDate
     */
    public void setTestDate(java.util.Calendar testDate) {
        this.testDate = testDate;
    }


    /**
     * Gets the asFound value for this MeterTest.
     * 
     * @return asFound
     */
    public com.cannontech.multispeak.deploy.service.AsFound getAsFound() {
        return asFound;
    }


    /**
     * Sets the asFound value for this MeterTest.
     * 
     * @param asFound
     */
    public void setAsFound(com.cannontech.multispeak.deploy.service.AsFound asFound) {
        this.asFound = asFound;
    }


    /**
     * Gets the asLeft value for this MeterTest.
     * 
     * @return asLeft
     */
    public com.cannontech.multispeak.deploy.service.AsLeft getAsLeft() {
        return asLeft;
    }


    /**
     * Sets the asLeft value for this MeterTest.
     * 
     * @param asLeft
     */
    public void setAsLeft(com.cannontech.multispeak.deploy.service.AsLeft asLeft) {
        this.asLeft = asLeft;
    }


    /**
     * Gets the reason value for this MeterTest.
     * 
     * @return reason
     */
    public java.lang.String getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this MeterTest.
     * 
     * @param reason
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }


    /**
     * Gets the testersInitials value for this MeterTest.
     * 
     * @return testersInitials
     */
    public java.lang.String getTestersInitials() {
        return testersInitials;
    }


    /**
     * Sets the testersInitials value for this MeterTest.
     * 
     * @param testersInitials
     */
    public void setTestersInitials(java.lang.String testersInitials) {
        this.testersInitials = testersInitials;
    }


    /**
     * Gets the testCompany value for this MeterTest.
     * 
     * @return testCompany
     */
    public java.lang.String getTestCompany() {
        return testCompany;
    }


    /**
     * Sets the testCompany value for this MeterTest.
     * 
     * @param testCompany
     */
    public void setTestCompany(java.lang.String testCompany) {
        this.testCompany = testCompany;
    }


    /**
     * Gets the comments value for this MeterTest.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this MeterTest.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the revolutions value for this MeterTest.
     * 
     * @return revolutions
     */
    public java.lang.Float getRevolutions() {
        return revolutions;
    }


    /**
     * Sets the revolutions value for this MeterTest.
     * 
     * @param revolutions
     */
    public void setRevolutions(java.lang.Float revolutions) {
        this.revolutions = revolutions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterTest)) return false;
        MeterTest other = (MeterTest) obj;
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
            ((this.asFound==null && other.getAsFound()==null) || 
             (this.asFound!=null &&
              this.asFound.equals(other.getAsFound()))) &&
            ((this.asLeft==null && other.getAsLeft()==null) || 
             (this.asLeft!=null &&
              this.asLeft.equals(other.getAsLeft()))) &&
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason()))) &&
            ((this.testersInitials==null && other.getTestersInitials()==null) || 
             (this.testersInitials!=null &&
              this.testersInitials.equals(other.getTestersInitials()))) &&
            ((this.testCompany==null && other.getTestCompany()==null) || 
             (this.testCompany!=null &&
              this.testCompany.equals(other.getTestCompany()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.revolutions==null && other.getRevolutions()==null) || 
             (this.revolutions!=null &&
              this.revolutions.equals(other.getRevolutions())));
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
        if (getAsFound() != null) {
            _hashCode += getAsFound().hashCode();
        }
        if (getAsLeft() != null) {
            _hashCode += getAsLeft().hashCode();
        }
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        if (getTestersInitials() != null) {
            _hashCode += getTestersInitials().hashCode();
        }
        if (getTestCompany() != null) {
            _hashCode += getTestCompany().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        if (getRevolutions() != null) {
            _hashCode += getRevolutions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterTest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("asFound");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asFound"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asFound"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("asLeft");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asLeft"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asLeft"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testersInitials");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testersInitials"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testCompany");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testCompany"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revolutions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revolutions"));
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
