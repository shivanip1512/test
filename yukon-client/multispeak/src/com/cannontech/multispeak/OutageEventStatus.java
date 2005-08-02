/**
 * OutageEventStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OutageEventStatus  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageStatus outageStatus;
    private java.lang.Boolean crewDispatched;
    private java.lang.Boolean crewOnSite;
    private java.util.Calendar ETOR;
    private java.lang.Boolean customerFound;

    public OutageEventStatus() {
    }

    public OutageEventStatus(
           com.cannontech.multispeak.OutageStatus outageStatus,
           java.lang.Boolean crewDispatched,
           java.lang.Boolean crewOnSite,
           java.util.Calendar ETOR,
           java.lang.Boolean customerFound) {
           this.outageStatus = outageStatus;
           this.crewDispatched = crewDispatched;
           this.crewOnSite = crewOnSite;
           this.ETOR = ETOR;
           this.customerFound = customerFound;
    }


    /**
     * Gets the outageStatus value for this OutageEventStatus.
     * 
     * @return outageStatus
     */
    public com.cannontech.multispeak.OutageStatus getOutageStatus() {
        return outageStatus;
    }


    /**
     * Sets the outageStatus value for this OutageEventStatus.
     * 
     * @param outageStatus
     */
    public void setOutageStatus(com.cannontech.multispeak.OutageStatus outageStatus) {
        this.outageStatus = outageStatus;
    }


    /**
     * Gets the crewDispatched value for this OutageEventStatus.
     * 
     * @return crewDispatched
     */
    public java.lang.Boolean getCrewDispatched() {
        return crewDispatched;
    }


    /**
     * Sets the crewDispatched value for this OutageEventStatus.
     * 
     * @param crewDispatched
     */
    public void setCrewDispatched(java.lang.Boolean crewDispatched) {
        this.crewDispatched = crewDispatched;
    }


    /**
     * Gets the crewOnSite value for this OutageEventStatus.
     * 
     * @return crewOnSite
     */
    public java.lang.Boolean getCrewOnSite() {
        return crewOnSite;
    }


    /**
     * Sets the crewOnSite value for this OutageEventStatus.
     * 
     * @param crewOnSite
     */
    public void setCrewOnSite(java.lang.Boolean crewOnSite) {
        this.crewOnSite = crewOnSite;
    }


    /**
     * Gets the ETOR value for this OutageEventStatus.
     * 
     * @return ETOR
     */
    public java.util.Calendar getETOR() {
        return ETOR;
    }


    /**
     * Sets the ETOR value for this OutageEventStatus.
     * 
     * @param ETOR
     */
    public void setETOR(java.util.Calendar ETOR) {
        this.ETOR = ETOR;
    }


    /**
     * Gets the customerFound value for this OutageEventStatus.
     * 
     * @return customerFound
     */
    public java.lang.Boolean getCustomerFound() {
        return customerFound;
    }


    /**
     * Sets the customerFound value for this OutageEventStatus.
     * 
     * @param customerFound
     */
    public void setCustomerFound(java.lang.Boolean customerFound) {
        this.customerFound = customerFound;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageEventStatus)) return false;
        OutageEventStatus other = (OutageEventStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageStatus==null && other.getOutageStatus()==null) || 
             (this.outageStatus!=null &&
              this.outageStatus.equals(other.getOutageStatus()))) &&
            ((this.crewDispatched==null && other.getCrewDispatched()==null) || 
             (this.crewDispatched!=null &&
              this.crewDispatched.equals(other.getCrewDispatched()))) &&
            ((this.crewOnSite==null && other.getCrewOnSite()==null) || 
             (this.crewOnSite!=null &&
              this.crewOnSite.equals(other.getCrewOnSite()))) &&
            ((this.ETOR==null && other.getETOR()==null) || 
             (this.ETOR!=null &&
              this.ETOR.equals(other.getETOR()))) &&
            ((this.customerFound==null && other.getCustomerFound()==null) || 
             (this.customerFound!=null &&
              this.customerFound.equals(other.getCustomerFound())));
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
        if (getOutageStatus() != null) {
            _hashCode += getOutageStatus().hashCode();
        }
        if (getCrewDispatched() != null) {
            _hashCode += getCrewDispatched().hashCode();
        }
        if (getCrewOnSite() != null) {
            _hashCode += getCrewOnSite().hashCode();
        }
        if (getETOR() != null) {
            _hashCode += getETOR().hashCode();
        }
        if (getCustomerFound() != null) {
            _hashCode += getCustomerFound().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageEventStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewDispatched");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewDispatched"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewOnSite");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewOnSite"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ETOR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ETOR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerFound");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerFound"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
