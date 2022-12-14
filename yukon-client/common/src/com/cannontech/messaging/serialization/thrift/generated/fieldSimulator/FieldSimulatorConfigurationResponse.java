/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.fieldSimulator;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-08-12")
public class FieldSimulatorConfigurationResponse implements org.apache.thrift.TBase<FieldSimulatorConfigurationResponse, FieldSimulatorConfigurationResponse._Fields>, java.io.Serializable, Cloneable, Comparable<FieldSimulatorConfigurationResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FieldSimulatorConfigurationResponse");

  private static final org.apache.thrift.protocol.TField _SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("_success", org.apache.thrift.protocol.TType.BOOL, (short)1);
  private static final org.apache.thrift.protocol.TField _SETTINGS_FIELD_DESC = new org.apache.thrift.protocol.TField("_settings", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FieldSimulatorConfigurationResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FieldSimulatorConfigurationResponseTupleSchemeFactory();

  private boolean _success; // required
  private @org.apache.thrift.annotation.Nullable FieldSimulatorSettings _settings; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _SUCCESS((short)1, "_success"),
    _SETTINGS((short)2, "_settings");

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
        case 1: // _SUCCESS
          return _SUCCESS;
        case 2: // _SETTINGS
          return _SETTINGS;
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
  private static final int ___SUCCESS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("_success", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields._SETTINGS, new org.apache.thrift.meta_data.FieldMetaData("_settings", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FieldSimulatorSettings.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FieldSimulatorConfigurationResponse.class, metaDataMap);
  }

  public FieldSimulatorConfigurationResponse() {
  }

  public FieldSimulatorConfigurationResponse(
    boolean _success,
    FieldSimulatorSettings _settings)
  {
    this();
    this._success = _success;
    set_successIsSet(true);
    this._settings = _settings;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FieldSimulatorConfigurationResponse(FieldSimulatorConfigurationResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this._success = other._success;
    if (other.isSet_settings()) {
      this._settings = new FieldSimulatorSettings(other._settings);
    }
  }

  public FieldSimulatorConfigurationResponse deepCopy() {
    return new FieldSimulatorConfigurationResponse(this);
  }

  @Override
  public void clear() {
    set_successIsSet(false);
    this._success = false;
    this._settings = null;
  }

  public boolean is_success() {
    return this._success;
  }

  public void set_success(boolean _success) {
    this._success = _success;
    set_successIsSet(true);
  }

  public void unset_success() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___SUCCESS_ISSET_ID);
  }

  /** Returns true if field _success is set (has been assigned a value) and false otherwise */
  public boolean isSet_success() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___SUCCESS_ISSET_ID);
  }

  public void set_successIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___SUCCESS_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public FieldSimulatorSettings get_settings() {
    return this._settings;
  }

  public void set_settings(@org.apache.thrift.annotation.Nullable FieldSimulatorSettings _settings) {
    this._settings = _settings;
  }

  public void unset_settings() {
    this._settings = null;
  }

  /** Returns true if field _settings is set (has been assigned a value) and false otherwise */
  public boolean isSet_settings() {
    return this._settings != null;
  }

  public void set_settingsIsSet(boolean value) {
    if (!value) {
      this._settings = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _SUCCESS:
      if (value == null) {
        unset_success();
      } else {
        set_success((java.lang.Boolean)value);
      }
      break;

    case _SETTINGS:
      if (value == null) {
        unset_settings();
      } else {
        set_settings((FieldSimulatorSettings)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _SUCCESS:
      return is_success();

    case _SETTINGS:
      return get_settings();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _SUCCESS:
      return isSet_success();
    case _SETTINGS:
      return isSet_settings();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof FieldSimulatorConfigurationResponse)
      return this.equals((FieldSimulatorConfigurationResponse)that);
    return false;
  }

  public boolean equals(FieldSimulatorConfigurationResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__success = true;
    boolean that_present__success = true;
    if (this_present__success || that_present__success) {
      if (!(this_present__success && that_present__success))
        return false;
      if (this._success != that._success)
        return false;
    }

    boolean this_present__settings = true && this.isSet_settings();
    boolean that_present__settings = true && that.isSet_settings();
    if (this_present__settings || that_present__settings) {
      if (!(this_present__settings && that_present__settings))
        return false;
      if (!this._settings.equals(that._settings))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((_success) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSet_settings()) ? 131071 : 524287);
    if (isSet_settings())
      hashCode = hashCode * 8191 + _settings.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(FieldSimulatorConfigurationResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_success()).compareTo(other.isSet_success());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_success()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._success, other._success);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_settings()).compareTo(other.isSet_settings());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_settings()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._settings, other._settings);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FieldSimulatorConfigurationResponse(");
    boolean first = true;

    sb.append("_success:");
    sb.append(this._success);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_settings:");
    if (this._settings == null) {
      sb.append("null");
    } else {
      sb.append(this._settings);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_success()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_success' is unset! Struct:" + toString());
    }

    if (!isSet_settings()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_settings' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_settings != null) {
      _settings.validate();
    }
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

  private static class FieldSimulatorConfigurationResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorConfigurationResponseStandardScheme getScheme() {
      return new FieldSimulatorConfigurationResponseStandardScheme();
    }
  }

  private static class FieldSimulatorConfigurationResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<FieldSimulatorConfigurationResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FieldSimulatorConfigurationResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _SUCCESS
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._success = iprot.readBool();
              struct.set_successIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _SETTINGS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._settings = new FieldSimulatorSettings();
              struct._settings.read(iprot);
              struct.set_settingsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, FieldSimulatorConfigurationResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(_SUCCESS_FIELD_DESC);
      oprot.writeBool(struct._success);
      oprot.writeFieldEnd();
      if (struct._settings != null) {
        oprot.writeFieldBegin(_SETTINGS_FIELD_DESC);
        struct._settings.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FieldSimulatorConfigurationResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorConfigurationResponseTupleScheme getScheme() {
      return new FieldSimulatorConfigurationResponseTupleScheme();
    }
  }

  private static class FieldSimulatorConfigurationResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<FieldSimulatorConfigurationResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorConfigurationResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeBool(struct._success);
      struct._settings.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorConfigurationResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._success = iprot.readBool();
      struct.set_successIsSet(true);
      struct._settings = new FieldSimulatorSettings();
      struct._settings.read(iprot);
      struct.set_settingsIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

