/**
 * SubstationLoadControlStatusControlledSubstation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SubstationLoadControlStatusControlledSubstation  implements java.io.Serializable {
    private java.lang.String substationName;

    private java.lang.String status;

    private java.lang.String mode;

    private com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog;

    private com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledSubstationControlledItemsControlItem[] controlledItems;

    public SubstationLoadControlStatusControlledSubstation() {
    }

    public SubstationLoadControlStatusControlledSubstation(
           java.lang.String substationName,
           java.lang.String status,
           java.lang.String mode,
           com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog,
           com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledSubstationControlledItemsControlItem[] controlledItems) {
           this.substationName = substationName;
           this.status = status;
           this.mode = mode;
           this.scadaAnalog = scadaAnalog;
           this.controlledItems = controlledItems;
    }


    /**
     * Gets the substationName value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @return substationName
     */
    public java.lang.String getSubstationName() {
        return substationName;
    }


    /**
     * Sets the substationName value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @param substationName
     */
    public void setSubstationName(java.lang.String substationName) {
        this.substationName = substationName;
    }


    /**
     * Gets the status value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the mode value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @return mode
     */
    public java.lang.String getMode() {
        return mode;
    }


    /**
     * Sets the mode value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @param mode
     */
    public void setMode(java.lang.String mode) {
        this.mode = mode;
    }


    /**
     * Gets the scadaAnalog value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @return scadaAnalog
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog getScadaAnalog() {
        return scadaAnalog;
    }


    /**
     * Sets the scadaAnalog value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @param scadaAnalog
     */
    public void setScadaAnalog(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) {
        this.scadaAnalog = scadaAnalog;
    }


    /**
     * Gets the controlledItems value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @return controlledItems
     */
    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledSubstationControlledItemsControlItem[] getControlledItems() {
        return controlledItems;
    }


    /**
     * Sets the controlledItems value for this SubstationLoadControlStatusControlledSubstation.
     * 
     * @param controlledItems
     */
    public void setControlledItems(com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledSubstationControlledItemsControlItem[] controlledItems) {
        this.controlledItems = controlledItems;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubstationLoadControlStatusControlledSubstation)) return false;
        SubstationLoadControlStatusControlledSubstation other = (SubstationLoadControlStatusControlledSubstation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.substationName==null && other.getSubstationName()==null) || 
             (this.substationName!=null &&
              this.substationName.equals(other.getSubstationName()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.mode==null && other.getMode()==null) || 
             (this.mode!=null &&
              this.mode.equals(other.getMode()))) &&
            ((this.scadaAnalog==null && other.getScadaAnalog()==null) || 
             (this.scadaAnalog!=null &&
              this.scadaAnalog.equals(other.getScadaAnalog()))) &&
            ((this.controlledItems==null && other.getControlledItems()==null) || 
             (this.controlledItems!=null &&
              java.util.Arrays.equals(this.controlledItems, other.getControlledItems())));
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
        if (getSubstationName() != null) {
            _hashCode += getSubstationName().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getMode() != null) {
            _hashCode += getMode().hashCode();
        }
        if (getScadaAnalog() != null) {
            _hashCode += getScadaAnalog().hashCode();
        }
        if (getControlledItems() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getControlledItems());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getControlledItems(), i);
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
        new org.apache.axis.description.TypeDesc(SubstationLoadControlStatusControlledSubstation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">substationLoadControlStatus>controlledSubstation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaAnalog");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaAnalog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlledItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlledItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">>>substationLoadControlStatus>controlledSubstation>controlledItems>controlItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlItem"));
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
