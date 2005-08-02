/**
 * MultiSpeakMsgHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MultiSpeakMsgHeader  implements java.io.Serializable {
    private java.lang.String version;  // attribute
    private java.lang.String userID;  // attribute
    private java.lang.String pwd;  // attribute
    private java.lang.String appName;  // attribute
    private java.lang.String appVersion;  // attribute
    private java.lang.String company;  // attribute
    private com.cannontech.multispeak.MessageHeaderCSUnits CSUnits;  // attribute
    private java.lang.String coordinateSystem;  // attribute
    private java.lang.String datum;  // attribute
    private java.lang.String sessionID;  // attribute
    private java.lang.String previousSessionID;  // attribute
    private java.math.BigInteger objectsRemaining;  // attribute

    public MultiSpeakMsgHeader() {
    }

    public MultiSpeakMsgHeader(
           java.lang.String version,
           java.lang.String userID,
           java.lang.String pwd,
           java.lang.String appName,
           java.lang.String appVersion,
           java.lang.String company,
           com.cannontech.multispeak.MessageHeaderCSUnits CSUnits,
           java.lang.String coordinateSystem,
           java.lang.String datum,
           java.lang.String sessionID,
           java.lang.String previousSessionID,
           java.math.BigInteger objectsRemaining) {
           this.version = version;
           this.userID = userID;
           this.pwd = pwd;
           this.appName = appName;
           this.appVersion = appVersion;
           this.company = company;
           this.CSUnits = CSUnits;
           this.coordinateSystem = coordinateSystem;
           this.datum = datum;
           this.sessionID = sessionID;
           this.previousSessionID = previousSessionID;
           this.objectsRemaining = objectsRemaining;
    }


    /**
     * Gets the version value for this MultiSpeakMsgHeader.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this MultiSpeakMsgHeader.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the userID value for this MultiSpeakMsgHeader.
     * 
     * @return userID
     */
    public java.lang.String getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this MultiSpeakMsgHeader.
     * 
     * @param userID
     */
    public void setUserID(java.lang.String userID) {
        this.userID = userID;
    }


    /**
     * Gets the pwd value for this MultiSpeakMsgHeader.
     * 
     * @return pwd
     */
    public java.lang.String getPwd() {
        return pwd;
    }


    /**
     * Sets the pwd value for this MultiSpeakMsgHeader.
     * 
     * @param pwd
     */
    public void setPwd(java.lang.String pwd) {
        this.pwd = pwd;
    }


    /**
     * Gets the appName value for this MultiSpeakMsgHeader.
     * 
     * @return appName
     */
    public java.lang.String getAppName() {
        return appName;
    }


    /**
     * Sets the appName value for this MultiSpeakMsgHeader.
     * 
     * @param appName
     */
    public void setAppName(java.lang.String appName) {
        this.appName = appName;
    }


    /**
     * Gets the appVersion value for this MultiSpeakMsgHeader.
     * 
     * @return appVersion
     */
    public java.lang.String getAppVersion() {
        return appVersion;
    }


    /**
     * Sets the appVersion value for this MultiSpeakMsgHeader.
     * 
     * @param appVersion
     */
    public void setAppVersion(java.lang.String appVersion) {
        this.appVersion = appVersion;
    }


    /**
     * Gets the company value for this MultiSpeakMsgHeader.
     * 
     * @return company
     */
    public java.lang.String getCompany() {
        return company;
    }


    /**
     * Sets the company value for this MultiSpeakMsgHeader.
     * 
     * @param company
     */
    public void setCompany(java.lang.String company) {
        this.company = company;
    }


    /**
     * Gets the CSUnits value for this MultiSpeakMsgHeader.
     * 
     * @return CSUnits
     */
    public com.cannontech.multispeak.MessageHeaderCSUnits getCSUnits() {
        return CSUnits;
    }


    /**
     * Sets the CSUnits value for this MultiSpeakMsgHeader.
     * 
     * @param CSUnits
     */
    public void setCSUnits(com.cannontech.multispeak.MessageHeaderCSUnits CSUnits) {
        this.CSUnits = CSUnits;
    }


    /**
     * Gets the coordinateSystem value for this MultiSpeakMsgHeader.
     * 
     * @return coordinateSystem
     */
    public java.lang.String getCoordinateSystem() {
        return coordinateSystem;
    }


    /**
     * Sets the coordinateSystem value for this MultiSpeakMsgHeader.
     * 
     * @param coordinateSystem
     */
    public void setCoordinateSystem(java.lang.String coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }


    /**
     * Gets the datum value for this MultiSpeakMsgHeader.
     * 
     * @return datum
     */
    public java.lang.String getDatum() {
        return datum;
    }


    /**
     * Sets the datum value for this MultiSpeakMsgHeader.
     * 
     * @param datum
     */
    public void setDatum(java.lang.String datum) {
        this.datum = datum;
    }


    /**
     * Gets the sessionID value for this MultiSpeakMsgHeader.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this MultiSpeakMsgHeader.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * Gets the previousSessionID value for this MultiSpeakMsgHeader.
     * 
     * @return previousSessionID
     */
    public java.lang.String getPreviousSessionID() {
        return previousSessionID;
    }


    /**
     * Sets the previousSessionID value for this MultiSpeakMsgHeader.
     * 
     * @param previousSessionID
     */
    public void setPreviousSessionID(java.lang.String previousSessionID) {
        this.previousSessionID = previousSessionID;
    }


    /**
     * Gets the objectsRemaining value for this MultiSpeakMsgHeader.
     * 
     * @return objectsRemaining
     */
    public java.math.BigInteger getObjectsRemaining() {
        return objectsRemaining;
    }


    /**
     * Sets the objectsRemaining value for this MultiSpeakMsgHeader.
     * 
     * @param objectsRemaining
     */
    public void setObjectsRemaining(java.math.BigInteger objectsRemaining) {
        this.objectsRemaining = objectsRemaining;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MultiSpeakMsgHeader)) return false;
        MultiSpeakMsgHeader other = (MultiSpeakMsgHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            ((this.pwd==null && other.getPwd()==null) || 
             (this.pwd!=null &&
              this.pwd.equals(other.getPwd()))) &&
            ((this.appName==null && other.getAppName()==null) || 
             (this.appName!=null &&
              this.appName.equals(other.getAppName()))) &&
            ((this.appVersion==null && other.getAppVersion()==null) || 
             (this.appVersion!=null &&
              this.appVersion.equals(other.getAppVersion()))) &&
            ((this.company==null && other.getCompany()==null) || 
             (this.company!=null &&
              this.company.equals(other.getCompany()))) &&
            ((this.CSUnits==null && other.getCSUnits()==null) || 
             (this.CSUnits!=null &&
              this.CSUnits.equals(other.getCSUnits()))) &&
            ((this.coordinateSystem==null && other.getCoordinateSystem()==null) || 
             (this.coordinateSystem!=null &&
              this.coordinateSystem.equals(other.getCoordinateSystem()))) &&
            ((this.datum==null && other.getDatum()==null) || 
             (this.datum!=null &&
              this.datum.equals(other.getDatum()))) &&
            ((this.sessionID==null && other.getSessionID()==null) || 
             (this.sessionID!=null &&
              this.sessionID.equals(other.getSessionID()))) &&
            ((this.previousSessionID==null && other.getPreviousSessionID()==null) || 
             (this.previousSessionID!=null &&
              this.previousSessionID.equals(other.getPreviousSessionID()))) &&
            ((this.objectsRemaining==null && other.getObjectsRemaining()==null) || 
             (this.objectsRemaining!=null &&
              this.objectsRemaining.equals(other.getObjectsRemaining())));
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
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        if (getPwd() != null) {
            _hashCode += getPwd().hashCode();
        }
        if (getAppName() != null) {
            _hashCode += getAppName().hashCode();
        }
        if (getAppVersion() != null) {
            _hashCode += getAppVersion().hashCode();
        }
        if (getCompany() != null) {
            _hashCode += getCompany().hashCode();
        }
        if (getCSUnits() != null) {
            _hashCode += getCSUnits().hashCode();
        }
        if (getCoordinateSystem() != null) {
            _hashCode += getCoordinateSystem().hashCode();
        }
        if (getDatum() != null) {
            _hashCode += getDatum().hashCode();
        }
        if (getSessionID() != null) {
            _hashCode += getSessionID().hashCode();
        }
        if (getPreviousSessionID() != null) {
            _hashCode += getPreviousSessionID().hashCode();
        }
        if (getObjectsRemaining() != null) {
            _hashCode += getObjectsRemaining().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MultiSpeakMsgHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("userID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "UserID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("pwd");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Pwd"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("appName");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AppName"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("appVersion");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AppVersion"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("company");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Company"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("CSUnits");
        attrField.setXmlName(new javax.xml.namespace.QName("", "CSUnits"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MessageHeaderCSUnits"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("coordinateSystem");
        attrField.setXmlName(new javax.xml.namespace.QName("", "CoordinateSystem"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("datum");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Datum"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("sessionID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "SessionID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("previousSessionID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "PreviousSessionID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("objectsRemaining");
        attrField.setXmlName(new javax.xml.namespace.QName("", "ObjectsRemaining"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        typeDesc.addFieldDesc(attrField);
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
