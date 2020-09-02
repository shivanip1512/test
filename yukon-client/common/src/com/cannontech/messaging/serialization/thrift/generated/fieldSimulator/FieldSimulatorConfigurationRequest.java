/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.fieldSimulator;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-08-12")
public class FieldSimulatorConfigurationRequest implements org.apache.thrift.TBase<FieldSimulatorConfigurationRequest, FieldSimulatorConfigurationRequest._Fields>, java.io.Serializable, Cloneable, Comparable<FieldSimulatorConfigurationRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FieldSimulatorConfigurationRequest");

  private static final org.apache.thrift.protocol.TField _SETTINGS_FIELD_DESC = new org.apache.thrift.protocol.TField("_settings", org.apache.thrift.protocol.TType.STRUCT, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FieldSimulatorConfigurationRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FieldSimulatorConfigurationRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable FieldSimulatorSettings _settings; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _SETTINGS((short)1, "_settings");

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
        case 1: // _SETTINGS
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
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._SETTINGS, new org.apache.thrift.meta_data.FieldMetaData("_settings", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FieldSimulatorSettings.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FieldSimulatorConfigurationRequest.class, metaDataMap);
  }

  public FieldSimulatorConfigurationRequest() {
  }

  public FieldSimulatorConfigurationRequest(
    FieldSimulatorSettings _settings)
  {
    this();
    this._settings = _settings;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FieldSimulatorConfigurationRequest(FieldSimulatorConfigurationRequest other) {
    if (other.isSet_settings()) {
      this._settings = new FieldSimulatorSettings(other._settings);
    }
  }

  public FieldSimulatorConfigurationRequest deepCopy() {
    return new FieldSimulatorConfigurationRequest(this);
  }

  @Override
  public void clear() {
    this._settings = null;
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
    case _SETTINGS:
      return isSet_settings();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof FieldSimulatorConfigurationRequest)
      return this.equals((FieldSimulatorConfigurationRequest)that);
    return false;
  }

  public boolean equals(FieldSimulatorConfigurationRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

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

    hashCode = hashCode * 8191 + ((isSet_settings()) ? 131071 : 524287);
    if (isSet_settings())
      hashCode = hashCode * 8191 + _settings.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(FieldSimulatorConfigurationRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FieldSimulatorConfigurationRequest(");
    boolean first = true;

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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FieldSimulatorConfigurationRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorConfigurationRequestStandardScheme getScheme() {
      return new FieldSimulatorConfigurationRequestStandardScheme();
    }
  }

  private static class FieldSimulatorConfigurationRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<FieldSimulatorConfigurationRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FieldSimulatorConfigurationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _SETTINGS
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, FieldSimulatorConfigurationRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._settings != null) {
        oprot.writeFieldBegin(_SETTINGS_FIELD_DESC);
        struct._settings.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FieldSimulatorConfigurationRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FieldSimulatorConfigurationRequestTupleScheme getScheme() {
      return new FieldSimulatorConfigurationRequestTupleScheme();
    }
  }

  private static class FieldSimulatorConfigurationRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<FieldSimulatorConfigurationRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorConfigurationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._settings.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FieldSimulatorConfigurationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._settings = new FieldSimulatorSettings();
      struct._settings.read(iprot);
      struct.set_settingsIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

