/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class PointData implements org.apache.thrift.TBase<PointData, PointData._Fields>, java.io.Serializable, Cloneable, Comparable<PointData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PointData");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_id", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("_type", org.apache.thrift.protocol.TType.BYTE, (short)3);
  private static final org.apache.thrift.protocol.TField _QUALITY_FIELD_DESC = new org.apache.thrift.protocol.TField("_quality", org.apache.thrift.protocol.TType.BYTE, (short)4);
  private static final org.apache.thrift.protocol.TField _TAGS_FIELD_DESC = new org.apache.thrift.protocol.TField("_tags", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField _VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_value", org.apache.thrift.protocol.TType.DOUBLE, (short)6);
  private static final org.apache.thrift.protocol.TField _STR_FIELD_DESC = new org.apache.thrift.protocol.TField("_str", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField _TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("_time", org.apache.thrift.protocol.TType.I64, (short)8);
  private static final org.apache.thrift.protocol.TField _MILLIS_FIELD_DESC = new org.apache.thrift.protocol.TField("_millis", org.apache.thrift.protocol.TType.I16, (short)9);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PointDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PointDataTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _id; // required
  private byte _type; // required
  private byte _quality; // required
  private int _tags; // required
  private double _value; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _str; // required
  private long _time; // required
  private short _millis; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _ID((short)2, "_id"),
    _TYPE((short)3, "_type"),
    _QUALITY((short)4, "_quality"),
    _TAGS((short)5, "_tags"),
    _VALUE((short)6, "_value"),
    _STR((short)7, "_str"),
    _TIME((short)8, "_time"),
    _MILLIS((short)9, "_millis");

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
        case 3: // _TYPE
          return _TYPE;
        case 4: // _QUALITY
          return _QUALITY;
        case 5: // _TAGS
          return _TAGS;
        case 6: // _VALUE
          return _VALUE;
        case 7: // _STR
          return _STR;
        case 8: // _TIME
          return _TIME;
        case 9: // _MILLIS
          return _MILLIS;
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
  private static final int ___TYPE_ISSET_ID = 1;
  private static final int ___QUALITY_ISSET_ID = 2;
  private static final int ___TAGS_ISSET_ID = 3;
  private static final int ___VALUE_ISSET_ID = 4;
  private static final int ___TIME_ISSET_ID = 5;
  private static final int ___MILLIS_ISSET_ID = 6;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._ID, new org.apache.thrift.meta_data.FieldMetaData("_id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._TYPE, new org.apache.thrift.meta_data.FieldMetaData("_type", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BYTE)));
    tmpMap.put(_Fields._QUALITY, new org.apache.thrift.meta_data.FieldMetaData("_quality", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BYTE)));
    tmpMap.put(_Fields._TAGS, new org.apache.thrift.meta_data.FieldMetaData("_tags", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._VALUE, new org.apache.thrift.meta_data.FieldMetaData("_value", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._STR, new org.apache.thrift.meta_data.FieldMetaData("_str", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._TIME, new org.apache.thrift.meta_data.FieldMetaData("_time", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields._MILLIS, new org.apache.thrift.meta_data.FieldMetaData("_millis", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PointData.class, metaDataMap);
  }

  public PointData() {
  }

  public PointData(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _id,
    byte _type,
    byte _quality,
    int _tags,
    double _value,
    java.lang.String _str,
    long _time,
    short _millis)
  {
    this();
    this._baseMessage = _baseMessage;
    this._id = _id;
    set_idIsSet(true);
    this._type = _type;
    set_typeIsSet(true);
    this._quality = _quality;
    set_qualityIsSet(true);
    this._tags = _tags;
    set_tagsIsSet(true);
    this._value = _value;
    set_valueIsSet(true);
    this._str = _str;
    this._time = _time;
    set_timeIsSet(true);
    this._millis = _millis;
    set_millisIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PointData(PointData other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._id = other._id;
    this._type = other._type;
    this._quality = other._quality;
    this._tags = other._tags;
    this._value = other._value;
    if (other.isSet_str()) {
      this._str = other._str;
    }
    this._time = other._time;
    this._millis = other._millis;
  }

  public PointData deepCopy() {
    return new PointData(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_idIsSet(false);
    this._id = 0;
    set_typeIsSet(false);
    this._type = 0;
    set_qualityIsSet(false);
    this._quality = 0;
    set_tagsIsSet(false);
    this._tags = 0;
    set_valueIsSet(false);
    this._value = 0.0;
    this._str = null;
    set_timeIsSet(false);
    this._time = 0;
    set_millisIsSet(false);
    this._millis = 0;
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

  public byte get_type() {
    return this._type;
  }

  public void set_type(byte _type) {
    this._type = _type;
    set_typeIsSet(true);
  }

  public void unset_type() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___TYPE_ISSET_ID);
  }

  /** Returns true if field _type is set (has been assigned a value) and false otherwise */
  public boolean isSet_type() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___TYPE_ISSET_ID);
  }

  public void set_typeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___TYPE_ISSET_ID, value);
  }

  public byte get_quality() {
    return this._quality;
  }

  public void set_quality(byte _quality) {
    this._quality = _quality;
    set_qualityIsSet(true);
  }

  public void unset_quality() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___QUALITY_ISSET_ID);
  }

  /** Returns true if field _quality is set (has been assigned a value) and false otherwise */
  public boolean isSet_quality() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___QUALITY_ISSET_ID);
  }

  public void set_qualityIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___QUALITY_ISSET_ID, value);
  }

  public int get_tags() {
    return this._tags;
  }

  public void set_tags(int _tags) {
    this._tags = _tags;
    set_tagsIsSet(true);
  }

  public void unset_tags() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___TAGS_ISSET_ID);
  }

  /** Returns true if field _tags is set (has been assigned a value) and false otherwise */
  public boolean isSet_tags() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___TAGS_ISSET_ID);
  }

  public void set_tagsIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___TAGS_ISSET_ID, value);
  }

  public double get_value() {
    return this._value;
  }

  public void set_value(double _value) {
    this._value = _value;
    set_valueIsSet(true);
  }

  public void unset_value() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___VALUE_ISSET_ID);
  }

  /** Returns true if field _value is set (has been assigned a value) and false otherwise */
  public boolean isSet_value() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___VALUE_ISSET_ID);
  }

  public void set_valueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___VALUE_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_str() {
    return this._str;
  }

  public void set_str(@org.apache.thrift.annotation.Nullable java.lang.String _str) {
    this._str = _str;
  }

  public void unset_str() {
    this._str = null;
  }

  /** Returns true if field _str is set (has been assigned a value) and false otherwise */
  public boolean isSet_str() {
    return this._str != null;
  }

  public void set_strIsSet(boolean value) {
    if (!value) {
      this._str = null;
    }
  }

  public long get_time() {
    return this._time;
  }

  public void set_time(long _time) {
    this._time = _time;
    set_timeIsSet(true);
  }

  public void unset_time() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___TIME_ISSET_ID);
  }

  /** Returns true if field _time is set (has been assigned a value) and false otherwise */
  public boolean isSet_time() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___TIME_ISSET_ID);
  }

  public void set_timeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___TIME_ISSET_ID, value);
  }

  public short get_millis() {
    return this._millis;
  }

  public void set_millis(short _millis) {
    this._millis = _millis;
    set_millisIsSet(true);
  }

  public void unset_millis() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___MILLIS_ISSET_ID);
  }

  /** Returns true if field _millis is set (has been assigned a value) and false otherwise */
  public boolean isSet_millis() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___MILLIS_ISSET_ID);
  }

  public void set_millisIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___MILLIS_ISSET_ID, value);
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

    case _TYPE:
      if (value == null) {
        unset_type();
      } else {
        set_type((java.lang.Byte)value);
      }
      break;

    case _QUALITY:
      if (value == null) {
        unset_quality();
      } else {
        set_quality((java.lang.Byte)value);
      }
      break;

    case _TAGS:
      if (value == null) {
        unset_tags();
      } else {
        set_tags((java.lang.Integer)value);
      }
      break;

    case _VALUE:
      if (value == null) {
        unset_value();
      } else {
        set_value((java.lang.Double)value);
      }
      break;

    case _STR:
      if (value == null) {
        unset_str();
      } else {
        set_str((java.lang.String)value);
      }
      break;

    case _TIME:
      if (value == null) {
        unset_time();
      } else {
        set_time((java.lang.Long)value);
      }
      break;

    case _MILLIS:
      if (value == null) {
        unset_millis();
      } else {
        set_millis((java.lang.Short)value);
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

    case _TYPE:
      return get_type();

    case _QUALITY:
      return get_quality();

    case _TAGS:
      return get_tags();

    case _VALUE:
      return get_value();

    case _STR:
      return get_str();

    case _TIME:
      return get_time();

    case _MILLIS:
      return get_millis();

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
    case _TYPE:
      return isSet_type();
    case _QUALITY:
      return isSet_quality();
    case _TAGS:
      return isSet_tags();
    case _VALUE:
      return isSet_value();
    case _STR:
      return isSet_str();
    case _TIME:
      return isSet_time();
    case _MILLIS:
      return isSet_millis();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof PointData)
      return this.equals((PointData)that);
    return false;
  }

  public boolean equals(PointData that) {
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

    boolean this_present__type = true;
    boolean that_present__type = true;
    if (this_present__type || that_present__type) {
      if (!(this_present__type && that_present__type))
        return false;
      if (this._type != that._type)
        return false;
    }

    boolean this_present__quality = true;
    boolean that_present__quality = true;
    if (this_present__quality || that_present__quality) {
      if (!(this_present__quality && that_present__quality))
        return false;
      if (this._quality != that._quality)
        return false;
    }

    boolean this_present__tags = true;
    boolean that_present__tags = true;
    if (this_present__tags || that_present__tags) {
      if (!(this_present__tags && that_present__tags))
        return false;
      if (this._tags != that._tags)
        return false;
    }

    boolean this_present__value = true;
    boolean that_present__value = true;
    if (this_present__value || that_present__value) {
      if (!(this_present__value && that_present__value))
        return false;
      if (this._value != that._value)
        return false;
    }

    boolean this_present__str = true && this.isSet_str();
    boolean that_present__str = true && that.isSet_str();
    if (this_present__str || that_present__str) {
      if (!(this_present__str && that_present__str))
        return false;
      if (!this._str.equals(that._str))
        return false;
    }

    boolean this_present__time = true;
    boolean that_present__time = true;
    if (this_present__time || that_present__time) {
      if (!(this_present__time && that_present__time))
        return false;
      if (this._time != that._time)
        return false;
    }

    boolean this_present__millis = true;
    boolean that_present__millis = true;
    if (this_present__millis || that_present__millis) {
      if (!(this_present__millis && that_present__millis))
        return false;
      if (this._millis != that._millis)
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

    hashCode = hashCode * 8191 + (int) (_type);

    hashCode = hashCode * 8191 + (int) (_quality);

    hashCode = hashCode * 8191 + _tags;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_value);

    hashCode = hashCode * 8191 + ((isSet_str()) ? 131071 : 524287);
    if (isSet_str())
      hashCode = hashCode * 8191 + _str.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_time);

    hashCode = hashCode * 8191 + _millis;

    return hashCode;
  }

  @Override
  public int compareTo(PointData other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_type()).compareTo(other.isSet_type());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_type()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._type, other._type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_quality()).compareTo(other.isSet_quality());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_quality()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._quality, other._quality);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_tags()).compareTo(other.isSet_tags());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_tags()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._tags, other._tags);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_value()).compareTo(other.isSet_value());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_value()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._value, other._value);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_str()).compareTo(other.isSet_str());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_str()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._str, other._str);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_time()).compareTo(other.isSet_time());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_time()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._time, other._time);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_millis()).compareTo(other.isSet_millis());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_millis()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._millis, other._millis);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PointData(");
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
    sb.append("_type:");
    sb.append(this._type);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_quality:");
    sb.append(this._quality);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_tags:");
    sb.append(this._tags);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_value:");
    sb.append(this._value);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_str:");
    if (this._str == null) {
      sb.append("null");
    } else {
      sb.append(this._str);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_time:");
    sb.append(this._time);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_millis:");
    sb.append(this._millis);
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

    if (!isSet_type()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_type' is unset! Struct:" + toString());
    }

    if (!isSet_quality()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_quality' is unset! Struct:" + toString());
    }

    if (!isSet_tags()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_tags' is unset! Struct:" + toString());
    }

    if (!isSet_value()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_value' is unset! Struct:" + toString());
    }

    if (!isSet_str()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_str' is unset! Struct:" + toString());
    }

    if (!isSet_time()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_time' is unset! Struct:" + toString());
    }

    if (!isSet_millis()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_millis' is unset! Struct:" + toString());
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

  private static class PointDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PointDataStandardScheme getScheme() {
      return new PointDataStandardScheme();
    }
  }

  private static class PointDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<PointData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PointData struct) throws org.apache.thrift.TException {
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
          case 3: // _TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.BYTE) {
              struct._type = iprot.readByte();
              struct.set_typeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _QUALITY
            if (schemeField.type == org.apache.thrift.protocol.TType.BYTE) {
              struct._quality = iprot.readByte();
              struct.set_qualityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _TAGS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._tags = iprot.readI32();
              struct.set_tagsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._value = iprot.readDouble();
              struct.set_valueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // _STR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._str = iprot.readString();
              struct.set_strIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // _TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct._time = iprot.readI64();
              struct.set_timeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // _MILLIS
            if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
              struct._millis = iprot.readI16();
              struct.set_millisIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PointData struct) throws org.apache.thrift.TException {
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
      oprot.writeFieldBegin(_TYPE_FIELD_DESC);
      oprot.writeByte(struct._type);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_QUALITY_FIELD_DESC);
      oprot.writeByte(struct._quality);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_TAGS_FIELD_DESC);
      oprot.writeI32(struct._tags);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_VALUE_FIELD_DESC);
      oprot.writeDouble(struct._value);
      oprot.writeFieldEnd();
      if (struct._str != null) {
        oprot.writeFieldBegin(_STR_FIELD_DESC);
        oprot.writeString(struct._str);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_TIME_FIELD_DESC);
      oprot.writeI64(struct._time);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_MILLIS_FIELD_DESC);
      oprot.writeI16(struct._millis);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PointDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PointDataTupleScheme getScheme() {
      return new PointDataTupleScheme();
    }
  }

  private static class PointDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<PointData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PointData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._id);
      oprot.writeByte(struct._type);
      oprot.writeByte(struct._quality);
      oprot.writeI32(struct._tags);
      oprot.writeDouble(struct._value);
      oprot.writeString(struct._str);
      oprot.writeI64(struct._time);
      oprot.writeI16(struct._millis);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PointData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._id = iprot.readI32();
      struct.set_idIsSet(true);
      struct._type = iprot.readByte();
      struct.set_typeIsSet(true);
      struct._quality = iprot.readByte();
      struct.set_qualityIsSet(true);
      struct._tags = iprot.readI32();
      struct.set_tagsIsSet(true);
      struct._value = iprot.readDouble();
      struct.set_valueIsSet(true);
      struct._str = iprot.readString();
      struct.set_strIsSet(true);
      struct._time = iprot.readI64();
      struct.set_timeIsSet(true);
      struct._millis = iprot.readI16();
      struct.set_millisIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

