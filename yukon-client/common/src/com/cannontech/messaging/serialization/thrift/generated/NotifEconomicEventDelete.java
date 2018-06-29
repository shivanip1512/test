/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-06-27")
public class NotifEconomicEventDelete implements org.apache.thrift.TBase<NotifEconomicEventDelete, NotifEconomicEventDelete._Fields>, java.io.Serializable, Cloneable, Comparable<NotifEconomicEventDelete> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NotifEconomicEventDelete");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _ECONOMIC_EVENT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_economicEventId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _DELETE_START_FIELD_DESC = new org.apache.thrift.protocol.TField("_deleteStart", org.apache.thrift.protocol.TType.BOOL, (short)3);
  private static final org.apache.thrift.protocol.TField _DELETE_STOP_FIELD_DESC = new org.apache.thrift.protocol.TField("_deleteStop", org.apache.thrift.protocol.TType.BOOL, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NotifEconomicEventDeleteStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NotifEconomicEventDeleteTupleSchemeFactory();

  private com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _economicEventId; // required
  private boolean _deleteStart; // required
  private boolean _deleteStop; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _ECONOMIC_EVENT_ID((short)2, "_economicEventId"),
    _DELETE_START((short)3, "_deleteStart"),
    _DELETE_STOP((short)4, "_deleteStop");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // _BASE_MESSAGE
          return _BASE_MESSAGE;
        case 2: // _ECONOMIC_EVENT_ID
          return _ECONOMIC_EVENT_ID;
        case 3: // _DELETE_START
          return _DELETE_START;
        case 4: // _DELETE_STOP
          return _DELETE_STOP;
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
  private static final int ___ECONOMICEVENTID_ISSET_ID = 0;
  private static final int ___DELETESTART_ISSET_ID = 1;
  private static final int ___DELETESTOP_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._ECONOMIC_EVENT_ID, new org.apache.thrift.meta_data.FieldMetaData("_economicEventId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._DELETE_START, new org.apache.thrift.meta_data.FieldMetaData("_deleteStart", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields._DELETE_STOP, new org.apache.thrift.meta_data.FieldMetaData("_deleteStop", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NotifEconomicEventDelete.class, metaDataMap);
  }

  public NotifEconomicEventDelete() {
  }

  public NotifEconomicEventDelete(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _economicEventId,
    boolean _deleteStart,
    boolean _deleteStop)
  {
    this();
    this._baseMessage = _baseMessage;
    this._economicEventId = _economicEventId;
    set_economicEventIdIsSet(true);
    this._deleteStart = _deleteStart;
    set_deleteStartIsSet(true);
    this._deleteStop = _deleteStop;
    set_deleteStopIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NotifEconomicEventDelete(NotifEconomicEventDelete other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._economicEventId = other._economicEventId;
    this._deleteStart = other._deleteStart;
    this._deleteStop = other._deleteStop;
  }

  public NotifEconomicEventDelete deepCopy() {
    return new NotifEconomicEventDelete(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_economicEventIdIsSet(false);
    this._economicEventId = 0;
    set_deleteStartIsSet(false);
    this._deleteStart = false;
    set_deleteStopIsSet(false);
    this._deleteStop = false;
  }

  public com.cannontech.messaging.serialization.thrift.generated.Message get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage) {
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

  public int get_economicEventId() {
    return this._economicEventId;
  }

  public void set_economicEventId(int _economicEventId) {
    this._economicEventId = _economicEventId;
    set_economicEventIdIsSet(true);
  }

  public void unset_economicEventId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ECONOMICEVENTID_ISSET_ID);
  }

  /** Returns true if field _economicEventId is set (has been assigned a value) and false otherwise */
  public boolean isSet_economicEventId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ECONOMICEVENTID_ISSET_ID);
  }

  public void set_economicEventIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ECONOMICEVENTID_ISSET_ID, value);
  }

  public boolean is_deleteStart() {
    return this._deleteStart;
  }

  public void set_deleteStart(boolean _deleteStart) {
    this._deleteStart = _deleteStart;
    set_deleteStartIsSet(true);
  }

  public void unset_deleteStart() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___DELETESTART_ISSET_ID);
  }

  /** Returns true if field _deleteStart is set (has been assigned a value) and false otherwise */
  public boolean isSet_deleteStart() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___DELETESTART_ISSET_ID);
  }

  public void set_deleteStartIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___DELETESTART_ISSET_ID, value);
  }

  public boolean is_deleteStop() {
    return this._deleteStop;
  }

  public void set_deleteStop(boolean _deleteStop) {
    this._deleteStop = _deleteStop;
    set_deleteStopIsSet(true);
  }

  public void unset_deleteStop() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___DELETESTOP_ISSET_ID);
  }

  /** Returns true if field _deleteStop is set (has been assigned a value) and false otherwise */
  public boolean isSet_deleteStop() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___DELETESTOP_ISSET_ID);
  }

  public void set_deleteStopIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___DELETESTOP_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.Message)value);
      }
      break;

    case _ECONOMIC_EVENT_ID:
      if (value == null) {
        unset_economicEventId();
      } else {
        set_economicEventId((java.lang.Integer)value);
      }
      break;

    case _DELETE_START:
      if (value == null) {
        unset_deleteStart();
      } else {
        set_deleteStart((java.lang.Boolean)value);
      }
      break;

    case _DELETE_STOP:
      if (value == null) {
        unset_deleteStop();
      } else {
        set_deleteStop((java.lang.Boolean)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _ECONOMIC_EVENT_ID:
      return get_economicEventId();

    case _DELETE_START:
      return is_deleteStart();

    case _DELETE_STOP:
      return is_deleteStop();

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
    case _ECONOMIC_EVENT_ID:
      return isSet_economicEventId();
    case _DELETE_START:
      return isSet_deleteStart();
    case _DELETE_STOP:
      return isSet_deleteStop();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NotifEconomicEventDelete)
      return this.equals((NotifEconomicEventDelete)that);
    return false;
  }

  public boolean equals(NotifEconomicEventDelete that) {
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

    boolean this_present__economicEventId = true;
    boolean that_present__economicEventId = true;
    if (this_present__economicEventId || that_present__economicEventId) {
      if (!(this_present__economicEventId && that_present__economicEventId))
        return false;
      if (this._economicEventId != that._economicEventId)
        return false;
    }

    boolean this_present__deleteStart = true;
    boolean that_present__deleteStart = true;
    if (this_present__deleteStart || that_present__deleteStart) {
      if (!(this_present__deleteStart && that_present__deleteStart))
        return false;
      if (this._deleteStart != that._deleteStart)
        return false;
    }

    boolean this_present__deleteStop = true;
    boolean that_present__deleteStop = true;
    if (this_present__deleteStop || that_present__deleteStop) {
      if (!(this_present__deleteStop && that_present__deleteStop))
        return false;
      if (this._deleteStop != that._deleteStop)
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

    hashCode = hashCode * 8191 + _economicEventId;

    hashCode = hashCode * 8191 + ((_deleteStart) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((_deleteStop) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(NotifEconomicEventDelete other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_economicEventId()).compareTo(other.isSet_economicEventId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_economicEventId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._economicEventId, other._economicEventId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_deleteStart()).compareTo(other.isSet_deleteStart());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deleteStart()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deleteStart, other._deleteStart);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_deleteStop()).compareTo(other.isSet_deleteStop());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deleteStop()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deleteStop, other._deleteStop);
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
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NotifEconomicEventDelete(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_economicEventId:");
    sb.append(this._economicEventId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_deleteStart:");
    sb.append(this._deleteStart);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_deleteStop:");
    sb.append(this._deleteStop);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_economicEventId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_economicEventId' is unset! Struct:" + toString());
    }

    if (!isSet_deleteStart()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_deleteStart' is unset! Struct:" + toString());
    }

    if (!isSet_deleteStop()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_deleteStop' is unset! Struct:" + toString());
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

  private static class NotifEconomicEventDeleteStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifEconomicEventDeleteStandardScheme getScheme() {
      return new NotifEconomicEventDeleteStandardScheme();
    }
  }

  private static class NotifEconomicEventDeleteStandardScheme extends org.apache.thrift.scheme.StandardScheme<NotifEconomicEventDelete> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NotifEconomicEventDelete struct) throws org.apache.thrift.TException {
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
          case 2: // _ECONOMIC_EVENT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._economicEventId = iprot.readI32();
              struct.set_economicEventIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _DELETE_START
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._deleteStart = iprot.readBool();
              struct.set_deleteStartIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _DELETE_STOP
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._deleteStop = iprot.readBool();
              struct.set_deleteStopIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NotifEconomicEventDelete struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_ECONOMIC_EVENT_ID_FIELD_DESC);
      oprot.writeI32(struct._economicEventId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_DELETE_START_FIELD_DESC);
      oprot.writeBool(struct._deleteStart);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_DELETE_STOP_FIELD_DESC);
      oprot.writeBool(struct._deleteStop);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NotifEconomicEventDeleteTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifEconomicEventDeleteTupleScheme getScheme() {
      return new NotifEconomicEventDeleteTupleScheme();
    }
  }

  private static class NotifEconomicEventDeleteTupleScheme extends org.apache.thrift.scheme.TupleScheme<NotifEconomicEventDelete> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NotifEconomicEventDelete struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._economicEventId);
      oprot.writeBool(struct._deleteStart);
      oprot.writeBool(struct._deleteStop);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NotifEconomicEventDelete struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._economicEventId = iprot.readI32();
      struct.set_economicEventIdIsSet(true);
      struct._deleteStart = iprot.readBool();
      struct.set_deleteStartIsSet(true);
      struct._deleteStop = iprot.readBool();
      struct.set_deleteStopIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

