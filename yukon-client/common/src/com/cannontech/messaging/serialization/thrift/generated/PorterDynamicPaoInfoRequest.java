/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PorterDynamicPaoInfoRequest implements org.apache.thrift.TBase<PorterDynamicPaoInfoRequest, PorterDynamicPaoInfoRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PorterDynamicPaoInfoRequest");

  private static final org.apache.thrift.protocol.TField _DEVICE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_deviceId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField _KEYS_FIELD_DESC = new org.apache.thrift.protocol.TField("_keys", org.apache.thrift.protocol.TType.SET, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PorterDynamicPaoInfoRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PorterDynamicPaoInfoRequestTupleSchemeFactory());
  }

  private int _deviceId; // required
  private Set<DynamicPaoInfoKeys> _keys; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _DEVICE_ID((short)1, "_deviceId"),
    _KEYS((short)2, "_keys");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // _DEVICE_ID
          return _DEVICE_ID;
        case 2: // _KEYS
          return _KEYS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int ___DEVICEID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._DEVICE_ID, new org.apache.thrift.meta_data.FieldMetaData("_deviceId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._KEYS, new org.apache.thrift.meta_data.FieldMetaData("_keys", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DynamicPaoInfoKeys.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PorterDynamicPaoInfoRequest.class, metaDataMap);
  }

  public PorterDynamicPaoInfoRequest() {
  }

  public PorterDynamicPaoInfoRequest(
    int _deviceId,
    Set<DynamicPaoInfoKeys> _keys)
  {
    this();
    this._deviceId = _deviceId;
    set_deviceIdIsSet(true);
    this._keys = _keys;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PorterDynamicPaoInfoRequest(PorterDynamicPaoInfoRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this._deviceId = other._deviceId;
    if (other.isSet_keys()) {
      Set<DynamicPaoInfoKeys> __this___keys = new HashSet<DynamicPaoInfoKeys>();
      for (DynamicPaoInfoKeys other_element : other._keys) {
        __this___keys.add(other_element);
      }
      this._keys = __this___keys;
    }
  }

  public PorterDynamicPaoInfoRequest deepCopy() {
    return new PorterDynamicPaoInfoRequest(this);
  }

  @Override
  public void clear() {
    set_deviceIdIsSet(false);
    this._deviceId = 0;
    this._keys = null;
  }

  public int get_deviceId() {
    return this._deviceId;
  }

  public void set_deviceId(int _deviceId) {
    this._deviceId = _deviceId;
    set_deviceIdIsSet(true);
  }

  public void unset_deviceId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___DEVICEID_ISSET_ID);
  }

  /** Returns true if field _deviceId is set (has been assigned a value) and false otherwise */
  public boolean isSet_deviceId() {
    return EncodingUtils.testBit(__isset_bitfield, ___DEVICEID_ISSET_ID);
  }

  public void set_deviceIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___DEVICEID_ISSET_ID, value);
  }

  public int get_keysSize() {
    return (this._keys == null) ? 0 : this._keys.size();
  }

  public java.util.Iterator<DynamicPaoInfoKeys> get_keysIterator() {
    return (this._keys == null) ? null : this._keys.iterator();
  }

  public void addTo_keys(DynamicPaoInfoKeys elem) {
    if (this._keys == null) {
      this._keys = new HashSet<DynamicPaoInfoKeys>();
    }
    this._keys.add(elem);
  }

  public Set<DynamicPaoInfoKeys> get_keys() {
    return this._keys;
  }

  public void set_keys(Set<DynamicPaoInfoKeys> _keys) {
    this._keys = _keys;
  }

  public void unset_keys() {
    this._keys = null;
  }

  /** Returns true if field _keys is set (has been assigned a value) and false otherwise */
  public boolean isSet_keys() {
    return this._keys != null;
  }

  public void set_keysIsSet(boolean value) {
    if (!value) {
      this._keys = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case _DEVICE_ID:
      if (value == null) {
        unset_deviceId();
      } else {
        set_deviceId((Integer)value);
      }
      break;

    case _KEYS:
      if (value == null) {
        unset_keys();
      } else {
        set_keys((Set<DynamicPaoInfoKeys>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case _DEVICE_ID:
      return Integer.valueOf(get_deviceId());

    case _KEYS:
      return get_keys();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case _DEVICE_ID:
      return isSet_deviceId();
    case _KEYS:
      return isSet_keys();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PorterDynamicPaoInfoRequest)
      return this.equals((PorterDynamicPaoInfoRequest)that);
    return false;
  }

  public boolean equals(PorterDynamicPaoInfoRequest that) {
    if (that == null)
      return false;

    boolean this_present__deviceId = true;
    boolean that_present__deviceId = true;
    if (this_present__deviceId || that_present__deviceId) {
      if (!(this_present__deviceId && that_present__deviceId))
        return false;
      if (this._deviceId != that._deviceId)
        return false;
    }

    boolean this_present__keys = true && this.isSet_keys();
    boolean that_present__keys = true && that.isSet_keys();
    if (this_present__keys || that_present__keys) {
      if (!(this_present__keys && that_present__keys))
        return false;
      if (!this._keys.equals(that._keys))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(PorterDynamicPaoInfoRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    PorterDynamicPaoInfoRequest typedOther = (PorterDynamicPaoInfoRequest)other;

    lastComparison = Boolean.valueOf(isSet_deviceId()).compareTo(typedOther.isSet_deviceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deviceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deviceId, typedOther._deviceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_keys()).compareTo(typedOther.isSet_keys());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_keys()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._keys, typedOther._keys);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("PorterDynamicPaoInfoRequest(");
    boolean first = true;

    sb.append("_deviceId:");
    sb.append(this._deviceId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_keys:");
    if (this._keys == null) {
      sb.append("null");
    } else {
      sb.append(this._keys);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_deviceId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_deviceId' is unset! Struct:" + toString());
    }

    if (!isSet_keys()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_keys' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PorterDynamicPaoInfoRequestStandardSchemeFactory implements SchemeFactory {
    public PorterDynamicPaoInfoRequestStandardScheme getScheme() {
      return new PorterDynamicPaoInfoRequestStandardScheme();
    }
  }

  private static class PorterDynamicPaoInfoRequestStandardScheme extends StandardScheme<PorterDynamicPaoInfoRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PorterDynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _DEVICE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._deviceId = iprot.readI32();
              struct.set_deviceIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _KEYS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set0 = iprot.readSetBegin();
                struct._keys = new HashSet<DynamicPaoInfoKeys>(2*_set0.size);
                for (int _i1 = 0; _i1 < _set0.size; ++_i1)
                {
                  DynamicPaoInfoKeys _elem2; // required
                  _elem2 = DynamicPaoInfoKeys.findByValue(iprot.readI32());
                  struct._keys.add(_elem2);
                }
                iprot.readSetEnd();
              }
              struct.set_keysIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, PorterDynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(_DEVICE_ID_FIELD_DESC);
      oprot.writeI32(struct._deviceId);
      oprot.writeFieldEnd();
      if (struct._keys != null) {
        oprot.writeFieldBegin(_KEYS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, struct._keys.size()));
          for (DynamicPaoInfoKeys _iter3 : struct._keys)
          {
            oprot.writeI32(_iter3.getValue());
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PorterDynamicPaoInfoRequestTupleSchemeFactory implements SchemeFactory {
    public PorterDynamicPaoInfoRequestTupleScheme getScheme() {
      return new PorterDynamicPaoInfoRequestTupleScheme();
    }
  }

  private static class PorterDynamicPaoInfoRequestTupleScheme extends TupleScheme<PorterDynamicPaoInfoRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PorterDynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct._deviceId);
      {
        oprot.writeI32(struct._keys.size());
        for (DynamicPaoInfoKeys _iter4 : struct._keys)
        {
          oprot.writeI32(_iter4.getValue());
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PorterDynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct._deviceId = iprot.readI32();
      struct.set_deviceIdIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set5 = new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._keys = new HashSet<DynamicPaoInfoKeys>(2*_set5.size);
        for (int _i6 = 0; _i6 < _set5.size; ++_i6)
        {
          DynamicPaoInfoKeys _elem7; // required
          _elem7 = DynamicPaoInfoKeys.findByValue(iprot.readI32());
          struct._keys.add(_elem7);
        }
      }
      struct.set_keysIsSet(true);
    }
  }

}

