/**
 * EndDeviceShipment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EndDeviceShipment  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String utility;

    private java.lang.String manufacturer;

    private java.lang.String poReferenceNumber;

    private java.lang.String shippingTicketNumber;

    private java.lang.String recipient;

    private com.cannontech.multispeak.deploy.service.Address shippedToAddress;

    private java.util.Calendar shippedDate;

    private com.cannontech.multispeak.deploy.service.EndDeviceShipmentTransponderIDRange transponderIDRange;

    private com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[] receivedMeters;

    private com.cannontech.multispeak.deploy.service.Module[] receivedModules;

    private com.cannontech.multispeak.deploy.service.LoadControlDevice[] receivedLoadControlDevices;

    private com.cannontech.multispeak.deploy.service.LoadDisconnectDevice[] receivedLoadDisconnectDevices;

    public EndDeviceShipment() {
    }

    public EndDeviceShipment(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String _utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String utility,
           java.lang.String manufacturer,
           java.lang.String poReferenceNumber,
           java.lang.String shippingTicketNumber,
           java.lang.String recipient,
           com.cannontech.multispeak.deploy.service.Address shippedToAddress,
           java.util.Calendar shippedDate,
           com.cannontech.multispeak.deploy.service.EndDeviceShipmentTransponderIDRange transponderIDRange,
           com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[] receivedMeters,
           com.cannontech.multispeak.deploy.service.Module[] receivedModules,
           com.cannontech.multispeak.deploy.service.LoadControlDevice[] receivedLoadControlDevices,
           com.cannontech.multispeak.deploy.service.LoadDisconnectDevice[] receivedLoadDisconnectDevices) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            _utility,
            extensions,
            comments,
            extensionsList);
        this.utility = utility;
        this.manufacturer = manufacturer;
        this.poReferenceNumber = poReferenceNumber;
        this.shippingTicketNumber = shippingTicketNumber;
        this.recipient = recipient;
        this.shippedToAddress = shippedToAddress;
        this.shippedDate = shippedDate;
        this.transponderIDRange = transponderIDRange;
        this.receivedMeters = receivedMeters;
        this.receivedModules = receivedModules;
        this.receivedLoadControlDevices = receivedLoadControlDevices;
        this.receivedLoadDisconnectDevices = receivedLoadDisconnectDevices;
    }


    /**
     * Gets the utility value for this EndDeviceShipment.
     * 
     * @return utility
     */
    public java.lang.String getUtility() {
        return utility;
    }


    /**
     * Sets the utility value for this EndDeviceShipment.
     * 
     * @param utility
     */
    public void setUtility(java.lang.String utility) {
        this.utility = utility;
    }


    /**
     * Gets the manufacturer value for this EndDeviceShipment.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this EndDeviceShipment.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the poReferenceNumber value for this EndDeviceShipment.
     * 
     * @return poReferenceNumber
     */
    public java.lang.String getPoReferenceNumber() {
        return poReferenceNumber;
    }


    /**
     * Sets the poReferenceNumber value for this EndDeviceShipment.
     * 
     * @param poReferenceNumber
     */
    public void setPoReferenceNumber(java.lang.String poReferenceNumber) {
        this.poReferenceNumber = poReferenceNumber;
    }


    /**
     * Gets the shippingTicketNumber value for this EndDeviceShipment.
     * 
     * @return shippingTicketNumber
     */
    public java.lang.String getShippingTicketNumber() {
        return shippingTicketNumber;
    }


    /**
     * Sets the shippingTicketNumber value for this EndDeviceShipment.
     * 
     * @param shippingTicketNumber
     */
    public void setShippingTicketNumber(java.lang.String shippingTicketNumber) {
        this.shippingTicketNumber = shippingTicketNumber;
    }


    /**
     * Gets the recipient value for this EndDeviceShipment.
     * 
     * @return recipient
     */
    public java.lang.String getRecipient() {
        return recipient;
    }


    /**
     * Sets the recipient value for this EndDeviceShipment.
     * 
     * @param recipient
     */
    public void setRecipient(java.lang.String recipient) {
        this.recipient = recipient;
    }


    /**
     * Gets the shippedToAddress value for this EndDeviceShipment.
     * 
     * @return shippedToAddress
     */
    public com.cannontech.multispeak.deploy.service.Address getShippedToAddress() {
        return shippedToAddress;
    }


    /**
     * Sets the shippedToAddress value for this EndDeviceShipment.
     * 
     * @param shippedToAddress
     */
    public void setShippedToAddress(com.cannontech.multispeak.deploy.service.Address shippedToAddress) {
        this.shippedToAddress = shippedToAddress;
    }


    /**
     * Gets the shippedDate value for this EndDeviceShipment.
     * 
     * @return shippedDate
     */
    public java.util.Calendar getShippedDate() {
        return shippedDate;
    }


    /**
     * Sets the shippedDate value for this EndDeviceShipment.
     * 
     * @param shippedDate
     */
    public void setShippedDate(java.util.Calendar shippedDate) {
        this.shippedDate = shippedDate;
    }


    /**
     * Gets the transponderIDRange value for this EndDeviceShipment.
     * 
     * @return transponderIDRange
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipmentTransponderIDRange getTransponderIDRange() {
        return transponderIDRange;
    }


    /**
     * Sets the transponderIDRange value for this EndDeviceShipment.
     * 
     * @param transponderIDRange
     */
    public void setTransponderIDRange(com.cannontech.multispeak.deploy.service.EndDeviceShipmentTransponderIDRange transponderIDRange) {
        this.transponderIDRange = transponderIDRange;
    }


    /**
     * Gets the receivedMeters value for this EndDeviceShipment.
     * 
     * @return receivedMeters
     */
    public com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[] getReceivedMeters() {
        return receivedMeters;
    }


    /**
     * Sets the receivedMeters value for this EndDeviceShipment.
     * 
     * @param receivedMeters
     */
    public void setReceivedMeters(com.cannontech.multispeak.deploy.service.ReceivedElectricMeter[] receivedMeters) {
        this.receivedMeters = receivedMeters;
    }


    /**
     * Gets the receivedModules value for this EndDeviceShipment.
     * 
     * @return receivedModules
     */
    public com.cannontech.multispeak.deploy.service.Module[] getReceivedModules() {
        return receivedModules;
    }


    /**
     * Sets the receivedModules value for this EndDeviceShipment.
     * 
     * @param receivedModules
     */
    public void setReceivedModules(com.cannontech.multispeak.deploy.service.Module[] receivedModules) {
        this.receivedModules = receivedModules;
    }


    /**
     * Gets the receivedLoadControlDevices value for this EndDeviceShipment.
     * 
     * @return receivedLoadControlDevices
     */
    public com.cannontech.multispeak.deploy.service.LoadControlDevice[] getReceivedLoadControlDevices() {
        return receivedLoadControlDevices;
    }


    /**
     * Sets the receivedLoadControlDevices value for this EndDeviceShipment.
     * 
     * @param receivedLoadControlDevices
     */
    public void setReceivedLoadControlDevices(com.cannontech.multispeak.deploy.service.LoadControlDevice[] receivedLoadControlDevices) {
        this.receivedLoadControlDevices = receivedLoadControlDevices;
    }


    /**
     * Gets the receivedLoadDisconnectDevices value for this EndDeviceShipment.
     * 
     * @return receivedLoadDisconnectDevices
     */
    public com.cannontech.multispeak.deploy.service.LoadDisconnectDevice[] getReceivedLoadDisconnectDevices() {
        return receivedLoadDisconnectDevices;
    }


    /**
     * Sets the receivedLoadDisconnectDevices value for this EndDeviceShipment.
     * 
     * @param receivedLoadDisconnectDevices
     */
    public void setReceivedLoadDisconnectDevices(com.cannontech.multispeak.deploy.service.LoadDisconnectDevice[] receivedLoadDisconnectDevices) {
        this.receivedLoadDisconnectDevices = receivedLoadDisconnectDevices;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EndDeviceShipment)) return false;
        EndDeviceShipment other = (EndDeviceShipment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.utility==null && other.getUtility()==null) || 
             (this.utility!=null &&
              this.utility.equals(other.getUtility()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.poReferenceNumber==null && other.getPoReferenceNumber()==null) || 
             (this.poReferenceNumber!=null &&
              this.poReferenceNumber.equals(other.getPoReferenceNumber()))) &&
            ((this.shippingTicketNumber==null && other.getShippingTicketNumber()==null) || 
             (this.shippingTicketNumber!=null &&
              this.shippingTicketNumber.equals(other.getShippingTicketNumber()))) &&
            ((this.recipient==null && other.getRecipient()==null) || 
             (this.recipient!=null &&
              this.recipient.equals(other.getRecipient()))) &&
            ((this.shippedToAddress==null && other.getShippedToAddress()==null) || 
             (this.shippedToAddress!=null &&
              this.shippedToAddress.equals(other.getShippedToAddress()))) &&
            ((this.shippedDate==null && other.getShippedDate()==null) || 
             (this.shippedDate!=null &&
              this.shippedDate.equals(other.getShippedDate()))) &&
            ((this.transponderIDRange==null && other.getTransponderIDRange()==null) || 
             (this.transponderIDRange!=null &&
              this.transponderIDRange.equals(other.getTransponderIDRange()))) &&
            ((this.receivedMeters==null && other.getReceivedMeters()==null) || 
             (this.receivedMeters!=null &&
              java.util.Arrays.equals(this.receivedMeters, other.getReceivedMeters()))) &&
            ((this.receivedModules==null && other.getReceivedModules()==null) || 
             (this.receivedModules!=null &&
              java.util.Arrays.equals(this.receivedModules, other.getReceivedModules()))) &&
            ((this.receivedLoadControlDevices==null && other.getReceivedLoadControlDevices()==null) || 
             (this.receivedLoadControlDevices!=null &&
              java.util.Arrays.equals(this.receivedLoadControlDevices, other.getReceivedLoadControlDevices()))) &&
            ((this.receivedLoadDisconnectDevices==null && other.getReceivedLoadDisconnectDevices()==null) || 
             (this.receivedLoadDisconnectDevices!=null &&
              java.util.Arrays.equals(this.receivedLoadDisconnectDevices, other.getReceivedLoadDisconnectDevices())));
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
        if (getUtility() != null) {
            _hashCode += getUtility().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getPoReferenceNumber() != null) {
            _hashCode += getPoReferenceNumber().hashCode();
        }
        if (getShippingTicketNumber() != null) {
            _hashCode += getShippingTicketNumber().hashCode();
        }
        if (getRecipient() != null) {
            _hashCode += getRecipient().hashCode();
        }
        if (getShippedToAddress() != null) {
            _hashCode += getShippedToAddress().hashCode();
        }
        if (getShippedDate() != null) {
            _hashCode += getShippedDate().hashCode();
        }
        if (getTransponderIDRange() != null) {
            _hashCode += getTransponderIDRange().hashCode();
        }
        if (getReceivedMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReceivedMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReceivedMeters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReceivedModules() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReceivedModules());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReceivedModules(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReceivedLoadControlDevices() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReceivedLoadControlDevices());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReceivedLoadControlDevices(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReceivedLoadDisconnectDevices() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReceivedLoadDisconnectDevices());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReceivedLoadDisconnectDevices(), i);
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
        new org.apache.axis.description.TypeDesc(EndDeviceShipment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDeviceShipment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utility");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utility"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manufacturer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poReferenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poReferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shippingTicketNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shippingTicketNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recipient");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recipient"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shippedToAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shippedToAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shippedDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shippedDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transponderIDRange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderIDRange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">endDeviceShipment>transponderIDRange"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivedMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivedModules");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedModules"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivedLoadControlDevices");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedLoadControlDevices"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadControlDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadControlDevice"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivedLoadDisconnectDevices");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedLoadDisconnectDevices"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDisconnectDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDisconnectDevice"));
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
