/**
 * GetLatestMeterReadingsByMeterGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetLatestMeterReadingsByMeterGroup  implements java.io.Serializable {
    private java.lang.String meterGroupID;

    private java.lang.String formattedBlockTemplateName;

    private java.lang.String[] fieldName;

    public GetLatestMeterReadingsByMeterGroup() {
    }

    public GetLatestMeterReadingsByMeterGroup(
           java.lang.String meterGroupID,
           java.lang.String formattedBlockTemplateName,
           java.lang.String[] fieldName) {
           this.meterGroupID = meterGroupID;
           this.formattedBlockTemplateName = formattedBlockTemplateName;
           this.fieldName = fieldName;
    }


    /**
     * Gets the meterGroupID value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @return meterGroupID
     */
    public java.lang.String getMeterGroupID() {
        return meterGroupID;
    }


    /**
     * Sets the meterGroupID value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @param meterGroupID
     */
    public void setMeterGroupID(java.lang.String meterGroupID) {
        this.meterGroupID = meterGroupID;
    }


    /**
     * Gets the formattedBlockTemplateName value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @return formattedBlockTemplateName
     */
    public java.lang.String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }


    /**
     * Sets the formattedBlockTemplateName value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @param formattedBlockTemplateName
     */
    public void setFormattedBlockTemplateName(java.lang.String formattedBlockTemplateName) {
        this.formattedBlockTemplateName = formattedBlockTemplateName;
    }


    /**
     * Gets the fieldName value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @return fieldName
     */
    public java.lang.String[] getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this GetLatestMeterReadingsByMeterGroup.
     * 
     * @param fieldName
     */
    public void setFieldName(java.lang.String[] fieldName) {
        this.fieldName = fieldName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLatestMeterReadingsByMeterGroup)) return false;
        GetLatestMeterReadingsByMeterGroup other = (GetLatestMeterReadingsByMeterGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterGroupID==null && other.getMeterGroupID()==null) || 
             (this.meterGroupID!=null &&
              this.meterGroupID.equals(other.getMeterGroupID()))) &&
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
        if (getMeterGroupID() != null) {
            _hashCode += getMeterGroupID().hashCode();
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
        new org.apache.axis.description.TypeDesc(GetLatestMeterReadingsByMeterGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLatestMeterReadingsByMeterGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
