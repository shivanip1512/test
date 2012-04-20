/**
 * GetLatestReadingsByMeterNoList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetLatestReadingsByMeterNoList  implements java.io.Serializable {
    private java.lang.String[] meterNo;

    private java.util.Calendar startDate;

    private java.util.Calendar endDate;

    private java.lang.String readingType;

    private java.lang.String lastReceived;

    private com.cannontech.multispeak.deploy.service.ServiceType serviceType;

    private java.lang.String formattedBlockTemplateName;

    private java.lang.String[] fieldName;

    public GetLatestReadingsByMeterNoList() {
    }

    public GetLatestReadingsByMeterNoList(
           java.lang.String[] meterNo,
           java.util.Calendar startDate,
           java.util.Calendar endDate,
           java.lang.String readingType,
           java.lang.String lastReceived,
           com.cannontech.multispeak.deploy.service.ServiceType serviceType,
           java.lang.String formattedBlockTemplateName,
           java.lang.String[] fieldName) {
           this.meterNo = meterNo;
           this.startDate = startDate;
           this.endDate = endDate;
           this.readingType = readingType;
           this.lastReceived = lastReceived;
           this.serviceType = serviceType;
           this.formattedBlockTemplateName = formattedBlockTemplateName;
           this.fieldName = fieldName;
    }


    /**
     * Gets the meterNo value for this GetLatestReadingsByMeterNoList.
     * 
     * @return meterNo
     */
    public java.lang.String[] getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this GetLatestReadingsByMeterNoList.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String[] meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the startDate value for this GetLatestReadingsByMeterNoList.
     * 
     * @return startDate
     */
    public java.util.Calendar getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this GetLatestReadingsByMeterNoList.
     * 
     * @param startDate
     */
    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the endDate value for this GetLatestReadingsByMeterNoList.
     * 
     * @return endDate
     */
    public java.util.Calendar getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate value for this GetLatestReadingsByMeterNoList.
     * 
     * @param endDate
     */
    public void setEndDate(java.util.Calendar endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the readingType value for this GetLatestReadingsByMeterNoList.
     * 
     * @return readingType
     */
    public java.lang.String getReadingType() {
        return readingType;
    }


    /**
     * Sets the readingType value for this GetLatestReadingsByMeterNoList.
     * 
     * @param readingType
     */
    public void setReadingType(java.lang.String readingType) {
        this.readingType = readingType;
    }


    /**
     * Gets the lastReceived value for this GetLatestReadingsByMeterNoList.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetLatestReadingsByMeterNoList.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }


    /**
     * Gets the serviceType value for this GetLatestReadingsByMeterNoList.
     * 
     * @return serviceType
     */
    public com.cannontech.multispeak.deploy.service.ServiceType getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this GetLatestReadingsByMeterNoList.
     * 
     * @param serviceType
     */
    public void setServiceType(com.cannontech.multispeak.deploy.service.ServiceType serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the formattedBlockTemplateName value for this GetLatestReadingsByMeterNoList.
     * 
     * @return formattedBlockTemplateName
     */
    public java.lang.String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }


    /**
     * Sets the formattedBlockTemplateName value for this GetLatestReadingsByMeterNoList.
     * 
     * @param formattedBlockTemplateName
     */
    public void setFormattedBlockTemplateName(java.lang.String formattedBlockTemplateName) {
        this.formattedBlockTemplateName = formattedBlockTemplateName;
    }


    /**
     * Gets the fieldName value for this GetLatestReadingsByMeterNoList.
     * 
     * @return fieldName
     */
    public java.lang.String[] getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this GetLatestReadingsByMeterNoList.
     * 
     * @param fieldName
     */
    public void setFieldName(java.lang.String[] fieldName) {
        this.fieldName = fieldName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLatestReadingsByMeterNoList)) return false;
        GetLatestReadingsByMeterNoList other = (GetLatestReadingsByMeterNoList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              java.util.Arrays.equals(this.meterNo, other.getMeterNo()))) &&
            ((this.startDate==null && other.getStartDate()==null) || 
             (this.startDate!=null &&
              this.startDate.equals(other.getStartDate()))) &&
            ((this.endDate==null && other.getEndDate()==null) || 
             (this.endDate!=null &&
              this.endDate.equals(other.getEndDate()))) &&
            ((this.readingType==null && other.getReadingType()==null) || 
             (this.readingType!=null &&
              this.readingType.equals(other.getReadingType()))) &&
            ((this.lastReceived==null && other.getLastReceived()==null) || 
             (this.lastReceived!=null &&
              this.lastReceived.equals(other.getLastReceived()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.formattedBlockTemplateName==null && other.getFormattedBlockTemplateName()==null) || 
             (this.formattedBlockTemplateName!=null &&
              this.formattedBlockTemplateName.equals(other.getFormattedBlockTemplateName()))) &&
            ((this.fieldName==null && other.getFieldName()==null) || 
             (this.fieldName!=null &&
              java.util.Arrays.equals(this.fieldName, other.getFieldName())));
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
        if (getMeterNo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterNo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterNo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStartDate() != null) {
            _hashCode += getStartDate().hashCode();
        }
        if (getEndDate() != null) {
            _hashCode += getEndDate().hashCode();
        }
        if (getReadingType() != null) {
            _hashCode += getReadingType().hashCode();
        }
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getFormattedBlockTemplateName() != null) {
            _hashCode += getFormattedBlockTemplateName().hashCode();
        }
        if (getFieldName() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFieldName());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFieldName(), i);
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
        new org.apache.axis.description.TypeDesc(GetLatestReadingsByMeterNoList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLatestReadingsByMeterNoList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReceived");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formattedBlockTemplateName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplateName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fieldName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
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
