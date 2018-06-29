/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-06-27")
public class NotifLMControl implements org.apache.thrift.TBase<NotifLMControl, NotifLMControl._Fields>, java.io.Serializable, Cloneable, Comparable<NotifLMControl> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NotifLMControl");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _NOTIF_GROUP_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("_notifGroupIds", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField _NOTIF_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("_notifType", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _PROGRAM_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_programId", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _START_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("_startTime", org.apache.thrift.protocol.TType.I64, (short)5);
  private static final org.apache.thrift.protocol.TField _STOP_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("_stopTime", org.apache.thrift.protocol.TType.I64, (short)6);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NotifLMControlStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NotifLMControlTupleSchemeFactory();

  private com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private java.util.List<java.lang.Integer> _notifGroupIds; // required
  private int _notifType; // required
  private int _programId; // required
  private long _startTime; // required
  private long _stopTime; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _NOTIF_GROUP_IDS((short)2, "_notifGroupIds"),
    _NOTIF_TYPE((short)3, "_notifType"),
    _PROGRAM_ID((short)4, "_programId"),
    _START_TIME((short)5, "_startTime"),
    _STOP_TIME((short)6, "_stopTime");

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
        case 2: // _NOTIF_GROUP_IDS
          return _NOTIF_GROUP_IDS;
        case 3: // _NOTIF_TYPE
          return _NOTIF_TYPE;
        case 4: // _PROGRAM_ID
          return _PROGRAM_ID;
        case 5: // _START_TIME
          return _START_TIME;
        case 6: // _STOP_TIME
          return _STOP_TIME;
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
  private static final int ___NOTIFTYPE_ISSET_ID = 0;
  private static final int ___PROGRAMID_ISSET_ID = 1;
  private static final int ___STARTTIME_ISSET_ID = 2;
  private static final int ___STOPTIME_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._NOTIF_GROUP_IDS, new org.apache.thrift.meta_data.FieldMetaData("_notifGroupIds", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields._NOTIF_TYPE, new org.apache.thrift.meta_data.FieldMetaData("_notifType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._PROGRAM_ID, new org.apache.thrift.meta_data.FieldMetaData("_programId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._START_TIME, new org.apache.thrift.meta_data.FieldMetaData("_startTime", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields._STOP_TIME, new org.apache.thrift.meta_data.FieldMetaData("_stopTime", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NotifLMControl.class, metaDataMap);
  }

  public NotifLMControl() {
  }

  public NotifLMControl(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    java.util.List<java.lang.Integer> _notifGroupIds,
    int _notifType,
    int _programId,
    long _startTime,
    long _stopTime)
  {
    this();
    this._baseMessage = _baseMessage;
    this._notifGroupIds = _notifGroupIds;
    this._notifType = _notifType;
    set_notifTypeIsSet(true);
    this._programId = _programId;
    set_programIdIsSet(true);
    this._startTime = _startTime;
    set_startTimeIsSet(true);
    this._stopTime = _stopTime;
    set_stopTimeIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NotifLMControl(NotifLMControl other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_notifGroupIds()) {
      java.util.List<java.lang.Integer> __this___notifGroupIds = new java.util.ArrayList<java.lang.Integer>(other._notifGroupIds);
      this._notifGroupIds = __this___notifGroupIds;
    }
    this._notifType = other._notifType;
    this._programId = other._programId;
    this._startTime = other._startTime;
    this._stopTime = other._stopTime;
  }

  public NotifLMControl deepCopy() {
    return new NotifLMControl(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._notifGroupIds = null;
    set_notifTypeIsSet(false);
    this._notifType = 0;
    set_programIdIsSet(false);
    this._programId = 0;
    set_startTimeIsSet(false);
    this._startTime = 0;
    set_stopTimeIsSet(false);
    this._stopTime = 0;
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

  public int get_notifGroupIdsSize() {
    return (this._notifGroupIds == null) ? 0 : this._notifGroupIds.size();
  }

  public java.util.Iterator<java.lang.Integer> get_notifGroupIdsIterator() {
    return (this._notifGroupIds == null) ? null : this._notifGroupIds.iterator();
  }

  public void addTo_notifGroupIds(int elem) {
    if (this._notifGroupIds == null) {
      this._notifGroupIds = new java.util.ArrayList<java.lang.Integer>();
    }
    this._notifGroupIds.add(elem);
  }

  public java.util.List<java.lang.Integer> get_notifGroupIds() {
    return this._notifGroupIds;
  }

  public void set_notifGroupIds(java.util.List<java.lang.Integer> _notifGroupIds) {
    this._notifGroupIds = _notifGroupIds;
  }

  public void unset_notifGroupIds() {
    this._notifGroupIds = null;
  }

  /** Returns true if field _notifGroupIds is set (has been assigned a value) and false otherwise */
  public boolean isSet_notifGroupIds() {
    return this._notifGroupIds != null;
  }

  public void set_notifGroupIdsIsSet(boolean value) {
    if (!value) {
      this._notifGroupIds = null;
    }
  }

  public int get_notifType() {
    return this._notifType;
  }

  public void set_notifType(int _notifType) {
    this._notifType = _notifType;
    set_notifTypeIsSet(true);
  }

  public void unset_notifType() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___NOTIFTYPE_ISSET_ID);
  }

  /** Returns true if field _notifType is set (has been assigned a value) and false otherwise */
  public boolean isSet_notifType() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___NOTIFTYPE_ISSET_ID);
  }

  public void set_notifTypeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___NOTIFTYPE_ISSET_ID, value);
  }

  public int get_programId() {
    return this._programId;
  }

  public void set_programId(int _programId) {
    this._programId = _programId;
    set_programIdIsSet(true);
  }

  public void unset_programId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___PROGRAMID_ISSET_ID);
  }

  /** Returns true if field _programId is set (has been assigned a value) and false otherwise */
  public boolean isSet_programId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___PROGRAMID_ISSET_ID);
  }

  public void set_programIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___PROGRAMID_ISSET_ID, value);
  }

  public long get_startTime() {
    return this._startTime;
  }

  public void set_startTime(long _startTime) {
    this._startTime = _startTime;
    set_startTimeIsSet(true);
  }

  public void unset_startTime() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___STARTTIME_ISSET_ID);
  }

  /** Returns true if field _startTime is set (has been assigned a value) and false otherwise */
  public boolean isSet_startTime() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___STARTTIME_ISSET_ID);
  }

  public void set_startTimeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___STARTTIME_ISSET_ID, value);
  }

  public long get_stopTime() {
    return this._stopTime;
  }

  public void set_stopTime(long _stopTime) {
    this._stopTime = _stopTime;
    set_stopTimeIsSet(true);
  }

  public void unset_stopTime() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___STOPTIME_ISSET_ID);
  }

  /** Returns true if field _stopTime is set (has been assigned a value) and false otherwise */
  public boolean isSet_stopTime() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___STOPTIME_ISSET_ID);
  }

  public void set_stopTimeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___STOPTIME_ISSET_ID, value);
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

    case _NOTIF_GROUP_IDS:
      if (value == null) {
        unset_notifGroupIds();
      } else {
        set_notifGroupIds((java.util.List<java.lang.Integer>)value);
      }
      break;

    case _NOTIF_TYPE:
      if (value == null) {
        unset_notifType();
      } else {
        set_notifType((java.lang.Integer)value);
      }
      break;

    case _PROGRAM_ID:
      if (value == null) {
        unset_programId();
      } else {
        set_programId((java.lang.Integer)value);
      }
      break;

    case _START_TIME:
      if (value == null) {
        unset_startTime();
      } else {
        set_startTime((java.lang.Long)value);
      }
      break;

    case _STOP_TIME:
      if (value == null) {
        unset_stopTime();
      } else {
        set_stopTime((java.lang.Long)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _NOTIF_GROUP_IDS:
      return get_notifGroupIds();

    case _NOTIF_TYPE:
      return get_notifType();

    case _PROGRAM_ID:
      return get_programId();

    case _START_TIME:
      return get_startTime();

    case _STOP_TIME:
      return get_stopTime();

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
    case _NOTIF_GROUP_IDS:
      return isSet_notifGroupIds();
    case _NOTIF_TYPE:
      return isSet_notifType();
    case _PROGRAM_ID:
      return isSet_programId();
    case _START_TIME:
      return isSet_startTime();
    case _STOP_TIME:
      return isSet_stopTime();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NotifLMControl)
      return this.equals((NotifLMControl)that);
    return false;
  }

  public boolean equals(NotifLMControl that) {
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

    boolean this_present__notifGroupIds = true && this.isSet_notifGroupIds();
    boolean that_present__notifGroupIds = true && that.isSet_notifGroupIds();
    if (this_present__notifGroupIds || that_present__notifGroupIds) {
      if (!(this_present__notifGroupIds && that_present__notifGroupIds))
        return false;
      if (!this._notifGroupIds.equals(that._notifGroupIds))
        return false;
    }

    boolean this_present__notifType = true;
    boolean that_present__notifType = true;
    if (this_present__notifType || that_present__notifType) {
      if (!(this_present__notifType && that_present__notifType))
        return false;
      if (this._notifType != that._notifType)
        return false;
    }

    boolean this_present__programId = true;
    boolean that_present__programId = true;
    if (this_present__programId || that_present__programId) {
      if (!(this_present__programId && that_present__programId))
        return false;
      if (this._programId != that._programId)
        return false;
    }

    boolean this_present__startTime = true;
    boolean that_present__startTime = true;
    if (this_present__startTime || that_present__startTime) {
      if (!(this_present__startTime && that_present__startTime))
        return false;
      if (this._startTime != that._startTime)
        return false;
    }

    boolean this_present__stopTime = true;
    boolean that_present__stopTime = true;
    if (this_present__stopTime || that_present__stopTime) {
      if (!(this_present__stopTime && that_present__stopTime))
        return false;
      if (this._stopTime != that._stopTime)
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

    hashCode = hashCode * 8191 + ((isSet_notifGroupIds()) ? 131071 : 524287);
    if (isSet_notifGroupIds())
      hashCode = hashCode * 8191 + _notifGroupIds.hashCode();

    hashCode = hashCode * 8191 + _notifType;

    hashCode = hashCode * 8191 + _programId;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_startTime);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_stopTime);

    return hashCode;
  }

  @Override
  public int compareTo(NotifLMControl other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_notifGroupIds()).compareTo(other.isSet_notifGroupIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_notifGroupIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._notifGroupIds, other._notifGroupIds);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_notifType()).compareTo(other.isSet_notifType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_notifType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._notifType, other._notifType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_programId()).compareTo(other.isSet_programId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_programId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._programId, other._programId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_startTime()).compareTo(other.isSet_startTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_startTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._startTime, other._startTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_stopTime()).compareTo(other.isSet_stopTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_stopTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._stopTime, other._stopTime);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NotifLMControl(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_notifGroupIds:");
    if (this._notifGroupIds == null) {
      sb.append("null");
    } else {
      sb.append(this._notifGroupIds);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_notifType:");
    sb.append(this._notifType);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_programId:");
    sb.append(this._programId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_startTime:");
    sb.append(this._startTime);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_stopTime:");
    sb.append(this._stopTime);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_notifGroupIds()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_notifGroupIds' is unset! Struct:" + toString());
    }

    if (!isSet_notifType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_notifType' is unset! Struct:" + toString());
    }

    if (!isSet_programId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_programId' is unset! Struct:" + toString());
    }

    if (!isSet_startTime()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_startTime' is unset! Struct:" + toString());
    }

    if (!isSet_stopTime()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_stopTime' is unset! Struct:" + toString());
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

  private static class NotifLMControlStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifLMControlStandardScheme getScheme() {
      return new NotifLMControlStandardScheme();
    }
  }

  private static class NotifLMControlStandardScheme extends org.apache.thrift.scheme.StandardScheme<NotifLMControl> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NotifLMControl struct) throws org.apache.thrift.TException {
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
          case 2: // _NOTIF_GROUP_IDS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct._notifGroupIds = new java.util.ArrayList<java.lang.Integer>(_list0.size);
                int _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readI32();
                  struct._notifGroupIds.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.set_notifGroupIdsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _NOTIF_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._notifType = iprot.readI32();
              struct.set_notifTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _PROGRAM_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._programId = iprot.readI32();
              struct.set_programIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _START_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct._startTime = iprot.readI64();
              struct.set_startTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _STOP_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct._stopTime = iprot.readI64();
              struct.set_stopTimeIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NotifLMControl struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._notifGroupIds != null) {
        oprot.writeFieldBegin(_NOTIF_GROUP_IDS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct._notifGroupIds.size()));
          for (int _iter3 : struct._notifGroupIds)
          {
            oprot.writeI32(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_NOTIF_TYPE_FIELD_DESC);
      oprot.writeI32(struct._notifType);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_PROGRAM_ID_FIELD_DESC);
      oprot.writeI32(struct._programId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_START_TIME_FIELD_DESC);
      oprot.writeI64(struct._startTime);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_STOP_TIME_FIELD_DESC);
      oprot.writeI64(struct._stopTime);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NotifLMControlTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifLMControlTupleScheme getScheme() {
      return new NotifLMControlTupleScheme();
    }
  }

  private static class NotifLMControlTupleScheme extends org.apache.thrift.scheme.TupleScheme<NotifLMControl> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NotifLMControl struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      {
        oprot.writeI32(struct._notifGroupIds.size());
        for (int _iter4 : struct._notifGroupIds)
        {
          oprot.writeI32(_iter4);
        }
      }
      oprot.writeI32(struct._notifType);
      oprot.writeI32(struct._programId);
      oprot.writeI64(struct._startTime);
      oprot.writeI64(struct._stopTime);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NotifLMControl struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._notifGroupIds = new java.util.ArrayList<java.lang.Integer>(_list5.size);
        int _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readI32();
          struct._notifGroupIds.add(_elem6);
        }
      }
      struct.set_notifGroupIdsIsSet(true);
      struct._notifType = iprot.readI32();
      struct.set_notifTypeIsSet(true);
      struct._programId = iprot.readI32();
      struct.set_programIdIsSet(true);
      struct._startTime = iprot.readI64();
      struct.set_startTimeIsSet(true);
      struct._stopTime = iprot.readI64();
      struct.set_stopTimeIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

