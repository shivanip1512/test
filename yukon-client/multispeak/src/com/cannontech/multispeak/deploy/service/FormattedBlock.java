/**
 * FormattedBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class FormattedBlock  implements java.io.Serializable {
    private java.lang.String separator;

    private java.lang.String contentNounType;

    private java.lang.String contentID;

    private java.lang.String comment;

    private com.cannontech.multispeak.deploy.service.SyntaxItem[] valSyntax;

    private java.lang.String[] valueList;

    public FormattedBlock() {
    }

    public FormattedBlock(
           java.lang.String separator,
           java.lang.String contentNounType,
           java.lang.String contentID,
           java.lang.String comment,
           com.cannontech.multispeak.deploy.service.SyntaxItem[] valSyntax,
           java.lang.String[] valueList) {
           this.separator = separator;
           this.contentNounType = contentNounType;
           this.contentID = contentID;
           this.comment = comment;
           this.valSyntax = valSyntax;
           this.valueList = valueList;
    }


    /**
     * Gets the separator value for this FormattedBlock.
     * 
     * @return separator
     */
    public java.lang.String getSeparator() {
        return separator;
    }


    /**
     * Sets the separator value for this FormattedBlock.
     * 
     * @param separator
     */
    public void setSeparator(java.lang.String separator) {
        this.separator = separator;
    }


    /**
     * Gets the contentNounType value for this FormattedBlock.
     * 
     * @return contentNounType
     */
    public java.lang.String getContentNounType() {
        return contentNounType;
    }


    /**
     * Sets the contentNounType value for this FormattedBlock.
     * 
     * @param contentNounType
     */
    public void setContentNounType(java.lang.String contentNounType) {
        this.contentNounType = contentNounType;
    }


    /**
     * Gets the contentID value for this FormattedBlock.
     * 
     * @return contentID
     */
    public java.lang.String getContentID() {
        return contentID;
    }


    /**
     * Sets the contentID value for this FormattedBlock.
     * 
     * @param contentID
     */
    public void setContentID(java.lang.String contentID) {
        this.contentID = contentID;
    }


    /**
     * Gets the comment value for this FormattedBlock.
     * 
     * @return comment
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this FormattedBlock.
     * 
     * @param comment
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }


    /**
     * Gets the valSyntax value for this FormattedBlock.
     * 
     * @return valSyntax
     */
    public com.cannontech.multispeak.deploy.service.SyntaxItem[] getValSyntax() {
        return valSyntax;
    }


    /**
     * Sets the valSyntax value for this FormattedBlock.
     * 
     * @param valSyntax
     */
    public void setValSyntax(com.cannontech.multispeak.deploy.service.SyntaxItem[] valSyntax) {
        this.valSyntax = valSyntax;
    }


    /**
     * Gets the valueList value for this FormattedBlock.
     * 
     * @return valueList
     */
    public java.lang.String[] getValueList() {
        return valueList;
    }


    /**
     * Sets the valueList value for this FormattedBlock.
     * 
     * @param valueList
     */
    public void setValueList(java.lang.String[] valueList) {
        this.valueList = valueList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FormattedBlock)) return false;
        FormattedBlock other = (FormattedBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.separator==null && other.getSeparator()==null) || 
             (this.separator!=null &&
              this.separator.equals(other.getSeparator()))) &&
            ((this.contentNounType==null && other.getContentNounType()==null) || 
             (this.contentNounType!=null &&
              this.contentNounType.equals(other.getContentNounType()))) &&
            ((this.contentID==null && other.getContentID()==null) || 
             (this.contentID!=null &&
              this.contentID.equals(other.getContentID()))) &&
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment()))) &&
            ((this.valSyntax==null && other.getValSyntax()==null) || 
             (this.valSyntax!=null &&
              java.util.Arrays.equals(this.valSyntax, other.getValSyntax()))) &&
            ((this.valueList==null && other.getValueList()==null) || 
             (this.valueList!=null &&
              java.util.Arrays.equals(this.valueList, other.getValueList())));
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
        if (getSeparator() != null) {
            _hashCode += getSeparator().hashCode();
        }
        if (getContentNounType() != null) {
            _hashCode += getContentNounType().hashCode();
        }
        if (getContentID() != null) {
            _hashCode += getContentID().hashCode();
        }
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        if (getValSyntax() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValSyntax());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValSyntax(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getValueList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValueList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValueList(), i);
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
        new org.apache.axis.description.TypeDesc(FormattedBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("separator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "separator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentNounType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contentNounType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contentID"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valSyntax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valSyntax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valueList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valueList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "val"));
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
