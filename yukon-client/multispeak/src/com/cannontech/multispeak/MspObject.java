/**
 * MspObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private java.lang.String comments;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.lang.String objectID;  // attribute
    private com.cannontech.multispeak.Action verb;  // attribute
    private java.lang.String errorString;  // attribute
    private java.lang.String replaceID;  // attribute
    private java.lang.String utility;  // attribute

    public MspObject() {
    }

    public MspObject(
           com.cannontech.multispeak.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.lang.String objectID,
           com.cannontech.multispeak.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility) {
           this.extensions = extensions;
           this.comments = comments;
           this.extensionsList = extensionsList;
           this.objectID = objectID;
           this.verb = verb;
           this.errorString = errorString;
           this.replaceID = replaceID;
           this.utility = utility;
    }


    /**
     * Gets the extensions value for this MspObject.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this MspObject.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the comments value for this MspObject.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this MspObject.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the extensionsList value for this MspObject.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this MspObject.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the objectID value for this MspObject.
     * 
     * @return objectID
     */
    public java.lang.String getObjectID() {
        return objectID;
    }


    /**
     * Sets the objectID value for this MspObject.
     * 
     * @param objectID
     */
    public void setObjectID(java.lang.String objectID) {
        this.objectID = objectID;
    }


    /**
     * Gets the verb value for this MspObject.
     * 
     * @return verb
     */
    public com.cannontech.multispeak.Action getVerb() {
        return verb;
    }


    /**
     * Sets the verb value for this MspObject.
     * 
     * @param verb
     */
    public void setVerb(com.cannontech.multispeak.Action verb) {
        this.verb = verb;
    }


    /**
     * Gets the errorString value for this MspObject.
     * 
     * @return errorString
     */
    public java.lang.String getErrorString() {
        return errorString;
    }


    /**
     * Sets the errorString value for this MspObject.
     * 
     * @param errorString
     */
    public void setErrorString(java.lang.String errorString) {
        this.errorString = errorString;
    }


    /**
     * Gets the replaceID value for this MspObject.
     * 
     * @return replaceID
     */
    public java.lang.String getReplaceID() {
        return replaceID;
    }


    /**
     * Sets the replaceID value for this MspObject.
     * 
     * @param replaceID
     */
    public void setReplaceID(java.lang.String replaceID) {
        this.replaceID = replaceID;
    }


    /**
     * Gets the utility value for this MspObject.
     * 
     * @return utility
     */
    public java.lang.String getUtility() {
        return utility;
    }


    /**
     * Sets the utility value for this MspObject.
     * 
     * @param utility
     */
    public void setUtility(java.lang.String utility) {
        this.utility = utility;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspObject)) return false;
        MspObject other = (MspObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.extensionsList==null && other.getExtensionsList()==null) || 
             (this.extensionsList!=null &&
              this.extensionsList.equals(other.getExtensionsList()))) &&
            ((this.objectID==null && other.getObjectID()==null) || 
             (this.objectID!=null &&
              this.objectID.equals(other.getObjectID()))) &&
            ((this.verb==null && other.getVerb()==null) || 
             (this.verb!=null &&
              this.verb.equals(other.getVerb()))) &&
            ((this.errorString==null && other.getErrorString()==null) || 
             (this.errorString!=null &&
              this.errorString.equals(other.getErrorString()))) &&
            ((this.replaceID==null && other.getReplaceID()==null) || 
             (this.replaceID!=null &&
              this.replaceID.equals(other.getReplaceID()))) &&
            ((this.utility==null && other.getUtility()==null) || 
             (this.utility!=null &&
              this.utility.equals(other.getUtility())));
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
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        if (getExtensionsList() != null) {
            _hashCode += getExtensionsList().hashCode();
        }
        if (getObjectID() != null) {
            _hashCode += getObjectID().hashCode();
        }
        if (getVerb() != null) {
            _hashCode += getVerb().hashCode();
        }
        if (getErrorString() != null) {
            _hashCode += getErrorString().hashCode();
        }
        if (getReplaceID() != null) {
            _hashCode += getReplaceID().hashCode();
        }
        if (getUtility() != null) {
            _hashCode += getUtility().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspObject"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("objectID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "objectID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("verb");
        attrField.setXmlName(new javax.xml.namespace.QName("", "verb"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "action"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("errorString");
        attrField.setXmlName(new javax.xml.namespace.QName("", "errorString"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("replaceID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "replaceID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("utility");
        attrField.setXmlName(new javax.xml.namespace.QName("", "utility"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
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
        elemField.setFieldName("extensionsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
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
