/**
 * ActionTaken.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ActionTaken implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ActionTaken(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Installed = "Installed";
    public static final java.lang.String _PlacedIntoService = "PlacedIntoService";
    public static final java.lang.String _Connected = "Connected";
    public static final java.lang.String _Disconnected = "Disconnected";
    public static final java.lang.String _Removed = "Removed";
    public static final java.lang.String _Inspected = "Inspected";
    public static final java.lang.String _Tested = "Tested";
    public static final java.lang.String _Repaired = "Repaired";
    public static final java.lang.String _Calibrated = "Calibrated";
    public static final java.lang.String _Checked = "Checked";
    public static final java.lang.String _ReturnedToInventory = "ReturnedToInventory";
    public static final java.lang.String _Retired = "Retired";
    public static final java.lang.String _Destroyed = "Destroyed";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final ActionTaken Installed = new ActionTaken(_Installed);
    public static final ActionTaken PlacedIntoService = new ActionTaken(_PlacedIntoService);
    public static final ActionTaken Connected = new ActionTaken(_Connected);
    public static final ActionTaken Disconnected = new ActionTaken(_Disconnected);
    public static final ActionTaken Removed = new ActionTaken(_Removed);
    public static final ActionTaken Inspected = new ActionTaken(_Inspected);
    public static final ActionTaken Tested = new ActionTaken(_Tested);
    public static final ActionTaken Repaired = new ActionTaken(_Repaired);
    public static final ActionTaken Calibrated = new ActionTaken(_Calibrated);
    public static final ActionTaken Checked = new ActionTaken(_Checked);
    public static final ActionTaken ReturnedToInventory = new ActionTaken(_ReturnedToInventory);
    public static final ActionTaken Retired = new ActionTaken(_Retired);
    public static final ActionTaken Destroyed = new ActionTaken(_Destroyed);
    public static final ActionTaken Other = new ActionTaken(_Other);
    public static final ActionTaken Unknown = new ActionTaken(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static ActionTaken fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ActionTaken enumeration = (ActionTaken)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ActionTaken fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ActionTaken.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
