/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class MCAddSchedule implements org.apache.thrift.TBase<MCAddSchedule, MCAddSchedule._Fields>, java.io.Serializable, Cloneable, Comparable<MCAddSchedule> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MCAddSchedule");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _SCHEDULE_FIELD_DESC = new org.apache.thrift.protocol.TField("_schedule", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField _SCRIPT_FIELD_DESC = new org.apache.thrift.protocol.TField("_script", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MCAddScheduleStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MCAddScheduleTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.MCSchedule _schedule; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _script; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _SCHEDULE((short)2, "_schedule"),
    _SCRIPT((short)3, "_script");

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
        case 1: // _BASE_MESSAGE
          return _BASE_MESSAGE;
        case 2: // _SCHEDULE
          return _SCHEDULE;
        case 3: // _SCRIPT
          return _SCRIPT;
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
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._SCHEDULE, new org.apache.thrift.meta_data.FieldMetaData("_schedule", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.MCSchedule.class)));
    tmpMap.put(_Fields._SCRIPT, new org.apache.thrift.meta_data.FieldMetaData("_script", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MCAddSchedule.class, metaDataMap);
  }

  public MCAddSchedule() {
  }

  public MCAddSchedule(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    com.cannontech.messaging.serialization.thrift.generated.MCSchedule _schedule,
    java.lang.String _script)
  {
    this();
    this._baseMessage = _baseMessage;
    this._schedule = _schedule;
    this._script = _script;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MCAddSchedule(MCAddSchedule other) {
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_schedule()) {
      this._schedule = new com.cannontech.messaging.serialization.thrift.generated.MCSchedule(other._schedule);
    }
    if (other.isSet_script()) {
      this._script = other._script;
    }
  }

  public MCAddSchedule deepCopy() {
    return new MCAddSchedule(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._schedule = null;
    this._script = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.Message get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage) {
    this._baseMessage = _baseMessage;
  }

  public void unset_baseMessage() {
    this._baseMessage = null;
  }

  /** Returns true if field _baseMessage is set (has been assigned a value) and false otherwise */
  public boolean isSet_baseMessage() {
    return this._baseMessage != null;
  }

  public void set_baseMessageIsSet(boolean value) {
    if (!value) {
      this._baseMessage = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.MCSchedule get_schedule() {
    return this._schedule;
  }

  public void set_schedule(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.MCSchedule _schedule) {
    this._schedule = _schedule;
  }

  public void unset_schedule() {
    this._schedule = null;
  }

  /** Returns true if field _schedule is set (has been assigned a value) and false otherwise */
  public boolean isSet_schedule() {
    return this._schedule != null;
  }

  public void set_scheduleIsSet(boolean value) {
    if (!value) {
      this._schedule = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_script() {
    return this._script;
  }

  public void set_script(@org.apache.thrift.annotation.Nullable java.lang.String _script) {
    this._script = _script;
  }

  public void unset_script() {
    this._script = null;
  }

  /** Returns true if field _script is set (has been assigned a value) and false otherwise */
  public boolean isSet_script() {
    return this._script != null;
  }

  public void set_scriptIsSet(boolean value) {
    if (!value) {
      this._script = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.Message)value);
      }
      break;

    case _SCHEDULE:
      if (value == null) {
        unset_schedule();
      } else {
        set_schedule((com.cannontech.messaging.serialization.thrift.generated.MCSchedule)value);
      }
      break;

    case _SCRIPT:
      if (value == null) {
        unset_script();
      } else {
        set_script((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _SCHEDULE:
      return get_schedule();

    case _SCRIPT:
      return get_script();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _BASE_MESSAGE:
      return isSet_baseMessage();
    case _SCHEDULE:
      return isSet_schedule();
    case _SCRIPT:
      return isSet_script();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof MCAddSchedule)
      return this.equals((MCAddSchedule)that);
    return false;
  }

  public boolean equals(MCAddSchedule that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__baseMessage = true && this.isSet_baseMessage();
    boolean that_present__baseMessage = true && that.isSet_baseMessage();
    if (this_present__baseMessage || that_present__baseMessage) {
      if (!(this_present__baseMessage && that_present__baseMessage))
        return false;
      if (!this._baseMessage.equals(that._baseMessage))
        return false;
    }

    boolean this_present__schedule = true && this.isSet_schedule();
    boolean that_present__schedule = true && that.isSet_schedule();
    if (this_present__schedule || that_present__schedule) {
      if (!(this_present__schedule && that_present__schedule))
        return false;
      if (!this._schedule.equals(that._schedule))
        return false;
    }

    boolean this_present__script = true && this.isSet_script();
    boolean that_present__script = true && that.isSet_script();
    if (this_present__script || that_present__script) {
      if (!(this_present__script && that_present__script))
        return false;
      if (!this._script.equals(that._script))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSet_baseMessage()) ? 131071 : 524287);
    if (isSet_baseMessage())
      hashCode = hashCode * 8191 + _baseMessage.hashCode();

    hashCode = hashCode * 8191 + ((isSet_schedule()) ? 131071 : 524287);
    if (isSet_schedule())
      hashCode = hashCode * 8191 + _schedule.hashCode();

    hashCode = hashCode * 8191 + ((isSet_script()) ? 131071 : 524287);
    if (isSet_script())
      hashCode = hashCode * 8191 + _script.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(MCAddSchedule other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_baseMessage()).compareTo(other.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, other._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_schedule()).compareTo(other.isSet_schedule());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_schedule()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._schedule, other._schedule);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_script()).compareTo(other.isSet_script());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_script()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._script, other._script);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("MCAddSchedule(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_schedule:");
    if (this._schedule == null) {
      sb.append("null");
    } else {
      sb.append(this._schedule);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_script:");
    if (this._script == null) {
      sb.append("null");
    } else {
      sb.append(this._script);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_schedule()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_schedule' is unset! Struct:" + toString());
    }

    if (!isSet_script()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_script' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_baseMessage != null) {
      _baseMessage.validate();
    }
    if (_schedule != null) {
      _schedule.validate();
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

  private static class MCAddScheduleStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MCAddScheduleStandardScheme getScheme() {
      return new MCAddScheduleStandardScheme();
    }
  }

  private static class MCAddScheduleStandardScheme extends org.apache.thrift.scheme.StandardScheme<MCAddSchedule> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MCAddSchedule struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _BASE_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _SCHEDULE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._schedule = new com.cannontech.messaging.serialization.thrift.generated.MCSchedule();
              struct._schedule.read(iprot);
              struct.set_scheduleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _SCRIPT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._script = iprot.readString();
              struct.set_scriptIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MCAddSchedule struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._schedule != null) {
        oprot.writeFieldBegin(_SCHEDULE_FIELD_DESC);
        struct._schedule.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._script != null) {
        oprot.writeFieldBegin(_SCRIPT_FIELD_DESC);
        oprot.writeString(struct._script);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MCAddScheduleTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MCAddScheduleTupleScheme getScheme() {
      return new MCAddScheduleTupleScheme();
    }
  }

  private static class MCAddScheduleTupleScheme extends org.apache.thrift.scheme.TupleScheme<MCAddSchedule> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MCAddSchedule struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      struct._schedule.write(oprot);
      oprot.writeString(struct._script);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MCAddSchedule struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._schedule = new com.cannontech.messaging.serialization.thrift.generated.MCSchedule();
      struct._schedule.read(iprot);
      struct.set_scheduleIsSet(true);
      struct._script = iprot.readString();
      struct.set_scriptIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

