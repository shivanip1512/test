/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.fieldSimulator;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-08-12")
public class FieldSimulatorSettings implements org.apache.thrift.TBase<FieldSimulatorSettings, FieldSimulatorSettings._Fields>, java.io.Serializable, Cloneable, Comparable<FieldSimulatorSettings> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FieldSimulatorSettings");

  private static final org.apache.thrift.protocol.TField _DEVICE_GROUP_FIELD_DESC = new org.apache.thrift.protocol.TField("_deviceGroup", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField _DEVICE_CONFIG_FAILURE_RATE_FIELD_DESC = new org.apache.thrift.protocol.TField("_deviceConfigFailureRate", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FieldSimulatorSettingsStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FieldSimulatorSettingsTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String _deviceGroup; // required
  private int _deviceConfigFailureRate; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _DEVICE_GROUP((short)1, "_deviceGroup"),
    _DEVICE_CONFIG_FAILURE_RATE((short)2, "_deviceConfigFailureRate");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // _DEVICE_GROUP
          return _DEVICE_GROUP;
        case 2: // _DEVICE_CONFIG_FAILURE_RATE
          return _DEVICE_CONFIG_FAILURE_RATE;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int ___DEVICECONFIGFAILURERATE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._DEVICE_GROUP, new org.apache.thrift.meta_data.FieldMetaData("_deviceGroup", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._DEVICE_CONFIG_FAILURE_RATE, new org.apache.thrift.meta_data.FieldMetaData("_deviceConfigFailureRate", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FieldSimulatorSettings.class, metaDataMap);
  }

  public FieldSimulatorSettings() {
  }

  public FieldSimulatorSettings(
    java.lang.String _deviceGroup,
    int _deviceConfigFailureRate)
  {
    this();
    this._deviceGroup = _deviceGroup;
    this._deviceConfigFailureRate = _deviceConfigFailureRate;
    set_deviceConfigFailureRateIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FieldSimulatorSettings(FieldSimulatorSettings other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_deviceGroup()) {
      this._deviceGroup = other._deviceGroup;
    }
    this._deviceConfigFailureRate = other._deviceConfigFailureRate;
  }

  public FieldSimulatorSettings deepCopy() {
    return new FieldSimulatorSettings(this);
  }

  @Override
  public void clear() {
    this._deviceGroup = null;
    set_deviceConfigFailureRateIsSet(false);
    this._deviceConfigFailureRate = 0;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_deviceGroup() {
    return this._deviceGroup;
  }

  public void set_deviceGroup(@org.apache.thrift.annotation.Nullable java.lang.String _deviceGroup) {
    this._deviceGroup = _deviceGroup;
  }

  public void unset_deviceGroup() {
    this._deviceGroup = null;
  }

  /** Returns true if field _deviceGroup is set (has been assigned a value) and false otherwise */
  public boolean isSet_deviceGroup() {
    return this._deviceGroup != null;
  }

  public void set_deviceGroupIsSet(boolean value) {
    if (!value) {
      this._deviceGroup = null;
    }
  }

  public int get_deviceConfigFailureRate() {
    return this._deviceConfigFailureRate;
  }

  public void set_deviceConfigFailureRate(int _deviceConfigFailureRate) {
    this._deviceConfigFailureRate = _deviceConfigFailureRate;
    set_deviceConfigFailureRateIsSet(true);
  }

  public void unset_deviceConfigFailureRate() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___DEVICECONFIGFAILURERATE_ISSET_ID);
  }

  /** Returns true if field _deviceConfigFailureRate is set (has been assigned a value) and false otherwise */
  public boolean isSet_deviceConfigFailureRate() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___DEVICECONFIGFAILURERATE_ISSET_ID);
  }

  public void set_deviceConfigFailureRateIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___DEVICECONFIGFAILURERATE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _DEVICE_GROUP:
      if (value == null) {
        unset_deviceGroup();
      } else {
        set_deviceGroup((java.lang.String)value);
      }
      break;

    case _DEVICE_CONFIG_FAILURE_RATE:
      if (value == null) {
        unset_deviceConfigFailureRate();
      } else {
        set_deviceConfigFailureRate((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _DEVICE_GROUP:
      return get_deviceGroup();

    case _DEVICE_CONFIG_FAILURE_RATE:
      return get_deviceConfigFailureRate();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _DEVICE_GROUP:
      return isSet_deviceGroup();
    case _DEVICE_CONFIG_FAILURE_RATE:
      return isSet_deviceConfigFailureRate();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof FieldSimulatorSettings)
      return this.equals((FieldSimulatorSettings)that);
    return false;
  }

  public boolean equals(FieldSimulatorSettings that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__deviceGroup = true && this.isSet_deviceGroup();
    boolean that_present__deviceGroup = true && that.isSet_deviceGroup();
    if (this_present__deviceGroup || that_present__deviceGroup) {
      if (!(this_present__deviceGroup && that_present__deviceGroup))
        return false;
      if (!this._deviceGroup.equals(that._deviceGroup))
        return false;
    }

    boolean this_present__deviceConfigFailureRate = true;
    boolean that_present__deviceConfigFailureRate = true;
    if (this_present__deviceConfigFailureRate || that_present__deviceConfigFailureRate) {
      if (!(this_present__deviceConfigFailureRate && that_present__deviceConfigFailureRate))
        return false;
      if (this._deviceConfigFailureRate != that._deviceConfigFailureRate)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSet_deviceGroup()) ? 131071 : 524287);
    if (isSet_deviceGroup())
      hashCode = hashCode * 8191 + _deviceGroup.hashCode();

    hashCode = hashCode * 8191 + _deviceConfigFailureRate;

    return hashCode;
  }

  @Override
  public int compareTo(FieldSimulatorSettings other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_deviceGroup()).compareTo(other.isSet_deviceGroup());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deviceGroup()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deviceGroup, other._deviceGroup);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_deviceConfigFailureRate()).compareTo(other.isSet_deviceConfigFailureRate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deviceConfigFailureRate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deviceConfigFailureRate, other._deviceConfigFailureRate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FieldSimulatorSettings(");
    boolean first = true;

    sb.append("_deviceGroup:");
    if (this._deviceGroup == null) {
      sb.append("null");
    } else {
      sb.append(this._deviceGroup);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_deviceConfigFailureRate:");
    sb.append(this._deviceConfigFailureRate);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_deviceGroup()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_deviceGroup' is unset! Struct:" + toString());
    }

    if (!isSet_deviceConfigFailureRate()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_deviceConfigFailureRate' is unset! Struct:" + toString());
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FieldSimulatorSettingsStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorSettingsStandardScheme getScheme() {
      return new FieldSimulatorSettingsStandardScheme();
    }
  }

  private static class FieldSimulatorSettingsStandardScheme extends org.apache.thrift.scheme.StandardScheme<FieldSimulatorSettings> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FieldSimulatorSettings struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _DEVICE_GROUP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._deviceGroup = iprot.readString();
              struct.set_deviceGroupIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _DEVICE_CONFIG_FAILURE_RATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._deviceConfigFailureRate = iprot.readI32();
              struct.set_deviceConfigFailureRateIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, FieldSimulatorSettings struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._deviceGroup != null) {
        oprot.writeFieldBegin(_DEVICE_GROUP_FIELD_DESC);
        oprot.writeString(struct._deviceGroup);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_DEVICE_CONFIG_FAILURE_RATE_FIELD_DESC);
      oprot.writeI32(struct._deviceConfigFailureRate);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FieldSimulatorSettingsTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorSettingsTupleScheme getScheme() {
      return new FieldSimulatorSettingsTupleScheme();
    }
  }

  private static class FieldSimulatorSettingsTupleScheme extends org.apache.thrift.scheme.TupleScheme<FieldSimulatorSettings> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorSettings struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct._deviceGroup);
      oprot.writeI32(struct._deviceConfigFailureRate);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorSettings struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._deviceGroup = iprot.readString();
      struct.set_deviceGroupIsSet(true);
      struct._deviceConfigFailureRate = iprot.readI32();
      struct.set_deviceConfigFailureRateIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

