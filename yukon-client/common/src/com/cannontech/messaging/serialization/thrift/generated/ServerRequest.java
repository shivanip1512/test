/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class ServerRequest implements org.apache.thrift.TBase<ServerRequest, ServerRequest._Fields>, java.io.Serializable, Cloneable, Comparable<ServerRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ServerRequest");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_id", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _PAYLOAD_FIELD_DESC = new org.apache.thrift.protocol.TField("_payload", org.apache.thrift.protocol.TType.STRUCT, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ServerRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ServerRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _id; // required
  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.GenericMessage _payload; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _ID((short)2, "_id"),
    _PAYLOAD((short)3, "_payload");

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
        case 3: // _PAYLOAD
          return _PAYLOAD;
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
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._ID, new org.apache.thrift.meta_data.FieldMetaData("_id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._PAYLOAD, new org.apache.thrift.meta_data.FieldMetaData("_payload", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.GenericMessage.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ServerRequest.class, metaDataMap);
  }

  public ServerRequest() {
  }

  public ServerRequest(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _id,
    com.cannontech.messaging.serialization.thrift.generated.GenericMessage _payload)
  {
    this();
    this._baseMessage = _baseMessage;
    this._id = _id;
    set_idIsSet(true);
    this._payload = _payload;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ServerRequest(ServerRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._id = other._id;
    if (other.isSet_payload()) {
      this._payload = new com.cannontech.messaging.serialization.thrift.generated.GenericMessage(other._payload);
    }
  }

  public ServerRequest deepCopy() {
    return new ServerRequest(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_idIsSet(false);
    this._id = 0;
    this._payload = null;
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

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.GenericMessage get_payload() {
    return this._payload;
  }

  public void set_payload(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.GenericMessage _payload) {
    this._payload = _payload;
  }

  public void unset_payload() {
    this._payload = null;
  }

  /** Returns true if field _payload is set (has been assigned a value) and false otherwise */
  public boolean isSet_payload() {
    return this._payload != null;
  }

  public void set_payloadIsSet(boolean value) {
    if (!value) {
      this._payload = null;
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

    case _ID:
      if (value == null) {
        unset_id();
      } else {
        set_id((java.lang.Integer)value);
      }
      break;

    case _PAYLOAD:
      if (value == null) {
        unset_payload();
      } else {
        set_payload((com.cannontech.messaging.serialization.thrift.generated.GenericMessage)value);
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

    case _PAYLOAD:
      return get_payload();

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
    case _PAYLOAD:
      return isSet_payload();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof ServerRequest)
      return this.equals((ServerRequest)that);
    return false;
  }

  public boolean equals(ServerRequest that) {
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

    boolean this_present__payload = true && this.isSet_payload();
    boolean that_present__payload = true && that.isSet_payload();
    if (this_present__payload || that_present__payload) {
      if (!(this_present__payload && that_present__payload))
        return false;
      if (!this._payload.equals(that._payload))
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

    hashCode = hashCode * 8191 + ((isSet_payload()) ? 131071 : 524287);
    if (isSet_payload())
      hashCode = hashCode * 8191 + _payload.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(ServerRequest other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_payload()).compareTo(other.isSet_payload());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_payload()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._payload, other._payload);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("ServerRequest(");
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
    sb.append("_payload:");
    if (this._payload == null) {
      sb.append("null");
    } else {
      sb.append(this._payload);
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

    if (!isSet_id()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_id' is unset! Struct:" + toString());
    }

    if (!isSet_payload()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_payload' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_baseMessage != null) {
      _baseMessage.validate();
    }
    if (_payload != null) {
      _payload.validate();
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

  private static class ServerRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ServerRequestStandardScheme getScheme() {
      return new ServerRequestStandardScheme();
    }
  }

  private static class ServerRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<ServerRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ServerRequest struct) throws org.apache.thrift.TException {
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
          case 3: // _PAYLOAD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._payload = new com.cannontech.messaging.serialization.thrift.generated.GenericMessage();
              struct._payload.read(iprot);
              struct.set_payloadIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ServerRequest struct) throws org.apache.thrift.TException {
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
      if (struct._payload != null) {
        oprot.writeFieldBegin(_PAYLOAD_FIELD_DESC);
        struct._payload.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ServerRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ServerRequestTupleScheme getScheme() {
      return new ServerRequestTupleScheme();
    }
  }

  private static class ServerRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<ServerRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ServerRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._id);
      struct._payload.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ServerRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._id = iprot.readI32();
      struct.set_idIsSet(true);
      struct._payload = new com.cannontech.messaging.serialization.thrift.generated.GenericMessage();
      struct._payload.read(iprot);
      struct.set_payloadIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

