/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class NotifCurtailmentEvent implements org.apache.thrift.TBase<NotifCurtailmentEvent, NotifCurtailmentEvent._Fields>, java.io.Serializable, Cloneable, Comparable<NotifCurtailmentEvent> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NotifCurtailmentEvent");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _CURTAILMENT_EVENT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_curtailmentEventId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _ACTION_FIELD_DESC = new org.apache.thrift.protocol.TField("_action", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NotifCurtailmentEventStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NotifCurtailmentEventTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _curtailmentEventId; // required
  private int _action; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _CURTAILMENT_EVENT_ID((short)2, "_curtailmentEventId"),
    _ACTION((short)3, "_action");

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
        case 2: // _CURTAILMENT_EVENT_ID
          return _CURTAILMENT_EVENT_ID;
        case 3: // _ACTION
          return _ACTION;
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
  private static final int ___CURTAILMENTEVENTID_ISSET_ID = 0;
  private static final int ___ACTION_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._CURTAILMENT_EVENT_ID, new org.apache.thrift.meta_data.FieldMetaData("_curtailmentEventId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._ACTION, new org.apache.thrift.meta_data.FieldMetaData("_action", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NotifCurtailmentEvent.class, metaDataMap);
  }

  public NotifCurtailmentEvent() {
  }

  public NotifCurtailmentEvent(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _curtailmentEventId,
    int _action)
  {
    this();
    this._baseMessage = _baseMessage;
    this._curtailmentEventId = _curtailmentEventId;
    set_curtailmentEventIdIsSet(true);
    this._action = _action;
    set_actionIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NotifCurtailmentEvent(NotifCurtailmentEvent other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._curtailmentEventId = other._curtailmentEventId;
    this._action = other._action;
  }

  public NotifCurtailmentEvent deepCopy() {
    return new NotifCurtailmentEvent(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_curtailmentEventIdIsSet(false);
    this._curtailmentEventId = 0;
    set_actionIsSet(false);
    this._action = 0;
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

  public int get_curtailmentEventId() {
    return this._curtailmentEventId;
  }

  public void set_curtailmentEventId(int _curtailmentEventId) {
    this._curtailmentEventId = _curtailmentEventId;
    set_curtailmentEventIdIsSet(true);
  }

  public void unset_curtailmentEventId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CURTAILMENTEVENTID_ISSET_ID);
  }

  /** Returns true if field _curtailmentEventId is set (has been assigned a value) and false otherwise */
  public boolean isSet_curtailmentEventId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CURTAILMENTEVENTID_ISSET_ID);
  }

  public void set_curtailmentEventIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CURTAILMENTEVENTID_ISSET_ID, value);
  }

  public int get_action() {
    return this._action;
  }

  public void set_action(int _action) {
    this._action = _action;
    set_actionIsSet(true);
  }

  public void unset_action() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ACTION_ISSET_ID);
  }

  /** Returns true if field _action is set (has been assigned a value) and false otherwise */
  public boolean isSet_action() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ACTION_ISSET_ID);
  }

  public void set_actionIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ACTION_ISSET_ID, value);
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

    case _CURTAILMENT_EVENT_ID:
      if (value == null) {
        unset_curtailmentEventId();
      } else {
        set_curtailmentEventId((java.lang.Integer)value);
      }
      break;

    case _ACTION:
      if (value == null) {
        unset_action();
      } else {
        set_action((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _CURTAILMENT_EVENT_ID:
      return get_curtailmentEventId();

    case _ACTION:
      return get_action();

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
    case _CURTAILMENT_EVENT_ID:
      return isSet_curtailmentEventId();
    case _ACTION:
      return isSet_action();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NotifCurtailmentEvent)
      return this.equals((NotifCurtailmentEvent)that);
    return false;
  }

  public boolean equals(NotifCurtailmentEvent that) {
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

    boolean this_present__curtailmentEventId = true;
    boolean that_present__curtailmentEventId = true;
    if (this_present__curtailmentEventId || that_present__curtailmentEventId) {
      if (!(this_present__curtailmentEventId && that_present__curtailmentEventId))
        return false;
      if (this._curtailmentEventId != that._curtailmentEventId)
        return false;
    }

    boolean this_present__action = true;
    boolean that_present__action = true;
    if (this_present__action || that_present__action) {
      if (!(this_present__action && that_present__action))
        return false;
      if (this._action != that._action)
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

    hashCode = hashCode * 8191 + _curtailmentEventId;

    hashCode = hashCode * 8191 + _action;

    return hashCode;
  }

  @Override
  public int compareTo(NotifCurtailmentEvent other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_curtailmentEventId()).compareTo(other.isSet_curtailmentEventId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_curtailmentEventId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._curtailmentEventId, other._curtailmentEventId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_action()).compareTo(other.isSet_action());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_action()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._action, other._action);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NotifCurtailmentEvent(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_curtailmentEventId:");
    sb.append(this._curtailmentEventId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_action:");
    sb.append(this._action);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_curtailmentEventId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_curtailmentEventId' is unset! Struct:" + toString());
    }

    if (!isSet_action()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_action' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_baseMessage != null) {
      _baseMessage.validate();
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

  private static class NotifCurtailmentEventStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifCurtailmentEventStandardScheme getScheme() {
      return new NotifCurtailmentEventStandardScheme();
    }
  }

  private static class NotifCurtailmentEventStandardScheme extends org.apache.thrift.scheme.StandardScheme<NotifCurtailmentEvent> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NotifCurtailmentEvent struct) throws org.apache.thrift.TException {
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
          case 2: // _CURTAILMENT_EVENT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._curtailmentEventId = iprot.readI32();
              struct.set_curtailmentEventIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _ACTION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._action = iprot.readI32();
              struct.set_actionIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NotifCurtailmentEvent struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_CURTAILMENT_EVENT_ID_FIELD_DESC);
      oprot.writeI32(struct._curtailmentEventId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_ACTION_FIELD_DESC);
      oprot.writeI32(struct._action);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NotifCurtailmentEventTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifCurtailmentEventTupleScheme getScheme() {
      return new NotifCurtailmentEventTupleScheme();
    }
  }

  private static class NotifCurtailmentEventTupleScheme extends org.apache.thrift.scheme.TupleScheme<NotifCurtailmentEvent> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NotifCurtailmentEvent struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._curtailmentEventId);
      oprot.writeI32(struct._action);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NotifCurtailmentEvent struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._curtailmentEventId = iprot.readI32();
      struct.set_curtailmentEventIdIsSet(true);
      struct._action = iprot.readI32();
      struct.set_actionIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

