/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class QueueData implements org.apache.thrift.TBase<QueueData, QueueData._Fields>, java.io.Serializable, Cloneable, Comparable<QueueData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("QueueData");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_id", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _QUEUE_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("_queueCount", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _RATE_FIELD_DESC = new org.apache.thrift.protocol.TField("_rate", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_requestId", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField _REQUEST_ID_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("_requestIdCount", org.apache.thrift.protocol.TType.I32, (short)6);
  private static final org.apache.thrift.protocol.TField _A_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("_aTime", org.apache.thrift.protocol.TType.I64, (short)7);
  private static final org.apache.thrift.protocol.TField _USER_MESSAGE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_userMessageId", org.apache.thrift.protocol.TType.I32, (short)8);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new QueueDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new QueueDataTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _id; // required
  private int _queueCount; // required
  private int _rate; // required
  private int _requestId; // required
  private int _requestIdCount; // required
  private long _aTime; // required
  private int _userMessageId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _ID((short)2, "_id"),
    _QUEUE_COUNT((short)3, "_queueCount"),
    _RATE((short)4, "_rate"),
    _REQUEST_ID((short)5, "_requestId"),
    _REQUEST_ID_COUNT((short)6, "_requestIdCount"),
    _A_TIME((short)7, "_aTime"),
    _USER_MESSAGE_ID((short)8, "_userMessageId");

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
        case 2: // _ID
          return _ID;
        case 3: // _QUEUE_COUNT
          return _QUEUE_COUNT;
        case 4: // _RATE
          return _RATE;
        case 5: // _REQUEST_ID
          return _REQUEST_ID;
        case 6: // _REQUEST_ID_COUNT
          return _REQUEST_ID_COUNT;
        case 7: // _A_TIME
          return _A_TIME;
        case 8: // _USER_MESSAGE_ID
          return _USER_MESSAGE_ID;
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
  private static final int ___ID_ISSET_ID = 0;
  private static final int ___QUEUECOUNT_ISSET_ID = 1;
  private static final int ___RATE_ISSET_ID = 2;
  private static final int ___REQUESTID_ISSET_ID = 3;
  private static final int ___REQUESTIDCOUNT_ISSET_ID = 4;
  private static final int ___ATIME_ISSET_ID = 5;
  private static final int ___USERMESSAGEID_ISSET_ID = 6;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._ID, new org.apache.thrift.meta_data.FieldMetaData("_id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._QUEUE_COUNT, new org.apache.thrift.meta_data.FieldMetaData("_queueCount", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._RATE, new org.apache.thrift.meta_data.FieldMetaData("_rate", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("_requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._REQUEST_ID_COUNT, new org.apache.thrift.meta_data.FieldMetaData("_requestIdCount", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._A_TIME, new org.apache.thrift.meta_data.FieldMetaData("_aTime", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields._USER_MESSAGE_ID, new org.apache.thrift.meta_data.FieldMetaData("_userMessageId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(QueueData.class, metaDataMap);
  }

  public QueueData() {
  }

  public QueueData(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _id,
    int _queueCount,
    int _rate,
    int _requestId,
    int _requestIdCount,
    long _aTime,
    int _userMessageId)
  {
    this();
    this._baseMessage = _baseMessage;
    this._id = _id;
    set_idIsSet(true);
    this._queueCount = _queueCount;
    set_queueCountIsSet(true);
    this._rate = _rate;
    set_rateIsSet(true);
    this._requestId = _requestId;
    set_requestIdIsSet(true);
    this._requestIdCount = _requestIdCount;
    set_requestIdCountIsSet(true);
    this._aTime = _aTime;
    set_aTimeIsSet(true);
    this._userMessageId = _userMessageId;
    set_userMessageIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public QueueData(QueueData other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._id = other._id;
    this._queueCount = other._queueCount;
    this._rate = other._rate;
    this._requestId = other._requestId;
    this._requestIdCount = other._requestIdCount;
    this._aTime = other._aTime;
    this._userMessageId = other._userMessageId;
  }

  public QueueData deepCopy() {
    return new QueueData(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_idIsSet(false);
    this._id = 0;
    set_queueCountIsSet(false);
    this._queueCount = 0;
    set_rateIsSet(false);
    this._rate = 0;
    set_requestIdIsSet(false);
    this._requestId = 0;
    set_requestIdCountIsSet(false);
    this._requestIdCount = 0;
    set_aTimeIsSet(false);
    this._aTime = 0;
    set_userMessageIdIsSet(false);
    this._userMessageId = 0;
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

  public int get_id() {
    return this._id;
  }

  public void set_id(int _id) {
    this._id = _id;
    set_idIsSet(true);
  }

  public void unset_id() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ID_ISSET_ID);
  }

  /** Returns true if field _id is set (has been assigned a value) and false otherwise */
  public boolean isSet_id() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ID_ISSET_ID);
  }

  public void set_idIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ID_ISSET_ID, value);
  }

  public int get_queueCount() {
    return this._queueCount;
  }

  public void set_queueCount(int _queueCount) {
    this._queueCount = _queueCount;
    set_queueCountIsSet(true);
  }

  public void unset_queueCount() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___QUEUECOUNT_ISSET_ID);
  }

  /** Returns true if field _queueCount is set (has been assigned a value) and false otherwise */
  public boolean isSet_queueCount() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___QUEUECOUNT_ISSET_ID);
  }

  public void set_queueCountIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___QUEUECOUNT_ISSET_ID, value);
  }

  public int get_rate() {
    return this._rate;
  }

  public void set_rate(int _rate) {
    this._rate = _rate;
    set_rateIsSet(true);
  }

  public void unset_rate() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___RATE_ISSET_ID);
  }

  /** Returns true if field _rate is set (has been assigned a value) and false otherwise */
  public boolean isSet_rate() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___RATE_ISSET_ID);
  }

  public void set_rateIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___RATE_ISSET_ID, value);
  }

  public int get_requestId() {
    return this._requestId;
  }

  public void set_requestId(int _requestId) {
    this._requestId = _requestId;
    set_requestIdIsSet(true);
  }

  public void unset_requestId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___REQUESTID_ISSET_ID);
  }

  /** Returns true if field _requestId is set (has been assigned a value) and false otherwise */
  public boolean isSet_requestId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___REQUESTID_ISSET_ID);
  }

  public void set_requestIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___REQUESTID_ISSET_ID, value);
  }

  public int get_requestIdCount() {
    return this._requestIdCount;
  }

  public void set_requestIdCount(int _requestIdCount) {
    this._requestIdCount = _requestIdCount;
    set_requestIdCountIsSet(true);
  }

  public void unset_requestIdCount() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___REQUESTIDCOUNT_ISSET_ID);
  }

  /** Returns true if field _requestIdCount is set (has been assigned a value) and false otherwise */
  public boolean isSet_requestIdCount() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___REQUESTIDCOUNT_ISSET_ID);
  }

  public void set_requestIdCountIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___REQUESTIDCOUNT_ISSET_ID, value);
  }

  public long get_aTime() {
    return this._aTime;
  }

  public void set_aTime(long _aTime) {
    this._aTime = _aTime;
    set_aTimeIsSet(true);
  }

  public void unset_aTime() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ATIME_ISSET_ID);
  }

  /** Returns true if field _aTime is set (has been assigned a value) and false otherwise */
  public boolean isSet_aTime() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ATIME_ISSET_ID);
  }

  public void set_aTimeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ATIME_ISSET_ID, value);
  }

  public int get_userMessageId() {
    return this._userMessageId;
  }

  public void set_userMessageId(int _userMessageId) {
    this._userMessageId = _userMessageId;
    set_userMessageIdIsSet(true);
  }

  public void unset_userMessageId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___USERMESSAGEID_ISSET_ID);
  }

  /** Returns true if field _userMessageId is set (has been assigned a value) and false otherwise */
  public boolean isSet_userMessageId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___USERMESSAGEID_ISSET_ID);
  }

  public void set_userMessageIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___USERMESSAGEID_ISSET_ID, value);
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

    case _ID:
      if (value == null) {
        unset_id();
      } else {
        set_id((java.lang.Integer)value);
      }
      break;

    case _QUEUE_COUNT:
      if (value == null) {
        unset_queueCount();
      } else {
        set_queueCount((java.lang.Integer)value);
      }
      break;

    case _RATE:
      if (value == null) {
        unset_rate();
      } else {
        set_rate((java.lang.Integer)value);
      }
      break;

    case _REQUEST_ID:
      if (value == null) {
        unset_requestId();
      } else {
        set_requestId((java.lang.Integer)value);
      }
      break;

    case _REQUEST_ID_COUNT:
      if (value == null) {
        unset_requestIdCount();
      } else {
        set_requestIdCount((java.lang.Integer)value);
      }
      break;

    case _A_TIME:
      if (value == null) {
        unset_aTime();
      } else {
        set_aTime((java.lang.Long)value);
      }
      break;

    case _USER_MESSAGE_ID:
      if (value == null) {
        unset_userMessageId();
      } else {
        set_userMessageId((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _ID:
      return get_id();

    case _QUEUE_COUNT:
      return get_queueCount();

    case _RATE:
      return get_rate();

    case _REQUEST_ID:
      return get_requestId();

    case _REQUEST_ID_COUNT:
      return get_requestIdCount();

    case _A_TIME:
      return get_aTime();

    case _USER_MESSAGE_ID:
      return get_userMessageId();

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
    case _ID:
      return isSet_id();
    case _QUEUE_COUNT:
      return isSet_queueCount();
    case _RATE:
      return isSet_rate();
    case _REQUEST_ID:
      return isSet_requestId();
    case _REQUEST_ID_COUNT:
      return isSet_requestIdCount();
    case _A_TIME:
      return isSet_aTime();
    case _USER_MESSAGE_ID:
      return isSet_userMessageId();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof QueueData)
      return this.equals((QueueData)that);
    return false;
  }

  public boolean equals(QueueData that) {
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

    boolean this_present__id = true;
    boolean that_present__id = true;
    if (this_present__id || that_present__id) {
      if (!(this_present__id && that_present__id))
        return false;
      if (this._id != that._id)
        return false;
    }

    boolean this_present__queueCount = true;
    boolean that_present__queueCount = true;
    if (this_present__queueCount || that_present__queueCount) {
      if (!(this_present__queueCount && that_present__queueCount))
        return false;
      if (this._queueCount != that._queueCount)
        return false;
    }

    boolean this_present__rate = true;
    boolean that_present__rate = true;
    if (this_present__rate || that_present__rate) {
      if (!(this_present__rate && that_present__rate))
        return false;
      if (this._rate != that._rate)
        return false;
    }

    boolean this_present__requestId = true;
    boolean that_present__requestId = true;
    if (this_present__requestId || that_present__requestId) {
      if (!(this_present__requestId && that_present__requestId))
        return false;
      if (this._requestId != that._requestId)
        return false;
    }

    boolean this_present__requestIdCount = true;
    boolean that_present__requestIdCount = true;
    if (this_present__requestIdCount || that_present__requestIdCount) {
      if (!(this_present__requestIdCount && that_present__requestIdCount))
        return false;
      if (this._requestIdCount != that._requestIdCount)
        return false;
    }

    boolean this_present__aTime = true;
    boolean that_present__aTime = true;
    if (this_present__aTime || that_present__aTime) {
      if (!(this_present__aTime && that_present__aTime))
        return false;
      if (this._aTime != that._aTime)
        return false;
    }

    boolean this_present__userMessageId = true;
    boolean that_present__userMessageId = true;
    if (this_present__userMessageId || that_present__userMessageId) {
      if (!(this_present__userMessageId && that_present__userMessageId))
        return false;
      if (this._userMessageId != that._userMessageId)
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

    hashCode = hashCode * 8191 + _id;

    hashCode = hashCode * 8191 + _queueCount;

    hashCode = hashCode * 8191 + _rate;

    hashCode = hashCode * 8191 + _requestId;

    hashCode = hashCode * 8191 + _requestIdCount;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_aTime);

    hashCode = hashCode * 8191 + _userMessageId;

    return hashCode;
  }

  @Override
  public int compareTo(QueueData other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_id()).compareTo(other.isSet_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_id()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._id, other._id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_queueCount()).compareTo(other.isSet_queueCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_queueCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._queueCount, other._queueCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_rate()).compareTo(other.isSet_rate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_rate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._rate, other._rate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_requestId()).compareTo(other.isSet_requestId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_requestId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._requestId, other._requestId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_requestIdCount()).compareTo(other.isSet_requestIdCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_requestIdCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._requestIdCount, other._requestIdCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_aTime()).compareTo(other.isSet_aTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_aTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._aTime, other._aTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_userMessageId()).compareTo(other.isSet_userMessageId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_userMessageId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._userMessageId, other._userMessageId);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("QueueData(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_id:");
    sb.append(this._id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_queueCount:");
    sb.append(this._queueCount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_rate:");
    sb.append(this._rate);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_requestId:");
    sb.append(this._requestId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_requestIdCount:");
    sb.append(this._requestIdCount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_aTime:");
    sb.append(this._aTime);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_userMessageId:");
    sb.append(this._userMessageId);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_id()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_id' is unset! Struct:" + toString());
    }

    if (!isSet_queueCount()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_queueCount' is unset! Struct:" + toString());
    }

    if (!isSet_rate()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_rate' is unset! Struct:" + toString());
    }

    if (!isSet_requestId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_requestId' is unset! Struct:" + toString());
    }

    if (!isSet_requestIdCount()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_requestIdCount' is unset! Struct:" + toString());
    }

    if (!isSet_aTime()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_aTime' is unset! Struct:" + toString());
    }

    if (!isSet_userMessageId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_userMessageId' is unset! Struct:" + toString());
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

  private static class QueueDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public QueueDataStandardScheme getScheme() {
      return new QueueDataStandardScheme();
    }
  }

  private static class QueueDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<QueueData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, QueueData struct) throws org.apache.thrift.TException {
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
          case 2: // _ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._id = iprot.readI32();
              struct.set_idIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _QUEUE_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._queueCount = iprot.readI32();
              struct.set_queueCountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _RATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._rate = iprot.readI32();
              struct.set_rateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _REQUEST_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._requestId = iprot.readI32();
              struct.set_requestIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _REQUEST_ID_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._requestIdCount = iprot.readI32();
              struct.set_requestIdCountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // _A_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct._aTime = iprot.readI64();
              struct.set_aTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // _USER_MESSAGE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._userMessageId = iprot.readI32();
              struct.set_userMessageIdIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, QueueData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_ID_FIELD_DESC);
      oprot.writeI32(struct._id);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_QUEUE_COUNT_FIELD_DESC);
      oprot.writeI32(struct._queueCount);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_RATE_FIELD_DESC);
      oprot.writeI32(struct._rate);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_REQUEST_ID_FIELD_DESC);
      oprot.writeI32(struct._requestId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_REQUEST_ID_COUNT_FIELD_DESC);
      oprot.writeI32(struct._requestIdCount);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_A_TIME_FIELD_DESC);
      oprot.writeI64(struct._aTime);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_USER_MESSAGE_ID_FIELD_DESC);
      oprot.writeI32(struct._userMessageId);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class QueueDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public QueueDataTupleScheme getScheme() {
      return new QueueDataTupleScheme();
    }
  }

  private static class QueueDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<QueueData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, QueueData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._id);
      oprot.writeI32(struct._queueCount);
      oprot.writeI32(struct._rate);
      oprot.writeI32(struct._requestId);
      oprot.writeI32(struct._requestIdCount);
      oprot.writeI64(struct._aTime);
      oprot.writeI32(struct._userMessageId);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, QueueData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._id = iprot.readI32();
      struct.set_idIsSet(true);
      struct._queueCount = iprot.readI32();
      struct.set_queueCountIsSet(true);
      struct._rate = iprot.readI32();
      struct.set_rateIsSet(true);
      struct._requestId = iprot.readI32();
      struct.set_requestIdIsSet(true);
      struct._requestIdCount = iprot.readI32();
      struct.set_requestIdCountIsSet(true);
      struct._aTime = iprot.readI64();
      struct.set_aTimeIsSet(true);
      struct._userMessageId = iprot.readI32();
      struct.set_userMessageIdIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

