/**
 * RegistrationInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RegistrationInfo  implements java.io.Serializable {
    private java.lang.String registrationID;

    private org.apache.axis.types.URI responseURL;

    private java.lang.String responseUserID;

    private java.lang.String responsePwd;

    private java.lang.String MSFunction;

    private java.lang.String[] methodsList;

    public RegistrationInfo() {
    }

    public RegistrationInfo(
           java.lang.String registrationID,
           org.apache.axis.types.URI responseURL,
           java.lang.String responseUserID,
           java.lang.String responsePwd,
           java.lang.String MSFunction,
           java.lang.String[] methodsList) {
           this.registrationID = registrationID;
           this.responseURL = responseURL;
           this.responseUserID = responseUserID;
           this.responsePwd = responsePwd;
           this.MSFunction = MSFunction;
           this.methodsList = methodsList;
    }


    /**
     * Gets the registrationID value for this RegistrationInfo.
     * 
     * @return registrationID
     */
    public java.lang.String getRegistrationID() {
        return registrationID;
    }


    /**
     * Sets the registrationID value for this RegistrationInfo.
     * 
     * @param registrationID
     */
    public void setRegistrationID(java.lang.String registrationID) {
        this.registrationID = registrationID;
    }


    /**
     * Gets the responseURL value for this RegistrationInfo.
     * 
     * @return responseURL
     */
    public org.apache.axis.types.URI getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this RegistrationInfo.
     * 
     * @param responseURL
     */
    public void setResponseURL(org.apache.axis.types.URI responseURL) {
        this.responseURL = responseURL;
    }


    /**
     * Gets the responseUserID value for this RegistrationInfo.
     * 
     * @return responseUserID
     */
    public java.lang.String getResponseUserID() {
        return responseUserID;
    }


    /**
     * Sets the responseUserID value for this RegistrationInfo.
     * 
     * @param responseUserID
     */
    public void setResponseUserID(java.lang.String responseUserID) {
        this.responseUserID = responseUserID;
    }


    /**
     * Gets the responsePwd value for this RegistrationInfo.
     * 
     * @return responsePwd
     */
    public java.lang.String getResponsePwd() {
        return responsePwd;
    }


    /**
     * Sets the responsePwd value for this RegistrationInfo.
     * 
     * @param responsePwd
     */
    public void setResponsePwd(java.lang.String responsePwd) {
        this.responsePwd = responsePwd;
    }


    /**
     * Gets the MSFunction value for this RegistrationInfo.
     * 
     * @return MSFunction
     */
    public java.lang.String getMSFunction() {
        return MSFunction;
    }


    /**
     * Sets the MSFunction value for this RegistrationInfo.
     * 
     * @param MSFunction
     */
    public void setMSFunction(java.lang.String MSFunction) {
        this.MSFunction = MSFunction;
    }


    /**
     * Gets the methodsList value for this RegistrationInfo.
     * 
     * @return methodsList
     */
    public java.lang.String[] getMethodsList() {
        return methodsList;
    }


    /**
     * Sets the methodsList value for this RegistrationInfo.
     * 
     * @param methodsList
     */
    public void setMethodsList(java.lang.String[] methodsList) {
        this.methodsList = methodsList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegistrationInfo)) return false;
        RegistrationInfo other = (RegistrationInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.registrationID==null && other.getRegistrationID()==null) || 
             (this.registrationID!=null &&
              this.registrationID.equals(other.getRegistrationID()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL()))) &&
            ((this.responseUserID==null && other.getResponseUserID()==null) || 
             (this.responseUserID!=null &&
              this.responseUserID.equals(other.getResponseUserID()))) &&
            ((this.responsePwd==null && other.getResponsePwd()==null) || 
             (this.responsePwd!=null &&
              this.responsePwd.equals(other.getResponsePwd()))) &&
            ((this.MSFunction==null && other.getMSFunction()==null) || 
             (this.MSFunction!=null &&
              this.MSFunction.equals(other.getMSFunction()))) &&
            ((this.methodsList==null && other.getMethodsList()==null) || 
             (this.methodsList!=null &&
              java.util.Arrays.equals(this.methodsList, other.getMethodsList())));
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
        if (getRegistrationID() != null) {
            _hashCode += getRegistrationID().hashCode();
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        if (getResponseUserID() != null) {
            _hashCode += getResponseUserID().hashCode();
        }
        if (getResponsePwd() != null) {
            _hashCode += getResponsePwd().hashCode();
        }
        if (getMSFunction() != null) {
            _hashCode += getMSFunction().hashCode();
        }
        if (getMethodsList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMethodsList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMethodsList(), i);
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
        new org.apache.axis.description.TypeDesc(RegistrationInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registrationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registrationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseUserID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseUserID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responsePwd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responsePwd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MSFunction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MSFunction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("methodsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "methodsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "methodName"));
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
