/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class Registration implements org.apache.thrift.TBase<Registration, Registration._Fields>, java.io.Serializable, Cloneable, Comparable<Registration> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Registration");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _APP_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("_appName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField _APP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_appId", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _APP_IS_UNIQUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_appIsUnique", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _APP_EXPIRATION_DELAY_FIELD_DESC = new org.apache.thrift.protocol.TField("_appExpirationDelay", org.apache.thrift.protocol.TType.I32, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RegistrationStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RegistrationTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _appName; // required
  private int _appId; // required
  private int _appIsUnique; // required
  private int _appExpirationDelay; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _APP_NAME((short)2, "_appName"),
    _APP_ID((short)3, "_appId"),
    _APP_IS_UNIQUE((short)4, "_appIsUnique"),
    _APP_EXPIRATION_DELAY((short)5, "_appExpirationDelay");

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
        case 2: // _APP_NAME
          return _APP_NAME;
        case 3: // _APP_ID
          return _APP_ID;
        case 4: // _APP_IS_UNIQUE
          return _APP_IS_UNIQUE;
        case 5: // _APP_EXPIRATION_DELAY
          return _APP_EXPIRATION_DELAY;
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
  private static final int ___APPID_ISSET_ID = 0;
  private static final int ___APPISUNIQUE_ISSET_ID = 1;
  private static final int ___APPEXPIRATIONDELAY_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._APP_NAME, new org.apache.thrift.meta_data.FieldMetaData("_appName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._APP_ID, new org.apache.thrift.meta_data.FieldMetaData("_appId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._APP_IS_UNIQUE, new org.apache.thrift.meta_data.FieldMetaData("_appIsUnique", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._APP_EXPIRATION_DELAY, new org.apache.thrift.meta_data.FieldMetaData("_appExpirationDelay", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Registration.class, metaDataMap);
  }

  public Registration() {
  }

  public Registration(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    java.lang.String _appName,
    int _appId,
    int _appIsUnique,
    int _appExpirationDelay)
  {
    this();
    this._baseMessage = _baseMessage;
    this._appName = _appName;
    this._appId = _appId;
    set_appIdIsSet(true);
    this._appIsUnique = _appIsUnique;
    set_appIsUniqueIsSet(true);
    this._appExpirationDelay = _appExpirationDelay;
    set_appExpirationDelayIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Registration(Registration other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_appName()) {
      this._appName = other._appName;
    }
    this._appId = other._appId;
    this._appIsUnique = other._appIsUnique;
    this._appExpirationDelay = other._appExpirationDelay;
  }

  public Registration deepCopy() {
    return new Registration(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._appName = null;
    set_appIdIsSet(false);
    this._appId = 0;
    set_appIsUniqueIsSet(false);
    this._appIsUnique = 0;
    set_appExpirationDelayIsSet(false);
    this._appExpirationDelay = 0;
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
  public java.lang.String get_appName() {
    return this._appName;
  }

  public void set_appName(@org.apache.thrift.annotation.Nullable java.lang.String _appName) {
    this._appName = _appName;
  }

  public void unset_appName() {
    this._appName = null;
  }

  /** Returns true if field _appName is set (has been assigned a value) and false otherwise */
  public boolean isSet_appName() {
    return this._appName != null;
  }

  public void set_appNameIsSet(boolean value) {
    if (!value) {
      this._appName = null;
    }
  }

  public int get_appId() {
    return this._appId;
  }

  public void set_appId(int _appId) {
    this._appId = _appId;
    set_appIdIsSet(true);
  }

  public void unset_appId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___APPID_ISSET_ID);
  }

  /** Returns true if field _appId is set (has been assigned a value) and false otherwise */
  public boolean isSet_appId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___APPID_ISSET_ID);
  }

  public void set_appIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___APPID_ISSET_ID, value);
  }

  public int get_appIsUnique() {
    return this._appIsUnique;
  }

  public void set_appIsUnique(int _appIsUnique) {
    this._appIsUnique = _appIsUnique;
    set_appIsUniqueIsSet(true);
  }

  public void unset_appIsUnique() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID);
  }

  /** Returns true if field _appIsUnique is set (has been assigned a value) and false otherwise */
  public boolean isSet_appIsUnique() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID);
  }

  public void set_appIsUniqueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID, value);
  }

  public int get_appExpirationDelay() {
    return this._appExpirationDelay;
  }

  public void set_appExpirationDelay(int _appExpirationDelay) {
    this._appExpirationDelay = _appExpirationDelay;
    set_appExpirationDelayIsSet(true);
  }

  public void unset_appExpirationDelay() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID);
  }

  /** Returns true if field _appExpirationDelay is set (has been assigned a value) and false otherwise */
  public boolean isSet_appExpirationDelay() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID);
  }

  public void set_appExpirationDelayIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID, value);
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

    case _APP_NAME:
      if (value == null) {
        unset_appName();
      } else {
        set_appName((java.lang.String)value);
      }
      break;

    case _APP_ID:
      if (value == null) {
        unset_appId();
      } else {
        set_appId((java.lang.Integer)value);
      }
      break;

    case _APP_IS_UNIQUE:
      if (value == null) {
        unset_appIsUnique();
      } else {
        set_appIsUnique((java.lang.Integer)value);
      }
      break;

    case _APP_EXPIRATION_DELAY:
      if (value == null) {
        unset_appExpirationDelay();
      } else {
        set_appExpirationDelay((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _APP_NAME:
      return get_appName();

    case _APP_ID:
      return get_appId();

    case _APP_IS_UNIQUE:
      return get_appIsUnique();

    case _APP_EXPIRATION_DELAY:
      return get_appExpirationDelay();

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
    case _APP_NAME:
      return isSet_appName();
    case _APP_ID:
      return isSet_appId();
    case _APP_IS_UNIQUE:
      return isSet_appIsUnique();
    case _APP_EXPIRATION_DELAY:
      return isSet_appExpirationDelay();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Registration)
      return this.equals((Registration)that);
    return false;
  }

  public boolean equals(Registration that) {
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

    boolean this_present__appName = true && this.isSet_appName();
    boolean that_present__appName = true && that.isSet_appName();
    if (this_present__appName || that_present__appName) {
      if (!(this_present__appName && that_present__appName))
        return false;
      if (!this._appName.equals(that._appName))
        return false;
    }

    boolean this_present__appId = true;
    boolean that_present__appId = true;
    if (this_present__appId || that_present__appId) {
      if (!(this_present__appId && that_present__appId))
        return false;
      if (this._appId != that._appId)
        return false;
    }

    boolean this_present__appIsUnique = true;
    boolean that_present__appIsUnique = true;
    if (this_present__appIsUnique || that_present__appIsUnique) {
      if (!(this_present__appIsUnique && that_present__appIsUnique))
        return false;
      if (this._appIsUnique != that._appIsUnique)
        return false;
    }

    boolean this_present__appExpirationDelay = true;
    boolean that_present__appExpirationDelay = true;
    if (this_present__appExpirationDelay || that_present__appExpirationDelay) {
      if (!(this_present__appExpirationDelay && that_present__appExpirationDelay))
        return false;
      if (this._appExpirationDelay != that._appExpirationDelay)
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

    hashCode = hashCode * 8191 + ((isSet_appName()) ? 131071 : 524287);
    if (isSet_appName())
      hashCode = hashCode * 8191 + _appName.hashCode();

    hashCode = hashCode * 8191 + _appId;

    hashCode = hashCode * 8191 + _appIsUnique;

    hashCode = hashCode * 8191 + _appExpirationDelay;

    return hashCode;
  }

  @Override
  public int compareTo(Registration other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_appName()).compareTo(other.isSet_appName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appName, other._appName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_appId()).compareTo(other.isSet_appId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appId, other._appId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_appIsUnique()).compareTo(other.isSet_appIsUnique());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appIsUnique()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appIsUnique, other._appIsUnique);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_appExpirationDelay()).compareTo(other.isSet_appExpirationDelay());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appExpirationDelay()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appExpirationDelay, other._appExpirationDelay);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Registration(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_appName:");
    if (this._appName == null) {
      sb.append("null");
    } else {
      sb.append(this._appName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_appId:");
    sb.append(this._appId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_appIsUnique:");
    sb.append(this._appIsUnique);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_appExpirationDelay:");
    sb.append(this._appExpirationDelay);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_appName()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_appName' is unset! Struct:" + toString());
    }

    if (!isSet_appId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_appId' is unset! Struct:" + toString());
    }

    if (!isSet_appIsUnique()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_appIsUnique' is unset! Struct:" + toString());
    }

    if (!isSet_appExpirationDelay()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_appExpirationDelay' is unset! Struct:" + toString());
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

  private static class RegistrationStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RegistrationStandardScheme getScheme() {
      return new RegistrationStandardScheme();
    }
  }

  private static class RegistrationStandardScheme extends org.apache.thrift.scheme.StandardScheme<Registration> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Registration struct) throws org.apache.thrift.TException {
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
          case 2: // _APP_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._appName = iprot.readString();
              struct.set_appNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _APP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._appId = iprot.readI32();
              struct.set_appIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _APP_IS_UNIQUE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._appIsUnique = iprot.readI32();
              struct.set_appIsUniqueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _APP_EXPIRATION_DELAY
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._appExpirationDelay = iprot.readI32();
              struct.set_appExpirationDelayIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Registration struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._appName != null) {
        oprot.writeFieldBegin(_APP_NAME_FIELD_DESC);
        oprot.writeString(struct._appName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_APP_ID_FIELD_DESC);
      oprot.writeI32(struct._appId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_APP_IS_UNIQUE_FIELD_DESC);
      oprot.writeI32(struct._appIsUnique);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_APP_EXPIRATION_DELAY_FIELD_DESC);
      oprot.writeI32(struct._appExpirationDelay);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RegistrationTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RegistrationTupleScheme getScheme() {
      return new RegistrationTupleScheme();
    }
  }

  private static class RegistrationTupleScheme extends org.apache.thrift.scheme.TupleScheme<Registration> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Registration struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeString(struct._appName);
      oprot.writeI32(struct._appId);
      oprot.writeI32(struct._appIsUnique);
      oprot.writeI32(struct._appExpirationDelay);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Registration struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._appName = iprot.readString();
      struct.set_appNameIsSet(true);
      struct._appId = iprot.readI32();
      struct.set_appIdIsSet(true);
      struct._appIsUnique = iprot.readI32();
      struct.set_appIsUniqueIsSet(true);
      struct._appExpirationDelay = iprot.readI32();
      struct.set_appExpirationDelayIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

