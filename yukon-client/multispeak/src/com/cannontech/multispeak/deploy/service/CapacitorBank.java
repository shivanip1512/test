package com.cannontech.multispeak.deploy.service;

import java.lang.reflect.Array;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class CapacitorBank  extends com.cannontech.multispeak.deploy.service.MspBankObject  implements java.io.Serializable {
    private ConnectionCode connectionCd;
    private SwType swType;
    private SwStatus swStatus;
    private Float swOn;
    private Float swOff;
    private ObjectRef cntrCkt;
    private Float bankKvar;
    private Float volts;
    private Capacitor[] capacitorList;

    public CapacitorBank() {
    }

    public CapacitorBank(
           String objectID,
           Action verb,
           String errorString,
           String replaceID,
           String utility,
           Extensions extensions,
           String comments,
           ExtensionsItem[] extensionsList,
           PointType mapLocation,
           String gridLocation,
           Float rotation,
           String facilityID,
           GraphicSymbol[] graphicSymbol,
           GenericAnnotationFeature[] annotationList,
           NodeIdentifier toNodeID,
           NodeIdentifier fromNodeID,
           ObjectRef parentSectionID,
           String sectionID,
           PhaseCd phaseCode,
           MspLoadGroup load,
           ConnectionCode connectionCd,
           SwType swType,
           SwStatus swStatus,
           Float swOn,
           Float swOff,
           ObjectRef cntrCkt,
           Float bankKvar,
           Float volts,
           Capacitor[] capacitorList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID,
            graphicSymbol,
            annotationList,
            toNodeID,
            fromNodeID,
            parentSectionID,
            sectionID,
            phaseCode,
            load);
        this.connectionCd = connectionCd;
        this.swType = swType;
        this.swStatus = swStatus;
        this.swOn = swOn;
        this.swOff = swOff;
        this.cntrCkt = cntrCkt;
        this.bankKvar = bankKvar;
        this.volts = volts;
        this.capacitorList = capacitorList;
    }


    /**
     * Gets the connectionCd value for this CapacitorBank.
     * 
     * @return connectionCd
     */
    public ConnectionCode getConnectionCd() {
        return connectionCd;
    }


    /**
     * Sets the connectionCd value for this CapacitorBank.
     * 
     * @param connectionCd
     */
    public void setConnectionCd(ConnectionCode connectionCd) {
        this.connectionCd = connectionCd;
    }


    /**
     * Gets the swType value for this CapacitorBank.
     * 
     * @return swType
     */
    public SwType getSwType() {
        return swType;
    }


    /**
     * Sets the swType value for this CapacitorBank.
     * 
     * @param swType
     */
    public void setSwType(SwType swType) {
        this.swType = swType;
    }


    /**
     * Gets the swStatus value for this CapacitorBank.
     * 
     * @return swStatus
     */
    public SwStatus getSwStatus() {
        return swStatus;
    }


    /**
     * Sets the swStatus value for this CapacitorBank.
     * 
     * @param swStatus
     */
    public void setSwStatus(SwStatus swStatus) {
        this.swStatus = swStatus;
    }


    /**
     * Gets the swOn value for this CapacitorBank.
     * 
     * @return swOn
     */
    public Float getSwOn() {
        return swOn;
    }


    /**
     * Sets the swOn value for this CapacitorBank.
     * 
     * @param swOn
     */
    public void setSwOn(Float swOn) {
        this.swOn = swOn;
    }


    /**
     * Gets the swOff value for this CapacitorBank.
     * 
     * @return swOff
     */
    public Float getSwOff() {
        return swOff;
    }


    /**
     * Sets the swOff value for this CapacitorBank.
     * 
     * @param swOff
     */
    public void setSwOff(Float swOff) {
        this.swOff = swOff;
    }


    /**
     * Gets the cntrCkt value for this CapacitorBank.
     * 
     * @return cntrCkt
     */
    public ObjectRef getCntrCkt() {
        return cntrCkt;
    }


    /**
     * Sets the cntrCkt value for this CapacitorBank.
     * 
     * @param cntrCkt
     */
    public void setCntrCkt(ObjectRef cntrCkt) {
        this.cntrCkt = cntrCkt;
    }


    /**
     * Gets the bankKvar value for this CapacitorBank.
     * 
     * @return bankKvar
     */
    public Float getBankKvar() {
        return bankKvar;
    }


    /**
     * Sets the bankKvar value for this CapacitorBank.
     * 
     * @param bankKvar
     */
    public void setBankKvar(Float bankKvar) {
        this.bankKvar = bankKvar;
    }


    /**
     * Gets the volts value for this CapacitorBank.
     * 
     * @return volts
     */
    public Float getVolts() {
        return volts;
    }


    /**
     * Sets the volts value for this CapacitorBank.
     * 
     * @param volts
     */
    public void setVolts(Float volts) {
        this.volts = volts;
    }


    /**
     * Gets the capacitorList value for this CapacitorBank.
     * 
     * @return capacitorList
     */
    public Capacitor[] getCapacitorList() {
        return capacitorList;
    }


    /**
     * Sets the capacitorList value for this CapacitorBank.
     * 
     * @param capacitorList
     */
    public void setCapacitorList(Capacitor[] capacitorList) {
        this.capacitorList = capacitorList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CapacitorBank)) return false;
        CapacitorBank other = (CapacitorBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.connectionCd==null && other.getConnectionCd()==null) || 
             (this.connectionCd!=null &&
              this.connectionCd.equals(other.getConnectionCd()))) &&
            ((this.swType==null && other.getSwType()==null) || 
             (this.swType!=null &&
              this.swType.equals(other.getSwType()))) &&
            ((this.swStatus==null && other.getSwStatus()==null) || 
             (this.swStatus!=null &&
              this.swStatus.equals(other.getSwStatus()))) &&
            ((this.swOn==null && other.getSwOn()==null) || 
             (this.swOn!=null &&
              this.swOn.equals(other.getSwOn()))) &&
            ((this.swOff==null && other.getSwOff()==null) || 
             (this.swOff!=null &&
              this.swOff.equals(other.getSwOff()))) &&
            ((this.cntrCkt==null && other.getCntrCkt()==null) || 
             (this.cntrCkt!=null &&
              this.cntrCkt.equals(other.getCntrCkt()))) &&
            ((this.bankKvar==null && other.getBankKvar()==null) || 
             (this.bankKvar!=null &&
              this.bankKvar.equals(other.getBankKvar()))) &&
            ((this.volts==null && other.getVolts()==null) || 
             (this.volts!=null &&
              this.volts.equals(other.getVolts()))) &&
            ((this.capacitorList==null && other.getCapacitorList()==null) || 
             (this.capacitorList!=null &&
              java.util.Arrays.equals(this.capacitorList, other.getCapacitorList())));
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
        if (getConnectionCd() != null) {
            _hashCode += getConnectionCd().hashCode();
        }
        if (getSwType() != null) {
            _hashCode += getSwType().hashCode();
        }
        if (getSwStatus() != null) {
            _hashCode += getSwStatus().hashCode();
        }
        if (getSwOn() != null) {
            _hashCode += getSwOn().hashCode();
        }
        if (getSwOff() != null) {
            _hashCode += getSwOff().hashCode();
        }
        if (getCntrCkt() != null) {
            _hashCode += getCntrCkt().hashCode();
        }
        if (getBankKvar() != null) {
            _hashCode += getBankKvar().hashCode();
        }
        if (getVolts() != null) {
            _hashCode += getVolts().hashCode();
        }
        if (getCapacitorList() != null) {
            for (int i=0;
                 i<Array.getLength(getCapacitorList());
                 i++) {
                java.lang.Object obj = Array.get(getCapacitorList(), i);
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
    private static TypeDesc typeDesc =
        new TypeDesc(CapacitorBank.class, true);

    static {
        typeDesc.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "capacitorBank"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("connectionCd");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "connectionCd"));
        elemField.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "connectionCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("swType");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "swType"));
        elemField.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "swType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("swStatus");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "swStatus"));
        elemField.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "swStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("swOn");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "swOn"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("swOff");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "swOff"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("cntrCkt");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "cntrCkt"));
        elemField.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("bankKvar");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "bankKvar"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("volts");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "volts"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("capacitorList");
        elemField.setXmlName(new QName("http://www.multispeak.org/Version_3.0", "capacitorList"));
        elemField.setXmlType(new QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static Serializer getSerializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static Deserializer getDeserializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
