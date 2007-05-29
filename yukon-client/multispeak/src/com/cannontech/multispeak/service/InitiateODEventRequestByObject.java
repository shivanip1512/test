/**
 * InitiateODEventRequestByObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class InitiateODEventRequestByObject  implements java.io.Serializable {
    private java.lang.String objectName;
    private java.lang.String nounType;
    private com.cannontech.multispeak.service.PhaseCd phaseCode;
    private java.util.Calendar requestDate;
    private java.lang.String responseURL;

    public InitiateODEventRequestByObject() {
    }

    public InitiateODEventRequestByObject(
           java.lang.String objectName,
           java.lang.String nounType,
           com.cannontech.multispeak.service.PhaseCd phaseCode,
           java.util.Calendar requestDate,
           java.lang.String responseURL) {
           this.objectName = objectName;
           this.nounType = nounType;
           this.phaseCode = phaseCode;
           this.requestDate = requestDate;
           this.responseURL = responseURL;
    }


    /**
     * Gets the objectName value for this InitiateODEventRequestByObject.
     * 
     * @return objectName
     */
    public java.lang.String getObjectName() {
        return objectName;
    }


    /**
     * Sets the objectName value for this InitiateODEventRequestByObject.
     * 
     * @param objectName
     */
    public void setObjectName(java.lang.String objectName) {
        this.objectName = objectName;
    }


    /**
     * Gets the nounType value for this InitiateODEventRequestByObject.
     * 
     * @return nounType
     */
    public java.lang.String getNounType() {
        return nounType;
    }


    /**
     * Sets the nounType value for this InitiateODEventRequestByObject.
     * 
     * @param nounType
     */
    public void setNounType(java.lang.String nounType) {
        this.nounType = nounType;
    }


    /**
     * Gets the phaseCode value for this InitiateODEventRequestByObject.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.service.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this InitiateODEventRequestByObject.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.service.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the requestDate value for this InitiateODEventRequestByObject.
     * 
     * @return requestDate
     */
    public java.util.Calendar getRequestDate() {
        return requestDate;
    }


    /**
     * Sets the requestDate value for this InitiateODEventRequestByObject.
     * 
     * @param requestDate
     */
    public void setRequestDate(java.util.Calendar requestDate) {
        this.requestDate = requestDate;
    }


    /**
     * Gets the responseURL value for this InitiateODEventRequestByObject.
     * 
     * @return responseURL
     */
    public java.lang.String getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this InitiateODEventRequestByObject.
     * 
     * @param responseURL
     */
    public void setResponseURL(java.lang.String responseURL) {
        this.responseURL = responseURL;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateODEventRequestByObject)) return false;
        InitiateODEventRequestByObject other = (InitiateODEventRequestByObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectName==null && other.getObjectName()==null) || 
             (this.objectName!=null &&
              this.objectName.equals(other.getObjectName()))) &&
            ((this.nounType==null && other.getNounType()==null) || 
             (this.nounType!=null &&
              this.nounType.equals(other.getNounType()))) &&
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode()))) &&
            ((this.requestDate==null && other.getRequestDate()==null) || 
             (this.requestDate!=null &&
              this.requestDate.equals(other.getRequestDate()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL())));
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
        if (getObjectName() != null) {
            _hashCode += getObjectName().hashCode();
        }
        if (getNounType() != null) {
            _hashCode += getNounType().hashCode();
        }
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        if (getRequestDate() != null) {
            _hashCode += getRequestDate().hashCode();
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateODEventRequestByObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateODEventRequestByObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nounType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nounType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PhaseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
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
