/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Registration implements org.apache.thrift.TBase<Registration, Registration._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Registration");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _APP_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("_appName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField _APP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_appId", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _APP_IS_UNIQUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_appIsUnique", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _APP_KNOWN_PORT_FIELD_DESC = new org.apache.thrift.protocol.TField("_appKnownPort", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField _APP_EXPIRATION_DELAY_FIELD_DESC = new org.apache.thrift.protocol.TField("_appExpirationDelay", org.apache.thrift.protocol.TType.I32, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RegistrationStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RegistrationTupleSchemeFactory());
  }

  private com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private String _appName; // required
  private int _appId; // required
  private int _appIsUnique; // required
  private int _appKnownPort; // required
  private int _appExpirationDelay; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _APP_NAME((short)2, "_appName"),
    _APP_ID((short)3, "_appId"),
    _APP_IS_UNIQUE((short)4, "_appIsUnique"),
    _APP_KNOWN_PORT((short)5, "_appKnownPort"),
    _APP_EXPIRATION_DELAY((short)6, "_appExpirationDelay");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
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
        case 2: // _APP_NAME
          return _APP_NAME;
        case 3: // _APP_ID
          return _APP_ID;
        case 4: // _APP_IS_UNIQUE
          return _APP_IS_UNIQUE;
        case 5: // _APP_KNOWN_PORT
          return _APP_KNOWN_PORT;
        case 6: // _APP_EXPIRATION_DELAY
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
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int ___APPID_ISSET_ID = 0;
  private static final int ___APPISUNIQUE_ISSET_ID = 1;
  private static final int ___APPKNOWNPORT_ISSET_ID = 2;
  private static final int ___APPEXPIRATIONDELAY_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._APP_NAME, new org.apache.thrift.meta_data.FieldMetaData("_appName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._APP_ID, new org.apache.thrift.meta_data.FieldMetaData("_appId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._APP_IS_UNIQUE, new org.apache.thrift.meta_data.FieldMetaData("_appIsUnique", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._APP_KNOWN_PORT, new org.apache.thrift.meta_data.FieldMetaData("_appKnownPort", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._APP_EXPIRATION_DELAY, new org.apache.thrift.meta_data.FieldMetaData("_appExpirationDelay", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Registration.class, metaDataMap);
  }

  public Registration() {
  }

  public Registration(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    String _appName,
    int _appId,
    int _appIsUnique,
    int _appKnownPort,
    int _appExpirationDelay)
  {
    this();
    this._baseMessage = _baseMessage;
    this._appName = _appName;
    this._appId = _appId;
    set_appIdIsSet(true);
    this._appIsUnique = _appIsUnique;
    set_appIsUniqueIsSet(true);
    this._appKnownPort = _appKnownPort;
    set_appKnownPortIsSet(true);
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
    this._appKnownPort = other._appKnownPort;
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
    set_appKnownPortIsSet(false);
    this._appKnownPort = 0;
    set_appExpirationDelayIsSet(false);
    this._appExpirationDelay = 0;
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

  public String get_appName() {
    return this._appName;
  }

  public void set_appName(String _appName) {
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
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___APPID_ISSET_ID);
  }

  /** Returns true if field _appId is set (has been assigned a value) and false otherwise */
  public boolean isSet_appId() {
    return EncodingUtils.testBit(__isset_bitfield, ___APPID_ISSET_ID);
  }

  public void set_appIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___APPID_ISSET_ID, value);
  }

  public int get_appIsUnique() {
    return this._appIsUnique;
  }

  public void set_appIsUnique(int _appIsUnique) {
    this._appIsUnique = _appIsUnique;
    set_appIsUniqueIsSet(true);
  }

  public void unset_appIsUnique() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID);
  }

  /** Returns true if field _appIsUnique is set (has been assigned a value) and false otherwise */
  public boolean isSet_appIsUnique() {
    return EncodingUtils.testBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID);
  }

  public void set_appIsUniqueIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___APPISUNIQUE_ISSET_ID, value);
  }

  public int get_appKnownPort() {
    return this._appKnownPort;
  }

  public void set_appKnownPort(int _appKnownPort) {
    this._appKnownPort = _appKnownPort;
    set_appKnownPortIsSet(true);
  }

  public void unset_appKnownPort() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___APPKNOWNPORT_ISSET_ID);
  }

  /** Returns true if field _appKnownPort is set (has been assigned a value) and false otherwise */
  public boolean isSet_appKnownPort() {
    return EncodingUtils.testBit(__isset_bitfield, ___APPKNOWNPORT_ISSET_ID);
  }

  public void set_appKnownPortIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___APPKNOWNPORT_ISSET_ID, value);
  }

  public int get_appExpirationDelay() {
    return this._appExpirationDelay;
  }

  public void set_appExpirationDelay(int _appExpirationDelay) {
    this._appExpirationDelay = _appExpirationDelay;
    set_appExpirationDelayIsSet(true);
  }

  public void unset_appExpirationDelay() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID);
  }

  /** Returns true if field _appExpirationDelay is set (has been assigned a value) and false otherwise */
  public boolean isSet_appExpirationDelay() {
    return EncodingUtils.testBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID);
  }

  public void set_appExpirationDelayIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___APPEXPIRATIONDELAY_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
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
        set_appName((String)value);
      }
      break;

    case _APP_ID:
      if (value == null) {
        unset_appId();
      } else {
        set_appId((Integer)value);
      }
      break;

    case _APP_IS_UNIQUE:
      if (value == null) {
        unset_appIsUnique();
      } else {
        set_appIsUnique((Integer)value);
      }
      break;

    case _APP_KNOWN_PORT:
      if (value == null) {
        unset_appKnownPort();
      } else {
        set_appKnownPort((Integer)value);
      }
      break;

    case _APP_EXPIRATION_DELAY:
      if (value == null) {
        unset_appExpirationDelay();
      } else {
        set_appExpirationDelay((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _APP_NAME:
      return get_appName();

    case _APP_ID:
      return Integer.valueOf(get_appId());

    case _APP_IS_UNIQUE:
      return Integer.valueOf(get_appIsUnique());

    case _APP_KNOWN_PORT:
      return Integer.valueOf(get_appKnownPort());

    case _APP_EXPIRATION_DELAY:
      return Integer.valueOf(get_appExpirationDelay());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
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
    case _APP_KNOWN_PORT:
      return isSet_appKnownPort();
    case _APP_EXPIRATION_DELAY:
      return isSet_appExpirationDelay();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Registration)
      return this.equals((Registration)that);
    return false;
  }

  public boolean equals(Registration that) {
    if (that == null)
      return false;

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

    boolean this_present__appKnownPort = true;
    boolean that_present__appKnownPort = true;
    if (this_present__appKnownPort || that_present__appKnownPort) {
      if (!(this_present__appKnownPort && that_present__appKnownPort))
        return false;
      if (this._appKnownPort != that._appKnownPort)
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
    return 0;
  }

  public int compareTo(Registration other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Registration typedOther = (Registration)other;

    lastComparison = Boolean.valueOf(isSet_baseMessage()).compareTo(typedOther.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, typedOther._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_appName()).compareTo(typedOther.isSet_appName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appName, typedOther._appName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_appId()).compareTo(typedOther.isSet_appId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appId, typedOther._appId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_appIsUnique()).compareTo(typedOther.isSet_appIsUnique());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appIsUnique()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appIsUnique, typedOther._appIsUnique);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_appKnownPort()).compareTo(typedOther.isSet_appKnownPort());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appKnownPort()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appKnownPort, typedOther._appKnownPort);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_appExpirationDelay()).compareTo(typedOther.isSet_appExpirationDelay());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_appExpirationDelay()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._appExpirationDelay, typedOther._appExpirationDelay);
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
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Registration(");
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
    sb.append("_appKnownPort:");
    sb.append(this._appKnownPort);
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

    if (!isSet_appKnownPort()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_appKnownPort' is unset! Struct:" + toString());
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RegistrationStandardSchemeFactory implements SchemeFactory {
    public RegistrationStandardScheme getScheme() {
      return new RegistrationStandardScheme();
    }
  }

  private static class RegistrationStandardScheme extends StandardScheme<Registration> {

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
          case 5: // _APP_KNOWN_PORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._appKnownPort = iprot.readI32();
              struct.set_appKnownPortIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _APP_EXPIRATION_DELAY
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
      oprot.writeFieldBegin(_APP_KNOWN_PORT_FIELD_DESC);
      oprot.writeI32(struct._appKnownPort);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_APP_EXPIRATION_DELAY_FIELD_DESC);
      oprot.writeI32(struct._appExpirationDelay);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RegistrationTupleSchemeFactory implements SchemeFactory {
    public RegistrationTupleScheme getScheme() {
      return new RegistrationTupleScheme();
    }
  }

  private static class RegistrationTupleScheme extends TupleScheme<Registration> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Registration struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeString(struct._appName);
      oprot.writeI32(struct._appId);
      oprot.writeI32(struct._appIsUnique);
      oprot.writeI32(struct._appKnownPort);
      oprot.writeI32(struct._appExpirationDelay);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Registration struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._appName = iprot.readString();
      struct.set_appNameIsSet(true);
      struct._appId = iprot.readI32();
      struct.set_appIdIsSet(true);
      struct._appIsUnique = iprot.readI32();
      struct.set_appIsUniqueIsSet(true);
      struct._appKnownPort = iprot.readI32();
      struct.set_appKnownPortIsSet(true);
      struct._appExpirationDelay = iprot.readI32();
      struct.set_appExpirationDelayIsSet(true);
    }
  }

}

