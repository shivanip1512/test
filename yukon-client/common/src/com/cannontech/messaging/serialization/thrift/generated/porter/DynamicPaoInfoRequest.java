/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.porter;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class DynamicPaoInfoRequest implements org.apache.thrift.TBase<DynamicPaoInfoRequest, DynamicPaoInfoRequest._Fields>, java.io.Serializable, Cloneable, Comparable<DynamicPaoInfoRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DynamicPaoInfoRequest");

  private static final org.apache.thrift.protocol.TField _DEVICE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_deviceId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField _DURATION_KEYS_FIELD_DESC = new org.apache.thrift.protocol.TField("_durationKeys", org.apache.thrift.protocol.TType.SET, (short)2);
  private static final org.apache.thrift.protocol.TField _TIMESTAMP_KEYS_FIELD_DESC = new org.apache.thrift.protocol.TField("_timestampKeys", org.apache.thrift.protocol.TType.SET, (short)3);
  private static final org.apache.thrift.protocol.TField _PERCENTAGE_KEYS_FIELD_DESC = new org.apache.thrift.protocol.TField("_percentageKeys", org.apache.thrift.protocol.TType.SET, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new DynamicPaoInfoRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new DynamicPaoInfoRequestTupleSchemeFactory();

  private int _deviceId; // required
  private @org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoDurationKeys> _durationKeys; // required
  private @org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoTimestampKeys> _timestampKeys; // required
  private @org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoPercentageKeys> _percentageKeys; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _DEVICE_ID((short)1, "_deviceId"),
    _DURATION_KEYS((short)2, "_durationKeys"),
    _TIMESTAMP_KEYS((short)3, "_timestampKeys"),
    _PERCENTAGE_KEYS((short)4, "_percentageKeys");

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
        case 1: // _DEVICE_ID
          return _DEVICE_ID;
        case 2: // _DURATION_KEYS
          return _DURATION_KEYS;
        case 3: // _TIMESTAMP_KEYS
          return _TIMESTAMP_KEYS;
        case 4: // _PERCENTAGE_KEYS
          return _PERCENTAGE_KEYS;
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
  private static final int ___DEVICEID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._DEVICE_ID, new org.apache.thrift.meta_data.FieldMetaData("_deviceId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._DURATION_KEYS, new org.apache.thrift.meta_data.FieldMetaData("_durationKeys", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DynamicPaoInfoDurationKeys.class))));
    tmpMap.put(_Fields._TIMESTAMP_KEYS, new org.apache.thrift.meta_data.FieldMetaData("_timestampKeys", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DynamicPaoInfoTimestampKeys.class))));
    tmpMap.put(_Fields._PERCENTAGE_KEYS, new org.apache.thrift.meta_data.FieldMetaData("_percentageKeys", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DynamicPaoInfoPercentageKeys.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DynamicPaoInfoRequest.class, metaDataMap);
  }

  public DynamicPaoInfoRequest() {
  }

  public DynamicPaoInfoRequest(
    int _deviceId,
    java.util.Set<DynamicPaoInfoDurationKeys> _durationKeys,
    java.util.Set<DynamicPaoInfoTimestampKeys> _timestampKeys,
    java.util.Set<DynamicPaoInfoPercentageKeys> _percentageKeys)
  {
    this();
    this._deviceId = _deviceId;
    set_deviceIdIsSet(true);
    this._durationKeys = _durationKeys;
    this._timestampKeys = _timestampKeys;
    this._percentageKeys = _percentageKeys;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DynamicPaoInfoRequest(DynamicPaoInfoRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this._deviceId = other._deviceId;
    if (other.isSet_durationKeys()) {
      java.util.Set<DynamicPaoInfoDurationKeys> __this___durationKeys = java.util.EnumSet.noneOf(DynamicPaoInfoDurationKeys.class);
      for (DynamicPaoInfoDurationKeys other_element : other._durationKeys) {
        __this___durationKeys.add(other_element);
      }
      this._durationKeys = __this___durationKeys;
    }
    if (other.isSet_timestampKeys()) {
      java.util.Set<DynamicPaoInfoTimestampKeys> __this___timestampKeys = java.util.EnumSet.noneOf(DynamicPaoInfoTimestampKeys.class);
      for (DynamicPaoInfoTimestampKeys other_element : other._timestampKeys) {
        __this___timestampKeys.add(other_element);
      }
      this._timestampKeys = __this___timestampKeys;
    }
    if (other.isSet_percentageKeys()) {
      java.util.Set<DynamicPaoInfoPercentageKeys> __this___percentageKeys = java.util.EnumSet.noneOf(DynamicPaoInfoPercentageKeys.class);
      for (DynamicPaoInfoPercentageKeys other_element : other._percentageKeys) {
        __this___percentageKeys.add(other_element);
      }
      this._percentageKeys = __this___percentageKeys;
    }
  }

  public DynamicPaoInfoRequest deepCopy() {
    return new DynamicPaoInfoRequest(this);
  }

  @Override
  public void clear() {
    set_deviceIdIsSet(false);
    this._deviceId = 0;
    this._durationKeys = null;
    this._timestampKeys = null;
    this._percentageKeys = null;
  }

  public int get_deviceId() {
    return this._deviceId;
  }

  public void set_deviceId(int _deviceId) {
    this._deviceId = _deviceId;
    set_deviceIdIsSet(true);
  }

  public void unset_deviceId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___DEVICEID_ISSET_ID);
  }

  /** Returns true if field _deviceId is set (has been assigned a value) and false otherwise */
  public boolean isSet_deviceId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___DEVICEID_ISSET_ID);
  }

  public void set_deviceIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___DEVICEID_ISSET_ID, value);
  }

  public int get_durationKeysSize() {
    return (this._durationKeys == null) ? 0 : this._durationKeys.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<DynamicPaoInfoDurationKeys> get_durationKeysIterator() {
    return (this._durationKeys == null) ? null : this._durationKeys.iterator();
  }

  public void addTo_durationKeys(DynamicPaoInfoDurationKeys elem) {
    if (this._durationKeys == null) {
      this._durationKeys = java.util.EnumSet.noneOf(DynamicPaoInfoDurationKeys.class);
    }
    this._durationKeys.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Set<DynamicPaoInfoDurationKeys> get_durationKeys() {
    return this._durationKeys;
  }

  public void set_durationKeys(@org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoDurationKeys> _durationKeys) {
    this._durationKeys = _durationKeys;
  }

  public void unset_durationKeys() {
    this._durationKeys = null;
  }

  /** Returns true if field _durationKeys is set (has been assigned a value) and false otherwise */
  public boolean isSet_durationKeys() {
    return this._durationKeys != null;
  }

  public void set_durationKeysIsSet(boolean value) {
    if (!value) {
      this._durationKeys = null;
    }
  }

  public int get_timestampKeysSize() {
    return (this._timestampKeys == null) ? 0 : this._timestampKeys.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<DynamicPaoInfoTimestampKeys> get_timestampKeysIterator() {
    return (this._timestampKeys == null) ? null : this._timestampKeys.iterator();
  }

  public void addTo_timestampKeys(DynamicPaoInfoTimestampKeys elem) {
    if (this._timestampKeys == null) {
      this._timestampKeys = java.util.EnumSet.noneOf(DynamicPaoInfoTimestampKeys.class);
    }
    this._timestampKeys.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Set<DynamicPaoInfoTimestampKeys> get_timestampKeys() {
    return this._timestampKeys;
  }

  public void set_timestampKeys(@org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoTimestampKeys> _timestampKeys) {
    this._timestampKeys = _timestampKeys;
  }

  public void unset_timestampKeys() {
    this._timestampKeys = null;
  }

  /** Returns true if field _timestampKeys is set (has been assigned a value) and false otherwise */
  public boolean isSet_timestampKeys() {
    return this._timestampKeys != null;
  }

  public void set_timestampKeysIsSet(boolean value) {
    if (!value) {
      this._timestampKeys = null;
    }
  }

  public int get_percentageKeysSize() {
    return (this._percentageKeys == null) ? 0 : this._percentageKeys.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<DynamicPaoInfoPercentageKeys> get_percentageKeysIterator() {
    return (this._percentageKeys == null) ? null : this._percentageKeys.iterator();
  }

  public void addTo_percentageKeys(DynamicPaoInfoPercentageKeys elem) {
    if (this._percentageKeys == null) {
      this._percentageKeys = java.util.EnumSet.noneOf(DynamicPaoInfoPercentageKeys.class);
    }
    this._percentageKeys.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Set<DynamicPaoInfoPercentageKeys> get_percentageKeys() {
    return this._percentageKeys;
  }

  public void set_percentageKeys(@org.apache.thrift.annotation.Nullable java.util.Set<DynamicPaoInfoPercentageKeys> _percentageKeys) {
    this._percentageKeys = _percentageKeys;
  }

  public void unset_percentageKeys() {
    this._percentageKeys = null;
  }

  /** Returns true if field _percentageKeys is set (has been assigned a value) and false otherwise */
  public boolean isSet_percentageKeys() {
    return this._percentageKeys != null;
  }

  public void set_percentageKeysIsSet(boolean value) {
    if (!value) {
      this._percentageKeys = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _DEVICE_ID:
      if (value == null) {
        unset_deviceId();
      } else {
        set_deviceId((java.lang.Integer)value);
      }
      break;

    case _DURATION_KEYS:
      if (value == null) {
        unset_durationKeys();
      } else {
        set_durationKeys((java.util.Set<DynamicPaoInfoDurationKeys>)value);
      }
      break;

    case _TIMESTAMP_KEYS:
      if (value == null) {
        unset_timestampKeys();
      } else {
        set_timestampKeys((java.util.Set<DynamicPaoInfoTimestampKeys>)value);
      }
      break;

    case _PERCENTAGE_KEYS:
      if (value == null) {
        unset_percentageKeys();
      } else {
        set_percentageKeys((java.util.Set<DynamicPaoInfoPercentageKeys>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _DEVICE_ID:
      return get_deviceId();

    case _DURATION_KEYS:
      return get_durationKeys();

    case _TIMESTAMP_KEYS:
      return get_timestampKeys();

    case _PERCENTAGE_KEYS:
      return get_percentageKeys();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _DEVICE_ID:
      return isSet_deviceId();
    case _DURATION_KEYS:
      return isSet_durationKeys();
    case _TIMESTAMP_KEYS:
      return isSet_timestampKeys();
    case _PERCENTAGE_KEYS:
      return isSet_percentageKeys();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof DynamicPaoInfoRequest)
      return this.equals((DynamicPaoInfoRequest)that);
    return false;
  }

  public boolean equals(DynamicPaoInfoRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__deviceId = true;
    boolean that_present__deviceId = true;
    if (this_present__deviceId || that_present__deviceId) {
      if (!(this_present__deviceId && that_present__deviceId))
        return false;
      if (this._deviceId != that._deviceId)
        return false;
    }

    boolean this_present__durationKeys = true && this.isSet_durationKeys();
    boolean that_present__durationKeys = true && that.isSet_durationKeys();
    if (this_present__durationKeys || that_present__durationKeys) {
      if (!(this_present__durationKeys && that_present__durationKeys))
        return false;
      if (!this._durationKeys.equals(that._durationKeys))
        return false;
    }

    boolean this_present__timestampKeys = true && this.isSet_timestampKeys();
    boolean that_present__timestampKeys = true && that.isSet_timestampKeys();
    if (this_present__timestampKeys || that_present__timestampKeys) {
      if (!(this_present__timestampKeys && that_present__timestampKeys))
        return false;
      if (!this._timestampKeys.equals(that._timestampKeys))
        return false;
    }

    boolean this_present__percentageKeys = true && this.isSet_percentageKeys();
    boolean that_present__percentageKeys = true && that.isSet_percentageKeys();
    if (this_present__percentageKeys || that_present__percentageKeys) {
      if (!(this_present__percentageKeys && that_present__percentageKeys))
        return false;
      if (!this._percentageKeys.equals(that._percentageKeys))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + _deviceId;

    hashCode = hashCode * 8191 + ((isSet_durationKeys()) ? 131071 : 524287);
    if (isSet_durationKeys())
      hashCode = hashCode * 8191 + _durationKeys.hashCode();

    hashCode = hashCode * 8191 + ((isSet_timestampKeys()) ? 131071 : 524287);
    if (isSet_timestampKeys())
      hashCode = hashCode * 8191 + _timestampKeys.hashCode();

    hashCode = hashCode * 8191 + ((isSet_percentageKeys()) ? 131071 : 524287);
    if (isSet_percentageKeys())
      hashCode = hashCode * 8191 + _percentageKeys.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(DynamicPaoInfoRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSet_deviceId(), other.isSet_deviceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_deviceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._deviceId, other._deviceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSet_durationKeys(), other.isSet_durationKeys());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_durationKeys()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._durationKeys, other._durationKeys);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSet_timestampKeys(), other.isSet_timestampKeys());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_timestampKeys()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._timestampKeys, other._timestampKeys);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSet_percentageKeys(), other.isSet_percentageKeys());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_percentageKeys()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._percentageKeys, other._percentageKeys);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("DynamicPaoInfoRequest(");
    boolean first = true;

    sb.append("_deviceId:");
    sb.append(this._deviceId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_durationKeys:");
    if (this._durationKeys == null) {
      sb.append("null");
    } else {
      sb.append(this._durationKeys);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_timestampKeys:");
    if (this._timestampKeys == null) {
      sb.append("null");
    } else {
      sb.append(this._timestampKeys);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_percentageKeys:");
    if (this._percentageKeys == null) {
      sb.append("null");
    } else {
      sb.append(this._percentageKeys);
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

    if (!isSet_durationKeys()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_durationKeys' is unset! Struct:" + toString());
    }

    if (!isSet_timestampKeys()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_timestampKeys' is unset! Struct:" + toString());
    }

    if (!isSet_percentageKeys()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_percentageKeys' is unset! Struct:" + toString());
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

  private static class DynamicPaoInfoRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DynamicPaoInfoRequestStandardScheme getScheme() {
      return new DynamicPaoInfoRequestStandardScheme();
    }
  }

  private static class DynamicPaoInfoRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<DynamicPaoInfoRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
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
          case 2: // _DURATION_KEYS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set0 = iprot.readSetBegin();
                struct._durationKeys = java.util.EnumSet.noneOf(DynamicPaoInfoDurationKeys.class);
                @org.apache.thrift.annotation.Nullable DynamicPaoInfoDurationKeys _elem1;
                for (int _i2 = 0; _i2 < _set0.size; ++_i2)
                {
                  _elem1 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys.findByValue(iprot.readI32());
                  if (_elem1 != null)
                  {
                    struct._durationKeys.add(_elem1);
                  }
                }
                iprot.readSetEnd();
              }
              struct.set_durationKeysIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _TIMESTAMP_KEYS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set3 = iprot.readSetBegin();
                struct._timestampKeys = java.util.EnumSet.noneOf(DynamicPaoInfoTimestampKeys.class);
                @org.apache.thrift.annotation.Nullable DynamicPaoInfoTimestampKeys _elem4;
                for (int _i5 = 0; _i5 < _set3.size; ++_i5)
                {
                  _elem4 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys.findByValue(iprot.readI32());
                  if (_elem4 != null)
                  {
                    struct._timestampKeys.add(_elem4);
                  }
                }
                iprot.readSetEnd();
              }
              struct.set_timestampKeysIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _PERCENTAGE_KEYS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set6 = iprot.readSetBegin();
                struct._percentageKeys = java.util.EnumSet.noneOf(DynamicPaoInfoPercentageKeys.class);
                @org.apache.thrift.annotation.Nullable DynamicPaoInfoPercentageKeys _elem7;
                for (int _i8 = 0; _i8 < _set6.size; ++_i8)
                {
                  _elem7 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoPercentageKeys.findByValue(iprot.readI32());
                  if (_elem7 != null)
                  {
                    struct._percentageKeys.add(_elem7);
                  }
                }
                iprot.readSetEnd();
              }
              struct.set_percentageKeysIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, DynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(_DEVICE_ID_FIELD_DESC);
      oprot.writeI32(struct._deviceId);
      oprot.writeFieldEnd();
      if (struct._durationKeys != null) {
        oprot.writeFieldBegin(_DURATION_KEYS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, struct._durationKeys.size()));
          for (DynamicPaoInfoDurationKeys _iter9 : struct._durationKeys)
          {
            oprot.writeI32(_iter9.getValue());
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct._timestampKeys != null) {
        oprot.writeFieldBegin(_TIMESTAMP_KEYS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, struct._timestampKeys.size()));
          for (DynamicPaoInfoTimestampKeys _iter10 : struct._timestampKeys)
          {
            oprot.writeI32(_iter10.getValue());
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct._percentageKeys != null) {
        oprot.writeFieldBegin(_PERCENTAGE_KEYS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, struct._percentageKeys.size()));
          for (DynamicPaoInfoPercentageKeys _iter11 : struct._percentageKeys)
          {
            oprot.writeI32(_iter11.getValue());
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DynamicPaoInfoRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DynamicPaoInfoRequestTupleScheme getScheme() {
      return new DynamicPaoInfoRequestTupleScheme();
    }
  }

  private static class DynamicPaoInfoRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<DynamicPaoInfoRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct._deviceId);
      {
        oprot.writeI32(struct._durationKeys.size());
        for (DynamicPaoInfoDurationKeys _iter12 : struct._durationKeys)
        {
          oprot.writeI32(_iter12.getValue());
        }
      }
      {
        oprot.writeI32(struct._timestampKeys.size());
        for (DynamicPaoInfoTimestampKeys _iter13 : struct._timestampKeys)
        {
          oprot.writeI32(_iter13.getValue());
        }
      }
      {
        oprot.writeI32(struct._percentageKeys.size());
        for (DynamicPaoInfoPercentageKeys _iter14 : struct._percentageKeys)
        {
          oprot.writeI32(_iter14.getValue());
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DynamicPaoInfoRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._deviceId = iprot.readI32();
      struct.set_deviceIdIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set15 = iprot.readSetBegin(org.apache.thrift.protocol.TType.I32);
        struct._durationKeys = java.util.EnumSet.noneOf(DynamicPaoInfoDurationKeys.class);
        @org.apache.thrift.annotation.Nullable DynamicPaoInfoDurationKeys _elem16;
        for (int _i17 = 0; _i17 < _set15.size; ++_i17)
        {
          _elem16 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys.findByValue(iprot.readI32());
          if (_elem16 != null)
          {
            struct._durationKeys.add(_elem16);
          }
        }
      }
      struct.set_durationKeysIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set18 = iprot.readSetBegin(org.apache.thrift.protocol.TType.I32);
        struct._timestampKeys = java.util.EnumSet.noneOf(DynamicPaoInfoTimestampKeys.class);
        @org.apache.thrift.annotation.Nullable DynamicPaoInfoTimestampKeys _elem19;
        for (int _i20 = 0; _i20 < _set18.size; ++_i20)
        {
          _elem19 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys.findByValue(iprot.readI32());
          if (_elem19 != null)
          {
            struct._timestampKeys.add(_elem19);
          }
        }
      }
      struct.set_timestampKeysIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set21 = iprot.readSetBegin(org.apache.thrift.protocol.TType.I32);
        struct._percentageKeys = java.util.EnumSet.noneOf(DynamicPaoInfoPercentageKeys.class);
        @org.apache.thrift.annotation.Nullable DynamicPaoInfoPercentageKeys _elem22;
        for (int _i23 = 0; _i23 < _set21.size; ++_i23)
        {
          _elem22 = com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoPercentageKeys.findByValue(iprot.readI32());
          if (_elem22 != null)
          {
            struct._percentageKeys.add(_elem22);
          }
        }
      }
      struct.set_percentageKeysIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

