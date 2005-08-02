/**
 * Customer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Customer  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String lastName;
    private java.lang.String firstName;
    private java.lang.String mName;
    private java.lang.String dBAName;
    private java.lang.String homeAc;
    private java.lang.String homePhone;
    private java.lang.String dayAc;
    private java.lang.String dayPhone;
    private java.lang.String billAddr1;
    private java.lang.String billAddr2;
    private java.lang.String billCity;
    private java.lang.String billState;
    private java.lang.String billZip;

    public Customer() {
    }

    public Customer(
           java.lang.String lastName,
           java.lang.String firstName,
           java.lang.String mName,
           java.lang.String dBAName,
           java.lang.String homeAc,
           java.lang.String homePhone,
           java.lang.String dayAc,
           java.lang.String dayPhone,
           java.lang.String billAddr1,
           java.lang.String billAddr2,
           java.lang.String billCity,
           java.lang.String billState,
           java.lang.String billZip) {
           this.lastName = lastName;
           this.firstName = firstName;
           this.mName = mName;
           this.dBAName = dBAName;
           this.homeAc = homeAc;
           this.homePhone = homePhone;
           this.dayAc = dayAc;
           this.dayPhone = dayPhone;
           this.billAddr1 = billAddr1;
           this.billAddr2 = billAddr2;
           this.billCity = billCity;
           this.billState = billState;
           this.billZip = billZip;
    }


    /**
     * Gets the lastName value for this Customer.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this Customer.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the firstName value for this Customer.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this Customer.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the mName value for this Customer.
     * 
     * @return mName
     */
    public java.lang.String getMName() {
        return mName;
    }


    /**
     * Sets the mName value for this Customer.
     * 
     * @param mName
     */
    public void setMName(java.lang.String mName) {
        this.mName = mName;
    }


    /**
     * Gets the dBAName value for this Customer.
     * 
     * @return dBAName
     */
    public java.lang.String getDBAName() {
        return dBAName;
    }


    /**
     * Sets the dBAName value for this Customer.
     * 
     * @param dBAName
     */
    public void setDBAName(java.lang.String dBAName) {
        this.dBAName = dBAName;
    }


    /**
     * Gets the homeAc value for this Customer.
     * 
     * @return homeAc
     */
    public java.lang.String getHomeAc() {
        return homeAc;
    }


    /**
     * Sets the homeAc value for this Customer.
     * 
     * @param homeAc
     */
    public void setHomeAc(java.lang.String homeAc) {
        this.homeAc = homeAc;
    }


    /**
     * Gets the homePhone value for this Customer.
     * 
     * @return homePhone
     */
    public java.lang.String getHomePhone() {
        return homePhone;
    }


    /**
     * Sets the homePhone value for this Customer.
     * 
     * @param homePhone
     */
    public void setHomePhone(java.lang.String homePhone) {
        this.homePhone = homePhone;
    }


    /**
     * Gets the dayAc value for this Customer.
     * 
     * @return dayAc
     */
    public java.lang.String getDayAc() {
        return dayAc;
    }


    /**
     * Sets the dayAc value for this Customer.
     * 
     * @param dayAc
     */
    public void setDayAc(java.lang.String dayAc) {
        this.dayAc = dayAc;
    }


    /**
     * Gets the dayPhone value for this Customer.
     * 
     * @return dayPhone
     */
    public java.lang.String getDayPhone() {
        return dayPhone;
    }


    /**
     * Sets the dayPhone value for this Customer.
     * 
     * @param dayPhone
     */
    public void setDayPhone(java.lang.String dayPhone) {
        this.dayPhone = dayPhone;
    }


    /**
     * Gets the billAddr1 value for this Customer.
     * 
     * @return billAddr1
     */
    public java.lang.String getBillAddr1() {
        return billAddr1;
    }


    /**
     * Sets the billAddr1 value for this Customer.
     * 
     * @param billAddr1
     */
    public void setBillAddr1(java.lang.String billAddr1) {
        this.billAddr1 = billAddr1;
    }


    /**
     * Gets the billAddr2 value for this Customer.
     * 
     * @return billAddr2
     */
    public java.lang.String getBillAddr2() {
        return billAddr2;
    }


    /**
     * Sets the billAddr2 value for this Customer.
     * 
     * @param billAddr2
     */
    public void setBillAddr2(java.lang.String billAddr2) {
        this.billAddr2 = billAddr2;
    }


    /**
     * Gets the billCity value for this Customer.
     * 
     * @return billCity
     */
    public java.lang.String getBillCity() {
        return billCity;
    }


    /**
     * Sets the billCity value for this Customer.
     * 
     * @param billCity
     */
    public void setBillCity(java.lang.String billCity) {
        this.billCity = billCity;
    }


    /**
     * Gets the billState value for this Customer.
     * 
     * @return billState
     */
    public java.lang.String getBillState() {
        return billState;
    }


    /**
     * Sets the billState value for this Customer.
     * 
     * @param billState
     */
    public void setBillState(java.lang.String billState) {
        this.billState = billState;
    }


    /**
     * Gets the billZip value for this Customer.
     * 
     * @return billZip
     */
    public java.lang.String getBillZip() {
        return billZip;
    }


    /**
     * Sets the billZip value for this Customer.
     * 
     * @param billZip
     */
    public void setBillZip(java.lang.String billZip) {
        this.billZip = billZip;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Customer)) return false;
        Customer other = (Customer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.mName==null && other.getMName()==null) || 
             (this.mName!=null &&
              this.mName.equals(other.getMName()))) &&
            ((this.dBAName==null && other.getDBAName()==null) || 
             (this.dBAName!=null &&
              this.dBAName.equals(other.getDBAName()))) &&
            ((this.homeAc==null && other.getHomeAc()==null) || 
             (this.homeAc!=null &&
              this.homeAc.equals(other.getHomeAc()))) &&
            ((this.homePhone==null && other.getHomePhone()==null) || 
             (this.homePhone!=null &&
              this.homePhone.equals(other.getHomePhone()))) &&
            ((this.dayAc==null && other.getDayAc()==null) || 
             (this.dayAc!=null &&
              this.dayAc.equals(other.getDayAc()))) &&
            ((this.dayPhone==null && other.getDayPhone()==null) || 
             (this.dayPhone!=null &&
              this.dayPhone.equals(other.getDayPhone()))) &&
            ((this.billAddr1==null && other.getBillAddr1()==null) || 
             (this.billAddr1!=null &&
              this.billAddr1.equals(other.getBillAddr1()))) &&
            ((this.billAddr2==null && other.getBillAddr2()==null) || 
             (this.billAddr2!=null &&
              this.billAddr2.equals(other.getBillAddr2()))) &&
            ((this.billCity==null && other.getBillCity()==null) || 
             (this.billCity!=null &&
              this.billCity.equals(other.getBillCity()))) &&
            ((this.billState==null && other.getBillState()==null) || 
             (this.billState!=null &&
              this.billState.equals(other.getBillState()))) &&
            ((this.billZip==null && other.getBillZip()==null) || 
             (this.billZip!=null &&
              this.billZip.equals(other.getBillZip())));
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
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getMName() != null) {
            _hashCode += getMName().hashCode();
        }
        if (getDBAName() != null) {
            _hashCode += getDBAName().hashCode();
        }
        if (getHomeAc() != null) {
            _hashCode += getHomeAc().hashCode();
        }
        if (getHomePhone() != null) {
            _hashCode += getHomePhone().hashCode();
        }
        if (getDayAc() != null) {
            _hashCode += getDayAc().hashCode();
        }
        if (getDayPhone() != null) {
            _hashCode += getDayPhone().hashCode();
        }
        if (getBillAddr1() != null) {
            _hashCode += getBillAddr1().hashCode();
        }
        if (getBillAddr2() != null) {
            _hashCode += getBillAddr2().hashCode();
        }
        if (getBillCity() != null) {
            _hashCode += getBillCity().hashCode();
        }
        if (getBillState() != null) {
            _hashCode += getBillState().hashCode();
        }
        if (getBillZip() != null) {
            _hashCode += getBillZip().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Customer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DBAName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dBAName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homeAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homeAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homePhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homePhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dayAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dayPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billAddr1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billAddr1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billAddr2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billAddr2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billZip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billZip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
